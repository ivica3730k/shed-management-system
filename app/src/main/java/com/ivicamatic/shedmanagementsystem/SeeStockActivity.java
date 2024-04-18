package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Map;


public class SeeStockActivity extends AppCompatActivity {
    DatabaseConnection database;
    LoginPreferences loginPreferences;
    ListView listView;
    Button addOrganiserButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.database = new DatabaseConnection(this);
        this.loginPreferences = new LoginPreferences(getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        boolean isAdmin = database.isUserAdmin(loginPreferences.getUserName());
        setContentView(R.layout.activity_see_stock);
        this.addOrganiserButton = findViewById(R.id.addOrganiserButton);
        if (!isAdmin) {
            this.addOrganiserButton.setEnabled(false);
            this.addOrganiserButton.setVisibility(View.INVISIBLE);
        }
        this.listView = findViewById(R.id.listview);
        displayTable();
    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
        displayTable();
    }


    private void displayTable() {
        ArrayList<Object> list = new ArrayList<Object>();
        for (Map<String, Object> a : database.getAllItemsInStock()) {
            list.add(a);
        }
        class Adapter extends SeeStockActivityDisplayAdapter {
            public Adapter(ArrayList<Object> list, Context context) {
                super(list, context);
            }

            @Override
            public void somethingChanged() {
                displayTable();
            }
        }
        Adapter adapter = new Adapter(list, SeeStockActivity.this);
        this.listView.setAdapter(adapter);
    }

    public void onAddOrganiserButtonClicked(View view) {
        Intent addOrganiserScreen = new Intent(this, AddOrganiserActivity.class);
        startActivity(addOrganiserScreen);
    }

}