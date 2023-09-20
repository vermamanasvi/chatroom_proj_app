import java.net.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.BorderLayout;
import java.awt.Font;
import java.io.*;

public class Client extends JFrame{


    Socket socket;
    BufferedReader br;//input stream
    PrintWriter out; //output stream

    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto",Font.PLAIN,20);

    public Client(){
        try{
            System.out.println("Sending request to server");
            socket = new Socket("127.0.0.1",7777);
            System.out.println("Connection done.");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
            //to fetch output
            out = new PrintWriter(socket.getOutputStream());


            createGUI();

            handleEvents();
            startReading();
            startWriting();
        }catch(Exception e){
            
        }
    }
    private void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'keyPressed'");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'keyReleased'");
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'keyTyped'");
            }
            
        });
    }

    private void createGUI(){
        this.setTitle("Client Messager[END]");
        this.setSize(600,700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);


        //coding for component 
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);
        heading.setIcon(new ImageIcon("/icons/icons8-message-100.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);


        //frame layout set
        this.setLayout(new BorderLayout());
        //adding components to frame
        this.add(heading,BorderLayout.NORTH);
        this.add(messageArea,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);

        heading.setLayout(new BorderLayout());
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