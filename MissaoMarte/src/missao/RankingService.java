package missao;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingService {

    public static void resetarRanking(Path rankingPath) {

        try {
            Files.write(
                    rankingPath,
                    "[]".getBytes(StandardCharsets.UTF_8)
            );

            System.out.println("\n✅ Ranking resetado com sucesso!");

        } catch (IOException e) {
            System.out.println("\n🛑 Não foi possível resetar o ranking.");
        }
    }

    public static boolean isTopScore(List<RankingEntry> ranking, int score) {

        if (ranking.size() < 5) {
            return true;
        }

        return score > ranking.get(ranking.size() - 1).score;
    }

    public static List<RankingEntry> loadRanking(Path path) {

        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        try {
            String json = new String(
                    Files.readAllBytes(path),
                    StandardCharsets.UTF_8
            ).trim();

            return parseRankingJson(json);

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void saveRanking(Path path, List<RankingEntry> ranking) {

        StringBuilder builder = new StringBuilder();
        builder.append("[");

        for (int i = 0; i < ranking.size(); i++) {

            RankingEntry entry = ranking.get(i);

            builder.append("{\"name\":\"")
                    .append(entry.name.replace("\"", "\\\""))
                    .append("\",\"score\":")
                    .append(entry.score)
                    .append(",\"dataHora\":\"")
                    .append(entry.dataHora)
                    .append("\",\"passageirosColetados\":")
                    .append(entry.passageirosColetados)
                    .append(",\"dificuldade\":\"")
                    .append(entry.dificuldade)
                    .append("\"}");

            if (i < ranking.size() - 1) {
                builder.append(",");
            }
        }

        builder.append("]");

        try {
            Files.write(
                    path,
                    builder.toString().getBytes(StandardCharsets.UTF_8)
            );

        } catch (IOException e) {
            System.out.println("🛑 Não foi possível salvar o ranking: " + e.getMessage());
        }
    }

    public static void adicionarAoRanking(
            List<RankingEntry> ranking,
            RankingEntry novaEntrada,
            Path rankingPath
    ) {

        ranking.add(novaEntrada);

        ranking.sort(
                Comparator.comparingInt(
                        (RankingEntry e) -> e.score
                ).reversed()
        );

        while (ranking.size() > 5) {
            ranking.remove(ranking.size() - 1);
        }

        saveRanking(rankingPath, ranking);
    }

    private static List<RankingEntry> parseRankingJson(String json) {
        List<RankingEntry> ranking = new ArrayList<>();
        if (json.isEmpty() || json.equals("[]")) {
            return ranking;
        }
        json = json.trim();
        if (json.startsWith("[")) {
            json = json.substring(1);
        }
        if (json.endsWith("]")) {
            json = json.substring(0, json.length() - 1);
        }

        int index = 0;
        while (index < json.length()) {
            int start = json.indexOf('{', index);
            if (start < 0) break;
            int end = json.indexOf('}', start);
            if (end < 0) break;
            String object = json.substring(start + 1, end);
            String name = null;
            Integer score = null;
            LocalDateTime dataHora = null;
            Integer passageirosColetados = null;
            Main.Dificuldade dificuldade = null;
            for (String part : object.split(",")) {
                String[] pair = part.split(":", 2);
                if (pair.length != 2) continue;
                String key = pair[0].trim().replaceAll("\"", "");
                String value = pair[1].trim();
                if (key.equals("name")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        name = value.substring(1, value.length() - 1).replace("\\\"", "\"");
                    }
                } else if (key.equals("score")) {
                    try {
                        score = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                    }
                } else if (key.equals("dataHora")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        String dataHoraTexto = value.substring(1, value.length() - 1);
                        dataHora = LocalDateTime.parse(dataHoraTexto);
                    }

                } else if (key.equals("passageirosColetados")) {
                    try {
                        passageirosColetados = Integer.parseInt(value);
                    } catch (NumberFormatException ignored) {
                    }

                } else if (key.equals("dificuldade")) {
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        String dificuldadeTexto = value.substring(1, value.length() - 1);

                        try {
                            dificuldade = Main.Dificuldade.valueOf(dificuldadeTexto);
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }
            if (name != null
                    && score != null
                    && dataHora != null
                    && passageirosColetados != null
                    && dificuldade != null) {

                ranking.add(new RankingEntry(
                        name,
                        score,
                        dataHora,
                        passageirosColetados,
                        dificuldade
                ));
            }
            index = end + 1;
        }

        ranking.sort(Comparator.comparingInt((RankingEntry e) -> e.score).reversed());
        return ranking;
    }

}
