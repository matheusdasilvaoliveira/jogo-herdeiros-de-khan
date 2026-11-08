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

        // Conexões da Capital Karakorum com os principais polos de rota das regiões
        conectar(karakorum, kazan);
        conectar(karakorum, xian);
        conectar(karakorum, samarcanda);
        conectar(karakorum, moscou);
        conectar(karakorum, pequim);
        conectar(karakorum, tabriz);

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
        conectar(cantao, tabriz);
    }

    private void conectar(String loc1, String loc2) {
        Set<String> s1 = this.rotas.get(loc1);
        if (s1 == null) {
            s1 = new HashSet<>();
            this.rotas.put(loc1, s1);
        }
        s1.add(loc2);

        Set<String> s2 = this.rotas.get(loc2);
        if (s2 == null) {
            s2 = new HashSet<>();
            this.rotas.put(loc2, s2);
        }
        s2.add(loc1);
    }

    /**
     * Calcula o menor número de passos (distância no grafo) entre duas localidades via BFS.
     * Retorna -1 se não houver caminho ou se alguma localidade for inválida.
     */
    public int calcularDistancia(String origem, String destino) {
        if (origem == null || destino == null) {
            return -1;
        }
        if (origem.equals(destino)) {
            return 0;
        }
        java.util.Queue<String> fila = new java.util.LinkedList<>();
        Map<String, Integer> distancias = new HashMap<>();
        fila.add(origem);
        distancias.put(origem, 0);

        while (!fila.isEmpty()) {
            String atual = fila.poll();
            int d = distancias.get(atual);

            Set<String> vizinhas = this.rotas.get(atual);
            if (vizinhas != null) {
                for (String v : vizinhas) {
                    if (!distancias.containsKey(v)) {
                        distancias.put(v, d + 1);
                        if (v.equals(destino)) {
                            return d + 1;
                        }
                        fila.add(v);
                    }
                }
            }
        }
        return -1;
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
