package GestionProfils;

public class Prise
{
	Etat etat;
	Appareil app;

	public Prise(Etat etat, Appareil app)
	{
		this.etat = etat;
		this.app = app;
	}
	
	public Prise(Appareil app)
	{
		this.etat = Etat.AUTO;
		this.app = app;
	}
}
