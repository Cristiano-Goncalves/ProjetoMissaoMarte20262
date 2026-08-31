package missao;

import java.util.Random;

public class Inimigo {
    private int x;
    private int y;

    public Inimigo(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { return x; }
    public int getY() { return y; }

    public void moverInimigos(
            Random r,
            int minX,
            int maxX,
            int minY,
            int maxY
    ) {

        boolean moveu = false;

        while (!moveu) {

            int direcao = r.nextInt(4);

            switch (direcao) {

                case 0:
                    if (x < maxX) {
                        x++;
                        moveu = true;
                    }
                    break;

                case 1:
                    if (x > minX) {
                        x--;
                        moveu = true;
                    }
                    break;

                case 2:
                    if (y < maxY) {
                        y++;
                        moveu = true;
                    }
                    break;

                case 3:
                    if (y > minY) {
                        y--;
                        moveu = true;
                    }
                    break;
            }
        }
    }

    public boolean colideCom(Nave n) {
        return n.getX() == x && n.getY() == y;
    }

}
