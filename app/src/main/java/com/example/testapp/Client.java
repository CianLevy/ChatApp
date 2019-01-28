package com.example.testapp;

import java.io.*;
import java.net.*;

public class Client implements Runnable
{
    private int port;
    private Socket s;
    private String ipText;
    static int bufferState;
    static String buffer;
    static boolean connectionStatus;
    private static boolean exit;


    public Client(String ip_, int port_){
        this.ipText = ip_;
        this.port = port_;
        this.exit = false;
        connectionStatus = false;
    }

    @Override
    public void run() {
        InetAddress ip;

        try {
            ip = InetAddress.getByName(ipText);
            this.s = new Socket(ip, port);
            buffer = "Client: Connected to server " + ipText + ":" + String.valueOf(port) + "\n";
            connectionStatus = true;

        } catch (IOException e) {
            e.printStackTrace();
            buffer = "Client: Failed to connect to server\n";
        }
        bufferState = 2; //Only update the buffer flag when the buffer is guaranteed to contain useful data

        final DataInputStream dis;
        final DataOutputStream dos;

        try {
            dis = new DataInputStream(s.getInputStream());
            dos = new DataOutputStream(s.getOutputStream());

            Thread sendMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {
                    while (!exit) {

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

            Thread readMessage = new Thread(new Runnable()
            {
                @Override
                public void run() {

                    while (!exit) {
                        try {
                            buffer = dis.readUTF();
                            bufferState = 2;

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        dis.close();
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            sendMessage.start();
            readMessage.start();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopExecuting() {
        exit = true;
        connectionStatus = false;

        buffer = "Client: Disconnected from server\n";
        bufferState = 2;
    }
}