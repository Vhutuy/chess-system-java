package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

	public Queen(Board board, Color color) {
		super(board, color);
		
	}
	
	@Override
	public String toString() {
		return "Q";
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
		
		//Above
		  p.setValues(position.getRow() - 1, position.getColum());
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setRow(p.getRow() - 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		  
		//left
		  p.setValues(position.getRow(), position.getColum() - 1);
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setColum(p.getColum() - 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		  
		//Right
		  p.setValues(position.getRow(), position.getColum() + 1);
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setColum(p.getColum() + 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		  
		  //bottom
		  p.setValues(position.getRow() + 1, position.getColum());
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setRow(p.getRow() + 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		
		  //sides
		  p.setValues(position.getRow() - 1, position.getColum() - 1);
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setRow(p.getRow() - 1);
			  p.setColum(p.getColum() - 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		  
		  p.setValues(position.getRow() + 1, position.getColum() - 1);
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setRow(p.getRow() + 1);
			  p.setColum(p.getColum() - 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		  
		  p.setValues(position.getRow() - 1, position.getColum() + 1);
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setRow(p.getRow() - 1);
			  p.setColum(p.getColum() + 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		  
		  p.setValues(position.getRow() + 1, position.getColum() + 1);
		  while (getBoard().positionExists(p) && !getBoard().threIsAPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
			  p.setRow(p.getRow() + 1);
			  p.setColum(p.getColum() + 1);
		  }
		  if(getBoard().positionExists(p) && isThereOpponentPiece(p)) {
			  mat[p.getRow()][p.getColum()] = true;
		  }
		  
		return mat;
	}

}
