package breza.addressbookapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.HashMap;

/**
 * Created by breza on 7/10/2014.
 */
public class NewContacts extends Activity {

    //fields correspond to add_new_contact.xml
    EditText firstName;
    EditText lastName;
    EditText phoneNumber;
    EditText emailAddress;
    EditText homeAddress;

    DbTools dbTools = new DbTools(this);

    public void onCreate(Bundle saveInstanceState){

        super.onCreate(saveInstanceState);
        setContentView(R.layout.add_new_contact);

        firstName = (EditText) findViewById(R.id.firstNameEditText);
        lastName = (EditText) findViewById(R.id.firstNameEditText);
        phoneNumber = (EditText) findViewById(R.id.phoneNumberEditText);
        emailAddress = (EditText) findViewById(R.id.emailEditText);
        homeAddress = (EditText) findViewById(R.id.addressEditText);
    }

    public void addNewContact(View view){
        HashMap<String, String> queryValuesMap = new HashMap<String, String>();

        queryValuesMap.put("firstName", firstName.getText().toString());
        queryValuesMap.put("lastName", lastName.getText().toString());
        queryValuesMap.put("phoneNumber", phoneNumber.getText().toString());
        queryValuesMap.put("emailAddress", emailAddress.getText().toString());
        queryValuesMap.put("homeAddress", homeAddress.getText().toString());

        dbTools.insertContact(queryValuesMap);

        this.callMainActivity(view);
    }

    public void  callMainActivity(View view){

        Intent theIntent = new Intent(getApplication(), MainActivity.class)
        startActivity(theIntent);

    }

}
