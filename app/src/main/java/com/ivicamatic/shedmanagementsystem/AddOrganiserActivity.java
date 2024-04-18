package com.ivicamatic.shedmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;

public class AddOrganiserActivity extends AppCompatActivity {
    DatabaseConnection database;// = new DatabaseConnection(this);
    EditText itemName;
    EditText containerId;
    EditText startingQty;
    Button scanContainerIdButton;
    Button addOrganiserButton;
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.database = new DatabaseConnection(this);
        setContentView(R.layout.activity_add_organiser);
        this.itemName = findViewById(R.id.itemName);
        this.containerId = findViewById(R.id.containerId);
        this.scanContainerIdButton = findViewById(R.id.scanContainerIdButton);
        this.startingQty = findViewById(R.id.startingQuantity);
        this.myIntent = getIntent();
        this.addOrganiserButton = findViewById(R.id.addOrganiserButton);
        this.addOrganiserButton.setEnabled(false);
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

                Button targetButton = addOrganiserButton;
                ArrayList<EditText> watchItems = new ArrayList<EditText>(Arrays.asList(itemName, containerId, startingQty));

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
        this.itemName.addTextChangedListener(any_field_changed);
        this.containerId.addTextChangedListener(any_field_changed);
        this.startingQty.addTextChangedListener(any_field_changed);
    }


    public void onScanContainerIdButtonClicked(View view) {
        Intent qr_nfc_scan = new Intent(this, NfcQrSelector.class);
        startActivityForResult(qr_nfc_scan, 100);
    }

    public void onAddOrganiserButtonClicked(View view) {
        String itemName = this.itemName.getText().toString();
        String containerId = this.containerId.getText().toString();
        int startingQuantity = Integer.parseInt(this.startingQty.getText().toString());
        this.database.addOrganiser(itemName, containerId, startingQuantity);
        Toast.makeText(this, "Item Added Successfully", Toast.LENGTH_LONG).show();
        setResult(RESULT_OK, this.myIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                this.containerId.setText(data.getStringExtra("ID"));
            }
        }
    }

}

