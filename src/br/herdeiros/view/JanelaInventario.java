package br.herdeiros.view;

import br.herdeiros.controller.JogoController;
import br.herdeiros.observer.IndicesEstado;
import br.herdeiros.observer.ObservadoIF;
import br.herdeiros.observer.ObservadorIF;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

/**
 * Janela para visualização estrita do inventário de tributos e votos do jogador da vez.
 * Cada jogador só pode consultar os seus próprios recursos.
 *
 * Observadora do Model: exibição (EXIBIR_INVENTARIO) e conteúdo resgatados via get(i).
 */
public class JanelaInventario extends JFrame implements ObservadorIF {

    private JPanel painelCartoes;

    public JanelaInventario() {
        setTitle("Herdeiros de Khan - Meu Inventário");
        setSize(650, 360);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JogoController.getInstance().fecharInventario();
            }
        });

        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBackground(new Color(245, 245, 250));
        painelPrincipal.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Meu Inventário de Recursos", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(40, 40, 50));
        painelPrincipal.add(lblTitulo, BorderLayout.NORTH);

        painelCartoes = new JPanel();
        painelCartoes.setLayout(new BorderLayout());
        painelCartoes.setOpaque(false);
        painelPrincipal.add(painelCartoes, BorderLayout.CENTER);

        JButton btnFechar = new JButton("Fechar");
        btnFechar.setPreferredSize(new Dimension(120, 36));
        btnFechar.setFont(new Font("Arial", Font.BOLD, 13));
        btnFechar.setBackground(new Color(127, 140, 141));
        btnFechar.setForeground(Color.WHITE);
        btnFechar.setFocusPainted(false);
        btnFechar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JogoController.getInstance().fecharInventario();
            }
        });

        JPanel painelBotao = new JPanel(new FlowLayout(FlowLayout.CENTER));
        painelBotao.setOpaque(false);
        painelBotao.add(btnFechar);
        painelPrincipal.add(painelBotao, BorderLayout.SOUTH);

        add(painelPrincipal);
        JogoController.getInstance().registra(this);
    }

    @Override
    public void notify(ObservadoIF o) {
        int vez = o.get(IndicesEstado.JOGADOR_DA_VEZ);
        painelCartoes.removeAll();
        if (vez >= 0) {
            JogoController ctrl = JogoController.getInstance();
            String nome = ctrl.getNomeJogador(vez);
            String cor = ctrl.getNomeCor(o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_COR)));
            String loc = IndicesEstado.nomeLocalidade(o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_LOCALIDADE)));
            int votos = o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_VOTOS));
            int espadas = o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_ESPADAS));
            int yurts = o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_YURTS));
            int moedas = o.get(IndicesEstado.jogador(vez, IndicesEstado.CAMPO_MOEDAS));
            montarCartao(nome, cor, loc, votos, espadas, yurts, moedas);
        }
        painelCartoes.revalidate();
        painelCartoes.repaint();

        boolean exibir = o.get(IndicesEstado.EXIBIR_INVENTARIO) == 1;
        if (exibir && !isVisible()) {
            setVisible(true);
            toFront();
        } else if (!exibir && isVisible()) {
            setVisible(false);
        }
    }

    private void montarCartao(String nome, String cor, String loc, int votos, int espadas, int yurts, int moedas) {
        Color corAwt = CoresView.obterCorAwt(cor);
        JPanel card = new JPanel(new GridLayout(4, 1, 10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(corAwt, 2),
                        nome + " (" + cor + ")",
                        TitledBorder.LEFT,
                        TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 14),
                        corAwt
                ),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel lblPos = new JLabel("Posição no Mapa: " + (loc != null ? loc : "-"));
        lblPos.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblVotos = new JLabel("Votos no Conselho Kurultai: " + votos);
        lblVotos.setFont(new Font("Arial", Font.BOLD, 14));

        JLabel lblTrib1 = new JLabel("Espadas para Saques: " + espadas + "    |    Yurts Disponíveis: " + yurts);
        lblTrib1.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel lblTrib2 = new JLabel("Moedas de Tributo: " + moedas);
        lblTrib2.setFont(new Font("Arial", Font.PLAIN, 14));

        card.add(lblPos);
        card.add(lblVotos);
        card.add(lblTrib1);
        card.add(lblTrib2);

        painelCartoes.add(card, BorderLayout.CENTER);
    }
}
