package sensitive;

public class Bouton 
{
        private double[] fft;

        public Bouton(double[] fft)
        {
                this.fft = new double[fft.length];
                
                for (int i = 0; i < fft.length; i++)
                        this.fft[i] = fft[i];
        }
        
        public double correlation(double[] fftTap)
    {
                double correlation = 0;
                int len = Math.min(fftTap.length, fft.length);
                
                for (int i = 0; i < len; i++)
                        correlation += fft[i]*fftTap[i];

        return correlation;
    }
}
