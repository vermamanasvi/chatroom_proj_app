import java.net.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import ai.openai.ChatCompletion;
import ai.openai.ChatCompletionResponse;

public class Server extends JFrame {
    ServerSocket server;
    Socket socket;
    BufferedReader br; // Input stream
    PrintWriter out; // Output stream

    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN, 20);

    private JRadioButton chatGptRadio;
    private JRadioButton chatClientRadio;

    private boolean chattingWithGpt = false;

    // Replace with your OpenAI API key
    private String apiKey = "YOUR_OPENAI_API_KEY";
    private String chatGptEngine = "text-davinci-002"; // Current model of ChatGPT API

    public Server() {
        try {
            server = new ServerSocket(7777);
            System.out.println("Waiting for a client...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            handleEvents();
            startReading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String contentToSend = messageInput.getText();
                    messageArea.append("Me: " + contentToSend + "\n");

                    if (chattingWithGpt) {
                        // Send user's message to ChatGPT API
                        String response = chatWithGpt(contentToSend);
                        messageArea.append("ChatGPT: " + response + "\n");
                    } else {
                        // Send user's message to the client
                        out.println(contentToSend);
                        out.flush();
                    }

                    messageInput.setText("");
                    messageInput.requestFocus();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {}
        });

        chatGptRadio.addActionListener(e -> {
            chattingWithGpt = true;
            messageArea.append("Now chatting with ChatGPT...\n");
        });

        chatClientRadio.addActionListener(e -> {
            chattingWithGpt = false;
            messageArea.append("Now chatting with the client...\n");
        });
    }

    private void createGUI() {
        this.setTitle("Server Messager");
        this.setSize(600, 700);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);

        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setHorizontalTextPosition(SwingConstants.CENTER);
        heading.setVerticalTextPosition(SwingConstants.BOTTOM);
        heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        messageArea.setEditable(false);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);

        chatGptRadio = new JRadioButton("Chat with ChatGPT");
        chatGptRadio.setFont(font);
        chatGptRadio.setHorizontalAlignment(SwingConstants.CENTER);

        chatClientRadio = new JRadioButton("Chat with Client");
        chatClientRadio.setFont(font);
        chatClientRadio.setHorizontalAlignment(SwingConstants.CENTER);
        chatClientRadio.setSelected(true);

        ButtonGroup radioGroup = new ButtonGroup();
        radioGroup.add(chatGptRadio);
        radioGroup.add(chatClientRadio);

        this.setLayout(new BorderLayout());
        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);
        this.add(chatClientRadio, BorderLayout.SOUTH);
        this.add(chatGptRadio, BorderLayout.SOUTH);
        heading.setLayout(new BorderLayout());
        this.setVisible(true);
    }

    public void startReading() {
        Runnable r1 = () -> {
            System.out.println("Reader started");
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated chat");
                        JOptionPane.showMessageDialog(this, "Client Terminated the chat");
                        messageInput.setEnabled(false);
                        socket.close();
                        break;
                    }
                    messageArea.append("Client: " + msg + "\n");
                }
            } catch (Exception e) {
                System.out.println("Connection closed");
            }
        };
        new Thread(r1).start();
    }

    private String chatWithGpt(String message) {
        try {
            ChatCompletion chatCompletion = new ChatCompletion(apiKey);
            String chatPrompt = "You: " + message + "\nChatGPT:";
            String response = chatCompletion.complete(chatPrompt, chatGptEngine);
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return "ChatGPT Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        System.out.println("This is the server. Waiting for a client...");
        new Server();
    }
}
