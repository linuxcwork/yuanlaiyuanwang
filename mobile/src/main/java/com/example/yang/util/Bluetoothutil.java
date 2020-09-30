package com.example.yang.util;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.yang.myapplication.sqlite_linkmanmss;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static android.bluetooth.le.ScanSettings.SCAN_MODE_LOW_LATENCY;
import static java.lang.Thread.sleep;

/****************************************************************
 * function : 蓝牙相关功能：搜索，广播，连接，断开，数据收发
 * *************************************************************/
@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
public class Bluetoothutil {

    private final String TAG = "Bluetoothutil";

    private final int REMOTERSP = 0;

    private static String account = null;

    private byte[] blehservice = {0x00, 0x00, 0x7a, 0x21, 0x3c, 0x6e, 0x54, 0x16, 0x1e, 0x64, 0x5c, 0x3f, 0x2b, 0x56, 0x01, 0x49};
    private byte[] blechr = {0x00, 0x00, 0x7a, 0x21, 0x3c, 0x6e, 0x54, 0x16, 0x1e, 0x64, 0x5c, 0x3f, 0x2b, 0x56, 0x01, 0x79};
    private byte[] appid = {0x01, 0x79};
	private static byte[] client = new byte[1000];
	private static int length = 0;

	//男
    private int sex = 0x0101;
    //UUID
    private UUID UUID_SERVICE = UUID.fromString(encodeUUIDString(blehservice));
    private UUID UUID_CHARACTERISTIC = UUID.fromString(encodeUUIDString(blechr));
    private UUID UUID_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    private SharedPreferencedUtils sharedPreferencedUtils = new SharedPreferencedUtils();

    private Context mcontext;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    private static BluetoothManager manager = null;
    private static BluetoothLeScanner mBLEScanner;;

    //定义Gatt实现类
    private static BluetoothGatt mBluetoothGatt;
    private static BluetoothDevice mBluetoothDevice;

    //验证码
    private byte[] key = {0x09,0x13,0x17,0x19,0x21};
    private boolean isauthentication = false;
    private String authenticationrsp = "Permission denied";
	
	private ArrayList<String> blelist = new ArrayList<>();

    private BluetoothGattCallback mGattCallback = new daqiBluetoothGattCallback();

    private advertiseCallback madvertiseCallback;
    private BluetoothScanCallback mbluetoothScanCallback;
    private BluetoothLeScanCallback mbluetoothLeScanCallback;
    private BluetoothGattServer mBluetoothGattServer = null;
    private sqlite_linkmanmss sql;

    private File file;

