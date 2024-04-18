package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SeeMyItemsActivityDisplayAdapter extends BaseAdapter implements ListAdapter {
    private final Context context;
    private final Intent homeIntent;
    DatabaseConnection database;// = new DatabaseConnection(context);
    LoginPreferences loginPreferences;
    Boolean owner = true;
    String userName;
    private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();


    public SeeMyItemsActivityDisplayAdapter(ArrayList<Map<String, Object>> list, Context context) {
        this.list = list;
        this.context = context;
        this.database = new DatabaseConnection(this.context);
        this.homeIntent = new Intent(context, SeeStockActivity.class);
        this.loginPreferences = new LoginPreferences(this.context.getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    }

    public SeeMyItemsActivityDisplayAdapter(ArrayList<Map<String, Object>> list, Context context, boolean owner, String userName) {
        this.list = list;
        this.context = context;
        this.database = new DatabaseConnection(this.context);
        this.homeIntent = new Intent(context, SeeStockActivity.class);
        this.owner = owner;
        this.userName = userName;
        this.loginPreferences = new LoginPreferences(this.context.getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
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
            view = inflater.inflate(R.layout.custom_see_myitems_display_layout, null);
        }
        TextView tvContact = view.findViewById(R.id.itemDetails);
        Button removeButton = view.findViewById(R.id.removeButton);
        Object e = list.get(position);
        HashMap<String, Object> a;
        a = (HashMap<String, Object>) e;
        String text = "";
        text += a.get("itemName");
        text += "\n";
        if (owner) {
            text += "In my possession: ";
            text += database.getUserBorrowedItemCount(this.loginPreferences.getUserName(), (String) a.get("containerId"));
        } else {
            text += "In possession: ";
            text += database.getUserBorrowedItemCount(this.userName, (String) a.get("containerId"));
        }

        tvContact.setText(text);

        if (!owner) {
            removeButton.setEnabled(false);
            removeButton.setVisibility(View.INVISIBLE);
        }
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent(context, ReturnItemActivity.class);
                returnIntent.putExtra("CONTAINER_ID", (String) a.get("containerId"));
                context.startActivity(returnIntent);

            }
        });

        return view;
    }
}