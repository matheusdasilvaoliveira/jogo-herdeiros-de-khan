package br.herdeiros.view;

import br.herdeiros.controller.JogoController;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * Painel lateral de controle da partida.
 * Cada ação aciona APENAS o Controller (JogoController.getInstance()); a atualização
 * visual decorrente chega pelas Views observadoras (padrão Observer).
 * Nenhum botão movimenta peões: a movimentação é feita por clique no tabuleiro.
 */
public class PainelControle extends JPanel {

    private PainelStatusTurno painelStatus;
    private JComboBox<Integer> comboMovimentos;
    private JComboBox<String> comboTributo;

    public PainelControle(final PainelTabuleiro painelTabuleiro) {
        setPreferredSize(new Dimension(300, 1050));
        setBackground(new Color(240, 240, 245));
        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 10));

        // Título principal do painel lateral
        JLabel lblTitulo = new JLabel("HERDEIROS DE KHAN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setForeground(new Color(40, 40, 40));
        add(lblTitulo);

        // 1. Painel com status visual do jogador da vez e movimentos restantes
        painelStatus = new PainelStatusTurno();
        painelStatus.setBorder(criarBorda("Turno / Estado da Jogada"));
        add(painelStatus);

        // 2. Encerrar turno
        JButton btnEncerrarTurno = criarBotao("Encerrar Turno", new Color(41, 128, 185), 45, 14);
        btnEncerrarTurno.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JogoController.getInstance().encerrarTurno();
            }
        });
        add(btnEncerrarTurno);

        // 2.1 Janelas de cartela, cartas e inventário (exibidas via Observer)
        JButton btnVerCartelas = criarBotao("Ver Meu Tabuleiro (Cartela)", new Color(52, 73, 94), 32, 12);
        btnVerCartelas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JogoController.getInstance().exibirCartela();
            }
        });
        add(btnVerCartelas);

        JButton btnVerCartas = criarBotao("Ver Minhas Cartas (Conselheiros)", new Color(142, 68, 173), 32, 12);
        btnVerCartas.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JogoController.getInstance().exibirCartas();
            }
        });
        add(btnVerCartas);

        JButton btnVerInventario = criarBotao("Ver Meu Inventário", new Color(39, 174, 96), 32, 12);
        btnVerInventario.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JogoController.getInstance().exibirInventario();
            }
        });
        add(btnVerInventario);

        // 3. Ações de regra na localização atual do jogador da vez
        JPanel painelAcoes = new JPanel();
        painelAcoes.setPreferredSize(new Dimension(260, 120));
        painelAcoes.setBackground(Color.WHITE);
        painelAcoes.setBorder(criarBorda("Ações na Posição Atual"));

        JButton btnYurt = criarBotao("Construir Yurt", new Color(211, 84, 0), 30, 12);
        btnYurt.setPreferredSize(new Dimension(240, 30));
        btnYurt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!JogoController.getInstance().construirYurt()) {
                    avisar("Não foi possível construir: é preciso ter 1 Yurt e o local deve estar livre.");
                }
            }
        });

        JButton btnSaquear = criarBotao("Saquear Cidade", new Color(192, 57, 43), 30, 12);
        btnSaquear.setPreferredSize(new Dimension(240, 30));
        btnSaquear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!JogoController.getInstance().saquearCidade()) {
                    avisar("Não foi possível saquear: é preciso estar numa cidade disponível e ter 1 Espada.");
                }
            }
        });
        painelAcoes.add(btnYurt);
        painelAcoes.add(btnSaquear);
        add(painelAcoes);

        // 4. Coleta de tributos
        JPanel painelTributo = new JPanel();
        painelTributo.setPreferredSize(new Dimension(260, 80));
        painelTributo.setBackground(Color.WHITE);
        painelTributo.setBorder(criarBorda("Coletar Tributo"));
        comboTributo = new JComboBox<String>(JogoController.getInstance().getNomesTributos());
        JButton btnColetar = new JButton("Coletar +1");
        btnColetar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JogoController.getInstance().coletarTributo(comboTributo.getSelectedIndex(), 1);
            }
        });
        painelTributo.add(comboTributo);
        painelTributo.add(btnColetar);
        add(painelTributo);

        // 5. Modo Testador (quantidade de movimentos/cavalos) e peão do Khan
        JPanel painelTestador = new JPanel();
        painelTestador.setPreferredSize(new Dimension(260, 120));
        painelTestador.setBackground(Color.WHITE);
        painelTestador.setBorder(criarBorda("Modo Testador (Cavalos / Movimento)"));

        Integer[] opcoesCavalos = {1, 2, 3, 4, 5, 6};
        comboMovimentos = new JComboBox<Integer>(opcoesCavalos);
        comboMovimentos.setPreferredSize(new Dimension(70, 28));

        JButton btnDefinirMovimentos = new JButton("Definir Movimentos");
        btnDefinirMovimentos.setPreferredSize(new Dimension(240, 30));
        btnDefinirMovimentos.setFont(new Font("Arial", Font.PLAIN, 12));
        btnDefinirMovimentos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object item = comboMovimentos.getSelectedItem();
                if (item instanceof Integer) {
                    JogoController.getInstance().definirMovimentos((Integer) item);
                }
            }
        });

        final JCheckBox chkKhan = new JCheckBox("Clique no mapa move o Khan");
        chkKhan.setOpaque(false);
        chkKhan.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                painelTabuleiro.setModoMoverKhan(chkKhan.isSelected());
            }
        });

        painelTestador.add(new JLabel("Cavalos:"));
        painelTestador.add(comboMovimentos);
        painelTestador.add(btnDefinirMovimentos);
        painelTestador.add(chkKhan);
        add(painelTestador);

        // 6. Salvar partida
        JButton btnSalvar = criarBotao("Salvar Jogo", new Color(127, 140, 141), 32, 12);
        btnSalvar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean ok = JogoController.getInstance().salvarPartida();
                avisar(ok ? "Partida salva com sucesso." : "Erro ao salvar a partida.");
            }
        });
        add(btnSalvar);
    }

    private JButton criarBotao(String texto, Color fundo, int altura, int tamanhoFonte) {
        JButton btn = new JButton(texto);
        btn.setPreferredSize(new Dimension(260, altura));
        btn.setFont(new Font("Arial", Font.BOLD, tamanhoFonte));
        btn.setBackground(fundo);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        return btn;
    }

    private TitledBorder criarBorda(String titulo) {
        return BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                titulo,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 11),
                new Color(90, 90, 90)
        );
    }

    private void avisar(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Herdeiros de Khan", JOptionPane.INFORMATION_MESSAGE);
    }
}
