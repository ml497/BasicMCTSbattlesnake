package snakebot;

import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;

public interface Bot {
	
	public Move move(SnakeHead me, Model model);

}
