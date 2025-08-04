package BusinessLogic.utilities;

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
 * Clase que extiende {@link AdvancedPlayer} para permitir:
 * <ul>
 *   <li>Reproducción desde un frame específico</li>
 *   <li>Pausar y reanudar desde la última posición exacta</li>
 *   <li>Escuchar eventos de reproducción personalizados</li>
 * </ul>
 *
 * Utiliza internamente {@link Bitstream} y {@link Decoder} para manejar los datos MP3,
 * y controla el estado de reproducción (como posición y frames).
 *
 * Es utilizada por el reproductor MP3 como motor de reproducción avanzado.
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

    /**
     * Constructor que crea un reproductor con un {@link InputStream} de entrada.
     * Usa el dispositivo de audio predeterminado del sistema.
     *
     * @param stream Flujo de entrada MP3
     * @throws JavaLayerException si ocurre un error al crear el dispositivo de audio
     */
    public AdvancedPlayerAcc(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    /**
     * Constructor que permite especificar un dispositivo de audio.
     *
     * @param stream Flujo de entrada MP3
     * @param device Dispositivo de audio personalizado (puede ser null)
     * @throws JavaLayerException si ocurre un error al inicializar la reproducción
     */
    public AdvancedPlayerAcc(InputStream stream, AudioDevice device) throws JavaLayerException {
        super(stream, device);
        bitstream = new Bitstream(stream);
        if (device != null) audio = device;
        else audio = FactoryRegistry.systemRegistry().createAudioDevice();
        audio.open(decoder = new Decoder());
    }

    /**
     * Reproduce la pista completa desde el frame actual hasta el final.
     *
     * @throws JavaLayerException si ocurre un error durante la reproducción
     */
    public void play() throws JavaLayerException {
        play(Integer.MAX_VALUE);
    }

    /**
     * Devuelve el número del último frame reproducido antes de pausar o detener.
     *
     * @return Frame actual
     */
    public int getLastPosition() {
        return currentFrame;
    }

    /**
     * Reproduce un número específico de frames a partir de la posición actual.
     *
     * @param frames Número de frames a reproducir
     * @return true si la reproducción fue exitosa
     * @throws JavaLayerException si ocurre un error durante la reproducción
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
     * Cierra la reproducción y libera recursos. Guarda la posición actual.
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
                // Error ignorado
            }
        }
    }

    /**
     * Decodifica un solo frame de audio y lo envía al dispositivo de audio.
     *
     * @return true si se decodificó correctamente
     * @throws JavaLayerException si ocurre un error de decodificación
     */
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

    /**
     * Salta un solo frame sin reproducirlo.
     *
     * @return true si se pudo saltar el frame
     * @throws JavaLayerException si ocurre un error
     */
    protected boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame();
        if (h == null) return false;
        bitstream.closeFrame();
        return true;
    }

    /**
     * Reproduce un rango específico de frames.
     *
     * @param start Frame inicial
     * @param end   Frame final
     * @return true si la reproducción fue exitosa
     * @throws JavaLayerException si ocurre un error durante la reproducción
     */
    public boolean play(final int start, final int end) throws JavaLayerException {
        boolean ret = true;
        currentFrame = start;
        int offset = start;
        while (offset-- > 0 && ret) ret = skipFrame();
        return play(end - start);
    }

    /**
     * Crea un evento de reproducción con el identificador proporcionado.
     *
     * @param id Identificador del evento (STARTED o STOPPED)
     * @return Evento de reproducción
     */
    private PlaybackEvent createEvent(int id) {
        return createEvent(audio, id);
    }

    /**
     * Crea un evento de reproducción con un dispositivo de audio específico.
     *
     * @param dev Dispositivo de audio
     * @param id  Identificador del evento
     * @return Evento de reproducción
     */
    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    /**
     * Establece un listener para los eventos de reproducción (inicio, pausa, fin).
     *
     * @param listener Listener de reproducción
     */
    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    /**
     * Devuelve el listener actualmente registrado para los eventos de reproducción.
     *
     * @return Listener de reproducción
     */
    public PlaybackListener getPlayBackListener() {
        return listener;
    }

    /**
     * Detiene la reproducción y dispara un evento de finalización si hay listener.
     */
    public void stop() {
        if (listener != null)
            listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
        close();
    }
}
