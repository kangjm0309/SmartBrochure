package com.example.jay.smart_brochure;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class MyClicked extends Activity {

    ArrayList<String> my_Array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_clicked);
        my_Array = new ArrayList<String>();
        String[] spec = {"Name_Text","Text"};
        my_Array.add("작품명1");
        my_Array.add("작품명2");
        my_Array.add("작품명3");
        customAdapter myAdap = new customAdapter(this, my_Array);
        ListView my_list = (ListView) findViewById(R.id.myclicked_list);

        my_list.setAdapter(myAdap);
        my_list.setOnItemClickListener(mItemClickListener);
        myAdap.notifyDataSetChanged();

        ImageView Image = (ImageView)findViewById(R.id.mc_Image);
        TextView content1 = (TextView)findViewById(R.id.mc_first);
        TextView content2 = (TextView)findViewById(R.id.mc_second);

        Image.setImageResource(R.drawable.ic_launcher);
        content1.setText(spec[0]);
        content2.setText(spec[1]);
    }

    class customAdapter extends ArrayAdapter {
        private Context context;
        private ArrayList<String> arry;

        public customAdapter(Context context, ArrayList<String> arry) {

            super(context, R.layout.myclicked_layout);

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

            convertView = li.inflate(R.layout.myclicked_layout, null);

            TextView content = (TextView)convertView.findViewById(R.id.work_name);
            content.setText(arry.get(position));

            return convertView;
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

            Intent i = new Intent(MyClicked.this, Explanation.class);
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
