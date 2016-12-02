package com.example.evgeniy.scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

final class PersonContract {
    private static final String SQL_CREATE_PROFILE =
            "CREATE TABLE " + PersonEntry.PROFILE_TABLE_NAME + " (" +
                    PersonEntry._ID + " INTEGER PRIMARY KEY," +
                    PersonEntry.COLUMN_NAME_FIRSTNAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_LASTNAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    PersonEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    PersonEntry.COLUMN_NAME_PHONE + " TEXT," +
                    PersonEntry.COLUMN_NAME_PICTURE + " BLOB)";
    private static final String SQL_CREATE_CONTACTS =
            "CREATE TABLE " + PersonEntry.CONTACTS_TABLE_NAME + " (" +
                    PersonEntry._ID + " INTEGER PRIMARY KEY," +
                    PersonEntry.COLUMN_NAME_FIRSTNAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_LASTNAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    PersonEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    PersonEntry.COLUMN_NAME_PHONE + " TEXT," +
                    PersonEntry.COLUMN_NAME_PICTURE + " BLOB)";
    private static final String SQL_DELETE_PROFILE =
            "DROP TABLE IF EXISTS " + PersonEntry.PROFILE_TABLE_NAME;
    private static final String SQL_DELETE_CONTACTS =
            "DROP TABLE IF EXISTS " + PersonEntry.CONTACTS_TABLE_NAME;

    private PersonContract() {
    }

    static void SaveProfile(Context context, Person person) {
        Boolean profileExists = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {PersonEntry._ID};

        Cursor c = db.query(
                PersonEntry.PROFILE_TABLE_NAME,     // The table to query
                projection,                         // Columns to return
                null,                               // Columns for WHERE clause
                null,                               // the values for WHERE clause
                null,                               // Don't group the rows
                null,                               // Don't filter by row groups
                null                                // The sort order
        );

        if (c.moveToFirst())
            profileExists = true;

        c.close();

        ContentValues values = new ContentValues();
        values.put(PersonEntry.COLUMN_NAME_FIRSTNAME, person.getFirstName());
        values.put(PersonEntry.COLUMN_NAME_LASTNAME, person.getLastName());
        values.put(PersonEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(PersonEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(PersonEntry.COLUMN_NAME_ADDRESS, person.getAddress());

        String selection = PersonEntry._ID + " = ?";
        String[] selectionArgs = {"1"};

        if (profileExists)
            db.update(
                    PersonEntry.PROFILE_TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        else
            db.insert(
                    PersonEntry.PROFILE_TABLE_NAME,
                    null,
                    values
            );
    }

    public static Person getProfile(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PersonEntry._ID,
                PersonEntry.COLUMN_NAME_FIRSTNAME,
                PersonEntry.COLUMN_NAME_LASTNAME,
                PersonEntry.COLUMN_NAME_PHONE,
                PersonEntry.COLUMN_NAME_EMAIL,
                PersonEntry.COLUMN_NAME_ADDRESS
        };

        Cursor c = db.query(
                PersonEntry.PROFILE_TABLE_NAME,     // The table to query
                projection,                         // Columns to return
                null,                               // Columns for WHERE clause
                null,                               // the values for WHERE clause
                null,                               // Don't group the rows
                null,                               // Don't filter by row groups
                null                                // The sort order
        );

        // If there are no results, profile does not exist in DB
        if (!c.moveToFirst())
            return null;

        String firstName = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_FIRSTNAME));
        String lastName = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_LASTNAME));
        String phone = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_PHONE));
        String email = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_EMAIL));
        String address = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_ADDRESS));
        c.close();
        return new Person(firstName, lastName, phone, email, address);
    }

    private static class PersonEntry implements BaseColumns {
        static final String PROFILE_TABLE_NAME = "profile";
        static final String CONTACTS_TABLE_NAME = "contacts";
        static final String COLUMN_NAME_FIRSTNAME = "firstName";
        static final String COLUMN_NAME_LASTNAME = "lastName";
        static final String COLUMN_NAME_PHONE = "phone";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_ADDRESS = "address";
        static final String COLUMN_NAME_PICTURE = "picture";
    }

    private static class DbHelper extends SQLiteOpenHelper {
        // If schema is changed, update this DB version!
        static final int DATABASE_VERSION = 1;
        static final String DATABASE_NAME = "Local.db";

        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_PROFILE);
            db.execSQL(SQL_CREATE_CONTACTS);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_PROFILE);
            db.execSQL(SQL_DELETE_CONTACTS);
            onCreate(db);
        }
    }
}
