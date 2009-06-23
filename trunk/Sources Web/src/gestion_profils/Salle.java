package gestion_profils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import Jama.Matrix;

public class Salle {
	private static int TOLERANCE_LUMINOSITE = 25;
	
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

		this.defaultProfil = Maison.getMaison().getCurrentProfil();
		this.currentProfil = defaultProfil;
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
		return temp;
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
		return temp;
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
	 * 
	 * @return
	 */
	private Matrix calibrationLampes()
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

	int pas = 32;
	int commande = 0;
	boolean tropBas = true;
	
	public HashMap<Prise, Integer> getCommandesLampes()
	{
		//v2 : sans calibration
		
		lampes = new ArrayList<Prise>();
		photocapteurs = new ArrayList<Capteur>();

		//extraction des prises correspondant à des lampes
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

		//extraction des capteurs de luminosité
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
		
		int luminositeMoyenne = 0;
		for (Capteur capteur : photocapteurs)
		{
			luminositeMoyenne += capteur.getLastValeur();
		}
		luminositeMoyenne /= photocapteurs.size();

		if(tropBas != luminositeMoyenne < currentProfil.getLuminosite())
		{
			pas /= 2; //si on est passé au dessus, on réduit le pas.
			tropBas = !tropBas;
		}
		
		if(Math.abs(luminositeMoyenne - currentProfil.getLuminosite()) > TOLERANCE_LUMINOSITE)
		{
			if(luminositeMoyenne < currentProfil.getLuminosite())
				commande += pas;
			else
				commande -= pas;
		}
		
		//ajustement des bornes
		if (commande > 255)
		{
			commande = 255;
		}
		else if (commande < 0)
		{
			commande = 0;
		}
		
		HashMap<Prise, Integer> ret = new HashMap<Prise, Integer>();
		for (int i = 0; i < lampes.size(); i++)
		{
			ret.put(lampes.get(i), commande);
		}
		
		return ret;
		
//		double[] vals = new double[A.getColumnDimension()];
//		for (int i = 0; i < vals.length; i++)
//		{
//			vals[i] = currentProfil.getLuminosite() - photocapteurs.get(i).getLastValeur();
//		}
//
//		Matrix b = new Matrix(vals, 1);
//
//		Matrix x = A.solve(b);
//
//		HashMap<Prise, Integer> ret = new HashMap<Prise, Integer>();
//
//		int command;
//		for (int i = 0; i < lampes.size(); i++)
//		{
//			command = (int) Math.round(x.get(i, 1));
//			if (command > 255)
//			{
//				command = 255;
//			}
//			else if (command < 0)
//			{
//				command = 0;
//			}
//
//			ret.put(lampes.get(i), command);
//		}
//
//		return ret;
	}

	/*
	 * Analyse la valeur des capteurs et envoie la commande en conséquence à
	 * la multiprise
	 */
	public void analyse() {
		// tous les capteurs ont été relevés, on s'occupe d'analyser et
		// d'envoyer les commandes
		int commande = (temperature_actuelle() < currentProfil.getTemperature()) ? 1 : 0;
		for (Multiprise mp : getMultiprises().values()) {
			for (int i = 0; i < 5; i++) {
				if (mp.getPrise(i).getType() == TypeMorceau.TEMPERATURE) {
					mp.sendMessage(i,commande);
				}
			}
		}
	}
}
