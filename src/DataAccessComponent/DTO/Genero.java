package DataAccessComponent.DTO;

import DataAccessComponent.SQLiteDataHelper;

/**
 * Clase DTO que representa un género musical en el sistema InkHarmony.
 * Extiende SQLiteDataHelper para aprovechar la funcionalidad de conexión
 * a la base de datos y encapsula la información de un género musical.
 * 
 * <p>Esta clase se utiliza para representar los diferentes géneros musicales
 * disponibles en la aplicación, como Rock, Pop, Jazz, Clásica, etc. Los géneros
 * se utilizan para categorizar música y para las preferencias musicales de los usuarios.</p>
 * 
 * <p>La clase proporciona constructores para crear géneros y métodos getter/setter
 * para acceder y modificar el nombre del género de manera controlada.</p>
 * 
 * @author InkHarmony Team
 * @version 1.0
 * @since 1.0
 */
public class Genero extends SQLiteDataHelper {
    
    /** Nombre del género musical */
    private String nombreGenero;

    /**
     * Constructor por defecto de Genero.
     * Crea una instancia vacía de Genero sin inicializar el nombre del género.
     */
    public Genero() {
    }

    /**
     * Constructor de Genero con el nombre del género.
     * 
     * <p>Este constructor inicializa el género con el nombre proporcionado.
     * Es útil para crear objetos Genero con datos específicos.</p>
     * 
     * @param nombreGenero El nombre del género musical
     * 
     * @throws IllegalArgumentException Si el nombreGenero es null o está vacío
     */
    public Genero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }

    /**
     * Obtiene el nombre del género musical.
     * 
     * @return El nombre del género musical
     */
    public String getNombreGenero() {
        return nombreGenero;
    }

    /**
     * Establece el nombre del género musical.
     * 
     * @param nombreGenero El nuevo nombre del género musical
     */
    public void setNombreGenero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }

    /**
     * Retorna una representación en cadena del género musical.
     * 
     * <p>Este método sobrescribe el método toString() de la clase Object
     * para proporcionar una representación legible del objeto Genero.</p>
     * 
     * @return Una cadena que representa el género musical en formato "Generos{nombreGenero='nombre'}"
     */
    @Override
    public String toString() {
        return "Generos{" +
                "nombreGenero='" + nombreGenero + '\'' +
                '}';
    }
} 