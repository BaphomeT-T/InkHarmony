import DataAccessComponent.SQLiteDataHelper;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class TestConexionBD extends SQLiteDataHelper {
    
    public static void main(String[] args) {
        System.out.println("=== Test de Conexión a Base de Datos ===");
        
        try {
            // Test 1: Abrir conexión
            System.out.println("1. Probando abrir conexión...");
            Connection conn = openConnection();
            System.out.println("✅ Conexión exitosa!");
            
            // Test 2: Crear tabla de prueba
            System.out.println("2. Probando crear tabla de prueba...");
            Statement stmt = conn.createStatement();
            String createTableSQL = """
                CREATE TABLE IF NOT EXISTS test_conexion (
                    id INTEGER PRIMARY KEY,
                    mensaje TEXT
                )
            """;
            stmt.execute(createTableSQL);
            System.out.println("✅ Tabla de prueba creada!");
            
            // Test 3: Insertar dato de prueba
            System.out.println("3. Probando insertar dato...");
            String insertSQL = "INSERT INTO test_conexion (mensaje) VALUES ('Conexión funcionando correctamente')";
            stmt.execute(insertSQL);
            System.out.println("✅ Dato insertado!");
            
            // Test 4: Consultar dato
            System.out.println("4. Probando consultar dato...");
            ResultSet rs = stmt.executeQuery("SELECT * FROM test_conexion");
            if (rs.next()) {
                System.out.println("✅ Dato consultado: " + rs.getString("mensaje"));
            }
            
            // Test 5: Limpiar tabla de prueba
            System.out.println("5. Limpiando tabla de prueba...");
            stmt.execute("DELETE FROM test_conexion");
            System.out.println("✅ Tabla limpiada!");
            
            // Cerrar recursos
            rs.close();
            stmt.close();
            //closeConnection();
            
            System.out.println("\n🎉 ¡Todos los tests pasaron! La conexión a la BD funciona correctamente.");
            
        } catch (Exception e) {
            System.err.println("❌ Error en la conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 