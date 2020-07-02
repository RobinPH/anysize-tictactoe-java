package game;

import component.Cell.CellState;
import display.Frame;

public class Tictactoe {
	private State state;

	public static void main(String[] args) {
		new Tictactoe(); // For Default Settings
//		new Tictactoe(3, 3, 100, 3, CellState.X); // Default Settings
//		new Tictactoe(5, 5, 60, 3, CellState.X); // 5x5 Board,
												 // X is the first Player
//		new Tictactoe(5, 7, 60, 5, CellState.O); // 5x7 Board,
												 // O is the first Player,
												 // and a requirement of 5 mark in horizontal, vertical, or diagonal row.
	}
	
	public Tictactoe() {
		new Tictactoe(new Config());
	}
	
	/**
	 * Creates new Tic Tac Toe.
	 *
	 * @param width is the number of <b>columns</b> of the Tic Tac Toe Board.
	 * @param height is the number of <b>row</b> of the Tic Tac Toe Board.
	 * @param cellSize is the <b>number of pixels</b> of each cell.
	 * @param length is the required number of consecutive marks horizontally, vertically, or diagonal row.
	 * @param firstPlayer is the first player;
	 */
	public Tictactoe(int width, int height, int cellSize, int length, CellState firstPlayer) {
		new Tictactoe(new Config(width, height, cellSize, length, firstPlayer));
	}
	
	public Tictactoe(Config config) {
		// Ensures that length is less than or equal to min(width, height)
		// so that all possible orientation of winning pattern can be used
		if (config.getLength() > config.getWidth() || config.getLength() > config.getHeight())
			config.setLength(Math.min(config.getWidth(), config.getHeight()));
		
		this.state = new State(config);
		
		new Frame(this.state, config);
	}
}
