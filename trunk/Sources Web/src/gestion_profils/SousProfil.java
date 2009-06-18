package gestion_profils;

import java.util.HashMap;
import java.util.Set;

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
	
	private gestion_profils.Salle salle;
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
	public Set<Prise> getPrises()
	{
		return configPrises.keySet();
	}
	public Etat getEtat(Prise p)
	{
		return configPrises.get(p);
	}
	public Salle getSalle()
	{
		return salle;
	}

	@Override public String toString()
	{
		return this.nom;
	}
}
