package montecarlo;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import snakegame.Move;

public class MCResult {

	private Random rand = new Random();
	public double totalScore = 0;
	public int totalResults = 0;
	public Map<Move, Double> results = new HashMap<Move, Double>();

	public MCResult() {
		results.put(Move.UP, 0d);
		results.put(Move.RIGHT, 0d);
		results.put(Move.DOWN, 0d);
		results.put(Move.LEFT, 0d);
	};

	public void addNewResult(Move actionTaken, double score) {
		results.put(actionTaken, results.get(actionTaken) + score);
		totalScore += score;
		totalResults++;
	}

	public Move generateRandomWeightedMove() {
		
		double up = Math.pow(results.get(Move.UP), 2);
		double right = Math.pow(results.get(Move.RIGHT), 2);
		double down = Math.pow(results.get(Move.DOWN), 2);
		double left = Math.pow(results.get(Move.LEFT), 2);
		double total = up + right + down + left;
		
		double ran = rand.nextDouble();
		ran -= up / total;
		if(ran <= 0) {
			return Move.UP;
		}
		ran -= right / total;
		if(ran <= 0) {
			return Move.RIGHT;
		}
		ran -= down / total;
		if(ran <= 0) {
			return Move.DOWN;
		}
		return Move.LEFT;
	}

}
