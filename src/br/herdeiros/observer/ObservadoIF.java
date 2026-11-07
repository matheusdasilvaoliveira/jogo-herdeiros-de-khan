package br.herdeiros.observer;

/**
 * Interface do objeto observado (padrão Observer - Capítulo 17).
 * O observado mantém a lista de observadores e, a cada mudança de estado,
 * chama notify(this) em cada um deles. Os observadores resgatam o estado
 * exclusivamente através de get(i), usando os índices definidos em {@link IndicesEstado}.
 */
public interface ObservadoIF {
    void add(ObservadorIF o);

    void remove(ObservadorIF o);

    int get(int i);
}

