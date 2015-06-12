package com.example.jay.smart_brochure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

/**
 * Created by Jay on 2015-05-31.
 */
public class My_Clicked extends Activity {


    String exh_img = "";
    String exh_title = "";
    String exh_code = "";
    ArrayList<String[]> exh_Array = new ArrayList<String[]>();

    String address = "";

    customAdapter myAdap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_clicked);

        Intent i = getIntent();
        address = i.getStringExtra("address");
        exh_code = i.getStringExtra("exh_cd");
        HashMap<String, Object> mapReqData = new HashMap<String, Object>();
        mapReqData.put("_exCd",exh_code);
        Log.d("my_clicked", "Code:: "+ exh_code);

        Transfer transfer = new Transfer();
        JSONObject jaResData = transfer.transData("LC0002", mapReqData);

        JSONArray jsonArray = new JSONArray();

        try {
            jsonArray = (JSONArray) jaResData.get("_pc_list");


            for(int p = 0 ; p < jsonArray.length() ; p++){
                JSONObject worksList = (JSONObject) jsonArray.get(p);
                String a = worksList.get("_thum_img").toString();
                String b = (String)worksList.get("_pc_nm");
                String c = (String)worksList.get("_pc_code");

                String[] works = {a, b, c};
                exh_Array.add(works);
            }

            exh_img = (String) ((JSONObject) jaResData.get("_ex_inform")).get("_full_img");
            exh_title = (String)((JSONObject)jaResData.get("_ex_inform")).get("_eh_nm");


            Log.d("jsonData", "imgUrl::" + exh_img);
            Log.d("jsonData", "_eh_nm::"+exh_title);
            Log.d("jsonData", "_thum_img::" + exh_Array.get(0)[0]);
            Log.d("jsonData", "_pc_nm::" + exh_Array.get(0)[1]);
        } catch(Exception e) {
            e.printStackTrace();
        }


        myAdap = new customAdapter(this, exh_Array);
        ListView my_list = (ListView) findViewById(R.id.pushclicked_list);

        my_list.setAdapter(myAdap);
        my_list.setOnItemClickListener(mItemClickListener);
        myAdap.notifyDataSetChanged();
        TextView name = (TextView)findViewById(R.id.pc_name);
        Typeface rb = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Bold.ttf");
        name.setTypeface(rb);
        name.setText(exh_title);

    }

    class customAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String[]> arry;

        public customAdapter(Context context, ArrayList<String[]> arry) {

            super(context, R.layout.pushclicked_layout);

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

            convertView = li.inflate(R.layout.pushclicked_layout, null);

            TextView workname = (TextView)convertView.findViewById(R.id.work_name);
            ImageView workImage = (ImageView)convertView.findViewById(R.id.work_image);

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory()
                    .cacheOnDisc()
                    .build();


            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(My_Clicked.this)
                    .threadPriority(Thread.NORM_PRIORITY - 2)
                    .denyCacheImageMultipleSizesInMemory()
                    .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                    .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                    .tasksProcessingOrder(QueueProcessingType.LIFO)
//			.writeDebugLogs() // Remove for release app
                    .build();

            ImageLoader.getInstance().init(config);
            ImageLoader.getInstance().displayImage(arry.get(position)[0] , workImage, options);
            Typeface nanum = Typeface.createFromAsset(getAssets(),
                    "fonts/NanumBarunGothic.ttf");
            workname.setTypeface(nanum);
            workname.setText(arry.get(position)[1]);
            myAdap.notifyDataSetChanged();

            return convertView;
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
        getMenuInflater().inflate(R.menu.menu_my_clicked, menu);
        return true;
    }

    AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Intent i = new Intent(My_Clicked.this, Explanation.class);
            Log.d("agsaflkasnvasf", "_pcCd" + exh_Array.get(position)[2]);
            i.putExtra("exp", exh_Array.get(position)[2]);
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
}
