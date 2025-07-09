package DataAccessComponent.DAO;

import DataAccessComponent.SQLiteDataHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GeneroDAO extends SQLiteDataHelper {

    public static List<String> obtenerTodos() {
        List<String> generos = new ArrayList<>();
        String sql = "SELECT nombre_genero FROM Genero";

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                generos.add(rs.getString("nombre_genero"));
            }

        } catch (Exception e) {
            System.err.println("Error al obtener géneros desde la base de datos:");
            e.printStackTrace();
        }

        return generos;
    }
}