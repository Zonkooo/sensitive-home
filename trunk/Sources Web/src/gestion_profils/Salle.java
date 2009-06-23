package gestion_profils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import web.Interface;

import Jama.Matrix;

public class Salle {
	private String nom;

	private HashMap<String, Multiprise> multiprises;
	private HashMap<String, ModuleCapteurs> modules;

	private ArrayList<SousProfil> availablesProfils;
	private AbstractProfil currentProfil;
	private ProfilGlobal defaultProfil;

	public Salle(String nom) {
		this.nom = nom;
		this.multiprises = new HashMap<String, Multiprise>();
		this.modules = new HashMap<String, ModuleCapteurs>();
		this.availablesProfils = new ArrayList<SousProfil>();

	}
	
	public Salle(String nom, ProfilGlobal defaut) {
		this.nom = nom;
		this.multiprises = new HashMap<String, Multiprise>();
		this.modules = new HashMap<String, ModuleCapteurs>();
		this.availablesProfils = new ArrayList<SousProfil>();
		
		this.defaultProfil = defaut;
		this.currentProfil = defaut;
	}

	public int temperature_actuelle() {
		int temp = 0;
		int nb = 0;
		ModuleCapteurs mc;
		Iterator it = modules.values().iterator();
		while (it.hasNext()) {
			mc = (ModuleCapteurs) it.next();
			if( mc.getTempMoy() != -1){
				temp += mc.getTempMoy();
				nb++;
			}
		}
		if(nb == 0){
			return -1;
		}
		temp /= nb;
		return nb;
	}

	public int luminosite_actuelle() {
		int temp = 0;
		int nb = 0;
		ModuleCapteurs mc;
		Iterator it = modules.values().iterator();
		while (it.hasNext()) {
			mc = (ModuleCapteurs) it.next();
			if( mc.getLumMoy() != -1){
				temp += mc.getLumMoy();
				nb++;
			}
		}
		if(nb == 0){
			return -1;
		}
		temp /= nb;
		return nb;
	}

	@Override
	public String toString() {
		return nom;
	}

	@Override
	public boolean equals(Object o) {
		return this.toString().equals(o.toString());
	}

	public HashMap<String, ModuleCapteurs> getModules() {
		return modules;
	}

	public HashMap<String, Multiprise> getMultiprises() {
		return multiprises;
	}

	public void addMultiprise(Multiprise mp) {
		// System.out.println("ajout de la MP " + mp + " à la salle " + this);
		multiprises.put(mp.getID(), mp);
	}

	public void addModule(ModuleCapteurs mc) {
		// System.out.println("ajout du module " + mc + " à la salle " + this);
		modules.put(mc.getID(), mc);
		//on envoie un message à la multiprise pour que le module commence à envoyer des données
		//TODO: pour l'instant on envoie à la première multiprise (vu qu'il n'y en a qu'une!!!:-))
		Interface.getHashSalle().get("salon").getMultiprises().get("1").getCommunication().addMessageToQueue("/"+mc.getID()+"\\");
	}

	public void removeMultiprise(Multiprise m) {
		multiprises.remove(m);
	}

	public void removeModule(ModuleCapteurs m) {
		modules.remove(m);
	}

	public void addAppareil(Multiprise mp, int i, TypeMorceau a) {
		Prise p = new Prise(a, mp, i);
		mp.setPrise(p);
	}

	/****************************
	 *** gestion profils ***
	 ***************************/

	public ArrayList<SousProfil> getAvailablesProfils() {
		return availablesProfils;
	}

	public AbstractProfil getCurrentProfil() {
		return currentProfil;
	}

	/**
	 * remplace le profil courant par le profil passé en param
	 * 
	 * @param newProfil
	 */
	public void switchProfil(SousProfil newProfil) {
		if (!(availablesProfils.contains(newProfil))) {
			System.err.println("Impossible d'appliquer le profil " + newProfil
					+ " à la salle " + this);
			return;
		}

		//on set les prises du profil suivant ce qui a été défini
		SousProfil sp = (SousProfil)newProfil;
		for (Prise p : sp.getPrises())
			p.getOwner().setEtatPrise(sp.getEtat(p), p.getPosition());

		//puis on parcours le reste pour tout mettre à auto
		//et ce n'est absolument pas optimisé mais who cares ?
		for (Multiprise multiprise : multiprises.values())
		{
			for (int i = 0; i < multiprise.getCapacity(); i++)
			{
				if(!sp.getPrises().contains(multiprise.getPrise(i)))
					multiprise.setEtatPrise(Etat.AUTO, i);
			}
		}

		this.currentProfil = newProfil;
	}
	
	public void setProfilGlobal(ProfilGlobal profil)
	{
		//si on est en global, on se met à jour
		if(currentProfil.equals(defaultProfil))
			this.currentProfil = profil;
		
		this.defaultProfil = profil;
	}
	
