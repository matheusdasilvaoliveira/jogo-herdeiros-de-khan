package br.herdeiros.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class PeaoTest {
    private static final int TIMEOUT_PADRAO = 1000;
    private Peao peao;

    @Before
    public void setUp() {
        peao = new Peao(Cor.AMARELO, "Karakorum");
    }

    /**
     * Objetivo: Testar se o peão registra as localizações visitadas durante o turno e reinicia no próximo turno.
     * Cenário: Peão criado na capital Karakorum.
     * Ação: Movimentação para Kazan e posterior reinício de turno.
     * Resultado Esperado: Histórico de caminho registra Karakorum e Kazan; jaVisitouNoTurno retorna verdadeiro;
     *                     após iniciarTurno, histórico é resetado.
     */
    @Test(timeout = TIMEOUT_PADRAO)
    public void testPeaoRegistraCaminhoDoTurnoEVerificaVisitados() {
        assertTrue("Inicia contendo a posição de partida", peao.jaVisitouNoTurno("Karakorum"));
        assertEquals("Caminho inicial contém 1 posição", 1, peao.getCaminhoDoTurno().size());

        peao.mover("Kazan");
        assertTrue("Registra Kazan como visitado no turno", peao.jaVisitouNoTurno("Kazan"));
        assertEquals("Caminho contém 2 posições", 2, peao.getCaminhoDoTurno().size());

        peao.iniciarTurno();
        assertEquals("Novo turno reinicia caminho contendo apenas localização atual (Kazan)", 1, peao.getCaminhoDoTurno().size());
        assertTrue("Contém Kazan", peao.jaVisitouNoTurno("Kazan"));
        assertFalse("Não contém mais Karakorum no turno atual", peao.jaVisitouNoTurno("Karakorum"));
    }
}