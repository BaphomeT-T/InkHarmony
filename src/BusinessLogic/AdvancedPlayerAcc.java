// Importación de clases necesarias para trabajar con streams de entrada
import java.io.InputStream;

// Importaciones de clases de la librería JLayer para decodificación y reproducción de audio MP3
import javazoom.jl.decoder.Bitstream;          // Permite leer los frames del archivo MP3
import javazoom.jl.decoder.BitstreamException; // Excepción relacionada con errores al leer el stream
import javazoom.jl.decoder.Decoder;            // Decodifica un frame de audio comprimido
import javazoom.jl.decoder.Header;             // Representa la cabecera de un frame MP3
import javazoom.jl.decoder.JavaLayerException; // Excepción general de la librería JLayer
import javazoom.jl.decoder.SampleBuffer;       // Buffer con los datos PCM del frame decodificado
import javazoom.jl.player.AudioDevice;         // Representa el dispositivo de salida de audio
import javazoom.jl.player.FactoryRegistry;     // Permite obtener instancias de dispositivos de audio
import javazoom.jl.player.advanced.AdvancedPlayer; // Clase base que permite reproducción avanzada
import javazoom.jl.player.advanced.PlaybackEvent;  // Representa eventos como STARTED o STOPPED
import javazoom.jl.player.advanced.PlaybackListener; // Oyente que reacciona a eventos de reproducción

/**
 * Clase AdvancedPlayerAcc que extiende el reproductor AdvancedPlayer de JLayer.
 * Añade funciones adicionales como reproducción desde un frame específico,
 * pausa y reanudación desde la última posición.
 */
public class AdvancedPlayerAcc extends AdvancedPlayer {

    // Maneja la lectura de los datos MP3 como flujo de bits
    private Bitstream bitstream;

    // Decodificador de audio MP3 a datos PCM (sin comprimir)
    private Decoder decoder;

    // Dispositivo que emite el sonido (ej. parlantes del sistema)
    private AudioDevice audio;

    // Bandera para indicar si el reproductor fue cerrado
    private boolean closed = false;

    // Bandera para indicar si la reproducción terminó correctamente
    private boolean complete = false;

    // Último frame reproducido antes de cerrar (sirve para reanudar)
    private int lastPosition = 0;

    // Contador que registra cuántos frames se han reproducido
    private int currentFrame = 0;

    // Listener que escucha los eventos de reproducción (inicio, fin, etc.)
    private PlaybackListener listener;

    /**
     * Constructor principal. Recibe el stream del archivo MP3 y
     * utiliza el dispositivo de audio predeterminado del sistema.
     */
    public AdvancedPlayerAcc(InputStream stream) throws JavaLayerException {
        this(stream, null);
    }

    /**
     * Constructor que permite especificar un dispositivo de audio.
     * Si se pasa null, se usa el dispositivo de audio predeterminado.
     */
    public AdvancedPlayerAcc(InputStream stream, AudioDevice device) throws JavaLayerException {
        super(stream, device);
        bitstream = new Bitstream(stream);
        audio = (device != null) ? device : FactoryRegistry.systemRegistry().createAudioDevice();
        audio.open(decoder = new Decoder()); // Se abre el dispositivo con un decodificador
    }

    /**
     * Inicia la reproducción del archivo MP3 desde el inicio hasta el final.
     */
    public void play() throws JavaLayerException {
        play(Integer.MAX_VALUE); // Se pasa un número muy grande para reproducir todo
    }

    /**
     * Retorna el número del último frame reproducido.
     */
    public int getLastPosition() {
        return currentFrame;
    }

    /**
     * Reproduce la cantidad indicada de frames MP3 desde la posición actual.
     * @param frames Número de frames a reproducir
     * @return true si se pudo reproducir, false si hubo error o fin de archivo
     */
    public boolean play(int frames) throws JavaLayerException {
        boolean ret = true;

        // Notificar que la reproducción ha comenzado
        if (listener != null)
            listener.playbackStarted(createEvent(PlaybackEvent.STARTED));

        // Bucle para reproducir el número de frames solicitado
        while (frames-- > 0 && ret) {
            ret = decodeFrame();
        }

        // Finalizar y limpiar recursos
        if (audio != null) {
            audio.flush(); // Limpiar buffers
            synchronized (this) {
                complete = (!closed); // Si no se cerró manualmente, se marca como completado
                close();              // Cerrar el reproductor
            }
            // Notificar que la reproducción ha terminado
            if (listener != null)
                listener.playbackFinished(createEvent(audio, PlaybackEvent.STOPPED));
        }

        return ret;
    }

