package BusinessLogic;

import java.util.*;


public class ElementoCancion implements ComponentePlaylist {
    private Cancion cancion;
    private Date fechaAgregado;
    private int orden;

    public ElementoCancion(Cancion cancion) {
        this.cancion = cancion;
        this.fechaAgregado = new Date();
        this.orden = 0;
    }

    public ElementoCancion(Cancion cancion, int orden) {
        this.cancion = cancion;
        this.fechaAgregado = new Date();
        this.orden = orden;
    }

    public Cancion getCancion() { return cancion; }
    public Date getFechaAgregado() { return fechaAgregado; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    @Override
    public void mostrarInformacion() {
        System.out.println("Canción: " + cancion.getTituloCancion() +
                " - Duración: " + cancion.getDuracion() + " min" +
                " - Orden: " + orden);
    }

    @Override
    public double obtenerDuracion() {
        return cancion.getDuracion();
    }

    @Override
    public void agregar(ComponentePlaylist componente) {
        // ElementoCancion es una hoja, no puede contener otros componentes
        throw new UnsupportedOperationException("ElementoCancion no puede contener otros componentes");
    }

    @Override
    public void eliminar(ComponentePlaylist componente) {
        // ElementoCancion es una hoja, no puede contener otros componentes
        throw new UnsupportedOperationException("ElementoCancion no puede eliminar componentes");
    }

    @Override
    public List<ComponentePlaylist> getComponentes() {
        // ElementoCancion es una hoja, retorna lista vacía
        return new ArrayList<>();
    }
}