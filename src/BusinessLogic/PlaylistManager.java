package BusinessLogic;

import java.util.*;

class PlaylistManager {
    public static void main(String[] args) {
        try {
            PlaylistDAO dao = new PlaylistDAO();

            Perfil usuario = new Perfil("Jdfaskfj dsakl", "dfakjldj@jdk");

            Playlist miPlaylist = new Playlist("Playlist", "Las mejores canciones del año", usuario);

            // Agregar canciones
            Cancion cancion1 = new Cancion(1, "Canción 1", 3.5, 1);
            Cancion cancion2 = new Cancion(2, "Canción 2", 4.2, 2);
            miPlaylist.agregar(cancion1);
            miPlaylist.agregar(cancion2);

            dao.registrarPlaylist(miPlaylist);

            System.out.println("Playlist guardada con ID: " + miPlaylist.getIdPlaylist());

            // Buscar playlists
            List<Playlist> playlists = dao.buscarPlaylist();
            System.out.println("Playlists encontradas: " + playlists.size());

            for (Playlist p : playlists) {
                p.mostrarInformacion();
                System.out.println("---");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}