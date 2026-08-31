package missao;

/**
 * Representa um asteroide estático no mapa com coordenadas (x,y).
 */
public class Asteroide {
    private int x;
    private int y;

    public Asteroide(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    /**
     * Verifica colisão simples por coincidência de coordenadas entre asteroide
     * e `Nave`.
     *
     * @param n nave a comparar
     * @return true se as coordenadas coincidirem
     */
    public boolean colideCom(Nave n) {
        // Colisão é simples igualdade de coordenadas; se futuramente o asteroide
        // tiver dimensão/movimento, ajustar essa lógica.
        return n.getX() == x && n.getY() == y;
    }
}
