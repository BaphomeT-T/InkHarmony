    package BusinessLogic;

    /**
     * Clase que representa el estado "Reproduciendo" de un reproductor MP3.
     * Implementa la interfaz {@link EstadoReproductor} y define el comportamiento
     * del reproductor cuando está reproduciendo una canción.
     *
     * En este estado se puede pausar, detener o cambiar de canción, pero no se puede
     * iniciar la reproducción ni reanudarla (ya está en curso).
     *
     * Forma parte del patrón de diseño **State**.
     */
    /**
     * Estado que representa cuando el reproductor está reproduciendo una canción.
     * Actualizado para usar JavaFX MediaPlayer.
     */
    public class EstadoReproduciendo implements EstadoReproductor {

        private ReproductorMP3 reproductor;

        public EstadoReproduciendo(ReproductorMP3 reproductor) {
            this.reproductor = reproductor;
        }

        @Override
        public void reproducir() {
            System.out.println("Ya se está reproduciendo");
        }

        @Override
        public void pausar() {
            reproductor.pausarMediaPlayer();
            reproductor.setEstado(new EstadoPausado(reproductor));
        }

        @Override
        public void reanudar() {
            System.out.println("Ya se está reproduciendo");
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
            reproductor.notificarCambioCancion();
        }

        @Override
        public void anterior() {
            reproductor.getPlaylist().anterior();
            reproductor.iniciarReproduccionDesde(0);
            reproductor.notificarCambioCancion();
        }
    }
