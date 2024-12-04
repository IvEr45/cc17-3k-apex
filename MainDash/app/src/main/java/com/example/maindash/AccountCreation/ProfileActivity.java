package com.example.maindash.AccountCreation;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.maindash.R;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private EditText usernameEditText, passwordEditText;
    private Button saveButton;
    private DatabaseHelper databaseHelper;
    private String currentUsername;
    private TextView scanCountTextView;
    private TextView user1;
    private String username;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        scanCountTextView = findViewById(R.id.scanCountTextView);
        user1 = findViewById(R.id.user1);
        databaseHelper = new DatabaseHelper(this);

        // Get the username from the Intent or from SharedPreferences (depending on your login flow)
        username = getIntent().getStringExtra("username"); // Assuming you pass username when opening ProfileActivity


        // Fetch the scan count for the user from the database
        int scanCount = databaseHelper.getScanCount(username);

        // Display the scan count in the TextView
        scanCountTextView.setText("Points: " + scanCount);
        user1.setText("Hi there "+username);

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Get current username from intent (assuming it was passed from previous activity)
        currentUsername = getIntent().getStringExtra("username");

        // Populate current username
        usernameEditText.setText(currentUsername);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserProfile();
            }
        });
    }

    private void updateUserProfile() {
        String newUsername = usernameEditText.getText().toString().trim();
        String newPassword = passwordEditText.getText().toString().trim();

        // Validate input
        if (newUsername.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if username or password is changed
        if (!newUsername.equals(currentUsername)) {
            // Delete old user record and create new one
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COL_USERNAME + "=?", new String[]{currentUsername});

            // Add new user record
            boolean isAdded = databaseHelper.addUser(newUsername, newPassword);

            if (isAdded) {
                currentUsername = newUsername;
                Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close activity after successful update
            } else {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update password for existing username
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COL_PASSWORD, newPassword);

            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            int rowsAffected = db.update(DatabaseHelper.TABLE_NAME,
                    contentValues,
                    DatabaseHelper.COL_USERNAME + "=?",
                    new String[]{currentUsername});

            if (rowsAffected > 0) {
                Toast.makeText(this, "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Update Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
