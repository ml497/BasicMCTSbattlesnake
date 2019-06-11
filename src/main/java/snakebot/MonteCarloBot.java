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
	
	@Override
	public Move move(SnakeHead me, Model model) {
		HashPoint meHash = new HashPoint(me.x, me.y);
		if(!headResultsCache.containsKey(meHash)){
			headResultsCache.put(meHash, runSimulations(model, me));
		}
		return headResultsCache.get(meHash).generateRandomWeightedMove();
	}
	
	private MCResult runSimulations(Model gameState, SnakeHead me) {
		MCResult result = new MCResult();
		List<Move> availableMoves = SnakeAgentUtils.getAvailableMoves(me, gameState.board);
		for(Move move : availableMoves) {
			double score = 0;
			for(int i=0; i<200; i++) {
				Model gameCopy = gameState.copyFromOrigin();
				advanceGameWithMove(gameState, me, move);
				runGame(gameCopy, me, opponentBot);
				if(isGameWon(gameState, me)) {
					score += 100;
				}
			}
			result.addNewResult(move, score);
		}
		return result;
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
