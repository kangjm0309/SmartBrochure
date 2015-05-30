package com.example.jay.smart_brochure;

import android.os.StrictMode;
import android.util.Log;

import org.apache.http.NameValuePair;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Jay on 2015-05-30.
 */
public class Transfer {
    private static String url="http://jung2.maden.kr/beacon_gateway/";

    public JSONObject transData(String svcCd, HashMap<String, Object> mapReqData){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String strJsonData = "";
        JSONObject reqData = new JSONObject();
        JSONArray reqArrayData = new JSONArray();
        try {
            reqArrayData.put(new JSONObject(mapReqData));
            reqData.put("req_data", reqArrayData);
            reqData.put("req_svc", svcCd);

            strJsonData = reqData.toString();

            Log.d("jsonData", strJsonData);

            ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
            paramList.add(new BasicNameValuePair("JSONData", strJsonData));

            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));

            DefaultHttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> reshand = new BasicResponseHandler();
            String strResponseBody = httpclient.execute(httpPost, reshand);
            String strResltJsonData = strResponseBody.trim();

            JSONObject joResData = new JSONObject(strResltJsonData);
            JSONObject jaRRE = (JSONObject) ((JSONArray) joResData.get("res_data")).get(0);

            Log.d("jsonData", "jaRRE::"+jaRRE.toString());
            return jaRRE;
        } catch (Exception ex) {
            return null;
        }


    }
}
