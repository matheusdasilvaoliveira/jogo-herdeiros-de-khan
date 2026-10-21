# 🐎 Jogo Herdeiros de Khan

![Java](https://img.shields.io/badge/java-25-ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JUnit4](https://img.shields.io/badge/JUnit-4-25A162.svg?style=for-the-badge&logo=junit5&logoColor=white)
![Status](https://img.shields.io/badge/1ª_Iteração-Concluída-brightgreen?style=for-the-badge)

Repositório dedicado ao desenvolvimento do jogo de tabuleiro **Herdeiros de Khan**, projeto prático desenvolvido para a disciplina **INF1636 – Programação Orientada a Objetos** da PUC-Rio.

---

## 📜 Sobre o Jogo

"Herdeiros de Khan" é um jogo estratégico de tabuleiro para 2 a 5 jogadores ambientado no Império Mongol. Com o envelhecimento de Gengis Khan, os jogadores assumem o papel de herdeiros competindo por influência e votos no conselho (*Kurultai*) para se tornarem o próximo grande líder.

O tabuleiro é dividido em três regiões principais (**Rússia**, **China** e **Pérsia**), com a capital **Karakorum** no centro. Durante a partida, os herdeiros devem:
- Mover seus peões estrategicamente pelas rotas do mapa entre as regiões;
- Coletar peças de tributo importantes (**Espadas**, **Yurts** e **Moedas**) nas províncias;
- Atacar e saquear cidades reveladas utilizando espadas para obter tesouros;
- Construir assentamentos (*Yurts*) no mapa para expandir influência e mobilidade;
- Dominar cidades esgotadas e garantir votos no conselho (*Kurultai*) para alcançar a vitória.

---

## 🏛️ Arquitetura e Especificações da 1ª Iteração

A 1ª iteração foca na modelagem e especificação do componente **Model** seguindo os princípios de Orientação a Objetos, ocultação de informação e boas práticas de engenharia de software da disciplina.

### 1. Ocultação de Informação e API Pública
- **Pacote `br.herdeiros.model`:** Contém todas as classes do domínio.
- **Classes Básicas Não-Públicas (*package-private*):** Elementos essenciais como `Cidade`, `Jogador`, `Peao`, `Tabuleiro` e `BaralhoDeCidades` são declarados como não públicos para evitar acoplamento direto com futuros pacotes de `View` e `Controller`.
- **API Pública / Padrão Façade (`Partida`):** A classe `Partida` atua como o ponto de entrada público para todo o componente `Model`, expondo regras e consultas através de tipos seguros e primitivos/enums (`Cor`, `NomeCidade`, `TipoTributo`).

### 2. Framework de Coleções e Uso de `HashMap` (Capítulo 15)
Em conformidade com a diretriz pedagógica da disciplina (*"Use um HashMap sempre que for possível"*):
- **Cidades do Tabuleiro:** `Map<NomeCidade, Cidade>` via `HashMap` para acesso e substituição de cartas ativas em tempo $O(1)$.
- **Gestão de Jogadores:** `Map<Cor, Jogador>` via `HashMap` indexando cada jogador por sua cor exclusiva.
- **Inventário de Recursos/Tributos:** `Map<TipoTributo, Integer>` via `HashMap` em cada jogador para controle de estoque de espadas, yurts e moedas.
- **Grafo de Rotas e Adjacências:** `Map<String, Set<String>>` via `HashMap` e `HashSet` representando a malha viária e fronteiras entre a capital e províncias.
- **Assentamentos (Yurts):** `Map<String, Cor>` via `HashMap` associando territórios à cor do jogador dominante.
- **Contratos `equals()` e `hashCode()`:** Implementados consistentemente em `Cidade`, `Jogador` e `Peao` para garantir integridade em tabelas hash e conjuntos.

### 3. Regras de Negócio Implementadas
- **Determinação do Jogador Inicial:** Regra oficial que elege automaticamente o jogador com a maior quantidade de descendentes para iniciar a partida.
- **Ciclo de Turnos e Histórico:** Avanço circular ordenado de turnos entre os participantes e reinício do caminho percorrido pelo peão a cada novo turno.
- **Movimentação Validada e Rastreamento de Caminho:** Movimentação de peões restrita a nós/cidades vizinhas conectadas no grafo do mapa, registrando em tempo real as localidades visitadas durante o turno (`turnPath`).
- **Peão Neutro do Khan:** Movimentação e posicionamento do peão branco do Khan, iniciando fora do tabuleiro e podendo ingressar no mapa.
- **Construção de Assentamentos:** Construção de Yurts consumindo o respectivo tributo do inventário e alocando no tabuleiro.
- **Mecânica Completa de Saque e Conquista:** Saque de cidade que valida posse de espadas, consome o recurso, pontua votos no Kurultai, detecta conquista, atribui a titularidade da cidade conquistada ao autor do último saque e substitui a cidade sacando nova carta do baralho.

---

## 🧪 Testes Unitários (Capítulo 18 - JUnit 4)

A suíte de testes unitários foi desenvolvida com o framework **JUnit 4** e documentada rigorosamente segundo as diretrizes de documentação do Capítulo 18:
- Todos os testes utilizam timeout padrão (`@Test(timeout = 1000)`);
- Documentação Javadoc estruturada para cada caso de teste contendo:
  - **Objetivo do teste:** O que a unidade de código está verificando;
  - **Cenário:** O estado inicial e pré-condições dos objetos;
  - **Ação:** O método ou operação invocada;
  - **Resultado esperado:** As asserções e o comportamento validado;
- **17 casos de testes não-triviais** cobrindo regras de saque, titularidade de conquista, inventário, rotação de turnos, movimentação em grafo, histórico de caminho do peão, peão do Khan, tabelas hash e contratos de igualdade.

---

## 🚀 Como Executar os Testes

### No Eclipse / IntelliJ IDEA
1. Importe o projeto como **Java Project**.
2. Certifique-se de que a biblioteca **JUnit 4** está adicionada ao Build Path.
3. Clique com o botão direito na pasta `tests` ou no arquivo `HerdeirosTest.java`.
4. Selecione **Run As > JUnit Test**.

### Via Linha de Comando (com JDK 25 e JUnit 4)
```bash
# Compilar código-fonte e testes
javac -d bin -cp "bin;caminho/para/junit-4.13.2.jar;caminho/para/hamcrest-core-1.3.jar" src/br/herdeiros/model/*.java tests/br/herdeiros/model/*.java

# Executar a suíte de testes
java -cp "bin;caminho/para/junit-4.13.2.jar;caminho/para/hamcrest-core-1.3.jar" org.junit.runner.JUnitCore br.herdeiros.model.HerdeirosTest
```

---

## 👨‍💻 Autores

- **Cristiano Ferreira De Mattos**
- **Felipe Eduardo Nunes Silva**
