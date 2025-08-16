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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        listView = findViewById(R.id.userListView);
        userDao = DogTrainingDatabase.getDatabase(getApplicationContext()).userDAO();

        // Observe users and render a filtered list (hide the seed admin account)
        userDao.getAllUsers().observe(this, users -> {
            // 1) Build filtered list (hide "admin1")
            List<User> filtered = new ArrayList<>();
            for (User u : users) {
                if (!"admin1".equalsIgnoreCase(u.getUsername())) {
                    filtered.add(u);
                }
            }

            // 2) Adapter that shows username and role
            ArrayAdapter<User> adapter = new ArrayAdapter<User>(
                    this,
                    android.R.layout.simple_list_item_1,
                    filtered
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

            Button backBtn = findViewById(R.id.btnBack);
            backBtn.setOnClickListener(v -> finish());

            Button logoutBtn = findViewById(R.id.btnLogout);
            logoutBtn.setOnClickListener(v -> {
                // Clear login state
                getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE)
                        .edit()
                        .remove(getString(R.string.preference_userId_key))
                        .apply();

                // Go back to login screen
                Intent intent = new Intent(ManageUsersActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });

            // 3) Toggle admin status on tap
            listView.setOnItemClickListener((parent, view, position, id) -> {
                User selected = filtered.get(position);

                if (selected.isAdmin()) {
                    // Demote
                    selected.setAdmin(false);
                    DogTrainingDatabase.databaseWriteExecutor.execute(() -> userDao.insert(selected));
                    Toast.makeText(this,
                            selected.getUsername() + " is no longer an admin.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Promote
                    selected.setAdmin(true);
                    DogTrainingDatabase.databaseWriteExecutor.execute(() -> userDao.insert(selected));
                    Toast.makeText(this,
                            selected.getUsername() + " is now an admin!",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}