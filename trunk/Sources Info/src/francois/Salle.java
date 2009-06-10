package francois;

import java.util.HashSet;

public class Salle {
	
	String nom;
	Multiprise multiprise;
	int temperature;
	int eclairage;
	
//	public Salle(String n, Multiprise mp, int t, int e){
//		nom = n;
//		multiprise = mp;
//		temperature = t;
//		eclairage = e;
//	}
	
	public Salle(String n) {
		nom = n;
		multiprise = null;
		temperature = 0;
		eclairage = 0;
	}
	
	//n'ajoute pas une mp mais init celle de la salle (une seule mp pour l'instant)
	public void addMultiprise(Multiprise mp){
		multiprise = mp;
		System.out.println("ajout de la mp: "+mp.nom+ " dans la salle: "+this.nom);
		for(int a=0; a<5; a++){
			System.out.println("prise"+a+": "+multiprise.getPrises()[a].type);
		}
	}
}
