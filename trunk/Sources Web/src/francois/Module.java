package francois;

public class Module {
	
	String nom;
	Capteur[] capteurs;
	
	public Module(String n, Capteur[] c) {
		nom =n;
		capteurs = c;
	}
	
	public String getNom() {
		return nom;
	}

	public void setPrise(Capteur[] c)
	{
		capteurs = c;
	}
	
	public Capteur[] getCapteurs(){
		return capteurs;
	}
}
