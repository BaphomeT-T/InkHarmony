package BusinessLogic;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase utilitaria para controlar el volumen del sistema.
 * Utiliza la API de Java Sound para manipular los controles de audio.
 */
public class VolumeController {
    
    private static VolumeController instance;
    private FloatControl volumeControl;
    private boolean muted = false;
    private float previousVolume = 0.5f;
    
    private VolumeController() {
        initializeVolumeControl();
    }
    
    public static VolumeController getInstance() {
        if (instance == null) {
            instance = new VolumeController();
        }
        return instance;
    }
    
    /**
     * Inicializa el control de volumen del sistema
     */
    private void initializeVolumeControl() {
        try {
            // Intentar obtener el mixer de salida por defecto
            AudioFormat format = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            
            // Buscar un mixer que soporte control de volumen
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();
            for (Mixer.Info mixerInfo : mixers) {
                try {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    Line.Info[] lineInfos = mixer.getTargetLineInfo();
                    
                    for (Line.Info lineInfo : lineInfos) {
                        if (lineInfo instanceof Port.Info) {
                            Port.Info portInfo = (Port.Info) lineInfo;
                            if (portInfo.getLineClass().equals(Port.class)) {
                                Port port = (Port) mixer.getLine(portInfo);
                                port.open();
                                
                                if (port.isControlSupported(FloatControl.Type.VOLUME)) {
                                    volumeControl = (FloatControl) port.getControl(FloatControl.Type.VOLUME);
                                    System.out.println("Control de volumen inicializado correctamente");
                                    return;
                                } else if (port.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                                    volumeControl = (FloatControl) port.getControl(FloatControl.Type.MASTER_GAIN);
                                    System.out.println("Control de ganancia principal inicializado");
                                    return;
                                }
                                port.close();
                            }
                        }
                    }
                } catch (Exception e) {
                    // Continuar probando otros mixers
                }
            }
            
            System.out.println("No se pudo encontrar control de volumen del sistema");
            
        } catch (Exception e) {
            System.out.println("Error al inicializar control de volumen: " + e.getMessage());
        }
    }
    
    /**
     * Establece el volumen del sistema
     * @param volume Volumen entre 0.0 y 1.0
     */
    public void setVolume(float volume) {
        if (volumeControl != null) {
            try {
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float scaledVolume = min + (max - min) * volume;
                volumeControl.setValue(scaledVolume);
                
                if (volume > 0) {
                    previousVolume = volume;
                    muted = false;
                } else {
                    muted = true;
                }
                
                System.out.println("Volumen del sistema establecido a: " + Math.round(volume * 100) + "%");
            } catch (Exception e) {
                System.out.println("Error al establecer volumen: " + e.getMessage());
            }
        } else {
            System.out.println("Control de volumen no disponible");
        }
    }
    
    /**
     * Obtiene el volumen actual del sistema
     * @return Volumen entre 0.0 y 1.0
     */
    public float getVolume() {
        if (volumeControl != null) {
            try {
                float min = volumeControl.getMinimum();
                float max = volumeControl.getMaximum();
                float current = volumeControl.getValue();
                return (current - min) / (max - min);
            } catch (Exception e) {
                System.out.println("Error al obtener volumen: " + e.getMessage());
            }
        }
        return 0.5f; // Valor por defecto
    }
    
    /**
     * Silencia o restaura el audio
     */
    public void toggleMute() {
        if (muted) {
            setVolume(previousVolume);
        } else {
            previousVolume = getVolume();
            setVolume(0.0f);
        }
    }
    
    /**
     * Verifica si el audio está silenciado
     */
    public boolean isMuted() {
        return muted;
    }
    
    /**
     * Verifica si el control de volumen está disponible
     */
    public boolean isAvailable() {
        return volumeControl != null;
    }
}
