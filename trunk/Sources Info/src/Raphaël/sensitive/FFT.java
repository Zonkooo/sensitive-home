package sensitive;

/*  
 *  source : http://www.ling.upenn.edu/~tklee/Projects/dsp/
 */

import java.util.Scanner;
import java.util.ArrayList;

/**
 * fft et utilitaires
 * 
 * @author raphael
 */
public class FFT
{
    public static void main(String[] args)
    {
		Capture capture = new Capture();
		int[][] audio;
		double[][] audioDouble1 = new double[2][];
		double[][] audioDouble2 = new double[2][];
		
		ArrayList<Bouton> btns = new ArrayList<Bouton>();
		int k = 1;
		
		Scanner sc = new Scanner(System.in);
		System.out.println("nb de boutons à saisir ?\n");
		int x = sc.nextInt();
		
		while(k <= x)
		{
			System.out.println("tapez le bouton " + k++);
			audio = capture.getTap();
			audioDouble1[0] = Outils.normalize(fftMag(zeroPadding(audio[0])));
			if(audio.length == 2)//stereo
				audioDouble1[1] = Outils.normalize(fftMag(zeroPadding(audio[1])));
			
//			System.out.println("encore !");
//			audio = capture.getTap();
//			audioDouble2[0] = Outils.normalize(fftMag(zeroPadding(audio[0])));
//			if(audio.length == 2)//stereo
//				audioDouble2[1] = Outils.normalize(fftMag(zeroPadding(audio[1])));
//						
//			//moyenne des 2 coups
//			for(int i = 0; i < audioDouble1.length; i++)
//				for(int j = 0; j < audioDouble1[0].length; j++)
//					audioDouble1[i][j] = audioDouble1[i][j] + audioDouble2[i][j];
			
			btns.add(new Bouton(audioDouble1));
		}
		
		System.out.println();
		
		while(true)
		{
			audio = capture.getTap(); 
			Outils.reverse(audio[0]);
			audioDouble1[0] = Outils.normalize(fftMag(zeroPadding(audio[0])));
			if(audio.length == 2)//stereo
			{	
				Outils.reverse(audio[1]);
				audioDouble1[1] = Outils.normalize(fftMag(zeroPadding(audio[1])));
			}
			
//			for (int i = 0; i < audioDouble1[0].length; i++)
//			{
//				System.out.println(audioDouble1[0][i]);
//			}

			
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
    }
    
    /**
     * Complete le signal sig avec des zeros 
     * jusqu'à atteindre une longeur qui soit une puissance de 2
     * 
     * @param sig signal originel
     * @return signal de longueur une puissance entière de 2
     */
    public static int[] zeroPadding(int[] sig)
    {
        int[] padded = new int[1 << ((Outils.lg(sig.length)) + 1)];
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
    public static double[] fftMag(int[] x)
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
            xre[i] = (double)x[i];
		
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

        for (int i = 0; i < n/2; i++)
        {
            mag[i] = (double)(xre[i] * xre[i] + xim[i] * xim[i]);
        }
        return mag;
    }
}
