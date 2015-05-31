package com.example.jay.smart_brochure;
/**
 * info 탭
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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


public class Info extends Activity {

    ArrayList<String> my_Array;
    ArrayList<String> code_Ar;


    private static String url = "http://jung2.maden.kr/beacon_gateway/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Transfer transfer = new Transfer();
        HashMap<String, Object> mapReqData = new HashMap<String, Object>();
//        mapReqData.put("_pcCd", g.getStringExtra("exp"));
        JSONObject jaResData = transfer.transData("LC0001", mapReqData);


        my_Array = new ArrayList<String>();
        code_Ar = new ArrayList<String>();
        try {
            sendId();
        }catch(Exception e) {
            e.printStackTrace();
        }

        customAdapter myAdap = new customAdapter(this, my_Array);
        ListView my_list = (ListView) findViewById(R.id.info_list);

        my_list.setAdapter(myAdap);
        my_list.setOnItemClickListener(mItemClickListener);
        myAdap.notifyDataSetChanged();
    }
    class customAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String> arry;

        public customAdapter(Context context, ArrayList<String> arry) {

            super(context, R.layout.info_layout);

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

            convertView = li.inflate(R.layout.info_layout, null);

            TextView content = (TextView) convertView.findViewById(R.id.info_tv);

            content.setText(arry.get(position));

            return convertView;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Intent i = new Intent(Info.this, Info_Clicked.class);
            i.putExtra("info_code", code_Ar.get(position));
            startActivity(i);
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


    private void sendId() throws IOException, JSONException {

        JSONObject dataObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray jArrayBeacon = new JSONArray();
        dataObject.put("req_data", jsonArray);
        dataObject.put("req_svc", "LC0001");

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
                Log.d("임주현", "jaResData : " + jaResData.toString());
                for(int i = 0; i < ((JSONArray) jaResData.get("_list")).length(); i++) {
                    JSONObject jaRRE = (JSONObject) ((JSONArray) jaResData.get("_list")).get(i);
                    my_Array.add((String) jaRRE.get("_eh_nm"));
                    code_Ar.add((String) jaRRE.get("_eh_code"));
                }

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
