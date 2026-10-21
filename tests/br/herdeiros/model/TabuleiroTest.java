package br.herdeiros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class TabuleiroTest {
    private static final int TIMEOUT_PADRAO = 1000;
    private Tabuleiro tabuleiroTeste;

    @Before
    public void setUp() {
        BaralhoDeCidades baralho = new BaralhoDeCidades();
        List<Cidade> cidadesIniciais = baralho.sacarCidadesIniciais();
        tabuleiroTeste = new Tabuleiro(cidadesIniciais);
    }

    /**
     * Objetivo: Testar se o Tabuleiro inicializa as cidades disponíveis utilizando a estrutura de Mapa (HashMap).
     * Cenário: Tabuleiro instanciado com 3 cidades sorteadas.
     * Ação: Consulta ao mapa de cidades disponíveis.
     * Resultado Esperado: Mapa não nulo com exatamente 3 cidades indexadas por NomeCidade.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testTabuleiroInicializaComCidadesDisponiveisNoMapa() {
        Map<NomeCidade, Cidade> disponiveis = tabuleiroTeste.getCidadesDisponiveis();
        assertNotNull("O mapa de cidades disponíveis no tabuleiro não deve ser nulo", disponiveis);
        assertEquals("O tabuleiro deve conter 3 cidades disponíveis inicialmente", 3, disponiveis.size());
    }

    /**
     * Objetivo: Testar a substituição de cidade conquistada no mapa de cidades do Tabuleiro via HashMap.
     * Cenário: Tabuleiro com 3 cidades ativas.
     * Ação: Remoção da cidade antiga e inclusão de nova cidade sacada.
     * Resultado Esperado: Antiga cidade inacessível via chave e nova cidade indexada com sucesso.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSubstituirCidadeConquistadaNoTabuleiroAtualizaMapa() {
        Cidade cidadeAntiga = new Cidade(NomeCidade.MOSCOU, Regiao.RUSSIA);
        Cidade novaCidade = new Cidade(NomeCidade.PEQUIM, Regiao.CHINA);

        Tabuleiro tabuleiro = new Tabuleiro(java.util.Arrays.asList(
            cidadeAntiga,
            new Cidade(NomeCidade.KAZAN, Regiao.RUSSIA),
            new Cidade(NomeCidade.NOVGOROD, Regiao.RUSSIA)
        ));

        tabuleiro.substituirCidadeConquistada(cidadeAntiga, novaCidade);

        assertNull("A cidade antiga não deve mais estar no mapa", tabuleiro.getCidade(NomeCidade.MOSCOU));
        assertEquals("A nova cidade deve estar acessível pela chave no mapa", novaCidade, tabuleiro.getCidade(NomeCidade.PEQUIM));
    }
}