package com.example.jay.smart_brochure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

/**
 *  "My" 탭 구성
 */

public class My extends Activity {

    ArrayList<String> name;
    ArrayList<String> address;
    ArrayList<String> code;
    Database data;
    customAdapter myAdap;

    private final String LC0000 = "LC0000";
    private static String url = "http://jung2.maden.kr/beacon_gateway/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        TextView title = (TextView)findViewById(R.id.my_title);
        Typeface rb = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Bold.ttf");
        title.setTypeface(rb);
        data = new Database(this);
        name = new ArrayList<String>();
        address = new ArrayList<String>();
        code = new ArrayList<String>();

        data.open();
        name = data.getHistoryName();
        address = data.getHistoryAddress();
        code = data.getHistoryCode();
        data.close();
        myAdap = new customAdapter(this, name);
        ListView my_list = (ListView) findViewById(R.id.my_list);

        my_list.setAdapter(myAdap);
        my_list.setOnItemClickListener(mItemClickListener);
        myAdap.notifyDataSetChanged();
    }

    class customAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String> arry;

        public customAdapter(Context context, ArrayList<String> arry) {

            super(context, R.layout.my_layout);

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

            convertView = li.inflate(R.layout.my_layout, null);

            TextView content = (TextView) convertView.findViewById(R.id.my_tv);
            TextView where = (TextView) convertView.findViewById(R.id.my_where);
            TextView when = (TextView) convertView.findViewById(R.id.my_when);
            Typeface nb = Typeface.createFromAsset(getAssets(),
                    "fonts/NanumBarunGothicBold.ttf");
            content.setTypeface(nb);
            where.setTypeface(nb);
            when.setTypeface(nb);
            content.setText(arry.get(position));

            return convertView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
/*            try {
                sendId(position);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e1) {
                e1.printStackTrace();
            }*/

            Intent intent = new Intent(My.this, My_Clicked.class);
            intent.putExtra("exh_cd", code.get(position).toString());
            Log.d("address", "Code :: " + code.get(position).toString());
            intent.putExtra("address", address.get(position).toString());
            Log.d("address", "address :: " + address.get(position).toString());
            startActivity(intent);
            overridePendingTransition(0, 0);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void sendId(int position) throws IOException, JSONException {
        if (address.size() != 0) {
            String id = address.get(position).toString();
        }

        HashMap<String, Object> mapReqData = new HashMap<String, Object>();
        mapReqData.put("beacon_id", address.get(position).toString());

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
                Log.d("response", "Code::" + jaRRE.get("_cd"));



 /*               PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

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


                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, b.build());*/



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
    protected void onResume() {
        super.onResume();
        myAdap.notifyDataSetChanged();
    }
}
