package gestion_profils;

public abstract class Morceau
{
	private TypeMorceau type;
	private Socle owner;
	private int position;

	public Morceau(TypeMorceau type, Socle owner, int position)
	{
		this.type = type;
		this.owner = owner;
		this.position = position;
	}

	public TypeMorceau getType()
	{
		return type;
	}

	public void setType(TypeMorceau type)
	{
		this.type = type;
	}

	protected Socle getOwner()
	{
		return owner;
	}

	public int getPosition()
	{
		return position;
	}
}
