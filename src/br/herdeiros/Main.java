package br.herdeiros;

import br.herdeiros.controller.JogoController;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Toda a sequência de eventos da partida é organizada pelo Controller (Singleton)
                JogoController.getInstance().iniciar();
            }
        });
    }
}
