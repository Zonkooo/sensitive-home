package sensitive;

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
    
    public static int getMax(int[] tab)
    {
        int max = -1;
        
        for (int i = 0; i < tab.length; i++)
            if(Math.abs(tab[i]) > max)
                max = tab[i];
                
        return max;
    }
    
    /**
     * normalise le vecteur suivant la norme 2
     * et le convertit en double[]
     * 
     * @param vect vecteur à normaliser
     * @return vecteur normalisé
     */
    public static double[] normalize(int[] vect)
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
        
        public static double[] subMoy(double[] vect)
        {               
        double som = 0;
        
        for (int i = 0; i < vect.length; i++)
            som += vect[i];
                som /= vect.length;
                
        for (int i = 0; i < vect.length; i++)
            vect[i] -= som;
                
                return vect;
        }
    
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
}
