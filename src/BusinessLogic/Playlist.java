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


public class Playlist implements ComponentePlaylist {

    private PlaylistDTO playlistDTO;
    private PlaylistDAO playlistDAO;
    private CancionDAO cancionDAO;
    private List<ComponentePlaylist> componentes;

    public Playlist() {
        this.playlistDAO = new PlaylistDAO();
        this.cancionDAO = new CancionDAO();
        this.componentes = new ArrayList<>();
    }

    public Playlist(String titulo, String descripcion, int idPropietario) {
        this();
        this.playlistDTO = new PlaylistDTO(titulo, descripcion, idPropietario, null, new ArrayList<>());
    }

    /**
     * Registra una nueva playlist en el sistema.
     */
    public boolean registrar(String titulo, String descripcion, int idPropietario,
                             byte[] imagenPortada, List<Integer> cancionesIds) throws Exception {

        PlaylistDTO nuevaPlaylist = new PlaylistDTO(titulo, descripcion, idPropietario,
                imagenPortada, cancionesIds);

        boolean resultado = playlistDAO.registrar(nuevaPlaylist);
        if (resultado) {
            this.playlistDTO = nuevaPlaylist;
            cargarComponentes();
        }

        return resultado;
    }

    /**
     * Busca todas las playlists.
     */
    public List<PlaylistDTO> buscarTodo() throws Exception {
        return playlistDAO.buscarTodo();
    }

    /**
     * Busca playlist por ID.
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
     */
    public List<PlaylistDTO> buscarPorNombre(String nombre) throws Exception {
        return playlistDAO.buscarPorNombre(nombre);
    }

    /**
     * Obtiene playlists de un usuario específico.
     */
    public List<PlaylistDTO> obtenerPlaylistPorUsuario(int idUsuario) throws Exception {
        return playlistDAO.obtenerPlaylistPorUsuario(idUsuario);
    }

    /**
     * Actualiza una playlist existente.
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
     * Elimina una playlist.
     */
    public boolean eliminar(int idPlaylist) throws Exception {
        return playlistDAO.eliminar(idPlaylist);
    }

    /**
     * Agrega una canción a la playlist.
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
     * Obtiene los bytes de audio de todas las canciones para reproducción.
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
     * Reproduce la playlist usando el reproductor MP3.
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
     * Agrega un componente a la playlist (patrón Composite).
     */
    public void agregar(ComponentePlaylist componente) {
        if (componentes == null) {
            componentes = new ArrayList<>();
        }
        componentes.add(componente);
    }

    /**
     * Elimina un componente de la playlist.
     */
    public void eliminar(ComponentePlaylist componente) {
        if (componentes != null) {
            componentes.remove(componente);
        }
    }

    /**
     * Calcula la cantidad total de canciones.
     */
    public int calcularCantidadCanciones() {
        if (playlistDTO != null && playlistDTO.getCancionesIds() != null) {
            return playlistDTO.getCancionesIds().size();
        }
        return 0;
    }

    /**
     * Carga los componentes de la playlist desde la base de datos.
     */
    private void cargarComponentes() throws Exception {
        if (playlistDTO != null && playlistDTO.getCancionesIds() != null) {
            componentes = new ArrayList<>();
            for (Integer idCancion : playlistDTO.getCancionesIds()) {
                CancionDTO cancionDTO = cancionDAO.buscarPorId(idCancion);
                if (cancionDTO != null) {
                    Cancion cancion = new Cancion();
                    // Aquí podrías cargar la canción completa si es necesario
                    componentes.add(cancion);
                }
            }
        }
    }

    // Getters y Setters
    public PlaylistDTO getPlaylistDTO() { return playlistDTO; }
    public void setPlaylistDTO(PlaylistDTO playlistDTO) { this.playlistDTO = playlistDTO; }
    public List<ComponentePlaylist> getComponentes() { return componentes; }
}