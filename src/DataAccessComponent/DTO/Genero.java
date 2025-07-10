package DataAccessComponent.DTO;

import DataAccessComponent.SQLiteDataHelper;

public class Genero extends SQLiteDataHelper {
    
    private String nombreGenero;

    public Genero() {
    }

    public Genero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }

    public String getNombreGenero() {
        return nombreGenero;
    }

    public void setNombreGenero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }

    @Override
    public String toString() {
        return "Generos{" +
                "nombreGenero='" + nombreGenero + '\'' +
                '}';
    }
} 