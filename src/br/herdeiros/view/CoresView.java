package br.herdeiros.view;

import java.awt.Color;

/**
 * Utilitário da View para converter o nome de uma cor (obtido via Controller)
 * em uma cor AWT. A View não conhece o enum de cores do Model.
 */
final class CoresView {

    private CoresView() {
    }

    static Color obterCorAwt(String nomeCor) {
        if (nomeCor == null) return Color.GRAY;
        switch (nomeCor) {
            case "AMARELO": return new Color(230, 190, 0);
            case "AZUL": return new Color(30, 144, 255);
            case "BRANCO": return new Color(240, 240, 240);
            case "LARANJA": return new Color(255, 140, 0);
            case "PRETO": return new Color(30, 30, 30);
            case "VERDE": return new Color(34, 139, 34);
            case "VERMELHO": return new Color(220, 20, 60);
            default: return Color.GRAY;
        }
    }

    static boolean usaTextoEscuro(String nomeCor) {
        return "BRANCO".equals(nomeCor) || "AMARELO".equals(nomeCor);
    }
}

