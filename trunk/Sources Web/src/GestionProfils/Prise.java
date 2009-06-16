package GestionProfils;

public class Prise extends Morceau
{
	private Etat etat;

	public Prise(Etat etat, TypeMorceau type, Socle owner, int position)
	{
		super(type,owner, position);
		this.etat = etat;
	}

	public void setEtat(Etat etat)
	{
		this.etat = etat;
	}
}
