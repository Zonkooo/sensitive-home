package sensitive;

import java.util.ArrayList;

/*  
 *  source : http://www.ling.upenn.edu/~tklee/Projects/dsp/
 */
import java.util.Scanner;
public class FFT
{
    public static void main(String[] args)
    {
                Capture capture = new Capture();
                int[] audio;
                double[] audioDouble1, audioDouble2;
                
                ArrayList<Bouton> btns = new ArrayList<Bouton>();
                int k = 1;
                
                Scanner sc = new Scanner(System.in);
                System.out.println("nb de boutons à saisir ?\n");
                int x = sc.nextInt();
                
                while(k <= x)
                {
                        System.out.println("tapez le bouton " + k++);
                        audio = capture.getTap();
                        audioDouble1 = fftMag(zeroPadding(Outils.normalize(audio)));
                        do
                        {
                                System.out.println("encore !");
                                audio = capture.getTap();
                                audioDouble2 = fftMag(zeroPadding(Outils.normalize(audio)));
                        } while(audioDouble1.length != audioDouble2.length);

                        for(int i = 0; i < audioDouble1.length; i++)
                                audioDouble1[i] = audioDouble1[i] + audioDouble2[i];
                        btns.add(new Bouton(audioDouble1));
                }
                
                System.out.println();
                
                while(true)
                {
                        audio = capture.getTap();
                        audioDouble1 = fftMag(zeroPadding(Outils.normalize(audio)));
                        
                        double cor, max = -1;
                        int indice = -1;
                        
                        for(int i = 0; i < btns.size(); i++)
                        {
                                cor = btns.get(i).correlation(audioDouble1);
								System.out.println(cor);
                                if(cor > max)
                                {
                                        max = cor;
                                        indice = i;
                                }
                        }

                        System.out.println("bouton " + (indice + 1));
                }
                        
//              Capture capture = new Capture();
//              int[] audio;
//              double[] audioDouble1, audioDouble2;
//              
//              //bouton 1
//              System.out.println("tapez le bouton 1");
//              audio = capture.getTap();
//              audioDouble1 = fftMag(zeroPadding(Outils.normalize(audio)));
//              System.out.println("encore !");
//              audio = capture.getTap();
//              audioDouble2 = fftMag(zeroPadding(Outils.normalize(audio)));
//              
//              for(int i = 0; i < audioDouble1.length; i++)
//                      audioDouble1[i] = audioDouble1[i] + audioDouble2[i];
//              Bouton b1 = new Bouton(audioDouble1);
//              
//              //bouton 2
//              System.out.println("tapez le bouton 2");
//              audio = capture.getTap();
//              audioDouble1 = fftMag(zeroPadding(Outils.normalize(audio)));
//              System.out.println("encore !");
//              audio = capture.getTap();
//              audioDouble2 = fftMag(zeroPadding(Outils.normalize(audio)));
//              
//              for(int i = 0; i < audioDouble1.length; i++)
//                      audioDouble1[i] = audioDouble1[i] + audioDouble2[i];
//              Bouton b2 = new Bouton(audioDouble1);
//              
//              System.out.println();
//              
//              while(true)
//              {
//                      audio = capture.getTap();
//                      audioDouble1 = fftMag(zeroPadding(Outils.subMoy(Outils.normalize(audio))));
//                      
//                      double c1 = b1.correlation(audioDouble1);
//                      double c2 = b2.correlation(audioDouble1);
//
//                      if(c2 > c1)
//                              System.out.println("bouton2");
//                      else
//                              System.out.println("bouton1");
//              }
    }
    
    /**
     * Complete le signal sig avec des zeros 
     * jusqu'à atteindre une longeur qui soit une puissance de 2
     * 
     * @param sig signal originel
     * @return signal de longueur une puissance entière de 2
     */
    public static double[] zeroPadding(double[] sig)
    {
        double[] padded = new double[65536/*1 << ((Outils.lg(sig.length)) + 1)*/];
        for(int i = 0; i < sig.length; i++)
            padded[i] = sig[i];
        
        return padded;
    }
    
    //TODO : comprendre & commenter
    private static int bitrev(int j1, int nu)
    {
        int j2;
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
        int nu = Outils.lg(n);
        int n2 = n / 2;
        int nu1 = nu - 1;
        double[] xre = new double[n];
        double[] xim = new double[n];
        double[] mag = new double[n2];
        double tr, ti, p, arg, c, s;
        for (int i = 0; i < n; i++)
        {
            xre[i] = x[i];
            xim[i] = 0.0;
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

        for (int i = 0; i < n / 2; i++)
        {
            mag[i] = (double) (Math.sqrt(xre[i] * xre[i] + xim[i] * xim[i]));
        }
        return mag;
    }
}
