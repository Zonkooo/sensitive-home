package sensitive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * représente une zone sur la surface tactile
 * et lui associe une fft, pour l'identifier ensuite.
 *
 * @author raphael
 */
public class Bouton
{
        private static final int NB_TAP_CALIBRATION = 9;
        private static final double SEUIL_BOUTON_VALIDE = 0.83;

        private ArrayList<ActionListener> listeners;
        private double[][] fft;

        /**
         * créé un bouton en enregistrant sa signature sonore en fft
         * les deux cannaux (eventuels) de la FFT doivent être en fft[0] et fft[1]
         *
         * @param fft
         */
        public Bouton(double[][] fft)
        {
                this.listeners = new ArrayList<ActionListener>();
                this.fft = new double[fft.length][fft[0].length];

                //duplication du tableau
                for (int i = 0; i < fft.length; i++)
                {
                        for (int j = 0; j < fft[0].length; j++)
                        {
                                this.fft[i][j] = fft[i][j];
                        }
                }
        }

        /**
         * construit un bouton en écoutant l'entrée passée en param
         * ce qui nécéssite donc une action de l'utilisateur
         * pendant ce constructeur, sans quoi il ne retournera pas.
         *
         * @param capture entrée à écouter
         */
        public Bouton(Capture capture)
        {
                this.listeners = new ArrayList<ActionListener>();
                
                int[][] audio;
                double[][] audioDouble1 = new double[2][];
                double[][] audioDouble2 = new double[2][];

                System.out.println("tapez !");
                audio = capture.getTap(true);
                Outils.antiContinu(audio);
                audioDouble1[0] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[0])));
                if (audio.length == 2)//stereo
                {
                        audioDouble1[1] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[1])));
                }

                for(int i = 0; i < NB_TAP_CALIBRATION; i++)
                {
                        System.out.println("encore !");
                        audio = capture.getTap(true);
                        Outils.antiContinu(audio);
                        audioDouble2[0] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[0])));
                        if (audio.length == 2)//stereo
                        {
                                audioDouble2[1] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[1])));
                        }

                        //mix 2 coups
                        for (int j = 0; j < audioDouble1.length; j++)
                        {
                                for (int k = 0; k < audioDouble1[0].length; k++)
                                {
                                        audioDouble1[j][k] = (audioDouble1[j][k] + audioDouble2[j][k]) / 2;
                                }
                        }
                }

                this.fft = audioDouble1;
        }

        /**
         * cherche quel bouton parmis btn correspond le plus
         * au signal passé en param, et déclenche l'évenement associé à ce bouton.
         * 
         * @param audio
         * @param btns
         */
        public static void traiterSignal(int[][] audio, ArrayList<Bouton> btns)
        {
                double[][] audioDouble = new double[2][];

                //Outils.reverse(audio);
                Outils.antiContinu(audio);
                audioDouble[0] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[0])));
                if (audio.length == 2)//stereo
                {
                        audioDouble[1] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[1])));
                }

                double[] cor = new double[btns.size()];
                double max = -1;
                int indice = -1;

                for (int i = 0; i < btns.size(); i++)
                {
                        cor[i] = btns.get(i).correlation(audioDouble);
                        System.out.println(cor[i]);
                        if (cor[i] > max)
                        {
                                max = cor[i];
                                indice = i;
                        }
                }

                
                if(max > SEUIL_BOUTON_VALIDE)
                {
                        System.out.println("bouton " + (indice + 1));
                        btns.get(indice).pressButton(indice);
                }
                else
                {
                        System.out.println("essaye pas de m'avoir gringo");
                }
        }
        
        /**
         * retourne la correlation entre la fft du signal passé en paramètre
         * et la fft du bouton
         * plus les signaux sont proches, plus la correlation est élevée
         *
         * @param fftTap
         * @return
         */
        public double correlation(double[][] fftTap)
        {
                //la correlation est la convolution des deux signaux
                //i.e. la multiplication des TF
                double correlation = 0;
                for (int i = 0; i < fft.length; i++)
                {
                        for (int j = 0; j < fft[0].length; j++)
                        {
                                correlation += fft[i][j] * fftTap[i][j];
                        }
                }
                correlation /= fft.length;

                return correlation;
        }

        /**********************************
         ***   Gestion des evenements   ***
         **********************************/
        public void addListener(ActionListener al)
        {
                this.listeners.add(al);
        }

        public void removeListener(ActionListener al)
        {
                this.listeners.remove(al);
        }

        public void pressButton(int indice)
        {
                ActionEvent e = new ActionEvent(this, indice, "");

                for (ActionListener actionListener : listeners)
                {
                        actionListener.actionPerformed(e);
                }
        }
}
