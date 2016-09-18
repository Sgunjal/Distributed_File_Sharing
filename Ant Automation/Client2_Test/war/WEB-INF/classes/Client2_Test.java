import java.net.*;
import java.io.*;
import java.util.*;

public class Client2_Test extends Thread
{
	String type;										// variable declaration 
	Socket incoming_socket;
	Socket[] s2_array=new Socket[10];							// Crate socket array
	BufferedReader[] s_input2_array=new BufferedReader[10];    // Crate BufferedReader array
	PrintWriter[] s_out2_array=new PrintWriter[10];				// Crate PrintWriter array
	int server_count=0;
	Socket[] s2_array_bkp=new Socket[10];							// Crate socket replica array
	BufferedReader[] s_input2_array_bkp=new BufferedReader[10];     // Crate BufferedReader replica array
	PrintWriter[] s_out2_array_bkp=new PrintWriter[10];             // Crate PrintWriter replica array
	int this_server=2;
	static Hashtable<String, String> list=new Hashtable<String, String>();   // Create hashtable 
	static String []ip =new String[10];
	static String []replica_ip =new String[10];
	static int []port =new int[10];
	static int []replica_port =new int[10];
	FileOutputStream fos = null;
	BufferedOutputStream bos = null;
	FileInputStream fis = null;
	BufferedInputStream bis = null;	
	OutputStream os=null;
	int total=0;
	int dataSize;
	int read;
	
