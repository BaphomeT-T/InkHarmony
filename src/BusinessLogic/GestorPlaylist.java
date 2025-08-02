package BusinessLogic;

import java.util.List;

/**
 * Clase encargada de gestionar una lista de reproducción de canciones en formato byte[].
 *
 * Permite navegar entre canciones (siguiente, anterior), reiniciar la lista,
 * y obtener o modificar la canción actual y la lista completa.
 *
 * Es utilizada por el reproductor MP3 para acceder a las canciones que se van a reproducir.
 */
public class GestorPlaylist {
    /** Lista de canciones en formato byte[]. */
    private List<byte[]> canciones;

    /** Índice de la canción que se está reproduciendo actualmente. */
    private int indiceActual = 0;

    /**
     * Constructor que inicializa la lista de canciones.
     *
     * @param canciones Lista de canciones en formato byte[]
     */
    public GestorPlaylist(List<byte[]> canciones) {
        this.canciones = canciones;
    }

    /**
     * Devuelve la canción actual en reproducción.
     *
     * @return Arreglo de bytes correspondiente a la canción actual
     */
    public byte[] obtenerCancionActual() {
        return canciones.get(indiceActual);
    }

    /**
     * Avanza a la siguiente canción en la lista.
     * Si está en la última, vuelve al inicio (comportamiento circular).
     */
    public void siguiente() {
        indiceActual = (indiceActual + 1) % canciones.size();
    }

    /**
     * Retrocede a la canción anterior en la lista.
     * Si está en la primera, salta a la última (comportamiento circular).
     */
    public void anterior() {
        indiceActual = (indiceActual - 1 + canciones.size()) % canciones.size();
    }

    /**
     * Reinicia la lista de reproducción al inicio (índice 0).
     */
    public void reiniciar() {
        indiceActual = 0;
    }

    /**
     * Obtiene el índice actual de la canción en reproducción.
     *
     * @return Índice actual
     */
    public int getIndiceActual() {
        return indiceActual;
    }

    /**
     * Establece el índice actual de la canción en reproducción.
     *
     * @param indice Nuevo índice
     */
    public void setIndiceActual(int indice) {
        this.indiceActual = indice;
    }

    /**
     * Devuelve la lista completa de canciones.
     *
     * @return Lista de canciones en formato byte[]
     */
    public List<byte[]> getCanciones() {
        return canciones;
    }

    /**
     * Reemplaza la lista de canciones y reinicia la posición actual.
     *
     * @param nuevaLista Nueva lista de canciones
     */
    public void setCanciones(List<byte[]> nuevaLista) {
        this.canciones = nuevaLista;
        reiniciar();
    }
}
