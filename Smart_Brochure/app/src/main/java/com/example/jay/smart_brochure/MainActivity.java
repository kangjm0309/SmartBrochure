package com.example.jay.smart_brochure;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.TabActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * 메인엑티비티. 탭 뷰 구성.
 */


public class MainActivity extends TabActivity {
    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.

    public ArrayList<String> devices = new ArrayList<String>();

    Database data = new Database(this);

    private BackPressed backbtn;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
            super.onCreate(savedInstanceState);

        Boolean checkService = getServiceTaskName();
        if(checkService == false){
            Intent i = new Intent(this, SearchBLE.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
            this.startService(i);
        }
        backbtn = new BackPressed(this);

        // BLE Search

        startActivity(new Intent(this, Splash.class)); // Splash
        setContentView(R.layout.activity_main);

        // Use this check to determine whether BLE is supported on the device. Then
// you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "This app requires device supporting BLE.", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This app requires a bluetooth capable phone", Toast.LENGTH_SHORT).show();
        }
        else{
            // When bluetooth is off.
            if(!mBluetoothAdapter.isEnabled()){
                turnOnBT();
            }
        }

        Resources res = getResources();
            TabHost tabHost = getTabHost();
            TabHost.TabSpec spec;
            Intent intent;

        intent = new Intent(this, My.class);
        spec = tabHost.newTabSpec("My")
                .setIndicator("My", res.getDrawable(android.R.drawable.sym_action_chat))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent(this, Info.class);
        spec = tabHost.newTabSpec("Info")
                .setIndicator("Info", res.getDrawable(android.R.drawable.sym_action_email))
                .setContent(intent);
        tabHost.addTab(spec);

        // 주변 기기 수동검색 삭제.
/*        intent = new Intent(this, Around.class);
        spec = tabHost.newTabSpec("Around")
                .setIndicator("Around", res.getDrawable(android.R.drawable.sym_action_call))
                .setContent(intent);
        tabHost.addTab(spec);*/

        intent = new Intent(this, Setting.class);
        spec = tabHost.newTabSpec("Settings")
                .setIndicator("Settings", res.getDrawable(android.R.drawable.sym_action_call))
                .setContent(intent);
        tabHost.addTab(spec);

        tabHost.setCurrentTab(0);
    }

    // Method to request the bluetooth to be on.
    private void turnOnBT() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED){
            Toast.makeText(getApplicationContext(), "This app requires the bluetooth to be on", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        backbtn.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(this, "onDestroy()", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    public boolean getServiceTaskName() {
        Log.d("getServiceTaskName()", "hihi");
        boolean checked = false;

        ActivityManager am = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        Log.d("getServiceTaskName()", "activity_service");

        for(ActivityManager.RunningServiceInfo service : am.getRunningServices(Integer.MAX_VALUE)){
            if(!service.service.getClassName().equals("SearchBLE"))
                checked = false;
            else
                checked = true;
        }

        return checked;
    }
}
/*    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            Log.i("ttt", "onLeScan");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String[] s = { device.getAddress(), device.getName() };
                    Boolean check = true;
                    Boolean b = true;
                    Log.d("runrunrun", "run 드루와");

                    for(int i = 0; i < beacons.size(); i++){
                        if(beacons.get(i).toString().equals(device.getAddress())){
                            if(devices.size() == 0) {
                                devices.add(device.getAddress());
                                check = false;
                            }
                            else{
                                for(int j = 0; j < devices.size(); j++){
                                    if(devices.get(j).toString().equals(device.getAddress()))
                                        break;
                                    else
                                        check = false;
                                }
                            }
                        }
                    }

                    if(check == false) {
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
            });
        }
    };

    // 스캔 시작 및 중단
    public void scanLeDevice(final boolean enable) {
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

    // 블루투스 활성화/비활성화 시 자동으로 검색
    public final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_ON:
                        Log.i("ttt", "State_on");
                        Toast.makeText(getApplicationContext(), "블루투스가 연결되었습니다." + "\n" + "BLE검색 시작", Toast.LENGTH_SHORT).show();
                        scanLeDevice(true);
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        //Toast.makeText(getApplicationContext(), "블루투스를 다시 연결해주세요.", 3000).show();
                        Toast.makeText(getApplicationContext(), "블루투스를 다시 연결해주세요.", Toast.LENGTH_SHORT).show();
                        scanLeDevice(false);
                        Log.i("ttt", "scanLeDevice(false);");
                        break;
                }
            }

        }
    };*/



    // Search_BLE 서비스에서 실행하므로, 삭제
/*    private void sendId() throws IOException, JSONException {
        if(devices.size()!=0) {
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
                Log.d("response", "Data::"+strResltJsonData);

                JSONObject joResData = new JSONObject(strResltJsonData);
                JSONObject jaResData = (JSONObject) ((JSONArray) joResData.get("res_data")).get(0);
                JSONObject jaRRE = (JSONObject)((JSONArray)jaResData.get("_exBea")).get(0);
                Log.d("response", "Name::"+jaRRE.get("_cd"));


                Intent intent = new Intent(this, Push_Clicked.class);
                intent.putExtra("exh_cd", (String)jaRRE.get("_cd"));
                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder b = new NotificationCompat.Builder(this);

                b.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setTicker("Hearty365")
                        .setContentTitle("Default notification")
                        .setContentText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.")
                        .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                        .setContentIntent(contentIntent)
                        .setContentInfo("Info");


                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
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

    // SearchBLE 서비스 on/off 여부 검사



