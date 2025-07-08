package DataAccessComponent.DTO;

import java.util.Date;

public class Artista {
    private int id;
    private String nombre;

    //Generos debemos coordinar
    //protected Genero genero; creo que así porque es enum en el modulo de catálogo canciones
    private String genero;
    private Date fechaNacimiento;
    private String biografia;

    //Coordinar con los otros grupos
    private String imagen;

}
