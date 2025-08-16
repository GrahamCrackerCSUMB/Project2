package com.example.dogtraininglog;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.UserDAO;
import com.example.dogtraininglog.database.entities.User;

public class ManageUsersActivity extends AppCompatActivity {
    private UserDAO userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        ListView listView = findViewById(R.id.userListView);
        userDao = DogTrainingDatabase.getDatabase(getApplicationContext()).userDAO();

        userDao.getAllUsers().observe(this, users -> { // DAO API exists.
            ArrayAdapter<User> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, users);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                User selected = users.get(position);
                if (!selected.isAdmin()) {
                    selected.setAdmin(true);
                    com.example.dogtraininglog.database.DogTrainingDatabase.databaseWriteExecutor.execute(() -> userDao.insert(selected));
                    Toast.makeText(this, selected.getUsername() + " is now an admin!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, selected.getUsername() + " is already an admin", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}