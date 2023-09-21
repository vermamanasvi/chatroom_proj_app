import java.net.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
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

    //Declaring componenets
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

           // startWriting();
        }catch(Exception e){
            
        }
    }
    private void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // TODO Auto-generated method stub
                if(e.getKeyCode()==10){
                    // System.out.println("you have pressed enter button");
                    String contentToSend=messageInput.getText();
                    messageArea.append("Me: " + contentToSend+"\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
                // TODO Auto-generated method stub
                
            }
            
        });
    }

    private void createGUI(){
        this.setTitle("Client Messager"); 
        this.setSize(600,700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);


        //coding for component 
        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setIcon(new ImageIcon("icons8-message-100.png"));
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);      
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);


        //frame layout set
        this.setLayout(new BorderLayout());
        //adding components to frame
        this.add(heading,BorderLayout.NORTH);
        JScrollPane jScrollPane=new JScrollPane(messageArea);
        this.add(jScrollPane,BorderLayout.CENTER);
        this.add(messageInput,BorderLayout.SOUTH);
        
        heading.setLayout(new BorderLayout());
        this.setVisible(true);
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
                            JOptionPane.showMessageDialog(this, "Server Terminanted the chat");
                            messageInput.setEnabled(false);
                            socket.close();
                            break;
                        }
                        //System.out.println("Server : " + msg);   
                        messageArea.append("Server : "+ msg + "\n");
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