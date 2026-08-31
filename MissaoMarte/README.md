# Missão Marte Unifor

> **Nome do grupo:** Grupo 01
>
> **Link do repositório:** https://github.com/Cristiano-Goncalves/ProjetoMissaoMarte20262

Projeto em Java (console) desenvolvido para a disciplina Projeto de arquitetura de sistemas, aplicando conceitos de
Programação Orientada a Objetos: herança, polimorfismo, encapsulamento e abstração.

---

## Integrantes

| Nome | Matrícula |
|---|---|
| Antônio Cristiano | 2526035 |
| Iandeyara Farias | 2526388 |
| Myrla Rodrigues | 2526284 |

---

## Sobre o projeto

Jogo de console em que a nave se movimenta por um grid, embarcando passageiros
(professores e engenheiros) e desviando de asteroides e inimigos. Ao final, a
pontuação é registrada em um ranking.

**Objetivo:** embarcar todos os passageiros sem colidir com os obstáculos.

### Comandos

| Tecla | Ação |
|---|---|
| `w` | Mover para cima |
| `s` | Mover para baixo |
| `a` | Mover para a esquerda |
| `d` | Mover para a direita |
| `c` | Embarcar (se houver passageiro na mesma posição) |
| `q` | Sair do jogo |

---

## Estrutura do projeto

```
ProjetoMissaoMarte20262/
├── README.md
└── MissaoMarte/
    ├── MissaoMarte.iml            # Módulo do IntelliJ IDEA
    ├── README.md
    ├── TUTORIAL-MISSAO-MARTE.md
    └── src/
        └── missao/
            ├── Main.java            # Ponto de entrada (main)
            ├── Jogo.java            # Laço principal e leitura dos comandos
            ├── Missao.java          # Regras e estado da missão
            ├── Nave.java            # Nave controlada pelo jogador
            ├── Passageiro.java      # Classe base dos passageiros
            ├── Astronauta.java      # Especialização de Passageiro
            ├── Professor.java       # Especialização de Passageiro
            ├── Engenheiro.java      # Especialização de Passageiro
            ├── Asteroide.java       # Obstáculo do mapa
            ├── Inimigo.java         # Obstáculo móvel
            ├── RankingEntry.java    # Registro de pontuação
            └── RankingService.java  # Gerenciamento do ranking
```

### Conceitos de OO aplicados

- **Herança:** `Astronauta`, `Professor` e `Engenheiro` estendem `Passageiro`.
- **Polimorfismo:** os diferentes tipos de passageiro são tratados de forma uniforme pela `Missao`.
- **Encapsulamento:** atributos privados com acesso controlado por métodos.
- **Abstração:** `Passageiro` define o contrato comum aos tipos concretos.

---

## Requisitos

- JDK 17 ou superior (verifique com `java -version`)
- Terminal / prompt de comando, ou IntelliJ IDEA

---

## Compilação e execução

### Pelo terminal

Execute os comandos **a partir da raiz do repositório**.

**Linux / macOS**

```bash
javac -d out MissaoMarte/src/missao/*.java
java -cp out missao.Main
```

**Windows (PowerShell / CMD)**

```bat
javac -d out MissaoMarte\src\missao\*.java
java -cp out missao.Main
```

A pasta `out/` é criada automaticamente e contém os arquivos `.class` gerados.

### Pelo IntelliJ IDEA

1. `File > Open` e selecione a pasta `MissaoMarte`.
2. Aguarde a indexação do projeto (o módulo `MissaoMarte.iml` já está configurado).
3. Abra `src/missao/Main.java` e clique no botão ▶ ao lado do método `main`.

---

## Licença

Trabalho acadêmico — Universidade de Fortaleza (Unifor).
