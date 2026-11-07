package br.herdeiros.view;

import br.herdeiros.controller.JogoController;
import br.herdeiros.observer.IndicesEstado;
import br.herdeiros.observer.ObservadoIF;
import br.herdeiros.observer.ObservadorIF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * Janela para exibição estrita do tabuleiro individual (cartela) do jogador da vez.
 * Cada jogador só tem acesso à visualização da sua própria cartela no seu turno.
 * Desenhada com Graphics2D.drawImage() via Java2D.
 *
 * Observadora do Model: sua exibição (EXIBIR_CARTELA) e o jogador da vez são
 * resgatados por get(i) ao receber notify.
 */
public class JanelaCartelas extends JFrame implements ObservadorIF {

    private Map<String, Image> cacheCartelasPorNome;
    private Map<String, Image> cacheCartelasPorCor;
    private JLabel lblJogador;
    private PainelExibicaoCartela painelExibicao;

    public JanelaCartelas() {
        this.cacheCartelasPorNome = new HashMap<String, Image>();
        this.cacheCartelasPorCor = new HashMap<String, Image>();

        setTitle("Herdeiros de Khan - Meu Tabuleiro (Cartela)");
        setSize(950, 720);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JogoController.getInstance().fecharCartela();
            }
        });

        carregarCartelas();

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(240, 240, 245));
        painelPrincipal.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Barra Superior: Nome do jogador da vez e botão fechar
        JPanel painelTopo = new JPanel(new BorderLayout());
        painelTopo.setOpaque(false);

        lblJogador = new JLabel("Cartela do Jogador da Vez", JLabel.LEFT);
        lblJogador.setFont(new Font("Arial", Font.BOLD, 15));
        lblJogador.setForeground(new Color(40, 40, 50));

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setFont(new Font("Arial", Font.BOLD, 12));
        btnFechar.setBackground(new Color(127, 140, 141));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFocusPainted(false);
        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JogoController.getInstance().fecharCartela();
            }
        });

        painelTopo.add(lblJogador, BorderLayout.WEST);
        painelTopo.add(btnFechar, BorderLayout.EAST);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        painelExibicao = new PainelExibicaoCartela();
        painelPrincipal.add(painelExibicao, BorderLayout.CENTER);

        add(painelPrincipal);
        JogoController.getInstance().registra(this);
    }

    private void carregarCartelas() {
        carregarPorNomeECor("altani", "VERMELHO", "img/cartelas/cartela_altani.jpg");
        carregarPorNomeECor("chagatai", "AZUL", "img/cartelas/cartela_chagatai.jpg");
        carregarPorNomeECor("jochi", "VERDE", "img/cartelas/cartela_jochi.jpg");
        carregarPorNomeECor("ogedei", "AMARELO", "img/cartelas/cartela_ogedei.jpg");
        carregarPorNomeECor("tolui", "LARANJA", "img/cartelas/cartela_tolui.jpg");
    }

    private void carregarPorNomeECor(String nomeHerdeiro, String cor, String caminho) {
        try {
            File f = new File(caminho);
            if (f.exists()) {
                Image img = ImageIO.read(f);
                cacheCartelasPorNome.put(nomeHerdeiro.toLowerCase(), img);
                if (cor != null) {
                    cacheCartelasPorCor.put(cor, img);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar cartela " + caminho + ": " + e.getMessage());
        }
    }

    @Override
    public void notify(ObservadoIF o) {
        JogoController ctrl = JogoController.getInstance();
        int vez = o.get(IndicesEstado.JOGADOR_DA_VEZ);
        String nome = null;
        String cor = null;
        if (vez >= 0) {
            nome = ctrl.getNomeJogador(vez);
            cor = ctrl.getNomeCor(o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_COR)));
        }
        lblJogador.setText("Tabuleiro / Cartela de: " + nome + " (" + cor + ")");
        painelExibicao.setJogadorAtual(nome, cor);

        boolean exibir = o.get(IndicesEstado.EXIBIR_CARTELA) == 1;
        if (exibir && !isVisible()) {
            setVisible(true);
            toFront();
        } else if (!exibir && isVisible()) {
            setVisible(false);
        }
    }

    private class PainelExibicaoCartela extends JPanel {
        private String nomeHerdeiro;
        private String corAtual;

        public PainelExibicaoCartela() {
            setBackground(new Color(230, 230, 235));
        }

        public void setJogadorAtual(String nomeHerdeiro, String cor) {
            this.nomeHerdeiro = nomeHerdeiro;
            this.corAtual = cor;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Image img = null;
            if (nomeHerdeiro != null) {
                img = cacheCartelasPorNome.get(nomeHerdeiro.toLowerCase().trim());
            }
            if (img == null && corAtual != null) {
                img = cacheCartelasPorCor.get(corAtual);
            }

            if (img != null) {
                int painelW = getWidth();
                int painelH = getHeight();
                int imgW = img.getWidth(this);
                int imgH = img.getHeight(this);

                double ratio = Math.min((double) painelW / imgW, (double) painelH / imgH);
                int targetW = (int) (imgW * ratio);
                int targetH = (int) (imgH * ratio);
                int x = (painelW - targetW) / 2;
                int y = (painelH - targetH) / 2;

                g2d.drawImage(img, x, y, targetW, targetH, this);
            } else {
                g2d.setColor(Color.DARK_GRAY);
                g2d.setFont(new Font("Arial", Font.BOLD, 16));
                g2d.drawString("Cartela não encontrada para o herdeiro " + nomeHerdeiro, 30, 50);
            }
        }
    }
}
