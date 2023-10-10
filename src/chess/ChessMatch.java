package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

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

	public ChessPiece getEnPassantVulnerable() {
		return enPassantVulnerable;
	}

	public ChessPiece getPromoted() {
		return promoted;
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

		ChessPiece movedPiece = (ChessPiece) board.piece(target);

		check = (testCheck(opponent(currentPlayer))) ? true : false;

		// promotion :)
		if (movedPiece instanceof Pawn) {
			if (movedPiece.getColor() == Color.WHITE && target.getRow() == 0
					|| movedPiece.getColor() == Color.BLACK && target.getRow() == 7) {
				promoted = (ChessPiece) board.piece(target);
				promoted = replacePromotedPiece("Q");
			}
		}

		if (testCheckMate(opponent(currentPlayer))) {
			checkMate = true;
		}
		nextTurn();

		// En passant
		if (movedPiece instanceof Pawn
				&& (target.getRow() == initial.getRow() - 2 || target.getRow() == initial.getRow() + 2)) {
			enPassantVulnerable = movedPiece;
		} else {
			enPassantVulnerable = null;
		}

		return (ChessPiece) capturedPiece;
	}

	public ChessPiece replacePromotedPiece(String type) {
		if (promoted == null) {
			throw new IllegalStateException("There is no piece to be promoted");
		}
		if (!type.equalsIgnoreCase("B") && !type.equalsIgnoreCase("n") && !type.equalsIgnoreCase("R") && !type.equalsIgnoreCase("Q")) {
			return promoted;
		}

		Position pos = promoted.getChessPosition().toPosition();
		Piece p = board.removePiece(pos);
		piecesOnTheBoard.remove(p);

		ChessPiece newPiece = newPiece(type, promoted.getColor());
		board.placePeice(newPiece, pos);
		piecesOnTheBoard.add(newPiece);

		return newPiece;
	}

	private ChessPiece newPiece(String type, Color color) {
		if (type.equalsIgnoreCase("B"))
			return new Bishop(board, color);
		if (type.equalsIgnoreCase("N"))
			return new Knight(board, color);
		if (type.equalsIgnoreCase("Q"))
			return new Queen(board, color);
		return new Rook(board, color);

	}

	private Piece makeMove(Position initial, Position target) {
		ChessPiece p = (ChessPiece) board.removePiece(initial);
		p.increaseMoveCount();
		Piece capturedPiece = board.removePiece(target);
		board.placePeice(p, target);

		if (capturedPiece != null) {
			piecesOnTheBoard.remove(capturedPiece);
			CapturedPieces.add(capturedPiece);
		}

		// castling small
		if (p instanceof King && target.getColum() == initial.getColum() + 2) {
			Position initialT = new Position(initial.getRow(), initial.getColum() + 3);
			Position targetT = new Position(initial.getRow(), initial.getColum() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(initialT);

			board.placePeice(rook, targetT);
			rook.increaseMoveCount();
		}

		// castling Big
		if (p instanceof King && target.getColum() == initial.getColum() - 2) {
			Position initialT = new Position(initial.getRow(), initial.getColum() - 4);
			Position targetT = new Position(initial.getRow(), initial.getColum() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(initialT);

			board.placePeice(rook, targetT);
			rook.increaseMoveCount();
		}

		// En Passant
		if (p instanceof Pawn) {
			if (initial.getColum() != target.getColum() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColum());
				} else {
					pawnPosition = new Position(target.getRow() - 1, target.getColum());
				}
				capturedPiece = board.removePiece(pawnPosition);
				CapturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);

			}
		}

		return capturedPiece;

	}

	private void undoMove(Position initial, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		board.placePeice(p, initial);

		if (capturedPiece != null) {
			board.placePeice(capturedPiece, target);
			CapturedPieces.remove(capturedPiece);
			piecesOnTheBoard.add(capturedPiece);
		}

		// castling small
		if (p instanceof King && target.getColum() == initial.getColum() + 2) {
			Position initialT = new Position(initial.getRow(), initial.getColum() + 3);
			Position targetT = new Position(initial.getRow(), initial.getColum() + 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);

			board.placePeice(rook, initialT);
			rook.decreaseMoveCount();
		}

		// castling Big
		if (p instanceof King && target.getColum() == initial.getColum() - 2) {
			Position initialT = new Position(initial.getRow(), initial.getColum() - 4);
			Position targetT = new Position(initial.getRow(), initial.getColum() - 1);
			ChessPiece rook = (ChessPiece) board.removePiece(targetT);

			board.placePeice(rook, initialT);
			rook.decreaseMoveCount();
		}

		// En Passant
		if (p instanceof Pawn) {
			ChessPiece pawn = (ChessPiece) board.removePiece(target);

			if (initial.getColum() != target.getColum() && capturedPiece == enPassantVulnerable) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColum());
				} else {
					pawnPosition = new Position(4, target.getColum());
				}

				board.placePeice(pawn, pawnPosition);

			}
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
		List<Piece> list = piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)
				.collect(Collectors.toList());
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < board.getRows(); i++) {
				for (int j = 0; j < board.getColumns(); j++) {
					if (mat[i][j]) {
						Position initial = ((ChessPiece) p).getChessPosition().toPosition();
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
		placePiece('a', 1, new Rook(board, Color.WHITE));
		placePiece('e', 1, new King(board, Color.WHITE, this));
		placePiece('h', 1, new Rook(board, Color.WHITE));
		placePiece('c', 1, new Bishop(board, Color.WHITE));
		placePiece('f', 1, new Bishop(board, Color.WHITE));
		placePiece('b', 1, new Knight(board, Color.WHITE));
		placePiece('g', 1, new Knight(board, Color.WHITE));
		placePiece('d', 1, new Queen(board, Color.WHITE));
		placePiece('a', 2, new Pawn(board, Color.WHITE, this));
		placePiece('b', 2, new Pawn(board, Color.WHITE, this));
		placePiece('c', 2, new Pawn(board, Color.WHITE, this));
		placePiece('d', 2, new Pawn(board, Color.WHITE, this));
		placePiece('e', 2, new Pawn(board, Color.WHITE, this));
		placePiece('f', 2, new Pawn(board, Color.WHITE, this));
		placePiece('g', 2, new Pawn(board, Color.WHITE, this));
		placePiece('h', 2, new Pawn(board, Color.WHITE, this));

		placePiece('a', 8, new Rook(board, Color.BLACK));
		placePiece('e', 8, new King(board, Color.BLACK, this));
		placePiece('h', 8, new Rook(board, Color.BLACK));
		placePiece('c', 8, new Bishop(board, Color.BLACK));
		placePiece('f', 8, new Bishop(board, Color.BLACK));
		placePiece('b', 8, new Knight(board, Color.BLACK));
		placePiece('g', 8, new Knight(board, Color.BLACK));
		placePiece('d', 8, new Queen(board, Color.BLACK));
		placePiece('a', 7, new Pawn(board, Color.BLACK, this));
		placePiece('b', 7, new Pawn(board, Color.BLACK, this));
		placePiece('c', 7, new Pawn(board, Color.BLACK, this));
		placePiece('d', 7, new Pawn(board, Color.BLACK, this));
		placePiece('e', 7, new Pawn(board, Color.BLACK, this));
		placePiece('f', 7, new Pawn(board, Color.BLACK, this));
		placePiece('g', 7, new Pawn(board, Color.BLACK, this));
		placePiece('h', 7, new Pawn(board, Color.BLACK, this));

	}

}
