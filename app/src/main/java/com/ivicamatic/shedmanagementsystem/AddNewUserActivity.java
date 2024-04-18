package com.ivicamatic.shedmanagementsystem;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddNewUserActivity extends AppCompatActivity {
    EditText userName;
    EditText userPassword;
    CheckBox userIsAdministrator;
    Button addUserButton;
    DatabaseConnection database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);
        this.addUserButton = findViewById(R.id.addOrganiserButton);
        this.addUserButton.setEnabled(false);
        this.database = new DatabaseConnection(this);
        this.userName = findViewById(R.id.userName);
        this.userPassword = findViewById(R.id.userPassword);
        this.userPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        this.userIsAdministrator = findViewById(R.id.userIsAdministrator);
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
                if (!userName.getText().toString().equals("")) {
                    addUserButton.setEnabled(!userPassword.getText().toString().equals(""));
                } else {
                    addUserButton.setEnabled(false);
                }
            }
        };
        this.userName.addTextChangedListener(any_field_changed);
        this.userPassword.addTextChangedListener(any_field_changed);
    }


    public void onAddUserButtonClicked(View view) {
        this.database.addUser(this.userName.getText().toString(), this.userPassword.getText().toString(), this.userIsAdministrator.isChecked());
        Toast.makeText(this, "User Added Successfully", Toast.LENGTH_LONG).show();
        finish();
    }

}