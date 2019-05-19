package snakegame;

public class SnakeTailSegment implements Collidable{

	private int lifeSpan;
	
	public SnakeTailSegment(int lifeSpan) {
		this.lifeSpan = lifeSpan;
	}
	
	public void setLifeSpan(int lifeSpan) {
		this.lifeSpan = lifeSpan;
	}
	
	public int getLifeSpan() {
		return this.lifeSpan;
	}
	
	public void decrementLifeSpan() {
		this.lifeSpan-=1;
	}
	
	public void incrementLifeSpan() {
		this.lifeSpan++;
	}

	public CollidableType getType() {
		return CollidableType.SnakeTail;
	}
	
}
