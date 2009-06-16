package GestionProfils;

/**
 *
 * @author raphael
 */
public class Capteur extends Morceau
{
	public final static int VALEUR_INCONNUE = Integer.MIN_VALUE;
	
	private TypeMorceau type;
	private int lastValeur;

	public Capteur(TypeMorceau type, Socle owner, int position)
	{
		super(type,owner, position);
		lastValeur =0;
	}	
}
