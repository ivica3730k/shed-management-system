package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.HashMap;

public class SeeStockActivityDisplayAdapter extends BaseAdapter implements ListAdapter {
    private final Context context;
    private final Intent homeIntent;
    DatabaseConnection database;
    LoginPreferences loginPreferences;
    boolean isAdmin = false;
    private ArrayList<Object> list = new ArrayList<Object>();


    public SeeStockActivityDisplayAdapter(ArrayList<Object> list, Context context) {
        this.list = list;
        this.context = context;
        this.database = new DatabaseConnection(this.context);
        this.homeIntent = new Intent(context, SeeStockActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        this.loginPreferences = new LoginPreferences(context.getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        this.isAdmin = database.isUserAdmin(loginPreferences.getUserName());
    }

    public void somethingChanged() {

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_see_stock_display_layout, null);
        }
        TextView tvContact = view.findViewById(R.id.itemDetails);
        Button removeButton = view.findViewById(R.id.removeButton);
        if (!isAdmin) {
            removeButton.setVisibility(View.INVISIBLE);
        }
        Object e = list.get(position);
        HashMap<String, Object> a;
        a = (HashMap<String, Object>) e;
        String text = "";
        text += a.get("itemName");
        text += "\n";
        text += "In Stock: ";
        text += a.get("quantity");
        tvContact.setText(text);

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                database.removeOrganiser(String.valueOf(a.get("id")));
                                somethingChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this organiser from the system?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });


        return view;
    }
}