package snakegame;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


//on every tick:
//1. create new snake segments - 
//2. advance all snake heads - 
//3. create list of all snake head collisions in map - 
//4. check if any snake head collided with food -
//  4.1. if so, create new food -
//	4.2. if not, decrement remaining lifespan on all segments -
//5. Check if any snake head collided with a snake segment
//  5.1. If snake segment lifespan is 0, just replace it with the snake head.
//  5.2. Otherwise, set lifespan of snake head and all its segments to 0
//6. Check if any snake heads collided with one another. - 
//  6.1 Compare the lengths of each snake and kill the smaller one -
//7. pass through the 2d array once and remove all elements with lifespan -

public class Model{

	public static final int BOARD_DIMENSION = 11;
	public Collidable[][] board = new Collidable[BOARD_DIMENSION][BOARD_DIMENSION];
	public List<SnakeHead> heads = new ArrayList<SnakeHead>();
	private Random rand = new Random();

	private List<List<Point>> originalSnakes;
	private List<Integer> originalSnakeLifespans;
	private List<Point> originalFoods;
	
	public Model(List<List<Point>> snakes, List<Integer> snakeLifespans, List<Point> foods) {
		this.originalSnakes = snakes;
		this.originalSnakeLifespans = snakeLifespans;
		this.originalFoods = foods;
		
		for( int x=0; x<snakes.size(); x++ ) {
			SnakeHead newSnakeHead = new SnakeHead(snakes.get(x).get(0).x, snakes.get(x).get(0).y, snakeLifespans.get(x));
			heads.add(newSnakeHead);
			board[snakes.get(x).get(0).x][snakes.get(x).get(0).y] = newSnakeHead;
			for(int i=1; i<snakes.get(x).size(); i++) {
				SnakeTailSegment segmentToAdd = new SnakeTailSegment(snakes.get(x).size()-i);
				newSnakeHead.body.add(segmentToAdd);
				board[snakes.get(x).get(i).x][snakes.get(x).get(i).y] = segmentToAdd;
			}
		}
		for (Point food : foods) {
			board[food.x][food.y] = new Food();
		}
	}
	
	/**
	 * this method is inefficient, use only for testing
	 * @return Model
	 */
	public Model slowCopy() {
		List<List<Point>> snakes = new ArrayList<List<Point>>();
		List<Integer> snakeLifeSpans = new ArrayList<Integer>();
		List<Point> foods = new ArrayList<Point>();
		Map<Collidable, Point> allSnakeElements = new HashMap<Collidable, Point>();
		for(int x=0; x<board.length; x++) {
			for(int y=0; y<board[x].length; y++) {
				if(board[x][y] != null) {
					if(board[x][y] instanceof Food) {
						foods.add(new Point(x, y));
					} else {
						allSnakeElements.put(board[x][y], new Point(x, y));
					}
				}
			}
		}
		
		for(SnakeHead head : heads) {
			List<Point> snake = new ArrayList<Point>();
			snake.add(allSnakeElements.get(head));
			for(SnakeTailSegment segment : head.body) {
				snake.add(allSnakeElements.get(segment));
			}
			snakes.add(snake);
			snakeLifeSpans.add(head.lifespan);
		}
		
		return new Model(snakes, snakeLifeSpans, foods);
	}
	
	public Model copyFromOrigin() {
		return new Model(originalSnakes, originalSnakeLifespans, originalFoods);
	}
	
	public List<List<Point>> getOriginalSnakes() {
		return this.originalSnakes;
	}

	public void tickGame(List<Move> moves) {
		createNewSegments();
		List<SnakeHead> observedCollisions = moveHeads(moves);
		decrementAllTailLifeSpans();
		
		
		handleCollisions(observedCollisions);
		cleanBoard();
	}
	
	private void cleanBoard() {
		for(int x=0; x<board.length; x++) {
			for(int y=0; y<board[x].length; y++) {
				if(board[x][y] != null && board[x][y].getLifeSpan() <= 0) {
					board[x][y] = null;
				}
			}
		}
		heads.removeIf( head -> head.lifespan == 0);
		for(SnakeHead head : heads) {
			head.body.removeIf( seg -> seg.getLifeSpan() == 0);
		}
	}
	
