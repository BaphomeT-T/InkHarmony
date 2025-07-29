package BusinessLogic;

import DataAccessComponent.DTO.CancionDTO;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ElementoCancion implements ComponentePlaylist {
    private CancionDTO cancion;
    private Date fechaAgregado;
    private int orden;

    public ElementoCancion(CancionDTO cancion) {
        this.cancion = cancion;
        this.fechaAgregado = new Date();
        this.orden = 0;
    }

    public ElementoCancion(CancionDTO cancion, int orden) {
        this.cancion = cancion;
        this.fechaAgregado = new Date();
        this.orden = orden;
    }

    public CancionDTO getCancion() { return cancion; }
    public Date getFechaAgregado() { return fechaAgregado; }
    public int getOrden() { return orden; }
    public void setOrden(int orden) { this.orden = orden; }

    // Métodos de conveniencia para acceder a información de la canción
    public String getTitulo() { 
        return cancion != null ? cancion.getTitulo() : ""; 
    }
    
    public String getArtista() { 
        if (cancion != null && cancion.getArtistas() != null && !cancion.getArtistas().isEmpty()) {
            return cancion.getArtistas().get(0).getNombre();
        }
        return "";
    }

    @Override
    public void mostrarInformacion() {
        System.out.println("Canción: " + cancion.getTitulo() +
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