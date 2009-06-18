package gestion_profils;

import Jama.Matrix;
import java.util.ArrayList;
import java.util.HashMap;

public class Salle
{
	private String nom;

	private HashMap<Integer,Multiprise> multiprises;
	private HashMap<Integer,ModuleCapteurs> modules;
	
	private ArrayList<SousProfil> availablesProfils;
	private SousProfil currentProfil;

	public Salle(String nom)
	{
		this.nom = nom;
		this.multiprises = new HashMap<Integer,Multiprise>();
		this.modules = new  HashMap<Integer,ModuleCapteurs>();
	}

	@Override
	public String toString() {
		return nom;
	}
	
	@Override public boolean equals(Object o)
	{
		return this.toString().equals(o.toString());
	}

	public  HashMap<Integer,ModuleCapteurs> getModules()
	{
		return modules;
	}

	public  HashMap<Integer,Multiprise> getMultiprises()
	{
		return multiprises;
	}
	
	public void addMultiprise(Multiprise mp)
	{
		//System.out.println("ajout de la MP " + mp + " à la salle " + this);
		multiprises.put(mp.getID(),mp);
	}

	public void addModule(ModuleCapteurs mc)
	{
		//System.out.println("ajout du module " + mc + " à la salle " + this);
		modules.put(mc.getID(),mc);
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

	public void switchProfil(SousProfil newProfil) {
		if (!(availablesProfils.contains(newProfil))) {
			System.err.println("Impossible d'appliquer le profil " + newProfil
					+ " à la salle " + this);
			return;
		}
			
		for(Prise p : newProfil.getPrises())
			p.getOwner().setEtatPrise(newProfil.getEtat(p), p.getPosition());

		this.currentProfil = newProfil;
	}

	public void addProfil(SousProfil sp) {
		if (!(sp.getSalle().equals(this))) {
			System.err.println("Impossible d'ajouter le profil " + sp
					+ " à la salle " + this);
			return;
		}
		
		this.availablesProfils.add(sp);
	}
	
	/****************************
	 ***   Calibrage lampes   ***
	 ****************************/
	
	private Matrix A;
	private ArrayList<Prise> lampes;
	private ArrayList<Capteur> photocapteurs;
	
	/**
	 * calibre les lampes en les allumant à 100% une à une
	 * et en observant leur effet sur les capteurs.
	 * 
	 * @return
	 */
	public Matrix calibrationLampes()
	{
		lampes = new ArrayList<Prise>();
		photocapteurs = new ArrayList<Capteur>();
		
		for (Multiprise multiprise : multiprises.values())
		{
			for(int i = 0; i < multiprise.getCapacity(); i++)
			{
				Prise p = multiprise.getPrise(i);
				if(p != null && p.getType() == TypeMorceau.LUMINOSITE)
				{
					p.setEtat(Etat.OFF);
					lampes.add(p);
				}
			}
		}
		
		for (ModuleCapteurs moduleCapteurs :modules.values())
		{
			for(int i = 0; i < moduleCapteurs.getCapacity(); i++)
			{
				Capteur c = moduleCapteurs.getCapteur(i);
				if(c != null && c.getType() == TypeMorceau.LUMINOSITE)
					photocapteurs.add(c);
			}
		}

		A = new Matrix(lampes.size(), photocapteurs.size());

		for (int i = 0; i < lampes.size(); i++)
		{
			lampes.get(i).setEtat(Etat.ON);
			
			try { Thread.sleep(1000); }
			catch(InterruptedException ie)
			{ System.out.println(ie); }
			
			for (int j = 0; j < photocapteurs.size(); j++)
			{
				A.set(i, j, photocapteurs.get(j).getLastValeur());
			}
			lampes.get(i).setEtat(Etat.OFF);
		}
		
		return A;
	}
	
	public HashMap<Prise, Integer> getCommandesLampes()
	{
		double[] vals = new double[A.getColumnDimension()];
		for (int i = 0; i < vals.length; i++)
		{
			vals[i] = currentProfil.getLuminosite() - photocapteurs.get(i).getLastValeur();
		}

		Matrix b = new Matrix(vals, 1);
		
		Matrix x = A.solve(b);
		
		HashMap<Prise, Integer> ret = new HashMap<Prise, Integer>();
		
		for (int i = 0; i < lampes.size(); i++)
		{
			ret.put(lampes.get(i), (int)Math.round(x.get(i, 1)));
		}
		
		return ret;
	}
	
	/*
	 * Analyse la valeur des capteurs et envoie la commande en conséquence à la multiprise
	 */
	public void analyse()
	{
		int temperature_moyenne_courante = 0;
		int nb_capteurs_de_temperature = 0;
		//on parcourt l'ensemble des modules de capteurs présents dans la pièce
		for (ModuleCapteurs mc : modules.values())
		{
			//on fait la moyenne des valeurs des capteurs de meme type
			for (int i = 0; i < mc.getCapacity(); i++)
			{ //on parcourt les capteurs de chaque module
				Capteur capteurCourant = mc.getCapteur(i);
				
				if(capteurCourant != null)
				{
					if (capteurCourant.getType() == TypeMorceau.TEMPERATURE)
					{
						temperature_moyenne_courante += capteurCourant.getLastValeur();
						nb_capteurs_de_temperature++;
					}
					else if (capteurCourant.getType() == TypeMorceau.LUMINOSITE)
					{
						System.out.println("Raphael s'occupe de toi");
					}
					else
					{
						System.out.println("type de capteur non géré");
					}
				}
			}
		}
		
		if(nb_capteurs_de_temperature != 0)
			temperature_moyenne_courante /= nb_capteurs_de_temperature;
		
		//tous les capteurs ont été relevés, on s'occupe d'analyser et d'envoyer les commandes
		int commande = (temperature_moyenne_courante < currentProfil.getTemperature()) ? 1 : 0;
		
		for (Multiprise mp : getMultiprises().values())
		{
			for (int i = 0; i < 5; i++)
			{
				if (mp.getPrise(i).getType() == TypeMorceau.LUMINOSITE)
				{
					mp.sendMessage("/" + i + ":" + commande);
				}
			}
		}
	}
}