	private void handleCollisions(List<SnakeHead> observedCollisions) {
		List<SnakeHead> toRemove = new ArrayList<SnakeHead>();
		for(SnakeHead hitHead : observedCollisions) {
			if(board[hitHead.x][hitHead.y].getType() == CollidableType.Food) {
				handleFoodConsumption(hitHead);
				toRemove.add(hitHead);
			}
		}
		for(SnakeHead head : toRemove) {
			observedCollisions.remove(head);
		}
		
		toRemove = new ArrayList<SnakeHead>();
		for(SnakeHead hitHead : observedCollisions) {
			if(board[hitHead.x][hitHead.y].getType() == CollidableType.SnakeTail) {
				handleSnakeTailCollision(hitHead);
				toRemove.add(hitHead);
			}
		}
		for(SnakeHead head : toRemove) {
			observedCollisions.remove(head);
		}
		for(SnakeHead hitHead : observedCollisions) {
			if(board[hitHead.x][hitHead.y].getType() == CollidableType.SnakeHead) {
				handleSnakeHeadCollision(hitHead);
				toRemove.add(hitHead);
			}
		}
	}
	
	private void handleSnakeHeadCollision(SnakeHead snakeHead) {
//		System.out.println("head on head");
		if(((SnakeHead) board[snakeHead.x][snakeHead.y]).body.size() >= snakeHead.body.size()) {
			snakeHead.killSnake();
		}
		if(((SnakeHead) board[snakeHead.x][snakeHead.y]).body.size() <= snakeHead.body.size()) {
			((SnakeHead) board[snakeHead.x][snakeHead.y]).killSnake();
		}
	}
	
	private void handleSnakeTailCollision(SnakeHead snakeHead) {
//		System.out.println("head on tail");

		if(board[snakeHead.x][snakeHead.y].getLifeSpan() == 0) {
			board[snakeHead.x][snakeHead.y] = snakeHead;
		} else {
			snakeHead.killSnake();
		}
	}
	 
	private void handleFoodConsumption(SnakeHead eaterHead) {
//		System.out.println("head on food");

		eaterHead.incrementBodyLifeSpans();
		eaterHead.maxSnakeLifeSpan();
		board[eaterHead.x][eaterHead.y] = eaterHead;
		generateNewFood();
	}
	
	private void generateNewFood() {
		while(true) {
			int newFoodx = rand.nextInt(BOARD_DIMENSION);
			int newFoody = rand.nextInt(BOARD_DIMENSION);
			
			if(board[newFoodx][newFoody] == null) {
				board[newFoodx][newFoody] = new Food();
				break;
			}
		}
	}
	
	private void decrementAllTailLifeSpans() {
		for (SnakeHead head : heads) {
			head.decrementBodyLifeSpans();
			head.lifespan-=1;
		}
	}
	
	private void createNewSegments() {
		for (SnakeHead head : heads) {
			SnakeTailSegment seg = new SnakeTailSegment(head.body.size()+1);
			board[head.x][head.y] = seg;
			head.addSnakeSegment(seg);
		}
	}
	
	private List<SnakeHead> moveHeads(List<Move> moves) {
		List<SnakeHead> observedCollisions = new ArrayList<SnakeHead>();
		for (int i=0; i<moves.size(); i++) {
			switch(moves.get(i)) {
				case DOWN:
					this.heads.get(i).y += 1;
					break;
				case LEFT:
					this.heads.get(i).x -= 1;
					break;
				case RIGHT:
					this.heads.get(i).x += 1;
					break;
				case UP:
					this.heads.get(i).y -= 1;
					break;
			}
			if(board[heads.get(i).x][heads.get(i).y] != null) {
				observedCollisions.add(heads.get(i));
			} else {
				board[heads.get(i).x][heads.get(i).y] = heads.get(i);
			}
		}
		return observedCollisions;
	}
}




