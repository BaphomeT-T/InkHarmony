package BusinessLogic;
import DataAccessComponent.DTO.CancionDTO;
import DataAccessComponent.DTO.ArtistaDTO;
import DataAccessComponent.DAO.CancionDAO;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
/**
 * Clase ServicioValidacionCancion que proporciona servicios de validación para canciones.
 *
 * <p>Esta clase se encarga de validar las propiedades de las canciones, como su nombre,
 * duración y otros atributos relevantes. Utiliza el DAO CancionDAO para acceder a los datos
 * necesarios para realizar las validaciones.</p>
 *
 * @author Grupo A
 * @version 1.0
 * @since 19-07-2025
 */
public class ServicioValidacionCancion implements UnicoNombreValidable {
    private CancionDAO cancionDAO = new CancionDAO();

    public boolean validarTitulo(String titulo) {
        // Verifica que el título no sea nulo ni vacío
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        // Verifica que el título no exceda los 100 caracteres
        if (titulo.length() > 100) {
            return false;
        }
        return true;
    }

    public boolean validarAnio(int anio) {
        // Verifica que el año sea un valor positivo
        if (anio <= 0) {
            return false;
        }
        // Verifica que el año no sea mayor al año actual
        int currentYear = java.time.LocalDate.now().getYear();
        if (anio > currentYear) {
            return false;
        }
        return true;
    }

    public boolean validarArtistas(List<ArtistaDTO> artistas) {
        // Verifica que la lista de artistas no sea nula ni vacía
        if (artistas == null || artistas.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean validarGeneros(List<Genero> generos) {
        // Verifica que la lista de géneros no sea nula ni vacía
        if (generos == null || generos.isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean validarArchivoMP3(byte[] archivoMP3) {
        //  Si es null, lo aceptamos (por ejemplo, en actualización sin cambio de archivo)
        if (archivoMP3 == null) {
            return true;
        }
        // Si no es null, validar tamaño
        if (archivoMP3.length > 10 * 1024 * 1024) {
            return false;
        }
        return true;
    }

    public boolean validarPortada(byte[] portada) {
        // Si es null, lo aceptamos (por ejemplo, en actualización sin cambio de portada)
        if (portada == null) {
            return true;
        }
        // Si no es null, validar tamaño
        if (portada.length > 5 * 1024 * 1024) {
            return false;
        }
        return true;
    }

    public boolean validar(CancionDTO cancion, boolean esActualizacion) {
        boolean valida = true;

        if (!validarTitulo(cancion.getTitulo())) {
            System.out.println("❌ Título inválido.");
            valida = false;
        }

        if (!validarAnio(cancion.getAnio())) {
            System.out.println("❌ Año inválido.");
            valida = false;
        }

        if (!validarArtistas(cancion.getArtistas())) {
            System.out.println("❌ Artista inválido.");
            valida = false;
        }

        if (!validarGeneros(cancion.getGeneros())) {
            System.out.println("❌ Género inválido.");
            valida = false;
        }

        // Para portada: si es registro, debe estar presente
        if (!esActualizacion && cancion.getPortada() == null) {
            System.out.println("❌ La portada es obligatoria para el registro.");
            valida = false;
        } else if (!validarPortada(cancion.getPortada())) {
            System.out.println("❌ Portada inválida.");
            valida = false;
        }

        // Para MP3: si es registro, debe estar presente
        if (!esActualizacion && cancion.getArchivoMP3() == null) {
            System.out.println("❌ El archivo MP3 es obligatorio para el registro.");
            valida = false;
        } else if (!validarArchivoMP3(cancion.getArchivoMP3())) {
            System.out.println("❌ Archivo MP3 inválido.");
            valida = false;
        }

        if (!esNombreUnico(cancion.getTitulo())) {
            System.out.println("❌ El nombre ya está en uso.");
            valida = false;
        }

        return valida;
    }


    @Override
    public boolean esNombreUnico(String nombre) {
        try {
            // Recupera todas las canciones registradas
            List<CancionDTO> canciones = cancionDAO.buscarTodo();
            Set<String> nombresCanciones = new HashSet<>();

            // Agrega los nombres de las canciones a un conjunto para verificar unicidad
            for (CancionDTO cancion : canciones) {
                nombresCanciones.add(cancion.getTitulo());
            }

            // Verifica si el nombre proporcionado ya existe en el conjunto
            return !nombresCanciones.contains(nombre);
        } catch (Exception e) {
            e.printStackTrace();
            return false; // En caso de error, se asume que el nombre no es único
        }
    }
}

/*
-------------------------------------------------------------------------------------------------------------------
 */

//    public boolean esTituloCancionUnico(String titulo) {
//        // Verifica si ya existe una canción con este título
//        return !cancionDAO.existeCancionConTitulo(titulo);
//    }
//}


//    // Necesitarás inyectar o acceder a tu DAO de playlists
//    private final PlaylistDAO playlistDAO = new PlaylistDAO();
//
//    public boolean tieneElementosAsociados(CancionDTO cancion) {
//        // Verificar si la canción está en alguna playlist
//        return playlistDAO.existeCancionEnPlaylists(cancion.getIdCancion());
//   }
//}
