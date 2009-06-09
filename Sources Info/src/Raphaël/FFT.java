/*  
 *  source : http://www.ling.upenn.edu/~tklee/Projects/dsp/
 */

public class FFT
{
    // Test the FFT to make sure it's working
    public static void main(String[] args)
    {
        Capture capture = new Capture();
        int[] audio = capture.ecoute(1000);
                
        double[] audioDouble = FFT.zeroPadding(audio);
        
        audioDouble = FFT.fftMag(audioDouble);
        
        for(int i = 0; i < audioDouble.length; i++)
            System.out.println(audioDouble[i]);
    }
    
    /**
     * Complete le signal sig avec des zeros 
     * jusqu'à atteindre une longeur qui soit une puissance de 2
     * et le transforme en double au passage
     * 
     * @param sig signal originel
     * @return signal de longueur une puissance entière de 2
     */
    private static double[] zeroPadding(int[] sig)
    {
        double[] padded = new double[1 << (lg(sig.length) + 1)];
        for(int i = 0; i < sig.length; i++)
            padded[i] = sig[i];
        
        return padded;
    }
    
    //TODO : comprendre & commenter
    private static int bitrev(int j, int nu)
    {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++)
        {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
    }

    //TODO : comprendre & commenter
    public static double[] fftMag(double[] x)
    {
        // assume n is a power of 2
        int n = x.length;
        int nu = lg(n);
        int n2 = n / 2;
        int nu1 = nu - 1;
        double[] xre = new double[n];
        double[] xim = new double[n];
        double[] mag = new double[n2];
        double tr, ti, p, arg, c, s;
        for (int i = 0; i < n; i++)
        {
            xre[i] = x[i];
            xim[i] = 0.0f;
        }
        int k = 0;

        for (int l = 1; l <= nu; l++)
        {
            while (k < n)
            {
                for (int i = 1; i <= n2; i++)
                {
                    p = bitrev(k >> nu1, nu);
                    arg = 2 * (double) Math.PI * p / n;
                    c = (double) Math.cos(arg);
                    s = (double) Math.sin(arg);
                    tr = xre[k + n2] * c + xim[k + n2] * s;
                    ti = xim[k + n2] * c - xre[k + n2] * s;
                    xre[k + n2] = xre[k] - tr;
                    xim[k + n2] = xim[k] - ti;
                    xre[k] += tr;
                    xim[k] += ti;
                    k++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 = n2 / 2;
        }
        k = 0;
        int r;
        while (k < n)
        {
            r = bitrev(k, nu);
            if (r > k)
            {
                tr = xre[k];
                ti = xim[k];
                xre[k] = xre[r];
                xim[k] = xim[r];
                xre[r] = tr;
                xim[r] = ti;
            }
            k++;
        }

        mag[0] = (double) (Math.sqrt(xre[0] * xre[0] + xim[0] * xim[0])) / n;
        for (int i = 1; i < n / 2; i++)
        {
            mag[i] = 2 * (double) (Math.sqrt(xre[i] * xre[i] + xim[i] * xim[i])) / n;
        }
        return mag;
    }
    
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
}
