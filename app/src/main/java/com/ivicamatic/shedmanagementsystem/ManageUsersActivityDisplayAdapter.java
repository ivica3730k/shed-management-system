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

public class ManageUsersActivityDisplayAdapter extends BaseAdapter implements ListAdapter {
    private final Context context;
    DatabaseConnection database;
    LoginPreferences loginPreferences;
    private ArrayList<Object> list = new ArrayList<Object>();


    public ManageUsersActivityDisplayAdapter(ArrayList<Object> list, Context context) {
        this.list = list;
        this.context = context;
        this.database = new DatabaseConnection(this.context);
        this.loginPreferences = new LoginPreferences(context.getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
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
            view = inflater.inflate(R.layout.custom_manage_users_display_layout, null);
        }
        TextView itemDetailsView = view.findViewById(R.id.itemDetails);
        Button removeButton = view.findViewById(R.id.removeButton);
        Button seeUserItemsButton = view.findViewById(R.id.seeUserItemsButton);

        Object e = list.get(position);
        HashMap<String, Object> a;
        a = (HashMap<String, Object>) e;
        String id = (String) a.get("id");
        if (id.equals(this.loginPreferences.getUserId())) {
            removeButton.setEnabled(false);
        }
        String text = "";
        text += (String) a.get("username");
        text += "\n";
        //text += "Role: ";
        boolean admin = (boolean) a.get("admin");
        if (admin)
            text += "Administrator";
        else {
            text += "Normal User";
        }
        itemDetailsView.setText(text);

        seeUserItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent viewItemsIntent = new Intent(context, SeeMyItemsActivity.class);
                viewItemsIntent.putExtra("USERNAME", (String) a.get("username"));
                context.startActivity(viewItemsIntent);
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                database.deleteUser(String.valueOf(a.get("id")));
                                somethingChanged();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Are you sure you want to delete this person from the system? This will require you to do manual stocktake if user has pending items to return! Continue?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();


            }
        });


        return view;
    }
}