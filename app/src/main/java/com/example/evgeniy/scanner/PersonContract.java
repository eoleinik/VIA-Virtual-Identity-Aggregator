package com.example.evgeniy.scanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

final class PersonContract {
    private static final String SQL_CREATE_PEOPLE =
            "CREATE TABLE " + PersonEntry.PEOPLE_TABLE_NAME + " (" +
                    PersonEntry.COLUMN_NAME_CONTACT_ID + " INTEGER PRIMARY KEY," +
                    PersonEntry.COLUMN_NAME_IS_ME + " INTEGER DEFAULT 0," +
                    PersonEntry.COLUMN_NAME_TIMESTAMP + " TEXT," +
                    PersonEntry.COLUMN_NAME_FIRSTNAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_LASTNAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    PersonEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    PersonEntry.COLUMN_NAME_PHONE + " TEXT," +
                    PersonEntry.COLUMN_NAME_IMAGE_FILENAME + " BLOB)";

    private static final String SQL_DELETE_PEOPLE =
            "DROP TABLE IF EXISTS " + PersonEntry.PEOPLE_TABLE_NAME;

    private PersonContract() {
    }

    static int addContact(Context context, Person person) {
        if (person == null || person.getId() == -1)
            return -1;

        Boolean profileExists = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {PersonEntry.COLUMN_NAME_FIRSTNAME};

        String selection = PersonEntry.COLUMN_NAME_CONTACT_ID + " = ?";

        String[] selectionArgs = {Integer.toString(person.getId())};

        Cursor c = db.query(
                PersonEntry.PEOPLE_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (c.moveToFirst())
            profileExists = true;

        c.close();

        if (profileExists)
            return -1;

        ContentValues values = new ContentValues();
        values.put(PersonEntry.COLUMN_NAME_FIRSTNAME, person.getFirstName());
        values.put(PersonEntry.COLUMN_NAME_LASTNAME, person.getLastName());
        values.put(PersonEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(PersonEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(PersonEntry.COLUMN_NAME_ADDRESS, person.getAddress());
        values.put(PersonEntry.COLUMN_NAME_CONTACT_ID, person.getId());

        return (int) db.insert(PersonEntry.PEOPLE_TABLE_NAME, null, values);
    }

    static List<Person> getContacts(Context context, Person person) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PersonEntry.COLUMN_NAME_TIMESTAMP,
                PersonEntry.COLUMN_NAME_FIRSTNAME,
                PersonEntry.COLUMN_NAME_LASTNAME,
                PersonEntry.COLUMN_NAME_PHONE,
                PersonEntry.COLUMN_NAME_EMAIL,
                PersonEntry.COLUMN_NAME_ADDRESS,
                PersonEntry.COLUMN_NAME_CONTACT_ID
        };

        String selection = PersonEntry.COLUMN_NAME_IS_ME + " = ?";
        String[] selectionArgs = {"0"};
        String sortOrder = PersonEntry.COLUMN_NAME_LASTNAME + " ASC";

        Cursor c = db.query(
                PersonEntry.PEOPLE_TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );


        List<Person> people = new ArrayList<>();

        while (c.moveToNext()) {
            people.add(getPersonFromCursor(c));
        }

        c.close();
        return people;
    }

    static int updateContact(Context context, Person person, int contactId) {
        if (person.getId() == -1)
            return -1;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(PersonEntry.COLUMN_NAME_TIMESTAMP, person.getTimestamp());
        values.put(PersonEntry.COLUMN_NAME_FIRSTNAME, person.getFirstName());
        values.put(PersonEntry.COLUMN_NAME_LASTNAME, person.getLastName());
        values.put(PersonEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(PersonEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(PersonEntry.COLUMN_NAME_ADDRESS, person.getAddress());

        String selection = PersonEntry.COLUMN_NAME_CONTACT_ID + " = ?";
        String[] selectionArgs = {Integer.toString(contactId)};

        return db.update(
                PersonEntry.PEOPLE_TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    static int saveProfile(Context context, Person person) {
        Boolean profileExists = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {PersonEntry.COLUMN_NAME_FIRSTNAME};

        Cursor c = db.query(
                PersonEntry.PEOPLE_TABLE_NAME,     // The table to query
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
        values.put(PersonEntry.COLUMN_NAME_CONTACT_ID, person.getId());
        values.put(PersonEntry.COLUMN_NAME_TIMESTAMP, person.getFirstName());
        values.put(PersonEntry.COLUMN_NAME_FIRSTNAME, person.getFirstName());
        values.put(PersonEntry.COLUMN_NAME_LASTNAME, person.getLastName());
        values.put(PersonEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(PersonEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(PersonEntry.COLUMN_NAME_ADDRESS, person.getAddress());

        String selection = PersonEntry.COLUMN_NAME_IS_ME + " = ?";
        String[] selectionArgs = {"1"};

        QRGenerator.generateAndSave(person, context.getString(R.string.my_qr_code), context);

        if (profileExists) {
            return db.update(
                    PersonEntry.PEOPLE_TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        } else {
            values.put(PersonEntry.COLUMN_NAME_IS_ME, 1);
            return (int) db.insert(
                    PersonEntry.PEOPLE_TABLE_NAME,
                    null,
                    values
            );
        }
    }

    @Nullable
    static Person getProfile(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                PersonEntry.COLUMN_NAME_TIMESTAMP,
                PersonEntry.COLUMN_NAME_FIRSTNAME,
                PersonEntry.COLUMN_NAME_LASTNAME,
                PersonEntry.COLUMN_NAME_PHONE,
                PersonEntry.COLUMN_NAME_EMAIL,
                PersonEntry.COLUMN_NAME_ADDRESS,
                PersonEntry.COLUMN_NAME_CONTACT_ID
        };

        String selection = PersonEntry.COLUMN_NAME_IS_ME + " = ?";
        String[] selectionArgs = {"1"};

        Cursor c = db.query(
                PersonEntry.PEOPLE_TABLE_NAME,      // The table to query
                projection,                         // Columns to return
                selection,                          // Columns for WHERE clause
                selectionArgs,                      // the values for WHERE clause
                null,                               // Don't group the rows
                null,                               // Don't filter by row groups
                null                                // The sort order
        );

        // If there are no results, profile does not exist in DB
        if (!c.moveToFirst())
            return null;

        Person person = getPersonFromCursor(c);
        c.close();
        return person;
    }

    private static Person getPersonFromCursor(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_CONTACT_ID));
        String timestamp = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_TIMESTAMP));
        String firstName = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_FIRSTNAME));
        String lastName = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_LASTNAME));
        String phone = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_PHONE));
        String email = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_EMAIL));
        String address = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_ADDRESS));

        return new Person(id, timestamp, firstName, lastName, phone, email, address);
    }

    private static class PersonEntry {
        static final String PEOPLE_TABLE_NAME = "profile";
        static final String COLUMN_NAME_IS_ME = "isMe";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        static final String COLUMN_NAME_FIRSTNAME = "firstName";
        static final String COLUMN_NAME_LASTNAME = "lastName";
        static final String COLUMN_NAME_PHONE = "phone";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_ADDRESS = "address";
        static final String COLUMN_NAME_IMAGE_FILENAME = "imageFilename";
        static final String COLUMN_NAME_CONTACT_ID = "contactId";
    }

    private static class DbHelper extends SQLiteOpenHelper {
        // If schema is changed, update this DB version!
        static final int DATABASE_VERSION = 3;
        static final String DATABASE_NAME = "Local.db";

        DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_PEOPLE);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_PEOPLE);
            onCreate(db);
        }
    }
}
