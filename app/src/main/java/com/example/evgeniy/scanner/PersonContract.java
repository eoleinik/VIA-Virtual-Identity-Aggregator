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
                    PersonEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
                    PersonEntry.COLUMN_NAME_ADDRESS + " TEXT," +
                    PersonEntry.COLUMN_NAME_EMAIL + " TEXT," +
                    PersonEntry.COLUMN_NAME_PHONE + " TEXT," +
                    PersonEntry.COLUMN_NAME_PICTURE_ID + " TEXT," +
                    PersonEntry.COLUMN_NAME_FACEBOOK + " TEXT," +
                    PersonEntry.COLUMN_NAME_TWITTER + " TEXT)";
    private static final String SQL_DELETE_PEOPLE =
            "DROP TABLE IF EXISTS " + PersonEntry.PEOPLE_TABLE_NAME;
    // Cache profile id since it is requested often
    private static int myId = -1;

    private static String[] allFields = {
            PersonEntry.COLUMN_NAME_TIMESTAMP,
            PersonEntry.COLUMN_NAME_FIRST_NAME,
            PersonEntry.COLUMN_NAME_LAST_NAME,
            PersonEntry.COLUMN_NAME_PHONE,
            PersonEntry.COLUMN_NAME_EMAIL,
            PersonEntry.COLUMN_NAME_ADDRESS,
            PersonEntry.COLUMN_NAME_CONTACT_ID,
            PersonEntry.COLUMN_NAME_PICTURE_ID,
            PersonEntry.COLUMN_NAME_FACEBOOK,
            PersonEntry.COLUMN_NAME_TWITTER
    };

    private PersonContract() {
    }

    static int addContact(Context context, Person person) {
        if (person == null || person.getId() == -1)
            return -1;

        Boolean profileExists = false;
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {PersonEntry.COLUMN_NAME_FIRST_NAME};

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

        try {
            if (c.moveToFirst())
                profileExists = true;
        } finally {
            c.close();
        }

        if (profileExists)
            return -1;

        ContentValues values = new ContentValues();
        putAllValues(values, person);
        values.put(PersonEntry.COLUMN_NAME_IS_ME, 0);
        return (int) db.insert(PersonEntry.PEOPLE_TABLE_NAME, null, values);
    }

    static List<Person> getContacts(Context context) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = PersonEntry.COLUMN_NAME_IS_ME + " = ?";
        String[] selectionArgs = {"0"};
        String sortOrder = PersonEntry.COLUMN_NAME_LAST_NAME + " ASC";

        Cursor c = db.query(
                PersonEntry.PEOPLE_TABLE_NAME,
                allFields,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );


        List<Person> people = new ArrayList<>();

        try {
            while (c.moveToNext()) {
                people.add(getPersonFromCursor(c));
            }
        } finally {
            c.close();
        }
        return people;
    }

    static int removeContact(Context context, Person person) {
        if (person == null || person.getId() == -1)
            return -1;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = PersonEntry.COLUMN_NAME_CONTACT_ID + " = ?";
        String[] selectionArgs = {Integer.toString(person.getId())};

        return db.delete(
                PersonEntry.PEOPLE_TABLE_NAME,
                selection,
                selectionArgs);
    }

    static int removeContact(Context context, int id) {
        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = PersonEntry.COLUMN_NAME_CONTACT_ID + " = ?";
        String[] selectionArgs = {Integer.toString(id)};

        return db.delete(
                PersonEntry.PEOPLE_TABLE_NAME,
                selection,
                selectionArgs);
    }

    static int updateContact(Context context, Person person) {
        if (person == null || person.getId() == -1)
            return -1;

        DbHelper dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        putAllValues(values, person);

        String selection = PersonEntry.COLUMN_NAME_CONTACT_ID + " = ?";
        String[] selectionArgs = {Integer.toString(person.getId())};

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

        String[] projection = {PersonEntry.COLUMN_NAME_FIRST_NAME};

        Cursor c = db.query(
                PersonEntry.PEOPLE_TABLE_NAME,     // The table to query
                projection,                         // Columns to return
                null,                               // Columns for WHERE clause
                null,                               // the values for WHERE clause
                null,                               // Don't group the rows
                null,                               // Don't filter by row groups
                null                                // The sort order
        );

        try {
            if (c.moveToFirst())
                profileExists = true;
        } finally {
            c.close();
        }

        ContentValues values = new ContentValues();
        putAllValues(values, person);

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



        String selection = PersonEntry.COLUMN_NAME_IS_ME + " = ?";
        String[] selectionArgs = {"1"};

        Cursor c = db.query(
                PersonEntry.PEOPLE_TABLE_NAME,      // The table to query
                allFields,                          // Columns to return
                selection,                          // Columns for WHERE clause
                selectionArgs,                      // the values for WHERE clause
                null,                               // Don't group the rows
                null,                               // Don't filter by row groups
                null                                // The sort order
        );

        Person person = null;
        try {
            if (c.moveToFirst()) {
                person = getPersonFromCursor(c);
                myId = person.getId();
            }
        } finally {
            c.close();
        }

        return person;
    }

    static int getMyId(Context context) {
        if (myId == -1)
            getProfile(context);

        return myId;
    }

    private static Person getPersonFromCursor(Cursor c) {
        int id = c.getInt(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_CONTACT_ID));
        String timestamp = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_TIMESTAMP));
        String firstName = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_FIRST_NAME));
        String lastName = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_LAST_NAME));
        String phone = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_PHONE));
        String email = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_EMAIL));
        String address = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_ADDRESS));
        String pictureId = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_PICTURE_ID));
        String facebook = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_FACEBOOK));
        String twitter = c.getString(c.getColumnIndexOrThrow(PersonEntry.COLUMN_NAME_TWITTER));

        return new PersonBuilder().id(id).timestamp(timestamp).firstName(firstName).lastName(lastName).phone(phone).email(email).address(address).picture(pictureId).facebook(facebook).twitter(twitter).buildPerson();
    }

    private static void putAllValues(ContentValues values, Person person) {
        values.put(PersonEntry.COLUMN_NAME_TIMESTAMP, person.getTimestamp());
        values.put(PersonEntry.COLUMN_NAME_FIRST_NAME, person.getFirstName());
        values.put(PersonEntry.COLUMN_NAME_LAST_NAME, person.getLastName());
        values.put(PersonEntry.COLUMN_NAME_PHONE, person.getPhone());
        values.put(PersonEntry.COLUMN_NAME_EMAIL, person.getEmail());
        values.put(PersonEntry.COLUMN_NAME_ADDRESS, person.getAddress());
        values.put(PersonEntry.COLUMN_NAME_CONTACT_ID, person.getId());
        values.put(PersonEntry.COLUMN_NAME_PICTURE_ID, person.getPicture());
        values.put(PersonEntry.COLUMN_NAME_FACEBOOK, person.getFacebook());
        values.put(PersonEntry.COLUMN_NAME_TWITTER, person.getTwitter());
    }

    private static class PersonEntry {
        static final String PEOPLE_TABLE_NAME = "profile";
        static final String COLUMN_NAME_IS_ME = "isMe";
        static final String COLUMN_NAME_TIMESTAMP = "timestamp";
        static final String COLUMN_NAME_FIRST_NAME = "firstName";
        static final String COLUMN_NAME_LAST_NAME = "lastName";
        static final String COLUMN_NAME_PHONE = "phone";
        static final String COLUMN_NAME_EMAIL = "email";
        static final String COLUMN_NAME_ADDRESS = "address";
        static final String COLUMN_NAME_CONTACT_ID = "contactId";
        static final String COLUMN_NAME_PICTURE_ID = "pictureId";
        static final String COLUMN_NAME_FACEBOOK = "facebook";
        static final String COLUMN_NAME_TWITTER = "twitter";
    }

    private static class DbHelper extends SQLiteOpenHelper {
        // If schema is changed, updatePersonList this DB version!
        static final int DATABASE_VERSION = 4;
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

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_PEOPLE);
            onCreate(db);
        }
    }
}
