package missao;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public enum Dificuldade{
        FACIL,
        MEDIO,
        DIFICIL
    }

    public static void main(String[] args) {
        Random random = new Random();

        Path rankingPath = Paths.get("ranking.json");
        List<RankingEntry> ranking = RankingService.loadRanking(rankingPath);

        Scanner scanner = new Scanner(System.in);

        Jogo jogo = new Jogo(
                scanner,
                random,
                ranking,
                rankingPath
        );


        boolean playAgain = true;
        while (playAgain) {
            int opcaoMenu = exibirMenu(scanner);

            if (opcaoMenu == 2) {
                System.out.println("\n🏆 Ranking Top 5:");

                if (ranking.isEmpty()) {
                    System.out.println(" - Ainda não há pontuações registradas.");
                } else {
                    printRanking(ranking);
                }

                continue;
            }

            if (opcaoMenu == 3) {

                RankingService.resetarRanking(rankingPath);
                ranking.clear();

                continue;
            }

            if (opcaoMenu == 4) {

                System.out.println("\n🚪 Saindo do jogo...");
                playAgain = false;

                continue;
            }

            if (opcaoMenu != 1) {

                System.out.println("\n❌ Opção inválida. Escolha 1, 2, 3 ou 4.");

                continue;
            }

            //Exercicio 6 - Mapa expandivel
            System.out.print("\n\uD83D\uDDFA\uFE0FTamanho do mapa (-X a +X): ");
            int tamanho = Integer.parseInt(scanner.nextLine());

            int minX = -tamanho;
            int maxX = tamanho;
            int minY = -tamanho;
            int maxY = tamanho;

            System.out.print("\n\uD83D\uDC68\u200D✈\uFE0FDigite o nome do piloto: ");
            String pilotoNome = scanner.nextLine().trim();
            if (pilotoNome.isEmpty()) {
                pilotoNome = "\n\uD83D\uDC68\u200D✈\uFE0FPiloto Anônimo";
            }

            System.out.println("\n================================================================\n");
            System.out.println("Missão Marte Unifor — Console");
            System.out.println();
            System.out.println("\uD83C\uDFC6Ranking dos melhores pilotos:\uD83C\uDFC6 ");
            if (ranking.isEmpty()) {
                System.out.println(" - Ainda não há pontuações registradas.");
            } else {
                printRanking(ranking);
            }

            System.out.println();
            System.out.println(pilotoNome + ", Bem-vindo (a) à Missão Marte Unifor! Sua nave foi selecionada para uma expedição de resgate e pesquisa na superfície marciana.");
            System.out.println("Seu objetivo é localizar e embarcar todos os passageiros necessários para completar a missão antes que o seu tempo (pontuação) chegue a zero.");
            System.out.println();
            System.out.println("Objetivo:");
            System.out.println(" - Mover a nave pelo mapa");
            System.out.println(" - Encontrar e embarcar todos os passageiros");
            System.out.println(" - Evitar colisões com asteroides");
            System.out.println(" - Manter a pontuação acima de zero");
            System.out.println();
            System.out.println("Comandos:");
            System.out.println(" - w: mover para ⬆\uFE0F  Cima");
            System.out.println(" - s: mover para ⬇\uFE0F  Baixo");
            System.out.println(" - a: mover para ⬅\uFE0F  Esquerda");
            System.out.println(" - d: mover para ➡\uFE0F  Direita");
            System.out.println(" - c: \uD83D\uDEF8  Embarcar passageiro na posição atual");
            System.out.println(" - q: \uD83D\uDEAA  Sair do jogo");
            System.out.println();
            System.out.println("Pontuação inicial: nível fácil 30 pontos, nível médio 20 pontos e nível difícil 15 pontos.\n Cada movimento custa 1 ponto.\n Cada embarque vale +10 pontos para Professor, +15 pontos para Engenheiro e +20 pontos para Astronauta.");
            System.out.println();
            System.out.println("Pressione Enter para ▶\uFE0F Iniciar a missão...");
            scanner.nextLine();
            System.out.println("================================================================");

            jogo.jogarPartida(
                    minX,
                    maxX,
                    minY,
                    maxY,
                    pilotoNome
            );

        }

        scanner.close();
        System.out.println("\n\uD83D\uDED1Fim da execução.");
    }
    private static int exibirMenu(Scanner scanner) {

        while (true) {

            System.out.println("\n==============================");
            System.out.println("🚀 MISSÃO MARTE UNIFOR");
            System.out.println("==============================");
            System.out.println("1 - ▶️ Iniciar Nova Missão");
            System.out.println("2 - 🏆 Visualizar Ranking Top 5");
            System.out.println("3 - 🗑️ Resetar Histórico de Ranking");
            System.out.println("4 - 🚪 Sair do Jogo");
            System.out.print("\nEscolha uma opção: ");

            String opcao = scanner.nextLine().trim();

            switch (opcao) {

                case "1":
                    return 1;

                case "2":
                    return 2;

                case "3":
                    return 3;

                case "4":
                    return 4;

                default:
                    System.out.println("\n❌ Opção inválida. Escolha 1, 2, 3 ou 4.");
            }
        }
    }

    private static void printRanking(List<RankingEntry> ranking) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int position = 1;

        for (RankingEntry entry : ranking) {

            System.out.printf(
                    "%d. %s - %d pontos | Passageiros: %d | Dificuldade: %s | Data: %s%n",
                    position++,
                    entry.name,
                    entry.score,
                    entry.passageirosColetados,
                    entry.dificuldade,
                    entry.dataHora.format(formatter)
            );
        }
    }

}
