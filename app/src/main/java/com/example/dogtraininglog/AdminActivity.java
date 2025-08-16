package com.example.dogtraininglog;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import com.example.dogtraininglog.database.ViewLogsActivity;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnAllLogs = findViewById(R.id.btnAllLogs);
        Button btnManageUsers = findViewById(R.id.btnManageUsers);

        btnAllLogs.setOnClickListener(v -> {
            Intent i = new Intent(this, ViewLogsActivity.class);
            i.putExtra("EXTRA_ADMIN", true); // flag so ViewLogsActivity shows all logs
            startActivity(i);
        });

        Button backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> finish());

        Button logoutBtn = findViewById(R.id.btnLogout);
        logoutBtn.setOnClickListener(v -> {
            // Clear login state if stored in SharedPreferences
            getSharedPreferences("app_prefs", MODE_PRIVATE)
                    .edit()
                    .remove("loggedInUserId")
                    .apply();

            // Go back to login screen
            Intent intent = new Intent(AdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnManageUsers.setOnClickListener(v -> {
            Intent i = new Intent(this, ManageUsersActivity.class);
            startActivity(i);

        });
    }
}