package francois;

import gestion_profils.Capteur;
import gestion_profils.Salle;
import gestion_profils.TypeMorceau;
import gestion_profils.Xml_manipulation;

import java.io.IOException;
import java.util.HashMap;





public class Lanceur {

	
	public static void main(String[] args){
		Xml_manipulation.serialize(Xml_manipulation.creation_Hashmap("src/francois/config.xml"),"src/francois/config.xml");
	//	Dhcp.rechercheAdresseXport();
		
	}
}
