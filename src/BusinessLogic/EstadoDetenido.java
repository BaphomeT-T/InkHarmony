package BusinessLogic;

/**
 * Clase que representa el estado "Detenido" de un reproductor MP3.
 * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
 * del reproductor cuando no está reproduciendo ni pausado.
 *
 * En este estado se puede iniciar la reproducción o navegar entre canciones.
 *
 * Forma parte del patrón de diseño **State**.
 */

/**
 * Estado que representa cuando el reproductor está detenido.
 * Actualizado para usar JavaFX MediaPlayer.
 */
public class EstadoDetenido implements EstadoReproductor {

    private ReproductorMP3 reproductor;

    public EstadoDetenido(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    @Override
    public void reproducir() {
        reproductor.iniciarReproduccionDesde(0);
    }

    @Override
    public void pausar() {
        System.out.println("No se puede pausar, el reproductor está detenido");
    }

    @Override
    public void reanudar() {
        System.out.println("No se puede reanudar, el reproductor está detenido");
    }

    @Override
    public void detener() {
        System.out.println("Ya está detenido");
    }

    @Override
    public void siguiente() {
        reproductor.getPlaylist().siguiente();
        // En estado detenido, solo cambiar canción sin reproducir automáticamente
        System.out.println("Cambiado a siguiente canción");
    }

    @Override
    public void anterior() {
        reproductor.getPlaylist().anterior();
        // En estado detenido, solo cambiar canción sin reproducir automáticamente
        System.out.println("Cambiado a canción anterior");
    }
}

