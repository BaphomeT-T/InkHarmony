package BusinessLogic;

import java.util.List;

public abstract class FiltroRecomendador<T> implements Recomendador<T> {
    protected final Recomendador<T> siguiente;

    public FiltroRecomendador(Recomendador<T> siguiente) {
        this.siguiente = siguiente;
    }

    @Override
    public List<T> recomendar() {
        List<T> entrada = (siguiente != null) ? siguiente.recomendar() : List.of();
        return filtrar(entrada);
    }

    protected abstract List<T> filtrar(List<T> elementos);
}