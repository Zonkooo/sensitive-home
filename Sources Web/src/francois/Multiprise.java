package francois;

public class Multiprise {
	
	private String nom;
	private Prise[] prise;
	
	public Multiprise(String n, Prise[] p) {
		nom =n;
		prise = p;
	}
	
	public void setPrise(Prise[] p)
	{
		prise = p;
	}
	
	public Prise[] getPrises(){
		return prise;
	}

	public String getNom() {
		return nom;
	}
}
