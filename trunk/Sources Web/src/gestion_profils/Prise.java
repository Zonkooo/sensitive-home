package gestion_profils;

public class Prise extends Morceau
{
	private Etat etat;

	public Prise(Etat etat, TypeMorceau type, Socle owner, int position)
	{
		super(type,owner, position);
		this.etat = etat;
	}

	public Prise(TypeMorceau type, Socle owner, int position)
	{
		super(type, owner, position);
		this.etat = Etat.AUTO;
	}

	@Override
	public Multiprise getOwner()
	{
		return (Multiprise)(super.getOwner());
	}
		
	public void setEtat(Etat etat)
	{
		this.etat = etat;
		//TODO : repercuter cette modif sur la prise réelle
	}
	
	public Etat getEtat()
	{
		return this.etat;
	}
}
