package francois;

public class Capteur {
	String type;
	int valeur;
	int id;
	
	public Capteur(String i,String t){
		type = t;
		id =Integer.parseInt(i);
	}
}
