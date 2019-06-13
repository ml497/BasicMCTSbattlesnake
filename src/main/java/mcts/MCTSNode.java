package mcts;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import snakebot.Bot;
import snakebot.SnakeAgentUtils;
import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;

public class MCTSNode implements Comparable {

	public static double BALANCE_PARAMETER = 1;
	public static int EXPAND_THRESHOLD = 5;

	public MCTSNode parent;
	public List<MCTSNode> children;
	public List<Move> agentMoves;
	public double totalScore;
	public int simulations;

	public MCTSNode(MCTSNode parent, List<Move> agentMoves) {
		this.parent = parent;
		this.agentMoves = agentMoves;
	}

	public void runSimulation(Model gameState, SnakeHead me, Bot opponentBot, Bot opponentFirstMoveBot) {
		int startLocX = me.x;
		int startLocY = me.y;
		int foodEatenStart = me.foodEaten;
		double snakesStart = gameState.heads.size();
		boolean finishedFirstTurn = advanceGameToNode(gameState, me, opponentBot, opponentFirstMoveBot);
		runGame(gameState, me, opponentBot);
//		double score = snakesBeatenBonus(snakesStart, gameState.heads.size());
		double score = me.foodEaten > foodEatenStart ? (80 / snakesStart) : 0;
		if (isGameWon(gameState, me)) {
			score += 100;
		} else if (finishedFirstTurn) {
			score = 0;
		}
		totalScore += score;
		simulations++;
		parent.propagateScore(score);

		if (simulations >= EXPAND_THRESHOLD) {
			expand(SnakeAgentUtils.newPosFromStartAndMoves(startLocX, startLocY, agentMoves),
					agentMoves.get(agentMoves.size() - 1));
		}
	}

	public double getUCB() {
		if (this.simulations != 0) {
			return (totalScore / simulations)
					+ BALANCE_PARAMETER * Math.sqrt(this.parent.simulations / this.simulations);
		} else {
			return (totalScore / simulations) + BALANCE_PARAMETER * 10;
		}
	}

	public void propagateScore(double score) {
		totalScore += score;
		simulations++;
		if (parent != null) {
			parent.propagateScore(score);
		}
	}

	public void expand(Point me, Move lastMove) {
		List<Move> availableMoves = SnakeAgentUtils.getAvailableMovesMinimal(me, lastMove);
		children = new ArrayList<MCTSNode>();
		for (Move move : availableMoves) {
			List<Move> nodeMoves = new ArrayList<Move>(this.agentMoves);
			nodeMoves.add(move);
			children.add(new MCTSNode(this, nodeMoves));
		}
	}

	private void runGame(Model gameState, SnakeHead me, Bot simulationBot) {
		while (!isGameOver(gameState, me)) {
			List<Move> moves = new ArrayList<Move>();
			for (SnakeHead head : gameState.heads) {
				moves.add(simulationBot.move(head, gameState));
			}
			gameState.tickGame(moves);
		}
	}

	private boolean advanceGameToNode(Model gameState, SnakeHead me, Bot opponentBot, Bot opponentFirstMoveBot) {
		boolean isFirstMove = true;
		for (Move move : this.agentMoves) {
			List<Move> snakeMovesThisTick = new ArrayList<Move>();
			for (SnakeHead snake : gameState.heads) {
				if (snake == me) {
					snakeMovesThisTick.add(move);
				} else {
					Move enemyMove = null;
					if (isFirstMove) {
						enemyMove = opponentFirstMoveBot.move(snake, gameState);
					}
					if (enemyMove == null) {
						enemyMove = opponentBot.move(snake, gameState);
					}

					snakeMovesThisTick.add(enemyMove);
				}
			}
			gameState.tickGame(snakeMovesThisTick);

			if (isGameOver(gameState, me)) {
				break;
			}
			isFirstMove = false;
		}
		return isFirstMove;
	}

	private boolean isGameOver(Model gameState, SnakeHead me) {
		return !gameState.heads.contains(me) || gameState.heads.size() < 2;
	}

	private double snakesBeatenBonus(double snakesStart, double snakesNow) {
		if (snakesStart == 0) {
			return 0;
		}
		return 100 * (snakesStart - snakesNow) / snakesStart;
	}

	private boolean isGameWon(Model gameState, SnakeHead me) {
		return gameState.heads.size() == 1 && gameState.heads.contains(me);
	}

	@Override
	public int compareTo(Object other) {
		double ucbDelta = ((MCTSNode) other).getUCB() - this.getUCB();
		if (ucbDelta < 0) {
			return -1;
		} else {
			return 1;
		}
	}

}
