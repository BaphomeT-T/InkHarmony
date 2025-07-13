package DataAccessComponent.DTO.CatalogoArtistas;

import java.util.HashSet;
import java.util.Set;

public class ServicioValidacion implements UnicoNombreValidable, AsociacionValidable {

    // Simulación: artistas con canciones y playlists
    private Set<Integer> artistasConCanciones = new HashSet<>();
    private Set<Integer> artistasEnPlaylist = new HashSet<>();

    public ServicioValidacion() {
        // Simula que los artistas con ID 1 y 2 tienen asociaciones
        artistasConCanciones.add(1);
        artistasEnPlaylist.add(2);
    }

    public boolean validarCampos(Artista artista) {
        // Implementar validacion de campos
        // Agregar
        return true; // Temporal
    }

    public boolean esNombreUnico(String nombre) {
        // Implementacion para verificar si el nombre es unico
        // Agregar
        return false;
    }

    //implementando todos los metodos de la interfaz
    @Override
    public boolean tieneElementosAsociados(Artista artista) {
        int id = artista.getId();
        return artistasConCanciones.contains(id) || artistasEnPlaylist.contains(id);
    }

}
