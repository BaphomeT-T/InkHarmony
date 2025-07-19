
import BusinessLogic.Cancion;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DTO.CancionDTO;
import BusinessLogic.Genero;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;


public class App {
    public static void main(String[] args) throws Exception {
        try {

            // Leer archivo MP3 y portada desde disco
            byte[] mp3 = Files.readAllBytes(new File("C:/Users/ramos/Downloads/Noah and the Whale - 'L.I.F.E.G.O.E.S.O.N.' (Official Video).mp3").toPath());
            byte[] portada = Files.readAllBytes(new File("C:/Users/ramos/Downloads/poratada.jpeg").toPath());

            // Llamada a la lógica de negocio
            Cancion cancion = new Cancion();
            List<CancionDTO> canciones = cancion.buscarTodo();
            CancionDTO registrada = canciones.get(canciones.size() - 1);
            System.out.println("Última canción registrada: " + registrada.getTitulo());
            // === ACTUALIZACIÓN ===
            System.out.println("\n=== ACTUALIZACIÓN ===");

            // Cambiar título y año
            registrada.setTitulo("Prueba de actualización");
            registrada.setAnio(2025);

            // Cambiar artista
            ArtistaDTO artista2 = new ArtistaDTO();
            artista2.setIdArtista(2); // Asegúrate que este artista exista
            artista2.setNombre("Artista nuevo");
            registrada.setArtistas(List.of(artista2));

            // Cambiar géneros
            registrada.setGeneros(Arrays.asList(Genero.MOVIE_OST, Genero.POP));

            // Actualizar portada (si se quiere)
            //byte[] nuevaPortada = Files.readAllBytes(new File("C:/Users/ramos/Downloads/nueva-portada.jpg").toPath());
            //registrada.setPortada(nuevaPortada);

            // No cambiar el archivo MP3, se deja como null
            registrada.setArchivoMP3(null);

            boolean actualizado = cancion.actualizar(registrada);
            if (actualizado) {
                System.out.println("✏️ Canción actualizada con éxito.");
            } else {
                System.out.println("❌ No se pudo actualizar la canción.");
            }

            System.out.println("\n=== ELIMINACIÓN ===");

            boolean eliminado = cancion.eliminar(registrada.getIdCancion());
            if (eliminado) {
                System.out.println("🗑 Canción eliminada correctamente.");
            } else {
                System.out.println("❌ No se pudo eliminar la canción.");
            }

        } catch (Exception e) {
            e.printStackTrace();
  }
}

}