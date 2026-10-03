package br.herdeiros.model;

public class Jogador {
	private String nome;
    private Cor cor;
    private Peao meuPeao;
    private int quantidadeDescendentes; // Define quem começa jogando
    private int votosKurultai; // Pontuação no tabuleiro

    public Jogador(String nome, Cor cor, int quantidadeDescendentes) {
        this.nome = nome;
        this.cor = cor;
        this.quantidadeDescendentes = quantidadeDescendentes;
        this.meuPeao = new Peao(this.cor, "Karakorum"); // Todos começam em Karakorum
        
        // Todo jogador nasce com 0 votos no conselho
        this.votosKurultai = 0;
    }
    
    public void ganharVotos(int quantidade) {
        if (quantidade > 0) {
            this.votosKurultai += quantidade;
        }
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

}
