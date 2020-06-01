//URL-https://www.dreamincode.net/forums/topic/259777-a-simple-chat-program-with-clientserver-gui-optional/
/*
 * @author-Shivam Kumar Sareen
 * @Student Id- 1001751987
 * */
import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import org.json.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.JOptionPane;

//import Server.ClientThread;

/*
 * The server that can be run both as a console application or a GUI
 */
public class Server {
	// a unique ID for each connection
	private static int uniqueId;
	// an ArrayList to keep the list of the Client
	private ArrayList<ClientThread> al;
	// if I am in a GUI
	private ServerGUI sg;
	// to display time
	private SimpleDateFormat sdf;
	// the port number to listen for connection
	private int port;
	// the boolean that will be turned of to stop the server
	private boolean keepGoing;
	// client_names contains list of online users
	public static ArrayList<String> client_names;
	
	static Set<String> all_user_names;
	static int srvrPort=9928;
	
	//for file
	
	static File username_file;
//	String msg_file="MessageFile";
	FileReader fr;
	FileWriter fw;
	BufferedReader br;
	//Queue<Map<String,List<String>>> q_msg;
	
	static String all_usrnm_file_name= "All_User_file.txt";
	Map<String,List<String>> mapMsg_content;
	Map<String,List<String>> mapMessages;
	//Map<String, String> mapMessages;
	LinkedList<String> msg_queue;
	
	//Constructor
	/*
	 *  server constructor that receive the port to listen to for connection as parameter
	 *  in console
	 */
	public Server(int port)
	{
		this(port, null);
	}
	
