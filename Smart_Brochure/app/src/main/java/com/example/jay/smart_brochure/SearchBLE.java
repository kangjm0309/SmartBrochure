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

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 100000000;

    private final String LC0000 = "LC0000";
    private static String url = "http://jung2.maden.kr/beacon_gateway/";

    Database data = new Database(this);
    ArrayList<String> beacons;
    Timer mTimer = new Timer();
    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        mHandler = new Handler();

        devices = new ArrayList<String>();
        data.open();
        beacons = data.getBeacons();
        data.close();


    }



/*    private void Searching() {
        timer = new CountDownTimer(10 * 1000, 20 * 1000) { // 20초마다, 10초동안
            public void onTick(long millisUntilFinished) {
                checkingTime = millisUntilFinished;
            }

            public void onFinish() {
                checkingTime = 0;
            }
        };
    }*/

    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다.",Toast.LENGTH_SHORT).show();
                        // 검색 주기 설정
                        mTimer = new Timer();
                        TimerTask search = new TimerTask(){
                            public void run() {
                                scanLeDevice(true);
                                Log.d("Timer", "search");
                            }
                        };
                        mTimer.schedule(search, 1000, 10*1000);
                        Log.d("mReceiver", "mTimer.schedule");
                            scanLeDevice(true);
                            Toast.makeText(getApplicationContext(), "검색중!", Toast.LENGTH_SHORT).show();

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
            Log.d("mLeScanCallback", "Handler 이전");
            Handler sHandler = new Handler(Looper.getMainLooper());
            Log.d("mLeScanCallback", "Handler Handler 초기화");
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("mLeScanCallback", "Handler 안쪽 run");
                    Boolean check = true;
                    for (int i = 0; i < beacons.size(); i++) {
                        Log.d("abbbbaaa", "for문 진입");
                        if (beacons.get(i).toString().equals(device.getAddress())) {
                            Log.d("abbbbaaa", "if문 진입");
                            if (devices.size() == 0) {
                                devices.add(device.getAddress());
                                check = false;
                            } else {
                                for (int j = 0; j < devices.size(); j++) {
                                    if (devices.get(j).toString().equals(device.getAddress()))
                                        break;
                                    else
                                        check = false;
                                }
                            }
                        }
                    }

                    if (check == false) {
                        Toast.makeText(getApplicationContext(), device.getAddress(), Toast.LENGTH_SHORT).show();
                        Log.i("ttt", "자동검색  :  " + device.getAddress());
                        try {
                            sendId();
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
                        Log.i("ttt", " mScanning = false;");
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                        Log.i("ttt", "mBluetoothAdapter.stopLeScan(mLeScanCallback);");
                    }
                }, SCAN_PERIOD);

                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.i("ttt", " startLeScan");

            } else {
                Log.i("ttt", " scanLeDevice, else");
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }

/*        private void sendId() throws IOException, JSONException {
            if (devices.size() != 0) {
                String id = devices.get(0).toString();
            }

            HashMap<String, Object> mapReqData = new HashMap<String, Object>();
            mapReqData.put("beacon_id", devices);

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
                    String strResltJsonData = strResponseBody.trim();
                    Log.d("response", "Data::" + strResltJsonData);

                    JSONObject joResData = new JSONObject(strResltJsonData);
                    JSONObject jaResData = (JSONObject) ((JSONArray) joResData.get("res_data")).get(0);
                    JSONObject jaRRE = (JSONObject) ((JSONArray) jaResData.get("_exBea")).get(0);
                    Log.d("response", "Name::" + jaRRE.get("_ehnm"));

                    Intent intent = new Intent(SearchBLE.this, Push_Clicked.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(SearchBLE.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder b = new NotificationCompat.Builder(SearchBLE.this);

                    b.setAutoCancel(true)
                            .setDefaults(Notification.DEFAULT_ALL)
                            .setWhen(System.currentTimeMillis())
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setTicker("TeamWSB")
                            .setContentTitle("브로셔 도착!")
                            .setContentText("브로셔를 받으시려면 클릭")
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                            .setContentIntent(contentIntent)
                            .setContentInfo("Info");


                    NotificationManager notificationManager = (NotificationManager) SearchBLE.this.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, b.build());

                *//*


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

                }*//*
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
        }*/


    private void sendId() throws IOException, JSONException {
        if (devices.size() != 0) {
            String id = devices.get(0).toString();
        }

        HashMap<String, Object> mapReqData = new HashMap<String, Object>();
        mapReqData.put("beacon_id", devices);

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
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder b = new NotificationCompat.Builder(this);

                b.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker("TeamWSB")
                        .setContentTitle("브로셔 도착!")
                        .setContentText("브로셔를 받으시려면 클릭")
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent)
                        .setContentInfo("Info");


                NotificationManager notificationManager = (NotificationManager) SearchBLE.this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, b.build());



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


    }
