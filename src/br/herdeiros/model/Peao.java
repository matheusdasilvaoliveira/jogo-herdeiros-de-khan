package br.herdeiros.model;

public class Peao {
	private Cor cor;
	private String localizacaoAtual; 

    public Peao(Cor cor, String localizacaoInicial) {
        this.cor = cor;
        this.localizacaoAtual = localizacaoInicial;
    }

    public void mover(String novaLocalizacao) {
        this.localizacaoAtual = novaLocalizacao;
    }

    public String getLocalizacaoAtual() {
        return localizacaoAtual;
    }

	public Cor getCor() {
		return cor;
	}
}
