# Motor de Xadrez em Java

Um jogo de xadrez completo escrito **em Java puro** — sem engines externas, sem
bibliotecas de terceiros, sem framework de UI além do Swing da própria JDK. Todas
as regras oficiais, a geração de lances legais, a detecção de xeque-mate, a
notação algébrica e a exportação em PGN foram implementadas do zero.

> **Sobre o nome:** o bot com IA (Minimax com poda alfa-beta) ainda não existe — é o
> próximo passo. Hoje o repositório tem a engine de regras e a interface para dois
> jogadores locais. Ver [Estado do projeto](#estado-do-projeto).

## O que está implementado

**Regras completas.** Movimentação e captura de todas as peças, **roque** (curto e
longo, validando que o rei não parte, cruza nem chega em casa atacada), **en
passant**, **promoção de peão** com diálogo de escolha, **xeque**, **xeque-mate** e
**afogamento** (*stalemate*).

**Geração de lances legais de verdade.** `GameState.generateLegalMoves` não devolve
apenas os movimentos geométricos da peça: cada candidato é simulado e descartado se
deixar o próprio rei em xeque. É isso que faz o mate e o afogamento caírem
naturalmente — `hasAnyLegalMove` distingue os dois casos pela presença de xeque.

**Notação algébrica padrão.** `AlgebraicNotation` gera SAN com **desambiguação**
(quando duas peças iguais alcançam a mesma casa, acrescenta coluna, linha ou ambas)
e sufixo `+` / `#`.

**Exportação PGN.** `Ctrl+S` (ou *Game → Save PGN*) grava a partida em `.pgn` com
cabeçalhos e resultado — o arquivo abre em Lichess, ChessBase ou qualquer leitor.

**Interface.** Tabuleiro em Swing desenhado à mão, com destaque dos lances
possíveis da peça selecionada, lista de peças capturadas, histórico de lances e
temas de tabuleiro alternáveis (marrom / cinza).

## Como rodar

Requer **JDK 17+** (o código usa `switch` de expressão).

```bash
git clone https://github.com/BudaBecker/AI-chess-bot.git
cd AI-chess-bot/java-chess-engine

javac -d bin src/app/*.java src/chess/*.java src/chess/pieces/*.java src/UI/*.java
java -cp bin app.Program
```

> Rode a partir de `java-chess-engine/` — as imagens do tabuleiro e das peças são
> carregadas por caminho relativo a partir de `res/`.

## Estrutura

```
java-chess-engine/
├── src/
│   ├── app/Program.java              # janela, menu e ponto de entrada
│   ├── chess/
│   │   ├── GameState.java            # lances legais, xeque, mate, afogamento
│   │   ├── AlgebraicNotation.java    # SAN com desambiguação
│   │   ├── Move.java                 # tipos de lance e estado da peça
│   │   ├── Piece.java · Color.java
│   │   └── pieces/                   # Bishop, King, Knight, Pawn, Queen, Rook
│   └── UI/
│       ├── ChessPanel.java           # laço do jogo, render e exportação PGN
│       ├── BoardUI.java              # desenho do tabuleiro e temas
│       ├── PromotionDialog.java · Mouse.java · SoundManager.java
└── res/                              # sprites das peças e texturas do tabuleiro
```

## Estado do projeto

A **engine e a interface estão completas e jogáveis** para partidas de dois
jogadores locais.

O que ainda **não** existe:

- [ ] **Bot com IA** — busca por Minimax com poda alfa-beta e função de avaliação.
      É o próximo passo e dá nome ao repositório; ainda não há nenhuma linha escrita.
- [ ] **Bitboards** — a representação atual é uma `ArrayList<Piece>`, suficiente para
      jogar mas lenta para uma busca em profundidade. Migrar é pré-requisito da IA.
- [ ] **Efeitos sonoros** — `SoundManager` está implementado e é chamado nos eventos
      certos, mas os arquivos `res/sounds/*.wav` não estão no repositório; o áudio
      degrada em silêncio.
- [ ] Relógio de partida, desfazer lance e importação de PGN.

## Inspiração

Projeto fortemente inspirado pelo conteúdo de:

- **Sebastian Lague** — [Chess Programming Video Series](https://www.youtube.com/playlist?list=PLFt_AvWsXl0s_C42_1_H6fc8uhK24yHI5)
- **RyiSnow** — [Java Game Dev Course](https://www.youtube.com/@RyiSnow)

## Licença

MIT.
