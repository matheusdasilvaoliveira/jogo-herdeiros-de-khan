package br.herdeiros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PartidaTest {
    private static final int TIMEOUT_PADRAO = 1000;
    private Partida partida;

    @Before
    public void setUp() {
        partida = new Partida();
    }

    /**
     * Objetivo: Testar adição e busca direta de jogadores por Cor na Partida utilizando HashMap.
     * Cenário: Partida recém-criada.
     * Ação: Adição de jogadores de cores azul e verde.
     * Resultado Esperado: 2 jogadores mapeados e recuperação direta via chave única (Cor).
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPartidaAdicionaEBuscaJogadorPorCorComHashMap() {
        partida.adicionarJogador("Batu", Cor.AZUL, 2);
        partida.adicionarJogador("Jochi", Cor.VERDE, 4);

        assertEquals("A partida deve conter 2 jogadores mapeados", 2, partida.getQuantidadeJogadores());
        assertEquals("O nome do jogador azul deve bater", "Batu", partida.getNomeJogador(Cor.AZUL));
        assertEquals("O nome do jogador verde deve bater", "Jochi", partida.getNomeJogador(Cor.VERDE));
        assertEquals("O primeiro jogador adicionado deve ser o atual", Cor.AZUL, partida.getCorJogadorAtual());
    }

    /**
     * Objetivo: Testar a regra de negócio oficial que define quem começa jogando pelo número de descendentes.
     * Cenário: Partida com três jogadores cadastrados com 2, 6 e 3 descendentes.
     * Ação: Invocação de definirPrimeiroJogadorPorDescendentes().
     * Resultado Esperado: O jogador com 6 descendentes (VERDE) deve ser eleito o jogador inicial.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testDefinirPrimeiroJogadorPorDescendentesRegraDeNegocio() {
        partida.adicionarJogador("Ogedei", Cor.AZUL, 2);
        partida.adicionarJogador("Tolui", Cor.VERDE, 6);
        partida.adicionarJogador("Chagatai", Cor.AMARELO, 3);

        Cor corInicial = partida.definirPrimeiroJogadorPorDescendentes();
        assertEquals("O jogador com mais descendentes (VERDE - 6) deve iniciar", Cor.VERDE, corInicial);
        assertEquals("O jogador atual da partida deve ser atualizado para o inicial", Cor.VERDE, partida.getCorJogadorAtual());
    }

    /**
     * Objetivo: Testar a validação de rotas e movimentação de peão entre nós vizinhos no Tabuleiro.
     * Cenário: Jogador atual posicionado na capital "Karakorum".
     * Ação: Mover para um território vizinho válido ("Kazan") e depois tentar movimento inválido para "Bagdá" sem rota direta.
     * Resultado Esperado: Primeiro movimento aceito com atualização de localização; segundo rejeitado.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testMoverJogadorAtualPelasRotasDoTabuleiro() {
        partida.adicionarJogador("Batu", Cor.AZUL, 1);

        assertEquals("Peão começa em Karakorum", "Karakorum", partida.getLocalizacaoJogadorAtual());

        // Karakorum possui conexão direta com Kazan (portão de entrada da Rússia)
        boolean moveuValido = partida.moverJogadorAtual("Kazan");
        assertTrue("Movimento para vizinho Kazan deve ser válido", moveuValido);
        assertEquals("Localização atual deve ser Kazan", "Kazan", partida.getLocalizacaoJogadorAtual());

        // Kazan NÃO possui rota direta com Bagdá
        boolean moveuInvalido = partida.moverJogadorAtual("Bagdá");
        assertFalse("Movimento para território não vizinho deve ser recusado", moveuInvalido);
        assertEquals("Localização deve permanecer em Kazan", "Kazan", partida.getLocalizacaoJogadorAtual());
    }

    /**
     * Objetivo: Testar a construção de Yurt consumindo o recurso de Yurt do jogador e registrando no Tabuleiro.
     * Cenário: Jogador atual sem yurts no inventário.
     * Ação: Tentativa de construção sem recurso (falha), seguida de coleta do tributo e construção bem-sucedida.
     * Resultado Esperado: Primeira tentativa retorna falso; após receber recurso, retorna verdadeiro e reduz inventário.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testConstruirYurtConsomeTributoEAtualizaTabuleiro() {
        partida.adicionarJogador("Batu", Cor.AZUL, 1);

        boolean construiuSemRecurso = partida.construirYurtJogadorAtual("Karakorum");
        assertFalse("Não deve construir yurt sem possuir o tributo YURT", construiuSemRecurso);

        partida.coletarTributo(Cor.AZUL, TipoTributo.YURT, 1);
        assertEquals("Jogador deve possuir 1 yurt", 1, partida.getTributosJogador(Cor.AZUL, TipoTributo.YURT));

        boolean construiuComRecurso = partida.construirYurtJogadorAtual("Karakorum");
        assertTrue("Deve construir yurt com sucesso", construiuComRecurso);
        assertEquals("O estoque de yurts do jogador deve ser decrementado para 0", 0, partida.getTributosJogador(Cor.AZUL, TipoTributo.YURT));
        assertTrue("O tabuleiro deve registrar presença de yurt em Karakorum", partida.temYurtNaLocalizacao("Karakorum"));
        assertEquals("O dono do yurt deve ser o jogador AZUL", Cor.AZUL, partida.getDonoYurtNaLocalizacao("Karakorum"));
    }

    /**
     * Objetivo: Testar a regra completa de saque de cidade via API Façade da Partida.
     * Cenário: Jogador atual recebe 1 espada para realizar saque em cidade disponível.
     * Ação: Saque de cidade com sucesso.
     * Resultado Esperado: Consumo de 1 espada, ganho de 1 voto no Kurultai e decremento de tesouros da cidade.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testSaquearCidadeViaPartidaConsomeEspadaEGanhaVotos() {
        partida.adicionarJogador("Batu", Cor.AZUL, 1);

        // Identifica uma cidade presente no tabuleiro
        NomeCidade cidadeDisponivel = partida.getTabuleiro().getCidadesDisponiveis().keySet().iterator().next();

        // Tentativa sem espada deve falhar
        boolean saqueSemEspada = partida.saquearCidade(cidadeDisponivel);
        assertFalse("Saque sem espada deve falhar", saqueSemEspada);

        // Jogador coleta 1 espada
        partida.coletarTributo(Cor.AZUL, TipoTributo.ESPADA, 1);
        int tesourosAntes = partida.getTesourosCidade(cidadeDisponivel);

        boolean saqueComEspada = partida.saquearCidade(cidadeDisponivel);
        assertTrue("Saque com espada deve suceder", saqueComEspada);
        assertEquals("Espada deve ser gasta", 0, partida.getTributosJogador(Cor.AZUL, TipoTributo.ESPADA));
        assertEquals("Jogador deve receber 1 voto no Kurultai", 1, partida.getVotosJogadorAtual());
        assertEquals("A cidade deve ter 1 tesouro a menos", tesourosAntes - 1, partida.getTesourosCidade(cidadeDisponivel));
    }

    /**
     * Objetivo: Testar a alternância circular de turnos entre os jogadores.
     * Cenário: Partida com 2 jogadores cadastrados (AZUL e VERMELHO).
     * Ação: Invocação de passarTurno().
     * Resultado Esperado: Alternância para o jogador seguinte e retorno ao primeiro de forma circular.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPassarTurnoAlternaEntreJogadores() {
        partida.adicionarJogador("Batu", Cor.AZUL, 1);
        partida.adicionarJogador("Genghis", Cor.VERMELHO, 2);

        assertEquals("Jogador inicial deve ser AZUL", Cor.AZUL, partida.getCorJogadorAtual());

        Cor proximo = partida.passarTurno();
        assertEquals("Próximo jogador deve ser VERMELHO", Cor.VERMELHO, proximo);
        assertEquals("Jogador atual deve ser VERMELHO", Cor.VERMELHO, partida.getCorJogadorAtual());

        Cor ciclo = partida.passarTurno();
        assertEquals("Ciclo deve retornar ao jogador AZUL", Cor.AZUL, ciclo);
        assertEquals("Jogador atual deve ser AZUL", Cor.AZUL, partida.getCorJogadorAtual());
    }

    /**
     * Objetivo: Testar a movimentação e consulta do peão neutro do Khan.
     * Cenário: Partida recém-criada com peão do Khan fora do tabuleiro.
     * Ação: Mover o Khan para Karakorum via API Façade.
     * Resultado Esperado: Localização inicial "Fora do Tabuleiro" atualizada para "Karakorum".
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testMoverPeaoDoKhan() {
        assertEquals("Khan inicia fora do tabuleiro", "Fora do Tabuleiro", partida.getLocalizacaoKhan());

        partida.moverKhan("Karakorum");
        assertEquals("Khan deve estar posicionado em Karakorum", "Karakorum", partida.getLocalizacaoKhan());
    }
}