package com.example.mosta.mserver;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    // Declaration for the attributes
    final Handler handler = new Handler();
    private TextView textFromTheClint;
    private Button Start;
    private Button Stop;
    private boolean end = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Start = findViewById(R.id.start);
        Stop = findViewById(R.id.stop);
        textFromTheClint = findViewById(R.id.textFromTheClint);

        Start.setOnClickListener(this);
        Stop.setOnClickListener(this);



    }


    // create a ServerSocket method to control the server
    private void startServerSocket() {

        final Thread thread = new Thread(new Runnable() {

            private String stringData = null;

            @Override
            public void run() {

                try {
                    // create a ServerSocket object
                    ServerSocket ss = new ServerSocket(7777);

                    // create a while loop to get all the massage from the clint
                    while (!end) {

                        // Socket that we declare it before in clint app ( project )
                        Socket s = ss.accept();
                        // create a buffering object to read the text ( massage ) that send by the clint
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        PrintWriter output = new PrintWriter(s.getOutputStream());

                        // we use a stringDate to read text that coming from the clint
                        stringData = input.readLine();
                        output.println(stringData.toUpperCase());
                        output.flush();

                        try {
                            // create a thread.sleep method to pause the main thread.execution
                            // we put it in try _ catch method to avoid the app from crashing
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //  we use updateUI method to close ( end ) incoming text
                        updateUI(stringData);
                        if (stringData.equalsIgnoreCase("STOP")) {
                            end = true;
                            output.close();
                            s.close();
                            break;
                        }
                        output.close();
                        s.close();
                    }
                    ss.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    // create an updateUI with the massage from the clint
    private void updateUI(final String stringData) {
        handler.post(new Runnable() {
            @Override
            public void run() {

                String s = textFromTheClint.getText().toString();
                if (stringData.trim().length() != 0) {
                    textFromTheClint.setText(s +"\n"+ "clint :" + stringData);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {

        // we use switch cases to pursuance the click on both buttons
        switch (v.getId()) {
            case R.id.start:

                startServerSocket();
                Start.setEnabled(false);
                Stop.setEnabled(true);
                break;


            case R.id.stop:

                Stop.setEnabled(false);
                Start.setEnabled(true);
                break;

        }


    }
}

