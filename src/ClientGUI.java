//URL- https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
/*
 * @author-Shivam Kumar Sareen
 * @Student Id- 1001751987
 * */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


// The Client with its GUI
public class ClientGUI extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	//for GUI
	protected JLabel label,lblClnm,lblSpc;					// will first hold "Username:", later on "Enter message"
	protected  JTextField tfInputMsg;					// to hold the Username and later on the messages
	private JButton login, logout, OnlineUser,recieve_msg,send_msg;			// to Login, Logout and get the list of the users
	private JTextArea taChatMsg;				// for the chat room
	private boolean connected;			// if it is for connection
	
	private Client client;					// the Client object
	
	private int defaultPort;				// the default port number
	private String defaultHost;
	private JButton btnCloseClntConn;		//close button

	private boolean connClntStatus= false;
	private static String iphost="localhost";
	static int port =9928;
	String username;

	// Constructor connection receiving a socket number
	ClientGUI(String host, int port) {

		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		// The NorthPanel with: 4 elements - Local host, label usnm, tf usnm, login button
		JPanel northPanel = new JPanel(new GridLayout(4,1));
		// the server name anmd the port number
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 100));

		lblClnm=new JLabel();
		lblClnm.setText("");
		
		lblSpc=new JLabel();
		lblSpc.setText("            ");

		serverAndPort.add(new JLabel("Server Address:localhost"));
		serverAndPort.add(lblSpc);
		serverAndPort.add(lblClnm);

		//serverAndPort.add(tfPort);
		serverAndPort.add(new JLabel(""));
		// adds the Server an port field to the GUI
		northPanel.add(serverAndPort);

		// the Label and the TextField
		label = new JLabel("Enter your username below", SwingConstants.CENTER);
		northPanel.add(label);
		tfInputMsg = new JTextField();
		tfInputMsg.setBackground(Color.WHITE);
		northPanel.add(tfInputMsg);
		
		JPanel btnPanel = new JPanel(new GridLayout(1,3));
		//mid for Login button
				JPanel LoginPanel = new JPanel();
				login = new JButton("Login");
				login.addActionListener(this);
				LoginPanel.add(login);
				
				//send button
				JPanel SendPanel = new JPanel();
				send_msg = new JButton("Send Message");
				 send_msg.setEnabled(false);
				send_msg.addActionListener(this);
				SendPanel.add(send_msg);
				
				//send button
				JPanel RecievePanel = new JPanel();
			    recieve_msg = new JButton("Recieve Message");
			  	recieve_msg.setEnabled(false);
				recieve_msg.addActionListener(this);
				RecievePanel.add(recieve_msg);
				
				btnPanel.add(LoginPanel);
				btnPanel.add(SendPanel);
				btnPanel.add(RecievePanel);
				
				northPanel.add(btnPanel);
				// adding 4 to nothpanel		
		add(northPanel, BorderLayout.NORTH);

		// The CenterPanel which is the chat room
		taChatMsg = new JTextArea("Welcome to the Chat room\n", 80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(taChatMsg));
		taChatMsg.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);

		// the 3 buttons
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false);		// you have to login before being able to logout
		OnlineUser = new JButton("Show Connected Users");
		OnlineUser.addActionListener(this);
		OnlineUser.setEnabled(false);		// you have to login before being able to Who is in
		
		//writing the functionality for close button which closes the connection
		btnCloseClntConn= new JButton("Logout");
		btnCloseClntConn.addActionListener(this);
		btnCloseClntConn.setEnabled(false);
				//adding button to the south panel
		JPanel southPanel = new JPanel();
		//southPanel.add(login);
	//	southPanel.add(logout);
		southPanel.add(OnlineUser);
		southPanel.add(btnCloseClntConn);
		add(southPanel, BorderLayout.SOUTH);

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		tfInputMsg.requestFocus();
		
		client = new Client(iphost, port, this);
	}
		
	protected void disable_receive_btn() {
		recieve_msg.setEnabled(false);
	}
	
