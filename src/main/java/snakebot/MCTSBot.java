package snakebot;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mcts.MCTSNode;
import networking.LatencySingleton;
import snakegame.Collidable;
import snakegame.CollidableType;
import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;

// 1. Generate current game state node
// 2. Expand node immediately, generate possible children
// 3. Copy original game state, move agent according to node
// 		actions, perform random moves for all opponents
// 4. Record game outcome at node.
// 5. Propagate score up tree
// 6. Expand node if score threshold is met.
// 7. Repeat from step 3.
// 8. 

//aka CRUSHMASTER 10000
public class MCTSBot implements Bot{
	
	private static int ALLOWED_TIME = 200;
	private static int MAX_THREADS = 2;
	private RandomSurviveSnake opponentBot = new RandomSurviveSnake();
	private List<MCTSNode> currentLeafNodes;
	private Set<MCTSNode> busyNodes;
	private Move lastMove;
	
	private ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
	
	public Move move(SnakeHead me, Model model, long availableTime) {
		long startTime = System.currentTimeMillis();
		
		MCTSNode root = new MCTSNode(null, new ArrayList<Move>());
		
		try {
			root.expand(new Point(me.x, me.y), getLastMove(me, model));
		} catch (Exception e) {
			root.expand(new Point(me.x, me.y), lastMove);
		}
		this.currentLeafNodes = new ArrayList<MCTSNode>(root.children);
		this.busyNodes = new HashSet<MCTSNode>();
		
		for(int i=0; i<MAX_THREADS; i++) {
			this.executorService.submit(() -> {
				while((System.currentTimeMillis() - startTime) < availableTime) {
					runSimulationOnBestCandidate(model, me.x, me.y);
				}
			});
		}
		try {
			long timeToSleep = availableTime - (System.currentTimeMillis() - startTime);
			Thread.sleep(timeToSleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("simulations achieved: " + root.simulations);
		lastMove = this.determineBestMove(root);
		return lastMove;
	}
	
	public Move move(SnakeHead me, Model model) {
		return move(me, model, ALLOWED_TIME - LatencySingleton.getLatency());
	}
	
	private Move getLastMove(SnakeHead toCheck, Model game) throws Exception {
		for(List<Point> snake : game.getOriginalSnakes()) {
			if(snake.get(0).x == toCheck.x && snake.get(0).y == toCheck.y) {
				if(snake.get(0).x > snake.get(1).x) {
					return Move.RIGHT;
				}
				else if(snake.get(0).x < snake.get(1).x) {
					return Move.LEFT;
				}
				else if(snake.get(0).y > snake.get(1).y) {
					return Move.DOWN;
				}
				else {
					return Move.UP;
				}
			}
		}
		throw new Exception("No matching snake found!");
	}
	
	@SuppressWarnings("unchecked")
	private void runSimulationOnBestCandidate(Model model, int meX, int meY) {
		Model modelCopy = model.copyFromOrigin();
		SnakeHead meCopy = (SnakeHead) modelCopy.board[meX][meY];
		MCTSNode nodeToEvaluate = null;
		synchronized(currentLeafNodes) {
			for(int i=0; i<MAX_THREADS ; i++) {
				if(!busyNodes.contains(currentLeafNodes.get(i))) {
					nodeToEvaluate = currentLeafNodes.get(i);
					busyNodes.add(nodeToEvaluate);
					break;
				} else {
				}
			}
		}	
		if(nodeToEvaluate == null ) {
			return;
		}
		nodeToEvaluate.runSimulation(modelCopy, meCopy, opponentBot);
		
		synchronized(currentLeafNodes) {
			if(nodeToEvaluate.children != null) {
				currentLeafNodes.addAll(nodeToEvaluate.children);
				currentLeafNodes.remove(nodeToEvaluate);
			} 
			busyNodes.remove(nodeToEvaluate);
			Collections.sort(currentLeafNodes);
		}
	}
	
	private Move determineBestMove(MCTSNode root) {
		double highestAvgScore = 0;
		Move bestAction = null;
		for(MCTSNode child : root.children) {
			
			System.out.println("Move: " + child.agentMoves.get(child.agentMoves.size()-1));
			
			double avgScore = child.totalScore/child.simulations;
			System.out.println("avgScore: " + avgScore);
			if(avgScore >= highestAvgScore) {
				highestAvgScore = avgScore;
				bestAction = child.agentMoves.get(0);
			}
		}
		return bestAction;
	}

}











