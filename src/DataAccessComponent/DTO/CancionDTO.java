/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Samira Arízaga, Paul Dávila, Sebastián Ramos
Descripción: Objeto de transferencia de datos (DTO) que representa una canción dentro del sistema InkHarmony.
*/

package DataAccessComponent.DTO;

import BusinessLogic.Genero;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase CancionDTO que representa una canción en el sistema InkHarmony.
 *
 * <p>Objeto de transferencia de datos (DTO) que encapsula toda la información
 * relacionada con una canción musical. Esta clase actúa como contenedor de datos
 * que se transporta entre las diferentes capas del sistema (presentación, lógica
 * de negocio y acceso a datos) sin contener lógica de negocio.</p>
 *
 * <p>Incluye información básica como título, duración y año, así como datos
 * binarios como archivo MP3 y portada. Permite asociar múltiples artistas
 * y géneros musicales a una misma canción, reflejando la realidad de la
 * industria musical donde las colaboraciones son comunes.</p>
 *
 * <p>Los datos binarios (archivoMP3 y portada) se almacenan como arreglos
 * de bytes para facilitar su transferencia y almacenamiento en base de datos.</p>
 *
 * @author Grupo A
 * @version 1.0
 * @since 18-07-2025
 */
public class CancionDTO {

    /** Identificador único de la canción autogenerado por la base de datos */
    private int idCancion;

    /** Título o nombre de la canción */
    private String titulo;

    /** Duración de la canción expresada en segundos como número decimal */
    private double duracion;

    /** Año de lanzamiento o publicación de la canción */
    private int anio;

    /** Fecha y hora de registro de la canción en el sistema */
    private LocalDateTime fechaRegistro;

    /** Archivo de audio en formato MP3 almacenado como arreglo de bytes */
    private byte[] archivoMP3;

    /** Imagen de portada de la canción almacenada como arreglo de bytes */
    private byte[] portada;

    /** Lista de artistas que participan en la canción (soporte para colaboraciones) */
    private List<ArtistaDTO> artistas;

    /** Lista de géneros musicales asociados a la canción */
    private List<Genero> generos;

    /**
     * Constructor vacío requerido por frameworks de mapeo objeto-relacional.
     *
     * Este constructor permite la instanciación de objetos CancionDTO sin parámetros,
     * facilitando la creación de objetos por parte de frameworks como Hibernate
     * o durante procesos de deserialización.
     */
    public CancionDTO(String titulo, double duracion, String anio, LocalDateTime fechaRegistro, String archivoMP3, List<Genero> portada, String artistas, byte[] generos) {
        // Constructor vacío para frameworks de mapeo
    }

    /**
     * Constructor principal para crear una nueva instancia de CancionDTO.
     *
     * Utilizado principalmente para crear nuevas canciones antes de insertarlas
     * en la base de datos. El ID de la canción se asigna automáticamente por
     * la base de datos durante la inserción.
     *
     * @param titulo Título de la canción (máximo 100 caracteres)
     * @param duracion Duración en segundos como número decimal
     * @param anio Año de publicación (debe ser positivo y no mayor al año actual)
     * @param fechaRegistro Fecha y hora de registro en el sistema
     * @param archivoMP3 Archivo de audio en formato byte array (máximo 10MB)
     * @param portada Imagen de portada en formato byte array (máximo 5MB)
     * @param artistas Lista de artistas participantes (mínimo 1)
     * @param generos Lista de géneros musicales asociados (mínimo 1)
     */
    public CancionDTO(String titulo, double duracion, int anio, LocalDateTime fechaRegistro,
                      byte[] archivoMP3, byte[] portada,
                      List<ArtistaDTO> artistas, List<Genero> generos) {
        this.titulo = titulo;
        this.duracion = duracion;
        this.anio = anio;
        this.fechaRegistro = fechaRegistro;
        this.archivoMP3 = archivoMP3;
        this.portada = portada;
        this.artistas = artistas;
        this.generos = generos;
    }

    // ==================== MÉTODOS GETTER Y SETTER ====================

    /**
     * Obtiene el identificador único de la canción.
     *
     * @return ID de la canción como entero
     */
    public int getIdCancion() {
        return idCancion;
    }

    /**
     * Establece el identificador único de la canción.
     *
     * Generalmente utilizado por el DAO al recuperar canciones de la base de datos
     * donde el ID ya ha sido asignado automáticamente.
     *
     * @param idCancion ID único de la canción
     */
    public void setIdCancion(int idCancion) {
        this.idCancion = idCancion;
    }

    /**
     * Obtiene el título de la canción.
     *
     * @return Título de la canción como cadena de texto
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Establece el título de la canción.
     *
     * El título debe cumplir con las validaciones definidas en ServicioValidacionCancion
     * (longitud entre 1 y 100 caracteres, unicidad en el sistema).
     *
     * @param titulo Nuevo título para la canción
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Obtiene la duración de la canción en segundos.
     *
     * La duración se almacena como double para permitir precisión decimal,
     * aunque normalmente representa segundos completos.
     *
     * @return Duración de la canción en segundos
     */
    public double getDuracion() {
        return duracion;
    }

