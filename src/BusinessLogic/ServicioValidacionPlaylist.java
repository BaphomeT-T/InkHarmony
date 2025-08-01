/*
package BusinessLogic;

import java.util.*;

public class ServicioValidacionPlaylist {

    public boolean validarDatosCompletos(Playlist playlist) {
        if (playlist == null) return false;

        return validarTitulo(playlist.getTitulo()) &&
                validarDescripcion(playlist.getDescripcion()) &&
                playlist.getPropietario() != null;
    }

    public boolean tieneDuplicados(Playlist playlist) {
        if (playlist == null || playlist.getComponentes().isEmpty()) {
            return false;
        }

        Set<String> cancionesVistas = new HashSet<>();
        return verificarDuplicadosRecursivo(playlist.getComponentes(), cancionesVistas);
    }

    private boolean verificarDuplicadosRecursivo(List<ComponentePlaylist> componentes,
                                                 Set<String> cancionesVistas) {
        for (ComponentePlaylist componente : componentes) {
            if (componente instanceof Cancion) {
                Cancion cancion = (Cancion) componente;
                String idCancion = String.valueOf(cancion.getCancion().getIdCancion());

                if (cancionesVistas.contains(idCancion)) {
                    return true; // Duplicado encontrado
                }
                cancionesVistas.add(idCancion);

            } else if (componente instanceof Playlist) {
                Playlist subPlaylist = (Playlist) componente;
                if (verificarDuplicadosRecursivo(subPlaylist.getComponentes(), cancionesVistas)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean validarDescripcion(String descripcion) {
        return descripcion != null &&
                descripcion.trim().length() >= 5 &&
                descripcion.trim().length() <= 500;
    }

    public boolean validarTitulo(String titulo) {
        return titulo != null &&
                titulo.trim().length() >= 2 &&
                titulo.trim().length() <= 100 &&
                !titulo.trim().isEmpty();
    }
}*/
