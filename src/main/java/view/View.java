package view;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import snakebot.Bot;
import snakebot.MCTSBot;
import snakebot.RandomSurviveSnake;
import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;


public class View extends JFrame{
	
	public Model gameModel;
	public Bot bot = new RandomSurviveSnake();
	public Bot mctsBot = new MCTSBot();
	public int drawSizeMult = 70;
	
	public View() {
		
		gameModel = generateBasicBoard();
		
		GameCanvas canvas = new GameCanvas();
		add("Center", canvas);
		
		setSize(drawSizeMult * Model.BOARD_DIMENSION, drawSizeMult * Model.BOARD_DIMENSION);
		
		setVisible(true);
		
		
		while(true) {
			System.out.println("Tick");
			SnakeHead me = gameModel.heads.get(0);
			
			List<Move> moves = new ArrayList<Move>();
			for(SnakeHead head : gameModel.heads) {
				if(me == head) {
					moves.add(mctsBot.move(head, gameModel));
				} else {
					moves.add(bot.move(head, gameModel));
				}
			}
			
			gameModel.tickGame(moves);
			gameModel = gameModel.slowCopy();
			canvas.repaint();
			
//			try {
//				Thread.sleep(1500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	private Model generateBasicBoard() {
		List<List<Point>> snakes = new ArrayList<List<Point>>();
		List<Integer> lifeSpans = new ArrayList<Integer>();
		List<Point> foods = new ArrayList<Point>();
		for(int i=1; i<5; i++) {
			List<Point> newSnake = new ArrayList<Point>();
			newSnake.add(new Point(i*2, 5));
			newSnake.add(new Point(i*2, 4));
			if(i > 1) {
				newSnake.add(new Point(i*2, 3));
				newSnake.add(new Point(i*2, 2));
				newSnake.add(new Point(i*2, 1));
			}
			snakes.add(newSnake);
			
			lifeSpans.add(90);
			
			foods.add(new Point(i*2, 8));
		}
		
		return new Model(snakes, lifeSpans, foods);
	}
	
	class GameCanvas extends Canvas {

		public void paint(Graphics graphics) {
		    Graphics2D g = (Graphics2D) graphics;
		    
		    g.setColor(Color.WHITE);
		    g.fillRect(0, 0, drawSizeMult * Model.BOARD_DIMENSION, drawSizeMult * Model.BOARD_DIMENSION);
		    
		    drawGame(g);
		}
		
		private void drawGame(Graphics2D g) {
			for(int x =0; x<gameModel.board.length; x++) {
				for(int y=0; y<gameModel.board.length; y++) {
					if(gameModel.board[x][y] != null) {
						switch(gameModel.board[x][y].getType()) {
							case Food:
								drawSquare(g, Color.ORANGE, x, y, gameModel.board[x][y].getLifeSpan());
								break;
							case SnakeHead:
								drawSquare(g, Color.BLUE, x, y, gameModel.board[x][y].getLifeSpan());
								break;
							case SnakeTail:
								drawSquare(g, Color.GREEN, x, y, gameModel.board[x][y].getLifeSpan());
								break;
						}
					}
				}
			}
		}
		
		private void drawSquare(Graphics2D g, Color c, int x, int y, int text) {
			g.setColor(c);
			g.fillRect(x * drawSizeMult, y * drawSizeMult, drawSizeMult, drawSizeMult);
			g.setColor(Color.BLACK);
			g.drawString("" + text, x * drawSizeMult, y * drawSizeMult);
		}
	}

}
