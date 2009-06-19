package francois;






public class Lanceur {

	
	public static void main(String[] args){
//		Xml_manipulation.etat_actuel(Xml_manipulation.creation_Hashmap("src/francois/config.xml"),"src/francois/etat.xml");
	//	Dhcp.rechercheAdresseXport();
		
//		Interface a;
//		new Interface();  //TODO: créer Interface pour lancer tout le programme
		
		Communication communication = new Communication("192.168.0.11");
		Interfacage interfacage = new Interfacage();
//		//		
//		communication.addMessageToQueue("/REQ:0:001\\");
//		communication.addMessageToQueue("/REQ:2:255\\");
//		communication.addMessageToQueue("/001144444\\");
//		interfacage.addBoutonToProfil((new ProfilGlobal("salon",20,20)), true);
//		
		communication.start();
		interfacage.start();
	}
	
	
	
}
