package GestionProfils;

/**
 *
 * @author raphael
 */
public class Capteur extends Morceau
{
	public final static int VALEUR_INCONNUE = Integer.MIN_VALUE;
	
	private TypeMorceau type;
	private int lastValeur = VALEUR_INCONNUE;

	public Capteur(TypeMorceau type, Socle owner, int position)
	{
		super(type,owner, position);
	}

	@Override
	public ModuleCapteurs getOwner()
	{
		return (ModuleCapteurs)(super.getOwner());
	}
}
