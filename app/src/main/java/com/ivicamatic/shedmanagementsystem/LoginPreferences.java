package com.ivicamatic.shedmanagementsystem;

import android.content.SharedPreferences;

public class LoginPreferences {
    SharedPreferences loginStorageSharedPreferences;

    LoginPreferences(SharedPreferences p) {
        this.loginStorageSharedPreferences = p;
    }

    public boolean isPasswordStored() {
        return this.loginStorageSharedPreferences.getBoolean("HAS_PASSWORD", false);
    }

    /**
     * Stores username in Shared Preferences
     *
     * @param userName Username in string format
     */
    public void saveUserName(String userName) {
        SharedPreferences.Editor editor = this.loginStorageSharedPreferences.edit();
        editor.putString("USERNAME", userName);
        editor.apply();
    }

    /**
     * Stores user password in Shared Preferences
     *
     * @param userPassword User password in string format
     */
    public void saveUserPassword(String userPassword) {
        SharedPreferences.Editor editor = this.loginStorageSharedPreferences.edit();
        editor.putString("USERPASSWORD", userPassword);
        editor.putBoolean("HAS_PASSWORD", true);
        editor.apply();
    }

    /**
     * Stores the user id in Shared Preferences
     *
     * @param userId User id to save
     */
    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = this.loginStorageSharedPreferences.edit();
        editor.putString("USERID", userId);
        editor.apply();
    }


    /**
     * Gets the username from the Shared Preferences
     * <p>
     * If the username is not set in the shared preferences returns empty string ""
     *
     * @return username in string format
     */
    public String getUserName() {
        return this.loginStorageSharedPreferences.getString("USERNAME", "");
    }

    /**
     * Gets the user password from the Shared Preferences
     * <p>
     * If the password is not set in the shared preferences return empty string ""
     *
     * @return user password in string format
     */
    public String getUserPassword() {
        return this.loginStorageSharedPreferences.getString("USERPASSWORD", "");
    }

    /**
     * @return user id in string format
     */
    public String getUserId() {
        return this.loginStorageSharedPreferences.getString("USERID", "");
    }

}
