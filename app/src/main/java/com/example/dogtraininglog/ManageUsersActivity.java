package com.example.dogtraininglog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.UserDAO;
import com.example.dogtraininglog.database.entities.User;

import java.util.ArrayList;
import java.util.List;

public class ManageUsersActivity extends AppCompatActivity {

    private UserDAO userDao;
    private ListView listView;
    private int currentUserId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        // who is logged in?
        currentUserId = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                .getInt(getString(R.string.preference_userId_key), -1);

        listView = findViewById(R.id.userListView);
        userDao = DogTrainingDatabase.getDatabase(getApplicationContext()).userDAO();

        // Bottom buttons (if present)
        Button backBtn = findViewById(R.id.btnBack);
        if (backBtn != null) backBtn.setOnClickListener(v -> finish());

        Button logoutBtn = findViewById(R.id.btnLogout);
        if (logoutBtn != null) {
            logoutBtn.setOnClickListener(v -> {
                getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                        .edit()
                        .remove(getString(R.string.preference_userId_key))
                        .apply();
                Intent i = new Intent(ManageUsersActivity.this, LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                finish();
            });
        }

        // Observe users; hide admin1 and current user
        userDao.getAllUsers().observe(this, users -> {
            List<User> display = new ArrayList<>();
            for (User u : users) {
                if ("admin1".equalsIgnoreCase(u.getUsername())) continue;
                if (u.getId() == currentUserId) continue;
                display.add(u);
            }

            ArrayAdapter<User> adapter = new ArrayAdapter<User>(
                    this, android.R.layout.simple_list_item_1, display
            ) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    TextView tv = v.findViewById(android.R.id.text1);
                    User u = getItem(position);
                    if (u != null) {
                        tv.setText(u.getUsername() + (u.isAdmin() ? " (Admin)" : ""));
                    }
                    return v;
                }
            };
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                User selected = display.get(position);
                // toggle admin for other users
                selected.setAdmin(!selected.isAdmin());
                DogTrainingDatabase.databaseWriteExecutor.execute(() -> userDao.insert(selected));
                Toast.makeText(
                        this,
                        selected.getUsername() + (selected.isAdmin() ? " is now an admin!" : " is no longer an admin."),
                        Toast.LENGTH_SHORT
                ).show();
            });
        });
    }
}