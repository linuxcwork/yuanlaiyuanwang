package com.example.yang.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;

import com.example.yang.myapplication.MainActivity;
import com.example.yang.myapplication.sqlite_linkmanmss;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_GIF;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_IMAGE;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_STRING;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_VIDEO;
import static com.example.yang.myapplication.sqlite_linkmanmss.KEY_MESSAGE_TYPE_VOICE;

public class MessageListener implements IncomingChatMessageListener, FileTransferListener {
    public static MessageListener messageListener;
    private XmppConnection xmppConnection;
    private static Context mContext;
    public static String BROADCASTRECVUPDATE = "com.example.yang.updaterecv";
    public static String BROADCASTRECVUPDATEFILE = "com.example.yang.updaterecv.file";
    private static String MESSAGE_TYPE_CHAT = "chat";
    private static String MESSAGE_TYPE_FILE = "file";
    private File recvimagepath = FileOperationUtil.CreateDir(FileOperationUtil.SECONDMESSAFEDIRPATH+"recvimage");

    public static MessageListener getMessageListener(Context context){
        mContext = context;
        if(messageListener == null){
            messageListener = new MessageListener();
        }
        return messageListener;
    }

    @Override
    public void newIncomingMessage(EntityBareJid from, Message message, Chat chat) {

        if(xmppConnection == null) {
            xmppConnection = XmppConnection.getInstance();
        }
        String body = message.getBody();

        VCard vCard = null;
        try {
            vCard = xmppConnection.getFriendunisInfo(from.toString());
            if(vCard == null){
                return;
            }
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XmppStringprepException e) {
            e.printStackTrace();
        }

        MainActivity.sql.open();
        Cursor cursor = MainActivity.sql.getContact(MainActivity.friendinfotable, sqlite_linkmanmss.KEY_ACTNB, from.toString());
        if (cursor != null && cursor.getCount()>0 && cursor.moveToFirst()) {
            int indexisnew = cursor.getColumnIndex(sqlite_linkmanmss.KEY_ISNEWMESSAGE);
            //int count = Integer.parseInt(cursor.getString(indexisnew)) + 1;
            Map<String, String> update = new HashMap<String, String>();
            update.put(sqlite_linkmanmss.KEY_ROWID, String.valueOf(vCard.getAvatar()));
            update.put(sqlite_linkmanmss.KEY_NAME, vCard.getNickName());
            update.put(sqlite_linkmanmss.KEY_CONTENT, body);
            update.put(sqlite_linkmanmss.KEY_TIME, String.valueOf(GetCurrentTime()));
            //update.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, String.valueOf(count));
            MainActivity.sql.updateContact(MainActivity.friendinfotable, sqlite_linkmanmss.KEY_ACTNB, from.toString(), update);
        } else {
            Map<String, Object> save = new HashMap<String, Object>();
            save.put(sqlite_linkmanmss.KEY_ROWID, vCard.getAvatar());
            save.put(sqlite_linkmanmss.KEY_NAME, vCard.getNickName());
            save.put(sqlite_linkmanmss.KEY_CONTENT, body);
            save.put(sqlite_linkmanmss.KEY_TIME, GetCurrentTime());
            save.put(sqlite_linkmanmss.KEY_ACTNB, from.toString());
            //save.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, String.valueOf(1));
            MainActivity.sql.insertContact(MainActivity.friendinfotable, save);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put(sqlite_linkmanmss.KEY_ACTNB, from.toString());
        map.put(sqlite_linkmanmss.KEY_DIRECTION, "recv");
        map.put(sqlite_linkmanmss.KEY_CONTENT, body);
        map.put(sqlite_linkmanmss.KEY_TIME, GetCurrentTime());
        map.put(sqlite_linkmanmss.EKY_MESSAGETYPE, sqlite_linkmanmss.KEY_MESSAGE_TYPE_STRING);
        MainActivity.sql.insertContact(sqlite_linkmanmss.DATABASE_TABLE, map);
        MainActivity.sql.close();

        dispatchMessage(from.toString(),body,message.getSubject("type"),GetCurrentTime());
    }

    @Override
    public void fileTransferRequest(FileTransferRequest request) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String type = request.getMimeType();
                IncomingFileTransfer infiletransfer = request.accept();
                File mfile = new File(recvimagepath.getAbsolutePath() + File.separator + request.getFileName());

                Jid from = request.getRequestor();
                try {
                    infiletransfer.receiveFile(mfile);
                    System.out.println("接收成功！");
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SmackException e) {
                    e.printStackTrace();
                }

                while (!infiletransfer.isDone()) {
                    if (infiletransfer.getStatus().equals(FileTransfer.Status.error)) {
                        System.out.println("status=" + infiletransfer.getStatus());
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (infiletransfer.getStatus().equals(FileTransfer.Status.complete)) {
                    Map<String, Object> file = new HashMap<String, Object>();
                    file.put(sqlite_linkmanmss.KEY_ACTNB, infiletransfer.getPeer().asEntityBareJidIfPossible().toString());
                    file.put(sqlite_linkmanmss.KEY_DIRECTION, "recv");
                    file.put(sqlite_linkmanmss.KEY_CONTENT, mfile.getPath());
                    file.put(sqlite_linkmanmss.KEY_TIME, GetCurrentTime());
                    file.put(sqlite_linkmanmss.EKY_MESSAGETYPE, type);
                    file.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, "new");

                    if (xmppConnection == null) {
                        xmppConnection = XmppConnection.getInstance();
                    }
                    VCard vCard = null;
                    try {
                        vCard = xmppConnection.getFriendunisInfo(from.toString());
                    } catch (XMPPException.XMPPErrorException e) {
                        e.printStackTrace();
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (SmackException.NoResponseException e) {
                        e.printStackTrace();
                    } catch (XmppStringprepException e) {
                        e.printStackTrace();
                    }
                    MainActivity.sql.open();
                    MainActivity.sql.insertContact(sqlite_linkmanmss.DATABASE_TABLE, file);
                    Cursor cursor = MainActivity.sql.getContact(MainActivity.friendinfotable, sqlite_linkmanmss.KEY_ACTNB, infiletransfer.getPeer().asEntityBareJidIfPossible().toString());
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        int indexisnew = cursor.getColumnIndex(sqlite_linkmanmss.KEY_ISNEWMESSAGE);
                        //int count = Integer.parseInt(cursor.getString(indexisnew)) + 1;
                        Map<String, String> update = new HashMap<String, String>();
                        //update.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, String.valueOf(count));
                        update.put(sqlite_linkmanmss.KEY_CONTENT, vCard.getNickName()+"[图片]");
                        update.put(sqlite_linkmanmss.KEY_NAME, vCard.getNickName());
                        update.put(sqlite_linkmanmss.KEY_TIME, GetCurrentTime().toString());
                        update.put(sqlite_linkmanmss.EKY_MESSAGETYPE, type);
                        MainActivity.sql.updateContact(MainActivity.friendinfotable, sqlite_linkmanmss.KEY_ACTNB, infiletransfer.getPeer().asEntityBareJidIfPossible().toString(), update);
                    } else {
                        Map<String, Object> save = new HashMap<String, Object>();
                        save.put(sqlite_linkmanmss.KEY_ACTNB, from.toString());
                        save.put(sqlite_linkmanmss.KEY_ROWID, vCard.getAvatar());
                        save.put(sqlite_linkmanmss.KEY_NAME, vCard.getNickName());
                        //save.put(sqlite_linkmanmss.KEY_ISNEWMESSAGE, 1);
                        save.put(sqlite_linkmanmss.KEY_CONTENT, vCard.getNickName()+"[图片]");
                        save.put(sqlite_linkmanmss.KEY_TIME, GetCurrentTime());
                        save.put(sqlite_linkmanmss.EKY_MESSAGETYPE, type);
                        MainActivity.sql.insertContact(MainActivity.friendinfotable, save);
                    }
                    MainActivity.sql.close();
                }

                dispatchMessage(from.toString(),request.getFileName(),MESSAGE_TYPE_FILE,GetCurrentTime());
            }
        }).start();

    }

    private void dispatchMessage(String from,String msg,String type,String time){
        Intent recvintent = new Intent();
        recvintent.setAction(BROADCASTRECVUPDATE);
        recvintent.putExtra("account",from);
        recvintent.putExtra("path",msg);
        recvintent.putExtra("type",type);
        recvintent.putExtra("time",time);
        //创建一个本地广播管理器
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mContext);
        //发送本地广播
        localBroadcastManager.sendBroadcast(recvintent);
    }

    /******************************************************************
     *获取当前时间
     ******************************************************************/
    private String GetCurrentTime() {
        Date mdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        StringBuilder mBuilder = new StringBuilder();

        mBuilder.append(sdf.format(mdate));

        return mBuilder.toString();
    }
}
