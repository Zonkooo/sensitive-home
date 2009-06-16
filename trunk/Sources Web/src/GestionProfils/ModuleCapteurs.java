package GestionProfils;

public class ModuleCapteurs extends Socle
{
	public ModuleCapteurs(long ID)
	{
		super(ID);
	}
	
	public ModuleCapteurs(long ID, Capteur[] capteurs)
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
