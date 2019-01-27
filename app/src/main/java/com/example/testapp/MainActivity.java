package com.example.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseUI();
    }

    public void sendMessage(View view){
        /*Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);


        startActivityForResult(intent, 1);
        */

    }

    public void connectToServer(View view){
        Intent i = new Intent(this, DisplayMessageActivity.class);

        TextView textView = findViewById(R.id.editText3);
        String ip = textView.getText().toString();

        textView = findViewById(R.id.editText2);
        String port = textView.getText().toString();

        String connectionData = ip + ":" + port;

        i.putExtra("ADDRESS_DATA", connectionData);
        startActivityForResult(i, 1);

    }

    void initialiseUI(){
        //Determine the IP of the device
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        //Update the IP textView to display the device's IP
        TextView textView = findViewById(R.id.editText3);
        textView.setText(ip);

        Switch hostSW = (Switch)findViewById(R.id.switch1);
        hostSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView textView = findViewById(R.id.editText3);
                if (isChecked) {
                    server = new Server();
                    server.start();

                }
                else {
                    server.stopExecuting();

                }

            }

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                TextView textView = findViewById(R.id.editText3);
                textView.setText(result);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}


