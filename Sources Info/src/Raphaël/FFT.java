import javasci.*;

/*  
 *  source : http://www.ling.upenn.edu/~tklee/Projects/dsp/
 */

public class FFT
{    
//    int n, m;
//    // Lookup tables.  Only need to recompute when size of FFT changes.
//    double[] cos;
//    double[] sin;
//    double[] window;
//
//    public FFT(int n)
//    {
//        this.n = n;
//
//        // Make sure n is a power of 2
//        if (n != (1 << lg(n)))
//        {
//            throw new RuntimeException("FFT length must be power of 2");
//        }
//
//        // precompute tables
//        cos = new double[n / 2];
//        sin = new double[n / 2];
//
////     for(int i=0; i<n/4; i++) {
////       cos[i] = Math.cos(-2*Math.PI*i/n);
////       sin[n/4-i] = cos[i];
////       cos[n/2-i] = -cos[i];
////       sin[n/4+i] = cos[i];
////       cos[n/2+i] = -cos[i];
////       sin[n*3/4-i] = -cos[i];
////       cos[n-i]   = cos[i];
////       sin[n*3/4+i] = -cos[i];        
////     }
//
//        for (int i = 0; i < n / 2; i++)
//        {
//            cos[i] = Math.cos(-2 * Math.PI * i / n);
//            sin[i] = Math.sin(-2 * Math.PI * i / n);
//        }
//
//        makeWindow();
//    }
//
//    protected void makeWindow()
//    {
//        // Make a Blackman window:
//        // w(n)=0.42-0.5cos{(2*PI*n)/(N-1)}+0.08cos{(4*PI*n)/(N-1)};
//        window = new double[n];
//        for (int i = 0; i < window.length; i++)
//        {
//            window[i] = 0.42 - 0.5 * Math.cos(2 * Math.PI * i / (n - 1)) + 0.08 * Math.cos(4 * Math.PI * i / (n - 1));
//        }
//    }
//
//    /*
//     * http://cnx.rice.edu/content/m12016/latest/
//     */
//public void fft(double[] x)
//    {
//        int i, j, k, n1, n2, a;
//        double c, s, e, t1, t2;
//        double[] y = new double[x.length];
//
//
//        // Bit-reverse
//        j = 0;
//        n2 = n / 2;
//        for (i = 1; i < n - 1; i++)
//        {
//            n1 = n2;
//            while (j >= n1)
//            {
//                j = j - n1;
//                n1 = n1 / 2;
//            }
//            j = j + n1;
//
//            if (i < j)
//            {
//                t1 = x[i];
//                x[i] = x[j];
//                x[j] = t1;
//                t1 = y[i];
//                y[i] = y[j];
//                y[j] = t1;
//            }
//        }
//
//        // FFT
//        n1 = 0;
//        n2 = 1;
//
//        for (i = 0; i < m; i++)
//        {
//            n1 = n2;
//            n2 = n2 + n2;
//            a = 0;
//
//            for (j = 0; j < n1; j++)
//            {
//                c = cos[a];
//                s = sin[a];
//                a += 1 << (m - i - 1);
//
//                for (k = j; k < n; k = k + n2)
//                {
//                    t1 = c * x[k + n1] - s * y[k + n1];
//                    t2 = s * x[k + n1] + c * y[k + n1];
//                    x[k + n1] = x[k] - t1;
//                    y[k + n1] = y[k] - t2;
//                    x[k] = x[k] + t1;
//                    y[k] = y[k] + t2;
//                }
//            }
//        }
//
//        for (i = 0; i < x.length; i++)
//        {
//            x[i] = Math.sqrt(x[i] * x[i] + y[i] * y[i]);
//        }
//    }
//    
    // Test the FFT to make sure it's working
    public static void main(String[] args)
    {
        Capture capture = new Capture();
        int[] audio = capture.ecoute(1000);
        
        //conversion en double
        double[] audioDouble = new double[audio.length];
        for(int i = 0; i < audio.length; i++)
            audioDouble[i] = (double)audio[i];
        
        audioDouble = FFT.zeroPadding(audioDouble);
        
        FFT fft = new FFT();
        audioDouble = fft.fftMag(audioDouble);
        
        for(int i = 0; i < audioDouble.length; i++)
            System.out.println(audioDouble[i]);
    }
    
    private static double[] zeroPadding(double[] sig)
    {
        double[] padded = new double[1 << (lg(sig.length) + 1)];
        for(int i = 0; i < sig.length; i++)
            padded[i] = sig[i];
        
        return padded;
    }
    
    private int nu, n;

    private int bitrev(int j)
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

    public double[] fftMag(double[] x)
    {
        // assume n is a power of 2
        n = x.length;
        nu = lg(n);
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
                    p = bitrev(k >> nu1);
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
            r = bitrev(k);
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
