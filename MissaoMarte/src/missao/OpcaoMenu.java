package missao;

public enum OpcaoMenu {
    INICIAR_MISSAO(1, "Iniciar Nova Missão"),
    VISUALIZAR_RANKING(2, "Visualizar Ranking Top 5"),
    RESETAR_RANKING(3, "Resetar Histórico de Ranking"),
    SAIR(4, "Sair do Jogo");

    private final int codigo;
    private final String descricao;

    OpcaoMenu(int codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public static OpcaoMenu buscarPorCodigo(int codigo) {

        for (OpcaoMenu opcao : values()) {
            if (opcao.codigo == codigo) {
                return opcao;
            }
        }

        return null;
    }
}
