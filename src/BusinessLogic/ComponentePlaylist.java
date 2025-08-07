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

public interface ComponentePlaylist {
    /**
     * Muestra la información del componente.
     */
    void mostrarInformacion();

    /**
     * Obtiene la duración total del componente en segundos.
     * @return duración en segundos
     */
    double obtenerDuracion();

    /**
     * Obtiene el título del componente.
     * @return título del componente
     */
    String getTitulo();
}