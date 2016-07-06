/**
 * 
 */
package project;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * @author Purva & Ranjitha
 *	version 1.8
 */
public class UDPSender extends Rc4UDP{


public  boolean sender(ByteBuffer sendData, int index) throws Exception
{ 
	
		//Intialzing the variables
		final int SERVER_PORT_NUM = 9990;
	
		final int SEQ_NUM_SIZE =4;
	
		final int INTERGRITY_SIZE =4;
	
		final int ACK_PKT_TYPE_ACKNUM =5;

		Rc4UDP rc4Udp = new Rc4UDP();
	
		byte[] localIntegrityCheck = new byte[INTERGRITY_SIZE];
	
		byte[] localSeqCheck = new byte[SEQ_NUM_SIZE];
	
		int localAckNum ;
	
		int packetCounter =index;
		
		//key inputed again to calaculate intergrity chech for ack
	
		byte[] key ={4,5,97,-103,-89,-1,61,60,-32,47,-7,-109,-12,126,-102,19}; //key

		int retransmissionCounter = 0;
    
		// For verifying the acknowledgement
		boolean ackRecievedCorrect = false;
		boolean ackPacketReceived = false;
    
		byte[] recieverkPacketAcknum= new byte[ACK_PKT_TYPE_ACKNUM];  
    
		//STEP 1: extracting the integrity check value from the data

		int intergrityPos = sendData.position()-INTERGRITY_SIZE;

		int sequencePos=1;

    //STEP 2: Extracting integrity check from the data packet to be sent
    				for(int i =0;i<INTERGRITY_SIZE;i++)
    					{
    						localIntegrityCheck[i] = sendData.get(intergrityPos);
    						intergrityPos++;
    						
    					}
    //STEP 3: Extracting sequence number check from the data packet to be sent

    				for(int i =0;i<SEQ_NUM_SIZE;i++)
    					{
    						localSeqCheck[i] =sendData.get(sequencePos);
    	
    						sequencePos++;
    					}
    
    				
    //Conversion of int to byte for sequence number 
    ByteBuffer wrapped = ByteBuffer.wrap(localSeqCheck);
    
    
    int localSequence = wrapped.getInt(); 
    
    // STEP 4: calculation of acknowledgement number
    localAckNum = localSequence +30;
	
    
    // STEP 5: Converting ByteBuffer to Byte array -->sendData
	byte[] sentMessage = new byte[sendData.position()];
	
	for(int o=0;o<sendData.position();o++)
		{
			sentMessage[o] =  sendData.get(o);
		}
	
	DatagramSocket clientSocket = new DatagramSocket(); 
	
	int time =1000; //timer
	
	// STEP 6: creating the UDP client socket (randomly chosen client port number)
	

	// sending the UDP packet to the server
	
		while((!ackRecievedCorrect) && retransmissionCounter<4)
			{
					clientSocket.setSoTimeout( time);
	
					if(retransmissionCounter!=0)
						{
							time = time*2;  //increment the timer by 2, for each retransmission
						}
					
					InetAddress server = InetAddress.getLocalHost();

	//STEP 7: creating the receive UDP packet
					
					byte[] receivedMessage = new byte[9]; 
	
					try
						
						{
							
							DatagramPacket sentPacket = new DatagramPacket(sentMessage, sentMessage.length, server, SERVER_PORT_NUM);
		
							System.out.println("SENDING THE  "+ packetCounter +"PACKET ");
		
							clientSocket.send(sentPacket);
							
							clientSocket.setSoTimeout( time); //Setting the timer

		
							System.out.println(packetCounter +"PACKET SENT ");

	//STEP 8: Receiving the ACK packet
							DatagramPacket receivedPacket = new DatagramPacket(receivedMessage,receivedMessage.length);
		
							System.out.println("RECIEVING THE ACKNOWLEDGEMENT FOR THE "+packetCounter+" PACKET");
		
							clientSocket.receive(receivedPacket);
		
							System.out.println("ACKNOWLEDGEMENT RECIEVED FOR"+packetCounter+"PACKET");
		
							ackPacketReceived = true;
		
							//*******************************************************************************
		
							byte[] recieveData = new byte[receivedPacket.getLength()];
		
							for(int i=0;i<receivedPacket.getLength();i++)
								{
									recieveData[i] = receivedPacket.getData()[i];
								}

		
							ByteBuffer recieveBuffer = ByteBuffer.allocate(receivedPacket.getLength());
		
							byte[] ackIntegrityCheck ;
		
							byte[] ackNumberByte = new byte[4];
	//STEP 9: Storing the ACK bytes	
							int n =1;
		
							for(int i=0;i<4;i++)
								{
									ackNumberByte[i] = recieveData[n];
									n++;
								}
		
							ByteBuffer wrappedAck = ByteBuffer.wrap(ackNumberByte);
		
							byte pt = (byte) 0xFF; //ideal packet type of the acknowledgement packet
	
        
							int numAck = wrappedAck.getInt();
        
							byte ptAck = recieveData[0]; //packet type of the received packet
        
							byte[] integrityCheckAck = new byte[4];
        
							int s = 5;
        //STEP 10: Storing Integrity Check bytes
							for(int i =0;i<4;i++)
								{
									integrityCheckAck[i] = recieveData[s];
									s++;
								}
        
							for(int i=0;i<5;i++)
								{
        	  
									recieverkPacketAcknum[i] = recieveData[i];

								}
       
							ByteBuffer temp = ByteBuffer.allocate(5);
        
							temp.put(recieverkPacketAcknum);
          
							byte[] localIntCheck2 = new byte[5];
          
							localIntCheck2 = rc4Udp.rc4_call(key,temp);
        
							System.out.println("Checking for  ***acknowldegement number*** ***intergrity check*** ");
    //STEP 11: Check for ACK no & Integrity Check
							
							if((ptAck == pt ) && (localAckNum==numAck))
								{
									for(int i = 0; i <4;i ++)
										if((localIntCheck2[i] ==  integrityCheckAck[i]))
											{
												ackRecievedCorrect = true;
											}
										System.out.println("THE RECIEVED ACKNOWLEDGEMENT FOR THE"+packetCounter+ "PACKET IS CORRECT");
										
								}
        
							else 
								{ 
	//STEP 12: If ack no & Integrity check not matched, Resend packet
        		
									System.out.println("THE RECIEVED ACKNOWLEDGEMENT FOR THE"+packetCounter+ "PACKET IS INCORRECT");
									System.out.println("RESENDING THE "+packetCounter + "PACKET");
									clientSocket.send(sentPacket);

									// Increment retransmission counter
									retransmissionCounter = retransmissionCounter + 1;
								}
						}
					
					catch(InterruptedIOException e) 
						{

							System.out.print("\nClient socket timeout! Exception object e : " + e); 
							
							System.exit(0);
						}
	
						//else discard the packet
    		
			}

		clientSocket.close(); //Close the socket
		packetCounter++;
	
	//STEP 12: Check if the ACK is received properly or not
		if(ackRecievedCorrect = true)
	
			{		
				return true;
			}
		else 
		
			{
				System.out.println("Communication failure"); //if not, Exit out of the System
				return false;
			

			}
	
	
	}




public ByteBuffer createPacket(byte pt, int seq, byte[] fullData, int counter, byte[] key)
	
