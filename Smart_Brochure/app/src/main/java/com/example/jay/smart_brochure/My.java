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
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *  "My" 탭 구성
 */

public class My extends Activity {

    ArrayList<String> my_Array;
    ArrayList<String[]> test_Array;
    Database data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        String[] test = {"ID", "ADDRESS", "NAME"};
        test_Array = new ArrayList<String[]>();
        test_Array.add(test);
        data = new Database(this);
        my_Array = new ArrayList<String>();
        data.open();
        test_Array = data.getTestHistory();
        my_Array = data.getHistory();
        data.close();
       customAdapter myAdap = new customAdapter(this, my_Array);
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

            Intent i = new Intent(My.this, MyClicked.class);
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