    /**
     * Cierra el reproductor y libera los recursos del dispositivo de audio y bitstream.
     */
    @Override
    public synchronized void close() {
        if (audio != null) {
            closed = true;                  // Se marca como cerrado
            audio = null;                   // Se libera el dispositivo de audio
            lastPosition = currentFrame;    // Se guarda la última posición reproducida
            audio.close();                  // Se cierra el dispositivo
            try {
                bitstream.close();          // Se cierra el stream de lectura
            } catch (BitstreamException ex) {
                // Se ignora la excepción en este caso
            }
        }
    }

    /**
     * Decodifica y reproduce un solo frame del archivo MP3.
     * @return true si se decodificó exitosamente, false si no hay más frames.
     */
    @Override
    protected boolean decodeFrame() throws JavaLayerException {
        try {
            if (audio == null) return false;

            // Leer el encabezado del siguiente frame
            Header h = bitstream.readFrame();
            if (h == null) return false; // Si no hay más frames, fin de archivo

            // Decodificar el frame y obtener los datos PCM
            SampleBuffer output = (SampleBuffer) decoder.decodeFrame(h, bitstream);

            // Escribir los datos decodificados al dispositivo de salida
            synchronized (this) {
                if (audio != null) {
                    audio.write(output.getBuffer(), 0, output.getBufferLength());
                }
            }

            bitstream.closeFrame(); // Se cierra el frame actual
            currentFrame++;         // Se incrementa el contador de frames
        } catch (RuntimeException ex) {
            throw new JavaLayerException("Exception decoding audio frame", ex);
        }
        return true;
    }

    /**
     * Salta un frame sin reproducirlo. Se usa para avanzar la posición inicial.
     * @return true si el frame fue leído y saltado correctamente.
     */
    protected boolean skipFrame() throws JavaLayerException {
        Header h = bitstream.readFrame(); // Leer el frame
        if (h == null) return false;      // Si no hay más, se retorna false
        bitstream.closeFrame();           // Cerrar el frame leído
        return true;
    }

    /**
     * Reproduce desde un frame específico (start) hasta otro (end, sin incluir).
     * @param start Frame inicial
     * @param end Frame final (no incluido)
     * @return true si la reproducción fue exitosa
     */
    public boolean play(final int start, final int end) throws JavaLayerException {
        boolean ret = true;
        currentFrame = start;

        // Saltar todos los frames hasta llegar a 'start'
        int offset = start;
        while (offset-- > 0 && ret) {
            ret = skipFrame();
        }

        // Reproducir los frames entre start y end
        return play(end - start);
    }

    /**
     * Crea un evento de reproducción con el tipo especificado.
     * @param id Tipo de evento (STARTED, STOPPED)
     * @return Objeto PlaybackEvent asociado
     */
    private PlaybackEvent createEvent(int id) {
        return createEvent(audio, id);
    }

    /**
     * Crea un PlaybackEvent con el dispositivo de audio y tipo de evento.
     * @param dev Dispositivo de audio asociado
     * @param id Tipo del evento (STARTED o STOPPED)
     * @return Evento generado
     */
    private PlaybackEvent createEvent(AudioDevice dev, int id) {
        return new PlaybackEvent(this, id, dev.getPosition());
    }

    /**
     * Establece el listener que recibirá notificaciones de reproducción.
     * @param listener Listener a asignar
     */
    public void setPlayBackListener(PlaybackListener listener) {
        this.listener = listener;
    }

    /**
     * Retorna el listener asignado actualmente.
     * @return Listener actual
     */
    public PlaybackListener getPlayBackListener() {
        return listener;
    }

    /**
     * Detiene la reproducción manualmente y lanza el evento de finalización.
     */
    public void stop() {
        if (listener != null)
            listener.playbackFinished(createEvent(PlaybackEvent.STOPPED));
        close(); // Se cierra el reproductor
    }
}
