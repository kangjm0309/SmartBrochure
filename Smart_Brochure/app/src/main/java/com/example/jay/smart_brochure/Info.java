package com.example.jay.smart_brochure;
/**
 * info 탭
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;


public class Info extends Activity {

    ArrayList<String> my_Array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        my_Array = new ArrayList<String>();
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
