package br.herdeiros.view;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 * Janela principal da aplicação (respeita o limite máximo de 1400x1050 pixels).
 * Cria os painéis e as janelas auxiliares; todas se registram como observadoras
 * do Model através do Controller e se atualizam sozinhas via notify/get.
 */
public class JanelaPrincipal extends JFrame {

    private PainelTabuleiro painelTabuleiro;
    private PainelControle painelControle;

    public JanelaPrincipal() {
        setTitle("Herdeiros de Khan");
        setSize(1400, 1020);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        painelTabuleiro = new PainelTabuleiro();
        painelControle = new PainelControle(painelTabuleiro);

        add(painelTabuleiro, BorderLayout.CENTER);
        add(painelControle, BorderLayout.EAST);

        // Janelas auxiliares: iniciam ocultas e são exibidas/ocultadas pelo Observer
        // conforme o estado EXIBIR_CARTELA / EXIBIR_CARTAS / EXIBIR_INVENTARIO do Model.
        new JanelaCartelas();
        new JanelaCartas();
        new JanelaInventario();
    }

    public void exibir() {
        setVisible(true);
    }
}
