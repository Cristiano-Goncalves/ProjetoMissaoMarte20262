package missao;

public class Engenheiro extends Passageiro {
    public Engenheiro(String nome, int x, int y) {
        super(nome, "Engenheiro", x, y);
    }

    @Override//Exercicio 4 - Add pontuacao
    public int getPontuacao() {
        return 15;
    }
}
