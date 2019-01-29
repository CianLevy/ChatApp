package com.example.testapp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.Vector;

public class Server extends Thread {

    static Vector<ClientHandler> ar = new Vector<>();
    private boolean exit;
    private int port;
    public static String status;
    public static boolean updateStatus;
    static int i = 0;

    public Server(int port_){
        updateStatus = false;
        this.port = port_;
    }

    @Override
    public void run()
    {
        exit = false;

        ServerSocket ss;

        try {
            ss = new ServerSocket(port);
            Socket s;
            status = "Server: Running on port: " + String.valueOf(port) + "\n";
            updateStatus = true;

            while (!exit) {

                s = ss.accept();

                status = "Server: New client request received from " + s.getInetAddress().getHostAddress() + ":" + s.getPort() + "\n";
                updateStatus = true;


                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());

                System.out.println("Creating a new handler for this client...");


                ClientHandler mtch = new ClientHandler(s,"client " + i, dis, dos);


                Thread t = new Thread(mtch);

                System.out.println("Adding this client to active client list");


                ar.add(mtch);


                t.start();


                i++;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopExecuting() throws IOException {
        exit = true;

        for (ClientHandler c : ar) {
            c.stopExecuting();
        }

        status = "Server: Server closed.\n";
        updateStatus = true;
    }
}

class ClientHandler implements Runnable {

    private String name;
    final DataInputStream dis;
    final DataOutputStream dos;
    private Socket s;
    boolean isloggedin;
    private boolean exit;

    public ClientHandler(Socket s, String name, DataInputStream dis, DataOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.name = name;
        this.s = s;
        this.isloggedin=true;
    }

    @Override
    public void run() {
        exit = false;

        String received;
        while (!exit)
        {
            try {
                received = dis.readUTF();

                System.out.println(received);

                if(received.equals("logout")){
                    this.isloggedin=false;
                    this.s.close();
                    break;
                }

                for (ClientHandler mc : Server.ar) {
                    mc.dos.writeUTF(this.name+" : "+ received);

                }
            } catch (IOException e) {

                e.printStackTrace();
            }

        }
        try {
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void stopExecuting() {
        exit = true;
        this.isloggedin=false;

        try {
            this.s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}