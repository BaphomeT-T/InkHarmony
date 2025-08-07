package BusinessLogic;

/**
 * Clase que representa el estado "Pausado" de un reproductor MP3.
 * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
 * cuando el reproductor se encuentra pausado temporalmente.
 *
 * En este estado se puede reanudar la reproducción, detener o cambiar de canción.
 *
 * Forma parte del patrón de diseño **State**.
 */

/**
 * Estado que representa cuando el reproductor está pausado.
 * Actualizado para usar JavaFX MediaPlayer.
 */
public class EstadoPausado implements EstadoReproductor {

    private ReproductorMP3 reproductor;

    public EstadoPausado(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    @Override
    public void reproducir() {
        // Desde pausado, reproducir debería reanudar
        reanudar();
    }

    @Override
    public void pausar() {
        System.out.println("Ya está pausado");
    }

    @Override
    public void reanudar() {
        reproductor.reanudarMediaPlayer();
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    @Override
    public void detener() {
        reproductor.detenerMediaPlayer();
        reproductor.setEstado(new EstadoDetenido(reproductor));
    }

    @Override
    public void siguiente() {
        reproductor.getPlaylist().siguiente();
        reproductor.iniciarReproduccionDesde(0);
    }

    @Override
    public void anterior() {
        reproductor.getPlaylist().anterior();
        reproductor.iniciarReproduccionDesde(0);
    }
}
