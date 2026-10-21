package br.herdeiros.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

class BaralhoDeCidades {
    private Deque<Cidade> pilha;

    public BaralhoDeCidades() {
        this.pilha = new ArrayDeque<>();
        criarTodasCidades();
        
        // Como Collections.shuffle exige uma List, convertemos temporariamente
        List<Cidade> listaTemporaria = new ArrayList<>(this.pilha);
        Collections.shuffle(listaTemporaria);
        this.pilha = new ArrayDeque<>(listaTemporaria);
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
        if (this.pilha.isEmpty()) {
            return null;
        }
        return this.pilha.pop();
    }
    
    public List<Cidade> sacarCidadesIniciais() {
        List<Cidade> cidadesIniciais = new ArrayList<>();
        
        // Saca as 3 cidades iniciais para a preparação do jogo
        for (int i = 0; i < 3; i++) {
            Cidade cidade = sacarCidadeDoTopo();
            if (cidade != null) {
                cidadesIniciais.add(cidade);
            }
        }
        return cidadesIniciais;
    }
}