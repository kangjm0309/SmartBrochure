package com.example.jay.smart_brochure;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jay on 2015-05-27.
 */
public class SearchBLE extends Service {
    public IBinder onBind(Intent intent) {
        return null;
    }

    BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler;
    private Boolean mScanning;
    private ArrayList<String> devices;
    private String sending = new String();

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10*1000;

    private final String LC0000 = "LC0000";
    private static String url = "http://jung2.maden.kr/beacon_gateway/";
    Database data = new Database(this);
    ArrayList<String> beacons;
    Timer mTimer = new Timer();
    IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        registerReceiver(mReceiver, filter);
        mHandler = new Handler();

        devices = new ArrayList<String>();
        data.open();
        if(data.check())  // 처음 다운로드시 db가 없기떄문에 확인
            data.init();  // db가 비어있을 경우, init();
        data.updateBeacons(); // 추가된 비콘 있을 경우 추가.
        beacons = data.getBeacons();
        data.close();

        if(mBluetoothAdapter.isEnabled()){
            Toast.makeText(this, "서치 시작", Toast.LENGTH_SHORT).show();
            mTimer = new Timer();
            TimerTask search = new TimerTask(){
                public void run() {
                    scanLeDevice(true);
                }
            };
            mTimer.schedule(search, 1000, 20*1000);
            Log.d("mReceiver", "mTimer.schedule");
            scanLeDevice(true);

        }

    }

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.",Toast.LENGTH_SHORT).show();
                        // 검색 주기 설정
                        mTimer.cancel();
                        mTimer = new Timer();
                        TimerTask search = new TimerTask(){
                            public void run() {
                                scanLeDevice(true);
                                Log.d("Timer", "search");
                            }
                        };
                        mTimer.schedule(search, 1000, 20*1000);
                        Log.d("mReceiver", "mTimer.schedule");
                            scanLeDevice(true);

                        break;
                    case BluetoothAdapter.STATE_OFF:
                        mTimer.cancel();
                        scanLeDevice(false);
                        break;
                }
            }

        }

    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            Log.d("ttt", "onLeScan");

            Log.d("ttt", "device.getAddress(): " + device.getAddress());
            Log.d("hihihihihihihihihihihih", "devices" + devices);
            Handler sHandler = new Handler(Looper.getMainLooper());
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Boolean check = true;
                    for (int i = 0; i < beacons.size(); i++) {
                        Log.d("forforforfor", "for문 시작 " + i + "번째");
                        if (beacons.get(i).toString().equals(device.getAddress())) {
                            if (devices.size() == 0) {
                                check = false;
                            } else {
                                for (int j = 0; j < devices.size(); j++) {
                                    if (devices.get(j).toString().equals(device.getAddress())) {
                                        check = true;
                                        break;
                                    }
                                    else {
                                        check = false;
                                    }
                                }
                            }
                        }
                    }

                    if (check == false) {
                        scanLeDevice(false);
                        //Toast.makeText(getApplicationContext(), device.getAddress(), Toast.LENGTH_SHORT).show();
                        try {
                            devices.add(device.getAddress());
                            sending = device.getAddress();
                            Log.d("CheckCheckCheck", "센딩 집어넣는다");
                            data.open();
                            ArrayList<String> checking = data.getHistoryAddress();
                            data.close();
                            Boolean secondCheck = false;
                            for(int i = 0; i < checking.size(); i++){
                                if(device.getAddress().equals(checking.get(i))){
                                    secondCheck = false;
                                    break;
                                }
                                else
                                    secondCheck = true;
                            }
                            if(secondCheck = true) {
                                Log.d("CheckCheckCheck", "secondCheck = true :: sending = " + sending);
                                scanLeDevice(false);
                                secondCheck = true;
                                check = true;
                                sendId();
                            }
                            else{}
                            scanLeDevice(true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }, 0);

        }
    };

        private void scanLeDevice(final boolean enable) {
            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.i("ttt", " startLeScan");

            } else {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }

    private void sendId() throws IOException, JSONException {
        if (devices.size() != 0) {
//            String id = devices.get(0).toString();
        }
        ArrayList<String> id = new ArrayList<String>();
        id.add(devices.get(devices.size()-1));

        HashMap<String, Object> mapReqData = new HashMap<String, Object>();
        Log.d("CheckCheckCheck", "sendid() : : sending = " + sending);
        Log.d("CheckCheckCheck", "sendid() : : devices" + "["+sending+"]");
        mapReqData.put("beacon_id", id);
        Log.d("CheckCheckCheck", "sendid() : : mapReqData = " + mapReqData);

        JSONObject beaconObject = new JSONObject(mapReqData);
        JSONObject dataObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jArrayBeacon = new JSONArray();
        jsonArray.put(beaconObject);
        dataObject.put("req_data", jsonArray);
        dataObject.put("req_svc", LC0000);

        String dataString = dataObject.toString();

        DefaultHttpClient client = new DefaultHttpClient();
        try {
            //android 3.0 부터는 네트워크작업을 UI쓰레드가 아닌 별도의 쓰레드로 돌려야해서
            if (android.os.Build.VERSION.SDK_INT > 9) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                //Param 설정
                ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                paramList.add(new BasicNameValuePair("JSONData", dataString));
                //Toast.makeText(getActivity().getApplicationContext(), paramList.toString(), Toast.LENGTH_SHORT).show();
                //연결지연시
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params, 3000);
                HttpConnectionParams.setSoTimeout(params, 3000);
                //Json 데이터를 서버로 전송
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));

                DefaultHttpClient httpclient = new DefaultHttpClient();
                ResponseHandler<String> reshand = new BasicResponseHandler();
                String strResponseBody = httpclient.execute(httpPost, reshand);
                String strResultJsonData = strResponseBody.trim();
                Log.d("response", "Data::" + strResultJsonData);

                JSONObject joResData = new JSONObject(strResultJsonData);
                JSONObject jaResData = (JSONObject) ((JSONArray) joResData.get("res_data")).get(0);
                JSONObject jaRRE = (JSONObject) ((JSONArray) jaResData.get("_exBea")).get(0);
                Log.d("response", "Name::" + jaRRE.get("_cd"));


                Intent intent = new Intent(this, Push_Clicked.class);
                intent.putExtra("exh_cd", (String) jaRRE.get("_cd"));
                intent.putExtra("address", devices.get(0).toString());
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder b = new NotificationCompat.Builder(this);

                HashMap<String, Object> _mapReqData = new HashMap<String, Object>();
                _mapReqData.put("_exCd",(String) jaRRE.get("_cd"));

                Transfer transfer = new Transfer();
                JSONObject jaData = transfer.transData("LC0002", _mapReqData);

                String ehnm = (String)((JSONObject)jaData.get("_ex_inform")).get("_eh_nm");

                b.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker("Smart_Brochure")
                        .setContentTitle(ehnm+" 브로셔 도착!")
                        .setContentText("브로셔를 받으시려면 클릭")
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent)
                        .setContentInfo("Info");


                NotificationManager notificationManager = (NotificationManager) SearchBLE.this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(devices.size(), b.build());

                scanLeDevice(true);



/*
                // httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("Content-type", "application/json");
                //데이터보낸 뒤 서버에서 데이터를 받아오는 과정
                ResponseHandler<String> reshand = new BasicResponseHandler();

                HttpResponse response = client.execute(httpPost);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                String line = null;
                String result = "";

                while ((line = bufferReader.readLine()) != null) {
                    result += line;

                }*/
            } else {

            }
            Log.i("aaaaaaaaaaaaaaaaaaaa", dataString);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            client.getConnectionManager().shutdown(); // 연결 지연 종료
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("destroy", "꺼질때!!!stopSelf전");
//        unregisterReceiver(mReceiver);
//        unregisterReceiver();
        stopSelf();
        Log.d("destroy", "꺼질때!!!stopSelf후" );
    }

}
