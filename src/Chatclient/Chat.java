package Chatclient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Chat {

    JTextArea incoming;
    JTextField outgoing;
    BufferedReader reader;
    PrintWriter writer;
    Socket sock;

    public void go(){
        JFrame frame = new JFrame("IGEC 专用聊天室");
        JPanel mainPanel = new JPanel();
        incoming = new JTextArea("欢迎使用IGEC 专属聊天室\n " +"作者 乔帅 利叶\n" ,15,50);
        incoming.setLineWrap(true);
        incoming.setWrapStyleWord(true);
        incoming.setEditable(false);
        incoming.setFont(new Font("Arial", Font.PLAIN, 16));
        JScrollPane qScroller = new JScrollPane(incoming);
        qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        outgoing = new JTextField(20);
        JButton sendButton = new JButton("send");
        sendButton.addActionListener(new SendButtonListener());
        outgoing.addKeyListener(new SendButtonListener());
        mainPanel.add(qScroller);
        mainPanel.add(outgoing);
        mainPanel.add(sendButton);
        setNetworking();

        Thread readerThread = new Thread(new IncomingReader());
        readerThread.start();

        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);

        frame.setSize(800,500);
        frame.setVisible(true);
    }

    public void setNetworking(){
        try{
            sock = new Socket("49.51.136.100", 5000);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream(),"UTF-8");
            reader = new BufferedReader(streamReader);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(sock.getOutputStream(),"UTF-8");
            writer = new PrintWriter(outputStreamWriter);
            System.out.println("networking established");
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public class SendButtonListener implements ActionListener,KeyListener {
        public void actionPerformed(ActionEvent ev){
            try{
                writer.println(outgoing.getText());
                writer.flush();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            outgoing.setText("");
            outgoing.requestFocus();
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            try {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    writer.println(outgoing.getText());
                    writer.flush();

                    outgoing.setText("");
                    outgoing.requestFocus();
                }
            }catch (Exception ex){ex.printStackTrace();}
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    public class IncomingReader implements Runnable{
        public void run(){
            String message;
            try {
                while((message = reader.readLine())!= null){
                    System.out.println("read "+message);
                   // incoming.setText(message + "\n");
                    incoming.append(message + "\n");
                }
            }catch (Exception ex){ex.printStackTrace();}
        }
    }
    public static void main(String[] args) {
        Chat client = new Chat();
        client.go();
    }
}