//URL- https://stackoverflow.com/questions/12749884/closing-a-jframe-using-a-button-in-eclipse
		void close_connection() {
			System.out.print("\nInside close_connection function");
	        	System.out.print("\nWill close the client connection ");
	        	try {
	        		System.out.print("\nClient connection Status is " + connClntStatus);
	        		if(connClntStatus==true)	//checking if there is a connection before closing it to prevent error
	        		{	
	        		//	clSocket.close();	//closes the connection if there was a connection established
					connClntStatus=false;
	        		}
	        		System.out.print("\nclosed the client connection. Status is "+connClntStatus);
	        		System.exit(0);
				//	frmClient.dispose();	*/
				}catch (NullPointerException e2) {
				//	System.out.println("\nNo client connection present "+ clSocket);
				//	frmClient.dispose();
				} 
	        	catch (Exception e1) {

				//	e1.printStackTrace();
				}
		}
		

	// called by the Client to append text in the TextArea - taChatMsg
	void append(String str) {
		taChatMsg.append(str);
		taChatMsg.setCaretPosition(taChatMsg.getText().length() - 1);
	}
	
	
	// called by the GUI is the connection failed	// we reset our buttons, label, textfield
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		OnlineUser.setEnabled(false);
		send_msg.setEnabled(false);
		recieve_msg.setEnabled(false);
		label.setText("Enter your username below");
		lblClnm.setText("");
		tfInputMsg.removeActionListener(this);
		connected = false;
	}
		
//performs all the actions for all the buttons- Send receive,Login, Logout
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		
	// ok it is a connection request
		if(o == login) {
			
			 username = tfInputMsg.getText().trim();		//accepting the username
			
			//if username is empty
			if (username==null || username=="" || username.isEmpty())
			{
				JOptionPane.showMessageDialog(ClientGUI.this," Please enter the username to connect");
			}
			else {
				// try creating a new Client with GUI
				System.out.println("Username="+username);
				System.out.println("Reached till client gui,="+this);
				String response="";
				try {	
					client.sOutput.writeObject(username);
					 response=client.sInput.readUTF();
				}
				catch (Exception e1) {
					System.out.println("Error validating ="+e1);
					e1.printStackTrace();
				}
				
				System.out.println("response ="+response);
				if (response.equals("Reject"))
				{	
					JOptionPane.showMessageDialog(getParent(), "User already exists. Please use different username");
					tfInputMsg.setText("");
					//System.exit(0);
					return;
				}
									
				// test if we can start the Client
				if(!client.start()) 
					return;
				
				lblClnm.setText("User Name: "+username);
				tfInputMsg.setText("");
				label.setText("Enter your message below and Click on Send to send/ Click on receive to receive");
				connected = true;
				
				// disable login button
				login.setEnabled(false);
				// enable the 2 buttons
				send_msg.setEnabled(true);
				recieve_msg.setEnabled(true);
				logout.setEnabled(true);
				OnlineUser.setEnabled(true);
				btnCloseClntConn.setEnabled(true);
				// Action listener for when the user enter a message
				tfInputMsg.addActionListener(this);
			}
		}
		
	// to send the message
		if(o==send_msg ) {	
			if (tfInputMsg.getText().trim()==null || tfInputMsg.getText().trim()=="" || tfInputMsg.getText().trim().isEmpty())
			{
				JOptionPane.showMessageDialog(ClientGUI.this," Please enter the message");
			}
			else {
				client.sendMessage(new ChatMessage(ChatMessage.MESSAGE, tfInputMsg.getText()));				
				tfInputMsg.setText("");
				return;
			}
		}
		
		
	// to receive the message
		if(o==recieve_msg) {
			System.out.println("asking for msg from server for the user="+username);
			client.fetch_msg_from_server(username);
			client.sendMessage(new ChatMessage(ChatMessage.RECEIVE,""));
		/*	try {	
				client.sOutput.writeObject(username);
				// response=client.sInput.readUTF();
			}
			catch (Exception e1) {
				System.out.println("Error validating ="+e1);
				e1.printStackTrace();
				}
		*/}
		
		
	// if it the who is in button
		if(o == OnlineUser) {
			client.sendMessage(new ChatMessage(ChatMessage.ONLINEUSER, ""));				
			return;
		}
		
		
		
	// if it is the Logout button
		//not using this for logout-using close instead of this
		if(o == logout) {
			lblClnm.setText("");
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));
			System.exit(0);
		//	return;
		}
		
	//for close button	
		if(o == btnCloseClntConn) {
			try {
			lblClnm.setText("");
			client.sendMessage(new ChatMessage(ChatMessage.LOGOUT, ""));}
			catch (NullPointerException e3) {
				
			}
			close_connection();
		}
	}

	// to start the whole thing the server
	public static void main(String[] args) {
		System.out.println("PORT="+port);
		try {
			
			new ClientGUI(iphost, port);
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}

}
