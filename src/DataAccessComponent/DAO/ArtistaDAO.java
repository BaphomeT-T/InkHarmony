package DataAccessComponent.DAO;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import DataAccessComponent.DTO.CatalogoArtistas.ArtistaDTO;
import DataAccessComponent.SQLiteDataHelper;

public class ArtistaDAO extends SQLiteDataHelper implements IDAO<ArtistaDTO> {
    @Override
    public boolean registrar(ArtistaDTO entity) throws Exception {
        return false;
    }

    @Override
    public List<ArtistaDTO> buscarTodo() throws Exception {
        return null;
    }

    @Override
    public ArtistaDTO buscarPorId(Integer id) throws Exception {
        return null;
    }

    @Override
    public boolean actualizar(ArtistaDTO entity) throws Exception {
        return false;
    }

    @Override
    public boolean eliminar(Integer id) throws Exception {
        return false;
    }

//    protected ArtistaDTO artista;
//    private ArrayList<ArtistaDTO> artistas;
//
//    private ServicioValidacion servicioValidacion;
//
//    public ArtistaDAO (ArtistaDTO artista){
//        this.artista = artista;
//        this.artistas = new ArrayList<>();
//        this.servicioValidacion = new ServicioValidacion();
//    }
//
//    public void registrarArtista(String nombre, List<Genero> generos, String biografia, byte[] imagen) {
//        try (Connection conexion = ConexionBD.getConexion()) {
//            // 1. Insertar en la tabla Artista
//            String sql = "INSERT INTO Artista(nombre, biografia, imagen) VALUES (?, ?, ?)";
//            PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
//            ps.setString(1, nombre);
//            ps.setString(2, biografia);
//            ps.setBytes(3, imagen);
//            ps.executeUpdate();
//
//            // 2. Obtener el ID generado automáticamente
//            ResultSet rs = ps.getGeneratedKeys();
//            int idArtista = -1;
//            if (rs.next()) {
//                idArtista = rs.getInt(1);
//            }
//
//            // 3. Insertar en tabla puente Artista_Genero
//            sql = "INSERT INTO Artista_Genero(id_artista, id_genero) VALUES (?, ?)";
//            ps = conexion.prepareStatement(sql);
//            for (Genero genero : generos) {
//                ps.setInt(1, idArtista);
//                ps.setInt(2, genero.getId()); // ← asegúrate de que Genero tiene el método getId()
//                ps.addBatch();
//            }
//            ps.executeBatch();
//
//            System.out.println("Artista registrado con éxito.");
//        } catch (SQLException e) {
//            System.err.println("Error al registrar artista: " + e.getMessage());
//        }
//    }
//
//    public void actualizarArtista (ArtistaDTO artistaAActualizar){
//        for(int i = 0; i < artistas.size(); i++){
//            ArtistaDTO artistaActual = artistas.get(i);
//            if(artistaActual.getId() == artistaAActualizar.getId()){
//                artistaActual.setNombre(artistaAActualizar.getNombre());
//                artistaActual.setGeneros(artistaAActualizar.getGenero());
//                artistaActual.setBiografia(artistaAActualizar.getBiografia());
//                artistaActual.setImagen(artistaAActualizar.getImagen());
//
//            }
//        }
//    }
//    public void eliminarArtista (ArtistaDTO artista){
//        /*
//        if (servicioValidacion.tieneElementosAsociados(artista)) {
//            System.out.println(" No se puede eliminar: tiene elementos asociados.");
//        } else {
//            artistas.remove(artista);
//            System.out.println("Artista eliminado con éxito.");
//        }*/
//
//
//        //implementar el metodo de tieneElementosAsociados
//        artistas.remove(artista);
//
//    }
//
//    public ArrayList<ArtistaDTO> buscarArtistas() {
//        return artistas;
//    }
//
//    public ArtistaDTO buscarArtistaID(ArtistaDTO artista) {
//        return artista;
//
//    }
}
