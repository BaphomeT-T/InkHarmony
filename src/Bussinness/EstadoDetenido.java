package Bussinness;

public class EstadoDetenido implements EstadoReproductor {
    private final ReproductorMP3 reproductor;

    public EstadoDetenido(ReproductorMP3 reproductor) {
        this.reproductor = reproductor;
    }

    public void reproducir() {
        reproductor.setFrameActual(0);
        reproductor.iniciarReproduccionDesde(0);
        reproductor.setEstado(new EstadoReproduciendo(reproductor));
    }

    public void pausar() {
        System.out.println("No se puede pausar. El reproductor está detenido.");
    }

    public void reanudar() {
        System.out.println("No se puede reanudar. El reproductor está detenido.");
    }

    public void detener() {
        System.out.println("Ya está detenido.");
    }

    public void siguiente() {
        reproductor.setIndiceActual((reproductor.getIndiceActual() + 1) % reproductor.getCancionesBytes().size());
        reproducir();
    }

    public void anterior() {
        int nuevaPos = (reproductor.getIndiceActual() - 1 + reproductor.getCancionesBytes().size()) % reproductor.getCancionesBytes().size();
        reproductor.setIndiceActual(nuevaPos);
        reproducir();
    }
}
