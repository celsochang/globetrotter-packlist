package com.celsa.globetrotterpacklist.persistance;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final static String DB_NAME = "db";
    private final static int DB_VERSION = 1;
    Context context;

    public DatabaseHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE item (" +
                "_id         integer       NOT NULL," +
                "name        varchar(100)  NOT NULL," +
                "photo_id    varchar(100)  DEFAULT NULL," +
                "status      integer(1)    NOT NULL DEFAULT 0," +
                "\"order\"   integer       NOT NULL," +
                "PRIMARY KEY (_id));");

        db.execSQL("INSERT INTO item(_id, name, status, \"order\") VALUES (1, 'test', 0, 1);");
        db.execSQL("INSERT INTO item(_id, name, status, \"order\") VALUES (2, 'test2', 0, 2);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
