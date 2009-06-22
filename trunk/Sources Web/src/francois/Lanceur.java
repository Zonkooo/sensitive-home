package francois;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import gestion_profils.ProfilGlobal;

public class Lanceur {

	static Interfacage interfacage;
	
	
	// n'est plus utilisée
	public static void main(String[] args) {
			//on redéfinit la sortie sur un fichier pour logguer ce qui se passe
	try {
		System.setOut(new PrintStream(new FileOutputStream("/home/cnous3/Desktop/out.log")));
		System.setErr(new PrintStream(new FileOutputStream("/home/cnous3/Desktop/err.log")));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	}
	System.out.println("eric");
	}

	
	
	
//	
//	public static void main(String[] args){
////		Xml_manipulation.etat_actuel(Xml_manipulation.creation_Hashmap("src/francois/config.xml"),"src/francois/etat.xml");
//	//	Dhcp.rechercheAdresseXport();
//		
////		Interface a;
////		new Interface();  //TODO: créer Interface pour lancer tout le programme
//		
//		Communication communication = new Communication("192.168.0.11");
//		interfacage = new Interfacage();
////		//		
//		communication.addMessageToQueue("/REQ:0:001\\");
//		communication.addMessageToQueue("/REQ:2:255\\");
//		communication.addMessageToQueue("/001144444\\");
////		interfacage.addBoutonToProfil((new ProfilGlobal("salon",20,20)), true);
////		
//		communication.start();
//		interfacage.start();
//	}
//
//
//	public static Interfacage getInterfacage() {
//		return interfacage;
//	}
//	
//	
}