package br.herdeiros.model;

import java.util.Objects;

class Cidade {
    private NomeCidade nome;
    private Regiao regiao;
    private int quantidadeTesouros;
    private boolean conquistada;
    private Cor conquistador;

    public Cidade(NomeCidade nome, Regiao regiao) {
        this.nome = nome;
        this.regiao = regiao;
        
        // Uma nova cidade revelada recebe 4 tesouros
        this.quantidadeTesouros = 4;
        this.conquistada = false;
        this.conquistador = null;
    }
    
    // Um Peão tentará saquear a cidade
    public boolean saquearTesouro() {
        return saquearTesouro(null);
    }

    public boolean saquearTesouro(Cor autorSaque) {
        if (conquistada || quantidadeTesouros <= 0) {
            return false; // Não há mais o que saquear
        }
        
        this.quantidadeTesouros--;
        
        // Se tomou o último tesouro, a cidade está conquistada pelo autor do último saque
        if (this.quantidadeTesouros == 0) {
            this.conquistada = true;
            this.conquistador = autorSaque;
        }
        
        return true;
    }

    public String getNome() {
        return nome.getNomeFormatado();
    }

    public NomeCidade getNomeCidade() {
        return nome;
    }

    public Regiao getRegiao() {
        return regiao;
    }

    public int getQuantidadeTesouros() {
        return quantidadeTesouros;
    }

    public boolean isConquistada() {
        return conquistada;
    }

    public Cor getConquistador() {
        return conquistador;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cidade cidade = (Cidade) o;
        return nome == cidade.nome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}

