/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Samira Arízaga, Paul Dávila, Sebastián Ramos
Descripción: Clase de lógica de negocio (BL) que gestiona operaciones sobre canciones dentro del sistema InkHarmony.
*/

package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.CancionDTO;

import java.time.LocalDateTime;
import java.util.List;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Header;
import java.io.ByteArrayInputStream;

/**
 * Clase CancionBL que representa la lógica de negocio relacionada con canciones.
 *
 * <p>Actúa como intermediaria entre la capa de presentación y la capa de acceso a datos (DAO),
 * proporcionando métodos para registrar, consultar, actualizar y eliminar canciones,
 * así como buscar por nombre.</p>
 *
 * <p>Esta clase no contiene lógica de acceso a la base de datos directamente,
 * sino que delega dicha responsabilidad al CancionDAO.</p>
 *
 * @author Grupo A
 * @version 1.0
 * @since 19-07-2025
 */
public class Cancion {

    /** Objeto temporal para almacenar la canción actual manipulada */
    private CancionDTO cancion;

    /** Objeto DAO que se encarga del acceso a la base de datos */
    private CancionDAO cancionDAO = new CancionDAO();

    /**
     * Constructor vacío.
     */
    public Cancion() {}

    /**
     * Recupera todas las canciones registradas en la base de datos.
     *
     * @return Lista de objetos CancionDTO con todos los registros.
     * @throws Exception si ocurre algún error en el acceso a datos.
     */
    public List<CancionDTO> buscarTodo() throws Exception {
        return cancionDAO.buscarTodo();
    }

    /**
     * Recupera una canción específica por su identificador.
     *
     * @param idCancion ID de la canción a consultar.
     * @return Objeto CancionDTO con los datos completos.
     * @throws Exception si ocurre algún error.
     */
    public CancionDTO buscarPorId(int idCancion) throws Exception {
        cancion = cancionDAO.buscarPorId(idCancion);
        return cancion;
    }

    /**
     * Registra una nueva canción en el sistema.
     * Crea internamente el objeto CancionDTO usando los parámetros recibidos
     * y luego lo pasa al DAO para su almacenamiento.
     *
     * @param titulo Título de la canción.
     * @param anio Año de publicación.
     * @param archivoMP3 Archivo de audio en formato MP3.
     * @param portada Imagen de portada.
     * @param artistas Lista de artistas asociados.
     * @param generos Lista de géneros asociados.
     * @return true si el registro fue exitoso.
     * @throws Exception si ocurre algún error en el DAO.
     */
    public boolean registrar(String titulo, String anio,
                             String archivoMP3, List<Genero> portada,
                             String artistas, byte[] generos) throws Exception {

        LocalDateTime fechaRegistro = LocalDateTime.now();
        double duracion = obtenerDuracionDesdeMP3(archivoMP3);
        CancionDTO nuevaCancion = new CancionDTO(titulo, duracion, anio, fechaRegistro,
                archivoMP3, portada, artistas, generos);
        return cancionDAO.registrar(nuevaCancion);
    }

    /**
     * Actualiza los datos de una canción ya existente.
     *
     * @param cancionDTO Objeto con los nuevos datos.
     * @return true si se actualizó correctamente.
     * @throws Exception si ocurre algún error.
     */
    public boolean actualizar(CancionDTO cancionDTO) throws Exception {
        return cancionDAO.actualizar(cancionDTO);
    }

    /**
     * Elimina una canción según su ID.
     *
     * @param idCancion ID de la canción a eliminar.
     * @return true si se eliminó correctamente.
     * @throws Exception si ocurre algún error.
     */
    public boolean eliminar(int idCancion) throws Exception {
        return cancionDAO.eliminar(idCancion);
    }

    /**
     * Busca canciones por coincidencia exacta del título.
     *
     * @param titulo Nombre de la canción a buscar.
     * @return Lista de canciones con ese título.
     * @throws Exception si ocurre algún error.
     */
    public List<CancionDTO> buscarPorTitulo(String titulo) throws Exception {
        return cancionDAO.buscarPorNombre(titulo);
    }

    /**
     * Calcula la duración total de una canción a partir del archivo MP3 en forma de arreglo de bytes.
     *
     * <p>Este método utiliza la librería JLayer para leer los encabezados de los fotogramas MP3
     * y sumar su duración estimada en milisegundos. El resultado se convierte a segundos.</p>
     *
     * @param mp3Data Arreglo de bytes que representa el contenido binario del archivo MP3.
     * @return Duración aproximada de la canción en segundos. Retorna 0 si ocurre algún error.
     */
    private double obtenerDuracionDesdeMP3(byte[] mp3Data) {
        try {
            Bitstream bitstream = new Bitstream(new ByteArrayInputStream(mp3Data));
            Header header = bitstream.readFrame();
            if (header == null) return 0;

            int frames = 0;
            double duration = 0;

            while (header != null) {
                duration += header.ms_per_frame();
                frames++;
                bitstream.closeFrame();
                header = bitstream.readFrame();
            }

            return duration / 1000.0; // convertir a segundos
        } catch (Exception e) {
            return 0;
        }
    }


}
