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

public class ManageUsersActivity extends AppCompatActivity {
    DatabaseConnection database;
    Button addUserButton;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);
        this.database = new DatabaseConnection(this);
        this.addUserButton = findViewById(R.id.addOrganiserButton);
        displayTable();
    }


    @Override
    protected void onPostResume() {

        super.onPostResume();
        displayTable();
    }

    public void onAddUserButtonClicked(View view) {
        Intent addUserIntent = new Intent(this, AddNewUserActivity.class);
        startActivity(addUserIntent);
    }

    private void displayTable() {
        this.listView = findViewById(R.id.listview);
        ArrayList<Object> list = new ArrayList<Object>();
        for (Map<String, Object> a : database.getAllUsers()) {
            list.add(a);
        }
        class Adapter extends ManageUsersActivityDisplayAdapter {
            public Adapter(ArrayList<Object> list, Context context) {
                super(list, context);
            }

            @Override
            public void somethingChanged() {
                displayTable();
            }
        }
        Adapter adapter = new Adapter(list, ManageUsersActivity.this);
        this.listView.setAdapter(adapter);
    }
}