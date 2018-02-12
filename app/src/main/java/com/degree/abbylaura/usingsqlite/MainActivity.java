package com.degree.abbylaura.usingsqlite;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.util.Log;
import android.database.Cursor;
import java.io.File;


public class MainActivity extends AppCompatActivity {

    //create SQLite databse
    SQLiteDatabase contactsDB = null;

    //create all of our buttons
    Button createDBButton, addContactButton, deleteContactButton,
            getContactsButton, deleteDBButton;

    EditText nameEditText, emailEditText, contactListEditText, idEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialise all our buttons
        createDBButton = (Button) findViewById(R.id.createDBButton);
        addContactButton = (Button) findViewById(R.id.addContactButton);
        deleteContactButton = (Button) findViewById(R.id.deleteContactButton);
        getContactsButton = (Button) findViewById(R.id.getContactsButton);
        deleteDBButton = (Button) findViewById(R.id.deleteDBButton);

        //initialise edittext boxes
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        contactListEditText = (EditText) findViewById(R.id.contactListEditText);
        idEditText = (EditText) findViewById(R.id.idEditText);

    }

    public void createDatabase(View view) {

        try{
            //either open old db or create new
            contactsDB = this.openOrCreateDatabase("MyContacts",
                    MODE_PRIVATE, null); //null can be a db error handler to handle db errors

            //execute sql statement that isnt select
            contactsDB.execSQL("CREATE TABLE IF NOT EXISTS contacts " +
                    "(id integer primary key, name VARCHAR, email VARCHAR);");

            // The database on the file system
            File database = getApplicationContext().getDatabasePath("MyContacts.db");

            // Check if the database exists
            if (database.exists()) {
                Toast.makeText(this, "Database Created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Database Missing", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e){
            Log.e("CONTACTS ERROR", "Error creating database");
        }

        //now make all buttons clickable (as we set clickable to false in .xml file)
        addContactButton.setClickable(true);
        deleteContactButton.setClickable(true);
        getContactsButton.setClickable(true);
        deleteDBButton.setClickable(true);
    }

    public void addContact(View view) {

        String contactName = nameEditText.getText().toString();

        String contactEmail = emailEditText.getText().toString();

        //now input above info into db
        contactsDB.execSQL("INSERT INTO contacts (name, email) VALUES ('" +
                contactName + "', '" + contactEmail + "');");


    }

    public void deleteContact(View view) {

        String id = idEditText.getText().toString();

        contactsDB.execSQL("DELETE FROM contacts WHERE id = " + id + ";");

    }

    public void getContacts(View view) {

        // A Cursor provides read and write access to database results
        Cursor cursor = contactsDB.rawQuery("SELECT * FROM contacts", null);

        int idColumn = cursor.getColumnIndex("id");
        int nameColumn = cursor.getColumnIndex("name");
        int emailColumn = cursor.getColumnIndex("email");

        cursor.moveToFirst(); //move to first row of results

        String contactList = "";

        //verify we actaully have reuslts
        if(cursor != null && (cursor.getCount() > 0)){

            //generate contact list
            do{
                String id = cursor.getString(idColumn);
                String name = cursor.getString(nameColumn);
                String email = cursor.getString(emailColumn);

                contactList = contactList + id + " : " + name + " : " + email + "\n";

            } while(cursor.moveToNext());

            contactListEditText.setText(contactList);
        }else { //if we got no results

            Toast.makeText(this, "No results to show",
                    Toast.LENGTH_SHORT).show();

            contactListEditText.setText("");

        }
    }



    public void deleteDatabase(View view) {

        this.deleteDatabase("MyContacts");

    }

    @Override
    protected void onDestroy() { //close DB if app is shutdown
        contactsDB.close();

        super.onDestroy();
    }
}
