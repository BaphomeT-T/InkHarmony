package DataAccessComponent.DAO;

import DataAccessComponent.SQLiteDataHelper;
import DataAccessComponent.DTO.Perfil;
import java.util.List;
import java.util.ArrayList;

public class PerfilDAO extends SQLiteDataHelper{
    
    // Lista temporal para simular base de datos
    private static List<Perfil> usuarios = new ArrayList<>();
    
    public void guardar(Perfil perfil) {
        // Implementación pendiente
        usuarios.add(perfil);
    }
    
    public Perfil buscarPorEmail(String email) {
        // Implementación pendiente
        for (Perfil perfil : usuarios) {
            if (perfil.getEmail().equals(email)) {
                return perfil;
            }
        }
        return null;
    }
    
    public List<Perfil> listarTodos() {
        // Implementación pendiente
        return new ArrayList<>(usuarios);
    }
    
    public void eliminar(Perfil perfil) {
        // Implementación pendiente
        usuarios.remove(perfil);
    }
    
    public void actualizar(Perfil perfil) {
        // Implementación pendiente
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getEmail().equals(perfil.getEmail())) {
                usuarios.set(i, perfil);
                break;
            }
        }
    }
    
    public void desactivar(Perfil perfil) {
        // Implementación pendiente
        perfil.setCuentaActiva(false);
        actualizar(perfil);
    }
} 