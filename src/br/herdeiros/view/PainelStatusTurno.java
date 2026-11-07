package br.herdeiros.view;

import br.herdeiros.controller.JogoController;
import br.herdeiros.observer.IndicesEstado;
import br.herdeiros.observer.ObservadoIF;
import br.herdeiros.observer.ObservadorIF;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Painel de status do turno (jogador da vez, posição, votos e movimentos restantes).
 * Observador do Model: lê o estado exclusivamente via get(i) ao ser notificado.
 */
public class PainelStatusTurno extends JPanel implements ObservadorIF {

    private String nomeJogador = "Aguardando";
    private String nomeCor = null;
    private String localizacao = "-";
    private int votos = 0;
    private int movimentosRestantes = 0;

    public PainelStatusTurno() {
        setPreferredSize(new Dimension(270, 160));
        setBackground(Color.WHITE);
        JogoController.getInstance().registra(this);
    }

    @Override
    public void notify(ObservadoIF o) {
        JogoController ctrl = JogoController.getInstance();
        int vez = o.get(IndicesEstado.JOGADOR_DA_VEZ);
        if (vez >= 0) {
            String nome = ctrl.getNomeJogador(vez);
            nomeJogador = nome != null ? nome : "Aguardando";
            nomeCor = ctrl.getNomeCor(o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_COR)));
            String loc = IndicesEstado.nomeLocalidade(o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_LOCALIDADE)));
            localizacao = loc != null ? loc : "-";
            votos = o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_VOTOS));
        } else {
            nomeJogador = "Aguardando";
            nomeCor = null;
            localizacao = "-";
            votos = 0;
        }
        movimentosRestantes = o.get(IndicesEstado.MOVIMENTOS_RESTANTES);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Faixa de cabeçalho colorida com a cor do jogador da vez
        g2d.setColor(CoresView.obterCorAwt(nomeCor));
        g2d.fillRoundRect(0, 0, w, 40, 10, 10);

        g2d.setColor(CoresView.usaTextoEscuro(nomeCor) ? Color.BLACK : Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Vez de: " + nomeJogador, 15, 25);

        // Corpo informativo do status
        g2d.setColor(new Color(245, 245, 250));
        g2d.fillRoundRect(0, 45, w, h - 45, 10, 10);
        g2d.setColor(new Color(200, 200, 210));
        g2d.drawRoundRect(0, 45, w - 1, h - 46, 10, 10);

        g2d.setColor(new Color(40, 40, 40));
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("Posição atual: " + localizacao, 15, 70);
        g2d.drawString("Votos no Kurultai: " + votos, 15, 92);

        // Movimentos / Cavalos disponíveis para gastar no turno
        g2d.setFont(new Font("Arial", Font.BOLD, 13));
        if (movimentosRestantes > 0) {
            g2d.setColor(new Color(0, 130, 60));
            g2d.drawString("Movimentos restantes: " + movimentosRestantes, 15, 125);
        } else {
            g2d.setColor(new Color(180, 50, 50));
            g2d.drawString("Sem movimentos definidos", 15, 125);
        }

        g2d.setFont(new Font("Arial", Font.ITALIC, 10));
        g2d.setColor(Color.GRAY);
        g2d.drawString("(Cavalos da coluna ou Modo Testador)", 15, 145);
    }
}
