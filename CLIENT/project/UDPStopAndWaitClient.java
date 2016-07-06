/**
 * 
 */
package project;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.Random;
import java.io.*;
import java.net.*;

/**
 * @author ranjitha,purva
 * version 1.8
 *
 */
public class UDPStopAndWaitClient extends UDPSender  
{


	public static void main(String[] args)  {
		
		//STEP 1: Generate Random Secret key and sequence number
		
		byte[] key ={4,5,97,-103,-89,-1,61,60,-32,47,-7,-109,-12,126,-102,19};
		
		final int PAYLOAD_SIZE = 500;
		
		final int PACKET_MSG_SIZE = 40;

		
		final int MPS_SIZE =30;
		
		int loop = PAYLOAD_SIZE/MPS_SIZE;
		
		int sequenceNumber = -1438047064;
		
		int index = 1;
		
		//STEP 2: creating bytebuffer to append header and payload 
        
        boolean packetSent = false;

        byte pt = (byte) 55;
        
        ByteBuffer inputByteBuffer = ByteBuffer.allocate(PACKET_MSG_SIZE) ;
        
        ByteBuffer inputLastByteBuffer = ByteBuffer.allocate(PACKET_MSG_SIZE) ;
        
        //STEP 3: generate data 
        
        byte[] fullData = new byte[PAYLOAD_SIZE];
        
        new Random().nextBytes(fullData); //500 bytes of random data

        
        UDPSender udpSender = new UDPSender();   //Calling the UDPsender to send 30 bytes of data
        
        System.out.println("DISTRIBUTED NETWORKING APPLICATION");
        System.out.println("");
              
        //STEP 4: Creating acknowledgement packet
        
        //counter is set to segregate 30 bytes of data from the full data
        int dataCounter=0;
				
					while(index <= loop) //Creating packets for the 1st 16 payloads
					{
                        //Calling the create packet class
						inputByteBuffer = udpSender.createPacket(pt,sequenceNumber,fullData,dataCounter,key);
						try{
							
						
						packetSent = udpSender.sender(inputByteBuffer,index);
						
						if(!packetSent)  //Exit the program after the 4th retransmission
							{
							System.out.println("Retransmission failed 4 times !!");
							System.exit(0);
							}
						}
						
						catch(Exception e)
						{
							System.out.println("Exception caught");
						}

						dataCounter = dataCounter + 30;

						sequenceNumber = sequenceNumber+ 30;

						index++;
					
					}
					
			
					if(index == loop+1)  //Creating Packet for the last 17th payload
						{
				
							dataCounter = 480; 
				
							pt = (byte) 0xAA;
				
							inputLastByteBuffer = udpSender.createPacket(pt,sequenceNumber,fullData,dataCounter,key);
				
							try
								{
					        //calling the create packet class
									packetSent = udpSender.sender(inputLastByteBuffer,index);
					
								}
							catch(Exception e) 
								{
									System.out.print("\nException object e : " + e); System.exit(0);
								}
						}	

		}
	
	
}
	 
			
			
	    
	 
	 
