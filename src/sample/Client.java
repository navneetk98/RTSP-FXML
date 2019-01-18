package sample;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javax.swing.*;
//import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;


public class Client extends Application {

    DatagramPacket rcvdp; //UDP packet received from the server
    DatagramSocket RTPsocket; //socket to be used to send and receive UDP packets
    static int RTP_RCV_PORT = 25000; //port where the client will receive the RTP packets

    Timer timer; //timer used to receive data from the UDP socket
    byte[] buf; //buffer used to store data received from the server

    //RTSP variables
//----------------
//rtsp states
    final static int INIT = 0;
    final static int READY = 1;
    final static int PLAYING = 2;
    static int state; //RTSP state == INIT or READY or PLAYING
    Socket RTSPsocket; //socket used to send/receive RTSP messages
    //input and output stream filters
    static BufferedReader RTSPBufferedReader;
    static BufferedWriter RTSPBufferedWriter;
    static String VideoFileName; //video file to request to the server
    int RTSPSeqNb = 0; //Sequence number of RTSP messages within the session
    int RTSPid = 0; //ID of the RTSP session (given by the RTSP Server)

    final static String CRLF = "\r\n";

    //Video constants:
//------------------
    static int MJPEG_TYPE = 26; //RTP payload type for MJPEG video


    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 445));
        primaryStage.show();

        //Create a Client object
        Client theClient = new Client();

        //get server RTSP port and IP address from the command line
        //------------------
        int RTSP_server_port = 8554; //Integer.parseInt(argv[1]);
        String ServerHost = "127.0.0.1"; //argv[0];
        InetAddress ServerIPAddr = InetAddress.getByName(ServerHost);

        //get video filename to request:
        VideoFileName = "/home/gamerstation/Code/rtsp_server/media/movie.mjpeg"; //argv[2];

        //Establish a TCP connection with the server to exchange RTSP messages
        //------------------
// theClient.RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);


        theClient.RTSPsocket = new Socket("127.0.0.1", 8554);

        //Set input and output stream filters:
        RTSPBufferedReader = new BufferedReader(new InputStreamReader(theClient.RTSPsocket.getInputStream()));
        RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(theClient.RTSPsocket.getOutputStream()));

        //init RTSP state:
        state = INIT;
    }

    public Client() {
        timer = new Timer(20, new timerListener());
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        //allocate enough memory for the buffer used to receive data from the server
        buf = new byte[15000];
    }


    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    public void setup(ActionEvent event) {
        System.out.println("Hello");
        if (state == INIT) {
            //Init non-blocking RTPsocket that will be used to receive data
            try {
                //construct a new DatagramSocket to receive RTP packets from the server, on port RTP_RCV_PORT
                //RTPsocket = ...
                RTPsocket = new DatagramSocket(RTP_RCV_PORT);


                //   set TimeOut value of the socket to 5msec.


                RTPsocket.setSoTimeout(5);
                //set TimeOut value of the socket to 5msec.
                //....

            } catch (SocketException se) {
                System.out.println("Socket exception: " + se);
                System.exit(0);
            }

            //init RTSP sequence number
            RTSPSeqNb = 1;

            //Send SETUP message to the server
            send_RTSP_request("SETUP");

            //Wait for the response
            if (parse_server_response() != 200)
                System.out.println("Invalid Server Response");
            else {
                //change RTSP state and print new state
                state = READY;
                System.out.println("New RTSP state: READY");
                //System.out.println("New RTSP state: ....");
            }
        }//else if state != INIT then do nothing
    }

    @FXML
    public void play(ActionEvent e) {
        System.out.println("Play Button pressed !");

        if (state == READY) {


            //increase RTSP sequence number


            RTSPSeqNb++;

//send PLAY message to the server


            send_RTSP_request("PLAY");


//   wait for the response


            if (parse_server_response() != 200)
                System.out.println("Invalid Server Response");
            else {


//   change RTSP state and print out new state


                state = PLAYING;
                System.out.println("New RTSP state: PLAYING");


//   start the timer


                timer.start();
            }
        }
    }

    @FXML
    public void play(ActionEvent e) {
        System.out.println("Play Button pressed !");

        if (state == READY) {


            //increase RTSP sequence number


            RTSPSeqNb++;

//send PLAY message to the server


            send_RTSP_request("PLAY");


//   wait for the response


            if (parse_server_response() != 200)
                System.out.println("Invalid Server Response");
            else {


//   change RTSP state and print out new state


                state = PLAYING;
                System.out.println("New RTSP state: PLAYING");


//   start the timer


                timer.start();
            }
        }
    }

    @FXML
    public void pause(ActionEvent e) {
        {

            //System.out.println("Pause Button pressed !");

            if (state == PLAYING) {
                //increase RTSP sequence number

                //........

                //Send PAUSE message to the server
                send_RTSP_request("PAUSE");

                //Wait for the response
                if (parse_server_response() != 200)
                    System.out.println("Invalid Server Response");
                else {
                    //change RTSP state and print out new state
                    //........
                    //System.out.println("New RTSP state: ...");

                    //stop the timer
                    timer.stop();
                }
            }
        }
    }
    @FXML
    public void stop(ActionEvent e) {
        //System.out.println("Teardown Button pressed !");

        //increase RTSP sequence number
        // ..........


        //Send TEARDOWN message to the server
        send_RTSP_request("TEARDOWN");

        //Wait for the response
        if (parse_server_response() != 200)
            System.out.println("Invalid Server Response");
        else {
            //change RTSP state and print out new state
            //........
            //System.out.println("New RTSP state: ...");

            //stop the timer
            timer.stop();

            //exit
            System.exit(0);
        }
    }

}
