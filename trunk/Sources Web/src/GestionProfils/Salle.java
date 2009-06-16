package GestionProfils;

import java.util.ArrayList;

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
}
