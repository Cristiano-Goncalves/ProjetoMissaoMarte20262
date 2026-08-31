package missao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        Path rankingPath = Paths.get("ranking.json");
        List<RankingEntry> ranking = loadRanking(rankingPath);

        System.out.print("Digite o nome do piloto: ");
        String pilotoNome = scanner.nextLine().trim();

        if (pilotoNome.isBlank()) {
            pilotoNome = "Piloto Anônimo";
        }

        exibirApresentacao();

        boolean executando = true;

        while (executando) {
            exibirMenuPrincipal();

            OpcaoMenu opcao = selecionarOpcaoMenu(scanner);

            switch (opcao) {
                case INICIAR_MISSAO -> jogarPartida(scanner, random, ranking, rankingPath, pilotoNome);
                case VISUALIZAR_RANKING -> exibirRanking(ranking);
                case RESETAR_RANKING -> resetarRanking(rankingPath, ranking);
                case SAIR -> executando = false;
            }
        }

        scanner.close();

        System.out.println();
        System.out.println("Fim da execução.");
    }

    // =========================================================
    // PARTIDA
    // =========================================================

    private static void jogarPartida(Scanner scanner, Random random, List<RankingEntry> ranking, Path rankingPath, String pilotoNome) {
        System.out.println();
        System.out.println("===== NOVA MISSÃO =====");
        System.out.print("Dificuldade (facil/medio/dificil): ");

        Dificuldade dificuldade = selecionarDificuldade(scanner);
        int tamanhoMapa = selecionarTamanhoMapa(scanner);

        int minX = -tamanhoMapa;
        int maxX = tamanhoMapa;
        int minY = -tamanhoMapa;
        int maxY = tamanhoMapa;

        Missao missao = criarNovaMissao(random, minX, maxX, minY, maxY, dificuldade);
        Nave nave = missao.getNave();
        int score = dificuldade.getPontuacaoInicial();

        EstatisticasMissao estatisticas = new EstatisticasMissao();
        estatisticas.iniciar();

        boolean partidaAtiva = true;

        while (partidaAtiva) {
            desenharMapa(missao, minX, maxX, minY, maxY, score, pilotoNome);

            System.out.printf(
                    "Nave em (%d,%d) | Pontos: %d | Vidas: %d | A bordo: %d/%d | Restantes: %d%n",
                    nave.getX(), nave.getY(), score, nave.getVidas(),
                    nave.getPassageiros().size(), nave.getCapacidade(),
                    missao.getPassageiros().size()
            );

            System.out.print("Para onde ir? ");
            String entrada = scanner.nextLine().trim().toLowerCase();

            if (entrada.isBlank()) {
                continue;
            }

            char comando = entrada.charAt(0);
            boolean movimentou = false;

            switch (comando) {
                case 'w', 's', 'a', 'd' -> {
                    nave.moverComLimites(comando, minX, maxX, minY, maxY);
                    score--;
                    estatisticas.registrarMovimento();
                    movimentou = true;
                }
                case 'c' -> score = tentarEmbarcarPassageiro(missao, score);
                case 'q' -> {
                    partidaAtiva = false;
                    System.out.println("Missão encerrada pelo jogador.");
                }
                default -> System.out.println("Comando desconhecido.");
            }

            if (!partidaAtiva) {
                break;
            }

            /*
             * Os inimigos se movimentam depois que
             * o jogador realiza um movimento.
             */
            if (movimentou) {
                missao.moverInimigos(random, minX, maxX, minY, maxY);
                verificarColisao(missao, nave);

                if (nave.getVidas() <= 0) {
                    System.out.println("GAME OVER!");
                    break;
                }
            }

            if (score <= 0) {
                System.out.println("Pontuação zerada. Missão perdida.");
                break;
            }

            // Vitória: todos embarcados E nave na Plataforma de Pouso (0,0)
            if (missao.todosEmbarcados()) {
                if (nave.getX() != 0 || nave.getY() != 0) {
                    System.out.println();
                    System.out.println("✨ Todos os passageiros resgatados! Retorne para a Plataforma de Pouso 'L' em (0,0) para completar a missão.");
                } else {
                    estatisticas.finalizar();

                    System.out.println();
                    System.out.println("🚀 DECOLAGEM AUTORIZADA! Nave acoplada à plataforma em (0,0).");
                    System.out.println("Missão cumprida!");

                    boolean novoRecorde = isNovoRecorde(ranking, score);
                    exibirEstatisticas(score, estatisticas, novoRecorde);
                    adicionarAoRanking(ranking, rankingPath, pilotoNome, score, dificuldade, nave);
                    break;
                }
            }
        }

        System.out.println();
        System.out.println("Retornando ao menu principal...");
    }

    // =========================================================
    // EMBARQUE
    // =========================================================

    private static int tentarEmbarcarPassageiro(Missao missao, int score) {
        Passageiro passageiro = missao.passagemNaPosicao();

        if (passageiro == null) {
            System.out.println("Nenhum passageiro nesta posição.");
            return score;
        }

        boolean embarcou = missao.embarcarPassageiroNaPosicao();

        if (!embarcou) {
            System.out.println("Nave cheia, não foi possível embarcar.");
            return score;
        }

        int bonus = passageiro.getPontuacao();
        System.out.printf("Passageiro embarcado (%s). +%d pontos!%n", passageiro.getTipo(), bonus);

        return score + bonus;
    }

    // =========================================================
    // COLISÃO
    // =========================================================

    private static void verificarColisao(Missao missao, Nave nave) {
        if (!missao.verificaColisao()) {
            return;
        }

        nave.perderVida();

        if (nave.getVidas() > 0) {
            System.out.printf("Colisão! Você perdeu 1 vida. Vidas restantes: %d%n", nave.getVidas());
        } else {
            System.out.println("Suas vidas acabaram.");
        }
    }

    // =========================================================
    // MENU PRINCIPAL
    // =========================================================

    private static void exibirMenuPrincipal() {
        System.out.println();
        System.out.println("--- MENU PRINCIPAL ---");
        System.out.println();

        for (OpcaoMenu opcao : OpcaoMenu.values()) {
            System.out.printf("%d. %s%n", opcao.getCodigo(), opcao.getDescricao());
        }

        System.out.println();
        System.out.print("Escolha uma opção: ");
    }

    private static OpcaoMenu selecionarOpcaoMenu(Scanner scanner) {
        while (true) {
            String entrada = scanner.nextLine().trim();

            try {
                int codigo = Integer.parseInt(entrada);
                OpcaoMenu opcao = OpcaoMenu.buscarPorCodigo(codigo);

                if (opcao != null) {
                    return opcao;
                }
            } catch (NumberFormatException ignored) {
            }

            System.out.print("Opção inválida. Escolha entre 1 e 4: ");
        }
    }

    // =========================================================
    // DIFICULDADE
    // =========================================================

    private static Dificuldade selecionarDificuldade(Scanner scanner) {
        while (true) {
            String opcao = scanner.nextLine().trim().toLowerCase();

            switch (opcao) {
                case "facil" -> { return Dificuldade.FACIL; }
                case "medio" -> { return Dificuldade.MEDIO; }
                case "dificil" -> { return Dificuldade.DIFICIL; }
                default -> System.out.print("Opção inválida. Digite facil, medio ou dificil: ");
            }
        }
    }

    private static int selecionarTamanhoMapa(Scanner scanner) {
        System.out.print("Tamanho do mapa (-X a +X): ");

        try {
            int tamanho = Integer.parseInt(scanner.nextLine().trim());
            if (tamanho < 2) {
                return 5;
            }
            return tamanho;
        } catch (NumberFormatException e) {
            return 5;
        }
    }

    // =========================================================
    // ESTATÍSTICAS
    // =========================================================

    private static void exibirEstatisticas(int score, EstatisticasMissao estatisticas, boolean novoRecorde) {
        System.out.println();
        System.out.println("===== ESTATÍSTICAS DA MISSÃO =====");
        System.out.printf("Pontuação final: %d pontos%n", score);
        System.out.printf("Total de movimentos: %d%n", estatisticas.getTotalMovimentos());
        System.out.printf("Duração da missão: %.2f segundos%n", estatisticas.getDuracaoSegundos());

        if (novoRecorde) {
            System.out.println("🏆 Novo recorde absoluto!");
        }

        System.out.println("==================================");
    }

    // =========================================================
    // CRIAÇÃO DA MISSÃO
    // =========================================================

    private static Missao criarNovaMissao(Random random, int minX, int maxX, int minY, int maxY, Dificuldade dificuldade) {
        Nave nave = new Nave("A-1", dificuldade.getQtdPassageiros());
        Missao missao = new Missao(nave);

        while (missao.getPassageiros().size() < dificuldade.getQtdPassageiros()) {
            int x = sortearCoordenada(random, minX, maxX);
            int y = sortearCoordenada(random, minY, maxY);
            if (posicaoOcupada(missao, x, y)) continue;
            adicionarPassageiro(missao, x, y);
        }

        while (missao.getAsteroides().size() < dificuldade.getQtdAsteroides()) {
            int x = sortearCoordenada(random, minX, maxX);
            int y = sortearCoordenada(random, minY, maxY);
            if (posicaoOcupada(missao, x, y)) continue;
            missao.addAsteroide(new Asteroide(x, y));
        }

        while (missao.getInimigos().size() < dificuldade.getQtdInimigos()) {
            int x = sortearCoordenada(random, minX, maxX);
            int y = sortearCoordenada(random, minY, maxY);
            if (posicaoOcupada(missao, x, y)) continue;
            missao.addInimigo(new Inimigo(x, y));
        }

        return missao;
    }

    private static int sortearCoordenada(Random random, int minimo, int maximo) {
        return random.nextInt(maximo - minimo + 1) + minimo;
    }

    private static void adicionarPassageiro(Missao missao, int x, int y) {
        int indice = missao.getPassageiros().size();

        switch (indice % 5) {
            case 0 -> missao.addPassageiro(new Professor("Dr. Silva", x, y));
            case 1 -> missao.addPassageiro(new Engenheiro("Eng. Rosa", x, y));
            case 2 -> missao.addPassageiro(new Astronauta("Ast. Maria", x, y));
            case 3 -> missao.addPassageiro(new Professor("Dr. Lima", x, y));
            default -> missao.addPassageiro(new Engenheiro("Eng. Carlos", x, y));
        }
    }

    private static boolean posicaoOcupada(Missao missao, int x, int y) {
        Nave nave = missao.getNave();
        if (nave.getX() == x && nave.getY() == y) return true;
        // Evita spawn na Plataforma de Pouso
        if (x == 0 && y == 0) return true;

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

    // =========================================================
    // MAPA
    // =========================================================

    private static void desenharMapa(Missao missao, int minX, int maxX, int minY, int maxY, int score, String pilotoNome) {
        System.out.println();
        System.out.printf("Mapa da Missão (Pontos: %d) - Piloto: %s%n", score, pilotoNome);

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
                char simbolo = obterSimboloMapa(missao, x, y);
                System.out.printf(" %2c", simbolo);
            }
            System.out.println();
        }

        System.out.println("Legenda: @=Nave, P=Professor, E=Engenheiro, T=Astronauta, #=Asteroide, X=Inimigo, L=Plataforma de Pouso, .=Vazio");
        System.out.println("Comandos: w(cima) / s(baixo) / a(esquerda) / d(direita) / c(embarcar) / q(sair)");
        System.out.println("Passageiros restantes:");
        for (Passageiro p : missao.getPassageiros()) {
            System.out.printf(" - %s (%s) em (%d,%d)%n", p.getNome(), p.getTipo(), p.getX(), p.getY());
        }
        System.out.println();
    }

    private static char obterSimboloMapa(Missao missao, int x, int y) {
        Nave nave = missao.getNave();

        if (nave.getX() == x && nave.getY() == y) {
            return '@';
        }

        for (Passageiro p : missao.getPassageiros()) {
            if (p.getX() != x || p.getY() != y) continue;
            if (p instanceof Engenheiro) return 'E';
            if (p instanceof Astronauta) return 'T';
            if (p instanceof Professor) return 'P';
        }

        for (Asteroide a : missao.getAsteroides()) {
            if (a.getX() == x && a.getY() == y) return '#';
        }

        for (Inimigo i : missao.getInimigos()) {
            if (i.getX() == x && i.getY() == y) return 'X';
        }

        if (x == 0 && y == 0) {
            return 'L';
        }

        return '.';
    }

    // =========================================================
    // RANKING
    // =========================================================

    private static void exibirRanking(List<RankingEntry> ranking) {
        System.out.println();
        System.out.println("===== RANKING TOP 5 =====");

        if (ranking.isEmpty()) {
            System.out.println("Ainda não há pontuações registradas.");
            return;
        }

        int posicao = 1;
        for (RankingEntry entry : ranking) {
            System.out.printf(
                    "%d. %s - %d pontos | dificuldade: %s | passageiros: %d | %s%n",
                    posicao++, entry.name, entry.score, entry.dificuldade, entry.passageirosColetados, entry.dataHora
            );
        }
    }

    private static boolean isTopScore(List<RankingEntry> ranking, int score) {
        return ranking.size() < 5 || score > ranking.get(ranking.size() - 1).score;
    }

    private static boolean isNovoRecorde(List<RankingEntry> ranking, int score) {
        return ranking.isEmpty() || score > ranking.get(0).score;
    }

    private static void adicionarAoRanking(List<RankingEntry> ranking, Path rankingPath, String pilotoNome, int score, Dificuldade dificuldade, Nave nave) {
        if (score <= 0 || !isTopScore(ranking, score)) {
            return;
        }

        String dataHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        RankingEntry entrada = new RankingEntry(
                pilotoNome, score, dificuldade.name(), nave.getPassageiros().size(), dataHora
        );

        ranking.add(entrada);
        ranking.sort(Comparator.comparingInt((RankingEntry entry) -> entry.score).reversed());

        while (ranking.size() > 5) {
            ranking.remove(ranking.size() - 1);
        }

        saveRanking(rankingPath, ranking);
        System.out.println("Pontuação adicionada ao Ranking Top 5!");
    }

    private static void resetarRanking(Path rankingPath, List<RankingEntry> ranking) {
        try {
            boolean arquivoExcluido = Files.deleteIfExists(rankingPath);
            ranking.clear();
            System.out.println();
            if (arquivoExcluido) {
                System.out.println("Histórico de ranking resetado com sucesso.");
            } else {
                System.out.println("O ranking já estava vazio.");
            }
        } catch (IOException e) {
            System.out.println("Não foi possível resetar o ranking: " + e.getMessage());
        }
    }

    private static List<RankingEntry> loadRanking(Path path) {
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }
        try {
            String json = Files.readString(path, StandardCharsets.UTF_8);
            return parseRankingJson(json.trim());
        } catch (IOException e) {
            System.out.println("Não foi possível carregar o ranking: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private static void saveRanking(Path path, List<RankingEntry> ranking) {
        StringBuilder builder = new StringBuilder("[");
        for (int i = 0; i < ranking.size(); i++) {
            RankingEntry entry = ranking.get(i);
            builder.append("{\"name\":\"")
                    .append(escapeJson(entry.name))
                    .append("\",\"score\":")
                    .append(entry.score)
                    .append(",\"dificuldade\":\"")
                    .append(escapeJson(entry.dificuldade))
                    .append("\",\"passageirosColetados\":")
                    .append(entry.passageirosColetados)
                    .append(",\"dataHora\":\"")
                    .append(escapeJson(entry.dataHora))
                    .append("\"}");
            if (i < ranking.size() - 1) {
                builder.append(",");
            }
        }
        builder.append("]");
        try {
            Files.writeString(path, builder.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Não foi possível salvar o ranking: " + e.getMessage());
        }
    }

    private static List<RankingEntry> parseRankingJson(String json) {
        List<RankingEntry> ranking = new ArrayList<>();
        if (json == null || json.isBlank() || json.equals("[]")) {
            return ranking;
        }
        json = json.trim();
        if (json.startsWith("[")) json = json.substring(1);
        if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

        int index = 0;
        while (index < json.length()) {
            int inicio = json.indexOf('{', index);
            if (inicio < 0) break;
            int fim = json.indexOf('}', inicio);
            if (fim < 0) break;

            String objeto = json.substring(inicio + 1, fim);
            String name = null;
            Integer score = null;
            String dificuldade = "N/A";
            int passageirosColetados = 0;
            String dataHora = "N/A";

            for (String parte : objeto.split(",")) {
                String[] par = parte.split(":", 2);
                if (par.length != 2) continue;
                String chave = par[0].trim().replace("\"", "");
                String valor = par[1].trim();

                switch (chave) {
                    case "name" -> name = limparStringJson(valor);
                    case "score" -> {
                        try { score = Integer.parseInt(valor); }
                        catch (NumberFormatException ignored) {}
                    }
                    case "dificuldade" -> dificuldade = limparStringJson(valor);
                    case "passageirosColetados" -> {
                        try { passageirosColetados = Integer.parseInt(valor); }
                        catch (NumberFormatException ignored) {}
                    }
                    case "dataHora" -> dataHora = limparStringJson(valor);
                    default -> {}
                }
            }

            if (name != null && score != null) {
                ranking.add(new RankingEntry(name, score, dificuldade, passageirosColetados, dataHora));
            }
            index = fim + 1;
        }

        ranking.sort(Comparator.comparingInt((RankingEntry entry) -> entry.score).reversed());
        return ranking;
    }

    private static String escapeJson(String valor) {
        return valor.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static String limparStringJson(String valor) {
        if (valor.startsWith("\"") && valor.endsWith("\"")) {
            valor = valor.substring(1, valor.length() - 1);
        }
        return valor.replace("\\\"", "\"").replace("\\\\", "\\");
    }

    // =========================================================
    // APRESENTAÇÃO
    // =========================================================

    private static void exibirApresentacao() {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("           MISSÃO MARTE UNIFOR — Console                        ");
        System.out.println("================================================================");
        System.out.println();
        System.out.println("Objetivo:");
        System.out.println(" - Localizar e embarcar todos os passageiros");
        System.out.println(" - Retornar à Plataforma de Pouso 'L' em (0,0) para concluir");
        System.out.println(" - Evitar asteroides (#) e inimigos (X)");
        System.out.println(" - Manter a pontuação acima de zero");
    }

    // =========================================================
    // ENTRADA DO RANKING
    // =========================================================

    private static class RankingEntry {
        private final String name;
        private final int score;
        private final String dificuldade;
        private final int passageirosColetados;
        private final String dataHora;

        private RankingEntry(String name, int score, String dificuldade, int passageirosColetados, String dataHora) {
            this.name = name;
            this.score = score;
            this.dificuldade = dificuldade;
            this.passageirosColetados = passageirosColetados;
            this.dataHora = dataHora;
        }
    }
}