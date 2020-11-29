package com.example.babyneeds.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.babyneeds.R;
import com.example.babyneeds.model.Item;
import com.example.babyneeds.util.Prefs;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private final Context context;

    public DatabaseHandler(@Nullable Context context) {
        super(context, Prefs.DB_NAME, null, Prefs.DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BABY_TABLE = "CREATE TABLE " + Prefs.TABLE_NAME + "("
                + Prefs.KEY_ID + " INTEGER PRIMARY KEY, "
                + Prefs.KEY_BABY_ITEM + " TEXT, "
                + Prefs.KEY_COLOR + " TEXT, "
                + Prefs.KEY_QTY_NUMBER + " INTEGER, "
                + Prefs.KEY_ITEM_SIZE + " INTEGER, "
                + Prefs.KEY_DATE_NAME + " LONG);";
        db.execSQL(CREATE_BABY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.valueOf(R.string.drop_table) + Prefs.TABLE_NAME);

        onCreate(db);

    }

    //crud operation

    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Prefs.KEY_BABY_ITEM, item.getItemName());
        values.put(Prefs.KEY_COLOR, item.getItemColor());
        values.put(Prefs.KEY_QTY_NUMBER, item.getItemQuantity());
        values.put(Prefs.KEY_ITEM_SIZE, item.getItemSize());
        values.put(Prefs.KEY_DATE_NAME, java.lang.System.currentTimeMillis());

        db.insert(Prefs.TABLE_NAME, null, values);

        Log.d("DbHandler", "addItem: ");
    }

    public Item getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Prefs.TABLE_NAME,
                new String[]{Prefs.KEY_ID,
                        Prefs.KEY_BABY_ITEM,
                        Prefs.KEY_COLOR,
                        Prefs.KEY_QTY_NUMBER,
                        Prefs.KEY_ITEM_SIZE,
                        Prefs.KEY_DATE_NAME},
                Prefs.KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        Item item = new Item();
        if (cursor != null) {

            item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Prefs.KEY_ID))));
            item.setItemName(cursor.getString(cursor.getColumnIndex(Prefs.KEY_BABY_ITEM)));
            item.setItemQuantity(cursor.getInt(cursor.getColumnIndex(Prefs.KEY_QTY_NUMBER)));
            item.setItemColor(cursor.getString(cursor.getColumnIndex(Prefs.KEY_COLOR)));
            item.setItemSize(cursor.getInt(cursor.getColumnIndex(Prefs.KEY_ITEM_SIZE)));

            //convert time stamp to readable
            DateFormat dateFormat = DateFormat.getDateInstance();
            String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Prefs.KEY_DATE_NAME))).getTime());
            item.setDateItemAdded(formatedDate);

        }

        return item;
    }
    public List<Item> getAllItems(){

        SQLiteDatabase db = this.getReadableDatabase();

        List<Item> items = new ArrayList<>();

        Cursor cursor = db.query(Prefs.TABLE_NAME,
                new String[]{Prefs.KEY_ID,
                        Prefs.KEY_BABY_ITEM,
                        Prefs.KEY_COLOR,
                        Prefs.KEY_QTY_NUMBER,
                        Prefs.KEY_ITEM_SIZE,
                        Prefs.KEY_DATE_NAME},
                null, null, null, null,
                Prefs.KEY_DATE_NAME + " DESC");
        if (cursor.moveToFirst()){
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Prefs.KEY_ID))));
                item.setItemName(cursor.getString(cursor.getColumnIndex(Prefs.KEY_BABY_ITEM)));
                item.setItemQuantity(cursor.getInt(cursor.getColumnIndex(Prefs.KEY_QTY_NUMBER)));
                item.setItemColor(cursor.getString(cursor.getColumnIndex(Prefs.KEY_COLOR)));
                item.setItemSize(cursor.getInt(cursor.getColumnIndex(Prefs.KEY_ITEM_SIZE)));
                //convert time stamp to readable
                DateFormat dateFormat = DateFormat.getDateInstance();
                String formatedDate = dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Prefs.KEY_DATE_NAME))).getTime());
                item.setDateItemAdded(formatedDate);
                items.add(item);
            }while (cursor.moveToNext());
        }
        return items;
    }

    public int updateItem(Item item){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Prefs.KEY_BABY_ITEM, item.getItemName());
        values.put(Prefs.KEY_COLOR, item.getItemColor());
        values.put(Prefs.KEY_QTY_NUMBER, item.getItemQuantity());
        values.put(Prefs.KEY_ITEM_SIZE, item.getItemSize());
        values.put(Prefs.KEY_DATE_NAME, java.lang.System.currentTimeMillis());

        return  db.update(Prefs.TABLE_NAME, values, Prefs.KEY_ID + "=?", new String[]{String.valueOf(item.getId())});
    }

    public void deleteItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Prefs.TABLE_NAME, Prefs.KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public int getItemsCount() {
        String count_query = "SELECT * FROM "+ Prefs.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(count_query, null);
        return cursor.getCount();
    }

}
