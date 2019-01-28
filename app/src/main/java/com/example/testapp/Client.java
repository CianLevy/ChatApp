package com.example.testapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client implements Runnable
{
    private int ServerPort = 1234;
    private Socket s;
    private String ipText;
    static int bufferState;
    static String buffer;
    static boolean connectionStatus;


    public Client(String ip_, int port_){
        this.ipText = ip_;
        this.ServerPort = port_;
        connectionStatus = false;

    }

    public boolean connect(int serverPort, String ip_){
        try {
            InetAddress ip = InetAddress.getByName(ip_);
            s = new Socket(ip, serverPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

/*
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String connectionData = intent.getStringExtra("ADDRESS_DATA");
        String[] splitData = connectionData.split(":");

       try {
            InetAddress ip = InetAddress.getByName(splitData[0]);
            int serverPort = Integer.valueOf(splitData[1]);

            s = new Socket(ip, serverPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","Connected to server: " + connectionData);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();

    }
    */
    public static void main(String args[]) throws UnknownHostException, IOException
    {
        final Scanner scn = new Scanner(System.in);

        // getting localhost ip
        InetAddress ip = InetAddress.getByName("localhost");

        // establish the connection
        Socket s = new Socket(ip, 1234);

        // obtaining input and out streams
        final DataInputStream dis = new DataInputStream(s.getInputStream());
        final DataOutputStream dos = new DataOutputStream(s.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    // read the message to deliver.
                    String msg = scn.nextLine();

                    try {
                        // write on the output stream
                        dos.writeUTF(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        String msg = dis.readUTF();
                        System.out.println(msg);
                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
            }
        });

        sendMessage.start();
        readMessage.start();

    }

    @Override
    public void run() {
        InetAddress ip = null;

        try {
            ip = InetAddress.getByName(ipText);
            this.s = new Socket(ip, ServerPort);
            connectionStatus = true;
            int scope = 2;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // obtaining input and out streams
        final DataInputStream dis;
        final DataOutputStream dos;

        try {
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

        // sendMessage thread
        Thread sendMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {
                while (true) {

                    if (bufferState == 1){
                        try {
                            bufferState = 0;
                            dos.writeUTF(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }


            }
        });

        // readMessage thread
        Thread readMessage = new Thread(new Runnable()
        {
            @Override
            public void run() {

                while (true) {
                    try {
                        // read the message sent to this client
                        buffer = dis.readUTF();
                        bufferState = 2;

                    } catch (IOException e) {

                        e.printStackTrace();
                    }
                }
                }
        });

            sendMessage.start();
            readMessage.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}