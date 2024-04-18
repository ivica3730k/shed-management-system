package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class StocktakeActivity extends AppCompatActivity {

    DatabaseConnection database;
    Map<String, Object> itemDetails;
    LoginPreferences loginPreferences;
    TextView itemNameLabel;
    TextView countLabel;
    Button adjustQuantityButton;
    Button scanContainerButton;
    Intent myIntent;
    EditText quantity;
    boolean containerScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stocktake);
        this.database = new DatabaseConnection(this);
        this.loginPreferences = new LoginPreferences(getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        this.adjustQuantityButton = findViewById(R.id.adjustQuantityButton);
        this.adjustQuantityButton.setEnabled(false);
        this.itemNameLabel = findViewById(R.id.itemNameLabel);
        this.countLabel = findViewById(R.id.countLabel);
        this.scanContainerButton = findViewById(R.id.scanContainerIdButton);
        this.quantity = findViewById(R.id.quantity);
        this.myIntent = getIntent();
        TextWatcher any_field_changed = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                Button targetButton = adjustQuantityButton;
                ArrayList<EditText> watchItems = new ArrayList<EditText>(Arrays.asList(quantity));

                for (EditText i : watchItems) {
                    if (i.getText().toString().equals("")) {
                        targetButton.setEnabled(false);
                        break;
                    } else {
                        targetButton.setEnabled(true);
                    }
                }

            }
        };
        this.quantity.addTextChangedListener(any_field_changed);
        Bundle extras = myIntent.getExtras();
        if (extras != null) {
            if (extras.containsKey("CONTAINER_ID")) {
                String containerId = this.myIntent.getStringExtra("CONTAINER_ID");
                if (database.isItemStocked(containerId)) {
                    this.itemDetails = database.getItemDetails(containerId);
                    long count = (long) itemDetails.get("quantity");
                    this.itemNameLabel.setText((String) this.itemDetails.get("itemName"));
                    //this.adjustQuantityButton.setEnabled(true);
                    this.quantity.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(Integer.MAX_VALUE))});
                    this.containerScanned = true;
                    String inStock = "On count: ";
                    inStock += count;
                    this.countLabel.setText(inStock);
                } else {
                    Toast.makeText(this, "This item is not stocked on the system", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } else {
            this.scanContainerForData();
        }
    }

    public void onAdjustButtonClicked(View view) {
        this.database.adjustStock((String) this.itemDetails.get("containerId"), Integer.parseInt(String.valueOf(this.quantity.getText())));
        finish();
    }

    public void onScanContainerButtonClicked(View view) {
        this.scanContainerForData();
    }

    public void scanContainerForData() {
        Intent qr_nfc_scan = new Intent(this, NfcQrSelector.class);
        startActivityForResult(qr_nfc_scan, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                String containerId = (data.getStringExtra("ID"));
                if (database.isItemStocked(containerId)) {
                    this.itemDetails = database.getItemDetails(containerId);
                    long count = (long) itemDetails.get("quantity");
                    this.itemNameLabel.setText((String) this.itemDetails.get("itemName"));
                    //this.adjustQuantityButton.setEnabled(true);
                    this.quantity.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(Integer.MAX_VALUE))});
                    this.containerScanned = true;
                    String inStock = "On count: ";
                    inStock += count;
                    this.countLabel.setText(inStock);
                } else {
                    Toast.makeText(this, "This item is not stocked on the system", Toast.LENGTH_LONG).show();
                    finish();
                }
            } else {
                finish();
            }
        }
    }

}