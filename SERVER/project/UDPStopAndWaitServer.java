package project;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

public class UDPStopAndWaitServer  
	{

		public static void main(String[] args)throws Exception
			{
				//Step 1: Generating random sequence and key
				final int MAX_MSG_SIZE = 40;
				final int MPS_SIZE =30;
				Random random = new Random();                   //creating random object 
				int seq = random.nextInt();                     //creating a random sequence number
				ByteBuffer recivedMessage = ByteBuffer.allocate(600);

				System.out.println("*************DISTRIBUTED NETWORKING APPLICATION***************");
        
				System.out.println("THE 32 BIT SEQUENCE NUMBER ="+seq);;
				byte[] key = new byte[16]; 
				//byte[] localIntCheck = new byte[36];
				byte[] localIntCheck3 = new byte[5];
				byte[] localIntCheck = new byte[36];

				new Random().nextBytes(key);                   //Creating random key
				System.out.println("THE 28 BIT KEY =");
				for(int i =0;i<16;i++)
					{
						System.out.print(key[i]+",");
					}
				
				//Defining the variables
				byte[] seq_no = new byte[4];
				byte[] data = new byte[36];
				byte[] dataLast = new byte[26];

				int ack_no=0;                                   //Initializing the acknowledgement number
				int temp1=0,temp2;                              //Initializing the variables which would be later used to s
				byte[] compress = new byte[4];
				byte[] sentPacket1 = new byte[5];
				byte[] sentPacket2 = new byte[9];
				int packetCounter =1;
	
				//Step 2: To Get data into the socket
				
				int SENT_MSG_SIZE =10;                           //Defining maximum message size of the acknowledgement packet
				int SERVER_PORT_NUM = 9990;
				InetAddress address;
				int port;
		
				Rc4UDPStopandWaitServer rc4UDP = new Rc4UDPStopandWaitServer();  //Calling the RC4UDP class
		
				DatagramSocket serverSocket = new DatagramSocket(SERVER_PORT_NUM); // client's IP address object
				InetAddress clientAddress;

				//Step 3: Receiving client's request
				while(true) 
					{ 	
						byte[] receivedMessage = new byte[40];
						byte[] localIntCheckLast = new byte[26];						
						int lengthPacket ;
						byte[] c = new byte[4];
						byte[] payload = new byte[30];
						byte[] payloadLast = new byte[20];
		                byte[] d = new byte[4];
		               		                
						//Creating packet for the received data
						DatagramPacket receivePacket = new DatagramPacket(receivedMessage, receivedMessage.length);
						System.out.println(" ");
						System.out.println("SEVER IS READY");
						int counter = 0,j=0;
						System.out.println("READY TO RECEIVE "+packetCounter+"PACKET FROM THE TRANSMITTER");
						serverSocket.receive(receivePacket);
						System.out.println(packetCounter+"PACKET RECEIVED FROM THE TRANSMITTER");                
						clientAddress = receivePacket.getAddress(); 
						int clientPort = receivePacket.getPort(); 
						int dataLength = receivePacket.getLength();												
						Byte b = receivePacket.getData()[5];
						lengthPacket = b.intValue();
						
						                
						if(receivePacket.getData()[0] ==  (byte) 55)  
							{
					//STEP 4 : Storing the Header and Payload in data
								//lengthPacket = (int)receivePacket.getData()[5];
								for(int i=0 ; i <36;i++)
									{  									
										data[i] = receivePacket.getData()[i];
										             	  
									}
								for(int i=6;i<36;i++)
									{
										payload[j]=receivePacket.getData()[i];
										j++;
									}
								recivedMessage.put(payload);
								counter = 36;
							}
						else
							{
								//lengthPacket = receivePacket.getData()[5];

									for(int i=0 ; i <26;i++)
									{  
										dataLast[i] = receivePacket.getData()[i];
																			
									}
								for(int i=6;i<26;i++)
									{
										payloadLast[j]=receivePacket.getData()[i];
										j++;
									}
								recivedMessage.put(payloadLast);
								counter = 26;
             	  
							}	

						for(int i=0;i<4;i++) //Storing the sequence number
							{  
								seq_no[i] = receivePacket.getData()[i+1];
								
							}
              
						ByteBuffer wrapped = ByteBuffer.wrap(seq_no);
						int num = wrapped.getInt();
						
						System.out.println("****PACKET"+packetCounter+"***");

						
						System.out.println("****SEQUENECE NUMBER CHECK***");
												
              //STEP 5:Matching the sequence number received to the generated sequence number
						if(num==seq)
							{	
								System.out.println("THE EXPECTED SEQUENEC NUMBER OF THE "+ packetCounter+"PACKET FROM THE TRANSMITTER IS"+seq);
								System.out.println("SEQUENEC NUMBER OF "+packetCounter+"FROM THE TRANSMITTER ="+num);

								System.out.println("SEQUENECE NUMBERS ARE MATCHED!");
								seq= seq+30;      //Increment the sequence number
								ack_no=seq;       //Ack no is the ordinal number of the next expected byte of the next payload
								temp1=1;
							}
						else 
							{
								System.out.println("SEQUENECE NUMBERS ARE NOT MATCHED!");
								
							}
                        
				//STEP 6: Integrity Check
              
						for(int m=0 ; m <4;m++)
							{
								
								compress[m] = receivePacket.getData()[counter];
								counter++;
								
							}
              
						if(receivePacket.getData()[0] ==  (byte) 55)             //call for the 1st 16 packets
							{
								localIntCheck = rc4UDP.rc4_call(key,data);       //performing RC4 on the data packet to check for IC
								c = compressData(localIntCheck);                  //compressing the locally generated IC bytes
							}
						else
							{
								localIntCheckLast =rc4UDP.rc4_call(key,dataLast); //call for the last packet
								c = compressData(localIntCheckLast);
              
							}
			      		      	
						if(Arrays.equals(compress, c))
      			
							temp2 =1;                                             //Integrity Check Success
						else 
							temp2 =0;
						
						System.out.println("****INTERGRITY CHECK***");
      		
						if(temp2==1)
							
							System.out.println("INTERGRITY CHECK SUCCESS FOR THE "+ packetCounter+" PACKET");
              
						else
							{
								System.out.println("INTERGRITY CHECK FAILURE FOR THE "+ packetCounter+" PACKET");
								
								System.exit(0);
								break;
              
							}
						ByteBuffer bufferPacket = ByteBuffer.allocate(5);
              
						ByteBuffer bufferPacket2 = ByteBuffer.allocate(9);
              
						byte pt = (byte) 0xFF;    //Packet type for the acknowledgement packet
              
						//STEP 7: If all 3 conditions satisfy , append the packet
						
						System.out.println("*****SEQUENCE NUMBER****INTERGRITY CHECK***MPS CHECK*****");

						
						if((temp1==1) && (temp2==1) && (lengthPacket <= MPS_SIZE)) //3 conditions 
							
							{  
            	  
							System.out.println("** ** ** SUCCESS ** ** **");
								bufferPacket.put(pt);                              //append packet type
								bufferPacket.putInt(ack_no);                       //append ack no
            	
            	 
							}
						else
							{
							System.out.println("FAILURE!!");
							System.out.println("DISCARDING THE PACKET");
							}
        
						for(int i=0;i<5;i++)
							{
            	  
								sentPacket1[i] = bufferPacket.get(i); 
							}	

						localIntCheck3 = rc4UDP.rc4_call(key,sentPacket1);	     //	call to find Integrity check bytes for acknowledgement packet	
              
						d = compressData(localIntCheck3);
        
						bufferPacket2 =  concatenateByteArrays(ack_no,d); //calling the method to append the packet
              
						for(int i=0;i<9;i++)
							{
                  
								sentPacket2[i] = bufferPacket2.get(i);
							}
              

						address = receivePacket.getAddress();
						port = receivePacket.getPort();
						
						//STEP 8: Sent the acknowledgement packet back to the transmitter
              
						DatagramPacket sentPacket = new DatagramPacket(sentPacket2, sentPacket2.length,address,port);
					                                                             	//sending the response to the client
						System.out.println("SENDING ACK FOR "+packetCounter+" PACKET");
						serverSocket.send(sentPacket);
						packetCounter++;
						System.out.println("ACKNOWLEDGEMENT SENT ");	
						receivePacket.setLength(MAX_MSG_SIZE);
						if(receivePacket.getData()[0] ==  (byte) 0xAA)  //displaying the received msg for the last payload
							{
								System.out.println("The Data recieved from client");
								for(int i=0;i<recivedMessage.position();i++)
									System.out.println("The "+i+" data = "+recivedMessage.get(i));
							}

					}
		
			
		}
		
	public static	ByteBuffer concatenateByteArrays(int a, byte[] b) //Append data to create acknowledgement packet
	
		{ 
		   
		 	ByteBuffer result = ByteBuffer.allocate(10);	   
		 	byte pt = (byte) 0xFF;		   		   
		 	result.put(pt);    //append packet type
		 	result.putInt(a);  //append acknowledgement number
		 	result.put(b);     //append Integrity check bytes
		    return result;
		} 
	
	static byte[] compressData(byte[] data)                //compress the IC 4 bytes
		{
			byte[] c = new byte[4];
		
			for(int r=0 ; r<data.length; r++)
				{
			       //XORing the values
					if(r%4==0)
							c[0] = (byte)(c[0] ^ data[r]);
					else if(r%4==1)
							c[1] = (byte)(c[1] ^ data[r]);
					else if(r%4==2)
							c[2] = (byte)(c[2] ^ data[r]);
					else if(r%4==3)
							c[3] = (byte)(c[3] ^ data[r]);			
				}
			return c;
		}

}