	public Server(int port, ServerGUI sg) 
	{
		// GUI or not
		this.sg = sg;
		// the port
		this.port = port;
		// to display hh:mm:ss
		sdf = new SimpleDateFormat("HH:mm:ss");
		// ArrayList for the Client list
		al = new ArrayList<ClientThread>();
		client_names=new ArrayList<String>();
		all_user_names= new HashSet<String>();
		
		

		
		//creating a file to store the messages
		try {
			//Create the file for storing messages
				
				
				//Create the file for storing usernames of all the users connected
				username_file=new File(all_usrnm_file_name);
					if (username_file.createNewFile()){
					    System.out.println("File is created!="+username_file.getName());
					} 
					else {
					    System.out.println("File already exists.="+username_file.getName());
					}
					
				
				mapMsg_content= new HashMap<String, List<String>>();
				msg_queue= new LinkedList<String>();
				//mapMessages= new HashMap<String, String>();
				mapMessages= new HashMap<String, List<String>>();
				
			}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public static void removeClientnames(String user)
	{		//	System.out.println("Inside removeClient");
		if (client_names.contains(user)) 
		{
			client_names.remove(user);
			System.out.println(user+" removed");
			
		}
	}

	public static void addClientnames(String user)
	{		//	System.out.println("Inside addClient");
		if (!client_names.contains(user)) {
			client_names.add(user);
			all_user_names.add(user);
		//	write_to_usernm_file(user);
		}
	}
	
	private static void write_to_usernm_file(String usnm) {

		System.out.println("All the users connected till now-");
		System.out.println(all_user_names); 

		//writing the usernames to the file
		if(!all_user_names.isEmpty())
		{

				System.out.println("traversing for user-"+usnm);
				boolean found = false;
				try
				{
					FileWriter fw_unm = new FileWriter(username_file,true);
					//BufferedReader br=new BufferedReader(new FileReader(username_file));
					if(username_file.length()==0)
					{
						System.out.println("file is empty");
						System.out.println("Username="+usnm);
						 fw_unm.append(usnm+"\n");
					//	 fw_unm.close();
					}
					else
					{
						System.out.println("file is not empty");
						System.out.println("Username="+usnm);
						BufferedReader br=new BufferedReader(new FileReader(username_file));
				//		System.out.println("content of file-"+username_file);
						
						while(br.ready())
					 	{
						 	String st=br.readLine();
						 	//System.out.println("cntnt="+st);
						 	if(usnm.equals(st))
						 	{
						 		System.out.println("same user exists in the file-"+usnm +"won't be added");
						 		found=true;
						 		break;
						 	}
					 	 }
						br.close();
						if(found==false)
						{
							System.out.println("new Username to be added="+usnm);
						 fw_unm.append(usnm+"\n");
						}
						//	System.out.println("Content saved in file");
					}
					fw_unm.close();
				}
				catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	

	public static boolean isClientOnline(String user) {
	//	System.out.println("Inside isClientOnline");
		return client_names.contains(user);
	}
	
	public void start() 
	{
	//	System.out.println("Inside server start");
		keepGoing = true;
		/* create socket server and wait for connection requests */
		try 
		{
			// the socket used by the server
			ServerSocket serverSocket = new ServerSocket(port);

			display("Server waiting for Clients on port " + port + ".");
			
			// infinite loop to wait for connections
			while(keepGoing) 
			{
				// format message saying we are waiting
				//display("Server waiting for Clients on port " + port + ".");
				
				Socket socket = serverSocket.accept();  	// accept connection
				// if I was asked to stop
				if(!keepGoing)
					break;
				ClientThread t = new ClientThread(socket);  // make a thread of it
			//	System.out.println("Adding t to al, size of al="+al.size());
				al.add(t);									// save it in the ArrayList
	
				//		System.out.println("Added t to al, size of al="+al.size() +"content =");
		//		System.out.println("will go to run method now");
				
				t.start();
			}
			// I was asked to stop
			try {
				serverSocket.close();
				for(int i = 0; i < al.size(); ++i) {
					ClientThread tc = al.get(i);
					try {
					tc.sInput.close();
					tc.sOutput.close();
					tc.socket.close();
					}
					catch(IOException ioE) {
						// not much I can do
					}
				}
			}
			catch(Exception e) {
				display("Exception closing the server and clients: " + e);
			}
		}
		// something went bad
			catch (IOException e) {
            String msg = sdf.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
			display(msg);
		}
	}
	
	
    /*
     * For the GUI to stop the server
     */
	protected void stop() 
	{
		keepGoing = false;
		// connect to myself as Client to exit statement 
		// Socket socket = serverSocket.accept();
		try {
			new Socket("localhost", port);
		}
		catch(Exception e) {
			// nothing I can really do
		}
	}
	
	
	 // Display an event (not a message) to or the GUI
	private void display(String msg) 
	{
		
		String time = sdf.format(new Date()) + " " + msg;
		if(sg == null)
			System.out.println(time);
		else
			sg.appendEvent(time + "\n");
	}
	
	
	
	
//This is async implementation	
	private synchronized boolean broadcast(String message)
	{	System.out.println("*******************************************************************************************");
		System.out.println("Message is="+message);
		List<String> usr_msg;	//for broadcast
		List<String> multi_usr_msg;	//for multicast
		List<String> unicast_usr_msg;	//for multicast
		JSONObject json_write_obj,jsn_read_ob;
		usr_msg= new ArrayList<String>();	//currently storing messages in broadcast functionality
		multi_usr_msg= new ArrayList<String>();
		unicast_usr_msg= new ArrayList<String>();
		String msg_file_name= "MessageFile.json";
		json_write_obj = new JSONObject();
		File msg_file=new File(msg_file_name);
		try {
			if (msg_file.createNewFile()){
			    System.out.println("File is created!="+msg_file.getName());
			} 
			else {
			    System.out.println("File already exists.="+msg_file.getName());
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		// add HH:mm:ss and \n to the message
				String time = sdf.format(new Date());
				String messageLf = time + " " + message + "\n";
				
				// display message on Server's GUI
				if(sg == null)
					System.out.print(messageLf);
				else
					sg.appendRoom(messageLf);     // append in the room window
		
				
		//storing according to the msg mode
				
//checking for multi/uni
				if(message.contains("@"))
				{	
					System.out.println("It's a uni/multi");	
					String[] w = message.split(" ",3);
					System.out.println("w="+w);
					boolean isPrivate = false;
					if(w[1].charAt(0)=='@') 	//check if at index 0 character is @
						isPrivate=true;
	//w[0] contains the sender name				
	//w[1] contains the list of recipients	
	//w[2] contains the message	
					//unicast- if private message, send message to mentioned username only
					//multicast mentioned with a multiple users separated using ","
					String sender = w[0].substring(0, w[0].indexOf(':'));
					System.out.println("===========================sender==========="+sender);
						if(isPrivate==true)
						{	
			//unicast						
							if (!w[1].contains(","))
							{	
								
								System.out.println("will be a unicast");
							//	unicast_usr_msg.add(messageLf);
								String tocheck=w[1].substring(1, w[1].length());
								String receiver=tocheck;
								message=w[0]+w[2];
								System.out.println("w[0]="+w[0]+",w[1]="+w[1]+",w[2]="+w[2]+",message="+message);
								//String messageLf = time + " " + message + "\n";
								
								boolean found=false;
																
								
								// we loop in reverse order to find the mentioned username
								for(int y=al.size(); --y>=0;)
								{
									ClientThread ct1=al.get(y);
								//	String check1=ct1.username;
									String check=ct1.getUsername();
									String thread_usrname=check;
									System.out.println("===============thread_usrname========="+thread_usrname);
									System.out.println("================receiver============="+receiver);
									if(!sender.equals(thread_usrname)) 
									{
										if(thread_usrname.equals(receiver))
										{
											System.out.println("check/current thread="+check);
											System.out.println("mentioned client/ tocheck="+tocheck);
	
													// if file is empty			
											if(msg_file.length()==0)
											{
												System.out.println("blank file");	
												System.out.println("blank file-Json ob before writing-"+json_write_obj);
												List<String> list = new ArrayList<>();
												list.add(messageLf);
												 json_write_obj.put(receiver, new ArrayList<>(list));
												 System.out.println("blank file-Json ob after writing-"+json_write_obj);
											//	 mapMessages.remove(tocheck);
											 }
											
													// if file is not empty				
											else if(msg_file.length()!=0)
											{	
												System.out.println("file is not empty");
												JSONParser jsonParser = new JSONParser();
												try (FileReader reader = new FileReader(msg_file_name))
										        {
										            //Read JSON file
										            Object obj = jsonParser.parse(reader);
										            System.out.println(obj);
										             jsn_read_ob = (JSONObject) obj;
										            System.out.println("JSON Read ob-"+jsn_read_ob);
										       //     System.out.println("checking for Message for-"+u);
										            System.out.println("========receiver====================="+receiver);
										            List<String> lst= (List<String>) jsn_read_ob.get(receiver);
										            
									            	if (lst != null && lst.size() > 0)
									            	{
									            		//lmsg.addAll(lst);
									            		System.out.println("=======list for tocheck=========="+lst.get(0));
									            		System.out.println("list is not null");
									            		System.out.println("list of msg from lst before adding-"+lst);
									            		lst.add(messageLf);
									       
									            		json_write_obj.put(receiver, new ArrayList<>(lst));
									            		lst.clear();
									            	}
									            	else 
									            	{	
									            		
									            		System.out.println("list is null");
									            		List<String> currentList = new ArrayList<>();
									            		currentList.add(messageLf);
									            		json_write_obj.put(receiver, currentList);
									            	}	            
						  					   }	//end- try
												 catch (FileNotFoundException e) {
											            e.printStackTrace();
											   } catch (IOException e) {
											            e.printStackTrace();
											   } catch (ParseException e) {
											            e.printStackTrace();
											   }
											}	//end- if msgfile.length!=0
											
											
											//URL-https://howtodoinjava.com/library/json-simple-read-write-json-examples/				
										       //Write JSON file
									        try 
									        {
									     
									        	if (!msg_file.createNewFile() && msg_file.length() > 0)
									        	{
									        		System.out.println("===========================================================================");
									        		JSONParser parser = new JSONParser();
									        		Object object = parser.parse(new FileReader(msg_file_name));
									        		JSONObject jsonObject = (JSONObject) object;
									        		for (Object key : jsonObject.keySet()) 
									        		{
									        		   if (!json_write_obj.containsKey(key)) 
									        		   {
									        			   json_write_obj.put(key, jsonObject.get(key));
									        			   System.out.println("========wrote====="+json_write_obj);
									        			   System.out.println("=========reader================"+jsonObject);
									        		   }
									        		}
									 
									        	}
									        	FileWriter file = new FileWriter(msg_file_name);
									        	System.out.println("will write msgs to file");
									            file.write(json_write_obj.toJSONString());
									            System.out.println("written msgs to file");
									            file.close();
									        }
									        catch (Exception e) 
									        {
									            e.printStackTrace();
									        }
	
	
											// username found and delivered the message
											found=true;
											//break;
									   }

										
									} 
									else 	// thread_username = sender
									{
										System.out.println("sender = thread");
										ct1.writeMsg(messageLf);
										
										
									}
								}
								
								if(!found)
								{
									// if file is empty			
									if(msg_file.length()==0)
									{
										System.out.println("blank file");	
										System.out.println("blank file-Json ob before writing-"+json_write_obj);
										List<String> list = new ArrayList<>();
										list.add(messageLf);
										 json_write_obj.put(receiver, new ArrayList<>(list));
										 System.out.println("blank file-Json ob after writing-"+json_write_obj);
									//	 mapMessages.remove(tocheck);
									 }	
							// if file is not empty				
								else if(msg_file.length()!=0)
								{
									JSONObject jo = new JSONObject();
									JSONParser jsonParser = new JSONParser();
									try (FileReader reader = new FileReader(msg_file_name))
							        {
							            //Read JSON file
							            Object obj = jsonParser.parse(reader);
							            System.out.println(obj);
							             jsn_read_ob = (JSONObject) obj;
							            System.out.println("JSON Read ob-"+jsn_read_ob);
							       //     System.out.println("checking for Message for-"+u);
							            System.out.println("========receiver====================="+receiver);
							            List<String> lst= (List<String>) jsn_read_ob.get(receiver);
							            
						            	if (lst != null && lst.size() > 0)
						            	{
						            		//lmsg.addAll(lst);
						            		System.out.println("=======list for tocheck=========="+lst.get(0));
						            		System.out.println("list is not null");
						            		System.out.println("list of msg from lst before adding-"+lst);
						            		lst.add(messageLf);
						       
						            		json_write_obj.put(receiver, new ArrayList<>(lst));
						            		lst.clear();
						            	}
						            	else 
						            	{	
						            		
						            		System.out.println("list is null");
						            		List<String> currentList = new ArrayList<>();
						            		currentList.add(messageLf);
						            		json_write_obj.put(receiver, currentList);
						            	}	 
						            	
			  					   }	//end- try
									 catch (FileNotFoundException e) {
								            e.printStackTrace();
								   } catch (IOException e) {
								            e.printStackTrace();
								   } catch (ParseException e) {
								            e.printStackTrace();
								   }
								}	
						        try 
						        {
						     
						        	if (!msg_file.createNewFile() && msg_file.length() > 0)
						        	{
						        		System.out.println("===========================================================================");
						        		JSONParser parser = new JSONParser();
						        		Object object = parser.parse(new FileReader(msg_file_name));
						        		JSONObject jsonObject = (JSONObject) object;
						        		for (Object key : jsonObject.keySet()) 
						        		{
						        		   if (!json_write_obj.containsKey(key)) 
						        		   {
						        			   json_write_obj.put(key, jsonObject.get(key));
						        			   System.out.println("========wrote====="+json_write_obj);
						        			   System.out.println("=========reader================"+jsonObject);
						        		   }
						        		}
						 
						        	}
						        	FileWriter file = new FileWriter(msg_file_name);
						        	System.out.println("will write msgs to file");
						            file.write(json_write_obj.toJSONString());
						            System.out.println("written msgs to file");
						            file.close();
						        }
						        catch (Exception e) 
						        {
						            e.printStackTrace();
						        }
								}

							}
	//multicast									
							else if(w[1].contains(","))
								{	
									System.out.println("will be a multicast");
								//	System.out.println("");
									multi_usr_msg.add(messageLf);
									System.out.println("multi msg list="+multi_usr_msg);
									String str_for_multi_users=w[1].substring(1);
								
								//creating an arraylist to store users for multicast
									ArrayList<String> listof_multicast_users= new ArrayList<String>();
									String[] recipient=str_for_multi_users.split(",");
									System.out.println("recipient's list="+recipient);
									System.out.println("No of users in reciepient for multicast="+recipient.length);
									
								
									//iterating over all reciepients
									for (String usr : recipient)
									{
										
										System.out.println("Receiveing users="+usr);
										mapMessages.put(usr, multi_usr_msg);
									//	json_write_obj.put(usr, messageLf);
										
									}		

										boolean found=false;
																		
										// we loop in order to find the mentioned username
										for(int y=0; y < al.size(); y++)
										{
											ClientThread ct1=al.get(y);
										//	String check1=ct1.username;
										//	System.out.println("from ct1.username="+check1);
											String check=ct1.getUsername();
											String thread_usrname= check;
											System.out.println("thread user name="+thread_usrname);
											System.out.println("sender="+sender);
											
											if(!sender.equals(thread_usrname))
											{
												for (int i=0;i<recipient.length;i++)
												{
													String receiver=recipient[i];
													System.out.println("Msg for="+ receiver);
													System.out.println("sender and thread are not same");
													if(thread_usrname.equals(recipient[i]))
													{
														if(msg_file.length()==0)
														{
															System.out.println("blank file");
															System.out.println("mapMessages="+mapMessages);
															json_write_obj.put(receiver, multi_usr_msg);
													}
														
													if(msg_file.length()!=0)
													{
														JSONParser jsonParser = new JSONParser();
														try (FileReader reader = new FileReader(msg_file_name))
												        {
												            //Read JSON file
												            Object obj = jsonParser.parse(reader);
												            System.out.println(obj);
												             jsn_read_ob= (JSONObject) obj;
												            System.out.println("JSON Read ob-"+jsn_read_ob);
												            List<String> lmsg=new ArrayList<String>();
												            lmsg.add(messageLf);
												            System.out.println("lsmg="+lmsg);
												            
												            
												            //reading from the map create for current users		
												            System.out.println("Reading from map/queue");

													            	List<String> lst= (List<String>) jsn_read_ob.get(receiver);
													            	if (lst != null && lst.size() > 0)
													            	{
													            		//lmsg.addAll(lst);
													            		lst.addAll(lmsg);
													            		json_write_obj.put(receiver, lst);
													            	}
													            	if (lst == null )
													            		json_write_obj.put(receiver, lmsg);
//													 	    }  
												            
															System.out.println();
															System.out.println("json write ob-"+json_write_obj);			            
								  					   }	//end- try
														 catch (FileNotFoundException e) {
													            e.printStackTrace();
													   } catch (IOException e) {
													            e.printStackTrace();
													   } catch (ParseException e) {
													            e.printStackTrace();
													   }
													}	//end- if msgfile.length!=0
													
													
														//URL-https://howtodoinjava.com/library/json-simple-read-write-json-examples/				
													       //Write JSON file
												        try 
												        {
												     
												        	if (!msg_file.createNewFile() && msg_file.length() > 0)
												        	{
												        		System.out.println("===========================================================================");
												        		JSONParser parser = new JSONParser();
												        		Object object = parser.parse(new FileReader(msg_file_name));
												        		JSONObject jsonObject = (JSONObject) object;
												        		for (Object key : jsonObject.keySet()) 
												        		{
												        		   if (!json_write_obj.containsKey(key)) 
												        		   {
												        			   json_write_obj.put(key, jsonObject.get(key));
												        			   System.out.println("========wrote====="+json_write_obj);
												        			   System.out.println("=========reader================"+jsonObject);
												        		   }
												        		}
												 
												        	}
												        	FileWriter file = new FileWriter(msg_file_name);
												        	System.out.println("will write msgs to file");
												            file.write(json_write_obj.toJSONString());
												            System.out.println("written msgs to file");
												            file.close();
												        }
												        catch (Exception e) 
												        {
												            e.printStackTrace();
												        }
		
														// username found and delivered the message
														found=true;
													//	break;
													}
													
												}	//iterating for list of recipients
											}	//end-if sender!= thread name
											else 
											{
												System.out.println("sender and thread are same");
												ct1.writeMsg(messageLf);
											}
										
										}	//stop iterating the client thread al
										
										
										// mentioned user not found, return false
										if(!found)
										{
											for (int i=0;i<recipient.length;i++)
											{
												String receiver=recipient[i];
												System.out.println("Msg for="+ receiver);
												// if file is empty			
												if(msg_file.length()==0)
												{
													System.out.println("blank file");	
													System.out.println("blank file-Json ob before writing-"+json_write_obj);
													List<String> list = new ArrayList<>();
													list.add(messageLf);
													 json_write_obj.put(receiver, new ArrayList<>(list));
													 System.out.println("blank file-Json ob after writing-"+json_write_obj);
												//	 mapMessages.remove(tocheck);
												 }	
									// if file is not empty				
										else if(msg_file.length()!=0)
										{
											JSONObject jo = new JSONObject();
											JSONParser jsonParser = new JSONParser();
											try (FileReader reader = new FileReader(msg_file_name))
									        {
									            //Read JSON file
									            Object obj = jsonParser.parse(reader);
									            System.out.println(obj);
									             jsn_read_ob = (JSONObject) obj;
									            System.out.println("JSON Read ob-"+jsn_read_ob);
									       //     System.out.println("checking for Message for-"+u);
									            System.out.println("========receiver====================="+receiver);
									            List<String> lst= (List<String>) jsn_read_ob.get(receiver);
									            
								            	if (lst != null && lst.size() > 0)
								            	{
								            		//lmsg.addAll(lst);
								            		System.out.println("=======list for tocheck=========="+lst.get(0));
								            		System.out.println("list is not null");
								            		System.out.println("list of msg from lst before adding-"+lst);
								            		lst.add(messageLf);
								       
								            		json_write_obj.put(receiver, new ArrayList<>(lst));
								            		lst.clear();
								            	}
								            	else 
								            	{	
								            		
								            		System.out.println("list is null");
								            		List<String> currentList = new ArrayList<>();
								            		currentList.add(messageLf);
								            		json_write_obj.put(receiver, currentList);
								            	}	 
								            	
					  					   }	//end- try
											 catch (FileNotFoundException e) {
										            e.printStackTrace();
										   } catch (IOException e) {
										            e.printStackTrace();
										   } catch (ParseException e) {
										            e.printStackTrace();
										   }
										}	
								        try 
								        {
								     
								        	if (!msg_file.createNewFile() && msg_file.length() > 0)
								        	{
								        		System.out.println("===========================================================================");
								        		JSONParser parser = new JSONParser();
								        		Object object = parser.parse(new FileReader(msg_file_name));
								        		JSONObject jsonObject = (JSONObject) object;
								        		for (Object key : jsonObject.keySet()) 
								        		{
								        		   if (!json_write_obj.containsKey(key)) 
								        		   {
								        			   json_write_obj.put(key, jsonObject.get(key));
								        			   System.out.println("========wrote====="+json_write_obj);
								        			   System.out.println("=========reader================"+jsonObject);
								        		   }
								        		}
								 
								        	}
								        	FileWriter file = new FileWriter(msg_file_name);
								        	System.out.println("will write msgs to file");
								            file.write(json_write_obj.toJSONString());
								            System.out.println("written msgs to file");
								            file.close();
								        }
								        catch (Exception e) 
								        {
								            e.printStackTrace();
								        }
										}
										
								}

								} //end- multicast
						} // end if isprivate							
				}	
// end of unicast/multicast
				
//it's a broadcast				
				else 
				{
					System.out.println("broadcast message="+message);
					String[] w = message.split(" ",3);
					System.out.println("w="+w);
					String sender = w[0].substring(0, w[0].indexOf(':'));
					usr_msg.add(messageLf);
					System.out.println("List of msgs="+usr_msg);
					System.out.println("It's a broadcast");	
			//iterating over all users present
					for (String usr : client_names)
					{
						
						System.out.println("Current users="+usr);
						mapMessages.put(usr, usr_msg);
					//	json_write_obj.put(usr, messageLf);
						
					}
					
					// we loop in order to find the mentioned username
					for(int y=0; y < al.size(); y++)
					{
						ClientThread ct1=al.get(y);
					//	String check1=ct1.username;
					//	System.out.println("from ct1.username="+check1);
						String check=ct1.getUsername();
						String thread_uname= check;
						String receiver=thread_uname;
						System.out.println("thread user name="+thread_uname);
						System.out.println("sender="+sender);
						
						if(!sender.equals(receiver))
						{
					
							if(msg_file.length()==0)
							{
						
								System.out.println("blank file");
								  System.out.println("mapMessages="+mapMessages);
								 System.out.println("Reading from map/queue");

						            	json_write_obj.put(receiver, usr_msg);
//						            }
						            
						            
							}
						
							if(msg_file.length()!=0)
							{
								JSONParser jsonParser = new JSONParser();
								try (FileReader reader = new FileReader(msg_file_name))
						        {
						            //Read JSON file
						            Object obj = jsonParser.parse(reader);
						            System.out.println(obj);
						             jsn_read_ob= (JSONObject) obj;
						            System.out.println("JSON Read ob-"+jsn_read_ob);
						          //     System.out.println("checking for Message for-"+u);
						            List<String> lmsg=new ArrayList<String>();
						            lmsg.add(messageLf);
						            System.out.println("lsmg="+lmsg);
						            
						            
						            //reading from the map create for current users		
						            System.out.println("Reading from map/queue");
/*						            for(Entry<String, List<String>> mapElement :mapMessages.entrySet())
						            {
						            	String usrnm= mapElement.getKey();
						            	List<String> value_set = mapElement.getValue(); 
						            	// for(String value : mapElement.getValue())
					            
						            	System.out.println("1.Message for-"+usrnm + " : "); 
						            	System.out.println("messasges from map valueset="+ value_set);
*/						            
		
							            	List<String> lst= (List<String>) jsn_read_ob.get(receiver);
							            	if (lst != null && lst.size() > 0)
							            	{
							            		//lmsg.addAll(lst);
							            		lst.addAll(lmsg);
							            		json_write_obj.put(receiver, lst);
							            	}
							            	if (lst == null )
							            		json_write_obj.put(receiver, lmsg);
//							 	    }  
						            
									System.out.println();
									System.out.println("json write ob-"+json_write_obj);			            
		  					   }	//end- try
								 catch (FileNotFoundException e) {
							            e.printStackTrace();
							   } catch (IOException e) {
							            e.printStackTrace();
							   } catch (ParseException e) {
							            e.printStackTrace();
							   }
							}	//end- if msgfile.length!=0

					
					
					System.out.println();
					System.out.println("json write ob-"+json_write_obj);
					
							//URL-https://howtodoinjava.com/library/json-simple-read-write-json-examples/				
						       //Write JSON file
					        try 
					        {
					     
					        	if (!msg_file.createNewFile() && msg_file.length() > 0)
					        	{
					        		System.out.println("===========================================================================");
					        		JSONParser parser = new JSONParser();
					        		Object object = parser.parse(new FileReader(msg_file_name));
					        		JSONObject jsonObject = (JSONObject) object;
					        		for (Object key : jsonObject.keySet()) 
					        		{
					        		   if (!json_write_obj.containsKey(key)) 
					        		   {
					        			   json_write_obj.put(key, jsonObject.get(key));
					        			   System.out.println("========wrote====="+json_write_obj);
					        			   System.out.println("=========reader================"+jsonObject);
					        		   }
					        		}
					 
					        	}
					        	FileWriter file = new FileWriter(msg_file_name);
					        	System.out.println("will write msgs to file");
					            file.write(json_write_obj.toJSONString());
					            System.out.println("written msgs to file");
					            file.close();
					        }
					        catch (Exception e) 
					        {
					            e.printStackTrace();
					        }
						}	//end-if sender != thread username 
						else 
						{
							System.out.println("sender and thread are same");
							ct1.writeMsg(messageLf);
						}
					}	//loop end-stop iterating the client thread al
				}	//end- broadcast logic

		return true;
		
		
	}	//end of function
	
	
	
	
	
	
	// for a client who logoff using the LOGOUT message
	synchronized void remove(int id)
	{
		// scan the array list until we found the Id
		for(int i = 0; i < al.size(); ++i)
		{
			ClientThread ct = al.get(i);
			// found it
			if(ct.id == id)
			{
				al.remove(i);
				return;
			}
		}
	}


	public static void main(String[] args) 
	{
		// start server on PortNumber that is specified 
		int portNumber = srvrPort;

		// create a server object and start it
		Server server = new Server(portNumber);
		server.start();
	}

	
	/** One instance of this thread will run for each client -contains sInput and sOutput */
	class ClientThread extends Thread {
		// the socket where to listen/talk
		Socket socket;
		ObjectInputStream sInput;
		ObjectOutputStream sOutput;
		// my unique id (easier for deconnection)
		int id;
		// the Username of the Client
		String username;
		// the only type of message a will receive
		ChatMessage cm;
		// the date I connect
		String date;

	// Constructor
		ClientThread(Socket socket) {
			
			//ClientGUI cnt_gui;
			id = ++uniqueId;		// a unique id
			this.socket = socket;
			/* Creating both Data Stream */
			System.out.println("Thread trying to create Object Input/Output Streams");
			//System.out.println("1--socket created from="+username);
			try
			{	
				// create output first
				sOutput = new ObjectOutputStream(socket.getOutputStream());
				sInput  = new ObjectInputStream(socket.getInputStream());
				System.out.println("I/O stream created for Server");
				
				String status="reject";
			
			while(true)
			{		//	System.out.println("2--socket created from="+username);
					//	System.out.println("Status="+status);
						// read the username
						username = (String) sInput.readObject();
						System.out.println("User name from server in Cl thread="+username);
					//	System.out.println("Thread created for-"+username);
			//Calling the method to check if the username entered is unique or not
						if(Server.isClientOnline(username))
						{
							sOutput.writeUTF("Reject");
							sOutput.flush();
							//continue;
						}
						else 
						{
							Server.addClientnames(username);
							//Server.write_to_usernm_file(username);
							sOutput.writeUTF("Accept");
							sOutput.flush();
							
							break;
						}
						
					
				} //end-while
			display(username + " just connected.");
			ServerGUI.send_Clnt_name(username);
			sg.append_Clnt_List();
			}
			catch (IOException e) {
				display("Exception creating new Input/output Streams: " + e);
				return;
			}
			catch (ClassNotFoundException e) {
			}
            date = new Date().toString() + "\n";
		}// end of constructor
		
		//Write a String to the Client output stream- sends the message to the client
		private boolean writeMsg(String msg) {
			// if Client is still connected send the message to it
			if(!socket.isConnected()) {
				close();
				return false;
			}
			// write the message to the stream
			try {
			//	System.out.println("msg to be sent to the client="+ msg);
				sOutput.writeObject(msg+"\n");
			}
			// if an error occurs, do not abort just inform the user
			catch(IOException e) {
				display("Error sending message to " + username);
				display(e.toString());
			}
			return true;
		}

	// client uses this function to read the messages stored for it in the file		
//https://howtodoinjava.com/library/json-simple-read-write-json-examples/		
		@SuppressWarnings("unchecked")
		private void read_from_msg_queue_file(String u) 
		{	String msg_file_name= "MessageFile.json";
			System.out.println("inside read_from_file- this will read the messages from the file");
			 String st = null;
			 System.out.println("checking for msg");
			 File msg_file=new File(msg_file_name);
			 
			 //JSON parser object to parse read file
	        JSONParser jsonParser = new JSONParser();
	         if(msg_file.length()==0)
	         {System.out.println("No contents");
	         }
	         else {
			        try (FileReader reader = new FileReader("MessageFile.json"))
			        {
			            //Read JSON file
			            Object obj = jsonParser.parse(reader);
		//	            System.out.println(obj);
			            JSONObject jsn_ob= (JSONObject) obj;
			            System.out.println("JSON ob-"+jsn_ob);
			            System.out.println("checking for Message for-"+u);
			      /*      if(jsn_ob.containsKey(u))
			            {	*/
			            	System.out.println("Msg for "+u);

			            	 List<String> msg_list=(List<String>) jsn_ob.get(u);
			            	System.out.println("messsages-"+msg_list);
			            	if(msg_list!=null)
			            	{	
				            	System.out.println("Msg list size="+msg_list.size());
				            	for(String msg: msg_list)
				            	{
				            		System.out.print(msg);
				            		writeMsg(msg);
				            	}
				            	//URL-https://howtodoinjava.com/library/json-simple-read-write-json-examples/				
							       //Write JSON file
						        try (FileWriter file = new FileWriter("MessageFile.json")) 
						        {
						        	System.out.println("will remove msgs from file for user="+u);
						        	jsn_ob.remove(u);
						        	System.out.println("json_ob from file="+jsn_ob);
						            file.write(jsn_ob.toJSONString());
						            file.flush();
						            System.out.println("removed from file");
						            file.close();
						        } 
						        catch (IOException e) 
						        {
						            e.printStackTrace();
						        }
			               }
			            	 else 
					            {
					            	System.out.println("No message for "+u);
					            	writeMsg("No unread message for "+u);
					            }
			        }
			        catch (FileNotFoundException e) {
			            e.printStackTrace();
			        } catch (IOException e) {
			            e.printStackTrace();
			        } catch (ParseException e) {
			            e.printStackTrace();
			        }

	         }
		}
		
		// client uses this function to read the messages stored for it in the file
		public void read_from_file() {
			try {
			System.out.println("inside read_from_file- this will read the messages from the file");
			 String st = null;
			 System.out.println("checking for msg");
			 
			 br=new BufferedReader(new FileReader("MessageFile.json"));
			 
			 if(!br.ready())
			 {System.out.println("No unread messages");}
			 else {
				 System.out.println("Fetching messages");
				// while(br.readLine()!=null)
					while(br.ready())
				 	{
					 	st=br.readLine();
						//System.out.println("Content in file="+st);
						
						if(st.contains(username)){
							System.out.println("\ncontains msg for "+username);
						//	System.out.println("Content in file="+st);
						//	list_msgs_for_user.add(st);
						//	System.out.println("List of messages for "+username+"="+list_msgs_for_user);
							System.out.println("Content in file in queue="+st);
							msg_queue.add(st);
							System.out.println("Messages in file in queue="+msg_queue);
							mapMsg_content.put(username, msg_queue);
							System.out.println("Map of messages for "+username+"="+mapMsg_content);
							
						}
					}
					
				 br.close();
				 System.out.println("Reading from map/queue");
				 for(Map.Entry<String, List<String>> mapElement :mapMsg_content.entrySet())
				 {
					 //String key = (String)mapElement.getKey(); 
					 String key= mapElement.getKey();
		            List<String> value_set = mapElement.getValue(); 
					// for(String value : mapElement.getValue())
			  
			            System.out.println("username="+key + " : "); 
			            System.out.println("messasges="+ value_set);
			            for(String msg_from_q : value_set)
			            {
			            	System.out.println(msg_from_q);
			            	
			           //sending back to client using SOutput 	
			            	writeMsg(msg_from_q);
			            	
	/*						 //remove from the queue
							 msg_queue.poll();
							 System.out.println("Queue after removing the element form the queue"+msg_queue);
							 
							 
							 //and then remove from the file
							 removeLineFromFile(file_name, msg_from_q);
		*/	            } //end- reading message for the user
			       }
			   }
			}	//try-end
			catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
        
// URL- https://stackoverflow.com/questions/1377279/find-a-line-in-a-file-and-remove-it	
		// function removes the read messages from the file
		public void removeLineFromFile(String file, String lineToRemove) {

				    try {

				      File inFile = new File(file);

				      if (!inFile.isFile()) {
				        System.out.println("Parameter is not an existing file");
				        return;
				      }

				      //Construct the new file that will later be renamed to the original filename.
				      File tempFile = new File(inFile.getAbsolutePath() + ".tmp");

				      BufferedReader br = new BufferedReader(new FileReader(file));
				      PrintWriter pw = new PrintWriter(new FileWriter(tempFile));

				      String line = null;

				      //Read from the original file and write to the new
				      //unless content matches data to be removed.
				      while ((line = br.readLine()) != null) {

				        if (!line.trim().equals(lineToRemove)) {

				          pw.println(line);
				          pw.flush();
				          System.out.println("Line removed");
				        }
				      }
				      pw.close();
				      br.close();

				      //Delete the original file
				      if (!inFile.delete()) {
				        System.out.println("Could not delete file");
				        return;
				      }

				      //Rename the new file to the filename the original file had.
				      if (!tempFile.renameTo(inFile))
				        System.out.println("Could not rename file");

				    }
				    catch (FileNotFoundException ex) {
				      ex.printStackTrace();
				    }
				    catch (IOException ex) {
				      ex.printStackTrace();
				    }
				  }
			
		
		public String getUsername()
		{
			return username;
		}
		
		
		// what will run forever- handles the messages
		public void run() {
		//	System.out.println("Inside server run");

			// to loop until LOGOUT
			boolean keepGoing = true;
			while(keepGoing) {
			//	System.out.println("3----socket created for="+username);
				// read a String (which is an object)
				try {
					cm = (ChatMessage) sInput.readObject();
				}
				catch (IOException e) {
					//display(username + " Exception reading Streams: " + e);
					break;				
				}
				catch(ClassNotFoundException e2) {
					break;
				}
				// the messaage part of the ChatMessage
				String message = cm.getMessage();

				// Switch on the type of message receive
				switch(cm.getType()) {

				case ChatMessage.MESSAGE:
					boolean confirmation = broadcast(username + ": " + message);
					if(confirmation==false){
						String msg =  "Sorry. No such user exists.\n" ;
						writeMsg(msg);
					}
					break;
				case ChatMessage.LOGOUT:
					display(username + " disconnected with a LOGOUT message.");
					Server.removeClientnames(username);
					sg.append_Clnt_List();
					keepGoing = false;
					break;
				case ChatMessage.ONLINEUSER:
					writeMsg("\n");
					writeMsg("\n **************List of the users connected at " + sdf.format(new Date()) + "**********\n");
					// scan al the users connected
					for(int i = 0; i < al.size(); ++i) {
						ClientThread ct = al.get(i);
						writeMsg((i+1) + ") " + ct.username + " since " + ct.date);
						//ServerGUI.send_Clnt_name(ct.username);
					//	sg.append_Clnt_List(ct.username);
					}
					writeMsg("\n");
					break;
				case ChatMessage.RECEIVE:
					System.out.println("Server- Will retrieve the msg for "+username);
				//	read_from_file();
					read_from_msg_queue_file(username);
				}
			}
			// remove myself from the arrayList containing the list of the
			// connected Clients
			remove(id);
			close();
		//	System.out.println("2----socket closed for="+username);
		//	System.out.println("Now, if there  is a new message for="+username+", it will be stored in the file "+ msg_file);
		//	write_to_file();
			
		}
		

		// try to close everything
		private void close() {
			// try to close the connection
			try {
				if(sOutput != null) sOutput.close();
			}
			catch(Exception e) {}
			try {
				if(sInput != null) sInput.close();
			}
			catch(Exception e) {};
			try {
				if(socket != null) socket.close();
			}
			catch (Exception e) {}
		}

	// this function will write the messages to the file
		public void write_to_file() {
			System.out.println("in write");
				try {
					 fw = new FileWriter("MessageFile.json");
					 String msg= cm.getMessage();
					 System.out.println("Message="+msg);
					 if(msg!=null) {
						 String s=username + " :"+ msg;
					// String st=username + " - Hey there buddy";
						fw.append(s);
					 }	
						fw.close();
						System.out.println("Content saved in file");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		
		}

		

	}
}

