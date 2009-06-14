package sensitive;

/**
 * contient des outils statiques qui n'ont pas leur place
 * dans une classe particuliaire
 * 
 * @author raphael
 */
public class Outils 
{
    /**
     * partie entière du logarithme base 2 de n
     * @param n
     * @return abs(log_2(n))
     */
    public static int lg(int n)
    {
        int r = 0;
        while(n >> r != 0)
            r++;
        
        return r - 1;
    }
    
	/**
	 * retourne la valeur max du tableau passé en param
	 * 
	 * @param tab
	 * @return max
	 */
    public static int getMax(int[] tab)
    {
        int max = -1;
        
        for (int i = 0; i < tab.length; i++)
            if(Math.abs(tab[i]) > max)
                max = tab[i];
                
        return max;
    }
	
	/**
	 * retourne la valeur max du tableau passé en param.
	 * optimisé pour un tableau plus large que long
	 * (tab.length < tab[0].lenght)
	 * 
	 * @param tab
	 * @return max
	 */
    public static int getMax(int[][] tab)
    {
        int max = -1;
        
		for (int i = 0; i < tab.length; i++)
			for (int j = 0; j < tab[0].length; j++)
				if(Math.abs(tab[i][j]) > max)
					max = tab[i][j];
                
        return max;
    }
    
    /**
     * normalise le vecteur suivant la norme 2
     * et le convertit en double[]
     * 
     * @param vect vecteur à normaliser
     * @return vecteur normalisé
     */
    public static double[] normalize(double[] vect)
    {
        double som = 0;
        double[] norm = new double[vect.length];
        
        for (int i = 0; i < vect.length; i++)
            som += vect[i]*vect[i];
		som = Math.sqrt(som);
        
        for (int i = 0; i < vect.length; i++)
            norm[i] = vect[i]/som;
        
        return norm;
    }
	
	/**
	 * concatene les 2 tableaux passés en paramètre
	 * 
	 * @param debut
	 * @param fin
	 * @return debut + fin
	 */
    public static int[] concatene(int[] debut, int[] fin)
    {
        int[] ret = new int[debut.length + fin.length];
        for (int i = 0; i < ret.length; i++)
        {
            if(i < debut.length)
                ret[i] = debut[i];
            else
                ret[i] = fin[i - debut.length];
        }
        return ret;
    }
	
	/**
	 * concatene en ligne les 2 tableaux passés en paramètre.
	 * optimisé pour un tableau plus large que long
	 * (tab.length < tab[0].lenght)
	 * 
	 * @param debut
	 * @param fin
	 * @return debut + fin
	 */
    public static int[][] concatene(int[][] debut, int[][] fin)
    {
        int[][] ret = new int[debut.length][];
		
        for (int i = 0; i < ret.length; i++)
        {
			ret[i] = concatene(debut[i], fin[i]);
        }
        return ret;
    }
	
	/**
	 * @param f(t)
	 * @return f(-t)
	 */
	public static int[] reverse(int[] sig)
	{
		int tmp;
		for(int i = 0; i < sig.length/2; i++)
		{
			tmp = sig[i];
			sig[i] = sig[sig.length - i - 1];
			sig[sig.length - i - 1] = tmp;
		}
		return sig;
	}
}