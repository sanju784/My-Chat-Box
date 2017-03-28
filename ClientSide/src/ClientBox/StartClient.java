package ClientBox;

import javax.swing.JFrame;

public class StartClient {

	public static void main(String args[]) {
		Client myClient = new Client("127.0.0.1");
		myClient.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myClient.startRunning();
	}
}
