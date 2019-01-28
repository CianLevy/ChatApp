package com.example.testapp;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";
    private static Server server;
    Client c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        c = new Client("null", 0);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialiseUI();



        Thread readMessage = new Thread(new Runnable()
        {
            TextView chatBox = findViewById(R.id.editText4);
            Button connectButton = findViewById(R.id.button2);
            Server s = new Server(0);

            @Override
            public void run() {
                Looper.prepare();

                while (true) {
                    if (c.bufferState == 2){
                        c.bufferState = 0;

                        chatBox.append(c.buffer);

                    }
                    if (s.updateStatus){
                        s.updateStatus = false;

                        if (s.status != null && !s.status.isEmpty())
                            chatBox.append(s.status);
                    }

                    if (c.connectionStatus && !(connectButton.getText().toString()).equals("Disconnect")){
                        setButtonText(connectButton, "Disconnect");
                    }


                }
            }
        });

        readMessage.start();



    }

    public void sendMessage(View view){
        EditText editText = (EditText) findViewById(R.id.multiAutoCompleteTextView);
        String message = editText.getText().toString() + "\n";

        editText.setText("");


        c.buffer = message;
        c.bufferState = 1;
        System.out.println(message);
    }

    public void connectToServer(View view){
        if (!c.connectionStatus) {
            Intent i = new Intent(this, DisplayMessageActivity.class);

            TextView textView = findViewById(R.id.editText3);
            String ip = textView.getText().toString();

            textView = findViewById(R.id.editText2);
            String port = textView.getText().toString();

            String connectionData = ip + ":" + port;

            i.putExtra("ADDRESS_DATA", connectionData);
            startActivityForResult(i, 1);
        }
        else{
            c.stopExecuting();

            Button b = findViewById(R.id.button2);
            setButtonText(b, "Connect");
        }

    }

    void initialiseUI(){
        //Determine the IP of the device
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

        //Update the IP textView to display the device's IP
        TextView textView = findViewById(R.id.editText3);
        textView.setText(ip);

        textView = findViewById(R.id.editText4);
        textView.setFocusable(false);
        //textView.setInputType(0);

        Switch hostSW = (Switch)findViewById(R.id.switch1);
        hostSW.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TextView textView = findViewById(R.id.editText3);
                if (isChecked) {
                    textView = findViewById(R.id.editText2);
                    int port = Integer.valueOf(textView.getText().toString());
                    server = new Server(port);
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
                TextView textView = findViewById(R.id.editText4);
                textView.append(result);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void setButtonText(final Button b,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                b.setText(value);
            }
        });
    }
}


