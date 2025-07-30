package Bussinness.utilities;

import java.io.InputStream;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.decoder.SampleBuffer;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

/**
 * Versión extendida de AdvancedPlayer para soportar reproducción desde un frame específico
 * y pausar/reanudar desde la última posición exacta.
 */
public class AdvancedPlayerAcc extends AdvancedPlayer {
    private Bitstream bitstream;
    private Decoder decoder;
    private AudioDevice audio;
    private boolean closed = false;
    private boolean complete = false;
    private int lastPosition = 0;
    private int currentFrame = 0;
    private PlaybackListener listener;

    public AdvancedPlayerAcc(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    public AdvancedPlayerAcc(InputStream stream, AudioDevice device) throws JavaLayerException {
        super(stream, device);
        bitstream = new Bitstream(stream);
        if (device != null) audio = device;
        else audio = FactoryRegistry.systemRegistry().createAudioDevice();
        audio.open(decoder = new Decoder());
    }

    public void play() throws JavaLayerException {
        play(Integer.MAX_VALUE);
    }

    public int getLastPosition() {
        return currentFrame;
    }

    public boolean play(int frames) throws JavaLayerException {
        boolean ret = true;
        if (listener != null)
            listener.playbackStarted(createEvent(PlaybackEvent.STARTED));

        while (frames-- > 0 && ret) {
            ret = decodeFrame();
        }

        AudioDevice out = audio;
        if (out != null) {
            out.flush();
            synchronized (this) {
                complete = (!closed);
                close();
            }
            if (listener != null)
                listener.playbackFinished(createEvent(out, PlaybackEvent.STOPPED));
        }

        return ret;
    }

    @Override
    public synchronized void close() {
        AudioDevice out = audio;
        if (out != null) {
            closed = true;
            audio = null;
            lastPosition = currentFrame;
            out.close();
            try {
                bitstream.close();
            } catch (BitstreamException ex) {
                // Ignorado
            }
        }
    }

    @Override
    protected boolean decodeFrame() throws JavaLayerException {
        try {
            AudioDevice out = audio;
            if (out == null) return false;

            Header h = bitstream.readFrame();
            if (h == null) return false;

            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

            synchronized (this) {
                out = audio;
                if (out != null) {
                    out.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }

            bitstream.closeFrame();
            currentFrame++;
        } catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }

    protected boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        if (h == null) return false;
        bitstream.closeFrame();
        return true;
    }

    public boolean play(final int start, final int end) throws JavaLayerException {
        boolean ret = true;
        currentFrame = start;
        int offset = start;
        while (offset-- > 0 && ret) ret = skipFrame();
        return play(end - start);
    }

    private PlaybackEvent createEvent(int id) {
        return createEvent(audio, id);
    }

    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    public PlaybackListener getPlayBackListener() {
        return listener;
    }

    public void stop() {
        if (listener != null)
            listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
        close();
    }
}
