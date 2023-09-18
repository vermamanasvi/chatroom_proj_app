import java.net.*;
import java.io.*;

class Client{


    Socket socket;
    BufferedReader br;//input stream
    PrintWriter out; //output stream

    public Client(){
        try{
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1",7777);
            System.out.println("Connection done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
            //to fetch output
            out = new PrintWriter(socket.getOutputStream());

            startReading();
            startWriting();
        }catch(Exception e){
            
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
                            System.out.println("Server terminated chat");
                            socket.close();
                            break;
                        }
                        System.out.println("Server : " + msg);   
                    }
              
            }catch(Exception e){
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
                System.out.println("Connection closed");
            }catch (Exception e){
                    e.printStackTrace();
                }
            };
            new Thread(r2).start();
    }


    public static void main(String[] args){
        System.out.println("This is Client.");
        new Client();
    }
}