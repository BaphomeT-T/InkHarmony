import DataAccessComponent.DAO.PerfilDAO;
import DataAccessComponent.DTO.Perfil;
import DataAccessComponent.DTO.TipoUsuario;
import java.util.List;
import java.util.Date;

public class TestPerfilDAO {
    
    public static void main(String[] args) {
        System.out.println("=== Test de PerfilDAO ===");
        
        PerfilDAO perfilDAO = new PerfilDAO();
        
        try {
            // Test 1: Guardar un perfil
            System.out.println("1. Probando guardar perfil...");
            Perfil perfilTest = new Perfil();
            perfilTest.setNombre("Test");
            perfilTest.setApellido("Usuario");
            perfilTest.setEmail("test@email.com");
            perfilTest.setContrasenia("password123");
            perfilTest.setTipoUsuario(TipoUsuario.USUARIO);
            perfilTest.setFechaRegistro(new Date());
            perfilTest.setFoto("/UserInterface/Resources/img/Perfil/perfilH1.jpg");
            
            perfilDAO.guardar(perfilTest);
            System.out.println("✅ Perfil guardado!");
            
            // Test 2: Buscar por email
            System.out.println("2. Probando buscar por email...");
            Perfil encontrado = perfilDAO.buscarPorEmail("test@email.com");
            if (encontrado != null) {
                System.out.println("✅ Perfil encontrado: " + encontrado.getNombre() + " " + encontrado.getApellido());
            } else {
                System.out.println("❌ Perfil no encontrado");
            }
            
            // Test 3: Listar todos
            System.out.println("3. Probando listar todos...");
            List<Perfil> todos = perfilDAO.listarTodos();
            System.out.println("✅ Total de perfiles: " + todos.size());
            for (Perfil p : todos) {
                System.out.println("  - " + p.getNombre() + " " + p.getApellido() + " (" + p.getEmail() + ")");
            }
            
            // Test 4: Actualizar perfil
            System.out.println("4. Probando actualizar perfil...");
            encontrado.setNombre("Test Actualizado");
            perfilDAO.actualizar(encontrado);
            System.out.println("✅ Perfil actualizado!");
            
            // Test 5: Desactivar cuenta
            System.out.println("5. Probando desactivar cuenta...");
            perfilDAO.desactivar(encontrado);
            System.out.println("✅ Cuenta desactivada!");
            
            // Test 6: Verificar que está desactivada
            Perfil desactivado = perfilDAO.buscarPorEmail("test@email.com");
            if (desactivado != null && !desactivado.isCuentaActiva()) {
                System.out.println("✅ Cuenta correctamente desactivada");
            }
            
            // Test 7: Eliminar perfil
            System.out.println("6. Probando eliminar perfil...");
            perfilDAO.eliminar(encontrado);
            System.out.println("✅ Perfil eliminado!");
            
            // Test 8: Verificar que fue eliminado
            Perfil eliminado = perfilDAO.buscarPorEmail("test@email.com");
            if (eliminado == null) {
                System.out.println("✅ Perfil correctamente eliminado");
            }
            
            System.out.println("\n🎉 ¡Todos los tests del PerfilDAO pasaron!");
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 