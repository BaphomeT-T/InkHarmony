package DataAccessComponent.DAO.CatalogoArtistas;
import java.util.ArrayList;
import java.util.List;

import DataAccessComponent.DTO.CatalogoArtistas.Artista;
import DataAccessComponent.DTO.CatalogoArtistas.ServicioValidacion;
import DataAccessComponent.DTO.CatalogoCanciones.Genero;

public class ArtistaDAO {

    protected Artista artista;
    private ArrayList<Artista> artistas;

    private ServicioValidacion servicioValidacion;

    public ArtistaDAO (Artista artista){
        this.artista = artista;
        this.artistas = new ArrayList<>();
        this.servicioValidacion = new ServicioValidacion();
    }

    public void registrarArtista (int id, String nombre, List<Genero> generos, String biografia, String imagen ){
        Artista artistaARegistrar = new Artista(id, nombre, generos, biografia, imagen);
        //implementar el metodo de esNombreUnico para validar que nadie mas tenga ese nombre
        artistas.add(artista);

    }
    public void actualizarArtista (Artista artistaAActualizar){
        for(int i = 0; i < artistas.size(); i++){
            Artista artistaActual = artistas.get(i);
            if(artistaActual.getId() == artistaAActualizar.getId()){
                artistaActual.setNombre(artistaAActualizar.getNombre());
                artistaActual.setGeneros(artistaAActualizar.getGenero());
                artistaActual.setBiografia(artistaAActualizar.getBiografia());
                artistaActual.setImagen(artistaAActualizar.getImagen());

            }
        }


    }
    public void eliminarArtista (Artista artista){
        /*
        if (servicioValidacion.tieneElementosAsociados(artista)) {
            System.out.println(" No se puede eliminar: tiene elementos asociados.");
        } else {
            artistas.remove(artista);
            System.out.println("Artista eliminado con éxito.");
        }*/


        //implementar el metodo de tieneElementosAsociados
        artistas.remove(artista);

    }

    public ArrayList<Artista> buscarArtistas() {
        return artistas;
    }

    public Artista buscarArtistaID(Artista artista) {
        return artista;

    }
}
