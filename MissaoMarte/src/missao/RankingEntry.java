package missao;

import java.time.LocalDateTime;

public class RankingEntry {
    final String name;
    final int score;
    final LocalDateTime dataHora;
    final int passageirosColetados;
    final Main.Dificuldade dificuldade;

    public RankingEntry(
            String name,
            int score,
            LocalDateTime dataHora,
            int passageirosColetados,
            Main.Dificuldade dificuldade
    ) {
        this.name = name;
        this.score = score;
        this.dataHora = dataHora;
        this.passageirosColetados = passageirosColetados;
        this.dificuldade = dificuldade;
    }
}
