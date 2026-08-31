package missao;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.time.LocalDateTime;

public class Jogo {
    private final Scanner scanner;
    private final Random random;
    private final List<RankingEntry> ranking;
    private final Path rankingPath;

    public Jogo(
            Scanner scanner,
            Random random,
            List<RankingEntry> ranking,
            Path rankingPath
    ) {
        this.scanner = scanner;
        this.random = random;
        this.ranking = ranking;
        this.rankingPath = rankingPath;
    }

    public void jogarPartida(
            int minX,
            int maxX,
            int minY,
            int maxY,
            String pilotoNome
    ) {

        Main.Dificuldade dificuldade = selecionarDificuldade();

        LocalDateTime dataHoraPartida = LocalDateTime.now();
        long tempoInicio = System.currentTimeMillis();

        Missao missao = criarNovaMissao(
                minX,
                maxX,
                minY,
                maxY,
                dificuldade
        );
        Nave nave = missao.getNave();

        int score;
        int movimentos = 0;

        switch (dificuldade) {
            case FACIL:
                score = 30;
                break;

            case MEDIO:
                score = 20;
                break;

            case DIFICIL:
                score = 15;
                break;

            default:
                score = 20;
        }

        boolean running = true;

        while (running) {

            desenharMapa(
                    missao,
                    minX,
                    maxX,
                    minY,
                    maxY,
                    score,
                    pilotoNome
            );

            System.out.printf(
                    "👨‍✈️Piloto: " + pilotoNome +
                            "\n🚀Nave em (%d,%d) | ⭐Pontos: %d | 👥Passageiros a bordo: %d | 👥Passageiros restantes: %d | ❤️Vidas: %d\n",
                    nave.getX(),
                    nave.getY(),
                    score,
                    nave.getPassageiros().size(),
                    missao.todosEmbarcados() ? 0 : missao.getPassageiros().size(),
                    nave.getVidas()
            );

            if (missao.verificaColisao()) {

                nave.perderVida();

                if (nave.getVidas() == 0) {
                    System.out.println("\n💥💀 GAME OVER! 💀💥");
                    break;
                } else {
                    System.out.println(
                            "\n🚀💥☄️Bateu em asteroide!\n❤️ Vidas restantes: "
                                    + nave.getVidas()
                    );
                }
            }

            if (missao.verificaColisaoInimigo()) {

                nave.perderVida();

                if (nave.getVidas() == 0) {
                    System.out.println("\n💥💀 GAME OVER! 💀💥");
                    break;
                } else {
                    System.out.println(
                            "\n🚀💥👾 A nave foi atingida por um inimigo!"
                                    + "\n❤️ Vidas restantes: "
                                    + nave.getVidas()
                    );
                }
            }

            System.out.print("\n🚀Para onde ir? \n");

            String line = scanner.nextLine().trim().toLowerCase();

            if (line.isEmpty()) {
                continue;
            }

            char cmd = line.charAt(0);

            boolean naveMoveu = false;

            switch (cmd) {

                case 'w':
                    nave.moveUp();
                    score--;
                    naveMoveu = true;
                    break;

                case 's':
                    nave.moveDown();
                    score--;
                    naveMoveu = true;
                    break;

                case 'a':
                    nave.moveLeft();
                    score--;
                    naveMoveu = true;
                    break;

                case 'd':
                    nave.moveRight();
                    score--;
                    naveMoveu = true;
                    break;

                case 'c':

                    Passageiro p = missao.passagemNaPosicao();

                    if (p == null) {

                        System.out.println(
                                "❌👤Nenhum passageiro nesta posição."
                        );

                    } else {

                        boolean ok =
                                missao.embarcarPassageiroNaPosicao();

                        if (ok) {

                            score += p.getPontuacao();

                            System.out.println(
                                    "\n✅👤Passageiro embarcado: +"
                                            + p.getPontuacao()
                                            + " pontos!\n"
                            );

                        } else {

                            System.out.println(
                                    "❌🚀Nave cheia, não foi possível embarcar."
                            );
                        }
                    }

                    break;

                case 'q':
                    running = false;
                    break;

                default:
                    System.out.println("Comando desconhecido.");
            }

            if (naveMoveu) {
                movimentos++;
            }

            missao.moverInimigos(
                    random,
                    minX,
                    maxX,
                    minY,
                    maxY
            );

            if (missao.verificaColisaoInimigo()) {

                nave.perderVida();

                if (nave.getVidas() == 0) {

                    System.out.println(
                            "\n💥💀 GAME OVER! 💀💥"
                    );

                    break;

                } else {

                    System.out.println(
                            "\n🚀💥👾 A nave foi atingida por um inimigo!"
                    );

                    System.out.println(
                            "\n❤️ Vidas restantes: "
                                    + nave.getVidas()
                    );
                }
            }

            if (score <= 0) {

                System.out.println(
                        "\nPontuação zerada. Missão perdida."
                );

                break;
            }

            if (missao.todosEmbarcados()
                    && (nave.getX() != 0 || nave.getY() != 0)) {

                System.out.println(
                        "\n👥 Todos os passageiros foram resgatados!"
                );

                System.out.println(
                        "\n🚀 Retorne para a plataforma de pouso L em (0,0)."
                );
            }

            if (missao.todosEmbarcados()
                    && nave.getX() == 0
                    && nave.getY() == 0) {

                long tempoFim =
                        System.currentTimeMillis();

                long duracao =
                        (tempoFim - tempoInicio) / 1000;

                System.out.println(
                        "\n🎉🚀 Pouso realizado com sucesso!"
                );

                System.out.println(
                        "\n🏆 Todos os passageiros foram resgatados. Missão concluída!"
                );

                exibirEstatisticas(
                        score,
                        duracao,
                        movimentos
                );

                boolean novoRecorde =
                        ranking.isEmpty()
                                || score > ranking.get(0).score;

                if (novoRecorde) {

                    System.out.println(
                            "🎉🏆 NOVO RECORDE DO SERVIDOR!"
                    );
                }

                if (score > 0
                        && RankingService.isTopScore(ranking, score)) {

                    RankingEntry novaEntrada =
                            new RankingEntry(
                                    pilotoNome,
                                    score,
                                    dataHoraPartida,
                                    nave.getPassageiros().size(),
                                    dificuldade
                            );

                    RankingService.adicionarAoRanking(
                            ranking,
                            novaEntrada,
                            rankingPath
                    );

                    System.out.println(
                            "🏆Novo ranking salvo! Você está entre os 5 maiores pontuadores."
                    );
                }

                break;
            }
        }
    }
    private Missao criarNovaMissao(
            int minX,
            int maxX,
            int minY,
            int maxY,
            Main.Dificuldade dificuldade
    ) {

        Nave nave = new Nave("#-1", 5);
        Missao missao = new Missao(nave);

        int quantidadeAsteroides;
        int quantidadePassageiros;

        switch (dificuldade) {

            case FACIL:
                quantidadeAsteroides = 5;
                quantidadePassageiros = 5;
                break;

            case MEDIO:
                quantidadeAsteroides = 3;
                quantidadePassageiros = 3;
                break;

            case DIFICIL:
                quantidadeAsteroides = 6;
                quantidadePassageiros = 4;
                break;

            default:
                quantidadeAsteroides = 3;
                quantidadePassageiros = 3;
        }

        while (missao.getPassageiros().size() < quantidadePassageiros) {

            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;

            if (x == nave.getX() && y == nave.getY()) {
                continue;
            }

            if (posicaoOcupada(missao, x, y)) {
                continue;
            }

            if (missao.getPassageiros().isEmpty()) {

                missao.addPassageiro(
                        new Professor("P - Dr. Silva", x, y)
                );

            } else if (missao.getPassageiros().size() == 1) {

                missao.addPassageiro(
                        new Engenheiro("E - Eng. Rosa", x, y)
                );

            } else if (missao.getPassageiros().size() == 2) {

                missao.addPassageiro(
                        new Astronauta("M - Astro. Bernardo", x, y)
                );

            } else if (missao.getPassageiros().size() == 3) {

                missao.addPassageiro(
                        new Professor("P - Dr. Lima", x, y)
                );

            } else if (missao.getPassageiros().size() == 4) {

                missao.addPassageiro(
                        new Engenheiro("E - Eng. Carlos", x, y)
                );
            }
        }

        while (missao.getAsteroides().size() < quantidadeAsteroides) {

            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;

            if (x == nave.getX() && y == nave.getY()) {
                continue;
            }

            if (posicaoOcupada(missao, x, y)) {
                continue;
            }

            missao.addAsteroide(
                    new Asteroide(x, y)
            );
        }

        while (missao.getInimigos().size() < 2) {

            int x = random.nextInt(maxX - minX + 1) + minX;
            int y = random.nextInt(maxY - minY + 1) + minY;

            if (x == nave.getX() && y == nave.getY()) {
                continue;
            }

            if (posicaoOcupada(missao, x, y)) {
                continue;
            }

            missao.addInimigo(
                    new Inimigo(x, y)
            );
        }

        return missao;
    }
    public static boolean posicaoOcupada(Missao missao, int x, int y) {
        if (missao.getNave().getX() == x && missao.getNave().getY() == y) return true;
        for (Passageiro p : missao.getPassageiros()) {
            if (p.getX() == x && p.getY() == y) return true;
        }
        for (Asteroide a : missao.getAsteroides()) {
            if (a.getX() == x && a.getY() == y) return true;
        }
        for (Inimigo i : missao.getInimigos()) {
            if (i.getX() == x && i.getY() == y) return true;
        }
        return false;
    }
    private void desenharMapa(
            Missao missao,
            int minX,
            int maxX,
            int minY,
            int maxY,
            int score,
            String pilotoNome
    ) {

        System.out.println();

        System.out.printf(
                "Mapa da Missão (Pontos: %d) - Piloto: %s%n",
                score,
                pilotoNome
        );

        System.out.print("    ");

        for (int x = minX; x <= maxX; x++) {
            System.out.printf(" %2d", x);
        }

        System.out.println();

        System.out.print("    ");

        for (int x = minX; x <= maxX; x++) {
            System.out.print(" __");
        }

        System.out.println();

        for (int y = minY; y <= maxY; y++) {

            System.out.printf("%3d|", y);

            for (int x = minX; x <= maxX; x++) {

                char symbol = '.';

                if (missao.getNave().getX() == x
                        && missao.getNave().getY() == y) {

                    symbol = '@';

                } else if (x == 0 && y == 0) {

                    symbol = 'L';

                } else {

                    for (Passageiro p : missao.getPassageiros()) {

                        if (p.getX() == x && p.getY() == y) {

                            if (p instanceof Engenheiro) {

                                symbol = 'E';

                            } else if (p instanceof Astronauta) {

                                symbol = 'M';

                            } else {

                                symbol = 'P';
                            }

                            break;
                        }
                    }

                    if (symbol == '.') {

                        for (Asteroide a : missao.getAsteroides()) {

                            if (a.getX() == x && a.getY() == y) {

                                symbol = '#';
                                break;
                            }
                        }
                    }

                    if (symbol == '.') {

                        for (Inimigo i : missao.getInimigos()) {

                            if (i.getX() == x && i.getY() == y) {

                                symbol = 'I';
                                break;
                            }
                        }
                    }
                }

                System.out.printf(" %2c", symbol);
            }

            System.out.println();
        }

        System.out.println(
                "Legenda: \n@=Nave, L=Plataforma de Pouso, P=Professor, E=Engenheiro, #=Asteroide, M=Astronauta, I=Inimigo, .=Vazio"
        );

        System.out.println(
                "Resumo de comandos: \nw ⬆️  Cima / s ⬇️  Baixo / a ⬅️  Esquerda / d ➡️  Direita, c 🛸  Embarcar, q 🚪  Sair"
        );

        System.out.println("Passageiros restantes:");

        for (Passageiro p : missao.getPassageiros()) {

            System.out.printf(
                    " - %s (%s) em (%d,%d)\n",
                    p.getNome(),
                    p.getTipo(),
                    p.getX(),
                    p.getY()
            );
        }

        System.out.println();
    }
    private Main.Dificuldade selecionarDificuldade() {

        while (true) {
            System.out.println("\nEscolha a dificuldade:");
            System.out.println("1 - Fácil");
            System.out.println("2 - Médio");
            System.out.println("3 - Difícil");
            System.out.print("Opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1":
                    return Main.Dificuldade.FACIL;

                case "2":
                    return Main.Dificuldade.MEDIO;

                case "3":
                    return Main.Dificuldade.DIFICIL;

                default:
                    System.out.println("❌ Opção inválida. Escolha 1, 2 ou 3.");
            }
        }
    }
    private void exibirEstatisticas(
            int score,
            long duracao,
            int movimentos
    ) {

        System.out.printf("\n⭐ Pontuação final: %d", score);
        System.out.println("\n⏱️ Tempo de partida: " + duracao + " segundos");
        System.out.println("\n🚀 Movimentos realizados: " + movimentos);
    }
}
