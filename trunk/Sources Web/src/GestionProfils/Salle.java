package GestionProfils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Salle
{
	private String nom;

	private ArrayList<Multiprise> multiprises;
	private ArrayList<ModuleCapteurs> modules;
	
	private ArrayList<SousProfil> availablesProfils;
	private SousProfil currentProfil;

	public Salle(String nom)
	{
		this.nom = nom;
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
	
	/****************************
	 ***   Calibrage lampes   ***
	 ****************************/
	
	public void calibrationLampes()
	{
		ArrayList<Prise> lampes = new ArrayList<Prise>();
		ArrayList<Capteur> photocapteurs = new ArrayList<Capteur>();
		
		for (Multiprise multiprise : multiprises)
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
		
		for (ModuleCapteurs moduleCapteurs : modules)
		{
			for(int i = 0; i < moduleCapteurs.getCapacity(); i++)
			{
				Capteur c = moduleCapteurs.getCapteur(i);
				if(c != null && c.getType() == TypeMorceau.LUMINOSITE)
					photocapteurs.add(c);
			}
		}


		for (Prise prise : lampes)
		{
			prise.setEtat(Etat.ON);
			
		}

	}
	
	/*
	 * Analyse la valeur des capteurs et envoie la commande en conséquence à la multiprise
	 */
	public void analyse()
	{
		int temperature_moyenne_courante = 0;
		int nb_capteurs_de_temperature = 0;
		//on parcourt l'ensemble des modules de capteurs présents dans la pièce
		for (ModuleCapteurs mc : modules)
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
		
		for (Multiprise mp : getMultiprises())
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
