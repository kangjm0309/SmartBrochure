package com.example.jay.smart_brochure;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Explanation extends Activity {

    Transfer transfer = new Transfer();
    JSONObject jaResData;

    ImageView image;
    TextView name;
    TextView spec;
    ArrayList<String> works;

    private static String url = "http://jung2.maden.kr/beacon_gateway/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_explanation);
        works = new ArrayList<String>();
        Intent g = getIntent();
        HashMap<String, Object> mapReqData = new HashMap<String, Object>();
        mapReqData.put("_pcCd", g.getStringExtra("exp"));
        jaResData = transfer.transData("LC0003", mapReqData);

        image = (ImageView) findViewById(R.id.exp_image);
        name = (TextView) findViewById(R.id.exp_name);
        spec = (TextView) findViewById(R.id.exp_spec);

        // 서버에서 받아서 이미지 및 텍스트 세팅
        setExplanation();

    }

    private void setExplanation() {
        try {
            JSONArray jsonArray = (JSONArray) jaResData.get("res_data");
                JSONObject worksList = (JSONObject) jsonArray.get(0);
                String a = worksList.get("_full_img").toString();
                String b = (String)worksList.get("_pc_nm");
                String c = (String)worksList.get("_pc_cont");

            works.add(a);
            works.add(b);
            works.add(c);


            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory()
                    .cacheOnDisc()
                    .build();


            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
//			.writeDebugLogs() // Remove for release app
                    .build();

            ImageLoader.getInstance().init(config);
            ImageLoader.getInstance().displayImage(works.get(0) , image, options);

            name.setText(works.get(1));
            spec.setText(works.get(2));

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_explanation, menu);
        return true;
    }

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
}
