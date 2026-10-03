package br.herdeiros.model;

public class Cidade {
	private NomeCidade nome;
    private Regiao regiao;
    private int quantidadeTesouros;
    private boolean conquistada;

    public Cidade(NomeCidade nome, Regiao regiao) {
        this.nome = nome;
        this.regiao = regiao;
        
        // Uma nova cidade revelada recebe 4 tesouros
        this.quantidadeTesouros = 4;
        this.conquistada = false;
    }
    
    // Um Peão tentará saquear a cidade
    public boolean saquearTesouro() {
        if (conquistada || quantidadeTesouros <= 0) {
            return false; // Não há mais o que saquear
        }
        
        this.quantidadeTesouros--;
        
        // Se tomou o último tesouro, a cidade está conquistada
        if (this.quantidadeTesouros == 0) {
            this.conquistada = true;
        }
        
        return true;
    }

    public String getNome() {
        return nome.getNomeFormatado();
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

}
