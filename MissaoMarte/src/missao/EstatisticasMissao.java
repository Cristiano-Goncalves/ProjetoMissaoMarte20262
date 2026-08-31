    package missao;

    public class EstatisticasMissao {
        private int totalMovimentos;
        private long inicioMissao;
        private long fimMissao;

        public EstatisticasMissao() {
            this.totalMovimentos = 0;
        }

        public void iniciar() {
            inicioMissao = System.currentTimeMillis();
        }

        public void registrarMovimento() {
            totalMovimentos++;
        }

        public void finalizar() {
            fimMissao = System.currentTimeMillis();
        }

        public int getTotalMovimentos() {
            return totalMovimentos;
        }

        public double getDuracaoSegundos() {
            return (fimMissao - inicioMissao) / 1000.0;
        }
    }
