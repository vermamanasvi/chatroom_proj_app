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
            System.out.println("waiting...");
            socket=server.accept();

            //extracting input stream from socket; giving it to inputstream which will change the byte data received into character;
            //bufferhandling will be done by BufferedReader; which will be then passed on to the var br. => br var will be used for input reading now on
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
            //to fetch output
            out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    } 


    public void startReading(){
        //thread 1: to read data(
        Runnable r1=()->{ //lambda expression
            System.out.println("reader started");
            try{
                while(true){
                
                        String msg = br.readLine();
                        if(msg.equals("exit")){
                            System.out.println("client terminated chat");
                            socket.close(); 
                            break;
                        }
                        System.out.println("Client : " + msg);       
                }
            }catch(Exception e){
                // e.printStackTrace();
                 System.out.println("connection closed");
            }
        };
        new Thread(r1).start();//thread obj intialization
    }


    public void startWriting(){
        //thread 2: user intake data and send it to client
        System.out.println("typing...   ");
        Runnable r2=()->{
            try{
                while(true && !socket.isClosed()){
                        //we have to write data on server; for that we have to take data from console
                        BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
                        String content = br1.readLine(); 
                        
                        out.println(content);
                        out.flush();
                        if(content.equals("exit")){
                            socket.close();
                            break;
                        }

                }
            }catch(Exception e){
                System.out.println("connection closed");
            }
             System.out.println("connection closed");
        };

        new Thread(r2).start();
    }


    public static void  main(String[] args){
        System.out.println("This is server going to start server");
        new Server();//calling constructor
    }
}