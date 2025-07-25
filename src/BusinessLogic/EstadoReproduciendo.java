public class EstadoReproduciendo implements EstadoReproductor {
    private final ReproductorMP3 reproductor;

    public EstadoReproduciendo(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    public void reproducir() {
        System.out.println("Ya está reproduciendo.");
    }

    public void pausar() {
        reproductor.setFrameActual(reproductor.getPlayer().getLastPosition());
        reproductor.cerrarReproduccion();
        reproductor.setEstado(new EstadoPausado(reproductor));
        System.out.println("Pausado en frame: " + reproductor.getFrameActual());
    }

    public void reanudar() {
        System.out.println("Ya está reproduciendo.");
    }

    public void detener() {
        reproductor.setFrameActual(reproductor.getPlayer().getLastPosition());
        reproductor.cerrarReproduccion();
        reproductor.setEstado(new EstadoDetenido(reproductor));
    }

    public void siguiente() {
        detener();
        reproductor.setIndiceActual((reproductor.getIndiceActual() + 1) % reproductor.getCancionesBytes().size());
        reproductor.reproducir();
    }

    public void anterior() {
        detener();
        int nuevaPos = (reproductor.getIndiceActual() - 1 + reproductor.getCancionesBytes().size()) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(nuevaPos);
        reproductor.reproducir();
    }
}
