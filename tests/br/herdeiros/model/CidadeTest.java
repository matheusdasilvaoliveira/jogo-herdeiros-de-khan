package br.herdeiros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class CidadeTest {
    private static final int TIMEOUT_PADRAO = 1000;
    private Cidade cidadeTeste;

    @Before
    public void setUp() {
        cidadeTeste = new Cidade(NomeCidade.BAGDA, Regiao.PERSIA);
    }

    /**
     * Objetivo: Testar se o saque em uma cidade com tesouros decrementa seu estoque e retorna sucesso.
     * Cenário: Cidade recém-criada com 4 tesouros.
     * Ação: Invocação de saquearTesouro().
     * Resultado Esperado: Retorno verdadeiro e estoque reduzido para 3.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeComTesourosDecrementaQuantidadeERetornaVerdadeiro() {
        boolean resultado = cidadeTeste.saquearTesouro();
        assertTrue("O saque deve ser bem-sucedido quando a cidade possui tesouros", resultado);
        assertEquals("A quantidade de tesouros deve diminuir de 4 para 3", 3, cidadeTeste.getQuantidadeTesouros());
    }

    /**
     * Objetivo: Testar se uma cidade tem seu estado alterado para conquistada ao ter todos os 4 tesouros saqueados.
     * Cenário: Cidade inicial com 4 tesouros.
     * Ação: Invocação de saquearTesouro() 4 vezes consecutivas.
     * Resultado Esperado: isConquistada() retorna verdadeiro e estoque de tesouros é 0.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeAteEsgotarTesourosMarcaComoConquistada() {
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro(); // Esgota os 4 tesouros

        assertTrue("A cidade deve estar marcada como conquistada após esgotar os tesouros", cidadeTeste.isConquistada());
        assertEquals("Estoque de tesouros deve ser zero", 0, cidadeTeste.getQuantidadeTesouros());
    }

    /**
     * Objetivo: Testar se tentativas de saque adicionais em cidades já esgotadas falham sem alterar estado.
     * Cenário: Cidade com todos os tesouros já saqueados.
     * Ação: Invocação de uma quinta operação de saque.
     * Resultado Esperado: Retorno falso e preservação do estado de conquistada.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void c() {
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro();
        cidadeTeste.saquearTesouro(); // Esgota
        boolean saqueExtra = cidadeTeste.saquearTesouro();

        assertFalse("O saque em cidade já esgotada deve retornar falso", saqueExtra);
        assertTrue("A cidade continua conquistada", cidadeTeste.isConquistada());
    }

    /**
     * Objetivo: Testar se a conquista de uma cidade registra o jogador autor do último saque como conquistador.
     * Cenário: Cidade com 4 tesouros e jogador AZUL executando os saques.
     * Ação: Execução de 4 saques associados ao jogador AZUL.
     * Resultado Esperado: Cidade marcada como conquistada e conquistador igual a Cor.AZUL.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testCidadeConquistadaRegistraJogadorConquistador() {
        Cidade cidade = new Cidade(NomeCidade.PEQUIM, Regiao.CHINA);
        assertNull("Cidade recém-criada não possui conquistador", cidade.getConquistador());

        cidade.saquearTesouro(Cor.AZUL);
        cidade.saquearTesouro(Cor.AZUL);
        cidade.saquearTesouro(Cor.AZUL);
        assertFalse("Com 1 tesouro restante ainda não está conquistada", cidade.isConquistada());
        
        cidade.saquearTesouro(Cor.AZUL);
        assertTrue("Cidade deve estar conquistada", cidade.isConquistada());
        assertEquals("O conquistador deve ser o jogador AZUL", Cor.AZUL, cidade.getConquistador());
    }

    /**
     * Objetivo: Testar a consistência do contrato equals() e hashCode() para Cidade (Capítulo 15).
     * Cenário: Duas instâncias de Cidade com os mesmos atributos identificadores.
     * Ação: Comparação via equals() e hashCode().
     * Resultado Esperado: Igualdade lógica e igualdade de código hash.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testEqualsEHashCodeCidadeParaTabelasHash() {
        Cidade cidade1 = new Cidade(NomeCidade.MOSCOU, Regiao.RUSSIA);
        Cidade cidade2 = new Cidade(NomeCidade.MOSCOU, Regiao.RUSSIA);
        assertEquals("Cidades com mesmo nome devem ser iguais", cidade1, cidade2);
        assertEquals("Cidades iguais devem ter o mesmo hashCode", cidade1.hashCode(), cidade2.hashCode());
    }
}