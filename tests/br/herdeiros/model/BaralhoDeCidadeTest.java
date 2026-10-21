package br.herdeiros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BaralhoDeCidadeTest{
    private static final int TIMEOUT_PADRAO = 1000;
    private BaralhoDeCidades baralhoTeste;

    @Before
    public void setUp() {
        baralhoTeste = new BaralhoDeCidades();
    }

    /**
     * Objetivo: Testar se o Baralho de Cidades saca exatamente as 3 cidades iniciais para o tabuleiro.
     * Cenário: Baralho de cidades recém-instanciado.
     * Ação: Chamada a sacarCidadesIniciais().
     * Resultado Esperado: Lista não-nula com exatamente 3 elementos do tipo Cidade.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testBaralhoDeCidadesGeraCidadesIniciaisComSucesso() {
        List<Cidade> iniciais = baralhoTeste.sacarCidadesIniciais();
        assertNotNull("A lista de cidades iniciais não deve ser nula", iniciais);
        assertEquals("Devem ser sacadas exatamente 3 cidades iniciais", 3, iniciais.size());
    }
}