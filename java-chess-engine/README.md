# Java Chess Engine

A fully functional chess game with a graphical user interface, built from the ground up using pure Java.

[//]: # "Future chess img going here"

## Features

- **Complete Chess Logic:** Implements all standard chess rules, including piece movements, captures, castling, pawn promotion, and en passant.
- **Intuitive GUI:** A clean and user-friendly interface for an enjoyable gameplay experience.
- **Visual Aids:**
  - Highlights possible moves for the selected piece.
  - Displays a list of captured pieces for both players.
- **Move History:** Keep track of all moves made throughout the game.
- **Sound Effects:** Audio feedback for in-game actions.

## Project Structure

The project's source code is organized as follows:

```
📁res/
 ├── 📁board/
 ├── 📁pieces/
 └── 📁sounds/

📁src/
 ├── 📁app/
 │      └── Program.java
 │
 ├── 📁boardgame/
 │    ├── Board.java
 │    ├── BoardException.java
 │    ├── Piece.java
 │    └── Position.java
 │
 ├── 📁chess/
 │    ├── 📁 pieces/
 │    │    ├── Bishop.java
 │    │    ├── King.java
 │    │    ├── Knight.java
 │    │    ├── Pawn.java
 │    │    ├── Queen.java
 │    │    └── Rook.java
 │    │
 │    ├── ChessException.java
 │    ├── ChessMatch.java
 │    ├── ChessPiece.java
 │    ├── ChessPosition.java
 │    └── Color.java
 │
 └── 📁UI/
      ├── ChessPanel.java
      └── UI.java
```

- `res/`: Includes all non-code assets, such as the images for the chess pieces.
- `src/`: Contains all the Java source code.
  - `app/`: The main application entry point.
  - `boardgame/`: Core classes for the game board, pieces, and positions.
  - `chess/`: Implements the specific rules and logic for chess.
    - `pieces/`: Defines each individual chess piece as a class.
  - `UI/`: Manages the graphical user interface and user interactions.

## Roadmap 🚀

Here are some of the features planned for future updates:

- **Game Timer:** Adding a clock for timed matches.
- **Undo Move:** The ability to take back the last move.
- **Player vs. Player:** Option to play as either black or white.
- **Customization:** More themes for the board and pieces.
- **Game Analysis:** Basic analysis of the game after it has concluded.

## Inspiration

This project was heavily inspired by the excellent content from:

- **Sebastian Lague's** [Chess Programming Video Series](https://www.youtube.com/playlist?list=PLFt_AvWsXl0s_C42_1_H6fc8uhK24yHI5)
- **RyiSnow's** [Java Game Dev Course](https://www.youtube.com/@RyiSnow)
