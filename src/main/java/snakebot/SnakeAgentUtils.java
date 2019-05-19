package snakebot;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import snakegame.Collidable;
import snakegame.CollidableType;
import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;

public class SnakeAgentUtils {

	public static List<Move> getAvailableMoves(SnakeHead me, Collidable[][] board){
		List<Move> availableMoves = new ArrayList<Move>();
		
		if(me.x > 0 && (board[me.x-1][me.y] == null || board[me.x-1][me.y].getLifeSpan() == 1)) {
			availableMoves.add(Move.LEFT);
		}
		if(me.x < Model.BOARD_DIMENSION-1 && (board[me.x+1][me.y] == null || board[me.x+1][me.y].getLifeSpan() == 1)) {
			availableMoves.add(Move.RIGHT);
		}
		if(me.y > 0 && (board[me.x][me.y-1] == null || board[me.x][me.y-1].getLifeSpan() == 1)) {
			availableMoves.add(Move.UP);
		}
		if(me.y < Model.BOARD_DIMENSION-1 && (board[me.x][me.y+1] == null || board[me.x][me.y+1].getLifeSpan() == 1)) {
			availableMoves.add(Move.DOWN);
		}
		return availableMoves;
	}
	
	public static List<Move> getAvailableMovesMinimal(Point me, Move lastMove) {
		List<Move> availableMoves = new ArrayList<Move>();
		
		if(me.x > 0 && lastMove != Move.RIGHT) {
			availableMoves.add(Move.LEFT);
		}
		if(me.x < Model.BOARD_DIMENSION-1 && lastMove != Move.LEFT) {
			availableMoves.add(Move.RIGHT);
		}
		if(me.y > 0 && lastMove != Move.DOWN) {
			availableMoves.add(Move.UP);
		}
		if(me.y < Model.BOARD_DIMENSION-1 && lastMove != Move.UP) {
			availableMoves.add(Move.DOWN);
		}
		return availableMoves;
	}
	
	public static Point newPosFromStartAndMoves(int x, int y, List<Move> moves) {
		for(Move move : moves) {
			switch(move) {
				case DOWN:
					y++;
					break;
				case LEFT:
					x--;
					break;
				case RIGHT:
					x++;
					break;
				case UP:
					y--;
					break;
			}
		}
		return new Point(x, y);
	}
	
}