    private Handler mnHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case REMOTERSP:
                    byte[] value = (byte[]) msg.obj;
                    String valuestring = new String(value);
                    Log.d(TAG, "handleMessage " + valuestring);
                    if (valuestring.equals("end")) {
                        byte[] data = new byte[length];
                        System.arraycopy(client, 0, data, 0, length);
                        if (new String(data).isEmpty()) {
                            return false;
                        }
                                            
                        getFriendInfo(new String(data));
                       
                        length = 0;
                        client = new byte[1000];
                        disconnectionremoteDevice();
                    } else {
                        Log.d(TAG, "      " + valuestring + " " + client.length);
                        System.arraycopy(value, 0, client, length, value.length);
                        length = length + value.length;
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public Bluetoothutil(Context context) {
        mcontext = context;
        sql = new sqlite_linkmanmss(mcontext, "link", null, 1);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            madvertiseCallback = new advertiseCallback();
        }

        if (CheckPermission.checkBluetooth(context)) {
          /*  FileOperationUtil.CreateDir(FileOperationUtil.SECONDFILEDIRPATH);
            FileOperationUtil.CreateDir(FileOperationUtil.THIRDANNOUNCEADDDIRPATH);
            file = FileOperationUtil.CreateFile(FileOperationUtil.THIRDANNOUNCEADDDIRPATH + File.separator + SystemUtil.getNumSmallLetter(18) + ".json");*/
            manager = (BluetoothManager) mcontext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = manager.getAdapter();

            //判断蓝牙是否开启，如果关闭则请求打开蓝牙
            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                //方式二：半静默打开蓝牙
                //低版本android会静默打开蓝牙，高版本android会请求打开蓝牙
                mBluetoothAdapter.enable();
            }
        }
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mcontext.registerReceiver(bluetoothbroadcastreceiver,filter);
    }

    public void getFriendInfo(String maccount) {
        Log.d(TAG, "getFriendInfo  " + maccount);

        if (maccount == null) {
            return ;
        }

        sql.open();
        Cursor cu = sql.getContact("blescanresult",sqlite_linkmanmss.KEY_ACTNB, maccount);
        if(cu.getCount() > 0){
			Log.d(TAG, "getFriendInfo  getCount: "+cu.getCount());
            sql.close();
            return ;
        }
        sql.close();

        XmppConnection xmppConnection = XmppConnection.getInstance();
        try {
            VCard vCard = xmppConnection.getFriendunisInfo(maccount);
            Map<String, Object> save = new HashMap<String, Object>();
            save.put(sqlite_linkmanmss.KEY_ROWID, vCard.getAvatar());
            save.put(sqlite_linkmanmss.KEY_NAME, vCard.getNickName());
            save.put("age", 10);
            save.put("sex","1");
            save.put("credit_value",100);
            save.put("real_name","yes");
            save.put("independent_money","dfs");
            save.put("position","sdfas");
            save.put(sqlite_linkmanmss.KEY_TIME, GetCurrentTime());
            save.put(sqlite_linkmanmss.KEY_ACTNB, maccount);

            sql.open();
            sql.insertContact("blescanresult", save);
            sql.close();

            Log.d(TAG, "getFriendInfo has insert databases "+maccount);

            Intent recvintent = new Intent();
            recvintent.setAction("com.example.yang.Bluetoothutil");
            recvintent.putExtra("account",vCard.getNickName());
            //创建一个本地广播管理器
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(mcontext);
            //发送本地广播
            localBroadcastManager.sendBroadcast(recvintent);

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
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startBluetoothBroadcast(){
        account = sharedPreferencedUtils.getUserInfo(mcontext, sqlite_linkmanmss.KEY_ACTNB,"null");
		Log.d(TAG, "startBluetoothBroadcast:"+account);

        //初始化广播设置
        AdvertiseSettings mAdvertiseSettings = new AdvertiseSettings.Builder()
                //设置广播模式，以控制广播的功率和延迟。
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                //发射功率级别
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH)
                //不得超过180000毫秒。值为0将禁用时间限制。
                .setTimeout(0)
                //设置是否可以连接
                .setConnectable(true)
                .build();
        //初始化广播包
        AdvertiseData mAdvertiseData = new AdvertiseData.Builder()
                //设置广播设备名称
                .setIncludeDeviceName(true)
                //设置发射功率级别
                .setIncludeDeviceName(true)
                .build();

        //初始化扫描响应包
        AdvertiseData mScanResponseData = new AdvertiseData.Builder()
                //隐藏广播设备名称
                .setIncludeDeviceName(false)
                //隐藏发射功率级别
                .setIncludeDeviceName(false)
                //设置广播的服务UUID
                .addServiceUuid(new ParcelUuid(UUID_SERVICE))
                //设置厂商数据
                .addManufacturerData(sex,appid)
                .build();

        initBluetoothPrimaryService();
        //获取BLE广播的操作对象。
        //如果蓝牙关闭或此设备不支持蓝牙LE广播，则返回null。
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();

        //mBluetoothLeAdvertiser不为空，且蓝牙已开打
        if (mBluetoothAdapter.isEnabled()) {
            if (mBluetoothLeAdvertiser != null) {
                //开启广播
                mBluetoothLeAdvertiser.startAdvertising(mAdvertiseSettings,
                        mAdvertiseData, mScanResponseData, madvertiseCallback);
            } else {
                Log.d(TAG, "该手机不支持ble广播");
            }
        } else {
            Log.d(TAG, "手机蓝牙未开启");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void stopBluetoothbroadcast(){
		Log.d(TAG, "stopBluetoothbroadcast");
        if(mBluetoothLeAdvertiser != null) {
            mBluetoothLeAdvertiser.stopAdvertising(madvertiseCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class advertiseCallback extends AdvertiseCallback {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            Log.d(TAG, "startAdvertising");
        }

        //无法启动广播回调。
        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            Log.d(TAG, "开启服务失败，失败码 = " + errorCode);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void initBluetoothPrimaryService(){
        BluetoothGattService service = new BluetoothGattService(UUID_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);
        //初始化特征值
        BluetoothGattCharacteristic mGattCharacteristic = new BluetoothGattCharacteristic(UUID_CHARACTERISTIC,
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE|
                        BluetoothGattCharacteristic.PROPERTY_NOTIFY|
                        BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PERMISSION_WRITE|
                        BluetoothGattCharacteristic.PERMISSION_READ);

        //初始化描述
        BluetoothGattDescriptor mGattDescriptor = new BluetoothGattDescriptor(UUID_DESCRIPTOR,BluetoothGattDescriptor.PERMISSION_WRITE);

        service.addCharacteristic(mGattCharacteristic);
        mGattCharacteristic.addDescriptor(mGattDescriptor);

        //初始化GattServer回调
        daqiBluetoothGattServerCallback mBluetoothGattServerCallback = new daqiBluetoothGattServerCallback();

        if (manager != null && mBluetoothGattServer == null) {
            mBluetoothGattServer = manager.openGattServer(mcontext, mBluetoothGattServerCallback);

            boolean result = mBluetoothGattServer.addService(service);
            if (result) {
                Log.d(TAG, "initBluetoothPrimaryService add service success");
            } else {
                Log.d(TAG, "initBluetoothPrimaryService add service fail");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private class daqiBluetoothGattServerCallback extends BluetoothGattServerCallback {
        //设备连接/断开连接回调
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            super.onConnectionStateChange(device, status, newState);
            // status 用于返回操作是否成功,会返回异常码。
            // newState 返回连接状态，如BluetoothProfile#STATE_DISCONNECTED、BluetoothProfile#STATE_CONNECTED
            Log.d(TAG,device+" :status:"+status+", new status:"+newState);
            //操作成功的情况下
            if (status == BluetoothGatt.GATT_SUCCESS){
                //判断是否连接码
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.d(TAG,"Connect");
                }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                    //判断是否断开连接码
                    Log.d(TAG,"DIS");
                }
            }
        }

        //添加本地服务回调
        @Override
        public void onServiceAdded(int status, BluetoothGattService service) {
            super.onServiceAdded(status, service);
        }

        //特征值读取回调
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);            

        }

        //特征值写入回调
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId, BluetoothGattCharacteristic characteristic, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);            
            if(isEq(key,value)){
                isauthentication = true;
                 new Thread(new Runnable() {
                    @Override
                    public void run() {
                        byte[] data = account.getBytes();
                        int i;
                        int len = data.length;
						byte[] frem;
                        for (i = 0; len > 0; i++) {   
                            Log.d(TAG," Thread "+len);                        
                            if (len > 20) {
								frem = new byte[20];
                                System.arraycopy(data, 20 * i, frem, 0, 20);
                                len = len-20;
                            } else {
								frem = new byte[len];
                                System.arraycopy(data, 20 * i, frem, 0, len);
                                len = 0;
                            }
							Log.d(TAG,"onCharacteristicWriteRequest FREM:"+new String(frem));
                            characteristic.setValue(frem);
                            mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
                        }
                        try {
                            sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        characteristic.setValue("end".getBytes());
                        mBluetoothGattServer.notifyCharacteristicChanged(device, characteristic, false);
                    }
                }).start();

            } else {
                isauthentication = false;
                mBluetoothGattServer.cancelConnection(device);
            }
        }

        @Override
        public void onNotificationSent(BluetoothDevice device, int status) {
            super.onNotificationSent(device, status);
        }

        //描述读取回调
        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset, BluetoothGattDescriptor descriptor) {
            super.onDescriptorReadRequest(device, requestId, offset, descriptor);
        }

        //描述写入回调
        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId, BluetoothGattDescriptor descriptor, boolean preparedWrite, boolean responseNeeded, int offset, byte[] value) {
            super.onDescriptorWriteRequest(device, requestId, descriptor, preparedWrite, responseNeeded, offset, value);
        }
    }

    private BroadcastReceiver bluetoothbroadcastreceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取蓝牙广播  本地蓝牙适配器的状态改变时触发
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                //获取蓝牙广播中的蓝牙新状态
                int blueNewState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                //获取蓝牙广播中的蓝牙旧状态
                int blueOldState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueNewState) {
                    //正在打开蓝牙
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG,"STATE_TURNING_ON");
                        stopBluetoothbroadcast();
                        break;
                    //蓝牙已打开
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG,"STATE_ON");
                        break;
                    //正在关闭蓝牙
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG,"STATE_TURNING_OFF");
                        startBluetoothBroadcast();
                        break;
                    //蓝牙已关闭
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG,"STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG,"STATE_CONNECTING");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG,"STATE_CONNECTED");
                        break;
                }
            }
        }
    };

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startBluetoothScan() {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            //开始扫描
            mbluetoothLeScanCallback = new BluetoothLeScanCallback();
            mBluetoothAdapter.startLeScan(mbluetoothLeScanCallback);
        } else {
            //获取 5.0 的扫描类实例
            mBLEScanner = mBluetoothAdapter.getBluetoothLeScanner();
            //开始扫描
            //可设置过滤条件，在第一个参数传入，但一般不设置过滤。
            List<ScanFilter> filters = new ArrayList<>();
            ScanFilter.Builder builder = new ScanFilter.Builder();
            builder.setServiceUuid(new ParcelUuid(UUID_SERVICE));
            //builder.setManufacturerData(sex, appid);
            filters.add(builder.build());
            //创建ScanSettings的build对象用于设置参数
            ScanSettings.Builder sansettingbuild = new ScanSettings.Builder()
                    //设置高功耗模式
                    .setScanMode(SCAN_MODE_LOW_LATENCY);
            //android 6.0添加设置回调类型、匹配模式等
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                //定义回调类型
                sansettingbuild.setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES);
                //设置蓝牙LE扫描滤波器硬件匹配的匹配模式
                sansettingbuild.setMatchMode(ScanSettings.MATCH_MODE_STICKY);
            }
            //芯片组支持批处理芯片上的扫描
            if (mBluetoothAdapter.isOffloadedScanBatchingSupported()) {
                //设置蓝牙LE扫描的报告延迟的时间（以毫秒为单位）
                //设置为0以立即通知结果
                sansettingbuild.setReportDelay(0L);
            }
            mbluetoothScanCallback = new BluetoothScanCallback();
            mBLEScanner.startScan(filters, sansettingbuild.build(), mbluetoothScanCallback);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void stopBluetoothScan() {
        if (android.os.Build.VERSION.SDK_INT < 21) {
            //停止扫描
            mBluetoothAdapter.stopLeScan(mbluetoothLeScanCallback);
        } else {
            //获取 5.0 的扫描类实例
            //停止扫描
            mBLEScanner.stopScan(mbluetoothScanCallback);
        }
    }

    private class BluetoothLeScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(!blelist.contains(device.getAddress())) {
                Log.d(TAG,""+scanRecord);
                connectionremoteDevice(device.getAddress());
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class BluetoothScanCallback extends ScanCallback{
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
                if(!blelist.contains(result.getDevice().getAddress())) {
                    Log.d(TAG,""+result);
                    connectionremoteDevice(result.getDevice().getAddress());
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void connectionremoteDevice(String addr){
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(addr);
        //连接设备
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = mBluetoothDevice.connectGatt(mcontext,
                    false, mGattCallback, BluetoothDevice.TRANSPORT_LE);
        } else {
            mBluetoothGatt = mBluetoothDevice.connectGatt(mcontext, false, mGattCallback);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void disconnectionremoteDevice(){
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }

    //定义蓝牙Gatt回调类
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public class daqiBluetoothGattCallback extends BluetoothGattCallback {

        //连接状态回调
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);           

            // status 用于返回操作是否成功,会返回异常码。
            //操作成功的情况下
            if (status == BluetoothGatt.GATT_SUCCESS){
                //判断是否连接码
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    //可延迟发现服务，也可不延迟
                    //发现服务
                    Log.d(TAG,"STATE_CONNECTED");
                    gatt.discoverServices();
                    blelist.add(gatt.getDevice().getAddress());
                }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                    //判断是否断开连接码
                    Log.d(TAG,"STATE_DISCONNECTED");
                }
            }
        }

        //服务发现回调
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(status == BluetoothGatt.GATT_SUCCESS) {
                //获取指定uuid的service
                BluetoothGattService gattService = gatt.getService(UUID_SERVICE);
                if (gattService != null) {
                    //写入需要传递给外设的特征值（即传递给外设的信息）
                    BluetoothGattCharacteristic gattCharacteristic = gattService.getCharacteristic(UUID_CHARACTERISTIC);
                    if (gattCharacteristic != null) {
                        gattCharacteristic.setValue(key);
                        //通过GATt实体类将，特征值写入到外设中。
                        gatt.writeCharacteristic(gattCharacteristic);
                    }
                    gatt.setCharacteristicNotification(gattCharacteristic, true);
                }
            }
        }

        //特征写入回调
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);            
            /*if(status == BluetoothGatt.GATT_SUCCESS){
				gatt.readCharacteristic(characteristic);
            }else{
				disconnectionremoteDevice();
			}*/
        }
 
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG,"connectionremoteDevice onCharacteristicRead "+new String(characteristic.getValue()));           
        }

        //外设特征值改变回调
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
			Message msg = new Message();
            msg.obj = characteristic.getValue();
                        msg.what = REMOTERSP;
                        mnHandler.sendMessage(msg);
        }

        //描述写入回调
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    }

    private static String encodeUUIDString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
            if(i ==3 || i==5 || i==7 || i==9){
                stringBuilder.append("-");
            }
        }
        System.out.println(stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * byte数组内数字是否相同
     * @param s1
     * @param s2
     * @return
     */
    public boolean isEq(byte[] s1,byte[] s2){
        int slen=s1.length;
        if(slen==s2.length){
            for(int index=0;index<slen;index++){
                if(s1[index]!=s2[index]){
                    return false;
                }
            }
            return true;
        }
        return  false;
    }

    /******************************************************************
     *获取当前时间
     ******************************************************************/
    private StringBuilder GetCurrentTime() {
        Date mdate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        StringBuilder mBuilder = new StringBuilder();

        mBuilder.append(sdf.format(mdate));

        return mBuilder;
    }

}
