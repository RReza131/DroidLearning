package breza.addressbookapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by breza on 7/3/2014.
 */
public class DbTools extends SQLiteOpenHelper {

    public DbTools(Context applicationContext){//context provides access to specific app an classes

        super(applicationContext, "contactbook.db", null, 1); //contact book would be the name of the DB to create
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String query = "CREATE TABLE contacts ( contactID INTEGER PRIMARY KEY, firstName TEXT," +
                "lastName Text, phoneNumber Text, emailAddress Text, homeAddress TEXT)";

        database.execSQL(query); //method will execute SQL statement so long as it is not a select or return any data

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) { //method for dropping or adding to tables

        String query = "DROP TABLE IF EXISTS contacts";
        database.execSQL(query);
        onCreate(database);

    }

    //CREATE
    public void insertContact(HashMap<String,String> queryValues){

        SQLiteDatabase database = this.getWritableDatabase(); //this is a reference to the SQLiteOpenHelper class database

        ContentValues values = new ContentValues(); // ContentValues stores key value pairs, column and values, and is need because the datatype needs to be passed

        values.put("firstName", queryValues.get("firstName"));
        values.put("lastName", queryValues.get("lastName"));
        values.put("phoneNumber", queryValues.get("phoneNumber"));
        values.put("emailAddress", queryValues.get("emailAddress"));
        values.put("homeAddress", queryValues.get("homeAddress"));

        database.insert("contacts", null, values);

        database.close();
    }
    //UPDATE
    public int updateContact(HashMap<String,String> queryValues){

        SQLiteDatabase database = this.getWritableDatabase(); //this is a reference to the SQLiteOpenHelper class database

        ContentValues values = new ContentValues(); // ContentValues stores key value pairs, column and values, and is need because the datatype needs to be passed

        values.put("firstName", queryValues.get("firstName"));
        values.put("lastName", queryValues.get("lastName"));
        values.put("phoneNumber", queryValues.get("phoneNumber"));
        values.put("emailAddress", queryValues.get("emailAddress"));
        values.put("homeAddress", queryValues.get("homeAddress"));

        return database.update("contacts", values,
                "contactID"+ "= ?", //where clause
                new String[] {queryValues.get("contactId")}); //where arguments
    }

    //DELETE
    public void deleteContact(String id){
        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery =  "DELETE FROM contacts WHERE contactId ='" + id + "'";

        database.execSQL(deleteQuery);
    }

    //READ
    public ArrayList<HashMap<String,String>> getAllContacts(){
        ArrayList<HashMap<String,String>> contactArrayList = new ArrayList<HashMap<String,String>>();

        String selectQuery = "SELECT * FROM contacts ORDER BY lastName";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null); // is a cursor really needed? Provide read and write access to the data returned by Query

        if(cursor.moveToFirst()){
            do{
                HashMap<String,String> contactMap = new HashMap<String,String>();

                contactMap.put("contactID", cursor.getString(0));
                contactMap.put("firstName", cursor.getString(1));
                contactMap.put("lastName", cursor.getString(2));
                contactMap.put("phoneNumber", cursor.getString(3));
                contactMap.put("emailAddress", cursor.getString(4));
                contactMap.put("homeAddress", cursor.getString(5));

                contactArrayList.add(contactMap);

            }while (cursor.moveToNext()); //when there is nothing left
        }
        return contactArrayList;
    }
    //Get 1 result instead of returning all
    public HashMap<String,String> getContactInfo(String id){
        HashMap<String,String> contactMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM contacts WHERE contactID ='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null); // is a cursor really needed? Provide read and write access to the data returned by Query
        if(cursor.moveToFirst()){
            do{
                contactMap.put("contactID", cursor.getString(0));
                contactMap.put("firstName", cursor.getString(1));
                contactMap.put("lastName", cursor.getString(2));
                contactMap.put("phoneNumber", cursor.getString(3));
                contactMap.put("emailAddress", cursor.getString(4));
                contactMap.put("homeAddress", cursor.getString(5));

            }while (cursor.moveToNext()); //when there is nothing left
        }
        return contactMap; //dont need the array list because we are just returning 1 result




    }





}
