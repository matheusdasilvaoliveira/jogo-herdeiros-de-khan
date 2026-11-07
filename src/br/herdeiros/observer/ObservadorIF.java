package br.herdeiros.observer;

/**
 * Interface do observador (padrão Observer - Capítulo 17).
 * Ao ser notificado, o observador chama o método get do observado
 * para obter as informações de estado de que precisa.
 */
public interface ObservadorIF {
    void notify(ObservadoIF o);
}

