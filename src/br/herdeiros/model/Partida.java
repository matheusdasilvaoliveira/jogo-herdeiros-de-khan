package br.herdeiros.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ponto de entrada (API / Façade) do componente Model para gerenciar partidas de Herdeiros de Khan.
 * Centraliza as regras de negócio, turno, movimentação, tributos e saques de cidades.
 */
public class Partida {
    private Map<Cor, Jogador> jogadores;
    private List<Cor> ordemTurnos;
    private int indiceTurnoAtual;
    private Tabuleiro tabuleiro;
    private BaralhoDeCidades baralho;
    private Jogador jogadorAtual;

    public Partida() {
        this.jogadores = new HashMap<>();
        this.ordemTurnos = new ArrayList<>();
        this.indiceTurnoAtual = 0;
        this.baralho = new BaralhoDeCidades();
        // Inicializa o tabuleiro com as 3 cidades iniciais sacadas do baralho
        this.tabuleiro = new Tabuleiro(baralho.sacarCidadesIniciais());
    }

    /**
     * Cadastra um novo jogador na partida.
     */
    public void adicionarJogador(String nome, Cor cor, int quantidadeDescendentes) {
        if (cor == null || nome == null || this.jogadores.containsKey(cor)) {
            return;
        }
        Jogador novoJogador = new Jogador(nome, cor, quantidadeDescendentes);
        this.jogadores.put(cor, novoJogador);
        this.ordemTurnos.add(cor);
        
        if (this.jogadorAtual == null) {
            this.jogadorAtual = novoJogador;
        }
    }

    /**
     * Regra oficial de Herdeiros de Khan: o jogador com a maior quantidade de descendentes começa.
     * @return a cor do jogador que iniciará a partida, ou null se não houver jogadores.
     */
    public Cor definirPrimeiroJogadorPorDescendentes() {
        if (this.jogadores.isEmpty()) {
            return null;
        }

        Jogador herdeiroMaisDescendentes = null;
        for (Jogador j : this.jogadores.values()) {
            if (herdeiroMaisDescendentes == null || j.getQuantidadeDescendentes() > herdeiroMaisDescendentes.getQuantidadeDescendentes()) {
                herdeiroMaisDescendentes = j;
            }
        }

        this.jogadorAtual = herdeiroMaisDescendentes;
        this.indiceTurnoAtual = this.ordemTurnos.indexOf(herdeiroMaisDescendentes.getCor());
        return herdeiroMaisDescendentes.getCor();
    }

    /**
     * Passa a vez para o próximo jogador da rodada.
     */
    public Cor passarTurno() {
        if (this.ordemTurnos.isEmpty()) {
            return null;
        }
        this.indiceTurnoAtual = (this.indiceTurnoAtual + 1) % this.ordemTurnos.size();
        Cor proximaCor = this.ordemTurnos.get(this.indiceTurnoAtual);
        this.jogadorAtual = this.jogadores.get(proximaCor);
        if (this.jogadorAtual != null && this.jogadorAtual.getMeuPeao() != null) {
            this.jogadorAtual.getMeuPeao().iniciarTurno();
        }
        return proximaCor;
    }

    /**
     * Move o peão do jogador atual para um território vizinho válido.
     * @param destino nome da localização de destino.
     * @return true se o movimento foi válido e realizado, false caso contrário.
     */
    public boolean moverJogadorAtual(String destino) {
        if (this.jogadorAtual == null || destino == null) {
            return false;
        }
        String origem = this.jogadorAtual.getMeuPeao().getLocalizacaoAtual();
        if (this.tabuleiro.saoVizinhas(origem, destino)) {
            this.jogadorAtual.getMeuPeao().mover(destino);
            return true;
        }
        return false;
    }

    /**
     * Constrói um assentamento (Yurt) na localização informada, gastando 1 tributo de Yurt do jogador atual.
     */
    public boolean construirYurtJogadorAtual(String localizacao) {
        if (this.jogadorAtual == null || localizacao == null) {
            return false;
        }
        if (this.jogadorAtual.getQuantidadeTributo(TipoTributo.YURT) < 1) {
            return false;
        }
        boolean construiu = this.tabuleiro.construirYurt(localizacao, this.jogadorAtual.getCor());
        if (construiu) {
            this.jogadorAtual.removerTributo(TipoTributo.YURT, 1);
            return true;
        }
        return false;
    }

    /**
     * Coleta tributos para o jogador da cor especificada.
     */
    public void coletarTributo(Cor cor, TipoTributo tipo, int quantidade) {
        Jogador j = this.jogadores.get(cor);
        if (j != null && tipo != null && quantidade > 0) {
            j.adicionarTributo(tipo, quantidade);
        }
    }

