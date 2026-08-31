# Missão Marte Unifor

> **Nome do grupo:** Grupo 01
>
> **Link do repositório:** <https://github.com/Cristiano-Goncalves/ProjetoMissaoMarte20262>

Projeto em Java (console) desenvolvido para a disciplina **Projeto de Arquitetura de Sistemas** do curso de ADS na Universidade de Fortaleza (Unifor), aplicando conceitos de Programação Orientada a Objetos: herança, polimorfismo, encapsulamento, abstração e persistência em arquivo JSON.

---

## Integrantes

| Nome              | Matrícula | GitHub |
|-------------------|-----------|--------|
| Antônio Cristiano | 2526035   | [@Cristiano-Goncalves](https://github.com/Cristiano-Goncalves) |
| Iandeyara Farias  | 2526388   | [@Iandaa](https://github.com/Iandaa) |
| Myrla Rodrigues   | 2526284   | [@myyrla](https://github.com/myyrla) |

---

## Sobre o projeto

Jogo de console em que a nave se movimenta por um grid, embarcando passageiros (professores, engenheiros e astronautas) e desviando de asteroides e inimigos. Ao final, a pontuação é registrada em um ranking Top 5 persistido em disco.

**Objetivo:** embarcar todos os passageiros e retornar à Plataforma de Pouso `L` em `(0,0)` para concluir a missão, sem que a pontuação zere ou a nave perca todas as vidas.

### Comandos

| Tecla | Ação |
|-------|------|
| `w`   | Mover para cima |
| `s`   | Mover para baixo |
| `a`   | Mover para a esquerda |
| `d`   | Mover para a direita |
| `c`   | Embarcar (se houver passageiro na mesma posição) |
| `q`   | Sair do jogo |

### Legenda do mapa

`@` Nave · `L` Plataforma de Pouso · `P` Professor · `E` Engenheiro · `T` Astronauta · `X` Inimigo · `#` Asteroide · `.` Vazio

---

## Estrutura do projeto

```
ProjetoMissaoMarte20262/
├── .gitignore
├── README.md
└── MissaoMarte/
    ├── MissaoMarte.iml
    ├── TUTORIAL-MISSAO-MARTE.md
    └── src/
        └── missao/
            ├── Main.java
            ├── Missao.java
            ├── Nave.java
            ├── Passageiro.java
            ├── Professor.java
            ├── Engenheiro.java
            ├── Astronauta.java
            ├── Asteroide.java
            ├── Inimigo.java
            ├── Dificuldade.java
            ├── OpcaoMenu.java
            └── EstatisticasMissao.java
```

### Conceitos de OO aplicados

- **Herança:** `Astronauta`, `Professor` e `Engenheiro` estendem `Passageiro`.
- **Polimorfismo:** cada tipo de passageiro sobrescreve `getPontuacao()` retornando um bônus distinto (+10, +15, +20).
- **Encapsulamento:** atributos privados com acesso controlado por getters; estatísticas isoladas em classe própria.
- **Abstração:** `Passageiro` define o contrato comum aos tipos concretos.
- **Enums:** `Dificuldade` e `OpcaoMenu` isolam constantes e comportamentos associados.

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

```powershell
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
