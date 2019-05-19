package snakegame;

public class Food implements Collidable{

	public int getLifeSpan() {
		return 1;
	}

	public CollidableType getType() {
		return CollidableType.Food;
	}
}
