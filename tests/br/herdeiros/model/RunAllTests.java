package br.herdeiros.model;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    CidadeTest.class,
    JogadorTest.class,
    BaralhoDeCidadeTest.class,
    TabuleiroTest.class,
    PeaoTest.class,
    PartidaTest.class
})
public class RunAllTests {

    public static void main(String[] args) {
        Result resultado = JUnitCore.runClasses(RunAllTests.class);

        for (Failure falha : resultado.getFailures()) {
            System.out.println(falha.toString());
        }
        System.out.println("Executados: " + resultado.getRunCount());
        System.out.println("Falhas: " + resultado.getFailureCount());
        System.out.println("Sucesso: " + resultado.wasSuccessful());
    }
}