package br.herdeiros.view;

import br.herdeiros.controller.JogoController;
import br.herdeiros.observer.IndicesEstado;
import br.herdeiros.observer.ObservadoIF;
import br.herdeiros.observer.ObservadorIF;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Painel responsável por renderizar o tabuleiro, cidades, tesouros e peões
 * OBRIGATORIAMENTE via Java2D (Graphics2D.drawImage), sem componentes JLabel/JButton.
 *
 * Observador do Model: ao receber notify, resgata as posições dos peões e os tesouros
 * das cidades através de get(i) e se redesenha. A movimentação do peão é feita
 * exclusivamente por clique do mouse sobre a localidade de destino.
 */
public class PainelTabuleiro extends JPanel implements ObservadorIF {

    private static final int QTD_CIDADES_MAX = IndicesEstado.LOCALIDADES.length;

    private Image imagemTabuleiro;
    private Map<String, Image> imagensPeoes;
    private boolean modoMoverKhan = false;

    // Estado resgatado do observado (via get)
    private int qtdJogadores = 0;
    private int[] corJogador = new int[0];
    private int[] localJogador = new int[0];
    private int[] tesourosCidade = new int[QTD_CIDADES_MAX];
    private int[] localCidade = new int[QTD_CIDADES_MAX];
    private int localKhan = -1;

    public PainelTabuleiro() {
        this.imagensPeoes = new HashMap<String, Image>();
        carregarImagens();

        // Tratamento de eventos de clique do mouse SEM expressao lambda (requisito formal)
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                String destino = MapaCoordenadas.identificarLocalizacaoClicada(
                        e.getX(), e.getY(), getWidth(), getHeight());
                JogoController.getInstance().localidadeClicada(destino, modoMoverKhan);
            }
        });

        JogoController.getInstance().registra(this);
    }

    public void setModoMoverKhan(boolean ativo) {
        this.modoMoverKhan = ativo;
    }

    @Override
    public void notify(ObservadoIF o) {
        qtdJogadores = Math.max(0, o.get(IndicesEstado.QTD_JOGADORES));
        corJogador = new int[qtdJogadores];
        localJogador = new int[qtdJogadores];
        for (int j = 0; j < qtdJogadores; j++) {
            corJogador[j] = o.get(IndicesEstado.jogador(j, IndicesEstado.CAMPO_COR));
            localJogador[j] = o.get(IndicesEstado.jogador(j, IndicesEstado.CAMPO_LOCALIDADE));
        }
        for (int c = 0; c < QTD_CIDADES_MAX; c++) {
            tesourosCidade[c] = o.get(IndicesEstado.cidade(c, IndicesEstado.CAMPO_TESOUROS));
            localCidade[c] = o.get(IndicesEstado.cidade(c, IndicesEstado.CAMPO_LOCALIDADE_CIDADE));
        }
        localKhan = o.get(IndicesEstado.LOCALIDADE_KHAN);
        repaint();
    }

    private void carregarImagens() {
        try {
            imagemTabuleiro = ImageIO.read(new File("img/tabuleiro.jpg"));
        } catch (IOException e) {
            System.err.println("Erro ao carregar a imagem do tabuleiro: " + e.getMessage());
        }

        String[] cores = {"AMARELO", "AZUL", "BRANCO", "LARANJA", "PRETO", "VERDE", "VERMELHO"};
        for (String cor : cores) {
            carregarPeao(cor, "img/peoes/peao_" + cor.toLowerCase() + ".png");
        }
    }

    private void carregarPeao(String cor, String caminho) {
        try {
            File f = new File(caminho);
            if (f.exists()) {
                imagensPeoes.put(cor, ImageIO.read(f));
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar imagem do peão " + cor + ": " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int largura = getWidth();
        int altura = getHeight();

        // 1. Desenha o tabuleiro com Java2D
        if (imagemTabuleiro != null) {
            g2d.drawImage(imagemTabuleiro, 0, 0, largura, altura, this);
        }

        // 2. Destaca cidades reveladas/disponíveis no tabuleiro com tesouros
        desenharCidadesDisponiveis(g2d, largura, altura);

        // 3. Desenha o peão do Khan e os peões dos jogadores usando Graphics2D
        desenharKhan(g2d, largura, altura);
        desenharPeoes(g2d, largura, altura);
    }

    private void desenharCidadesDisponiveis(Graphics2D g2d, int largura, int altura) {
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        for (int c = 0; c < QTD_CIDADES_MAX; c++) {
            if (tesourosCidade[c] < 0) continue; // cidade não disponível
            String nome = IndicesEstado.nomeLocalidade(localCidade[c]);
            Point p = MapaCoordenadas.getCoordenadaTela(nome, largura, altura);
            if (p != null) {
                g2d.setColor(new Color(255, 215, 0, 220));
                g2d.fillRoundRect(p.x - 30, p.y + 20, 60, 20, 8, 8);
                g2d.setColor(Color.BLACK);
                g2d.drawRoundRect(p.x - 30, p.y + 20, 60, 20, 8, 8);
                g2d.drawString("Tes: " + tesourosCidade[c], p.x - 22, p.y + 35);
            }
        }
    }

    private void desenharKhan(Graphics2D g2d, int largura, int altura) {
        Point p = MapaCoordenadas.getCoordenadaTela(IndicesEstado.nomeLocalidade(localKhan), largura, altura);
        if (p == null) return;
        int t = 30;
        g2d.setColor(new Color(120, 0, 0));
        g2d.fillRect(p.x - t / 2, p.y - t - 20, t, t);
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("K", p.x - 5, p.y - 20 - t / 2 + 5);
    }

    private void desenharPeoes(Graphics2D g2d, int largura, int altura) {
        int tamanhoPeao = 36;
        int raioOffset = 18;

        for (int j = 0; j < qtdJogadores; j++) {
            String loc = IndicesEstado.nomeLocalidade(localJogador[j]);
            Point p = MapaCoordenadas.getCoordenadaTela(loc, largura, altura);
            if (p == null) continue;

            // Distribui os peões ligeiramente para não ficarem sobrepostos na mesma casa
            int offsetX = (int) (raioOffset * Math.cos(j * (2 * Math.PI / 4)));
            int offsetY = (int) (raioOffset * Math.sin(j * (2 * Math.PI / 4)));
            int xPeao = p.x + offsetX - (tamanhoPeao / 2);
            int yPeao = p.y + offsetY - (tamanhoPeao / 2);

            String nomeCor = JogoController.getInstance().getNomeCor(corJogador[j]);
            Image imgPeao = imagensPeoes.get(nomeCor);
            if (imgPeao != null) {
                g2d.drawImage(imgPeao, xPeao, yPeao, tamanhoPeao, tamanhoPeao, this);
            } else {
                // Fallback em caso de imagem ausente
                g2d.setColor(CoresView.obterCorAwt(nomeCor));
                g2d.fillOval(xPeao, yPeao, tamanhoPeao, tamanhoPeao);
                g2d.setColor(Color.BLACK);
                g2d.drawOval(xPeao, yPeao, tamanhoPeao, tamanhoPeao);
            }
        }
    }
}