    /**
     * Saqueia um tesouro de uma cidade disponível no tabuleiro.
     * Exige que o jogador atual possua pelo menos 1 espada.
     * Concede 1 voto no Kurultai pelo saque. Caso a cidade seja dominada (último tesouro),
     * concede +2 votos bônus e saca uma nova cidade do baralho para substituir no tabuleiro.
     */
    public boolean saquearCidade(NomeCidade nomeCidade) {
        if (this.jogadorAtual == null || nomeCidade == null) {
            return false;
        }
        Cidade cidade = this.tabuleiro.getCidade(nomeCidade);
        if (cidade == null || cidade.isConquistada()) {
            return false;
        }
        if (this.jogadorAtual.getQuantidadeTributo(TipoTributo.ESPADA) < 1) {
            return false;
        }

        boolean sucesso = cidade.saquearTesouro(this.jogadorAtual.getCor());
        if (sucesso) {
            this.jogadorAtual.removerTributo(TipoTributo.ESPADA, 1);
            this.jogadorAtual.ganharVotos(1);

            // Se o saque esgotou os tesouros, a cidade foi conquistada
            if (cidade.isConquistada()) {
                this.jogadorAtual.ganharVotos(2); // bônus de conquista
                Cidade novaCidade = this.baralho.sacarCidadeDoTopo();
                this.tabuleiro.substituirCidadeConquistada(cidade, novaCidade);
            }
            return true;
        }
        return false;
    }

    /**
     * Move o peão neutro do Khan pelo mapa.
     */
    public void moverKhan(String destino) {
        if (destino != null) {
            this.tabuleiro.getPeaoDoKhan().mover(destino);
        }
    }

    public String getLocalizacaoKhan() {
        return this.tabuleiro.getPeaoDoKhan().getLocalizacaoAtual();
    }

    public boolean jaVisitouNoTurnoAtual(String localizacao) {
        return this.jogadorAtual != null && this.jogadorAtual.getMeuPeao().jaVisitouNoTurno(localizacao);
    }

    public List<String> getCaminhoDoTurnoJogadorAtual() {
        if (this.jogadorAtual == null || this.jogadorAtual.getMeuPeao() == null) {
            return Collections.emptyList();
        }
        return this.jogadorAtual.getMeuPeao().getCaminhoDoTurno();
    }

    public Cor getConquistadorCidade(NomeCidade nomeCidade) {
        Cidade c = this.tabuleiro.getCidade(nomeCidade);
        return c != null ? c.getConquistador() : null;
    }

    // --- Métodos de consulta da API pública ---

    public Cor getCorJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getCor() : null;
    }

    public String getNomeJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getNome() : null;
    }

    public int getVotosJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getVotosKurultai() : 0;
    }

    public String getLocalizacaoJogadorAtual() {
        return this.jogadorAtual != null ? this.jogadorAtual.getMeuPeao().getLocalizacaoAtual() : null;
    }

    public String getNomeJogador(Cor cor) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getNome() : null;
    }

    public int getVotosJogador(Cor cor) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getVotosKurultai() : 0;
    }

    public int getTributosJogador(Cor cor, TipoTributo tipo) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getQuantidadeTributo(tipo) : 0;
    }

    public String getLocalizacaoJogador(Cor cor) {
        Jogador j = this.jogadores.get(cor);
        return j != null ? j.getMeuPeao().getLocalizacaoAtual() : null;
    }

    public int getQuantidadeJogadores() {
        return this.jogadores.size();
    }

    public Set<Cor> getCoresJogadores() {
        return Collections.unmodifiableSet(this.jogadores.keySet());
    }

    public boolean isCidadeDisponivel(NomeCidade nomeCidade) {
        return this.tabuleiro.getCidade(nomeCidade) != null;
    }

    public int getTesourosCidade(NomeCidade nomeCidade) {
        Cidade c = this.tabuleiro.getCidade(nomeCidade);
        return c != null ? c.getQuantidadeTesouros() : 0;
    }

    public boolean isCidadeConquistada(NomeCidade nomeCidade) {
        Cidade c = this.tabuleiro.getCidade(nomeCidade);
        return c != null && c.isConquistada();
    }

    public Set<String> getVizinhosLocalizacao(String localizacao) {
        return this.tabuleiro.getVizinhos(localizacao);
    }

    public boolean temYurtNaLocalizacao(String localizacao) {
        return this.tabuleiro.temYurt(localizacao);
    }

    public Cor getDonoYurtNaLocalizacao(String localizacao) {
        return this.tabuleiro.getDonoYurt(localizacao);
    }

    // --- Métodos de acesso para classes do pacote de modelo e testes unitários ---

    Jogador getJogador(Cor cor) {
        return this.jogadores.get(cor);
    }

    Map<Cor, Jogador> getJogadores() {
        return Collections.unmodifiableMap(jogadores);
    }

    Tabuleiro getTabuleiro() {
        return tabuleiro;
    }

    BaralhoDeCidades getBaralho() {
        return baralho;
    }

    Jogador getJogadorAtual() {
        return jogadorAtual;
    }

    public void definirJogadorAtual(Jogador jogador) {
        if (jogador != null && this.jogadores.containsKey(jogador.getCor())) {
            this.jogadorAtual = jogador;
            this.indiceTurnoAtual = this.ordemTurnos.indexOf(jogador.getCor());
        }
    }
}