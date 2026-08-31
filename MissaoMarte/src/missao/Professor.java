package missao;

public class Professor extends Passageiro {
    public Professor(String nome, int x, int y) {
        super(nome, "Professor", x, y);
    }

    @Override//Exercicio 4 - Add pontuacao
    public int getPontuacao() {
        return 10;
    }
}

