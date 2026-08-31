package missao;

import java.util.Random;

public class Inimigo {
    private int x;
    private int y;

    public Inimigo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean colideCom(Nave n) {
        return n.getX() == x && n.getY() == y;
    }

    public void mover(Random random,
            int minX,
            int maxX,
            int minY,
            int maxY) {

        int direcao = random.nextInt(4);
        switch (direcao) {
            case 0 -> {
                if (y > minY) {
                    y--;
                }
            }
            case 1 -> {
                if (y < maxY) {
                    y++;
                }
            }
            case 2 -> {
                if (x > minX) {
                    x--;
                }
            }
            case 3 -> {
                if (x < maxX) {
                    x++;
                }
            }
        }
    }
}
