
/**
 * Un profil global est appliqué à toute la maison, 
 * et a une priorité inférieure à celle d'un sous-profil
 * 
 * @author raphael
 */
public class ProfilGlobal 
{
	private String nom;
	private int temperatureMin;
	private int luminositeGlobale;

	public ProfilGlobal(String nom, int temperatureMin, int luminositeGlobale)
	{
		this.nom = nom;
		this.temperatureMin = temperatureMin;
		this.luminositeGlobale = luminositeGlobale;
	}

	//GETTERS
	public int getLuminositeGlobale()
	{
		return luminositeGlobale;
	}
	public String getNom()
	{
		return nom;
	}
	public int getTemperatureMin()
	{
		return temperatureMin;
	}
	
	//SETTERS
	public void setLuminositeGlobale(int luminositeGlobale)
	{
		this.luminositeGlobale = luminositeGlobale;
	}
	public void setNom(String nom)
	{
		this.nom = nom;
	}
	public void setTemperatureMin(int temperatureMin)
	{
		this.temperatureMin = temperatureMin;
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
