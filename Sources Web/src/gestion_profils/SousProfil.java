package gestion_profils;

import java.util.HashMap;
import java.util.Set;

/**
 * un sous-profil est associé à une salle, 
 * et prend le pas sur le profil global
 * 
 * @author raphael
 */
public class SousProfil extends AbstractProfil
{
	private gestion_profils.Salle salle;
	private HashMap<Prise, Etat> configPrises;

	public SousProfil(String nom, int temperature, int luminosite, Salle salle)
	{
		super(nom, temperature, luminosite);
		this.salle = salle;
		configPrises = new HashMap<Prise, Etat>();
	}
	
	public void addPrise(Prise p, Etat e)
	{
		configPrises.put(p, e);
	}
	
	//GETTERS
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
}
