/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Grupo C
Clase de lógica de negocio para gestión de playlists.
Implementa el patrón Composite para manejar componentes de playlist.
*/
package BusinessLogic;

import DataAccessComponent.DAO.PlaylistDAO;
import DataAccessComponent.DAO.CancionDAO;
import DataAccessComponent.DTO.PlaylistDTO;
import DataAccessComponent.DTO.CancionDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase de lógica de negocio para la gestión de playlists.
 * <p>
 * Implementa el patrón de diseño {@code Composite}, permitiendo que playlists
 * contengan otros componentes como canciones, y sean manipuladas de forma unificada.
 * </p>
 * <p>
 * Esta clase interactúa con los DAOs y valida datos utilizando {@link ServicioValidacionPlaylist}.
 * </p>
 * 
 * @author Grupo C
 * @version 1.0
 * @since 2025
 */
public class Playlist implements ComponentePlaylist {

    private PlaylistDTO playlistDTO;
    private PlaylistDAO playlistDAO;
    private CancionDAO cancionDAO;
    private List<ComponentePlaylist> componentes;
    private ServicioValidacionPlaylist validador;

    /**
     * Constructor vacío que inicializa DAOs, componentes y validador.
     */
    public Playlist() {
        this.playlistDAO = new PlaylistDAO();
        this.cancionDAO = new CancionDAO();
        this.componentes = new ArrayList<>();
        this.validador = new ServicioValidacionPlaylist();
    }

    /**
     * Constructor que crea una nueva playlist con los datos proporcionados.
     *
     * @param titulo título de la playlist
     * @param descripcion descripción de la playlist
     * @param idPropietario identificador del usuario propietario
     */
    public Playlist(String titulo, String descripcion, int idPropietario) {
        this();
        this.playlistDTO = new PlaylistDTO(titulo, descripcion, idPropietario, null, new ArrayList<>());
    }

    /**
     * Registra una nueva playlist en el sistema, validando su integridad
     * y verificando duplicados.
     *
     * @param titulo título de la playlist
     * @param descripcion descripción de la playlist
     * @param idPropietario ID del usuario propietario
     * @param imagenPortada imagen en bytes (puede ser null)
     * @param cancionesIds lista de IDs de canciones
     * @return {@code true} si se registró correctamente
     * @throws Exception si los datos son inválidos o hay duplicados
     */
    public boolean registrar(String titulo, String descripcion, int idPropietario,
                             byte[] imagenPortada, List<Integer> cancionesIds) throws Exception {

        // Crear DTO temporal para validación
        PlaylistDTO nuevaPlaylist = new PlaylistDTO(titulo, descripcion, idPropietario,
                imagenPortada, cancionesIds);

        // Validar datos usando el servicio de validación
        if (!validador.validarDatosCompletos(nuevaPlaylist)) {
            throw new Exception("Los datos de la playlist son inválidos. Verifique el título, descripción y propietario.");
        }

        // Verificar duplicados si es necesario
        if (validador.tieneDuplicados(nuevaPlaylist)) {
            throw new Exception("La playlist contiene canciones duplicadas.");
        }

        boolean resultado = playlistDAO.registrar(nuevaPlaylist);
        if (resultado) {
            this.playlistDTO = nuevaPlaylist;
            cargarComponentes();
        }

        return resultado;
    }


    /**
     * Busca y devuelve todas las playlists del sistema.
     *
     * @return lista de objetos {@code PlaylistDTO}
     * @throws Exception si ocurre un error en la consulta
     */
    public List<PlaylistDTO> buscarTodo() throws Exception {
        return playlistDAO.buscarTodo();
    }

    /**
     * Busca una playlist por su ID.
     *
     * @param id identificador de la playlist
     * @return objeto {@code PlaylistDTO} encontrado o {@code null}
     * @throws Exception si ocurre un error en la búsqueda
     */
    public PlaylistDTO buscarPorId(int id) throws Exception {
        PlaylistDTO playlist = playlistDAO.buscarPorId(id);
        if (playlist != null) {
            this.playlistDTO = playlist;
            cargarComponentes();
        }
        return playlist;
    }

    /**
     * Busca playlists por nombre.
     *
     * @param nombre cadena a buscar
     * @return lista de playlists con nombre coincidente
     * @throws Exception si ocurre un error en la consulta
     */
    public List<PlaylistDTO> buscarPorNombre(String nombre) throws Exception {
        return playlistDAO.buscarPorNombre(nombre);
    }

