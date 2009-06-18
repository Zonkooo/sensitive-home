package gestion_profils;

public class ModuleCapteurs extends Socle
{
	public ModuleCapteurs(int ID,int taille)
	{
		super(ID,taille);
	}
	
	public ModuleCapteurs(int ID, Capteur[] capteurs)
	{
		super(ID, capteurs);
	}

	public void setCapteur(Capteur c)
	{
		super.setMorceau(c);
	}

	public Capteur getCapteur(int index)
	{
		return (Capteur)(super.getMorceau(index));
	}
}
