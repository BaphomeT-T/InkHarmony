package DataAccessComponent.DAO.CatalogoArtistas;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*Cree una clase ConexiónBD porque necesitamos que exista la conexión con la base de datos
 * no solo gráfica si no tambien como parte lógica
 * */

public class ConexionBD {

    private static final String URL = "jdbc:sqlite:C:/Users/Salma Morales/IdeaProjects/InkHarmony/identifier.sqlite"; //pon la ruta donde esta la base de datos

    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}