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
			for (int j = 0; j < fft[0].length; j++)
				this.fft[i][j] = fft[i][j];
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
		int[][] audio;
		double[][] audioDouble1 = new double[2][];
		double[][] audioDouble2 = new double[2][];
		
		System.out.println("tapez !");
		audio = capture.getTap();
		audioDouble1[0] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[0])));
		if(audio.length == 2)//stereo
			audioDouble1[1] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[1])));

		System.out.println("encore !");
		audio = capture.getTap();
		audioDouble2[0] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[0])));
		if(audio.length == 2)//stereo
			audioDouble2[1] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[1])));

		//mix 2 coups
		for(int i = 0; i < audioDouble1.length; i++)
			for(int j = 0; j < audioDouble1[0].length; j++)
				audioDouble1[i][j] = (audioDouble1[i][j] + audioDouble2[i][j])/2;
			
		this.fft = audioDouble1;
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
		double pas = 44100/fft[0].length;
		int start = (int)(100/pas);
		int stop = (int)(10000/pas);
		
		for (int i = 0; i < fft.length; i++)
			for (int j = start; j < stop; j++) 
				correlation += fft[i][j]*fftTap[i][j];

        return correlation;
    }
	
	public static void traiterSignal(int[][] audio, ArrayList<Bouton> btns)
	{
		double[][] audioDouble = new double[2][];
		
		//Outils.reverse(audio[0]);
		audioDouble[0] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[0])));
		if(audio.length == 2)//stereo
		{	
			//Outils.reverse(audio[1]);
			audioDouble[1] = Outils.normalize(FFT.fftMag(FFT.zeroPadding(audio[1])));
		}

//			for (int i = 0; i < audioDouble1[0].length; i++)
//				System.out.println(audioDouble1[0][i]);

		double cor, max = -1;
		int indice = -1;

		for(int i = 0; i < btns.size(); i++)
		{
			cor = btns.get(i).correlation(audioDouble);
			System.out.println(cor);
			if(cor > max)
			{
				max = cor;
				indice = i;
			}
		}
		
		System.out.println("bouton " + (indice + 1));
		//pressButton();
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
	
	public void pressButton()
	{
		ActionEvent e = new ActionEvent(this, 0, "");
		
		for (ActionListener actionListener : listeners)
			actionListener.actionPerformed(e);
	}
}
