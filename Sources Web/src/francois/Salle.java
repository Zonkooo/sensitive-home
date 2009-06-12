package francois;

import java.util.HashMap;
import java.util.HashSet;

public class Salle {
	static final private int MAX = 10;
	private String nom;
	public HashMap<String, Multiprise> getHash_multiprise() {
		return hash_multiprise;
	}

	public HashMap<String, Module> getHash_module() {
		return hash_module;
	}

	public String getNom() {
		return nom;
	}

	private HashMap<String, Multiprise> hash_multiprise;
	private HashMap<String, Module> hash_module;
	private int temperature;
	private int eclairage;
	
	public Salle(String n) {
		hash_multiprise = new HashMap<String, Multiprise>();
		hash_module = new HashMap<String, Module>();
		nom = n;
		temperature = 0;
		eclairage = 0;
	}
	
	public void addMultiprise(Multiprise mp){
		System.out.println(mp.getNom());
		hash_multiprise.put(mp.getNom(), mp);
	}
	
	public void addModule(Module mc){
		hash_module.put(mc.getNom(), mc);
	}
	
	public void getMultiprise(String id){
		hash_multiprise.get(id);
	}
	
	public void getModule(String id){
		hash_module.get(id);
	}
	
	public void remtMultiprise(String id){
		hash_multiprise.remove(id);
	}
	
	public void remModule(String id){
		hash_module.remove(id);
	}
	
}
