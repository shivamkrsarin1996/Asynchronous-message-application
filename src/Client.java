//URL- https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
/*
 * @author-Shivam Kumar Sareen
 * @Student Id- 1001751987
 * */
import java.net.*;
import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

/*
 * The Client that can be run both as a console or a GUI
 */
public class Client  {

	// for I/O
	public ObjectInputStream sInput;		// to read from the socket
	public ObjectOutputStream sOutput;		// to write on the socket
	private Socket socket;

	// if I use a GUI or not
	private ClientGUI clnt_gui;
	
	// the server, the port and the username
	private String server;
	static private int port=9928;
	static private String username;

	/*
	 * Constructor call when used from a GUI
	 * in console mode the ClienGUI parameter is null
	 */
	Client(String server,int port,  ClientGUI clnt_gui) {
		
		this.server = server;
		this.port = port;
	//	this.username = username;
		// save if we are in GUI mode or not
		this.clnt_gui = clnt_gui;
		System.out.println("inside client constructor gui");
		
		try {
				System.out.println("inside client, port"+port);
				socket = new Socket(server, port);
			} 
		catch(ConnectException ce){
			JOptionPane.showMessageDialog(clnt_gui, "Server Down!.Please check if the Server is running ");
			clnt_gui.tfInputMsg.setText("");
			System.exit(0);
		}
		catch(Exception ec) {
			//display("Error connectiong to server:" + ec);
			System.out.println(ec);
		}
		
		
		try
		{
			sInput  = new ObjectInputStream(socket.getInputStream());
			sOutput = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("I/O stream created");
		}
		catch (IOException eIO) {
			System.out.println("Exception creating new Input/output Streams: " + eIO);
			display("Exception creating new Input/output Streams: " + eIO);
			
		}
	}
	
	/*
	 * To start the dialog
	 */
	public boolean start() {
		// try to connect to the server
		
		String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
		display(msg);

		// creates the Thread to listen from the server 
		new ListenFromServer().start();
		// Send our username to the server this is the only message that we
		// will send as a String. All other messages will be ChatMessage objects
		
		// success we inform the caller that it worked
		return true;
	}

	/*
	 * To send a message to the console or the GUI
	 */
	private void display(String msg) {
		if(clnt_gui == null)
			System.out.println(msg);      // println in console mode
		else
			clnt_gui.append(msg + "\n");		// append to the ClientGUI JTextArea (or whatever)
	}
	
	 //To receive a message to the server
		void fetch_msg_from_server(String uname) {
		try {
		//	System.out.println("asking for msg from server for the user="+uname);
			//sOutput.writeObject("");
		//	String msg_from_file = (String) sInput.readObject();
		//	System.out.println("Client- msg="+msg_from_file);
			//clnt_gui.disable_receive_btn();
		}
	/*	catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	*/	catch(Exception e) {
			display("Exception writing to server: " + e);
		}
	}
	
	/*
	 * To send a message to the server
	 */
	void sendMessage(ChatMessage msg) {
		try {
			sOutput.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception writing to server: " + e);
		}
	}


	private void disconnect() {
		try { 
			if(sInput != null) sInput.close();
		}
		catch(Exception e) {} // not much else I can do
		try {
			if(sOutput != null) sOutput.close();
		}
		catch(Exception e) {} // not much else I can do
        try{
			if(socket != null) socket.close();
		}
		catch(Exception e) {} // not much else I can do
		
		// inform the GUI
		if(clnt_gui != null)
			clnt_gui.connectionFailed();
			
	}

	public static void main(String[] args) {
		// default values
		int portNumber = port;
		String serverAddress = "localhost";
		String userName = username;
	
		
	
	}

	/*
	 * a class that waits for the message from the server and append them to the JTextArea
	 */
	class ListenFromServer extends Thread {

		public void run() {
			while(true) {
				try {
					String msg = (String) sInput.readObject();
				//	System.out.println("appending");
					clnt_gui.append(msg);
				//	}
				}
				catch(IOException e) {
					display("Client " +username+" has disconnected from the connection: ");
					if(clnt_gui != null) 
						clnt_gui.connectionFailed();
					System.exit(0);
					break;
				}
				// can't happen with a String object but need the catch anyhow
				catch(ClassNotFoundException e2) {
				}
			}
		}
	}
}
