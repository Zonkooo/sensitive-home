package gestion_profils;


/**
 * Un profil global est appliqué à toute la maison, 
 * et a une priorité inférieure à celle d'un sous-profil
 * 
 * @author raphael
 */
public class ProfilGlobal 
{
	//TODO : attribuer un ID ?
	private String nom;
	private int temperature;
	private int luminosite;

	public ProfilGlobal(String nom, int temperatureMin, int luminositeGlobale)
	{
		this.nom = nom;
		this.temperature = temperatureMin;
		this.luminosite = luminositeGlobale;
	}

	public ProfilGlobal()
	{
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
