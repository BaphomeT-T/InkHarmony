package BusinessLogic;

import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DTO.CancionDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
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

    /** Objeto en memoria que representa una canción manipulada actualmente */
    private CancionDTO cancion;


    /** DAO responsable de operaciones de acceso a datos para canciones */
    private CancionDAO cancionDAO = new CancionDAO();

    /**
     * Constructor por defecto.
     */
    public Cancion() {}

    /**
     * Recupera todas las canciones registradas en la base de datos.
     *
     * @return Lista de objetos {@link CancionDTO} con toda la información de canciones existentes.
     * @throws Exception si ocurre un error en la operación DAO.
     */
    public List<CancionDTO> buscarTodo() throws Exception {
        return cancionDAO.buscarTodo();
    }

    /**
     * Recupera una canción por su identificador único.
     *
     * @param idCancion ID de la canción buscada.
     * @return Objeto {@link CancionDTO} correspondiente.
     * @throws Exception si ocurre un error durante la consulta.
     */
    public CancionDTO buscarPorId(int idCancion) throws Exception {
        cancion = cancionDAO.buscarPorId(idCancion);
        return cancion;
    }

    /**
     * Registra una nueva canción con un solo artista (para compatibilidad con versiones anteriores).
     *
     * @param titulo     Título de la canción.
     * @param anio       Año de lanzamiento.
     * @param duracion   Duración de la canción (formato mm:ss).
     * @param generos    Lista de géneros musicales.
     * @param letra      Letra de la canción.
     * @param portada    Imagen de portada en bytes.
     * @param archivoMP3 Archivo MP3 en bytes.
     * @param artista    Artista principal.
     * @return true si el registro fue exitoso; false en caso contrario.
     * @throws Exception si ocurre un error al registrar.
     */
    public boolean registrar(String titulo, String anio,
                             String duracion, List<Genero> generos,
                             String letra, byte[] portada,
                             byte[] archivoMP3, ArtistaDTO artista) throws Exception {

        List<ArtistaDTO> artistas = new ArrayList<>();
        if (artista != null) {
            artistas.add(artista);
        }

        return registrarConMultiplesArtistas(titulo, anio, duracion, generos,
                letra, portada, archivoMP3, artistas);
    }


    /**
     * Registra una nueva canción con uno o más artistas.
     *
     * @param titulo     Título de la canción.
     * @param anio       Año de lanzamiento (en formato texto).
     * @param duracion   Duración en formato "mm:ss".
     * @param generos    Lista de géneros musicales asociados.
     * @param letra      Letra de la canción.
     * @param portada    Portada de la canción (formato byte array).
     * @param archivoMP3 Archivo MP3 en formato byte array.
     * @param artistas   Lista de artistas participantes.
     * @return true si se registró correctamente.
     * @throws Exception si ocurre un error en la operación DAO o en los datos de entrada.
     */
    public boolean registrarConMultiplesArtistas(String titulo, String anio,
                                                 String duracion, List<Genero> generos,
                                                 String letra, byte[] portada,
                                                 byte[] archivoMP3, List<ArtistaDTO> artistas) throws Exception {

        LocalDateTime fechaRegistro = LocalDateTime.now();

        // Conversión de duración a segundos
        String[] duracionParts = duracion.split(":");
        double duracionSegundos = 0;
        if (duracionParts.length == 2) {
            duracionSegundos = Integer.parseInt(duracionParts[0]) * 60 + Integer.parseInt(duracionParts[1]);
        }

        // Validar que hay al menos un artista
        if (artistas == null || artistas.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un artista asociado a la canción");
        }

        CancionDTO nuevaCancion = new CancionDTO(
                titulo,
                duracionSegundos,
                Integer.parseInt(anio),
                fechaRegistro,
                archivoMP3,
                portada,
                artistas,
                generos
        );

        System.out.println("Registrando canción '" + titulo + "' con " + artistas.size() +
                " artista(s): " + artistas.stream()
                .map(ArtistaDTO::getNombre)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Sin artistas"));

        return cancionDAO.registrar(nuevaCancion);
    }

    /**
     * Actualiza los datos de una canción existente en la base de datos.
     *
     * @param cancionDTO Objeto con la información actualizada de la canción.
     * @return true si la actualización fue exitosa.
     * @throws Exception si ocurre un error en la actualización.
     */
    public boolean actualizar(CancionDTO cancionDTO) throws Exception {
        return cancionDAO.actualizar(cancionDTO);
    }

    /**
     * Elimina una canción del sistema según su ID.
     *
     * @param idCancion Identificador de la canción a eliminar.
     * @return true si se eliminó correctamente.
     * @throws Exception si ocurre un error durante el proceso.
     */
    public boolean eliminar(int idCancion) throws Exception {
        return cancionDAO.eliminar(idCancion);
    }

    /**
     * Busca canciones cuyo título coincida exactamente con el valor proporcionado.
     *
     * @param titulo Título exacto de la canción a buscar.
     * @return Lista de {@link CancionDTO} con coincidencias.
     * @throws Exception si ocurre un error en la búsqueda.
     */
    public List<CancionDTO> buscarPorTitulo(String titulo) throws Exception {
        return cancionDAO.buscarPorNombre(titulo);
    }

    /**
     * Calcula la duración de un archivo MP3 utilizando su encabezado.
     *
     * Utiliza la librería JLayer para analizar los fotogramas del MP3
     * y calcular la duración total en segundos.
     *
     * @param mp3Data Arreglo de bytes correspondiente al archivo de audio.
     * @return Duración estimada en segundos; retorna 0 si ocurre un error.
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