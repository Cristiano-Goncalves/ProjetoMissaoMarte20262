package missao;

// Exercício 2 - Add novo passgeiro
public class Astronauta extends Passageiro {
    public Astronauta(String nome, int x, int y){
        super (nome, "Astronauta", x, y);
    };

    @Override//Exercicio 4 - Add pontuacao
    public int getPontuacao() {
        return 20;
    }
}
