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
 * Clase AdvancedPlayerAcc que extiende el reproductor de JLayer para añadir
 * funcionalidades adicionales como reproducir desde un frame específico,
 * pausar y reanudar desde la última posición.
 *
 * @author Grupo B
 * @version 1.0
 * @since 25-07-2025
 */
public class AdvancedPlayerAcc extends AdvancedPlayer {

    /** Objeto para procesar el flujo de datos MP3 */
    private Bitstream bitstream;

    /** Objeto decodificador de audio */
    private Decoder decoder;

    /** Dispositivo de salida de audio */
    private AudioDevice audio;

    /** Indicador de cierre del reproductor */
    private boolean closed = false;

    /** Indicador de finalización de la reproducción */
    private boolean complete = false;

    /** Frame donde terminó la última reproducción */
    private int lastPosition = 0;

    /** Contador del frame actual reproducido */
    private int currentFrame = 0;

    /** Oyente para eventos de reproducción */
    private PlaybackListener listener;

    /**
     * Constructor que recibe un flujo de entrada MP3 y crea el reproductor
     * con el dispositivo de audio predeterminado del sistema.
     *
     * @param stream Flujo de entrada del archivo MP3.
     * @throws JavaLayerException Si ocurre un error en la inicialización.
     */
    public AdvancedPlayerAcc(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    /**
     * Constructor que permite especificar un dispositivo de audio.
     *
     * @param stream Flujo de entrada del archivo MP3.
     * @param device Dispositivo de audio a utilizar (puede ser null).
     * @throws JavaLayerException Si ocurre un error en la inicialización.
     */
    public AdvancedPlayerAcc(InputStream stream, AudioDevice device) throws JavaLayerException {
        super(stream, device);
        bitstream = new Bitstream(stream);
        audio = (device != null) ? device : FactoryRegistry.systemRegistry().createAudioDevice();
        audio.open(decoder = new Decoder());
    }

    /**
     * Inicia la reproducción desde el inicio hasta el final del archivo.
     *
     * @throws JavaLayerException Si ocurre un error durante la reproducción.
     */
    public void play() throws JavaLayerException {
        play(Integer.MAX_VALUE);
    }

    /**
     * Retorna el último frame reproducido.
     *
     * @return Número del último frame reproducido.
     */
    public int getLastPosition() {
        return currentFrame;
    }

    /**
     * Reproduce la cantidad especificada de frames desde la posición actual.
     *
     * @param frames Número de frames a reproducir.
     * @return true si se reprodujo correctamente, false si hubo errores o fin del archivo.
     * @throws JavaLayerException Si ocurre un error durante la reproducción.
     */
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

    /**
     * Cierra el reproductor y libera los recursos de audio y bitstream.
     */
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
            }
        }
    }

    /**
     * Decodifica y reproduce un solo frame de audio.
     *
     * @return true si el frame se decodificó exitosamente, false si no hay más frames.
     * @throws JavaLayerException Si ocurre un error al decodificar.
     */
    @Override
    protected boolean decodeFrame() throws JavaLayerException {
        try {
            if (audio == null) return false;

            Header h = bitstream.readFrame();
            if (h == null) return false;

            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

            synchronized (this) {
                if (audio != null) {
                    audio.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }

            bitstream.closeFrame();
            currentFrame++;
        } catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }

    /**
     * Salta (sin reproducir) un frame del archivo MP3.
     *
     * @return true si el frame fue saltado correctamente.
     * @throws JavaLayerException Si ocurre un error al leer el frame.
     */
    protected boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        if (h == null) return false;
        bitstream.closeFrame();
        return true;
    }

    /**
     * Reproduce los frames desde una posición inicial hasta una final.
     *
     * @param start Frame de inicio.
     * @param end Frame final (no incluido).
     * @return true si la reproducción fue exitosa.
     * @throws JavaLayerException Si ocurre un error.
     */
    public boolean play(final int start, final int end) throws JavaLayerException {
        boolean ret = true;
        currentFrame = start;
        int offset = start;
        while (offset-- > 0 && ret) ret = skipFrame();
        return play(end - start);
    }

    /**
     * Crea un evento PlaybackEvent con el ID dado.
     *
     * @param id Tipo del evento (STARTED o STOPPED).
     * @return Objeto PlaybackEvent asociado.
     */
    private PlaybackEvent createEvent(int id) {
        return createEvent(audio, id);
    }

    /**
     * Crea un PlaybackEvent con dispositivo y tipo de evento.
     *
     * @param dev Dispositivo de audio asociado.
     * @param id Tipo del evento.
     * @return Evento de reproducción.
     */
    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    /**
     * Establece un objeto listener que será notificado durante la reproducción.
     *
     * @param listener Oyente a asignar.
     */
    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    /**
     * Obtiene el listener asignado.
     *
     * @return Objeto PlaybackListener actual.
     */
    public PlaybackListener getPlayBackListener() {
        return listener;
    }

    /**
     * Detiene la reproducción actual y lanza evento de finalización.
     */
    public void stop() {
        if (listener != null)
            listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
        close();
    }
}
