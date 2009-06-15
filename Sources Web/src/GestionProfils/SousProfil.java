package GestionProfils;

import java.util.HashMap;

/**
 * un sous-profil est associé à une salle, 
 * et prend le pas sur le profil global
 * 
 * @author raphael
 */
public class SousProfil
{
	private String nom;
	private int temperature;
	private int luminosite;
	
	private GestionProfils.Salle salle;
	private HashMap<Prise, Etat> configPrises;

	public SousProfil(int temperature, int luminosite, Salle salle)
	{
		this.temperature = temperature;
		this.luminosite = luminosite;
		this.salle = salle;
		configPrises = new HashMap<Prise, Etat>();
	}
	
	public void addPrise(Prise p, Etat e)
	{
		configPrises.put(p, e);
	}
	
	//GETTERS
	public int getLuminosite()
	{
		return luminosite;
	}
	public String getNom()
	{
		return nom;
	}
	public int getTemperature()
	{
		return temperature;
	}
	
	//SETTERS
	public void setLuminosite(int luminosite)
	{
		this.luminosite = luminosite;
	}
	public void setNom(String nom)
	{
		this.nom = nom;
	}
	public void setTemperature(int temperature)
	{
		this.temperature = temperature;
	}

	@Override public String toString()
	{
		return this.nom;
	}
	
	@Override public boolean equals(Object o)
	{
		return (this.toString()).equals(o.toString());
	}
}
