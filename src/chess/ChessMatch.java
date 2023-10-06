package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {

	private Board board;

	public ChessMatch() {
		board = new Board(8, 8);
		initialSetup();
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
	}

	public ChessPiece performeChessMove(ChessPosition initialPosition, ChessPosition targetPosition) {
			Position initial = initialPosition.toPosition();
			Position target = targetPosition.toPosition();
			
			validateSourcePosition(initial);
			validateTargetPosition(initial, target);
			
			Piece capturedPiece = makeMove(initial, target);
			return (ChessPiece)capturedPiece;
	}
	
	

	private Piece makeMove(Position initial, Position target) {
		Piece p = board.removePiece(initial);
		Piece capturedPiece = board.removePiece(target);
		board.placePeice(p, target);
		return capturedPiece;
	}
	
	private void validateTargetPosition(Position initial, Position target) {
		if (!board.piece(initial).possibleMove(target)) {
			throw new ChessExceptions("The chosen piece can't move to target position!");
		}
		
	}
	
	private void validateSourcePosition(Position position) {
		if(!board.threIsAPiece(position)) {
			throw new ChessExceptions("There is no piece in source position!");
		}
		if (!board.piece(position).isThereAnyPossibleMove()) {
			throw new ChessExceptions("There is no possible moves for this piece!");
		}
	}

	private void initialSetup() {
		placePiece('c', 1, new Rook(board, Color.WHITE));
		placePiece('c', 2, new Rook(board, Color.WHITE));
		placePiece('d', 2, new Rook(board, Color.WHITE));
		placePiece('e', 2, new Rook(board, Color.WHITE));
		placePiece('e', 1, new Rook(board, Color.WHITE));
		placePiece('d', 1, new King(board, Color.WHITE));

		placePiece('c', 7, new Rook(board, Color.BLACK));
		placePiece('c', 8, new Rook(board, Color.BLACK));
		placePiece('d', 7, new Rook(board, Color.BLACK));
		placePiece('e', 7, new Rook(board, Color.BLACK));
		placePiece('e', 8, new Rook(board, Color.BLACK));
		placePiece('d', 8, new King(board, Color.BLACK));

	}

}
