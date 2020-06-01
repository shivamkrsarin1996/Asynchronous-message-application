//URL-https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
/*
 * @author-Shivam Kumar Sareen
 * @Student Id- 1001751987
 * */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/*
 * The server as a GUI
 */
public class ServerGUI extends JFrame implements ActionListener, WindowListener {
	
	private static final long serialVersionUID = 1L;
	// the stop and start buttons
	private JButton stopStart,btnClear,btnSrvrClose;
	// JTextArea for the chat room and the events
	private JTextArea taChatLog, taSrvrEvtLog;
	private static JTextArea taClntList;
	// The port number
	private JTextField tPortNumber;
	// my server
	private Server server;
	
	static int srvrPort=9928;
	protected boolean connSrvrStatus=false;
//	List<String> clnt_list= new List<String>();
	
	// server constructor that receive the port to listen to for connection as parameter
	ServerGUI(int port) {
		super("Chat Server");
		server = null;
		
		// in the NorthPanel the Start and Stop buttons
		JPanel north = new JPanel();

		// to stop or start the server, we start with "Start"
		stopStart = new JButton("Start");
		stopStart.addActionListener(this);
		
		//Clear button clears out the data to start a new server
		/*btnClear = new JButton("Clear");
		btnClear.addActionListener(this);
		*/

		
		north.add(stopStart);
	//	north.add(btnClear);
		add(north, BorderLayout.NORTH);
		
		
		JPanel center = new JPanel(new GridLayout(2,1));
		
		// the event and chat room to the centerup panel
		JPanel centerup = new JPanel(new GridLayout(1,1));
		taChatLog = new JTextArea(80,80);
		taChatLog.setEditable(false);
		appendRoom("****Chat room.****\n");
		centerup.add(new JScrollPane(taChatLog));
		center.add(centerup,BorderLayout.CENTER);
			
		//Center down panel for cleint log and active client list
		JPanel centerdown = new JPanel(new GridLayout(1,2));
		taSrvrEvtLog = new JTextArea(80,80);
		taSrvrEvtLog.setEditable(false);
		appendEvent("****Events log/Client Log.****");
		//centerdown.add(new JScrollPane(taSrvrEvtLog));	
		taClntList = new JTextArea(80,80);
		taClntList.setEditable(false);
		taClntList.append("****Active Clients****\n");
		//append_Clnt_List("Active Clients");
		centerdown.add(new JScrollPane(taSrvrEvtLog));	
		centerdown.add(new JScrollPane(taClntList));
		
		
		center.add(centerdown,BorderLayout.CENTER);
		
		add(center,BorderLayout.CENTER);
		
		
		//south panel for close button
		JPanel south = new JPanel();
		//Close button closes the server and shuts the window
		btnSrvrClose = new JButton("Close");
		btnSrvrClose.addActionListener(this);
		south.add(btnSrvrClose);
		add(south,BorderLayout.SOUTH);
		
		// need to be informed when the user click the close button on the frame
		addWindowListener(this);
		setSize(800, 600);
		setVisible(true);
		
		//writing the functionality for closing the connection
//URL- https://stackoverflow.com/questions/12749884/closing-a-jframe-using-a-button-in-eclipse
		btnSrvrClose.addActionListener(new ActionListener() {
	        public void actionPerformed(ActionEvent e) {
	        	System.out.print("Will close the server connection");
	        	try {
	        		
					//ser.close();
					connSrvrStatus=false;
				//	Sckt.close();
					System.out.print("\nclosed the Server.Server Status is "+ connSrvrStatus);
					System.exit(0);
					
				//	this.dispose();
				} catch (Exception e1) {

					e1.printStackTrace();
				}
	        	finally {
	        	//	srvrSocket.close();
				}

	        }
	    }); 
	
	        }
		// Ending ServerGUI constructor	

	
	
	// append message to the three JTextArea
	// position at the end
	void appendRoom(String str) {
		taChatLog.append(str);
		taChatLog.setCaretPosition(taChatLog.getText().length() - 1);
	}
	
	 static void send_Clnt_name(String str) {
		 System.out.println("Client name="+str);
		//	taClntList.append(str);
		//	taClntList.setCaretPosition(taClntList.getText().length() - 1);
		}
		
	  void append_Clnt_List() {
		  taClntList.setText("");
		  taClntList.append("****Active Clients****\n");
		for(String s : Server.client_names) {
			System.out.println(s);
			taClntList.append("-> "+s+"\n");
		}
		  
		  
	//	taClntList.append("-> "+str+"\n");
	//	taClntList.setCaretPosition(taClntList.getText().length() - 1);
	}
	
	//method to display content in 
	void appendEvent(String str) {
		
		if(str=="Server Stopped")
		{
		
			 taClntList.setText("");
			  taClntList.append("****Active Clients****\n");
		
		}
		if(str=="Server waiting for Clients on port " + Server.srvrPort + ".")
		{	System.out.println("server started again");
			taSrvrEvtLog.setText("");
			taSrvrEvtLog.append("****Events log/Client Log.****"+"\n");
		
		}
		try {
		taSrvrEvtLog.append(str+"\n");
		taSrvrEvtLog.setCaretPosition(taSrvrEvtLog.getText().length() - 1);}
		catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error");
		}
	}
	
	//function to start and stop the server
	void start_stop_server() {
		// if running we have to stop
		if(server != null) {
			server.stop();
			server = null;
			 taClntList.setText("");
			  taClntList.append("****Active Clients****\n");
		//	tPortNumber.setEditable(true);
			stopStart.setText("Start");
			return;
		}
      	// OK start the server	
		int port;
		try {
			//port = Integer.parseInt(tPortNumber.getText().trim());
			port=srvrPort;
		}
		catch(Exception er) {
			appendEvent("Invalid port number");
			return;
		}
		// ceate a new Server
		server = new Server(srvrPort, this);
		System.out.println("Server created on port "+srvrPort);
		// and start it as a thread
		new ServerRunning().start();
	//	appendEvent("Server waiting for Clients on port " + Server.srvrPort);
		taSrvrEvtLog.setText("");
		taSrvrEvtLog.append("****Events log/Client Log.****"+"\n");
		stopStart.setText("Stop");
	//	tPortNumber.setEditable(false);
	}

	
	// entry point to start the Server
	public static void main(String[] arg) {
		// start server default port 1500
		new ServerGUI(srvrPort);
	}

	/*
	 * If the user click the X button to close the application
	 * I need to close the connection with the server to free the port
	 */
	public void windowClosing(WindowEvent e) {
		// if my Server exist
		if(server != null) {
			try {
				server.stop();			// ask the server to close the conection
			}
			catch(Exception eClose) {
			}
			server = null;
		}
		// dispose the frame
		dispose();
		System.exit(0);
	}
	// I can ignore the other WindowListener method
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}

	/*
	 * A thread to run the Server
	 */
	class ServerRunning extends Thread {
		public void run() {
			server.start();         // should execute until if fails
			// the server failed
			stopStart.setText("Start");
		//	tPortNumber.setEditable(true);
			appendEvent("Server Stopped");
			 taClntList.setText("");
			  taClntList.append("****Active Clients****\n");
			server = null;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		try {
		Object o = e.getSource();
	//	System.out.println("Action recieved for action "+o);
		if(o == stopStart) {
			start_stop_server();
		}
		}
		catch (Exception e2) {
			// TODO: handle exception
			
		}
		
	}

}
