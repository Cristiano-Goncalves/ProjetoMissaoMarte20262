package missao;

import java.util.ArrayList;
import java.util.List;

public class Nave {
    private String id;
    private int x;
    private int y;
    private int vidas;
    private int capacidade;
    private List<Passageiro> passageiros = new ArrayList<>();

    public Nave(String id, int capacidade) {
        this.id = id;
        this.capacidade = capacidade;
        this.vidas = 3;
        this.x = 0;
        this.y = 0;
    }

    public String getId() { return id; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getCapacidade() { return capacidade; }
    public int getVidas() { return vidas; }
    public List<Passageiro> getPassageiros() { return passageiros; }

    /** Move a nave respeitando os limites do mapa. */
    public void moverComLimites(char direcao, int minX, int maxX, int minY, int maxY) {
        switch (direcao) {
            case 'w' -> { if (y > minY) y--; }
            case 's' -> { if (y < maxY) y++; }
            case 'a' -> { if (x > minX) x--; }
            case 'd' -> { if (x < maxX) x++; }
        }
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
    }

    public boolean embarcar(Passageiro p) {
        if (passageiros.size() < capacidade) {
            passageiros.add(p);
            return true;
        }
        return false;
    }
}