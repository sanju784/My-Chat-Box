package application;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MyServer extends JFrame {
  private JTextField userText;
  private JTextArea chatWindow;
  private ObjectOutputStream output;
  private ObjectInputStream input;
  private ServerSocket server;
  private Socket connection;
  
  //constructor
  public MyServer() {
	super("My Chat Box");
	userText = new JTextField();
	userText.setEditable(false);
	userText.addActionListener(
	  new ActionListener() {
		  public void actionPerformed(ActionEvent event) {
			  sendMessage(event.getActionCommand());
			  userText.setText("");
		  }
	  }
	);
	add(userText, BorderLayout.NORTH);
	chatWindow = new JTextArea();
	add(new JScrollPane(chatWindow));
	setSize(300, 150);
	setVisible(true);
  }
  
  //set up and run the server
  public void startRunning() {
	  try {
		  server = new ServerSocket(6789, 100);
		  while(true) {
			  try {
				  waitForConnection();
				  setupStreams();
				  whileChatting();
			  } catch(EOFException eof) {
				  showMessage("\nUnable to connect to the Server");
			  } finally {
				  closeAll();
			  }
		  }
	  } catch(IOException ioe) {
		  ioe.printStackTrace();
	  }
  }
  
  //wait for the connection and then display the connection detail
  private void waitForConnection() throws IOException {
	  showMessage(" Waiting for someone to connect... \n");
	  connection = server.accept();
	  showMessage(" Now connected to " + connection.getInetAddress(). getHostName());
  }
  
  //get Stream to send and receive data
  private void setupStreams() throws IOException {
	  output = new ObjectOutputStream(connection.getOutputStream());
	  output.flush();
	  input = new ObjectInputStream(connection.getInputStream());
	  showMessage("\n Streams are now setup! \n");
  }
  
  private void whileChatting() throws IOException {
	  String message = "Connected!!! Start Conversation";
	  sendMessage(message);
	  ableToType(true);
	  do{
		  try {
			  message = (String)input.readObject();
			  showMessage("\n" + message);
		  } catch(ClassNotFoundException ce) {
			  showMessage("Unable to read the message");
		  }
	  } while(!message.equals("CLIENT - END")); //The conversation will be ended when user type END
  }
  
  //closing streams and socket after chatting done 
  private void closeAll() {
	  showMessage("\n Closing Connections...\n");
	  ableToType(false);
	  try {
		  output.close();
		  input.close();
		  connection.close();
	  } catch(IOException ioe) {
		  ioe.printStackTrace();
	  }
  }
  
  //send message to the client
  private void sendMessage(String msg) {
	  try {
		  output.writeObject("Server - " + msg);
		  output.flush();
		  showMessage("\n Server - " + msg);
	  } catch(IOException ioe) {
		  chatWindow.append("\n ERROR: Failed to send message.");
	  }
  }
  
  //update chatWindow
  private void showMessage(final String text) {
	  SwingUtilities.invokeLater(
			  new Runnable() {
				  public void run() {
					  chatWindow.append(text);
				  }
			  }
	   );
  }
  
  //allow user to type in chat box
  private void ableToType(final boolean bool) {
	  SwingUtilities.invokeLater(
			  new Runnable() {
				  public void run() {
					  userText.setEditable(bool);
				  }
			  }
	  );
  }
  
}

