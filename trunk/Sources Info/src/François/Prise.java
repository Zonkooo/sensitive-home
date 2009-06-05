package François;

public class Prise {
	
	static final boolean ON = true;
	static final boolean OFF = false;
	
	String nom;
	int valeur;
	boolean etat;
	
	public Prise(String n, int val, boolean e){
		nom = n;
		valeur = val;
		etat = e;
	}
	
}
