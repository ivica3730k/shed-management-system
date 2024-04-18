package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    CheckBox rememberMeCheckBox;
    EditText userName;
    EditText userPassword;
    DatabaseConnection database; // = new DatabaseConnection(this);
    LoginPreferences loginPreferences;

    /**
     * OnCreate function for the Login Screen
     * <p>
     * Once the Login screen is created populate the username field with the username from the
     * shared preferences. Also, obtain if the user that last logged in clicked remember my password
     * checkbox. If that is the case, obtain the username and user password from the Shared preferences
     * and log-in automatically.
     *
     * @param savedInstanceState _
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.database = new DatabaseConnection(this);
        this.loginPreferences = new LoginPreferences(getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        if (loginPreferences.isPasswordStored()) {
            if (database.authenticateUser(this.loginPreferences.getUserName(), this.loginPreferences.getUserPassword())) {
                this.redirectToHomeView();
                finish();

            }
        }
        setContentView(R.layout.activity_login);
        this.userName = findViewById(R.id.userName);
        this.userPassword = findViewById(R.id.userPassword);
        this.rememberMeCheckBox = findViewById(R.id.rememberPasswordCheckbox);
        this.userName.setText(this.loginPreferences.getUserName());


    }

    @Override
    protected void onPostResume() {

        super.onPostResume();
        SharedPreferences loginStorageSharedPreferences = getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = loginStorageSharedPreferences.edit();
        editor.remove("USERPASSWORD");
        editor.remove("HAS_PASSWORD");
        editor.apply();
    }

    /**
     * Login button callback function
     * <p>
     * Function runs once user click on login button on the login screen.
     * RUns the username/ userpassword combo validation and redirects to the appropriate
     * user screen
     *
     * @param view View
     */
    public void onLoginButtonClicked(View view) {
        String userName = this.userName.getText().toString();
        String userPassword = this.userPassword.getText().toString();
        if (database.authenticateUser(userName, userPassword)) {
            this.loginPreferences.saveUserName(userName);
            this.loginPreferences.saveUserId(this.database.getUserIdFromUsername(userName));
            //Log.d("userid", loginPreferences.getUserId());
            if (this.rememberMeCheckBox.isChecked()) {
                this.loginPreferences.saveUserPassword(userPassword);
            }
            if (database.isUserAdmin(userName)) {
                this.redirectToAdminView();
            } else {
                this.redirectToHomeView();
            }
        } else {
            Toast.makeText(this, "Login Failed!",
                    Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Redirects to the Home View
     */
    private void redirectToHomeView() {
        Intent homeView = new Intent(this, HomeView.class);
        startActivity(homeView);
    }

    private void redirectToAdminView() {
        Intent adminView = new Intent(this, AdminHomeView.class);
        startActivity(adminView);
    }


}