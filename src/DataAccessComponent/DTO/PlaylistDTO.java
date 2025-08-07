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
 * <p>Esta clase encapsula todos los datos necesarios para representar una playlist,
 * incluyendo su información básica, metadatos y las canciones que contiene.
 * Se utiliza para transferir datos entre las diferentes capas del sistema sin exponer la lógica de negocio.</p>
 *
 * @author Grupo C
 * @version 1.0
 * @since 31-07-2025
 */

public class PlaylistDTO {
    /**Identificador único de la playlist en la base de datos.*/
    private int idPlaylist;
    /**Título o nombre de la playlist.*/
    private String tituloPlaylist;
    /**Descripción opcional de la playlist. Puede ser null si no se proporciona descripción.*/
    private String descripcion;
    /**Identificador del usuario propietario de la playlist. Referencia a la tabla de usuarios/perfiles.*/
    private int idPropietario;
    /**Imagen de portada de la playlist en formato byte array. Puede ser null si la playlist no tiene imagen de portada.*/
    private byte[] imagenPortada;
    /**Fecha y hora de creación de la playlist. Se establece automáticamente al crear la playlist.*/
    private LocalDateTime fechaCreacion;
    /**Lista de identificadores de las canciones que contiene la playlist.El orden en la lista representa el orden de reproducción.
     * Puede ser null si la playlist está vacía.*/
    private List<Integer> cancionesIds;

    /**Constructor por defecto que inicializa una playlist vacía.
     * Los atributos se inicializan con valores por defecto.
     */
    public PlaylistDTO() {}

    /**
     * Constructor completo que inicializa una playlist con todos sus datos.
     * <p>Este constructor establece automáticamente la fecha de creación
     * con el momento actual del sistema.</p>
     * 
     * @param tituloPlaylist el título de la playlist
     * @param descripcion la descripción de la playlist (puede ser null)
     * @param idPropietario el ID del usuario propietario
     * @param imagenPortada la imagen de portada en formato byte array (puede ser null)
     * @param cancionesIds lista de IDs de las canciones que contiene (puede ser null)
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

    // ====================================================================
    // GETTERS Y SETTERS
    // ====================================================================
    
    /**
     * Obtiene el identificador único de la playlist.
     * 
     * @return el ID de la playlist
     */
    public int getIdPlaylist() { return idPlaylist; }
    /**
     * Establece el identificador único de la playlist.
     * 
     * @param idPlaylist el nuevo ID de la playlist
     */
    public void setIdPlaylist(int idPlaylist) { this.idPlaylist = idPlaylist; }
    /**
     * Obtiene el título de la playlist.
     * 
     * @return el título de la playlist
     */
    public String getTituloPlaylist() { return tituloPlaylist; }
        /**
     * Establece el título de la playlist.
     * 
     * @param tituloPlaylist el nuevo título de la playlist
     */
    public void setTituloPlaylist(String tituloPlaylist) { this.tituloPlaylist = tituloPlaylist; }
    /**
     * Obtiene la descripción de la playlist.
     * 
     * @return la descripción de la playlist (puede ser null)
     */
    public String getDescripcion() { return descripcion; }
    /**
     * Establece la descripción de la playlist.
     * 
     * @param descripcion la nueva descripción de la playlist (puede ser null)
     */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    /**
     * Obtiene el identificador del propietario de la playlist.
     * 
     * @return el ID del usuario propietario
     */
    public int getIdPropietario() { return idPropietario; }
    /**
     * Establece el identificador del propietario de la playlist.
     * 
     * @param idPropietario el nuevo ID del usuario propietario
     */
    public void setIdPropietario(int idPropietario) { this.idPropietario = idPropietario; }
    /**
     * Obtiene la imagen de portada de la playlist.
     * 
     * @return la imagen de portada en formato byte array (puede ser null)
     */
    public byte[] getImagenPortada() { return imagenPortada; }
    /**
     * Establece la imagen de portada de la playlist.
     * 
     * @param imagenPortada la nueva imagen de portada en formato byte array (puede ser null)
     */
    public void setImagenPortada(byte[] imagenPortada) { this.imagenPortada = imagenPortada; }
    /**
     * Obtiene la fecha de creación de la playlist.
     * 
     * @return la fecha y hora de creación
     */
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
        /**
     * Establece la fecha de creación de la playlist.
     * 
     * @param fechaCreacion la nueva fecha y hora de creación
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    /**
     * Obtiene la lista de identificadores de las canciones de la playlist.
     * 
     * @return lista de IDs de canciones (puede ser null si la playlist está vacía)
     */
    public List<Integer> getCancionesIds() { return cancionesIds; }
    /**
     * Establece la lista de identificadores de las canciones de la playlist.
     * 
     * @param cancionesIds la nueva lista de IDs de canciones (puede ser null)
     */
    public void setCancionesIds(List<Integer> cancionesIds) { this.cancionesIds = cancionesIds; }
    // ====================================================================
    // MÉTODOS DE REPRESENTACIÓN
    // ====================================================================
    
    /**
     * Genera una representación en cadena de texto de la playlist.
     * 
     * <p>Esta representación incluye los datos más relevantes de la playlist:
     * ID, título, descripción, propietario, fecha de creación y número de canciones.</p>
     * 
     * @return representación en cadena de texto de la playlist
     */
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
