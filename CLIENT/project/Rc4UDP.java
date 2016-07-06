/**
 * 
 */
package project;

import java.nio.ByteBuffer;

/**
 * @author ranjitha,purva
 *
 */
public class Rc4UDP 

{

	public byte[]  rc4_call(byte[] key, ByteBuffer data) //RC4 Algorithm
		
		{   //Initialization
		
				int[] S = new int[256];
				int[] T = new int[256];
				int keylen;
  
            if (key.length < 1 || key.length > 256)
            	{
                    System.out.println("key must be between 1 and 128 bytes");
            	}
            else 
            	{
                    keylen = key.length;
                    for(int i = 0; i < 256; i++) 
                    	{
                            S[i] = i;
                            T[i] = key[i % keylen];
                    	}
                    int j = 0;
                    for (int i = 0; i < 256; i++)  //Initial Permutation of S

                    	{
                            j = (j + S[i] + T[i]) & 0xFF;
                            S[i] = S[j];
                            S[j] = S[i];
                            S[i] = S[j];
                    	}
            	}
    
            	byte[] c = new byte[4];
            	byte[] ciphertext = new byte[data.position()];
            	byte[] decipher = new byte[data.position()];
            	int i = 0, j = 0, t;
            	byte k;
            	for (int counter = 0; counter < data.position(); counter++) 
            	{   //Stream Generation
                    i = (i + 1) & 0xFF;
                    j = (j + S[i]) & 0xFF;
                    S[i] = S[j];
                    S[j] = S[i];
                    S[i] = S[j];
                    t = (S[i] + S[j]) & 0xFF;
                    k = (byte) S[t];
                    ciphertext[counter] = (byte)( data.get(counter) ^ k );
                    
                    decipher[counter] =  (byte)(ciphertext[counter] ^ k);
            	}
            
            //Compression Algorithm
            
            	for(int r=0 ; r<ciphertext.length; r++)  
    			{
    			 //Xoring the output of the RC4 algo bytes
    				if(r%4==0)      
    						c[0] = (byte)(c[0] ^ ciphertext[r]);
    				else if(r%4==1)
    						c[1] = (byte)(c[1] ^ ciphertext[r]);
    				else if(r%4==2)
    					c[2] = (byte)(c[2] ^ ciphertext[r]);
    				else if(r%4==3)
    					c[3] = (byte)(c[3] ^ ciphertext[r]);
    			
    			}
            
            return c;    
		}

	}