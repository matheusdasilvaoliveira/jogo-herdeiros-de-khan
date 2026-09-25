package br.herdeiros.model;

import java.util.Stack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaralhoDeCidades {
	private Stack<Cidade> pilha;

	public BaralhoDeCidades() {
		this.pilha = new Stack<>();
		criarTodasCidades();
		
		Collections.shuffle(this.pilha);
	}

	private void criarTodasCidades() {
		this.pilha.push(new Cidade(NomeCidade.MOSCOU, Regiao.RUSSIA));
		this.pilha.push(new Cidade(NomeCidade.KAZAN, Regiao.RUSSIA));
        this.pilha.push(new Cidade(NomeCidade.NOVGOROD, Regiao.RUSSIA));
        
        this.pilha.push(new Cidade(NomeCidade.PEQUIM, Regiao.CHINA));
        this.pilha.push(new Cidade(NomeCidade.CANTAO, Regiao.CHINA));
        this.pilha.push(new Cidade(NomeCidade.XIAN, Regiao.CHINA));
        
        this.pilha.push(new Cidade(NomeCidade.SAMARCANDA, Regiao.PERSIA));
        this.pilha.push(new Cidade(NomeCidade.BAGDA, Regiao.PERSIA));
        this.pilha.push(new Cidade(NomeCidade.TABRIZ, Regiao.PERSIA));		
	}
	
	public Cidade sacarCidadeDoTopo() {
		if (this.pilha.empty()) {
			return null;
		}
		return this.pilha.pop();
	}
	
	public List<Cidade> sacarCidadesIniciais() {
		List<Cidade> cidadesIniciais = new ArrayList<>();
        
        // Saca as 3 cidades iniciais para a preparação do jogo
        for (int i = 0; i < 3; i++) {
        	cidadesIniciais.add(sacarCidadeDoTopo());
        }
        return cidadesIniciais;
    }
	

}
