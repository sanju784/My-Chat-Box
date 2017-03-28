package ClientBox;

import java.awt.BorderLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
	public Client(String host) {
		super("Client Chat Box");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
			 new ActionListener() {
				 public void actionPerformed(ActionEvent ae) {
					 sendMessage(ae.getActionCommand());
					 userText.setText("");
				 }
			 }
		  );
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true);
	}
	
	public void startRunning() {
		try {
			connectToServer();
			setupStreams();
			whileChatting();
		} catch(EOFException e) {
			showMessage("\n Client terminated connection");
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
	}
	
	//connecting to server
	private void connectToServer() throws IOException {
		showMessage("Trying to connect...\n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789);
		showMessage("Connected to: " + connection.getInetAddress().getHostName());
	}
	
	//set up streams to send and receive message
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n You can send messages now!!!");
	}
	
	//while chatting with server
	private void whileChatting() throws IOException {
		ableToType(true);
		do {
			try {
				message = (String) input.readObject();
				showMessage("\n" + message);
			} catch(ClassNotFoundException e) {
				showMessage("\nSorry message cannot be read!!");
			}
		} while(!message.equals("Server - END"));
	}
	
	//close the streams and socket
	private void closeAll() {
		showMessage("\n Closing the chat...");
		ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
		} catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	//send message to server
	private void sendMessage(String message) {
		try{
			output.writeObject("CLIENT- " + message);
			output.flush();
			showMessage("\nCLIENT - " + message);
		}catch(IOException e) {
			chatWindow.append("Something went wrong!!!");
		}
	}
	
	//update chat window
	private void showMessage(final String msg) {
		SwingUtilities.invokeLater(
			new Runnable() {
				public void run() {
					chatWindow.append(msg);
				}
			}
		);
	}
	
	//allow user to type
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