    /**
     * Obtiene las playlists pertenecientes a un usuario específico.
     *
     * @param idUsuario identificador del usuario
     * @return lista de {@code PlaylistDTO}
     * @throws Exception si ocurre un error en la consulta
     */
    public List<PlaylistDTO> obtenerPlaylistPorUsuario(int idUsuario) throws Exception {
        return playlistDAO.obtenerPlaylistPorUsuario(idUsuario);
    }

    /**
     * Actualiza los datos de una playlist.
     *
     * @param playlist objeto con la información a actualizar
     * @return {@code true} si se actualizó exitosamente
     * @throws Exception si ocurre un error
     */
    public boolean actualizar(PlaylistDTO playlist) throws Exception {
        boolean resultado = playlistDAO.actualizar(playlist);
        if (resultado) {
            this.playlistDTO = playlist;
            cargarComponentes();
        }
        return resultado;
    }

    /**
     * Elimina una playlist del sistema.
     *
     * @param idPlaylist identificador de la playlist
     * @return {@code true} si se eliminó correctamente
     * @throws Exception si ocurre un error
     */
    public boolean eliminar(int idPlaylist) throws Exception {
        return playlistDAO.eliminar(idPlaylist);
    }

    /**
     * Agrega una canción a la playlist, evitando duplicados.
     *
     * @param idCancion identificador de la canción
     * @return {@code true} si se agregó; {@code false} si ya existía
     * @throws Exception si no hay playlist cargada
     */
    public boolean agregarCancion(int idCancion) throws Exception {
        if (playlistDTO == null) {
            throw new Exception("No hay playlist cargada");
        }

        if (playlistDTO.getCancionesIds() == null) {
            playlistDTO.setCancionesIds(new ArrayList<>());
        }

        if (!playlistDTO.getCancionesIds().contains(idCancion)) {
            playlistDTO.getCancionesIds().add(idCancion);
            return playlistDAO.actualizar(playlistDTO);
        }

        return false; // Ya existe la canción
    }

    /**
     * Elimina una canción de la playlist.
     *
     * @param idCancion identificador de la canción
     * @return {@code true} si se eliminó; {@code false} si no estaba presente
     * @throws Exception si ocurre un error
     */
    public boolean eliminarCancion(int idCancion) throws Exception {
        if (playlistDTO == null || playlistDTO.getCancionesIds() == null) {
            return false;
        }

        boolean eliminado = playlistDTO.getCancionesIds().remove(Integer.valueOf(idCancion));
        if (eliminado) {
            return playlistDAO.actualizar(playlistDTO);
        }

        return false;
    }

    /**
     * Obtiene los archivos de audio de las canciones asociadas a la playlist.
     *
     * @return lista de arreglos de bytes con los archivos MP3
     * @throws Exception si ocurre un error
     */
    public List<byte[]> obtenerCancionesParaReproduccion() throws Exception {
        if (playlistDTO == null) {
            return new ArrayList<>();
        }

        List<byte[]> cancionesBytes = new ArrayList<>();
        List<CancionDTO> canciones = playlistDAO.obtenerCancionesCompletasDePlaylist(playlistDTO.getIdPlaylist());

        for (CancionDTO cancion : canciones) {
            if (cancion.getArchivoMP3() != null) {
                cancionesBytes.add(cancion.getArchivoMP3());
            }
        }

        return cancionesBytes;
    }

    /**
     * Reproduce la playlist usando un reproductor MP3 si contiene canciones válidas.
     *
     * @throws Exception si no hay canciones o ocurre un error
     */

    public void reproducir() throws Exception {
        List<byte[]> cancionesBytes = obtenerCancionesParaReproduccion();
        if (!cancionesBytes.isEmpty()) {
            ReproductorMP3 reproductor = ReproductorMP3.getInstancia(cancionesBytes);
            reproductor.reproducir();
        } else {
            throw new Exception("La playlist está vacía o no tiene archivos de audio");
        }
    }

    // Implementación de ComponentePlaylist

    @Override
    public void mostrarInformacion() {
        if (playlistDTO != null) {
            System.out.println("Playlist: " + playlistDTO.getTituloPlaylist());
            System.out.println("Descripción: " + playlistDTO.getDescripcion());
            System.out.println("Cantidad de canciones: " + calcularCantidadCanciones());
            System.out.println("Duración total: " + obtenerDuracion() + " segundos");
        }
    }

