package DataAccessComponent.DAO;

public class ArtistaDAO {

    protected Artista artista;
    private ArrayList<Artista> artistas;

    public ArtistaDAO (Artista artista){
        this.artista = artista;
        this.artistas = new ArrayList<>();
    }

    public void registrarArtista (Artista artista){

    }
    public void actualizarArtista (Artista artista){

    }
    public void eliminarArtista (Artista artista){

    }

    public ArrayList<Artista> buscarArtistas() {
        return artistas;
    }

    public Artista buscarArtistaID(Artista artista) {
        return artista;
    }
}
