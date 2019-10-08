package snakegame;

public class BoardSizeSingleton {
	
	private static BoardSizeSingleton boardSize;
	public int dimension;
	
	private BoardSizeSingleton() {
		dimension = 11;
	}
	
	public static void setDimension(int dimension) {
		if(boardSize == null) {
			boardSize = new BoardSizeSingleton();
		}
		boardSize.dimension = dimension;
	}
	
	public static int getDimension() {
		if(boardSize == null) {
			boardSize = new BoardSizeSingleton();
		}
		return boardSize.dimension;
	}

}
