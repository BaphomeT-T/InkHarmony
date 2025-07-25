package BusinessLogic;

import java.util.List;

public interface Recomendador<T> {
    List<T> recomendar();
}