    /**
     * Establece la duración de la canción en segundos.
     *
     * La duración debe ser un valor positivo. Generalmente se calcula
     * automáticamente a partir del archivo MP3 durante el registro.
     *
     * @param duracion Duración en segundos como número decimal
     */
    public void setDuracion(double duracion) {
        this.duracion = duracion;
    }

    /**
     * Obtiene el año de lanzamiento de la canción.
     *
     * @return Año de publicación como entero
     */
    public int getAnio() {
        return anio;
    }

    /**
     * Establece el año de lanzamiento de la canción.
     *
     * El año debe ser validado para asegurar que sea un valor positivo
     * y no mayor al año actual según las reglas de negocio.
     *
     * @param anio Año de publicación
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * Obtiene la fecha y hora de registro de la canción en el sistema.
     *
     * @return Fecha de registro como LocalDateTime
     */
    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    /**
     * Establece la fecha y hora de registro de la canción.
     *
     * Generalmente se asigna automáticamente al momento de crear
     * la canción usando LocalDateTime.now().
     *
     * @param fechaRegistro Fecha y hora de registro
     */
    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /**
     * Obtiene el archivo de audio MP3 como arreglo de bytes.
     *
     * El archivo se almacena en formato binario para facilitar su
     * transferencia y almacenamiento en la base de datos.
     *
     * @return Archivo MP3 como byte array, null si no hay archivo asociado
     */
    public byte[] getArchivoMP3() {
        return archivoMP3;
    }

    /**
     * Establece el archivo de audio MP3.
     *
     * El archivo debe cumplir con las validaciones de formato MP3 y tamaño
     * (máximo 10MB) definidas en ServicioValidacionCancion.
     *
     * @param archivoMP3 Archivo de audio como arreglo de bytes
     */
    public void setArchivoMP3(byte[] archivoMP3) {
        this.archivoMP3 = archivoMP3;
    }

    /**
     * Obtiene la imagen de portada de la canción como arreglo de bytes.
     *
     * @return Imagen de portada como byte array, null si no hay portada
     */
    public byte[] getPortada() {
        return portada;
    }

    /**
     * Establece la imagen de portada de la canción.
     *
     * La imagen debe tener dimensiones exactas de 264x264 píxeles
     * y no exceder los 5MB de tamaño según las reglas de validación.
     *
     * @param portada Imagen de portada como arreglo de bytes
     */
    public void setPortada(byte[] portada) {
        this.portada = portada;
    }

    /**
     * Obtiene la lista de artistas asociados a la canción.
     *
     * El sistema permite múltiples artistas por canción para soportar
     * colaboraciones musicales y featuring entre artistas.
     *
     * @return Lista de objetos ArtistaDTO asociados a la canción
     */
    public List<ArtistaDTO> getArtistas() {
        return artistas;
    }

    /**
     * Establece la lista de artistas asociados a la canción.
     *
     * Debe contener al menos un artista según las reglas de validación
     * del sistema. Permite múltiples artistas para colaboraciones.
     *
     * @param artistas Lista de artistas participantes
     */
    public void setArtistas(List<ArtistaDTO> artistas) {
        this.artistas = artistas;
    }

    /**
     * Obtiene la lista de géneros musicales asociados a la canción.
     *
     * @return Lista de géneros musicales de tipo Genero enum
     */
    public List<Genero> getGeneros() {
        return generos;
    }

    /**
     * Establece la lista de géneros musicales asociados a la canción.
     *
     * Debe contener al menos un género según las reglas de validación.
     * Permite múltiples géneros para canciones que fusionan estilos musicales.
     *
     * @param generos Lista de géneros musicales
     */
    public void setGeneros(List<Genero> generos) {
        this.generos = generos;
    }

    /**
     * Devuelve una representación textual completa de la canción para depuración.
     *
     * Incluye todos los atributos de la canción en un formato legible,
     * mostrando información sobre archivos binarios sin incluir los datos
     * reales para evitar salidas excesivamente largas en logs.
     *
     * @return Representación en cadena de texto de todos los atributos de la canción
     */
    @Override
    public String toString() {
        return "\n" + getClass().getName()
                + "\nIdCancion      : " + idCancion
                + "\nTitulo         : " + titulo
                + "\nDuracion       : " + duracion
                + "\nAnio           : " + anio
                + "\nFechaRegistro  : " + fechaRegistro
                + "\nArchivoMP3     : " + (archivoMP3 != null ? "Sí" : "No")
                + "\nPortada        : " + (portada != null ? "Sí" : "No")
                + "\nArtistas       : " + artistas
                + "\nGeneros        : " + generos;
    }
}