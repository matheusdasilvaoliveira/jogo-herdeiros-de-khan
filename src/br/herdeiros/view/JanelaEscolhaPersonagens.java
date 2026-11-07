package br.herdeiros.view;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class JanelaEscolhaPersonagens extends JFrame {

    public static class DadosJogadorConfig {
        public String nome;
        public String cor; // nome da cor (resolvido pelo Controller); a View nao conhece o enum do Model
        public int descendentes;

        public DadosJogadorConfig(String nome, String cor, int descendentes) {
            this.nome = nome;
            this.cor = cor;
            this.descendentes = descendentes;
        }
    }

    public interface OuvinteConfiguracaoJogo {
        void aoConfirmarConfiguracao(List<DadosJogadorConfig> jogadores);
        void aoVoltarMenu();
    }

    private OuvinteConfiguracaoJogo ouvinte;
    private JComboBox<Integer> comboQtdJogadores;
    private JPanel painelCamposJogadores;
    private List<LinhaJogador> linhasJogadores;

    private static class LinhaJogador {
        JComboBox<String> comboNomeHerdeiro;
        JComboBox<String> comboCor;
        JSpinner spinnerDescendentes;
    }

    public JanelaEscolhaPersonagens(OuvinteConfiguracaoJogo ouvinte) {
        this.ouvinte = ouvinte;
        this.linhasJogadores = new ArrayList<LinhaJogador>();

        setTitle("Herdeiros de Khan - Escolha dos Personagens");
        setSize(850, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel painelPrincipal = new JPanel(new BorderLayout(15, 15)) {
            private java.awt.Image imgFundo;
            {
                try {
                    java.io.File f = new java.io.File("img/plano_de_fundo.jpg");
                    if (f.exists()) {
                        imgFundo = javax.imageio.ImageIO.read(f);
                    }
                } catch (Exception e) {}
            }
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (imgFundo != null) {
                    g.drawImage(imgFundo, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        painelPrincipal.setBackground(new Color(245, 245, 250));
        painelPrincipal.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Cabeçalho e seletor da quantidade de jogadores (2 a 5 participantes)
        JPanel painelTopo = new JPanel(new BorderLayout(10, 10));
        painelTopo.setOpaque(false);

        JLabel lblTitulo = new JLabel("Configuração dos Herdeiros", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitulo.setForeground(new Color(255, 255, 255));
        painelTopo.add(lblTitulo, BorderLayout.NORTH);

        JPanel painelSeletorQtd = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        painelSeletorQtd.setOpaque(false);
        JLabel lblQtd = new JLabel("Quantidade de Jogadores:");
        lblQtd.setFont(new Font("Arial", Font.BOLD, 14));
        lblQtd.setForeground(Color.WHITE);

        Integer[] opcoesQtd = {2, 3, 4, 5};
        comboQtdJogadores = new JComboBox<Integer>(opcoesQtd);
        comboQtdJogadores.setSelectedItem(3);
        comboQtdJogadores.setPreferredSize(new Dimension(80, 30));

        // Listener sem expressao lambda
        comboQtdJogadores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int qtd = (Integer) comboQtdJogadores.getSelectedItem();
                recriarCamposJogadores(qtd);
            }
        });

        painelSeletorQtd.add(lblQtd);
        painelSeletorQtd.add(comboQtdJogadores);
        painelTopo.add(painelSeletorQtd, BorderLayout.SOUTH);
        painelPrincipal.add(painelTopo, BorderLayout.NORTH);

        // Painel Central com os campos de cada jogador
        painelCamposJogadores = new JPanel();
        painelCamposJogadores.setLayout(new GridLayout(5, 1, 0, 10));
        painelCamposJogadores.setOpaque(false);
        painelPrincipal.add(painelCamposJogadores, BorderLayout.CENTER);

        // Painel Inferior com botões de Ação
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        painelBotoes.setOpaque(false);

        JButton btnVoltar = new JButton("Voltar ao Menu");
        btnVoltar.setPreferredSize(new Dimension(180, 42));
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 13));
        btnVoltar.setBackground(new Color(127, 140, 141));
        btnVoltar.setForeground(Color.WHITE);
        btnVoltar.setFocusPainted(false);

        JButton btnIniciar = new JButton("Iniciar Partida");
        btnIniciar.setPreferredSize(new Dimension(220, 42));
        btnIniciar.setFont(new Font("Arial", Font.BOLD, 14));
        btnIniciar.setBackground(new Color(39, 174, 96));
        btnIniciar.setForeground(Color.WHITE);
        btnIniciar.setFocusPainted(false);

        // Listeners dos botões inferiores sem lambda
        btnVoltar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (JanelaEscolhaPersonagens.this.ouvinte != null) {
                    JanelaEscolhaPersonagens.this.ouvinte.aoVoltarMenu();
                }
            }
        });

        btnIniciar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarEConfirmar();
            }
        });

        painelBotoes.add(btnVoltar);
        painelBotoes.add(btnIniciar);
        painelPrincipal.add(painelBotoes, BorderLayout.SOUTH);

        add(painelPrincipal);

        // Inicializa com a quantidade padrão (3 jogadores)
        recriarCamposJogadores(3);
    }

    private void recriarCamposJogadores(int qtd) {
        painelCamposJogadores.removeAll();
        linhasJogadores.clear();

        // 5 Herdeiros oficiais do jogo
        final String[] herdeirosOficiais = {"Altani", "Chagatai", "Jochi", "Ogedei", "Tolui"};
        String[] coresPadrao = {"VERMELHO", "AZUL", "VERDE", "AMARELO", "LARANJA"};
        int[] descendentesPadrao = {4, 2, 5, 3, 1};

        String[] todasCoresDisponiveis = {"VERMELHO", "AZUL", "VERDE", "AMARELO", "LARANJA", "PRETO"};

        for (int i = 0; i < qtd; i++) {
            JPanel cardJogador = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
            cardJogador.setBackground(Color.WHITE);
            cardJogador.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 210)),
                    "Jogador " + (i + 1),
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Arial", Font.BOLD, 12),
                    new Color(70, 70, 80)
            ));

            JComboBox<String> comboHerdeiro = new JComboBox<String>(herdeirosOficiais);
            comboHerdeiro.setSelectedIndex(i % herdeirosOficiais.length);
            comboHerdeiro.setPreferredSize(new Dimension(130, 28));

            JComboBox<String> comboCor = new JComboBox<String>(todasCoresDisponiveis);
            comboCor.setSelectedItem(coresPadrao[i % coresPadrao.length]);
            comboCor.setPreferredSize(new Dimension(110, 28));

            JSpinner spinDesc = new JSpinner(new SpinnerNumberModel(descendentesPadrao[i % descendentesPadrao.length], 0, 20, 1));
            spinDesc.setPreferredSize(new Dimension(60, 28));

            cardJogador.add(new JLabel("Herdeiro:"));
            cardJogador.add(comboHerdeiro);
            cardJogador.add(new JLabel("Cor do Peão:"));
            cardJogador.add(comboCor);
            cardJogador.add(new JLabel("Descendentes:"));
            cardJogador.add(spinDesc);

            LinhaJogador linha = new LinhaJogador();
            linha.comboNomeHerdeiro = comboHerdeiro;
            linha.comboCor = comboCor;
            linha.spinnerDescendentes = spinDesc;
            linhasJogadores.add(linha);

            painelCamposJogadores.add(cardJogador);
        }

        painelCamposJogadores.revalidate();
        painelCamposJogadores.repaint();
    }

    private void validarEConfirmar() {
        List<DadosJogadorConfig> listaFinal = new ArrayList<DadosJogadorConfig>();
        Set<String> coresEscolhidas = new HashSet<String>();
        Set<String> herdeirosEscolhidos = new HashSet<String>();

        for (int i = 0; i < linhasJogadores.size(); i++) {
            LinhaJogador linha = linhasJogadores.get(i);
            String nome = (String) linha.comboNomeHerdeiro.getSelectedItem();
            if (herdeirosEscolhidos.contains(nome)) {
                JOptionPane.showMessageDialog(this,
                        "Cada participante deve escolher um herdeiro diferente. Herdeiro repetido: " + nome,
                        "Herdeiros Conflitantes", JOptionPane.WARNING_MESSAGE);
                return;
            }
            herdeirosEscolhidos.add(nome);

            String cor = (String) linha.comboCor.getSelectedItem();
            if (coresEscolhidas.contains(cor)) {
                JOptionPane.showMessageDialog(this,
                        "Cada jogador deve escolher uma cor única para seu peão. Cor repetida: " + cor,
                        "Cores Conflitantes", JOptionPane.WARNING_MESSAGE);
                return;
            }
            coresEscolhidas.add(cor);

            int desc = (Integer) linha.spinnerDescendentes.getValue();
            listaFinal.add(new DadosJogadorConfig(nome, cor, desc));
        }

        if (ouvinte != null) {
            ouvinte.aoConfirmarConfiguracao(listaFinal);
        }
    }

    public void exibir() {
        setVisible(true);
    }
}
