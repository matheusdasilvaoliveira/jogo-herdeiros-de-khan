package br.herdeiros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class JogadorTest {
    private static final int TIMEOUT_PADRAO = 1000;
    private Jogador jogadorTeste;

    @Before
    public void setUp() {
        jogadorTeste = new Jogador("Genghis", Cor.VERMELHO, 3);
    }

    /**
     * Objetivo: Testar a correta inicialização dos atributos de um Jogador.
     * Cenário: Instanciação de Jogador com nome "Genghis", cor VERMELHO e 3 descendentes.
     * Ação: Leitura dos getters correspondentes.
     * Resultado Esperado: Atributos correspondentes aos parâmetros passados e votos no Kurultai zerados.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testJogadorInicializaComAtributosCorretos() {
        assertEquals("O nome do jogador deve corresponder ao informado", "Genghis", jogadorTeste.getNome());
        assertEquals("A cor do jogador deve ser vermelha", Cor.VERMELHO, jogadorTeste.getCor());
        assertEquals("A quantidade inicial de descendentes deve ser 3", 3, jogadorTeste.getQuantidadeDescendentes());
        assertEquals("Os votos iniciais no Kurultai devem ser zero", 0, jogadorTeste.getVotosKurultai());
    }

    /**
     * Objetivo: Testar o incremento de pontuação (votos no Kurultai) de um Jogador.
     * Cenário: Jogador com 0 votos.
     * Ação: Chamada a ganharVotos(2).
     * Resultado Esperado: Total de votos atualizado para 2.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testGanharVotosIncrementaKurultaiCorretamente() {
        jogadorTeste.ganharVotos(2);
        assertEquals("O jogador deve possuir 2 votos no Kurultai após o ganho", 2, jogadorTeste.getVotosKurultai());
    }

    /**
     * Objetivo: Testar a correta associação e criação do peão do jogador no nó inicial "Karakorum".
     * Cenário: Instanciação do Jogador.
     * Ação: Invocação de getMeuPeao() e getLocalizacaoAtual().
     * Resultado Esperado: Peão não nulo e localizado em Karakorum.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPeaoDoJogadorENaoNulo() {
        assertNotNull("O jogador deve possuir um peão associado ao ser instanciado", jogadorTeste.getMeuPeao());
        assertEquals("O peão deve iniciar na capital Karakorum", "Karakorum", jogadorTeste.getMeuPeao().getLocalizacaoAtual());
    }

    /**
     * Objetivo: Testar a gestão do inventário de tributos do Jogador modelado com HashMap.
     * Cenário: Jogador com inventário zerado.
     * Ação: Adição de 3 espadas e 5 moedas, seguida de remoção de 1 espada e de uma remoção em excesso.
     * Resultado Esperado: Estoques atualizados corretamente e retorno booleano adequado.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testJogadorGerenciaTributosComHashMap() {
        assertEquals("Jogador deve iniciar com 0 espadas", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals("Jogador deve iniciar com 0 yurts", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.YURT));
        assertEquals("Jogador deve iniciar com 0 moedas", 0, jogadorTeste.getQuantidadeTributo(TipoTributo.MOEDA));

        jogadorTeste.adicionarTributo(TipoTributo.ESPADA, 3);
        jogadorTeste.adicionarTributo(TipoTributo.MOEDA, 5);

        assertEquals("Jogador deve ter 3 espadas após adição", 3, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));
        assertEquals("Jogador deve ter 5 moedas após adição", 5, jogadorTeste.getQuantidadeTributo(TipoTributo.MOEDA));

        boolean removeu = jogadorTeste.removerTributo(TipoTributo.ESPADA, 1);
        assertTrue("A remoção de 1 espada deve ser bem-sucedida", removeu);
        assertEquals("Jogador deve ficar com 2 espadas", 2, jogadorTeste.getQuantidadeTributo(TipoTributo.ESPADA));

        boolean removeuExcesso = jogadorTeste.removerTributo(TipoTributo.ESPADA, 10);
        assertFalse("Não deve permitir remover mais recursos do que o jogador possui", removeuExcesso);
    }

    /**
     * Objetivo: Testar a consistência do contrato equals() e hashCode() para Jogador (Capítulo 15).
     * Cenário: Dois jogadores com a mesma cor, porém nome e descendentes diferentes.
     * Ação: Comparação via equals() e hashCode().
     * Resultado Esperado: Igualdade lógica e igualdade de código hash (identidade definida pela cor).
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testEqualsEHashCodeJogadorParaTabelasHash() {
        Jogador jog1 = new Jogador("Subutai", Cor.AMARELO, 1);
        Jogador jog2 = new Jogador("OutroNome", Cor.AMARELO, 5);
        assertEquals("Jogadores com a mesma cor devem ser iguais", jog1, jog2);
        assertEquals("Jogadores iguais devem ter o mesmo hashCode", jog1.hashCode(), jog2.hashCode());
    }
}