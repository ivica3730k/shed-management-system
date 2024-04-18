package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;


public class SeeMyItemsActivity extends AppCompatActivity {
    DatabaseConnection database;
    LoginPreferences loginPreferences;
    ListView listView;
    Intent myIntent;
    TextView headline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.database = new DatabaseConnection(this);
        setContentView(R.layout.activity_see_my_items);
        this.loginPreferences = new LoginPreferences(getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        this.listView = findViewById(R.id.listview);
        this.headline = findViewById(R.id.headline);
        this.myIntent = getIntent();
        Bundle extras = myIntent.getExtras();
        if (extras != null) {
            if (extras.containsKey("USERNAME")) {
                String userName = this.myIntent.getStringExtra("USERNAME");
                headline.setText(userName);
                if (userName.equals(loginPreferences.getUserName())) {
                    displayTable();
                } else {
                    displayTable(userName);
                }
            }
        } else {
            displayTable();
        }

    }

    @Override
    protected void onPostResume() {
        //Log.d("flw", "Called onPostResume");
        this.listView.setAdapter(null);
        this.database = new DatabaseConnection(this);
        super.onPostResume();
        Bundle extras = myIntent.getExtras();
        if (extras != null) {
            if (extras.containsKey("USERNAME")) {
                String userName = this.myIntent.getStringExtra("USERNAME");
                headline.setText(userName);
                if (userName.equals(loginPreferences.getUserName())) {
                    displayTable();
                } else {
                    displayTable(userName);
                }
            }
        } else {
            displayTable();
        }
    }


    private void displayTable() {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> a : database.getUserBorrowedItems(this.loginPreferences.getUserName())) {
            if (!list.contains(a)) {
                list.add(a);
            }
        }
        class Adapter extends SeeMyItemsActivityDisplayAdapter {
            public Adapter(ArrayList<Map<String, Object>> list, Context context) {
                super(list, context);
            }

        }
        Adapter adapter = new Adapter(list, SeeMyItemsActivity.this);
        this.listView.setAdapter(adapter);
    }

    private void displayTable(String userNameToLookup) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> a : database.getUserBorrowedItems(userNameToLookup)) {
            if (!list.contains(a)) {
                list.add(a);
            }
        }
        class Adapter extends SeeMyItemsActivityDisplayAdapter {
            public Adapter(ArrayList<Map<String, Object>> list, Context context) {
                super(list, context, false, userNameToLookup);
            }


        }
        Adapter adapter = new Adapter(list, SeeMyItemsActivity.this);
        this.listView.setAdapter(adapter);
    }

}