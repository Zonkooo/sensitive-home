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
	ArrayList<ActionListener> listeners;
	
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
