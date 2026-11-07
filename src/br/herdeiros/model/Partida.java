package br.herdeiros.model;

import br.herdeiros.observer.IndicesEstado;
import br.herdeiros.observer.ObservadoIF;
import br.herdeiros.observer.ObservadorIF;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Ponto de entrada (API / Façade) do componente Model para gerenciar partidas de Herdeiros de Khan.
 * Centraliza as regras de negócio, turno, movimentação, tributos e saques de cidades.
 *
 * Padrões aplicados:
 *  - Singleton: ponto único de acesso ao Model via {@link #getInstance()}.
 *  - Façade: única classe pública com operações do Model; as demais são package-private.
 *  - Observer: é o objeto observado ({@link ObservadoIF}); a cada mudança de estado chama
 *    notify(this) nos observadores, que leem o estado por get(i) (ver {@link IndicesEstado}).
 */
public class Partida implements ObservadoIF, Serializable {
    private static final long serialVersionUID = 1L;

    private static Partida instancia;

    private Map<Cor, Jogador> jogadores;
    private List<Cor> ordemTurnos;
    private int indiceTurnoAtual;
    private Tabuleiro tabuleiro;
    private BaralhoDeCidades baralho;
    private Jogador jogadorAtual;
    private int movimentosRestantes;

    // Estado de exibição das janelas (refletido nas Views via Observer)
    private transient boolean exibirCartela;
    private transient boolean exibirCartas;
    private transient boolean exibirInventario;

    private transient List<ObservadorIF> observadores = new ArrayList<ObservadorIF>();

    /** Construtor restrito ao pacote (Singleton). Visível ao pacote apenas para testes unitários. */
    Partida() {
        inicializarEstado();
    }

    public static synchronized Partida getInstance() {
        if (instancia == null) {
            instancia = new Partida();
        }
        return instancia;
    }

    private void inicializarEstado() {
        this.jogadores = new HashMap<>();
        this.ordemTurnos = new ArrayList<>();
        this.indiceTurnoAtual = 0;
        this.jogadorAtual = null;
        this.movimentosRestantes = 0;
        this.baralho = new BaralhoDeCidades();
        // Inicializa o tabuleiro com as 3 cidades iniciais sacadas do baralho
        this.tabuleiro = new Tabuleiro(baralho.sacarCidadesIniciais());
        this.exibirCartela = false;
        this.exibirCartas = false;
        this.exibirInventario = false;
    }

    /** Reinicia a partida mantendo os observadores registrados. */
    public void reiniciar() {
        inicializarEstado();
        notificarObservadores();
    }

    // --- Observer (ObservadoIF) ---

    @Override
    public void add(ObservadorIF o) {
        if (o != null && !observadores.contains(o)) {
            observadores.add(o);
        }
    }

    @Override
    public void remove(ObservadorIF o) {
        observadores.remove(o);
    }

    private void notificarObservadores() {
        List<ObservadorIF> copia = new ArrayList<ObservadorIF>(observadores);
        for (ObservadorIF o : copia) {
            o.notify(this);
        }
    }

    @Override
    public int get(int i) {
        switch (i) {
            case IndicesEstado.QTD_JOGADORES: return ordemTurnos.size();
            case IndicesEstado.JOGADOR_DA_VEZ: return jogadorAtual == null ? -1 : ordemTurnos.indexOf(jogadorAtual.getCor());
            case IndicesEstado.MOVIMENTOS_RESTANTES: return movimentosRestantes;
            case IndicesEstado.EXIBIR_CARTELA: return exibirCartela ? 1 : 0;
            case IndicesEstado.EXIBIR_CARTAS: return exibirCartas ? 1 : 0;
            case IndicesEstado.EXIBIR_INVENTARIO: return exibirInventario ? 1 : 0;
            case IndicesEstado.LOCALIDADE_KHAN: return IndicesEstado.indiceLocalidade(getLocalizacaoKhan());
            default: break;
        }
        if (i >= IndicesEstado.YURT_BASE) {
            String loc = IndicesEstado.nomeLocalidade(i - IndicesEstado.YURT_BASE);
            Cor dono = loc == null ? null : tabuleiro.getDonoYurt(loc);
            return dono == null ? -1 : dono.ordinal();
        }
        if (i >= IndicesEstado.CIDADE_BASE) {
            int rel = i - IndicesEstado.CIDADE_BASE;
            int ordinal = rel / IndicesEstado.CIDADE_PASSO;
            int campo = rel % IndicesEstado.CIDADE_PASSO;
            if (ordinal >= NomeCidade.values().length) return -1;
            NomeCidade nc = NomeCidade.values()[ordinal];
            Cidade c = tabuleiro.getCidade(nc);
            if (campo == IndicesEstado.CAMPO_TESOUROS) return c == null ? -1 : c.getQuantidadeTesouros();
            if (campo == IndicesEstado.CAMPO_LOCALIDADE_CIDADE) return IndicesEstado.indiceLocalidade(nc.getNomeFormatado());
            return -1;
        }
        if (i >= IndicesEstado.JOGADOR_BASE) {
            int rel = i - IndicesEstado.JOGADOR_BASE;
            int idx = rel / IndicesEstado.JOGADOR_PASSO;
            int campo = rel % IndicesEstado.JOGADOR_PASSO;
            if (idx >= ordemTurnos.size()) return -1;
            Jogador j = jogadores.get(ordemTurnos.get(idx));
            switch (campo) {
                case IndicesEstado.CAMPO_COR: return j.getCor().ordinal();
                case IndicesEstado.CAMPO_LOCALIDADE: return IndicesEstado.indiceLocalidade(j.getMeuPeao().getLocalizacaoAtual());
                case IndicesEstado.CAMPO_VOTOS: return j.getVotosKurultai();
                case IndicesEstado.CAMPO_ESPADAS: return j.getQuantidadeTributo(TipoTributo.ESPADA);
                case IndicesEstado.CAMPO_YURTS: return j.getQuantidadeTributo(TipoTributo.YURT);
                case IndicesEstado.CAMPO_MOEDAS: return j.getQuantidadeTributo(TipoTributo.MOEDA);
                case IndicesEstado.CAMPO_DESCENDENTES: return j.getQuantidadeDescendentes();
                case IndicesEstado.CAMPO_QTD_CARTAS: return Math.min(j.getCartas().size(), IndicesEstado.MAX_CARTAS);
                default:
                    int k = campo - IndicesEstado.CAMPO_CARTA_0;
                    if (k >= 0 && k < j.getCartas().size()) {
                        return IndicesEstado.codificarCarta(j.getCartas().get(k));
                    }
                    return -1;
            }
        }
        return -1;
    }

    // --- Dados textuais fixos (nomes), expostos à View apenas através do Controller ---

    public String getNomeJogadorPorIndice(int indice) {
        if (indice < 0 || indice >= ordemTurnos.size()) return null;
        return jogadores.get(ordemTurnos.get(indice)).getNome();
    }

    public static String getNomeCor(int ordinal) {
        Cor[] cores = Cor.values();
        return (ordinal >= 0 && ordinal < cores.length) ? cores[ordinal].name() : null;
    }

    /** Cadastra um jogador informando o nome da cor (usado pelo Controller, sem expor o enum à View). */
    public void adicionarJogador(String nome, String nomeCor, int quantidadeDescendentes) {
        try {
            adicionarJogador(nome, Cor.valueOf(nomeCor), quantidadeDescendentes);
        } catch (IllegalArgumentException | NullPointerException e) {
            // cor inválida: ignorada
        }
    }

    // --- Movimentação com cavalos (estado de turno pertence ao Model) ---

    public void definirMovimentos(int quantidade) {
        this.movimentosRestantes = Math.max(0, quantidade);
        notificarObservadores();
    }

    /**
     * Move o jogador da vez consumindo os movimentos (cavalos) disponíveis no turno.
     * @return true se o movimento foi realizado.
     */
    public boolean moverJogadorDaVez(String destino) {
        if (this.jogadorAtual == null || destino == null || movimentosRestantes <= 0) {
            return false;
        }
        String origem = getLocalizacaoJogadorAtual();
        if (origem == null || origem.equalsIgnoreCase(destino)) {
            return false;
        }
        int distancia = this.tabuleiro.calcularDistancia(origem, destino);
        if (distancia <= 0 || distancia > movimentosRestantes) {
            return false;
        }
        if (moverJogadorAtual(destino)) {
            movimentosRestantes -= distancia;
            notificarObservadores();
            return true;
        }
        return false;
    }

    // --- Regras acionadas pelo jogador da vez a partir da sua localização ---

    public boolean construirYurtNaLocalizacaoAtual() {
        return construirYurtJogadorAtual(getLocalizacaoJogadorAtual());
    }

    public boolean saquearCidadeNaLocalizacaoAtual() {
        String loc = getLocalizacaoJogadorAtual();
        for (NomeCidade nc : NomeCidade.values()) {
            if (nc.getNomeFormatado().equalsIgnoreCase(loc)) {
                return saquearCidade(nc);
            }
        }
        return false;
    }

    /** @param ordinalTipo índice do tipo de tributo (0 = ESPADA, 1 = YURT, 2 = MOEDA). */
    public void coletarTributoJogadorAtual(int ordinalTipo, int quantidade) {
        TipoTributo[] tipos = TipoTributo.values();
        if (jogadorAtual != null && ordinalTipo >= 0 && ordinalTipo < tipos.length) {
            coletarTributo(jogadorAtual.getCor(), tipos[ordinalTipo], quantidade);
        }
    }

    public static String[] getNomesTributos() {
        TipoTributo[] tipos = TipoTributo.values();
        String[] nomes = new String[tipos.length];
        for (int i = 0; i < tipos.length; i++) nomes[i] = tipos[i].name();
        return nomes;
    }

    // --- Exibição de janelas (mudanças de estado refletidas via Observer) ---

    public void setExibirCartela(boolean exibir) {
        this.exibirCartela = exibir;
        notificarObservadores();
    }

    public void setExibirCartas(boolean exibir) {
        this.exibirCartas = exibir;
        notificarObservadores();
    }

    public void setExibirInventario(boolean exibir) {
        this.exibirInventario = exibir;
        notificarObservadores();
    }

    // --- Persistência (salvar / continuar jogo) ---

    public boolean salvar(String caminho) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(caminho))) {
            out.writeObject(this);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar partida: " + e.getMessage());
            return false;
        }
    }

    public boolean carregar(String caminho) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(caminho))) {
            Partida salva = (Partida) in.readObject();
            this.jogadores = salva.jogadores;
            this.ordemTurnos = salva.ordemTurnos;
            this.indiceTurnoAtual = salva.indiceTurnoAtual;
            this.tabuleiro = salva.tabuleiro;
            this.baralho = salva.baralho;
            this.jogadorAtual = salva.jogadorAtual;
            this.movimentosRestantes = salva.movimentosRestantes;
            this.exibirCartela = false;
            this.exibirCartas = false;
            this.exibirInventario = false;
            notificarObservadores();
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar partida: " + e.getMessage());
            return false;
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.observadores = new ArrayList<ObservadorIF>();
    }

    /**
     * Cadastra um novo jogador na partida.
     */
    public void adicionarJogador(String nome, Cor cor, int quantidadeDescendentes) {
        if (cor == null || nome == null || this.jogadores.containsKey(cor)) {
            return;
        }
        Jogador novoJogador = new Jogador(nome, cor, quantidadeDescendentes);
        
        // Distribui as 2 cartas de conselheiro secreto iniciais (regra oficial de preparação, pág. 3)
        int idx = this.jogadores.size();
        int pasta = 3 + (idx % 6); // pastas cartas3 a cartas8
        novoJogador.adicionarCarta("cartas" + pasta + "-1");
        novoJogador.adicionarCarta("cartas" + pasta + "-2");

        this.jogadores.put(cor, novoJogador);
        this.ordemTurnos.add(cor);
        
        if (this.jogadorAtual == null) {
            this.jogadorAtual = novoJogador;
        }
        notificarObservadores();
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
        notificarObservadores();
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
        this.movimentosRestantes = 0;
        // Ao trocar de jogador, janelas privadas (cartela/cartas/inventário) passam a mostrar o novo jogador
        notificarObservadores();
        return proximaCor;
    }

    /**
     * Move o peão do jogador atual para a localização de destino.
     * @param destino nome da localização de destino.
     * @return true se o movimento foi válido e realizado, false caso contrário.
     */
    public boolean moverJogadorAtual(String destino) {
        if (this.jogadorAtual == null || destino == null) {
            return false;
        }
        String origem = this.jogadorAtual.getMeuPeao().getLocalizacaoAtual();
        if (origem.equals(destino)) {
            return false;
        }
        int dist = this.tabuleiro.calcularDistancia(origem, destino);
        if (dist > 0) {
            this.jogadorAtual.getMeuPeao().mover(destino);
            notificarObservadores();
            return true;
        }
        return false;
    }

    /**
     * Retorna o menor número de passos (distância no grafo) entre origem e destino.
     */
    public int calcularDistancia(String origem, String destino) {
        return this.tabuleiro.calcularDistancia(origem, destino);
    }

    /**
     * Retorna a lista de cartas em posse do jogador atual da vez.
     */
    public java.util.List<String> getCartasJogadorAtual() {
        if (this.jogadorAtual == null) {
            return java.util.Collections.emptyList();
        }
        return this.jogadorAtual.getCartas();
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
            notificarObservadores();
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
            notificarObservadores();
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
            notificarObservadores();
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
            notificarObservadores();
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
            notificarObservadores();
        }
    }
}