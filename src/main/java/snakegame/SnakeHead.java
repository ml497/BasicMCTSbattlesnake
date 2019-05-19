package snakegame;

import java.util.ArrayList;
import java.util.List;

public class SnakeHead implements Collidable{

	public List<SnakeTailSegment> body = new ArrayList<SnakeTailSegment>();
	public int x;
	public int y;
	public int lifespan;
	
	public SnakeHead(int x, int y, int lifespan) {
		this.x = x;
		this.y = y;
		this.lifespan = lifespan;
	}
	
	public void addSnakeSegment(SnakeTailSegment segment) {
		body.add(0, segment);
	}
	
	public void killSnake() {
		this.lifespan = 0;
		for(SnakeTailSegment segment : body) {
			segment.setLifeSpan(0);
		}
	}
	
	public void maxSnakeLifeSpan() {
		this.lifespan = 100;
	}
	
	public void incrementBodyLifeSpans() {
		for(SnakeTailSegment segment : this.body) {
			segment.incrementLifeSpan();
		}
	}
	
	public void decrementBodyLifeSpans() {
		for(SnakeTailSegment segment : this.body) {
			segment.decrementLifeSpan();
		}
	}

	public int getLifeSpan() {
		return lifespan;
	}

	public CollidableType getType() {
		return CollidableType.SnakeHead;
	}
	
}
