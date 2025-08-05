/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Grupo C
Objeto de transferencia de datos (DTO) para transferir datos de playlist entre capas
*/

package DataAccessComponent.DTO;

import BusinessLogic.ComponentePlaylist;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase CancionDTO que representa una canción en el sistema.
 *
 * <p>Contiene información relacionada a una playlist como su id, título, descripcion,
 * referencia al usuario creador, imagen de portada, fecha de creación y las canciones que contiene
 * Esta clase se utiliza para transportar datos entre las capas del sistema sin lógica de negocio.</p>
 *
 * @author Grupo C
 * @version 1.0
 * @since 31-07-2025
 */

public class PlaylistDTO {
    private int idPlaylist;
    private String tituloPlaylist;
    private String descripcion;
    private int idPropietario;
    private byte[] imagenPortada;
    private LocalDateTime fechaCreacion;
    private List<Integer> cancionesIds;

    /**
     * Constructor vacío
     */
    public PlaylistDTO() {}

    /**
     * Constructor completo
     */
    public PlaylistDTO(String tituloPlaylist, String descripcion, int idPropietario,
                       byte[] imagenPortada, List<Integer> cancionesIds) {
        this.tituloPlaylist = tituloPlaylist;
        this.descripcion = descripcion;
        this.idPropietario = idPropietario;
        this.imagenPortada = imagenPortada;
        this.cancionesIds = cancionesIds;
        this.fechaCreacion = LocalDateTime.now();
    }

    /**
     * Getters y Setters
     */
    public int getIdPlaylist() { return idPlaylist; }
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }

    public String getTituloPlaylist() { return tituloPlaylist; }
    public void setTituloPlaylist(String tituloPlaylist) { this.tituloPlaylist = tituloPlaylist; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getIdPropietario() { return idPropietario; }
    public void setIdPropietario(int idPropietario) { this.idPropietario = idPropietario; }

    public byte[] getImagenPortada() { return imagenPortada; }
    public void setImagenPortada(byte[] imagenPortada) { this.imagenPortada = imagenPortada; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public List<Integer> getCancionesIds() { return cancionesIds; }
    public void setCancionesIds(List<Integer> cancionesIds) { this.cancionesIds = cancionesIds; }

    @Override
    public String toString() {
        return "PlaylistDTO{" +
                "idPlaylist=" + idPlaylist +
                ", tituloPlaylist='" + tituloPlaylist + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", idPropietario=" + idPropietario +
                ", fechaCreacion=" + fechaCreacion +
                ", numCanciones=" + (cancionesIds != null ? cancionesIds.size() : 0) +
                '}';
    }
}