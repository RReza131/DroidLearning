package breza.addressbookapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ListActivity { //extends list activity since thats what this is going to be used for

    Intent intent;
    TextView contactId;

    DbTools dbTools = new DbTools(this);

    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState); //get any save data if there is any
        setContentView(R.layout.activity_main); //References ActivityMain.xml
        ArrayList<HashMap<String,String>> contactList = dbTools.getAllContacts(); //get all data from database and store in array list

        if(contactList.size() !=0){
            ListView listView = getListView();
            listView.setOnClickListener(new OnItemClickListener(){

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    contactId = (TextView) view.findViewById(R.id.contactId);

                    String contactIdValue = contactId.getText().toString();

                    Intent theIntent = new Intent(getApplication(), EditContact.class); //it will call edit contacts

                    theIntent.putExtra("contactId", contactIdValue); //and pass it the contact ID

                    startActivity(theIntent);
                }
            });

            ListAdapter adapter = new SimpleAdapter( MainActivity.this, contactList,R.layout.contact_entry, //ListAdapter used to toggle information between the list view and the list views data and the list adapter
             new String[] {"contactId", "lastName", "firstName"}, //SimpleAdapter connect the data from the array list to the XML file
             new int[] {R.id.contactId,R.id.lastName, R.id.firstName});

             setListAdapter(adapter); //cursor for the list view that accesses the database data

             }

        }

    public void showAddContact(View view){
        Intent theIntent = new Intent(getApplication(), NewContacts.class);
        startActivity(theIntent);
    }


}
