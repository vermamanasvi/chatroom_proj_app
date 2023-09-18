import java.net.*;
import java.io.*;

class Server{//constructor

    ServerSocket server;
    Socket socket;
    BufferedReader br;//input stream
    PrintWriter out; //output stream


    public Server(){
        try{
            server = new ServerSocket(7777);
            System.out.println("wating...");
            socket=server.accept();

            //extracting input stream from socket; giving it to inputstream which will change the byte data received into character;
            //bufferhandling will be done by BufferedReader; which will be then passed on to the var br. => br var will be used for input reading now on
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
            //to fetch output
            out = new PrintWriter(socket.getOutputStream());



        }
        catch(Exception e){
            e.printStackTrace();
        }
    } 

    public static void  main(String[] args){
        System.out.println("This is server going to start server");
        new Server();//calling constructor
    }
}