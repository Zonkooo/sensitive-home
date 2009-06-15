package GestionProfils;

import java.util.ArrayList;

public class Salle
{
	private String nom;
	private int currentTemperature;
	private int currentLuminosite;

	private ArrayList<Multiprise> multiprises;
	private ArrayList<ModuleCapteurs> modules;

	public Salle(String nom)
	{
		this.nom = nom;
		this.currentTemperature = Capteur.VALEUR_INCONNUE;
		this.currentLuminosite = Capteur.VALEUR_INCONNUE;
		this.multiprises = new ArrayList<Multiprise>();
		this.modules = new ArrayList<ModuleCapteurs>();
	}

	public String getNom()
	{
		return nom;
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

	public void removeMultiprise(long ID)
	{
		//TODO
	}

	public void removeModule(long ID)
	{
		//TODO
	}
}
