package francois;

// rob@faludi.com
// A full example for using Sockets in Processing to communicate with a Lantronix XPort.
// This program could also be used to communicate primitive variables (bytes) with any other TCP/IP enabled device.
//
// We open a Socket, check to see if data is available, get a byte of data
// from the remote device, and then send a byte of data to the remote device.
// At the end of the program, we close the streams of data and close the Socket.
// (Once you are done debugging your code, you should remove all the println statements for better speed.)

import gestion_profils.Maison;
import gestion_profils.ModuleCapteurs;
import gestion_profils.Salle;
import gestion_profils.Xml_manipulation;

import web.Interface;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Communication extends Thread {

	String host;
	int port;
	Socket mySocket; // declare Socket
	DataInputStream myInputStream; // declare data input stream. This will run
									// within a socket, bringing data into Java
	DataOutputStream myOutputStream; // declare data output stream. This will
										// run within a socket, sending data out
										// from Java
	byte myDataIn, myDataOut; // declare some variables to store the data we're
								// sending and receiving
	String[] message_split;
	ConcurrentLinkedQueue<String> messageAenvoyer;
	
	
//	public static void main(String[] args) {
//		Communication communication = new Communication("192.168.0.11");
//
//		//		communication.start();
//		communication.addMessageToQueue("/REQ:0:001\\");
//		communication.addMessageToQueue("/REQ:2:255\\");
////		communication.addMessageToQueue("/REQ:2:000\\");
//				communication.addMessageToQueue("/001144444\\");
////				communication.addMessageToQueue("/001111111\\");
//
////		communication.addMessageToQueue("/REQ:0:000\\");
//		
//		
//		while(true){
//
//			if(communication.messageAenvoyer.size()>0) {
//				System.out.println("\n"+communication.sendQueue());
//			}
//			//on attend de recevoir l'accusé de réception avant de supprimer le message
//			String ecoute;
//			long time = System.currentTimeMillis();
//
//			while((ecoute=communication.listen()).equals("") && System.currentTimeMillis()-time<1000){
//				//on attend de recevoir quelquechose ou que le temps d'accusé soit dépassé
//				if((System.currentTimeMillis()-time)%500==0){
////					System.out.println((System.currentTimeMillis()-time)/1000);
//				}
//			}
//			if(communication.messageAenvoyer.size()>0) {
//			if(!(ecoute.length()<28) && ecoute.substring(19, 28).equals("ACK:" + communication.messageAenvoyer.peek().substring(5, 10))){
////				System.out.println("accusé reconnu");
//				//si c'est l'accusé, l'envoi a réussi donc on peut supprimer le message
//				communication.messageAenvoyer.remove();
//			} else if(System.currentTimeMillis()-time>1000) {
//				//si le temps est dépassé on renvoie le message
//				System.out.println("\n"+communication.sendQueue());
//				time = System.currentTimeMillis();
//			}
//			}
//			System.out.println(ecoute);
//			
//		}
//	}

	public Communication(String ip) {
		host = ip;
		port = 31337;
		messageAenvoyer = new ConcurrentLinkedQueue<String>();
		checkConnection(host, port); 		
	}

	public void run(){
		while(true){
		if(messageAenvoyer.size()>0) {
			System.out.println("\n"+sendQueue());
		}
		//on attend de recevoir l'accusé de réception avant de supprimer le message
		String ecoute;
		long time = System.currentTimeMillis();

		while((ecoute=listen()).equals("") && System.currentTimeMillis()-time<1000){
			//on attend de recevoir quelquechose ou que le temps d'accusé soit dépassé
			if((System.currentTimeMillis()-time)%500==0){
//				System.out.println((System.currentTimeMillis()-time)/1000);
			}
		}
		if(messageAenvoyer.size()>0) {
		if(!(ecoute.length()<28) && ecoute.substring(19, 28).equals("ACK:" + messageAenvoyer.peek().substring(5, 10))){
//			System.out.println("accusé reconnu");
			//si c'est l'accusé, l'envoi a réussi donc on peut supprimer le message
			messageAenvoyer.remove();
		} else if(System.currentTimeMillis()-time>1000) {
			//si le temps est dépassé on renvoie le message
			System.out.println("\n"+sendQueue());
			time = System.currentTimeMillis();
		}
		}
		System.out.println(ecoute);
		}
	}

	// écoute sur le port série
	String  listen() {
		String retour="";
		if (dataIsWaiting() == true) { // check to see if there's new data
										// waiting to come in
			myDataIn = getSomeData(); // ... and if there's new data, get it
			if (myDataIn == '/') { // signal de début d'un message de type
									// /adresse du module de
									// capteur:val_cap1:val_cap2:val_cap3:val_cap4\
//				System.out.println("début d'un message");
				String message = "";
				while (myDataIn != '\\') { // on attend de recevoir le signal de
											// fin d'un message
					if (dataIsWaiting() == true
							&& (myDataIn = getSomeData()) != '\\') {
						message = message + (char) myDataIn;
					}
				}
				retour = analyseData(message);
			}
		}
		return retour;
	}

	String analyseData(String message) {
		String retour="";
		// on sait de quelle multiprise vient les données puisqu'on est conecté
		// à celle-ci
		// on a besoin de savoir de quel capteur viennent les données
		message_split = message.split(":");
		// On regarde si on a affaire à des données ou un accusé de réception
		if (message_split.length == 5) { // données car message de la forme
											// /module:d0:d1:d2:d3\
			retour="Données d'un module de capteur: " + message;
			// On met à jour la valeur des capteurs dont on vient de recevoir
			// l'info
			// On parcourt la liste de salles
			for (Salle itS : Maison.getMaison().getSalles()) {
				Salle salleCourante = itS;
				// on parcourt la liste des modules de capteurs de la salle
				// courante
				for (ModuleCapteurs itMC : salleCourante.getModules().values()) {
					// est-ce que le module dont on vient de recevoir les
					// données est celui que l'on parcourt?
					ModuleCapteurs moduleCourant = itMC;
					if (moduleCourant.getID() == message_split[0]) {
						System.out.println("Le message provient de la salle: "
								+ salleCourante);
						for (int i = 0; i < 4; i++) { // on modifie la valeur du
														// capteur à partir du
														// message reçu
							moduleCourant.getCapteur(i).setLastValeur(
									Integer.parseInt(message_split[i + 1]));
							//on modifie le XML pour que l'interface web soit mise à jour
							Xml_manipulation.etat_actuel(Interface.getHashSalle(),"../webapps/web_interface/etat.xml");
						}
					}
					System.out.println("Le message provient d'une salle non enregistrée");
				}
			}
		} else if (message_split.length == 3) { // accusé de commande car message de la
												// forme /ACK:prise:val\
			retour="Accusé du message: " + message;
		} else if (message_split.length == 2) { // accusé d'adresse de module car message de la
												// forme /ACK:adresse\
			retour="Accusé du message: " + message;
		}  else if (message_split.length == 6) { // message provenant d'un module qui vient d'etre branché
												 // forme /0011xxxxx:::::NOUVEAU::::\
			// On ajoute le nouveau module à la salle vide
			//TODO: vérifier que la salle où l'on ajoute le nouveau module s'appelle vide
			for (Salle s : Maison.getMaison().getSalles()) {
				if(s.toString().equals("vide")){
					s.addModule(new ModuleCapteurs((message.substring(0,9)),4));
				}
			}
			retour="Nouveau module détecté: " + message;
		}	else {
			retour="Message de type inconnu: " + message;
		}
		return retour;
	}

	// //////CHECK CONNECTION\\\\\\\\
	void checkConnection(String host, int port) {
		if (mySocket == null || mySocket.isConnected() == false) {
			System.out.println("trying to connect to: " + host + " at port: "
					+ port);
			try { // make an attempt to run the following code
				mySocket = new Socket(host, port); // initialize socket,
													// connecting it to a host
													// computer's port
				System.out.println("connected!");
			} catch (Exception e) { // if the "try" attempt gave an error, run
									// the following code
				e.printStackTrace(); // print the error to the log
				System.out.println("unable to connect to: " + host
						+ " at port: " + port);
			}
		}
	}

	// //////CHECK TO SEE IF DATA IS WAITING TO COME IN\\\\\\\\\\\
	boolean dataIsWaiting() {
		boolean bytesAvailable = false;
		if (myInputStream == null) { // if there's no active input stream
			try {
				myInputStream = new DataInputStream(mySocket.getInputStream()); // create
																				// an
																				// new
																				// input
																				// stream
																				// from
																				// a
																				// particular
																				// socket
				System.out.println("opening input stream");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("error while opening input stream");
			}
		}
		try {
			if (myInputStream.available() > 0) { // check to see if any bytes
													// are available
				bytesAvailable = true; // ...and if they are set the variable to
										// true
				// System.out.println(myInputStream.available() +
				// " bytes available...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error while checking for bytes available");
		}
		return bytesAvailable;
	}

	// //////GET SOME DATA \\\\\\\\\\
	public byte getSomeData() {
		byte inData = 0; // declare and initialize the data variable
		try {
			if (myInputStream.available() > 0) { // only read the byte if
													// there's a byte to read
													// [this is a redundant
													// check]
				inData = myInputStream.readByte(); // read a byte from the input
													// stream
//				System.out.println("data received: " + inData + " = " + (char)inData);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("no data");
		}
		return inData;
	}

	// //////SEND SOME DATA\\\\\\\\\\
	public void sendSomeData(byte outData) {
		if (myOutputStream == null) { // if there's no active output stream
			try {
				myOutputStream = new DataOutputStream(mySocket
						.getOutputStream()); // create an new output stream from
												// a particular socket
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("no output stream");
			}
		}
		try {
			myOutputStream.writeByte(outData); // write a byte to the output
												// stream
//			 System.out.println("data sent: " + outData);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("event send failed");
		}
	}

	public void fin() { // when the program quits
		try {
			myInputStream.close(); // close the input stream
			myOutputStream.close(); // close the output stream
			mySocket.close(); // close the socket
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("couldn't close connection");
		}
	}
	
	/*
	 * ajoute un message dans la liste d'envoi
	 */
	public void addMessageToQueue(String arg0) {
		messageAenvoyer.add(arg0);
	}
	
	/*
	 * envoie le dernier message de la queue
	 */
	public String sendQueue() {
		String retour="Problème durant l'envoi";
			for (int i = 0; i < messageAenvoyer.peek().length(); i++) {
				sendSomeData((byte) messageAenvoyer.peek().charAt(i));
			}
			retour = "Message envoyé: "+messageAenvoyer.peek();
		return retour;
	}
	

}
