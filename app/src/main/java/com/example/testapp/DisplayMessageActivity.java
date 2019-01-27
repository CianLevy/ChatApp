package com.example.testapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


public class DisplayMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*setContentView(R.layout.activity_display_message);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        TextView textView = findViewById(R.id.textView);
        textView.setText(message);
        */
        Intent intent = getIntent();
        String connectionData = intent.getStringExtra("ADDRESS_DATA");
        String[] splitData = connectionData.split(":");

        Client c = new Client(splitData[0], Integer.valueOf(splitData[1]));
        Thread clientThread = new Thread(c);
        clientThread.start();

        //String result = c.connect(Integer.valueOf(splitData[1]), splitData[0]) ? "Connected to server: " + connectionData : "Failed to connect to server";

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","Connected to server");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void displayText(String s){

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result",s);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
