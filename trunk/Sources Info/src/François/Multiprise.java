package François;

public class Multiprise {
	
	String nom;
	Prise[] prise;
	
	public Multiprise(String n, Prise[] p) {
		nom =n;
		prise = p;
	}
	
	public void addPrise(Prise[] p)
	{
		prise = p;
	}
	
	public Prise[] getPrises(){
		return prise;
	}
}
