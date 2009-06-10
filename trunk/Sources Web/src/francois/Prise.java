package francois;

public class Prise {
	
	static final boolean ON = true;
	static final boolean OFF = false;
	
	String type;
	int valeur;
	boolean etat;
	
	public Prise(String t, int val, boolean e){
		type = t;
		valeur = val;
		etat = e;
	}
	
}
