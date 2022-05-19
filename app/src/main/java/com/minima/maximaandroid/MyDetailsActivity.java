package com.minima.maximaandroid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.minima.utils.MinimaLogger;
import org.minima.utils.RPCClient;
import org.minima.utils.json.JSONObject;
import org.minima.utils.json.parser.JSONParser;

import java.util.Date;

public class MyDetailsActivity extends AppCompatActivity {

    EditText mName;
    String mNameString = "";

    Button mUpdate;

    TextView mContact;
    String mContactString = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mydetails);

        mName       = findViewById(R.id.mydetails_name);
        mUpdate     = findViewById(R.id.mydetails_updatename);
        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if no view has focus:
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                updateName();
            }
        });

        mContact    = findViewById(R.id.mydetails_contact);

        setDetails();
    }

    public void updateName(){

        Toast.makeText(this,"Updating Maxima Name..",Toast.LENGTH_SHORT).show();

        Runnable update = new Runnable() {
            @Override
            public void run() {

                //Get the new name..
                String name = mName.getText().toString().trim();

                //Call the Maxima function..
                try{

                    //Do the RPC call..
                    String maxupdate = RPCClient.sendGET(MainActivity.MINIMA_HOST+"maxcontacts action:myname name:\""+name+"\"");

                    MinimaLogger.log(maxupdate);

                }catch(Exception exc){
                    MinimaLogger.log(exc);
                }
            }
        };

        Thread tt = new Thread(update);
        tt.start();
    }

    public void setDetails(){

        Runnable set = new Runnable() {
            @Override
            public void run() {

                //Call Maxima..
                try{

                    //Do the RPC call..
                    String maxdetails = RPCClient.sendGET(MainActivity.MINIMA_HOST+"maxima");

                    JSONObject json         = (JSONObject)new JSONParser().parse(maxdetails);
                    JSONObject response     = (JSONObject)json.get("response");

                    mNameString             = (String) response.get("name");
                    mNameString = mNameString.replace("\"", "");
                    mNameString = mNameString.replace("'", "");
                    mNameString = mNameString.replace(";", "");

                    mContactString          = (String) response.get("contact");

                    Runnable ui = new Runnable() {
                        @Override
                        public void run() {
                            mName.setText(mNameString);
                            mContact.setText(mContactString);
                        }
                    };

                    runOnUiThread(ui);

                }catch(Exception exc){
                    MinimaLogger.log(exc);
                }
            }
        };

        Thread tt = new Thread(set);
        tt.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mydetails, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_share:

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, mContactString);
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, null);
                startActivity(shareIntent);

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
