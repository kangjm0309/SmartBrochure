package com.example.jay.smart_brochure;
//Info 탭에서 리스트 중 하나 클릭 했을 떄 페이지

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Info_Clicked extends Activity {

    Transfer transfer = new Transfer();
    JSONObject jaResData;


    ArrayList<String> information;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info__clicked);

        Intent g = getIntent();
        HashMap<String, Object> mapReqData = new HashMap<String, Object>();
        mapReqData.put("_exCd", g.getStringExtra("info_code"));
        jaResData = transfer.transData("LC0004", mapReqData);

        information = new ArrayList<String>();
        setInformation();

    }

    private void setInformation() {

        TextView info_title = (TextView) findViewById(R.id.info_title);
        ImageView info_img = (ImageView) findViewById(R.id.info_img);
        TextView info_expl = (TextView) findViewById(R.id.info_expl);
        TextView info_date = (TextView) findViewById(R.id.info_date);
        TextView info_address = (TextView) findViewById(R.id.info_address);

        try{

            String a = jaResData.get("_eh_nm").toString();
            String b = jaResData.get("_full_img").toString();
            String c = jaResData.get("_eh_cont").toString();
            String d = jaResData.get("_eh_date").toString();
            String e = jaResData.get("_eh_address").toString();


            information.add(a);
            information.add(b);
            information.add(c);
            information.add(d);
            information.add(e);


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
            ImageLoader.getInstance().displayImage(information.get(1) , info_img, options);

            info_title.setText(information.get(0));
            info_expl.setText(information.get(2));
            info_date.setText(information.get(3));
            info_address.setText(information.get(4));

            Typeface rb = Typeface.createFromAsset(getAssets(),
                    "fonts/Roboto-Bold.ttf");
            Typeface nanum = Typeface.createFromAsset(getAssets(),
                    "fonts/NanumBarunGothic.ttf");
            info_title.setTypeface(rb);
            info_expl.setTypeface(nanum);
            info_date.setTypeface(nanum);
            info_address.setTypeface(nanum);



        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void mOnClick(View v) {
        switch (v.getId()) {
            case R.id.clicked_back1:
                onBackPressed();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info__clicked, menu);
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
