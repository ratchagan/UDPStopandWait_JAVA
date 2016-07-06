/**
 * 
 */
package project;

import java.nio.ByteBuffer;

/**
  * @author purva,ranjitha
 *
 */
public class Rc4UDPStopandWaitServer {
	
	public byte[]  rc4_call(byte[] key, byte[] data)    //RC4 Algorithm
    {   //Initialization
    	int[] S = new int[256];  
        int[] T = new int[256];
        int keylen;
        
        
                if (key.length < 1 || key.length > 256) {
                        System.out.println("key must be between 1 and 128 bytes");
                }
                else {
                        keylen = key.length;
                        for 	(int i = 0; i < 256; i++) {
                                S[i] = i;
                                T[i] = key[i % keylen];
                        }
                        int j = 0;
                        for (int i = 0; i < 256; i++) {  //Initial Permutation of S
                                j = (j + S[i] + T[i]) & 0xFF;
                                S[i] = S[j];
                                S[j] = S[i];
                                S[i] = S[j];
                        }
                }
                
                byte[] ciphertext = new byte[data.length];
                byte[] decipher = new byte[data.length];
                int i = 0, j = 0, t;
                byte k;
                
                //Stream Generation
                for (int counter = 0; counter < data.length; counter++) {
                        i = (i + 1) & 0xFF;
                        j = (j + S[i]) & 0xFF;
                        S[i] = S[j];
                        S[j] = S[i];
                        S[i] = S[j];
                        t = (S[i] + S[j]) & 0xFF;
                        k = (byte) S[t];
                        ciphertext[counter] = (byte)( data[counter] ^ k );  //Storing the values in the ciphertext variable
                        
                        decipher[counter] =  (byte)(ciphertext[counter] ^ k);
                }
                

                return ciphertext;
	     }
   

}

