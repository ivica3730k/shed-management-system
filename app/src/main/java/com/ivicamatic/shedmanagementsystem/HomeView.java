package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class HomeView extends AppCompatActivity {
    LoginPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.loginPreferences = new LoginPreferences(getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        setContentView(R.layout.activity_home_view);
    }

    public void onTakeItemButtonClicked(View view) {
        Intent takeItemIntent = new Intent(this, TakeItemActivity.class);
        startActivity(takeItemIntent);
    }

    public void onReturnItemButtonClicked(View view) {
        Intent returnItemIntent = new Intent(this, ReturnItemActivity.class);
        startActivity(returnItemIntent);
    }

    public void onMyItemsButtonClicked(View view) {
        Intent seeMyItemsIntent = new Intent(this, SeeMyItemsActivity.class);
        startActivity(seeMyItemsIntent);
    }


    public void onLogoutButtonClicked(View view) {
        SharedPreferences loginStorageSharedPreferences = getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginStorageSharedPreferences.edit();
        editor.remove("USERPASSWORD");
        editor.remove("HAS_PASSWORD");
        editor.apply();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);

    }

    public void onAddOrganiserButtonClicked(View view) {
        Intent addOrganiserScreen = new Intent(this, AddOrganiserActivity.class);
        startActivity(addOrganiserScreen);
    }

    public void onSeeStockButtonClicked(View view) {
        Intent seeStockScreen = new Intent(this, SeeStockActivity.class);
        startActivity(seeStockScreen);
    }
}