/*
 * source : http://java.sun.com/products/java-media/sound/samples/JavaSoundDemo/
 */

import java.io.*;
import javax.sound.sampled.*;

/** 
 * Reads data from the input channel and writes to the output stream
 */
class Capture implements Runnable
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
        
        capture.start();
        try { Thread.sleep(1000); } //enregistrement pendant 1 seconde
        catch (InterruptedException ie) {;} //ceci n'arrive jamais
        capture.stop();
    }
    
    TargetDataLine line;
    Thread thread;
    String errStr = null;
    
    //format de capture
    AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED; //ALAW - ULAW - PCM_SIGNED - PCM-UNSIGNED
    boolean bigEndian = true;
    float sampleRate; //44100 - 22050 - 16000 - 11025 - 8000
    int sampleSizeInBits; // 16 - 8
    int channels; //2 = stereo - 1 = mono

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
    
    /**
     * lance un thread qui écoute le micro
     */
    public void start()
    {
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start(); //lance la méthode run
    }

    /**
     * arrête l'écoute du micro
     */
    public void stop()
    {
        thread = null;
    }

    /**
     * méthode d'arrêt propre pour les erreurs
     * 
     * @param message message d'erreur
     */
    private void shutDown(String message)
    {
        if ((errStr = message) != null && thread != null)
        {
            thread = null;
            System.err.println(errStr);
        }
    }

    /**
     * implémentation de Runnable
     * cette méthode est lancée quand 
     * un thread initialisé avec this démarre (thread.start())
     */
    public void run()
    {
        // define the required attributes for our line
        AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, (sampleSizeInBits / 8) * channels, sampleRate, bigEndian);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        //make sure a compatible line is supported.
        if (!AudioSystem.isLineSupported(info))
        {
            shutDown("Line matching " + info + " not supported.");
            return;
        }

        // get and open the target data line for capture.
        try
        {
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format, line.getBufferSize());
        }
        catch (LineUnavailableException ex)
        {
            shutDown("Unable to open the line: " + ex);
            return;
        }
        catch (Exception ex)
        {
            shutDown(ex.toString());
            return;
        }

        //enregistrement
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInBytes = (line.getBufferSize() / 8)* frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes]; //tampon de lecture

        line.start(); //silence, ça tourne

        int numBytesRead;
        while (thread != null)
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
        int[] audioData = traitementBytes(out.toByteArray(), sampleSizeInBits);
        
        for (int i = 0; i < audioData.length; i++)
        {
            System.out.println(audioData[i]);
        }
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
        int[] audioData = null;
        int nbSamples;
        if (sampleSize == 16)
        {
            nbSamples = audioBytes.length / 2;
            audioData = new int[nbSamples];
            for (int i = 0; i < nbSamples; i++)
            {
                int MSB = (int) audioBytes[2 * i];
                int LSB = (int) audioBytes[2 * i + 1];
                audioData[i] = MSB << 8 | (255 & LSB);
            }
        }
        else if (sampleSize == 8)
        {
            nbSamples = audioBytes.length;
            audioData = new int[nbSamples];

            //si on est en 8 bits, les bytes contiennent déjà les données
            //il suffit de tout recopier dans un tableau de int
            for (int i = 0; i < nbSamples; i++)
                audioData[i] = audioBytes[i];
        }
        
        return audioData;
    }
} // End class Capture
