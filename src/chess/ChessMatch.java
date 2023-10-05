package chess;

import boardgame.Board;
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
		for (int i =0; i<board.getRows(); i++) {
			for (int j=0; j<board.getColumns();j++) {
				mat[i][j] =(ChessPiece) board.piece(i, j);
			}
		}
		return mat;
	}
	
	private void placePiece(char column, int row, ChessPiece piece) {
		board.placePeice(piece, new ChessPosition(column, row).toPosition());
	}
	
	private void initialSetup() {
		placePiece('g', 1, new Rook(board, Color.WHITE));
		placePiece('g', 5, new King(board, Color.WHITE));
	}
}
