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
            if(tab[i] > max)
                max = tab[i];
                
        return max;
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
