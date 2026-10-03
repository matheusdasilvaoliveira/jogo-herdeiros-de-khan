package br.herdeiros.model;

import java.util.ArrayList;
import java.util.List;

public class Tabuleiro {
	private Peao peaoDoKhan;
    private List<Cidade> cidadesDisponiveis; 

    public Tabuleiro(List<Cidade> cidadesIniciaisSorteadas) {
        // O peão branco representa o Khan e começa ao lado do tabuleiro
        this.peaoDoKhan = new Peao(Cor.BRANCO, "Fora do Tabuleiro");
        
        this.cidadesDisponiveis = new ArrayList<>(cidadesIniciaisSorteadas);
    }
    
    public void substituirCidadeConquistada(Cidade cidadeAntiga, Cidade novaCidade) {
    	this.cidadesDisponiveis.remove(cidadeAntiga);
        
        if (novaCidade != null) {
            this.cidadesDisponiveis.add(novaCidade);
        }
    }

    public Peao getPeaoDoKhan() {
        return peaoDoKhan;
    }

    public List<Cidade> getCidadesDisponiveis() {
        return cidadesDisponiveis;
    }
}
