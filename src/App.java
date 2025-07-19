import BusinessLogic.Cancion;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DTO.CancionDTO;
import BusinessLogic.Genero;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class App {
    public static void main(String[] args) throws Exception {
        try {
            // Simulación de artistas y géneros existentes (mock)
            ArtistaDTO artista1 = new ArtistaDTO();
            artista1.setIdArtista(1); // ID debe existir en la base de datos
            artista1.setNombre("Artista de prueba");

            List<ArtistaDTO> artistas = new ArrayList<>();
            artistas.add(artista1);

            List<Genero> generos = new ArrayList<>();
            generos.add(Genero.ROCK); // Asumiendo que tienes un enum llamado ROCK

            // Leer archivo MP3 y portada desde disco
            byte[] mp3 = Files.readAllBytes(new File("C:/Users/ASUS/Desktop/Mama/y2meta.com - NAVIDAD, NAVIDAD [KARAOKE] HD (128 kbps).mp3").toPath());
            byte[] portada = Files.readAllBytes(new File("C:/Users/ASUS/Desktop/ani obi/DSC00079.JPG").toPath());

            // Llamada a la lógica de negocio
            Cancion cancion = new Cancion();
            boolean exito = cancion.registrar(
                    "Mi Canción de Pruebssssa",
                    2024,
                    mp3,
                    portada,
                    artistas,
                    generos
            );

            if (exito) {
                System.out.println("✅ Canción registrada con éxito.");
            } else {
                System.out.println("❌ No se pudo registrar la canción.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
