package com.example.maindash.AccountCreation;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "UserDatabase.db";
    public static final int DATABASE_VERSION = 3; // Incremented version number
    public static final String TABLE_NAME = "users";
    public static final String COL_USERNAME = "username";
    public static final String COL_PASSWORD = "password";
    public static final String COL_SCAN_COUNT = "scan_count";
    private static final String TABLE_SCANS = "user_scans";
    private static final String COL_QR_CODE = "qr_code";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating the table
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_USERNAME + " TEXT PRIMARY KEY, " +
                COL_PASSWORD + " TEXT, " +
                COL_SCAN_COUNT + " INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE " + TABLE_SCANS + " (" +
                COL_USERNAME + " TEXT, " +
                COL_QR_CODE + " TEXT, " +
                "PRIMARY KEY (" + COL_USERNAME + ", " + COL_QR_CODE + "))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add scan_count column for existing database
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COL_SCAN_COUNT + " INTEGER DEFAULT 0");
        }

        if (oldVersion < 3) {
            // Create user_scans table for newer versions
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_SCANS + " (" +
                    COL_USERNAME + " TEXT, " +
                    COL_QR_CODE + " TEXT, " +
                    "PRIMARY KEY (" + COL_USERNAME + ", " + COL_QR_CODE + "))");
        }
    }
    // New method to check if a QR code has been scanned before by this user
    public boolean isUniqueScan(String username, String qrCode) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_SCANS +
                        " WHERE " + COL_USERNAME + "=? AND " + COL_QR_CODE + "=?",
                new String[]{username, qrCode}
        );

        boolean isUnique = cursor.getCount() == 0;
        cursor.close();
        return isUnique;
    }

    // Method to record a unique scan
    public void recordUniqueScan(String username, String qrCode) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_QR_CODE, qrCode);

        db.insert(TABLE_SCANS, null, contentValues);
    }

    // Add a new user to the database
    public boolean addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (isUsernameExist(username,password)) {
            return false;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_USERNAME, username);
        contentValues.put(COL_PASSWORD, password);
        contentValues.put(COL_SCAN_COUNT, 0); // Initialize scan count to 0

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    // Check if a username exists
    public boolean isUsernameExist(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});

        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Validate username and password
    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    // Get the current scan count for a user
    public int getScanCount(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + COL_SCAN_COUNT + " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?",
                new String[]{username});

        int scanCount = 0;
        if (cursor.moveToFirst()) {
            scanCount = cursor.getInt(0);
        }
        cursor.close();
        return scanCount;
    }

    @SuppressLint("Range")
    public int incrementScanCount(String username) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Query to get the current scan count for the user
        Cursor cursor = db.rawQuery("SELECT " + COL_SCAN_COUNT + " FROM " + TABLE_NAME + " WHERE " + COL_USERNAME + "=?", new String[]{username});
        int scanCount = 0;

        if (cursor.moveToFirst()) {
            // If the user exists, retrieve the current scan count
            scanCount = cursor.getInt(cursor.getColumnIndex(COL_SCAN_COUNT));
        }
        cursor.close();

        // Increment the scan count
        scanCount++;

        // Update the user's scan count in the database
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SCAN_COUNT, scanCount);
        db.update(TABLE_NAME, contentValues, COL_USERNAME + "=?", new String[]{username});

        return scanCount; // Return the updated scan count
    }

}

