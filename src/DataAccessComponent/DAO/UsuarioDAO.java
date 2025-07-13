package DataAccessComponent.DAO;

import DataAccessComponent.DTO.Usuario;
import DataAccessComponent.SQLiteDataHelper;
import DataAccessComponent.DTO.Genero;
import java.sql.*;
import java.util.List;

public class UsuarioDAO extends SQLiteDataHelper {

    public boolean guardarPreferencias(String correo, List<Genero> generos) {
        String sql = "UPDATE Usuario SET preferencias_musicales = ? WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            // Validar géneros contra la BD
            List<String> generosValidos = GeneroDAO.obtenerTodos();
            for (Genero genero : generos) {
                if (!generosValidos.contains(genero.getNombreGenero())) {
                    throw new SQLException("Género no registrado: " + genero.getNombreGenero());
                }
            }

            // Convertir a JSON y guardar
            Usuario usuario = new Usuario();
            usuario.setPreferenciasMusicales(generos);
            pstmt.setString(1, usuario.toJSON());
            pstmt.setString(2, correo);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Error al guardar preferencias: " + e.getMessage());
            return false;
        }
    }

    public List<Genero> obtenerPreferencias(String correo) {
        String sql = "SELECT preferencias_musicales FROM Usuario WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, correo);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String json = rs.getString("preferencias_musicales");
                return Usuario.fromJSON(json);
            }
            return List.of();
        } catch (Exception e) {
            System.err.println("Error al obtener preferencias: " + e.getMessage());
            return List.of();
        }
    }

    public boolean actualizarPreferencias(String correo, List<Genero> nuevosGeneros) {
        String sql = "UPDATE Usuario SET preferencias_musicales = ? WHERE correo = ?";
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            if (nuevosGeneros == null) {
                // Si es null, se actualiza la columna como NULL en la base de datos
                pstmt.setNull(1, Types.VARCHAR);
            } else {
                // Validar géneros
                List<String> generosValidos = GeneroDAO.obtenerTodos();
                for (Genero genero : nuevosGeneros) {
                    if (!generosValidos.contains(genero.getNombreGenero())) {
                        return false;
                    }
                }

                // Convertir a JSON manualmente
                StringBuilder json = new StringBuilder("[");
                for (int i = 0; i < nuevosGeneros.size(); i++) {
                    json.append("\"").append(nuevosGeneros.get(i).getNombreGenero()).append("\"");
                    if (i < nuevosGeneros.size() - 1) json.append(",");
                }
                json.append("]");
                pstmt.setString(1, json.toString());
            }

            pstmt.setString(2, correo);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            System.err.println("Error al actualizar las preferencias: " + e.getMessage());
            return false;
        }
    }

}