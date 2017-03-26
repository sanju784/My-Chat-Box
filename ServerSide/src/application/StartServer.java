package application;

import javax.swing.JFrame;

public class StartServer {

	  public static void main(String args[]) {
		  MyServer myServer = new MyServer();
		  myServer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		  myServer.startRunning();
	  }
}