    @Override
    public double obtenerDuracion() {
        double duracionTotal = 0;
        try {
            if (playlistDTO != null) {
                List<CancionDTO> canciones = playlistDAO.obtenerCancionesCompletasDePlaylist(playlistDTO.getIdPlaylist());
                for (CancionDTO cancion : canciones) {
                    duracionTotal += cancion.getDuracion();
                }
            }
        } catch (Exception e) {
            System.err.println("Error al calcular duración: " + e.getMessage());
        }
        return duracionTotal;
    }

    @Override
    public String getTitulo() {
        return playlistDTO != null ? playlistDTO.getTituloPlaylist() : "";
    }

    /**
     * Agrega un componente a la lista de subcomponentes.
     *
     * @param componente objeto que implementa {@code ComponentePlaylist}
     */
    public void agregar(ComponentePlaylist componente) {
        if (componentes == null) {
            componentes = new ArrayList<>();
        }
        componentes.add(componente);
    }

    /**
     * Elimina un componente de la lista de subcomponentes.
     *
     * @param componente objeto a eliminar
     */
    public void eliminar(ComponentePlaylist componente) {
        if (componentes != null) {
            componentes.remove(componente);
        }
    }

    /**
     * Retorna la cantidad total de canciones en la playlist.
     *
     * @return número de canciones
     */
    public int calcularCantidadCanciones() {
        if (playlistDTO != null && playlistDTO.getCancionesIds() != null) {
            return playlistDTO.getCancionesIds().size();
        }
        return 0;
    }

    /**
     * Carga las canciones de la playlist desde la base de datos y las adapta como componentes.
     *
     * @throws Exception si ocurre un error al acceder a la base de datos
     */
    private void cargarComponentes() throws Exception {
        if (playlistDTO != null && playlistDTO.getCancionesIds() != null) {
            componentes = new ArrayList<>();
            for (Integer idCancion : playlistDTO.getCancionesIds()) {
                CancionDTO cancionDTO = cancionDAO.buscarPorId(idCancion);
                if (cancionDTO != null) {
                    // Crear un componente adaptador que encapsula el CancionDTO
                    CancionComponente cancionComponente = new CancionComponente(cancionDTO);
                    componentes.add(cancionComponente);
                }
            }
        }
    }

    /**
     * Clase interna que actúa como adaptador para CancionDTO.
     * Permite que los objetos CancionDTO se comporten como ComponentePlaylist.
     */
    private static class CancionComponente implements ComponentePlaylist {
        private final CancionDTO cancionDTO;
        /**
         * Constructor que recibe el objeto {@code CancionDTO} a adaptar.
         *
         * @param cancionDTO objeto de tipo canción
         */
        public CancionComponente(CancionDTO cancionDTO) {
            this.cancionDTO = cancionDTO;
        }

        @Override
        public void mostrarInformacion() {
            if (cancionDTO != null) {
                System.out.println("Canción: " + cancionDTO.getTitulo());
                System.out.println("Duración: " + cancionDTO.getDuracion() + " segundos");
                if (cancionDTO.getArtistas() != null && !cancionDTO.getArtistas().isEmpty()) {
                    System.out.println("Artista(s): " + 
                        cancionDTO.getArtistas().stream()
                            .map(artista -> artista.getNombre())
                            .reduce((a, b) -> a + ", " + b)
                            .orElse("Sin artistas"));
                }
            }
        }
     /**
         * Obtiene la duración de la canción adaptada.
         *
         * @return duración en segundos
         */
        @Override
        public double obtenerDuracion() {
            return cancionDTO != null ? cancionDTO.getDuracion() : 0.0;
        }
        /**
         * Obtiene el título de la canción adaptada.
         *
         * @return título como cadena
         */
        @Override
        public String getTitulo() {
            return cancionDTO != null ? cancionDTO.getTitulo() : "";
        }
        /**
         * Devuelve el objeto {@code CancionDTO} original encapsulado.
         *
         * @return instancia de {@code CancionDTO}
         */
        public CancionDTO getCancionDTO() {
            return cancionDTO;
        }
    }

    /**
     * Asigna el objeto {@code PlaylistDTO} a la playlist.
     *
     * @param playlistDTO objeto a asignar
     */
    public PlaylistDTO getPlaylistDTO() { return playlistDTO; }
    public void setPlaylistDTO(PlaylistDTO playlistDTO) { this.playlistDTO = playlistDTO; }
    /**
     * Devuelve la lista de componentes asociados a la playlist.
     *
     * @return lista de {@code ComponentePlaylist}
     */
    public List<ComponentePlaylist> getComponentes() { return componentes; }
}
