//import java.util.Vector;
import java.io.*;
import javax.sound.sampled.*;

/** 
 * Reads data from the input channel and writes to the output stream
 */
class Capture implements Runnable
{
    public static void main(String s[])
    {
        Capture capture = new Capture(44100, 16, 1);
        System.out.println("hello");
        capture.start();
        try { Thread.sleep(1000); } //enregistrement pendant 1 seconde
        catch (InterruptedException ie) {;}
        capture.stop();
    }
    
    TargetDataLine line;
    Thread thread;
    String errStr = null;
    double duration = 0.0;
    //format de capture
    AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED; //ALAW - ULAW - PCM_SIGNED - PCM-UNSIGNED
    float sampleRate; //44100 - 22050 - 16000 - 11025 - 8000
    int sampleSizeInBits; // 16 - 8
    int channels; //2 = stereo - 1 = mono
    boolean bigEndian = true;

    /**
     * Ce constructeur permet de spécifier 
     * les paramètres à utiliser pour la capture.
     * Par défaut, on est en 44,1 kHz, 16 bits, stereo
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
        sampleRate = 44100; //44100 - 22050 - 16000 - 11025 - 8000
        sampleSizeInBits = 16; // 16 - 8
        channels = 2; //2 = stereo - 1 = mono
    }
    

    public void start()
    {
        thread = new Thread(this);
        thread.setName("Capture");
        thread.start();
    }

    public void stop()
    {
        thread = null;
    }

    private void shutDown(String message)
    {
        if ((errStr = message) != null && thread != null)
        {
            thread = null;
            //samplingGraph.stop();
            System.err.println(errStr);
        //samplingGraph.repaint();
        }
    }

    public void run()
    {
        duration = 0.0;
        AudioInputStream audioInputStream = null;

        // define the required attributes for our line, 
        // and make sure a compatible line is supported.

        AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED; //ALAW - ULAW - PCM_SIGNED - PCM-UNSIGNED
        float sampleRate = 8000; //44100 - 22050 - 16000 - 11025 - 8000
        int sampleSizeInBits = 16; // 16 - 8
        int channels = 1; //2 = stereo - 1 = mono
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, (sampleSizeInBits / 8) * channels, sampleRate, bigEndian);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

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

        // play back the captured audio data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;

        line.start();

        while (thread != null)
        {
            if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1)
            {
                break;
            }
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

        // load bytes into the audio input stream for playback

        byte audioBytes[] = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

        long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
        duration = milliseconds / 1000.0;

        try
        {
            audioInputStream.reset();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return;
        }

        for (int i = 0; i < audioBytes.length; i++)
        {
            System.out.println(audioBytes[i]);
        }

    //samplingGraph.createWaveForm(audioBytes);
    }
} // End class Capture
