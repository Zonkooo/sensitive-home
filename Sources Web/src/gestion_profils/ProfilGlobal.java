package gestion_profils;


/**
 * Un profil global est appliqué à toute la maison, 
 * et a une priorité inférieure à celle d'un sous-profil
 * 
 * @author raphael
 */
public class ProfilGlobal extends AbstractProfil
{
	public ProfilGlobal(String nom, int temperatureMin, int luminositeGlobale)
	{
		super(nom, temperatureMin, luminositeGlobale);
	}
}
