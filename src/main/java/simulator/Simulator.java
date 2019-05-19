package simulator;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import snakebot.Bot;
import snakebot.RandomSurviveSnake;
import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;

public class Simulator {
	
	public static void main(String[] args) {
		runSimulations();
	}
	
	private static void runSimulations() {
		Bot bot = new RandomSurviveSnake();
		long start = System.currentTimeMillis();
		for(int i=0; i<10000; i++) {
			Model gameModel = generateBasicBoard();
			while(gameModel.heads.size() > 1) {
				List<Move> moves = new ArrayList<Move>();
				for(SnakeHead head : gameModel.heads) {
					moves.add(bot.move(head, gameModel));
				}
				gameModel.tickGame(moves);
			}
		}
		long delta = System.currentTimeMillis() - start;
		System.out.println("DONE: " + delta);
	}

	private static Model generateBasicBoard() {
		List<List<Point>> snakes = new ArrayList<List<Point>>();
		List<Integer> lifeSpans = new ArrayList<Integer>();
		List<Point> foods = new ArrayList<Point>();
		for(int i=1; i<5; i++) {
			List<Point> newSnake = new ArrayList<Point>();
			newSnake.add(new Point(i*2, 5));
			newSnake.add(new Point(i*2, 4));
			newSnake.add(new Point(i*2, 3));
			snakes.add(newSnake);
			
			lifeSpans.add(90);
			
			foods.add(new Point(i*2, 8));
		}
		
		return new Model(snakes, lifeSpans, foods);
	}
	
}
