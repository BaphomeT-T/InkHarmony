package DataAccessComponent.DAO;

import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DTO.TipoUsuario;
import DataAccessComponent.SQLiteDataHelper;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class PerfilDAO extends SQLiteDataHelper {
    
    public PerfilDAO() {
        
    }
    
    public void guardar(Perfil perfil) {
        String sql = """
            INSERT INTO perfil (nombre, apellido, email, contrasenia, tipo_usuario, foto)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, perfil.getNombre());
            pstmt.setString(2, perfil.getApellido());
            pstmt.setString(3, perfil.getEmail());
            pstmt.setString(4, perfil.getContrasenia());
            pstmt.setString(5, perfil.getTipoUsuario().toString());
            pstmt.setString(6, perfil.getFoto());
            
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Perfil buscarPorEmail(String email) {
        String sql = "SELECT * FROM perfil WHERE email = ?";
        
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Perfil perfil = crearPerfilDesdeResultSet(rs);
                rs.close();
                pstmt.close();
                return perfil;
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Perfil> listarTodos() {
        List<Perfil> perfiles = new ArrayList<>();
        String sql = "SELECT * FROM perfil";
        
        try {
            Connection conn = openConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                perfiles.add(crearPerfilDesdeResultSet(rs));
            }
            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return perfiles;
    }
    
    public void eliminar(Perfil perfil) {
        String sql = "DELETE FROM perfil WHERE email = ?";
        
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, perfil.getEmail());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void actualizar(Perfil perfil) {
        String sql = """
            UPDATE perfil 
            SET nombre = ?, apellido = ?, contrasenia = ?, 
                cuenta_activa = ?, tipo_usuario = ?, foto = ?
            WHERE email = ?
        """;
        
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            
            pstmt.setString(1, perfil.getNombre());
            pstmt.setString(2, perfil.getApellido());
            pstmt.setString(3, perfil.getContrasenia());
            pstmt.setBoolean(4, perfil.isCuentaActiva());
            pstmt.setString(5, perfil.getTipoUsuario().toString());
            pstmt.setString(6, perfil.getFoto());
            pstmt.setString(7, perfil.getEmail());
            
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void desactivar(Perfil perfil) {
        String sql = "UPDATE perfil SET cuenta_activa = 0 WHERE email = ?";
        
        try {
            Connection conn = openConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, perfil.getEmail());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Perfil crearPerfilDesdeResultSet(ResultSet rs) throws SQLException {
        Perfil perfil = new Perfil();
        perfil.setNombre(rs.getString("nombre"));
        perfil.setApellido(rs.getString("apellido"));
        perfil.setEmail(rs.getString("email"));
        perfil.setContrasenia(rs.getString("contrasenia"));
        perfil.setCuentaActiva(rs.getBoolean("cuenta_activa"));
        perfil.setTipoUsuario(TipoUsuario.valueOf(rs.getString("tipo_usuario")));
        perfil.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        perfil.setFoto(rs.getString("foto"));
        return perfil;
    }

}