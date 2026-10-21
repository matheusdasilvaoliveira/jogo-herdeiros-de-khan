package br.herdeiros.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Tabuleiro {
    private Peao peaoDoKhan;
    private Map<NomeCidade, Cidade> cidadesDisponiveis; 
    private Map<String, Set<String>> rotas;
    private Map<String, Cor> yurts;

    public Tabuleiro(List<Cidade> cidadesIniciaisSorteadas) {
        // O peão branco representa o Khan e começa ao lado do tabuleiro
        this.peaoDoKhan = new Peao(Cor.BRANCO, "Fora do Tabuleiro");
        
        this.cidadesDisponiveis = new HashMap<>();
        if (cidadesIniciaisSorteadas != null) {
            for (Cidade cidade : cidadesIniciaisSorteadas) {
                this.cidadesDisponiveis.put(cidade.getNomeCidade(), cidade);
            }
        }

        this.rotas = new HashMap<>();
        this.yurts = new HashMap<>();
        inicializarGrafoRotas();
    }
    
    private void inicializarGrafoRotas() {
        // Capital central
        String karakorum = "Karakorum";
        
        // Rússia
        String moscou = NomeCidade.MOSCOU.getNomeFormatado();
        String kazan = NomeCidade.KAZAN.getNomeFormatado();
        String novgorod = NomeCidade.NOVGOROD.getNomeFormatado();

        // China
        String pequim = NomeCidade.PEQUIM.getNomeFormatado();
        String cantao = NomeCidade.CANTAO.getNomeFormatado();
        String xian = NomeCidade.XIAN.getNomeFormatado();

        // Pérsia
        String samarcanda = NomeCidade.SAMARCANDA.getNomeFormatado();
        String bagda = NomeCidade.BAGDA.getNomeFormatado();
        String tabriz = NomeCidade.TABRIZ.getNomeFormatado();

        // Conexões da Capital com portões de entrada das 3 regiões
        conectar(karakorum, kazan);
        conectar(karakorum, xian);
        conectar(karakorum, samarcanda);

        // Rotas internas da Rússia
        conectar(moscou, kazan);
        conectar(moscou, novgorod);
        conectar(kazan, novgorod);

        // Rotas internas da China
        conectar(pequim, xian);
        conectar(pequim, cantao);
        conectar(xian, cantao);

        // Rotas internas da Pérsia
        conectar(samarcanda, bagda);
        conectar(samarcanda, tabriz);
        conectar(bagda, tabriz);

        // Rotas fronteiriças entre regiões
        conectar(kazan, samarcanda);
        conectar(xian, samarcanda);
    }

    private void conectar(String loc1, String loc2) {
        this.rotas.computeIfAbsent(loc1, k -> new HashSet<>()).add(loc2);
        this.rotas.computeIfAbsent(loc2, k -> new HashSet<>()).add(loc1);
    }

    public boolean saoVizinhas(String loc1, String loc2) {
        if (loc1 == null || loc2 == null) {
            return false;
        }
        Set<String> vizinhas = this.rotas.get(loc1);
        return vizinhas != null && vizinhas.contains(loc2);
    }

    public Set<String> getVizinhos(String localizacao) {
        Set<String> vizinhos = this.rotas.get(localizacao);
        if (vizinhos == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(vizinhos);
    }

    public boolean construirYurt(String localizacao, Cor corJogador) {
        if (localizacao == null || corJogador == null || this.yurts.containsKey(localizacao)) {
            return false;
        }
        this.yurts.put(localizacao, corJogador);
        return true;
    }

    public boolean temYurt(String localizacao) {
        return this.yurts.containsKey(localizacao);
    }

    public Cor getDonoYurt(String localizacao) {
        return this.yurts.get(localizacao);
    }
    
    public void substituirCidadeConquistada(Cidade cidadeAntiga, Cidade novaCidade) {
        if (cidadeAntiga != null) {
            this.cidadesDisponiveis.remove(cidadeAntiga.getNomeCidade());
        }
        
        if (novaCidade != null) {
            this.cidadesDisponiveis.put(novaCidade.getNomeCidade(), novaCidade);
        }
    }

    public Cidade getCidade(NomeCidade nome) {
        return this.cidadesDisponiveis.get(nome);
    }

    public Peao getPeaoDoKhan() {
        return peaoDoKhan;
    }

    public Map<NomeCidade, Cidade> getCidadesDisponiveis() {
        return Collections.unmodifiableMap(cidadesDisponiveis);
    }
}
