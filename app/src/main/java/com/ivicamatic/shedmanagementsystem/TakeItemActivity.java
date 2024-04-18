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


public class TakeItemActivity extends AppCompatActivity {
    DatabaseConnection database;// = new DatabaseConnection(this);
    LoginPreferences loginPreferences;
    TextView itemNameLabel;
    TextView inStockCountLabel;
    Button takeItemButton;
    Button scanContainerButton;
    Intent myIntent;
    EditText takeQuantity;
    boolean containerScanned = false;
    Map<String, Object> containerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.database = new DatabaseConnection(this);
        setContentView(R.layout.activity_take_item);
        this.loginPreferences = new LoginPreferences(getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        this.takeItemButton = findViewById(R.id.addOrganiserButton);
        this.takeItemButton.setEnabled(false);
        this.itemNameLabel = findViewById(R.id.itemNameLabel);
        this.inStockCountLabel = findViewById(R.id.countLabel);
        this.scanContainerButton = findViewById(R.id.scanContainerIdButton);
        this.takeQuantity = findViewById(R.id.quantity);
        this.myIntent = getIntent();
        this.scanContainerForData();

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

                Button targetButton = takeItemButton;
                ArrayList<EditText> watchItems = new ArrayList<EditText>(Arrays.asList(takeQuantity));

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
        this.takeQuantity.addTextChangedListener(any_field_changed);
    }


    public void onTakeItemClicked(View view) {
        this.database.userBorrowItem(loginPreferences.getUserName(), (String) this.containerData.get("containerId"), Integer.parseInt(String.valueOf(this.takeQuantity.getText())));
        Toast.makeText(this, "Item taken successfully!", Toast.LENGTH_LONG).show();
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
                if (!database.isItemInStock(containerId)) {
                    Toast.makeText(this, "Not in stock!",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    this.containerData = database.getItemDetails(containerId);
                    String itemName = (String) this.containerData.get("itemName");
                    this.itemNameLabel.setText(itemName);
                    String inStock = "In Stock: ";
                    long quantity;
                    quantity = (long) this.containerData.get("quantity");
                    inStock += quantity;
                    this.inStockCountLabel.setText(inStock);
                    this.containerScanned = true;
                    this.takeQuantity.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(quantity))});
                    //this.takeItemButton.setEnabled(true);
                }
            } else {
                finish();
            }
        }
    }

}