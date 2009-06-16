package GestionProfils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Salle
{
	private String nom;
	private int currentTemperature;
	private int currentLuminosite;

	private ArrayList<Multiprise> multiprises;
	private ArrayList<ModuleCapteurs> modules;
	
	private ArrayList<SousProfil> availablesProfils;
	private SousProfil currentProfil;

	public Salle(String nom)
	{
		this.nom = nom;
		this.currentTemperature = Capteur.VALEUR_INCONNUE;
		this.currentLuminosite = Capteur.VALEUR_INCONNUE;
		this.multiprises = new ArrayList<Multiprise>();
		this.modules = new ArrayList<ModuleCapteurs>();
	}

	@Override public String toString()
	{
		return nom;
	}
	
	@Override public boolean equals(Object o)
	{
		return this.toString().equals(o.toString());
	}

	public ArrayList<ModuleCapteurs> getModules()
	{
		return modules;
	}

	public ArrayList<Multiprise> getMultiprises()
	{
		return multiprises;
	}
	
	public void addMultiprise(Multiprise mp)
	{
		//System.out.println("ajout de la MP " + mp + " à la salle " + this);
		multiprises.add(mp);
	}

	public void addModule(ModuleCapteurs mc)
	{
		//System.out.println("ajout du module " + mc + " à la salle " + this);
		modules.add(mc);
	}

	public void removeMultiprise(Multiprise m)
	{
		multiprises.remove(m);
	}

	public void removeModule(ModuleCapteurs m)
	{
		modules.remove(m);
	}
	
	public void addAppareil(Multiprise mp, int i, TypeMorceau a)
	{
		Prise p = new Prise(a, mp, i);
		mp.setPrise(p);
	}

	/****************************
	 ***   gestion profils   ***
	 ***************************/
	
	public ArrayList<SousProfil> getAvailablesProfils()
	{
		return availablesProfils;
	}

	public SousProfil getCurrentProfil()
	{
		return currentProfil;
	}
	
	public void switchProfil(SousProfil newProfil)
	{
		if(!(availablesProfils.contains(newProfil)))
		{
			System.err.println("Impossible d'appliquer le profil " + newProfil + " à la salle " + this);
			return;
		}
			
		for(Prise p : newProfil.getPrises())
			p.getOwner().setEtatPrise(newProfil.getEtat(p), p.getPosition());

		this.currentProfil = newProfil;
	}
	
	public void addProfil(SousProfil sp)
	{
		if(!(sp.getSalle().equals(this)))
		{
			System.err.println("Impossible d'ajouter le profil " + sp + " à la salle " + this);
			return;
		}
		
		this.availablesProfils.add(sp);
	}
	
	/*
	 * Analyse la valeur des capteurs et envoie la commande en conséquence à la multiprise
	 */
	public void analyse(){
		int temp_moyenne_courante = 0;
		Collection<Integer> temp_courante_capteur = null;
		//on parcourt l'ensemble des modules de capteurs présents dans la pièce
		Iterator<ModuleCapteurs> itMC = getModules().iterator();
		while(itMC.hasNext()){
			//on fait la moyenne des valeurs des capteurs de meme type
			for(int i=0;i<4;i++){ //on parcourt les 4 capteurs de chaque module
				Capteur capteurCourant = itMC.next().getCapteurs(i);
				if(capteurCourant.getType()==TypeMorceau.TEMPERATURE){
					temp_courante_capteur.add(capteurCourant.getLastValeur());
				} else if(capteurCourant.getType()==TypeMorceau.LUMINOSITE) {
					System.out.println("Raphael s'occupe de toi");
				} else {
					System.out.println("type de capteur non géré");
				}
			}
		}
		//tous les capteurs ont été relevés, on s'occupe d'analyser et d'envoyer les commandes
		
		//température:
		for (Integer temp : temp_courante_capteur) {
			temp_moyenne_courante += temp;
		}
		temp_moyenne_courante = temp_moyenne_courante / temp_courante_capteur.size();
		int commande = 0;
		if(temp_moyenne_courante < currentProfil.getTemperature()){
			commande = 1;	//on veut allumer le chauffage
		} else {
			commande = 0;	//on veut couper le chauffage
		}
		for (Multiprise mp : getMultiprises()) {
			for(int i=0;i<5;i++){
				if(mp.getPrise(i).getType()==TypeMorceau.LUMINOSITE){
					mp.communication.sendMessage("/"+i+":"+commande);
				}
			}
		}
	}
}
