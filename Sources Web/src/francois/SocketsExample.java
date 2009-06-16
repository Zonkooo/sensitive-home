package francois;
	// rob@faludi.com
	// A full example for using Sockets in Processing to communicate with a Lantronix XPort.
	// This program could also be used to communicate primitive variables (bytes) with any other TCP/IP enabled device.
	//
	// We open a Socket, check to see if data is available, get a byte of data
	// from the remote device, and then send a byte of data to the remote device.
	// At the end of the program, we close the streams of data and close the Socket.
	// (Once you are done debugging your code, you should remove all the println statements for better speed.)

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
	
public class SocketsExample {


	String host; 
	int port;
	Socket mySocket;                    // declare Socket
	DataInputStream myInputStream;      // declare data input stream. This will run within a socket, bringing data into Java
	DataOutputStream myOutputStream;    // declare data output stream. This will run within a socket, sending data out from Java
	byte myDataIn, myDataOut;           // declare some variables to store the data we're sending and receiving

	public static void main(String[] args){
		SocketsExample socketsExample = new SocketsExample();
		socketsExample.setup();
		socketsExample.sendMessage("Pierre y dit de la merde");
		while(true){
			socketsExample.listen();
		}
		
		
	}
	
	
	void setup(){
//	  size(400,255);
//	  background(0);
//	  framerate(30);
	  host = "192.168.0.11";  // define a host to communicate with. This can be a name or IP address
	  port = 31337;              // define a port to contact on that host. Must be a number, typically 10001 for an XPort
	  checkConnection(host, port);        // subroutine to create a connection, via a socket, to the XPort
	}


	//envoie un message
	public void sendMessage(String message){
		for(int i=0;i<message.length();i++){
			sendSomeData((byte)message.charAt(i));
		}
	}
	
	//écoute sur le port série
	void listen(){
	  if (dataIsWaiting() == true) {      //  check to see if there's new data waiting to come in
	    myDataIn = getSomeData();         //  ... and if there's new data, get it
	    if(myDataIn=='/'){ //signal de début d'un message de type /MP-Capteur-valeur; en 3 fois 2octets
	    	System.out.println("début d'un message");
	    	String message = "";
	    	while(myDataIn != ';'){ //on attend de recevoir le signal de fin d'un message
	    		if (dataIsWaiting() == true && (myDataIn = getSomeData()) !=';') {
	    			message = message + (char)myDataIn;
	    		}
	    	}
	    	System.out.println("message reçu: "+message);
	    }
	  }
	}

	////////CHECK CONNECTION\\\\\\\\
	void checkConnection(String host, int port) {
	  if(mySocket == null || mySocket.isConnected() == false) {                 
	    System.out.println("trying to connect to: " + host + " at port: " + port);
	    try{                                      // make an attempt to run the following code
	      mySocket = new Socket(host,port);       // initialize socket, connecting it to a host computer's port
	      System.out.println("connected!");
	    }
	    catch(Exception e) {                         // if the "try" attempt gave an error, run the following code
	      e.printStackTrace();                       // print the error to the log
	      System.out.println("unable to connect to: " + host + " at port: " + port);
	    }
	  }
	}


	////////CHECK TO SEE IF DATA IS WAITING TO COME IN\\\\\\\\\\\
	boolean dataIsWaiting() {
	  boolean bytesAvailable = false;
	  if ( myInputStream == null) {  // if there's no active input stream
	    try {
	      myInputStream = new DataInputStream(mySocket.getInputStream()); // create an new input stream from a particular socket
	      System.out.println("opening input stream");
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	      System.out.println("error while opening input stream");
	    }
	  }
	  try {
	    if (myInputStream.available()>0) {      // check to see if any bytes are available
	      bytesAvailable = true;                // ...and if they are set the variable to true
	      System.out.println(myInputStream.available() + " bytes available...");
	    }
	  }
	  catch(Exception e) {
	    e.printStackTrace();
	    System.out.println("error while checking for bytes available");
	  }
	  return bytesAvailable;
	}


	////////GET SOME DATA \\\\\\\\\\
	byte getSomeData() {
	  byte inData = 0;               // declare and initialize the data variable
	  try {
	    if (myInputStream.available()>0) {  //   only read the byte if there's a byte to read [this is a redundant check]
	      inData = myInputStream.readByte();  // read a byte from the input stream
	      System.out.println("data received: " + inData);
	    }
	  }
	  catch(Exception e){
	    e.printStackTrace();
	    System.out.println("no data");
	  }
	  return inData;
	}


	////////SEND SOME DATA\\\\\\\\\\
	void sendSomeData(byte outData){
	  if (myOutputStream == null) { // if there's no active output stream
	    try {
	      myOutputStream = new DataOutputStream(mySocket.getOutputStream()); // create an new output stream from a particular socket
	        }
	    catch (Exception e) {
	      e.printStackTrace();
	      System.out.println("no output stream");
	    }
	  }
	  try{
	    myOutputStream.writeByte(outData); // write a byte to the output stream
	    System.out.println("data sent: " + outData);
	  }
	  catch(Exception e){
	    e.printStackTrace();
	    System.out.println("event send failed");
	  } 
	}


	public void stop() { // when the program quits
	  try {
	    myInputStream.close();  // close the input stream
	    myOutputStream.close(); // close the output stream
	    mySocket.close(); // close the socket
	  }
	  catch (Exception e) {
	    e.printStackTrace();
	    System.out.println("couldn't close connection");
	  }
	}
}
