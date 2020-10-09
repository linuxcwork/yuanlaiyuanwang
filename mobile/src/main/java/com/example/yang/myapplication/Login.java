package com.example.yang.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yang.Activity.CountryActivity;
import com.example.yang.Activity.VerifyCodeView;
import com.example.yang.network.CheckNetwork;
import com.example.yang.network.OkHttpManager;
import com.example.yang.network.ServerResponse;
import com.example.yang.util.SharedPreferencedUtils;
import com.example.yang.util.CheckPermission;
import com.example.yang.util.FileOperationUtil;
import com.example.yang.util.MessageListener;
import com.example.yang.util.XmppConnection;

import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaCollector;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.yang.util.FileOperationUtil.SECONDUSERINFODIRPATH;


public class Login extends Activity implements View.OnClickListener {

    private LinearLayout phone_number_lin;
    private EditText admin;
    private Button phone_number_ensure;
    private LinearLayout phone_number_area;
    private TextView conutry_number;

    private LinearLayout ver_lin;
    private VerifyCodeView verifyCodeView;
    private TextView phone_number_tips;
    private Button ver_action;

    private EditText passwd;
    private TextView byshortmassage;
    private Button login;
    private TextView forgetpasswd;
    private TextView regist;

    private final int RESPONSE = 1;
    private static final int MATCH_VER_RSP = 2;
    private static final int IS_SEND_VER_RSP = 3;
    private MessageListener listener;
    private XmppConnection xmppConnection;
    private File path;

    //计时器
    private Boolean runningThree = false;
    private static String phone_number;

    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();