	File file_list=null;
	Client2_Test(String type)								// Constructor
	{
		this.type=type;
	}
	Client2_Test(Socket s,String type)						/// Constructor 
	{
		this.type=type;
		incoming_socket=s;
	}
	synchronized public void run()                  
	{		
		try
		{
			if(type.equals("Server"))                             // Execute this if stattement only if its server.
			{
				ServerSocket ss=new ServerSocket(25002);		// Create server socket on port 25002 which will listen to incoming socket request
				type="Request";									// apply requester type to incoming connections
				while(true)
				{
					 Socket s = ss.accept();  		            // accpet incoming request
					 Thread t1 =new Client2_Test(s,"Request");	// Create separate thread for each incoming request.
					 t1.start();
				}				
			}
			else
			{				
				BufferedReader sc_input=new BufferedReader(new InputStreamReader(System.in));           // Create BufferedReader object
				BufferedReader s_input=new BufferedReader(new InputStreamReader(incoming_socket.getInputStream()));     // Create BufferedReader object
				PrintWriter s_out=new PrintWriter(incoming_socket.getOutputStream(),true);          // Create PrintWriter object   		
			//	os = incoming_socket.getOutputStream();
				Server_request(incoming_socket,sc_input, s_input, s_out);											// call server request menu.
			}
		}
		catch(Exception e)
		{			
			//System.out.println();
		}
	}
	synchronized public void Server_request(Socket incoming_socket1,BufferedReader sc_input,BufferedReader s_input,PrintWriter s_out) throws Exception
	{ 														// This procedure will server the incoming request from clients.
		String user_choice,key1,value1,get1,delete1,str,rline,dl_file;
		Enumeration names; 
		int choice;		
		try
		{	
			//System.out.println("Reached Server menu");
			choice=Integer.parseInt(s_input.readLine());					
			switch(choice)
			{
				case 1: 											/// Case 1 will provide put operation service
				//		System.out.println("Put Server option");
						key1=s_input.readLine();
					//	System.out.println("Received key" +key1);
						value1=s_input.readLine();																								
					//	System.out.println("Received value" +value1);
						if((rline=list.get(key1))!=null)
						{
							rline=rline.concat(",");
							value1=rline.concat(value1);
							list.put(key1,value1); 
						//	System.out.println("value is " +value1);						
						}
						else
						list.put(key1,value1); 						// put object from hashtable		
						s_out.println("File registered successfully !!!");						
						Server_request( incoming_socket1,sc_input, s_input, s_out);						
						break;
				case 2:												/// Case 2 will provide get operation service
						//System.out.println("Get Server option");
						get1=s_input.readLine();
						//System.out.println("key to search " +get1);
						s_out.println(list.get(get1));					// get object from hashtable		
						Server_request(incoming_socket1, sc_input, s_input, s_out);
						break;
				case 3:												/// Case 3 will provide delete operation service
						//System.out.println("Get Server option");
						get1=s_input.readLine();
						//System.out.println("key to search " +get1);
						s_out.println(list.get(get1));					// get object from hashtable		
						Server_request(incoming_socket1, sc_input, s_input, s_out);
						break;
				case 4:		
						dataSize=0;
						int read=0;
						 fos = null;
						 bos = null;
						 fis = null;
						 bis = null;	
						 OutputStream os1 = null;
						 file_list = null;
						//System.out.println("Sendfile ");
						dl_file=s_input.readLine();			
						//System.out.println(dl_file);
						file_list=new File("Myfiles/"+dl_file);   								 // Open Indexing file 
					
						os1=incoming_socket1.getOutputStream();
						
						
						if (!file_list.exists()) 												// Check if file exist or not if not exist then create new file
						{
							System.out.println(dl_file+" File does not exist");
						}
						else
						{		
							byte [] mybytearray;
							fis = new FileInputStream(file_list);
							bis = new BufferedInputStream(fis);
							
							float filesizeByte=(float)file_list.length();
							//System.out.println("filesizeByte "+filesizeByte);							
							
							boolean uploadProgress=true;
							if(filesizeByte<65536)				
								dataSize=(int)filesizeByte;
							else				
								dataSize=65536;
								//System.out.println("dataSize "+dataSize);
								s_out.println(dataSize);
								while(uploadProgress)
								{
									mybytearray=new byte[dataSize];
									read=bis.read(mybytearray,0,mybytearray.length);
					
							//		System.out.println("Read "+read);
								//	System.out.println("Writing file1");
									os1.write(mybytearray,0,mybytearray.length);					
							//		System.out.println("Writing file");
									if(read<0 || dataSize<65536)									
										uploadProgress=false;									
								}										
								bis.close();
								os1.close();
								fis.close();
							//	file_list.close();
								//System.out.println("Done.");
								System.out.println("File "+dl_file+" Sent");								
						}					
						Server_request( incoming_socket1,sc_input, s_input, s_out);
						break;
					case 5: 		
							FileOutputStream fos_r2 = null;
							BufferedOutputStream bos_r2 = null;
							File file_list_r2 = null;
							InputStream is_r2 = null;
							
							is_r2 = incoming_socket1.getInputStream();			
							BufferedReader s_input_r=new BufferedReader(new InputStreamReader(is_r2));     // Create BufferedReader object
							
						//	System.out.println("Reached case 5");
							//s_input.close();
							dl_file=s_input.readLine();						
							int bytesRead;
							
							int datasize1;
							int flag1=1;							
							
							//System.out.println(is_r2);						
							datasize1=Integer.parseInt(s_input.readLine());
							//System.out.println("Data Size is:-"+datasize1);
							byte [] mybytearray_r;	
							int count=1;			
							bytesRead=0;
							fos_r2 = new FileOutputStream("Myfiles/"+dl_file);
							bos_r2 = new BufferedOutputStream(fos_r2);							
							int totalc=0;							
							bos_r2.flush();
							s_out.flush();
							
							do
							{														
								mybytearray_r= new byte [datasize1];
								bytesRead = is_r2.read(mybytearray_r, 0, datasize1);
								totalc=totalc+bytesRead;
								if(bytesRead > 1  ) 						
								bos_r2.write(mybytearray_r, 0 , bytesRead);			
								//System.out.println(bytesRead);								
								count++;
							}while(bytesRead > 0 );		
							//System.out.println("Count is "+count);
							//System.out.println("Total byest received "+totalc);
					//		System.out.println(dl_file + " File has been successfully Downloaded ");	
							bos_r2.flush();
							s_out.flush();
							bos_r2.close();
							fos_r2.close();							
							incoming_socket1.close();
							is_r2.close();							
														
							break;										
					}							
		}
		catch(Exception e)
		{			
			//System.out.println();
		}
	}
	synchronized public void client_side() throws Exception					// This procedure will handle client processs 
	{
		try
		{			
			int x1;
			int y;
			BufferedReader sc_input1=new BufferedReader(new InputStreamReader(System.in));     // Create  BufferedReader object 
			System.out.println("Enter number of servers you want to initiate :- ");            // Accept number of servers to create input 
			server_count=Integer.parseInt(sc_input1.readLine());	
			y=server_count-1;
			//System.out.println("y is "+y);
			for(int z=0;z<=(server_count-1);z++)												// Create socket connections for each server 
			{				
				InetAddress ia1=InetAddress.getByName(ip[z]);		
				s2_array[z] =new Socket(ia1,port[z]);		
				s_input2_array[z]=new BufferedReader(new InputStreamReader(s2_array[z].getInputStream())); 		
				s_out2_array[z]=new PrintWriter(s2_array[z].getOutputStream(),true);
		
			//	System.out.println("Backup nodes !!!");												// Create socket connections for each replica server 
				InetAddress ia2=InetAddress.getByName(ip[y]);		
		//		System.out.println(ia2);
				s2_array_bkp[z] =new Socket(ia2,port[y]);		
			//	System.out.println(s2_array_bkp[z]);
				s_input2_array_bkp[z]=new BufferedReader(new InputStreamReader(s2_array_bkp[z].getInputStream())); 		
				s_out2_array_bkp[z]=new PrintWriter(s2_array_bkp[z].getOutputStream(),true);	
				y--;
			}
				
			Client_Choice(sc_input1,s2_array,s_input2_array,s_out2_array);								
		}
		catch(Exception e)
		{			
			System.out.println(e);
		}
	}
	synchronized public void Client_Choice(BufferedReader sc_input1,Socket[] s2_array,BufferedReader[] s_input2_array,PrintWriter[] s_out2_array)
	{																// This procedure will handle the client choice of operations
		try
		{		
		int choice;
		String repeat_menu;		
		//System.out.println("Reached Client menu");
		System.out.println("1: Register File ");								// Desplay these choices to user
		System.out.println("2: Search File ");
		System.out.println("3: Obtain File ");
		System.out.println("4: Exit ");
		System.out.println();
		System.out.println("Enter your choice:- ");									
		choice=Integer.parseInt(sc_input1.readLine());
		
		switch(choice)
		{	
			case 1: 
			//		System.out.println("Put Client option");
					client_put(s2_array,s_input2_array,s_out2_array);	// Handle put operation of client
					System.out.println("Do you want to repeat menu (Y/N) ?");
					repeat_menu=sc_input1.readLine();											//	This code will ask user if he wants to repeat oparation or not if
					if((repeat_menu.equals("Y")) || (repeat_menu.equals("y")))											//	Y then call begin() method to repeat menu else 
					{																		//	else closes session.						
						Client_Choice(sc_input1,s2_array,s_input2_array,s_out2_array);
					}
					else
						System.exit(0);
					break;
			case 2: 
				//	System.out.println("Get Client option");
					search_file(s2_array,s_input2_array,s_out2_array);		// Handle get operation of client
					System.out.println("Do you want to repeat menu (Y/N) ?");
					repeat_menu=sc_input1.readLine();											//	This code will ask user if he wants to repeat oparation or not if
					if((repeat_menu.equals("Y")) || (repeat_menu.equals("y")))											//	Y then call begin() method to repeat menu else 
					{																		//	else closes session.						
						Client_Choice(sc_input1,s2_array,s_input2_array,s_out2_array);
					}
					else
						System.exit(0);
					break;
			case 3: 
					//System.out.println("Get Client option");
					client_get(s2_array,s_input2_array,s_out2_array);		// Handle get operation of client
					System.out.println("Do you want to repeat menu (Y/N) ?");
					repeat_menu=sc_input1.readLine();											//	This code will ask user if he wants to repeat oparation or not if
					if((repeat_menu.equals("Y")) || (repeat_menu.equals("y")))											//	Y then call begin() method to repeat menu else 
					{																		//	else closes session.						
						Client_Choice(sc_input1,s2_array,s_input2_array,s_out2_array);
					}
					else
						System.exit(0);
					break;
			case 4:
						System.exit(0);
						break;	
		   default:
					break;
		}		
		}
		catch(Exception e)
		{			
			//System.out.println();
		}
	}
	synchronized public void client_put(Socket[] s2_array1,BufferedReader[] s_input2_array1,PrintWriter[] s_out2_array1)
	{								// This procedure will handle put operation of client
		try
		{
			String key,value,xxxx,rline;
			char c;
			int add,v,index,port_no;
			int flag=0;
			int count=0;
			int connect_server=0;
			BufferedReader c_put=new BufferedReader(new InputStreamReader(System.in));       // Accept key 	from user		
			System.out.println("Enter the Name of file to store:-");
			key=c_put.readLine();
			count=server_count-1;
	//		System.out.println("Enter the value to store against above key:-");				// Accept value from user			
	//		value=c_put.readLine();
			
			add=0;
			for(int i=0;i<key.length();i++)                             					// add ascii value of all characters.
			{
				c=key.charAt(i);
				v=(int)c;
				add=add+v;
			}		
			index=add%server_count;															/// hash function will calculate which server to connect.
			//System.out.println("Index is :- "+index);
			connect_server=count-index;
			//System.out.println("Count "+count+"connect_server  "+connect_server);
			try
			{
			//	System.out.println("Key & Value sent to server to store");
				s_out2_array1[index].println("1");											// send option 1 to server 
				s_out2_array1[index].println(key);                                          // Send key to server 
				s_out2_array1[index].println("2");                                         // Send value to server 
				if(!(rline=s_input2_array1[index].readLine()).equals("null"))               // accept message from server
				{
					if(this_server!=index)
					{
							replica_file(index,key);
					}
					System.out.println(rline);
				}
				
				
				flag=1;				
			}	
		
			catch(java.net.SocketException e)										// handle SocketException to maintain replica
			{		
				try
				{
					s_out2_array_bkp[index].println("1");							// send option 1 to server 
					s_out2_array_bkp[index].println(key);                           // Send key to server 
					s_out2_array_bkp[index].println(connect_server);                          // Send value to server 
					System.out.println(s_input2_array_bkp[index].readLine());       // accept message from server
					if(this_server!=connect_server)
					{
							//System.out.println("Sending files to replica");
							replica_file(connect_server,key);
					}
					
				}
				catch(java.net.SocketException e1)
				{
				}
			}
			catch(java.lang.NullPointerException e)										// handle NullPointerException to maintain replica
			{		
				try
				{
					s_out2_array_bkp[index].println("1");							// send option 1 to server 
					s_out2_array_bkp[index].println(key);                           // Send key to server 
					s_out2_array_bkp[index].println(connect_server);                          // Send value to server 
					System.out.println(s_input2_array_bkp[index].readLine());       // accept message from server
					if(this_server!=connect_server)
					{
							//System.out.println("Sending files to replica");
							replica_file(connect_server,key);
					}
				}
				catch(java.lang.NullPointerException e1)
				{
				}
			}
			try
			{
				if(flag==1)
				{
					s_out2_array_bkp[index].println("1");							// send option 1 to server 
					s_out2_array_bkp[index].println(key);                           // Send key to server 
					s_out2_array_bkp[index].println(connect_server);                          // Send value to server 					
					xxxx=s_input2_array_bkp[index].readLine();					// accept message from server
					if(this_server!=connect_server)
					{
							//System.out.println("Sending files to replica");
							replica_file(connect_server,key);
					}
					flag=0;
				}
			}	
			catch(java.net.SocketException e)
			{
				flag=0;
			}
			catch(java.lang.NullPointerException e)
			{
				flag=0;
			}
			}
		catch(Exception e)
		{						
			System.out.println(e);
		}
	}
	synchronized public void replica_file(int connect_Server,String dl_file)
	{
			try
			{
						int dataSize=0;
						int read=0;
						 FileOutputStream fos3 = null;
						 BufferedOutputStream bos3 = null;
						 FileInputStream fis3 = null;
						 BufferedInputStream bis3 = null;	
						 OutputStream os3 = null;
						 File file_list1 = null;
						 
						 InetAddress ia4=InetAddress.getByName(ip[connect_Server]);		
						 Socket sok2 =new Socket(ia4,port[connect_Server]);				
						 BufferedReader gs_input2=new BufferedReader(new InputStreamReader(sok2.getInputStream()));     // Create BufferedReader object
						 PrintWriter gs_out2=new PrintWriter(sok2.getOutputStream(),true);          // Create PrintWriter object   	
						 
				//		System.out.println("Send backup file ");					
						//System.out.println(dl_file);
						file_list1=new File("Myfiles/"+dl_file);   								 // Open Indexing file 
					
						os3=sok2.getOutputStream();
						
						if (!file_list1.exists()) 												// Check if file exist or not if not exist then create new file
						{
							System.out.println(dl_file+" File does not exist");
						}
						else
						{		
							gs_out2.println("5");
							gs_out2.println(dl_file);
							byte [] mybytearray;
							fis3 = new FileInputStream(file_list1);
							bis3 = new BufferedInputStream(fis3);
							total=0;
							float filesizeByte=(float)file_list1.length();
					///		System.out.println("filesizeByte "+filesizeByte);							
							
							boolean uploadProgress=true;
							if(filesizeByte<65536)				
								dataSize=(int)filesizeByte;
							else				
								dataSize=65536;
						//		System.out.println("dataSize "+dataSize);
								gs_out2.println(dataSize);
								gs_out2.flush();
								os3.flush();
								while(uploadProgress)
								{
									mybytearray=new byte[dataSize];
									read=bis3.read(mybytearray,0,mybytearray.length);
									total=total+read;
									//System.out.println("Read "+read);
									//gs_out2.println(read);
								//	System.out.println("Writing file1");
									os3.write(mybytearray,0,mybytearray.length);					
							//		System.out.println("Writing file");
									if(read<0 || dataSize<65536)									
									uploadProgress=false;									
								}										
								bis3.close();
								os3.close();
								fis3.close();
								sok2.close();
							//	file_list.close();
							//	System.out.println("Total bytes read is "+total);
							//	System.out.println("Done.");
								//System.out.println("Replica File "+dl_file+" Sent");								
						}														
			}						
		catch(Exception e)
		{			
			System.out.println(e);
		}			
	}
	synchronized public void get_file(Socket[] s2_array1,BufferedReader[] s_input2_array1,PrintWriter[] s_out2_array1,String dl_file)
	{
		try
		{
			int peerid;
			int bytesRead;
			String readl;
			BufferedReader c_putd=new BufferedReader(new InputStreamReader(System.in)); 

			System.out.println("Enter peerid from which you want to download file");
			peerid=Integer.parseInt(c_putd.readLine());
		
		
			if(peerid==server_count)
			{
				peerid=peerid-server_count;
			//	s_out2_array1[peerid].println("4");
			//	s_out2_array1[peerid].println(dl_file);
			}
			else
			{				
			//	s_out2_array1[peerid].println("4");
			//	s_out2_array1[peerid].println(dl_file);
			}
			//	BufferedReader gsc_input=new BufferedReader(new InputStreamReader(System.in));           // Create BufferedReader object
			InetAddress ia3=InetAddress.getByName(ip[peerid]);		
			Socket sok =new Socket(ia3,port[peerid]);				
			BufferedReader gs_input=new BufferedReader(new InputStreamReader(sok.getInputStream()));     // Create BufferedReader object
			PrintWriter gs_out=new PrintWriter(sok.getOutputStream(),true);          // Create PrintWriter object   		
			gs_out.println("4");
			gs_out.println(dl_file);	
							
			FileOutputStream fos1 = null;
			BufferedOutputStream bos1 = null;
			File file_list1 = null;
			InputStream is = null;
			int datasize1;
			int flag1=1;
			
			is = sok.getInputStream();			
			//System.out.println(is);
			//System.out.println("Client connected to server !!!");												
			file_list1=new File("Myfiles/"+dl_file);
			datasize1=Integer.parseInt(gs_input.readLine());
			//System.out.println("Data Size is:-"+datasize1);
			byte [] mybytearray  = new byte [datasize1];	
			int count=1;			
			bytesRead=0;
				fos1 = new FileOutputStream("Myfiles/"+dl_file);
				bos1 = new BufferedOutputStream(fos1);							
				do
				{	
					
					//System.out.println("hi");									
					bytesRead = is.read(mybytearray, 0, datasize1);
								
					if(bytesRead > 1  ) 						
					bos1.write(mybytearray, 0 , bytesRead);			
				//	System.out.println(bytesRead);								
				
				}while(bytesRead > 0 );		
				//System.out.println("Count is "+count);
				System.out.println(dl_file + " File has been successfully Downloaded ");
				bos1.close();
				fos1.close();
				sok.close();
			//	file_list1.close();
				is.close();
				
		}
		catch(Exception e)
		{
			//System.out.println("get_file");
			System.out.println(e);
		}	
	}
	synchronized public void search_file(Socket[] s2_array1,BufferedReader[] s_input2_array1,PrintWriter[] s_out2_array1)
	{
		try
		{
		String get_key,get_string;
		char c;
		BufferedReader c_put=new BufferedReader(new InputStreamReader(System.in)); 
		int add,v,index,port_no;
		System.out.println("Enter the name of file to Search:- ");				// Accept key to get from user 
		get_key=c_put.readLine();
		add=0;
		for(int i=0;i<get_key.length();i++)          					// add ascii value of all characters.
			{
				c=get_key.charAt(i);
				v=(int)c;
				add=add+v;
			}			
			index=add%server_count;										/// hash function will calculate which server to connect.
			
			try
			{
			
			s_out2_array1[index].println("2");									// send option 2 to server 
			s_out2_array1[index].println(get_key);                              					// Send key to get to server 
			if(!((get_string=s_input2_array1[index].readLine()).equals("null")))
			{
				System.out.println("List of peer Id who has this file:-  "+get_string);   // Print value sent by server.
				//get_file(s2_array1,s_input2_array1,s_out2_array1,get_key);
			}
			else
			{
				System.out.println("Null Key does not found ");                   // If key does not found
			}
			}			
			catch(java.net.SocketException e)										// handle SocketException to maintain replica
			{			
		//		System.out.println("SocketException");			
				s_out2_array_bkp[index].println("2");						// send option 2 to server 
				s_out2_array_bkp[index].println(get_key);                   					// Send key to get to server 
				if((get_string=s_input2_array_bkp[index].readLine()).equals("null"))
				{
					System.out.println("Null Key does not found");
				}
				else
				{
					System.out.println("List of peer Id who has this file:-  "+get_string);
				//	get_file(s2_array_bkp,s_input2_array_bkp,s_out2_array_bkp,get_key);
				}
			}	
			catch(java.lang.NullPointerException e)										// handle NullPointerException to maintain replica
			{			
		//		System.out.println("SocketException");			
				s_out2_array_bkp[index].println("2");							// send option 2 to server 
				s_out2_array_bkp[index].println(get_key);                       					// Send key to get to server 
				if((get_string=s_input2_array_bkp[index].readLine()).equals("null"))
				{
					System.out.println("Null Key does not found");
				}
				else
				{
					System.out.println("List of peer Id who has this file:-  "+get_string);
				//	get_file(s2_array_bkp,s_input2_array_bkp,s_out2_array_bkp,get_key);
				}
			}	
		}
		catch(Exception e)
		{			
			System.out.println(e);
		}
	}
	synchronized public void client_get(Socket[] s2_array1,BufferedReader[] s_input2_array1,PrintWriter[] s_out2_array1)
	{										// This procedure will handle clients get operation
		try
		{
		String get_key,get_string;
		char c;
		BufferedReader c_put=new BufferedReader(new InputStreamReader(System.in)); 
		int add,v,index,port_no;
		System.out.println("Enter the name of file to download:- ");				// Accept key to get from user 
		get_key=c_put.readLine();
		add=0;
		for(int i=0;i<get_key.length();i++)          					// add ascii value of all characters.
			{
				c=get_key.charAt(i);
				v=(int)c;
				add=add+v;
			}			
			index=add%server_count;										/// hash function will calculate which server to connect.
			
			try
			{
			
			s_out2_array1[index].println("2");									// send option 2 to server 
			s_out2_array1[index].println(get_key);                              					// Send key to get to server 
			if(!((get_string=s_input2_array1[index].readLine()).equals("null")))
			{
				System.out.println("List of peer Id who has this file:-  "+get_string);   // Print value sent by server.
				get_file(s2_array1,s_input2_array1,s_out2_array1,get_key);
			}
			else
			{
				System.out.println("Null Key does not found ");                   // If key does not found
			}
			}			
			catch(java.net.SocketException e)										// handle SocketException to maintain replica
			{			
		//		System.out.println("SocketException");			
				s_out2_array_bkp[index].println("2");						// send option 2 to server 
				s_out2_array_bkp[index].println(get_key);                   					// Send key to get to server 
				if((get_string=s_input2_array_bkp[index].readLine()).equals("null"))
				{
					System.out.println("Null Key does not found");
				}
				else
				{
					System.out.println("List of peer Id who has this file:-  "+get_string);
					get_file(s2_array_bkp,s_input2_array_bkp,s_out2_array_bkp,get_key);
				}
			}	
			catch(java.lang.NullPointerException e)										// handle NullPointerException to maintain replica
			{			
		//		System.out.println("SocketException");			
				s_out2_array_bkp[index].println("2");							// send option 2 to server 
				s_out2_array_bkp[index].println(get_key);                       					// Send key to get to server 
				if((get_string=s_input2_array_bkp[index].readLine()).equals("null"))
				{
					System.out.println("Null Key does not found");
				}
				else
				{
					System.out.println("List of peer Id who has this file:-  "+get_string);
					get_file(s2_array_bkp,s_input2_array_bkp,s_out2_array_bkp,get_key);
				}
			}	
		}
		catch(Exception e)
		{			
			System.out.println(e);
		}
	}
	synchronized public static void main(String args[]) throws Exception
	{												
		try
		{			
			int i,j;
			String rline;
			File server_address=new File("Servers.txt");
			BufferedReader br4=new BufferedReader(new InputStreamReader(System.in)); 	// Create BufferedReader object
			if(!server_address.exists())
			{
				System.out.println("File does not exist !!!");
			}
			else
			{
				FileReader fr=new FileReader("Servers.txt");						// read Servers.txt file
				BufferedReader br=new BufferedReader(fr);							// Create BufferedReader object
				i=0;
			//	j=7;
				while((rline = br.readLine()) != null)  							// Read file till end.
					{
					//	System.out.println(rline);
						ip[i]=rline.substring(0,rline.indexOf(" "));				// copy Ip address from file
					//	replica_ip[j]=rline.substring(0,rline.indexOf(" "));	
					//	System.out.println("Ip is "+ip[i]);
						port[i]=Integer.parseInt(rline.substring(rline.indexOf(" ")+1));    // copy port from file
					//	replica_port[j]=Integer.parseInt(rline.substring(rline.indexOf(" ")+1));
					//	System.out.println("Port is "+port[i]);
						i++;	
					//	j--;
					}    					
					br.close();     
			}
			System.out.println();
							
			Client2_Test ct=new Client2_Test("Client");									// Create client object 
			Thread t=new Client2_Test("Server");										// Create server thread to accept incoming socket
			t.start();																	// call run menthod
			ct.client_side();															// call client_side method.
		}
		catch(Exception e)
		{			
			System.out.println(e);
		}
	}
}
