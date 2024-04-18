package com.ivicamatic.shedmanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class NfcQrSelector extends AppCompatActivity {
    Intent myIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_qr_selector);
        this.myIntent = getIntent();

    }

    public void onScanNfcButtonClicked(View view) {
        Intent nfcReadIntent = new Intent(this, ScanNfcActivity.class);
        startActivityForResult(nfcReadIntent, 100);
    }

    public void onScanQrCodeButtonClicked(View view) {
        Intent nfcReadIntent = new Intent(this, ScanQrActivity.class);
        startActivityForResult(nfcReadIntent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                this.myIntent.putExtra("ID", data.getStringExtra("ID_HEX").replaceAll("\\s+", ""));
                setResult(RESULT_OK, this.myIntent);
                finish();
            }
        }
        if (requestCode == 101) {
            if (resultCode == RESULT_OK) {
                this.myIntent.putExtra("ID", data.getStringExtra("QR_TEXT").replaceAll("\\s+", ""));
                setResult(RESULT_OK, this.myIntent);
                finish();
            }
        }
    }

}