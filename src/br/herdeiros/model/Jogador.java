package br.herdeiros.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class Jogador {
    private String nome;
    private Cor cor;
    private Peao meuPeao;
    private int quantidadeDescendentes; // Define quem começa jogando
    private int votosKurultai; // Pontuação no tabuleiro
    private Map<TipoTributo, Integer> tributos;

    public Jogador(String nome, Cor cor, int quantidadeDescendentes) {
        this.nome = nome;
        this.cor = cor;
        this.quantidadeDescendentes = quantidadeDescendentes;
        this.meuPeao = new Peao(this.cor, "Karakorum"); // Todos começam em Karakorum
        this.votosKurultai = 0;
        
        // Inicializa o inventário de tributos com HashMap
        this.tributos = new HashMap<>();
        for (TipoTributo tipo : TipoTributo.values()) {
            this.tributos.put(tipo, 0);
        }
    }
    
    public void ganharVotos(int quantidade) {
        if (quantidade > 0) {
            this.votosKurultai += quantidade;
        }
    }

    public void adicionarTributo(TipoTributo tipo, int quantidade) {
        if (tipo != null && quantidade > 0) {
            this.tributos.put(tipo, this.tributos.getOrDefault(tipo, 0) + quantidade);
        }
    }

    public boolean removerTributo(TipoTributo tipo, int quantidade) {
        if (tipo == null || quantidade <= 0) {
            return false;
        }
        int atual = this.tributos.getOrDefault(tipo, 0);
        if (atual < quantidade) {
            return false;
        }
        this.tributos.put(tipo, atual - quantidade);
        return true;
    }

    public int getQuantidadeTributo(TipoTributo tipo) {
        if (tipo == null) return 0;
        return this.tributos.getOrDefault(tipo, 0);
    }

    public Map<TipoTributo, Integer> getTributos() {
        return Collections.unmodifiableMap(tributos);
    }
    
    public String getNome() {
        return nome;
    }

    public Cor getCor() {
        return cor;
    }

    public int getQuantidadeDescendentes() {
        return quantidadeDescendentes;
    }

    public int getVotosKurultai() {
        return votosKurultai;
    }

    public Peao getMeuPeao() {
        return meuPeao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jogador jogador = (Jogador) o;
        return cor == jogador.cor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cor);
    }
}

