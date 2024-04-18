package com.ivicamatic.shedmanagementsystem;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class DatabaseConnection {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Context context;
    LoginPreferences loginPreferences;

    public DatabaseConnection(Context context) {
        this.context = context;
        this.loginPreferences = new LoginPreferences(this.context.getSharedPreferences("LOGIN_PREFERENCES", Context.MODE_PRIVATE));
    }

    public void addUser(String userName, String userPassword, boolean isUserAdmin) {
        new Thread(() -> {

            Map<String, Object> userData = new HashMap<>();
            userData.put("username", userName);
            userData.put("admin", isUserAdmin);
            userData.put("items", new ArrayList<DocumentReference>());
            MessageDigest messageDigest = null;
            try {
                messageDigest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
                return;
            }
            messageDigest.update(userPassword.getBytes());
            String stringHash = new String(messageDigest.digest());
            userData.put("password", stringHash);
            db.collection("users").add(userData);
        }).start();


    }

    public boolean authenticateUser(String userName, String userPassword) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
        messageDigest.update(userPassword.getBytes());
        userPassword = new String(messageDigest.digest());
        Task<QuerySnapshot> all_users = db.collection("users").get();
        while (!all_users.isSuccessful()) {

        }
        for (QueryDocumentSnapshot document : all_users.getResult()) {
            String db_username = document.getString("username");
            String db_password = document.getString("password");
            if (userName.equals(db_username)) {
                if (userPassword.equals(db_password)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void deleteUser(String userId) {
        db.collection("users").document(userId).delete();
    }

    public boolean isUserAdmin(String userName) {
        Task<QuerySnapshot> all_users = db.collection("users").get();
        while (!all_users.isSuccessful()) {

        }
        for (QueryDocumentSnapshot document : all_users.getResult()) {
            String db_username = document.getString("username");
            if (userName.equals(db_username)) {
                return document.getBoolean("admin");
            }
        }
        return false;

    }

    public String getUserIdFromUsername(String userName) {
        if (this.loginPreferences.isPasswordStored()) {
            return this.loginPreferences.getUserName();
        }
        Task<QuerySnapshot> all_users = db.collection("users").get();
        while (!all_users.isSuccessful()) {

        }
        for (QueryDocumentSnapshot document : all_users.getResult()) {
            String db_username = document.getString("username");
            if (userName.equals(db_username)) {
                return document.getId();
            }
        }
        return "";
    }

    public ArrayList<Map<String, Object>> getAllUsers() {
        ArrayList<Map<String, Object>> users = new ArrayList<Map<String, Object>>();
        Task<QuerySnapshot> all_users = db.collection("users").get();
        while (!all_users.isSuccessful()) {

        }
        for (QueryDocumentSnapshot document : all_users.getResult()) {
            Map<String, Object> userData = new HashMap<>();
            String db_username = document.getString("username");
            Boolean is_admin = document.getBoolean("admin");
            ArrayList<DocumentReference> items = (ArrayList<DocumentReference>) document.get("items");
            int borrowed = items.size();
            userData.put("username", db_username);
            userData.put("admin", is_admin);
            userData.put("borrowed", borrowed);
            userData.put("id", document.getId());
            users.add(userData);
        }

        return users;
    }

    public void addOrganiser(String itemName,
                             String containerId,
                             int startingQuantity) {

        new Thread(() -> {

            Map<String, Object> data = new HashMap<>();
            data.put("itemName", itemName);
            data.put("containerId", containerId);
            data.put("quantity", startingQuantity);
            db.collection("items").add(data);
        }).start();
    }

    public void removeOrganiser(String containerId) {
        db.collection("items").document(containerId).delete();
    }

    public ArrayList<Map<String, Object>> getAllItemsInStock() {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Task<QuerySnapshot> all_items = db.collection("items").get();
        while (!all_items.isSuccessful()) {

        }
        for (QueryDocumentSnapshot document : all_items.getResult()) {
            Map<String, Object> a = document.getData();
            a.put("id", document.getId());
            list.add(a);
        }
        return list;
    }

    private String getItemReferenceFromContainerId(String containerId) {
        for (Map<String, Object> a : this.getAllItemsInStock()) {
            String id = (String) a.get("id");
            String obtainedContainerId = (String) a.get("containerId");
            if (obtainedContainerId.equals(containerId)) {
                return id;
            }
        }
        return "";
    }


    public Map<String, Object> getItemDetails(String containerId) {
        String itemReferenceAsString = this.getItemReferenceFromContainerId(containerId);
        DocumentReference reference_to_item = db.collection("items").document(itemReferenceAsString);
        Task<DocumentSnapshot> task_to_get_item_details = reference_to_item.get();
        while (!task_to_get_item_details.isSuccessful()) {

        }
        return task_to_get_item_details.getResult().getData();
    }

    public Boolean isItemInStock(String containerId) {
        try {
            String itemReferenceAsString = this.getItemReferenceFromContainerId(containerId);
            DocumentReference reference_to_item = db.collection("items").document(itemReferenceAsString);
            Task<DocumentSnapshot> task_to_get_item_details = reference_to_item.get();
            while (!task_to_get_item_details.isSuccessful()) {

            }
            Map<String, Object> item_details = task_to_get_item_details.getResult().getData();
            long quantity = (long) item_details.get("quantity");
            return quantity > 0;
        } catch (java.lang.IllegalArgumentException e) {
            return false;
        }
    }

    public Boolean isItemStocked(String containerId) {
        try {
            String itemReferenceAsString = this.getItemReferenceFromContainerId(containerId);
            DocumentReference reference_to_item = db.collection("items").document(itemReferenceAsString);
            Task<DocumentSnapshot> task_to_get_item_details = reference_to_item.get();
            while (!task_to_get_item_details.isSuccessful()) {

            }
            Map<String, Object> item_details = task_to_get_item_details.getResult().getData();
            long quantity = (long) item_details.get("quantity");
            return quantity > 0;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public ArrayList<Map<String, Object>> getUserBorrowedItems(String userName) {
        String userId = this.getUserIdFromUsername(userName);
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Task<DocumentSnapshot> user_entry = db.collection("users").document(userId).get();
        while (!user_entry.isSuccessful()) {

        }
        Map<String, Object> user_entry_results = user_entry.getResult().getData();
        assert user_entry_results != null;
        if (!user_entry_results.containsKey("items")) {
            return list;

        }
        ArrayList<DocumentReference> items = (ArrayList<DocumentReference>) user_entry_results.get("items");
        for (DocumentReference i : items) {
            Task<DocumentSnapshot> document = i.get();
            while (!document.isSuccessful()) {

            }
            Map<String, Object> a = document.getResult().getData();
            if (Objects.isNull(a)) {
                continue;
            }
            a.put("id", document.getResult().getId());
            list.add(a);
        }
        return list;

    }

    public int getUserBorrowedItemCount(String userName, String containerId) {
        int count = 0;
        String userId = this.getUserIdFromUsername(userName);
        Task<DocumentSnapshot> user_entry = db.collection("users").document(userId).get();
        while (!user_entry.isSuccessful()) {

        }
        Map<String, Object> user_entry_results = user_entry.getResult().getData();
        assert user_entry_results != null;
        if (!user_entry_results.containsKey("items")) {
            return 0;
        }
        ArrayList<DocumentReference> items = (ArrayList<DocumentReference>) user_entry_results.get("items");
        for (DocumentReference i : items) {
            Task<DocumentSnapshot> document = i.get();
            while (!document.isSuccessful()) {

            }
            Map<String, Object> a = document.getResult().getData();
            if (Objects.isNull(a)) {
                continue;
            }
            String obtainedContainerId = (String) a.get("containerId");
            if (obtainedContainerId.equals(containerId)) {
                count++;
            }
        }
        return count;
    }


    public boolean userHasItemBorrowed(String userName, String containerId) {
        String userId = this.getUserIdFromUsername(userName);
        Task<DocumentSnapshot> user_entry = db.collection("users").document(userId).get();
        while (!user_entry.isSuccessful()) {

        }
        Map<String, Object> user_entry_results = user_entry.getResult().getData();
        assert user_entry_results != null;
        if (!user_entry_results.containsKey("items")) {
            return false;
        }
        ArrayList<DocumentReference> items = (ArrayList<DocumentReference>) user_entry_results.get("items");
        for (DocumentReference i : items) {
            Task<DocumentSnapshot> document = i.get();
            while (!document.isSuccessful()) {

            }
            Map<String, Object> a = document.getResult().getData();
            if (Objects.isNull(a)) {
                continue;
            }
            String obtainedContainerId = (String) a.get("containerId");
            if (obtainedContainerId.equals(containerId)) {
                return true;
            }
        }
        return false;
    }


    public void userBorrowItem(String userName, String containerId, int takeCount) {
        new Thread(() -> {
            if (!this.isItemInStock(containerId)) {
                return;
            }
            String userId = this.getUserIdFromUsername(userName);
            String itemReferenceAsString = this.getItemReferenceFromContainerId(containerId);
            DocumentReference reference_to_item = db.collection("items").document(itemReferenceAsString);
            Task<DocumentSnapshot> task_to_get_item_details = reference_to_item.get();
            while (!task_to_get_item_details.isSuccessful()) {

            }
            Map<String, Object> item_details = task_to_get_item_details.getResult().getData();
            Task<DocumentSnapshot> user_entry = db.collection("users").document(userId).get();
            while (!user_entry.isSuccessful()) {

            }
            Map<String, Object> user_entry_results = user_entry.getResult().getData();
            assert user_entry_results != null;
            if (!user_entry_results.containsKey("items")) {
                ArrayList<DocumentReference> list_of_users_items = new ArrayList<DocumentReference>();
                user_entry_results.put("items", list_of_users_items);
                db.collection("users").document(userId).update(user_entry_results);
                userBorrowItem(userName, containerId, takeCount);
            }
            long old_quantity = (long) item_details.get("quantity");
            item_details.replace("quantity", old_quantity - takeCount);
            reference_to_item.update(item_details);
            ArrayList<DocumentReference> list_of_users_items = (ArrayList<DocumentReference>) user_entry_results.get("items");
            for (int i = 0; i < takeCount; i++) {
                list_of_users_items.add(reference_to_item);
            }
            db.collection("users").document(userId).update(user_entry_results);
        }).start();
    }


    public void userReturnItem(String userName, String containerId, int returnQuantity) {
        new Thread(() -> {

            String userId = this.getUserIdFromUsername(userName);
            if (!this.userHasItemBorrowed(userName, containerId)) {
                return;
            }
            String itemReferenceAsString = this.getItemReferenceFromContainerId(containerId);
            DocumentReference reference_to_item = db.collection("items").document(itemReferenceAsString);
            Task<DocumentSnapshot> task_to_get_item_details = reference_to_item.get();
            while (!task_to_get_item_details.isSuccessful()) {

            }
            Map<String, Object> item_details = task_to_get_item_details.getResult().getData();
            Task<DocumentSnapshot> user_entry = db.collection("users").document(userId).get();
            while (!user_entry.isSuccessful()) {

            }
            Map<String, Object> user_entry_results = user_entry.getResult().getData();
            ArrayList<DocumentReference> list_of_users_items = (ArrayList<DocumentReference>) user_entry_results.get("items");
            Iterator<DocumentReference> itr = list_of_users_items.iterator();
            int returned = 0;
            while (itr.hasNext()) {
                DocumentReference ref = itr.next();
                if (ref.equals(reference_to_item)) {
                    Log.d("rtn", "Returning");
                    returned++;
                    itr.remove();
                }

                if (returned >= returnQuantity) {
                    break;
                }
            }
            long old_quantity = (long) item_details.get("quantity");
            item_details.replace("quantity", old_quantity + returned);
            reference_to_item.update(item_details);
            db.collection("users").document(userId).update(user_entry_results);
        }).start();
    }

    public void adjustStock(String containerId, int realQuantity) {
        new Thread(() -> {
            String itemReferenceAsString = this.getItemReferenceFromContainerId(containerId);
            DocumentReference reference_to_item = db.collection("items").document(itemReferenceAsString);
            Task<DocumentSnapshot> task_to_get_item_details = reference_to_item.get();
            while (!task_to_get_item_details.isSuccessful()) {

            }
            Map<String, Object> item_details = task_to_get_item_details.getResult().getData();
            assert item_details != null;
            item_details.replace("quantity", realQuantity);
            reference_to_item.update(item_details);
        }).start();
    }
}