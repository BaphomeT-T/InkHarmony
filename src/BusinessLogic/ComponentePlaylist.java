/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Grupo C
Interface que define el comportamiento común para componentes de playlist.
Implementado por Cancion y Playlist para permitir composición.
*/

package BusinessLogic;

/**
 * Interfaz que define el comportamiento base para componentes de una playlist,
 * como {@code Cancion} y {@code Playlist}, permitiendo su composición.
 * <p>
 * Esta interfaz forma parte del patrón de diseño *Composite*, facilitando
 * el tratamiento uniforme de elementos simples y compuestos.
 * </p>
 *
 * @author Grupo C
 * @version 1.0
 * @since 2025
 */
public interface ComponentePlaylist {

    /**
     * Muestra por consola o interfaz la información del componente.
     */
    void mostrarInformacion();

    /**
     * Obtiene la duración total del componente (en segundos).
     *
     * @return duración total en segundos
     */
    double obtenerDuracion();

    /**
     * Obtiene el título o nombre del componente.
     *
     * @return cadena de texto con el título
     */
    String getTitulo();
}
