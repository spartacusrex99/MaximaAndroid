package com.minima.maximaandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.minima.utils.MinimaLogger;
import org.minima.utils.RPCClient;
import org.minima.utils.json.JSONArray;
import org.minima.utils.json.JSONObject;
import org.minima.utils.json.parser.JSONParser;
import org.minima.utils.json.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static String MINIMA_HOST = "http://10.0.2.2:9002/";

    ListView mMainList;

    ArrayList<Contact> mContacts = new ArrayList<>();

    private String mNewContactAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMainList = findViewById(R.id.maxima_list);

        mMainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                //Get the contact..
                Contact contact = mContacts.get(position);

                //Now open a Contact Activity
                Intent intent = new Intent(MainActivity.this, ContactActivity.class);
                intent.putExtra("contact", contact);
                startActivityForResult(intent,0);
            }
        });

        updateContactList();

        FloatingActionButton fab = findViewById(R.id.fab_maxima);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewContactDialog();
            }
        });
    }

    public void showNewContactDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new contact");

        // Set up the input
        final EditText input = new EditText(this);

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNewContactAddress = input.getText().toString().trim();

                if(mNewContactAddress.equals("")){
                    return;
                }

                Toast.makeText(MainActivity.this,"Adding contact.. please wait..", Toast.LENGTH_SHORT).show();

                addContact(mNewContactAddress);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void addContact(String zAddress){
        Runnable add = new Runnable() {
            @Override
            public void run() {
                //Tell Minima..
                try{
                    String result = RPCClient.sendGET(MainActivity.MINIMA_HOST+"maxcontacts action:add contact:"+mNewContactAddress);

                    MinimaLogger.log(result);

                    //Small pause..
                    Thread.sleep(5000);

                    //And Update the List
                    updateContactList();

                }catch(Exception exc){
                    MinimaLogger.log(exc);
                }
            }
        };

        Thread tt = new Thread(add);
        tt.start();
    }

    public void updateContactList(){

        Runnable update = new Runnable() {
            @Override
            public void run() {

                ArrayList<Contact> contacts = new ArrayList<>();

                try {
                    //Do the RPC call..
                    String maxcontacts = RPCClient.sendGET(MINIMA_HOST+"maxcontacts");

                    //Convert JSON
                    JSONObject json         = (JSONObject)new JSONParser().parse(maxcontacts);
                    JSONObject response     = (JSONObject)json.get("response");
                    JSONArray allcontacts   = (JSONArray) response.get("contacts");

                    //Convert these..
                    for(Object obj : allcontacts){

                        JSONObject jconcontact = (JSONObject)obj;

                        Contact newcontact = new Contact(jconcontact);
                        contacts.add(newcontact);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Get the array list
                Contact[] allcontacts = contacts.toArray(new Contact[0]);

                //Keep for later
                mContacts = contacts;

                //Create the custom arrayadapter
                ContactAdapter adapter = new ContactAdapter(MainActivity.this, R.layout.contact_view, allcontacts);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMainList.setAdapter(adapter);
                    }
                });
            }
        };

        Thread tt = new Thread(update);
        tt.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_mydetails:

                //Show your details
                Intent intent = new Intent(MainActivity.this, MyDetailsActivity.class);
                startActivity(intent);

                return true;

            case R.id.menu_refresh:
                Toast.makeText(this,"Refreshing Contact List", Toast.LENGTH_SHORT).show();

                updateContactList();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult (int requestCode,
                                     int resultCode,
                                     Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Have we deleted a contact
        if(resultCode == 99){
            updateContactList();
        }
    }

}