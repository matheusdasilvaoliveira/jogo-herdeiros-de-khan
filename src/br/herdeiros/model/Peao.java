package br.herdeiros.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class Peao {
    private Cor cor;
    private String localizacaoAtual; 
    private List<String> caminhoDoTurno;

    public Peao(Cor cor, String localizacaoInicial) {
        this.cor = cor;
        this.localizacaoAtual = localizacaoInicial;
        this.caminhoDoTurno = new ArrayList<>();
        this.caminhoDoTurno.add(localizacaoInicial);
    }

    public void iniciarTurno() {
        this.caminhoDoTurno.clear();
        this.caminhoDoTurno.add(this.localizacaoAtual);
    }

    public void mover(String novaLocalizacao) {
        this.localizacaoAtual = novaLocalizacao;
        this.caminhoDoTurno.add(novaLocalizacao);
    }

    public boolean jaVisitouNoTurno(String localizacao) {
        return this.caminhoDoTurno.contains(localizacao);
    }

    public List<String> getCaminhoDoTurno() {
        return Collections.unmodifiableList(caminhoDoTurno);
    }

    public String getLocalizacaoAtual() {
        return localizacaoAtual;
    }

    public Cor getCor() {
        return cor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peao peao = (Peao) o;
        return cor == peao.cor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cor);
    }
}

