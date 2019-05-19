package snakebot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import snakegame.Collidable;
import snakegame.CollidableType;
import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;

public class RandomSurviveSnake implements Bot{
	
	private Random rand = new Random();
	
	public RandomSurviveSnake() {
		
	}
	
	public Move move(SnakeHead me, Model model) {
		List<Move> availableMoves = SnakeAgentUtils.getAvailableMoves(me, model.board);
		
		if(availableMoves.size() > 0) {
			return availableMoves.get(rand.nextInt(availableMoves.size()));
		} else {
			return me.y != 0 ? Move.UP : Move.DOWN;
		}
	}

}
