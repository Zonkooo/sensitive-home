package GestionProfils;

public class ModuleCapteurs
{
	long ID;
	Capteur[] capteurs;

	public ModuleCapteurs(long ID, Capteur[] capteurs)
	{
		this.ID = ID;
		this.capteurs = capteurs;
	}

	public long getID()
	{
		return this.ID;
	}

	public void setCapteur(Capteur c, int index)
	{
		capteurs[index] = c;
	}

	public Capteur getCapteurs(int index)
	{
		return capteurs[index];
	}
}
