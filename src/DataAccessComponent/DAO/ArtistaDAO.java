package DataAccessComponent.DAO;
import java.util.ArrayList;
import DataAccessComponent.DTO.Artista;

public class ArtistaDAO {

    protected Artista artista;
    private ArrayList<Artista> artistas;

    public ArtistaDAO (Artista artista){
        this.artista = artista;
        this.artistas = new ArrayList<>();
    }

    public void registrarArtista (Artista artista){
        //implementar el metodo de esNombreUnico
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
