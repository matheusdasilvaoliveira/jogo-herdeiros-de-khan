package br.herdeiros.model;

public enum NomeCidade {
	MOSCOU("Moscou"),
    KAZAN("Kazan"),
    NOVGOROD("Novgorod"),
    
    PEQUIM("Pequim"),
    CANTAO("Cantão"),
    XIAN("Xi'an"),
    
    SAMARCANDA("Samarcanda"),
    BAGDA("Bagdá"),
    TABRIZ("Tabriz");
	
	private final String nomeFormatado;
	
	NomeCidade(String nomeFormatado) {
		this.nomeFormatado = nomeFormatado;
	}
	
	public String getNomeFormatado() {
		return this.nomeFormatado;
	}
}
