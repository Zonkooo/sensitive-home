/*
 * source : http://java.sun.com/products/java-media/sound/samples/JavaSoundDemo/
 */

import java.io.*;
import javax.sound.sampled.*;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/** 
 * Reads data from the input channel and writes to the output stream
 */
class Capture
{
    /**
     * méthode de lancement (tests)
     * peut prendre en paramètres des instructions sur le format de capture
     * 
     * @param args frequence et taille des echantillons et nombre de cannaux, ou rien du tout
     */
    @SuppressWarnings("empty-statement") //pour le catch de l'exception
    public static void main(String args[])
    {
        Capture capture = null;        
        if(args.length == 3) //chargement des paramètres depuis la ligne de commande
            capture = new Capture(Float.parseFloat(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        else //chargement des params par defaut
        {
            System.out.println("arguments ignorés");
            capture = new Capture();
        }
        
        int[] ad = capture.ecoute(1000); //capture pendant 1 seconde
        for (int i = 0; i < ad.length; i++)
        {
            System.out.println(ad[i]);
        }
    }
    
    
    private TargetDataLine line;
    
    //format de capture
    private AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED; //ALAW - ULAW - PCM_SIGNED - PCM-UNSIGNED
    private boolean bigEndian = true;
    private float sampleRate; //44100 - 22050 - 16000 - 11025 - 8000
    private int sampleSizeInBits; // 16 - 8
    private int channels; //2 = stereo - 1 = mono
    
    private int[] audioData; //resultat de la capture

    /**
     * Ce constructeur permet de spécifier 
     * les paramètres à utiliser pour la capture.
     * Par défaut, on est en 44,1 kHz, 16 bits, stereo
     * 
     * @param sampleRate 44100 - 22050 - 16000 - 11025 - 8000
     * @param sampleSizeInBits 16 - 8
     * @param channels 2 = stereo - 1 = mono
     */
    public Capture(float sampleRate, int sampleSizeInBits, int channels)
    {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
    }

    /**
     * Constructeur par defaut :
     * 44,1 kHz, 16 bits, stereo
     */
    public Capture()
    {
        this.sampleRate = 44100; //44100 - 22050 - 16000 - 11025 - 8000
        this.sampleSizeInBits = 16; // 16 - 8
        this.channels = 1; //2 = stereo - 1 = mono
    }
    
    private boolean elapsed = false; //utilisé par le timer
    
    /**
     * écoute le micro pendant millisec millisecondes
     * 
     * @param millisec durée d'écoute
     * @return données écoutées
     */
    public int[] ecoute(int millisec)
    {
        // define the required attributes for our line
        AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, (sampleSizeInBits / 8) * channels, sampleRate, bigEndian);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        //make sure a compatible line is supported.
        if (!AudioSystem.isLineSupported(info))
        {
            System.out.println("Line matching " + info + " not supported.");
            return null;
        }

        // get and open the target data line for capture.
        try
        {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        }
        catch (LineUnavailableException ex)
        {
            System.out.println("Unable to open the line: " + ex);
            return null;
        }
        catch (Exception ex)
        {
            System.out.println(ex.toString());
            return null;
        }

        //enregistrement
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInBytes = (line.getBufferSize() / 8)* frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes]; //tampon de lecture

        Timer timer = new Timer(millisec, new ActionListener()
        {
            public void actionPerformed(ActionEvent ae)
            {
                elapsed = true;
            }
        });
        timer.setRepeats(false);
        
        line.start(); //silence, ça tourne
        timer.start();
        
        int numBytesRead;
        while (!elapsed)
        {
            //lecture de l'entrée dans data, en écrasant les données précédentes
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1)
            {
                break;
            }
            //on utilise out pour stocker temporairement les bytes lus (?)
            out.write(data, 0, numBytesRead);
        }

        // we reached the end of the stream.  stop and close the line.
        line.stop();
        line.close();
        line = null;

        // stop and close the output stream
        try
        {
            out.flush();
            out.close();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        //on recharge tout le contenu de out dans un tableau de bytes
        //ce qui nous permet de récupérer tout ce qu'on a mis dedans pendant le scan
        //et on le traite pour extraire les données
        audioData = traitementBytes(out.toByteArray(), sampleSizeInBits);
        return audioData;
    }
    
    /**
     * Convertit le flux de bytes reçu en un tableau d'int
     * contenant les bonnes valeurs des samples
     * (si on écoute en 16 bits, il y a 2 bytes par sample
     * et il faut les concatener pour avoir la bonne valeur)
     * 
     * /!\ l'encodage doit être PCMsigned et BigEndian
     * 
     * @param audioBytes flux reçu
     * @param sampleSize taille en bits d'un sample (8 ou 16)
     * @return valeurs des samples
     */
    private int[] traitementBytes(byte[] audioBytes, int sampleSize)
    {
        //on suppose qu'on est en PCMsigned, BigEndian
        int[] intData = null;
        int nbSamples;
        if (sampleSize == 16)
        {
            nbSamples = audioBytes.length / 2;
            intData = new int[nbSamples];
            for (int i = 0; i < nbSamples; i++)
            {
                int MSB = (int) audioBytes[2 * i];
                int LSB = (int) audioBytes[2 * i + 1];
                intData[i] = MSB << 8 | (255 & LSB);
            }
        }
        else if (sampleSize == 8)
        {
            nbSamples = audioBytes.length;
            intData = new int[nbSamples];

            //si on est en 8 bits, les bytes contiennent déjà les données
            //il suffit de tout recopier dans un tableau de int
            for (int i = 0; i < nbSamples; i++)
                intData[i] = audioBytes[i];
        }
        
        return intData;
    }
} // End class Capture
