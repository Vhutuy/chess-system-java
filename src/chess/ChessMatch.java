package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;

	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> CapturedPieces = new ArrayList<>();

	public ChessMatch() {
		board = new Board(8, 8);
		turn = 1;
		currentPlayer = Color.WHITE;
		initialSetup();
	}

	public int getTurn() {
		return turn;
	}

	public Color getcurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return check;
	}

	public boolean getCheckMate() {
		return checkMate;
	}

	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
		for (int i = 0; i < board.getRows(); i++) {
			for (int j = 0; j < board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}

	public boolean[][] possibleMoves(ChessPosition initalPosition) {
		Position position = initalPosition.toPosition();
		validateSourcePosition(position);
		return board.piece(position).possibleMoves();
	}

	private void placePiece(char column, int row, ChessPiece piece) {
		board.placePeice(piece, new ChessPosition(column, row).toPosition());
		piecesOnTheBoard.add(piece);
	}

	public ChessPiece performeChessMove(ChessPosition initialPosition, ChessPosition targetPosition) {
		Position initial = initialPosition.toPosition();
		Position target = targetPosition.toPosition();

		validateSourcePosition(initial);
		validateTargetPosition(initial, target);

		Piece capturedPiece = makeMove(initial, target);

		if (testCheck(currentPlayer)) {
			undoMove(initial, target, capturedPiece);
			throw new ChessExceptions("You can't put tourself in check!");
		}
		
		
		
		check = (testCheck(opponent(currentPlayer))) ? true : false;

		
		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		nextTurn();
		return (ChessPiece) capturedPiece;
	}

	private Piece makeMove(Position initial, Position target) {
		ChessPiece p = (ChessPiece)board.removePiece(initial);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePeice(p, target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			CapturedPieces.add(capturedPiece);
		}
		return capturedPiece;

	}

	private void undoMove(Position initial, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece)board.removePiece(target);
		p.decreaseMoveCount();
		board.placePeice(p, initial);

		if (capturedPiece != null) {
			board.placePeice(capturedPiece, target);
			CapturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}

	}

	private void validateTargetPosition(Position initial, Position target) {
		if (!board.piece(initial).possibleMove(target)) {
			throw new ChessExceptions("The chosen piece can't move to target position!");
		}

	}

	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private void validateSourcePosition(Position position) {
		if (!board.threIsAPiece(position)) {
			throw new ChessExceptions("There is no piece in source position!");
		}
		if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
			throw new ChessExceptions("The chosen piece is not yours!");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessExceptions("There is no possible moves for this piece!");
		}
	}

	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}

	private ChessPiece king(Color color) {
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece) p;
			}
		}
		throw new IllegalStateException("There is no " + color + " king on the board");
	}

	private boolean testCheck(Color color) {
		Position kingPosition = king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = piecesOnTheBoard.stream()
				.filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColum()]) {
				return true;
			}
		}
		return false;
	}

	private boolean testCheckMate(Color color) {
		if (!testCheck(color)) {
			return false;
		}
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
		for (Piece p:list) {
			boolean[][] mat = p.possibleMoves();
			for (int  i=0;i<board.getRows(); i++) {
				for (int j=0;j<board.getColumns(); j++) {
					if(mat[i][j]) {
						Position initial = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						Piece capturedPiece = makeMove(initial, target);
						boolean testCheck = testCheck(color);
						undoMove(initial, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
			}
		}
		
		return true;
	}

	private void initialSetup() {
		placePiece('h', 7, new Rook(board, Color.WHITE));
		placePiece('d', 1, new Rook(board, Color.WHITE));
		placePiece('e', 1, new King(board, Color.WHITE));

		placePiece('b', 8, new Rook(board, Color.BLACK));
		placePiece('a', 8, new King(board, Color.BLACK));

	}

}
