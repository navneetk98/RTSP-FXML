package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;


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
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 445));
        primaryStage.show();

        //Create a Client object
        Client theClient = new Client();

        //get server RTSP port and IP address from the command line
        //------------------
        int RTSP_server_port = 8554; //Integer.parseInt(argv[1]);
        String ServerHost ="127.0.0.1"; //argv[0];
        InetAddress ServerIPAddr = InetAddress.getByName(ServerHost);

        //get video filename to request:
        VideoFileName = "/home/gamerstation/Code/rtsp_server/media/movie.mjpeg"  ; //argv[2];

        //Establish a TCP connection with the server to exchange RTSP messages
        //------------------
// theClient.RTSPsocket = new Socket(ServerIPAddr, RTSP_server_port);


        theClient.RTSPsocket = new Socket("127.0.0.1", 8554);

        //Set input and output stream filters:
        RTSPBufferedReader = new BufferedReader(new InputStreamReader(theClient.RTSPsocket.getInputStream()) );
        RTSPBufferedWriter = new BufferedWriter(new OutputStreamWriter(theClient.RTSPsocket.getOutputStream()) );

        //init RTSP state:
        state = INIT;
    }
    public Client()
    {
        timer = new Timer(20, new timerListener());
        timer.setInitialDelay(0);
        timer.setCoalesce(true);

        //allocate enough memory for the buffer used to receive data from the server
        buf = new byte[15000];
    }


    public static void main(String[] args) {
        launch(args);
    }
}
