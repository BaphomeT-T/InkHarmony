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
 * <p>Valida los campos de una canción como el título, año, artistas, géneros, archivo MP3 y portada.
 * Es utilizada tanto para registros nuevos como actualizaciones.</p>
 *
 * <p>Implementa la interfaz {@link UnicoNombreValidable} para validar la unicidad del nombre.</p>
 *
 * @author Grupo A
 * @version 1.0
 * @since 19-07-2025
 */
public class ServicioValidacionCancion implements UnicoNombreValidable {
    /** DAO para acceder a las canciones registradas */
    private CancionDAO cancionDAO = new CancionDAO();
    /**
     * Valida que el título no sea nulo, vacío ni mayor a 100 caracteres.
     *
     * @param titulo Título de la canción.
     * @return true si es válido, false en caso contrario.
     */
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
    /**
     * Valida que el año sea positivo y no mayor al año actual.
     *
     * @param anio Año de lanzamiento.
     * @return true si el año es válido.
     */
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
    /**
     * Valida que la lista de artistas no sea nula ni vacía.
     *
     * @param artistas Lista de artistas.
     * @return true si contiene al menos un artista.
     */
    public boolean validarArtistas(List<ArtistaDTO> artistas) {
        // Verifica que la lista de artistas no sea nula ni vacía
        if (artistas == null || artistas.isEmpty()) {
            return false;
        }
        return true;
    }
    /**
     * Valida que la lista de géneros no sea nula ni vacía.
     *
     * @param generos Lista de géneros.
     * @return true si contiene al menos un género.
     */
    public boolean validarGeneros(List<Genero> generos) {
        // Verifica que la lista de géneros no sea nula ni vacía
        if (generos == null || generos.isEmpty()) {
            return false;
        }
        return true;
    }
    /**
     * Valida un archivo MP3 según su encabezado y tamaño.
     * Acepta null si no se requiere un nuevo archivo (por ejemplo, en actualizaciones).
     *
     * @param archivoMP3 Archivo binario del MP3.
     * @return true si cumple el formato MP3.
     */
    public boolean validarArchivoMP3(byte[] archivoMP3) {
        // Aceptamos null (por ejemplo en actualizaciones sin nuevo archivo)
        if (archivoMP3 == null) {
            return true;
        }

        // Validar tamaño (10 MB máx)
        if (archivoMP3.length > 10 * 1024 * 1024) {
            return false;
        }

        // Validar que tenga al menos 3 bytes para verificar la cabecera
        if (archivoMP3.length < 3) {
            return false;
        }

        // Verificar si empieza con "ID3"
        if (archivoMP3[0] == 'I' && archivoMP3[1] == 'D' && archivoMP3[2] == '3') {
            return true;
        }

        // Verificar si empieza con el encabezado típico de frame MPEG (0xFF 0xFB o similares)
        if ((archivoMP3[0] & 0xFF) == 0xFF &&
                ((archivoMP3[1] & 0xE0) == 0xE0)) {
            return true;
        }

        // Si no cumple ninguno de los dos, no es MP3
        return false;
    }

    /**
     * Valida el tamaño de la imagen de portada.
     * Acepta null si no se requiere una nueva imagen (por ejemplo, en actualizaciones).
     *
     * @param portada Imagen binaria.
     * @return true si cumple los requisitos.
     */
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
    /**
     * Realiza la validación general de una canción, ya sea para registro o actualización.
     *
     * @param cancion Canción a validar.
     * @param esActualizacion true si es actualización, false si es un nuevo registro.
     * @return true si la canción es válida, false en caso contrario.
     */
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

    /**
     * Verifica si un nombre de canción ya existe en el sistema.
     *
     * @param nombre Nombre a verificar.
     * @return true si el nombre es único, false si ya existe.
     */
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