    //目录名
    private ServerResponse serverResponse = new ServerResponse();
    private OkHttpManager http = new OkHttpManager();

    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
        public void handleMessage(Message msg) {
            String response = (String) msg.obj;
            switch (msg.what) {
                case RESPONSE:
                    LoginResponse(response);
                    break;
                case MATCH_VER_RSP:
                     if("succeed".equals(response)) {
                         Intent mainactivity = new Intent(Login.this, MainActivity.class);
                         startActivity(mainactivity);
                     }else {
                         Toast.makeText(getApplicationContext(),"验证码错误",Toast.LENGTH_SHORT).show();
                     }
                    break;
                case IS_SEND_VER_RSP:
                    if("sended".equals(response)){
                        phone_number_lin.setVisibility(View.GONE);
                        ver_lin.setVisibility(View.VISIBLE);
                        phone_number_tips.setText("验证码已发送至 " + admin.getText().toString());
                    }else {
                        Toast.makeText(getApplicationContext(),"请检查手机号是否正确",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        /*
         * 创建一级目录*/
        FileOperationUtil.CreateDir(FileOperationUtil.MAINDIRPATH);
        FileOperationUtil.CreateDir(FileOperationUtil.SECONDCACHEDIRPATH);
        FileOperationUtil.CreateDir(SECONDUSERINFODIRPATH);

        listener = MessageListener.getMessageListener(getApplicationContext());
        phone_number_lin = findViewById(R.id.loging_phone_number_linearlayout);
        phone_number_area = findViewById(R.id.login_phone_number_area);
        conutry_number = findViewById(R.id.login_phone_conutry_number);
        admin = (EditText) findViewById(R.id.login_administrator);
        phone_number_ensure = findViewById(R.id.login_main_login);

        ver_lin = findViewById(R.id.login_ver_linearlayout);
        ver_lin.setVisibility(View.GONE);
        verifyCodeView = findViewById(R.id.loging_verifycodeview_edit);
        verifyCodeView.setInputCompleteListener(new VerifyCode());
        phone_number_tips = findViewById(R.id.loging_ver_send_phone_tips);
        ver_action = findViewById(R.id.login_ver_state_action);

        passwd = (EditText) findViewById(R.id.login_passwd);
        byshortmassage = (TextView) findViewById(R.id.login_bymessage);
        forgetpasswd = (TextView) findViewById(R.id.login_forget_passwd_textview);
        regist = (TextView) findViewById(R.id.login_register_textview);

        phone_number_area.setOnClickListener(this);
        phone_number_ensure.setOnClickListener(this);
        ver_action.setOnClickListener(this);
        phone_number_tips.setClickable(false);

        byshortmassage.setOnClickListener(this);
        //login.setOnClickListener(this);
        forgetpasswd.setOnClickListener(this);
        regist.setOnClickListener(this);

        CheckPermission.isStorage(this);
        CheckPermission.isGPSpermission(this);
        if (CheckPermission.isGPSOpen(this) == true) {
            CheckPermission.openGPS(this);
        }

        ProviderManager.addIQProvider("query", "hoo.iq.userinfo", new Login.PhoneProvider());

        xmppConnection = XmppConnection.getInstance();
        FileOperationUtil.CreateDir(SECONDUSERINFODIRPATH + File.separator + "image");
        path = FileOperationUtil.CreateFile(SECONDUSERINFODIRPATH + File.separator + "image" + File.separator + "avatar.jpg");
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_phone_number_area:
                Intent area_intent = new Intent(Login.this, CountryActivity.class);
                startActivityForResult(area_intent, 12);
                break;
            case R.id.login_bymessage:
                Intent phonelogin = new Intent(Login.this, PhoneLogin.class);
                startActivity(phonelogin);
                break;
            case R.id.login_main_login:
                //向服务器发送手机号
                phone_number = admin.getText().toString();
                getMobiile(phone_number);


                if (CheckNetwork.isMobileConnected(this) || CheckNetwork.isWifiConnected(this) || CheckNetwork.isNetworkConnected(this)) {
                    /*boolean islogin = xmppConnection.login(this,admin.getText().toString(), passwd.getText().toString());
                    if (islogin) {
                        xmppConnection.recieveMessage(listener);
                        xmppConnection.receiveFiles(listener);
                        xmppConnection.addRosterListener();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    VCard vCard = xmppConnection.getOwnInfo();
                                    Map<String, Object> account = new HashMap<String, Object>();
                                    account.put(sqlite_linkmanmss.KEY_ACTNB, xmppConnection.getCurrentUser().toString());
                                    byte [] avatar = vCard.getAvatar();
                                    if(avatar != null && avatar.length >0) {
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                                        account.put(sqlite_linkmanmss.KEY_ROWID, FileOperationUtil.saveBitmapFile(bitmap, path.getPath()).getPath());
                                    }
                                    account.put(sqlite_linkmanmss.KEY_NAME, vCard.getNickName());
                                    sharedPreferencedUtils.UpdateFile(Login.this, account);
                                } catch (XMPPException.XMPPErrorException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NotConnectedException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (SmackException.NoResponseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                        Bluetoothutil bluetoothutil = new Bluetoothutil(getApplicationContext());
                        bluetoothutil.startBluetoothBroadcast();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(this,"请检查账号和密码是否正确",Toast.LENGTH_LONG).show();
                    }*/
                } else {
                    Toast.makeText(this, "请检查网络连接", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.login_forget_passwd_textview:
                Intent forgetpasswd = new Intent(Login.this, MainActivity.class);
                startActivity(forgetpasswd);
                break;
            case R.id.login_register_textview:
                Intent register = new Intent(Login.this, UserRegister.class);
                startActivity(register);
                finish();
                break;
            case R.id.login_ver_state_action:
                getMobiile(phone_number);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch (requestCode) {
            case 12:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String countryNumber = bundle.getString("countryNumber");

                    conutry_number.setText(countryNumber);
                }
                break;

            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void LoginResponse(String res) {
        Map<String, Object> response = http.getJsonObject(res);
        if (response.get("loginstate") == null) {
            print("用户名或密码错误，请重试");
            return;
        }
        String loginstate = response.get("loginstate").toString();
        if (loginstate.equals(serverResponse.LOGINSUCCEED)) {
            SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();
            sharedPreferencedUtils.UpdateFile(this, response);
            Intent intent = new Intent(Login.this, MainActivity.class);
            intent.putExtra("num", 1);
            startActivity(intent);
        } else if (loginstate.equals(serverResponse.LOGINFAIL)) {
            String statet = String.valueOf(response.get("state"));
            String lastlogintime = String.valueOf(response.get("logintime"));
            String devicename = String.valueOf(response.get("devicename"));
            if (statet != null && statet.equals("1") &&
                    lastlogintime != null &&
                    devicename != null) {
                print(lastlogintime + " 已登陆,设备名" + devicename);
            } else {
                print("用户名或密码错误，请重试");
            }
        }
    }

    private void requestVerifyCode(String mobile) throws InterruptedException {
        /*Map<String,Object> map = new HashMap<String, Object>();
        map.put("event", mess.EVENT_GET_VERIFICATION);
        map.put(mess.getData(),mobile);*/

        PhoneNumLogin phoneNumLogin = new PhoneNumLogin();
        phoneNumLogin.setUsername("111");
        phoneNumLogin.setElement_name("phonenumber");
        phoneNumLogin.setPhonenumber(admin.getText().toString());
        phoneNumLogin.setType(IQ.Type.get);
        XMPPTCPConnection mConnection = xmppConnection.getXmppTcpConnection();
        StanzaCollector collector = mConnection.createStanzaCollector(new PacketIDFilter(phoneNumLogin.getPacketID()));
        //AndFilter filter = new AndFilter(new PacketIDFilter(PhoneNumLogin.getPacketID()), new PacketTypeFilter(IQ.class));
        try {
            mConnection.sendStanza(phoneNumLogin);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //PhoneNumLogin response = collector.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout());

        if (collector.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout()) == null) {
            SmackException.NoResponseException.newWith(mConnection, collector);
        }
        collector.cancel();// 停止请求results（是否成功的结果）

        if (runningThree) {
        } else {
            downTimer.start();
        }
        //http.postKeyValuePaires(user.host_url, map, null);

    }

    //获取验证码信息，判断是否有手机号码
    private void getMobiile(String mobile) {
        if ("".equals(mobile)) {
            Log.e("tag", "mobile=" + mobile);
            new AlertDialog.Builder(this).setTitle("提示").setMessage("手机号码不能为空").setCancelable(true).show();
        } else if (isMobileNO(mobile) == false) {
            new AlertDialog.Builder(this).setTitle("提示").setMessage("请输入正确的手机号码").setCancelable(true).show();
        } else {
            Log.e("tag", "输入了正确的手机号");
            try {
                requestVerifyCode(mobile);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isMobileNO(String tel) {
        Pattern p = Pattern.compile("^(13[0-9]|15([0-3]|[5-9])|14[5,7,9]|17[1,3,5,6,7,8]|18[0-9])\\d{8}$");
        Matcher m = p.matcher(tel);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    class VerifyCode implements VerifyCodeView.InputCompleteListener {

        @Override
        public void inputComplete() {
            String ver = verifyCodeView.getEditContent();
            //发送验证码到服务器

            PhoneNumLogin phoneNumLogin = new PhoneNumLogin();
            phoneNumLogin.setUsername("111");
            phoneNumLogin.setElement_name("ver");

            phoneNumLogin.setPhonenumber(admin.getText().toString());
            phoneNumLogin.setphoneVer(ver);

            phoneNumLogin.setType(IQ.Type.get);
            XMPPTCPConnection mConnection = xmppConnection.getXmppTcpConnection();
            StanzaCollector collector = mConnection.createStanzaCollector(new PacketIDFilter(phoneNumLogin.getPacketID()));
            //AndFilter filter = new AndFilter(new PacketIDFilter(PhoneNumLogin.getPacketID()), new PacketTypeFilter(IQ.class));
            try {
                mConnection.sendStanza(phoneNumLogin);
            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //PhoneNumLogin response = collector.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout());
            try {
                if (collector.nextResult(SmackConfiguration.getDefaultPacketReplyTimeout()) == null) {
                    SmackException.NoResponseException.newWith(mConnection, collector);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            collector.cancel();// 停止请求results（是否成功的结果）

        }

        @Override
        public void invalidContent() {

        }
    }

    private CountDownTimer downTimer = new CountDownTimer(60 * 1000, 1000) {
        @Override
        public void onTick(long l) {
            runningThree = true;
            ver_action.setText("重新发送" + (l / 1000));
        }

        @Override
        public void onFinish() {
            runningThree = false;
            ver_action.setText("重新发送");
        }
    };

    /***************************************************************
     * openfire 手机登录处理
     * *************************************************************/
    /*自定义一个iq，用于接收和发送自定义
    iq</pre><pre code_snipp
    et_id="134255" snippet_file_name="blog_20131230_5_2676678"
    name="code" class="java" style="color: rgb(51, 51, 51);
    font-size: 13px; line-height: 19px;">*/
    private static class PhoneNumLogin extends IQ {
        private String username;
        private String phonenumber;
        private String ver;
        private String element_name;
        /**
         * Namespace of the packet extension.
         */
        public static final String NAMESPACE = "hoo.iq.userinfo";

        public PhoneNumLogin() {
            super("query");
        }

        public PhoneNumLogin(IQ iq) {
            super(iq);
        }

        public String getElementName() {
            return "query";
        }

        public String getNamespace() {
            return "hoo.iq.userinfo";
        }

        public String getBody() {
            StringBuilder sb = new StringBuilder();
            if (null != username) {
                sb.append("<username>").append(username).append("</username>");
            }
            return sb.toString();
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setPhonenumber(String number){
            this.phonenumber = number;
        }

        public void setphoneVer(String ver){
            this.ver = ver;
        }

        public void setElement_name(String element_name){
            this.element_name = element_name;
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
            xml.rightAngleBracket();
            xml.append("<query xmlns=\"").append(NAMESPACE).append("\">");
            xml.append("<prompt>").append(username).append("</prompt>");
            xml.append("</query>");
            xml.append( "<" + element_name + " xmlns=\"" + NAMESPACE + "\">" );
            xml.element( "phonenumber",  phonenumber );
            xml.element( "ver", ver );
            xml.append( "</" + element_name + ">" );
            return xml;
        }
    }

    public class PhoneProvider extends IQProvider<PhoneNumLogin> {
        private static final String PREFERRED_ENCODING = "UTF-8";

        @Override
        public PhoneNumLogin parse(XmlPullParser parser, int initialDepth) throws Exception {
            final StringBuilder sb = new StringBuilder();
            int event = parser.getEventType();
            while (true) {
                switch (event) {
                    case XmlPullParser.TEXT:
                        sb.append(StringUtils.escapeForXml(parser.getText()));
                        break;
                    case XmlPullParser.START_TAG:
                        sb.append('<').append(parser.getName()).append('>');
                        Message msg = new Message();
                        //验证码获取请求---结果
                        if (parser.getName().equals("reqver")) {
                            String reqverrsp = parser.nextText();
                            msg.what = IS_SEND_VER_RSP;
                            if (reqverrsp != null && reqverrsp.equals("verrspsend")) {
                                msg.obj = "sended";
                            } else {
                                msg.obj = "sendfail";
                            }
                        } else if (parser.getName().equals("matchver")) {
                            //验证码匹配结果
                            String matchrsp = parser.nextText();
                            msg.what = MATCH_VER_RSP;
                            if (matchrsp != null && matchrsp.equals("matched")) {
                               msg.obj = "succeed";
                            } else {
                                msg.obj = "failed";
                            }
                        }
                        mhandler.sendMessage(msg);
                        break;
                    case XmlPullParser.END_TAG:
                        sb.append("</").append(parser.getName()).append('>');
                        break;
                    default:
                }
                if (event == XmlPullParser.END_TAG && "query".equals(parser.getName())) break;
                event = parser.next();

            }
            String xmlText = sb.toString();
            return new PhoneNumLogin();
        }
    }

    /***************************************************************/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void print(String massage) {
        Toast toast = Toast.makeText(this, massage, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 80);
        toast.show();
    }
}
