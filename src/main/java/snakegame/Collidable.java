package snakegame;

//on every tick:
// 1. create new snake segments
// 2. advance all snake heads
// 3. create list of all snake head collisions in map
// 4. check if any snake head collided with food
//  4.1. if so, create new food
//	4.2. if not, decrement remaining lifespan on all segments
// 5. Check if any snake head collided with a snake segment
//  5.1. If snake segment lifespan is 0, just replace it with the snake head.
//  5.2. Otherwise, set lifespan of snake head and all its segments to 0
// 6. pass through the 2d array once and remove all elements with lifespan 0

public interface Collidable {

	public int getLifeSpan();
	public CollidableType getType();
	
}
