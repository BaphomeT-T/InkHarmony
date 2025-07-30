package BusinessLogic;

public class EstadoPausado implements EstadoReproductor {
    private final ReproductorMP3 reproductor;

    public EstadoPausado(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    public void reproducir() {
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    public void pausar() {
        System.out.println("Ya está pausado.");
    }

    public void reanudar() {
        reproductor.iniciarReproduccionDesde(reproductor.getFrameActual());
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
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