	public void switchToGlobal()
	{		
		for (Multiprise multiprise : multiprises.values())
		{
			for (int i = 0; i < multiprise.getCapacity(); i++)
			{
				multiprise.setEtatPrise(Etat.AUTO, i);
			}
		}
		
		this.currentProfil = defaultProfil;
	}
	
	public void addProfil(SousProfil sp) {
		if (!(sp.getSalle().equals(this))) {
			System.err.println("Impossible d'ajouter le profil " + sp
					+ " à la salle " + this);
			return;
		}

		this.availablesProfils.add(sp);
	}
	
	public void removeProfil(SousProfil sp)
	{
		if(!(sp.equals(currentProfil)))
			availablesProfils.remove(sp);
		else
			System.err.println("le profil " + sp + "est utilisé actuellement");
	}

	/****************************
	 *** Calibrage lampes ***
	 ****************************/

	private Matrix A;
	private ArrayList<Prise> lampes;
	private ArrayList<Capteur> photocapteurs;

	/**
	 * calibre les lampes en les allumant à 100% une à une et en observant
	 * leur effet sur les capteurs.
	 * /!\ cette methode pete tout sur la configuration des prises
	 * 
	 * @return
	 */
	public Matrix calibrationLampes()
	{
		lampes = new ArrayList<Prise>();
		photocapteurs = new ArrayList<Capteur>();

		for (Multiprise multiprise : multiprises.values())
		{
			for (int i = 0; i < multiprise.getCapacity(); i++)
			{
				Prise p = multiprise.getPrise(i);
				if (p != null && p.getType() == TypeMorceau.LUMINOSITE)
				{
					p.setEtat(Etat.OFF);
					if(p.getEtat() == Etat.AUTO)
						lampes.add(p);
				}
			}
		}

		for (ModuleCapteurs moduleCapteurs : modules.values())
		{
			for (int i = 0; i < moduleCapteurs.getCapacity(); i++)
			{
				Capteur c = moduleCapteurs.getCapteur(i);
				if (c != null && c.getType() == TypeMorceau.LUMINOSITE)
				{
					photocapteurs.add(c);
				}
			}
		}

		A = new Matrix(lampes.size(), photocapteurs.size());

		for (int i = 0; i < lampes.size(); i++)
		{
			lampes.get(i).setEtat(Etat.ON);

			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException ie)
			{
				System.out.println(ie);
			}

			for (int j = 0; j < photocapteurs.size(); j++)
			{
				A.set(i, j, photocapteurs.get(j).getLastValeur());
			}
			lampes.get(i).setEtat(Etat.OFF);
		}

		return A;
	}

	public HashMap<Prise, Integer> getCommandesLampes() {
		double[] vals = new double[A.getColumnDimension()];
		for (int i = 0; i < vals.length; i++) {
			vals[i] = currentProfil.getLuminosite()
					- photocapteurs.get(i).getLastValeur();
		}

		Matrix b = new Matrix(vals, 1);

		Matrix x = A.solve(b);

		HashMap<Prise, Integer> ret = new HashMap<Prise, Integer>();

		for (int i = 0; i < lampes.size(); i++) {
			ret.put(lampes.get(i), (int) Math.round(x.get(i, 1)));
		}

		return ret;
	}

	/*
	 * Analyse la valeur des capteurs et envoie la commande en conséquence à
	 * la multiprise
	 */
	public void analyse() {
		int tempMoyCapt = 0;
		int nb_capteurs_de_temperature = 0;
		// on parcourt l'ensemble des modules de capteurs présents dans la
		// pièce
		for (ModuleCapteurs mc : modules.values()) {
			// on fait la moyenne des valeurs des capteurs de meme type
			for (int i = 0; i < mc.getCapacity(); i++) { // on parcourt les
															// capteurs de
															// chaque module
				Capteur capteurCourant = mc.getCapteur(i);

				if (capteurCourant != null) {
					if (capteurCourant.getType() == TypeMorceau.TEMPERATURE) {
						tempMoyCapt += capteurCourant
								.getLastValeur();
						nb_capteurs_de_temperature++;
					} else if (capteurCourant.getType() == TypeMorceau.LUMINOSITE) {
						System.out.println("Raphael s'occupe de toi");
					} else {
						System.out.println("type de capteur non géré");
					}
				}
			}
		}

		if (nb_capteurs_de_temperature != 0)
			tempMoyCapt /= nb_capteurs_de_temperature;

		// tous les capteurs ont été relevés, on s'occupe d'analyser et
		// d'envoyer les commandes
		int commande = (tempMoyCapt < currentProfil.getTemperature()) ? 1 : 0;

		for (Multiprise mp : getMultiprises().values()) {
			for (int i = 0; i < 5; i++) {
				if (mp.getPrise(i).getType() == TypeMorceau.TEMPERATURE) {
					mp.sendMessage(i,commande);
				}
			}
		}
	}
}
