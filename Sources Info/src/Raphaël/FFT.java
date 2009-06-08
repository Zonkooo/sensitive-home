/*  
 *  source : http://www.ee.columbia.edu/~ronw/code/MEAPsoft/doc/html/FFT_8java-source.html
 */

public class FFT
{

    int n, m;
    // Lookup tables.  Only need to recompute when size of FFT changes.
    double[] cos;
    double[] sin;
    double[] window;

    public FFT(int n)
    {
        this.n = n;
        this.m = (int) (Math.log(n) / Math.log(2));

        // Make sure n is a power of 2
        if (n != (1 << m))
        {
            throw new RuntimeException("FFT length must be power of 2");
        }

        // precompute tables
        cos = new double[n / 2];
        sin = new double[n / 2];

//     for(int i=0; i<n/4; i++) {
//       cos[i] = Math.cos(-2*Math.PI*i/n);
//       sin[n/4-i] = cos[i];
//       cos[n/2-i] = -cos[i];
//       sin[n/4+i] = cos[i];
//       cos[n/2+i] = -cos[i];
//       sin[n*3/4-i] = -cos[i];
//       cos[n-i]   = cos[i];
//       sin[n*3/4+i] = -cos[i];        
//     }

        for (int i = 0; i < n / 2; i++)
        {
            cos[i] = Math.cos(-2 * Math.PI * i / n);
            sin[i] = Math.sin(-2 * Math.PI * i / n);
        }

        makeWindow();
    }

    protected void makeWindow()
    {
        // Make a blackman window:
        // w(n)=0.42-0.5cos{(2*PI*n)/(N-1)}+0.08cos{(4*PI*n)/(N-1)};
        window = new double[n];
        for (int i = 0; i < window.length; i++)
        {
            window[i] = 0.42 - 0.5 * Math.cos(2 * Math.PI * i / (n - 1)) + 0.08 * Math.cos(4 * Math.PI * i / (n - 1));
        }
    }

    /*
     * http://cnx.rice.edu/content/m12016/latest/
     */
    public void fft(double[] x)
    {
        int i, j, k, n1, n2, a;
        double c, s, t1, t2;


        // Bit-reverse
        j = 0;
        n2 = n / 2;
        for (i = 1; i < n - 1; i++)
        {
            n1 = n2;
            while (j >= n1)
            {
                j = j - n1;
                n1 = n1 / 2;
            }
            j = j + n1;

            if (i < j)
            {
                t1 = x[i];
                x[i] = x[j];
                x[j] = t1;
            }
        }

        // FFT
        n1 = 0;
        n2 = 1;

        for (i = 0; i < m; i++)
        {
            n1 = n2;
            n2 = n2 + n2;
            a = 0;

            for (j = 0; j < n1; j++)
            {
                c = cos[a];
                s = sin[a];
                a += 1 << (m - i - 1);

                for (k = j; k < n; k = k + n2)
                {
                    t1 = c * x[k + n1];
                    t2 = s * x[k + n1];
                    x[k + n1] = x[k] - t1;
                    x[k] = x[k] + t1;
                }
            }
        }
    }
    
    private double[] zeroPadding(double[] sig)
    {
        return null;
    }

    // Test the FFT to make sure it's working
    public static void main(String[] args)
    {
        Capture capture = new Capture();
        capture.start();
        try 
        { Thread.sleep(1000); } //enregistrement pendant 1 seconde
        catch (InterruptedException ie) 
        { System.out.println(ie.toString());} //ceci n'arrive jamais
        capture.stop();
        
        int[] audio = capture.getAudioData();
        double[] audioDouble = new double[audio.length];
        for(int i = 0; i < audio.length; i++)
            audioDouble[i] = (double)audio[i];
        
        //TODO : zero padding
        FFT fft = new FFT(42);
        
        
//        int N = 8;
//
//        FFT fft = new FFT(N);
//
//        double[] re = new double[N];
//
//        // Impulse
//        re[0] = 1;
//        for (int i = 1; i < N; i++)
//        {
//            re[i] = 0;
//        }
//        beforeAfter(fft, re);
//
//        // Nyquist
//        for (int i = 0; i < N; i++)
//        {
//            re[i] = Math.pow(-1, i);
//        }
//        beforeAfter(fft, re);
//
//        // Single sin
//        for (int i = 0; i < N; i++)
//        {
//            re[i] = Math.cos(2 * Math.PI * i / N);
//        }
//        beforeAfter(fft, re);
//
//        // Ramp
//        for (int i = 0; i < N; i++)
//        {
//            re[i] = i;
//        }
//        beforeAfter(fft, re);
//
//        long time = System.currentTimeMillis();
//        double iter = 30000;
//        for (int i = 0; i < iter; i++)
//        {
//            fft.fft(re);
//        }
//        time = System.currentTimeMillis() - time;
//        System.out.println("Averaged " + (time / iter) + "ms per iteration");
    }

//    protected static void beforeAfter(FFT fft, double[] re)
//    {
//        System.out.println("Before: ");
//        printReIm(re);
//        fft.fft(re);
//        System.out.println("After: ");
//        printReIm(re);
//    }
//
//    protected static void printReIm(double[] re)
//    {
//        System.out.print("Re: [");
//        for (int i = 0; i < re.length; i++)
//        {
//            System.out.print(((int) (re[i] * 1000) / 1000.0) + " ");
//        }
//        System.out.println("]");
//    }
}
