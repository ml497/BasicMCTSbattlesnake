package snakebot;

import snakegame.Model;
import snakegame.Move;
import snakegame.SnakeHead;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import montecarlo.HashPoint;
import montecarlo.MCResult;

public class MonteCarloBot implements Bot{

	private Map<HashPoint, MCResult> headResultsCache = new HashMap<HashPoint, MCResult>();

	private RandomSurviveSnake opponentBot = new RandomSurviveSnake();
	
	public MonteCarloBot(Model model) {
		for(SnakeHead head : model.heads) {
			headResultsCache.put(new HashPoint(head.x, head.y), new MCResult());
		}
	}
	
	@Override
	public Move move(SnakeHead me, Model model) {
//		System.out.println("getting move for: " + me.x + ", " + me.y);
		HashPoint meHash = new HashPoint(me.x, me.y);
		
		if(headResultsCache.get(meHash).totalResults < 20 || headResultsCache.get(meHash).totalScore == 0) {
			return null;
		}
		
		return headResultsCache.get(meHash).generateRandomWeightedMove();
	}
	
	public void runSimulations(Model gameState) {
		for (SnakeHead head : gameState.heads ) {
			List<Move> availableMoves = SnakeAgentUtils.getAvailableMoves(head, gameState.board);
			HashPoint headStartLoc = new HashPoint(head.x, head.y);
			
			for(Move move : availableMoves) {
				Model gameCopy = gameState.copyFromOrigin();
				SnakeHead meCopy = (SnakeHead) gameCopy.board[head.x][head.y];
				double score = 0;
				advanceGameWithMove(gameCopy, meCopy, move);
				runGame(gameCopy, meCopy, opponentBot);
				if(isGameWon(gameCopy, meCopy)) {
					score += 100;
				}
				headResultsCache.get(headStartLoc).addNewResult(move, score);
			}
		}
	}
	
	private void advanceGameWithMove(Model gameState, SnakeHead me, Move moveToTest) {
		List<Move> snakeMovesThisTick = new ArrayList<Move>();
		for(SnakeHead snake : gameState.heads) {
			if(snake == me) {				
				snakeMovesThisTick.add(moveToTest);
			} else {
				snakeMovesThisTick.add(opponentBot.move(snake, gameState));
			}
		}
		gameState.tickGame(snakeMovesThisTick);
	}
	
	private boolean isGameWon(Model gameState, SnakeHead me) {
		return gameState.heads.size() == 1 && gameState.heads.contains(me);
	}

	private void runGame(Model gameState, SnakeHead me, Bot simulationBot) {
		while(!isGameOver(gameState, me)) {
			List<Move> moves = new ArrayList<Move>();
			for(SnakeHead head : gameState.heads) {
				moves.add(simulationBot.move(head, gameState));
			}
			gameState.tickGame(moves);
		}
	}
	
	private boolean isGameOver(Model gameState, SnakeHead me) {
		return !gameState.heads.contains(me) || gameState.heads.size() < 2;
	}
	
	
}
