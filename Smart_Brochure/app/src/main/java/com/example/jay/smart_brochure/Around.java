package com.example.jay.smart_brochure;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Around extends Activity {

    private customAdapter myAdap;
    private ListView list;
    public ArrayList<String[]> devices;

    BluetoothAdapter mBluetoothAdapter;

    private Handler mHandler;
    private Boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    private final String LC0001 = "LC0001";
    private static String url = "http://jung2.maden.kr/beacon_gateway";

    Database db = new Database(this);
    ArrayList<String> b_list = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_around);

        init();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "No bluetooth detected ", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                turnOnBT();
            }
        }

        // ble를 지원하는지 확인
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "이 기기는 BLE를 지원하지 않습니다.", Toast.LENGTH_SHORT)
                    .show();
            finish();
        }
    }

    private void turnOnBT() {
        // TODO Auto-generated method stub
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_ENABLE_BT);
    }

    private void init() {
        // TODO Auto-generated method stub
        db.open();
        db.beaconList();
        b_list = db.getBeacons();
        Log.i("ttt", "b_list.size() : : : " + b_list.size());
        db.close();
        devices = new ArrayList<String[]>();

		/*
		 * // test String[] first = { "주소1", "이름1" }; String[] second = { "주소2",
		 * "이름2" };
		 *
		 * devices.add(first); devices.add(second);
		 */

        myAdap = new customAdapter(this, devices);
        list = (ListView) findViewById(R.id.listview);
        list.setAdapter(myAdap);

        mHandler = new Handler();


        for(int i = 0 ; i < b_list.size() ; i++)
            Log.i("ttt", "b_list : " + b_list.get(i).toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_around, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void mOnClick(View v) {
        TextView txt = (TextView) findViewById(R.id.sub_title);
        switch (v.getId()) {
            case R.id.bScan:
                if (!mBluetoothAdapter.isEnabled()) {
                    turnOnBT();
                }
                if(!mBluetoothAdapter.isEnabled()){
                    Toast.makeText(this, "블루투스를 켜야합니다.", Toast.LENGTH_SHORT).show();
                    break;
                }
                txt.setText("검색중...");
                scanLeDevice(true);
                Log.i("ttt", "스캔");
                break;
            case R.id.bCancel:
                scanLeDevice(false);
                txt.setText("검색된 전시회");
                Log.i("ttt", "스캔종료");
                break;
        }
    }

    class customAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String[]> arry;

        public customAdapter(Context context, ArrayList<String[]> arry) {

            super(context, R.layout.around_layout);

            this.context = context;
            this.arry = arry;
        }

        @Override
        public int getCount() {
            return arry.size();
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            LayoutInflater li = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = li.inflate(R.layout.around_layout, null);

            TextView address = (TextView) convertView
                    .findViewById(R.id.device_address);
            TextView name = (TextView) convertView
                    .findViewById(R.id.device_name);

            address.setText(arry.get(position)[0]);
            name.setText(arry.get(position)[1]);
            return convertView;
        }
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             byte[] scanRecord) {
            Log.i("ttt", "onLeScan");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 스캔해서 device에서 주소랑 이름 불러와서 리스트에 뿌려줌
                    String[] s = { device.getAddress(), device.getName() };
                    Boolean check = false;
                    for(int i = 0 ; i < b_list.size() ; i++ ){
                        if(b_list.get(i).toString().equals(s[0])){
                            for(int j = 0 ; j < devices.size() ; j++){
                                if(devices.get(j)[0].toString().equals(s[0])){
                                    check = true;
                                    break;
                                }
                                else {
                                    check = false;
                                }
                            }
                        }
                    }

                    if(check == false)
                    devices.add(s);
                    Log.i("ttt", "device.getAddress(): " + device.getAddress());
/*                    try {
                        sendId();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }*/
                    myAdap.notifyDataSetChanged();
                }
            });
        }
    };

    // 스캔 시작 및 중단
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    Log.i("ttt", " mScanning = false;");
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    if(devices != null){
                        TextView txt = (TextView)findViewById(R.id.sub_title);
                        txt.setText("검색된 전시회");
                    }
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

    private void sendId() throws IOException, JSONException {
        String id = devices.get(0).toString();


        JSONObject beaconObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jArrayBeacon = new JSONArray();
        beaconObject.put("beacon_id", devices);
        jsonArray.put(beaconObject);
        dataObject.put("_req_data", jsonArray);
        dataObject.put("_req_svc", LC0001);

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

                // httpPost.setHeader("Accept", "application/json");
                // httpPost.setHeader("Content-type", "application/json");
                //데이터보낸 뒤 서버에서 데이터를 받아오는 과정
                ResponseHandler<String> reshand = new BasicResponseHandler();

                HttpResponse response = client.execute(httpPost);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                String line = null;
                String result = "";

                while ((line = bufferReader.readLine()) != null) {
                    result += line;

                }
            } else {

            }
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