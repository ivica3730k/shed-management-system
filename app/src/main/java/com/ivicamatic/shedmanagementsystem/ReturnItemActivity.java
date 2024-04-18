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


public class ReturnItemActivity extends AppCompatActivity {
    DatabaseConnection database;// = new DatabaseConnection(this);
    Map<String, Object> itemDetails;
    LoginPreferences loginPreferences;
    TextView itemNameLabel;
    TextView borrowedCountLabel;
    Button returnItemButton;
    Button scanContainerButton;
    Intent myIntent;
    EditText returnQuantity;
    boolean containerScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.database = new DatabaseConnection(this);
        setContentView(R.layout.activity_return_item);
        this.loginPreferences = new LoginPreferences(getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        this.returnItemButton = findViewById(R.id.addOrganiserButton);
        this.returnItemButton.setEnabled(false);
        this.itemNameLabel = findViewById(R.id.itemNameLabel);
        this.borrowedCountLabel = findViewById(R.id.countLabel);
        this.scanContainerButton = findViewById(R.id.scanContainerIdButton);
        this.returnQuantity = findViewById(R.id.quantity);
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

                Button targetButton = returnItemButton;
                ArrayList<EditText> watchItems = new ArrayList<EditText>(Arrays.asList(returnQuantity));

                for (EditText i : watchItems) {
                    if (i.getText().toString().equals("")) {
                        targetButton.setEnabled(false);
                        break;
                    } else {
                        targetButton.setEnabled(true);
                    }
                }
                //
            }
        };
        this.returnQuantity.addTextChangedListener(any_field_changed);
        Bundle extras = myIntent.getExtras();
        if (extras != null) {
            if (extras.containsKey("CONTAINER_ID")) {
                String containerId = this.myIntent.getStringExtra("CONTAINER_ID");
                if (!database.userHasItemBorrowed(loginPreferences.getUserName(), containerId)) {
                    Toast.makeText(this, "You don't have this item in your possession", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    this.itemDetails = database.getItemDetails(containerId);
                    int count = database.getUserBorrowedItemCount(this.loginPreferences.getUserName(), containerId);
                    this.itemNameLabel.setText((String) this.itemDetails.get("itemName"));
                    // this.returnItemButton.setEnabled(true);
                    this.returnQuantity.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(count))});
                    this.containerScanned = true;
                    String inStock = "Borrowed: ";
                    inStock += count;
                    this.borrowedCountLabel.setText(inStock);
                }
            }
        } else {
            this.scanContainerForData();
        }
    }

    public void onReturnItemClicked(View view) {
        this.database.userReturnItem(this.loginPreferences.getUserName(), (String) this.itemDetails.get("containerId"), Integer.parseInt(String.valueOf(this.returnQuantity.getText())));
        Toast.makeText(this, "Item returned successfully", Toast.LENGTH_LONG).show();
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
                if (!database.userHasItemBorrowed(loginPreferences.getUserName(), containerId)) {
                    Toast.makeText(this, "You don't have this item in your possession", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    this.itemDetails = database.getItemDetails(containerId);
                    int count = database.getUserBorrowedItemCount(this.loginPreferences.getUserName(), containerId);
                    this.itemNameLabel.setText((String) this.itemDetails.get("itemName"));
                    // this.returnItemButton.setEnabled(true);
                    this.returnQuantity.setFilters(new InputFilter[]{new InputFilterMinMax("1", String.valueOf(count))});
                    this.containerScanned = true;
                    String inStock = "Borrowed: ";
                    inStock += count;
                    this.borrowedCountLabel.setText(inStock);
                }
            } else {
                finish();
            }
        }
    }

}