		{
		
				final int PAYLOAD_SIZE = 500;
	
				final int PACKET_MSG_SIZE = 40;

				final int MPS_SIZE =30;
		
				final int LOOP_SIZE =479;
		
				int loop = PAYLOAD_SIZE/MPS_SIZE;
	
				Rc4UDP rc4Udp1 = new Rc4UDP();

				byte[] payload = new byte[30];
				
				byte[] payloadLast = new byte[20];
	
				byte len = (byte)payload.length;
				byte lenLast =(byte)payloadLast.length;
	
				ByteBuffer messageTemp = ByteBuffer.allocate(40); 
	
	
				if(counter < LOOP_SIZE)
					{
						for(int i=0;i<30;i++)
							{ 
								payload[i]=fullData[counter];
								counter++;
							}
				
						messageTemp = appendData(pt,seq,len,payload);

					}
				else 
					{
						for(int i=0;i<20;i++)
							{  
							payloadLast[i]=fullData[counter];
							counter++;
							}
						messageTemp = appendData(pt ,seq,lenLast,payloadLast);

					}
	
				byte[] rc4_result = new byte[30];

				//method call of rc4 algorithm with packet argument
	
				rc4_result = rc4Udp1.rc4_call(key, messageTemp);	

				messageTemp.put(rc4_result);
	
				return messageTemp;

		}

static ByteBuffer appendData(byte pt, int seq,byte len,byte[] payload)
		{
				ByteBuffer messageTemp = ByteBuffer.allocate(40); 

				messageTemp.put(pt);
	
				messageTemp.putInt(seq);
	
				messageTemp.put(len);
	
				messageTemp.put(payload);
	
				return messageTemp;
		}

}