package GestionProfils;

public class Prise
{
	private Etat etat;
	private Appareil app;
	
	private Multiprise owner;
	private int position;

	public Prise(Etat etat, Appareil app, Multiprise owner, int position)
	{
		this.etat = etat;
		this.app = app;
		this.owner = owner;
		this.position = position;
	}

	public Prise(Appareil app, Multiprise owner, int position)
	{
		this.app = app;
		this.owner = owner;
		this.position = position;
		
		this.etat = Etat.AUTO;
	}

	public Multiprise getOwner()
	{
		return owner;
	}

	public int getPosition()
	{
		return position;
	}

	public void setEtat(Etat etat)
	{
		this.etat = etat;
	}
}
