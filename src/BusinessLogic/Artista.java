/*
|-----------------------------------------------|
| © 2025 EPN-FIS, Todos los derechos reservados |
| GR1SW                                         |
|-----------------------------------------------|
Autores: Doménica Cárdenas, Danna Morales, Salma Morales, Alisson Lita, Génesis Vásconez
Descripción: Clase de lógica de negocio (BL) que gestiona operaciones sobre Artistas dentro del sistema InkHarmony.
*/
package BusinessLogic;

import DataAccessComponent.DAO.ArtistaDAO;
import DataAccessComponent.DTO.CatalogoArtistas.ArtistaDTO;

import java.util.List;

public class Artista {
    private ArtistaDTO artista;
    private ArtistaDAO artistaDAO = new ArtistaDAO();

    public Artista(){

    }
    public List<ArtistaDTO> buscarTodo() throws Exception{
        return artistaDAO.buscarTodo();
    }
    public ArtistaDTO buscarPorId(int idArtista)throws Exception{
        artista = artistaDAO.buscarPorId(idArtista);
        return artista;
    }
    public boolean registrar(String nombre, List<Genero> generos,
                             String biografia, byte[] imagen) throws Exception{
        ArtistaDTO nuevoArtista = new ArtistaDTO(nombre,generos,biografia,imagen);
        return artistaDAO.registrar(nuevoArtista);
    }
    public boolean actualizar(ArtistaDTO artistaDTO) throws Exception {
        return artistaDAO.actualizar(artistaDTO);
    }

    public boolean eliminar(int idArtista) throws Exception {
        return artistaDAO.eliminar(idArtista);
    }
}
