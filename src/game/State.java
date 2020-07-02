package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import component.Cell;
import component.Cell.CellState;

public class State {
	private Map<String, Cell> cells;
	private List<Cell> cellsToRender = new ArrayList<Cell>();
	private CellState currentPlayer;
	private Config config;
	private boolean gameDone = false;
	private CellState winner;
	private Integer[] winningLinePosition;
	private List<Cell> occupiedCells = new ArrayList<Cell>();
	
	public State(Config config) {
		this.config = config;
		this.currentPlayer = this.config.getFirstPlayer();
		this.generateStartingState(this.config.getWidth(), this.config.getHeight());
	}
	
	public void restartState() {
		this.generateStartingState(this.config.getWidth(), this.config.getHeight());
		this.gameDone = false;
		this.winner = null;
		this.winningLinePosition = null;
		this.occupiedCells = new ArrayList<Cell>();
		this.currentPlayer = this.config.getFirstPlayer();
	}
	
	private boolean checkWinner() {
		int lengthRequired = this.config.getLength();
		
		for (Cell cell : this.occupiedCells) {
			CellState cellState = cell.getCellState();
			Map<String, Integer> position = cell.getPosition();
			int x = position.get("x");
			int y = position.get("y");
			
			if (cellState != CellState.EMPTY) {
				// Checks Horizontal
				if (checkOccuranceLength(cell, 1, 0) >= lengthRequired) {
					this.winningLinePosition = new Integer[] { x, y, x + 1 * (lengthRequired - 1), y + 0 * (lengthRequired - 1) };
					this.winner = cell.getCellState();
					return true;
				}
				
				// Checks Diagonal +x
				if (checkOccuranceLength(cell, 1, 1) >= lengthRequired) {
					this.winningLinePosition = new Integer[] { x, y, x + 1 * (lengthRequired - 1), y + 1 * (lengthRequired - 1) };
					this.winner = cell.getCellState();
					return true;
				}
				
				// Checks Diagonal -x
				if (checkOccuranceLength(cell, -1, 1) >= lengthRequired) {
					this.winningLinePosition = new Integer[] { x, y, x - 1 * (lengthRequired - 1), y + 1 * (lengthRequired - 1) };
					this.winner = cell.getCellState();
					return true;
				}
				
				// Checks Vertical
				if (checkOccuranceLength(cell, 0, 1) >= lengthRequired) {
					this.winningLinePosition = new Integer[] { x, y, x + 0 * (lengthRequired - 1), y + 1 * (lengthRequired - 1) };
					this.winner = cell.getCellState();
					return true;
				}
			}
		}
		return false;
	}
	
	private int checkOccuranceLength(Cell cell, int xDirection, int yDirection) {
		int lengthRequired = this.config.getLength();
		Map<String, Integer> positionCell = cell.getPosition();
		int xCell = positionCell.get("x");
		int yCell = positionCell.get("y");
		CellState stateCell = cell.getCellState();
		
		for (int i = 0; i < lengthRequired; i++) {
			int xChecking = xCell + i * xDirection;
			int yChecking = yCell + i * yDirection;
			Cell cellChecking = this.cells.get(positionToKey(xChecking, yChecking));
			
			// handles Out of Bounds
			if (cellChecking == null) return i;
			
			CellState cellStateChecking = cellChecking.getCellState();
			
			// if state did not match to original cell's state;
			if (cellStateChecking != stateCell) return i;

			// return if it did meet the required length
			if (i == lengthRequired - 1) return i + 1;
		}
		
		return 0; 
	}
	
	public void placeMove(Cell cell, CellState move) {
		if (this.gameDone) return;
		
		CellState EMPTY = CellState.EMPTY;
		
		if (move == EMPTY) return;
		if (cell.getCellState() != EMPTY) return;
		
		cell.changeCellState(move);
		this.cellsToRender.add(cell);
		this.occupiedCells.add(cell);
		
		this.changePlayer();
		
		if (this.checkWinner()) {
			this.gameDone = true;
		}
	}
	
	public void placeMove(Cell cell) {
		this.placeMove(cell, this.currentPlayer);
	}
	
	public void changePlayer() {
		this.changePlayer(this.currentPlayer == CellState.X ? CellState.O : CellState.X);
	}
	
	public void changePlayer(CellState nextPlayer) {
		this.currentPlayer = nextPlayer;
	}
	
	private void generateStartingState(int width, int height) {
		Map<String, Cell> cells = new HashMap<>();
		
		for (int i = 0; i < height; i++)
			for (int j = 0; j < width; j++)
				cells.put(positionToKey(j, i), new Cell(j, i));
		
		this.cells = cells;
	}
	
	public static String positionToKey(Map<String, Integer> position) {
		String x = Integer.toString(position.get("x"));
		String y = Integer.toString(position.get("y"));
		
		return x.concat("-").concat(y);
	}
	
	public CellState getWinner() {
		return this.winner;
	}
	
	public boolean isGameDone() {
		return this.gameDone;
	}
	
	public Integer[] getLinePosition() {
		return this.winningLinePosition;
	}
	
	public static String positionToKey(int x, int y) {
		String _x = Integer.toString(x);
		String _y = Integer.toString(y);
		
		return _x.concat("-").concat(_y);
	}
	
	public Cell getCell(int x, int y) {
		return this.cells.get(positionToKey(x, y));
	}
	
	public Cell getCell(String key) {
		return this.cells.get(key);
	}
	
	public CellState getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public Map<String, Cell> getCells() {
		return this.cells;
	}
	
	public List<Cell> getCellsToRender() {
		return this.cellsToRender;
	}
}
