
import gestion_profils.Salle;


/**
 * un sous-profil est associé à une salle, 
 * et prend le pas sur le profil global
 * 
 * @author raphael
 */
public class SousProfil extends ProfilGlobal
{
	Salle salle;
	//TODO :
	//de cette salle, on récupère l'ensemble de prises
	//que l'on configure suivant le profil :
	//on, off ou auto

	public SousProfil(String nom, int temperatureMin, int luminositeGlobale, Salle salle)
	{
		super(nom, temperatureMin, luminositeGlobale);
		this.salle = salle;
	}
}
