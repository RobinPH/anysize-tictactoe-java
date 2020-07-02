package component;

import java.util.HashMap;
import java.util.Map;

public class Cell {
	private CellState cellState = CellState.EMPTY;
	private int x;
	private int y;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Map<String, Integer> getPosition() {
		Map<String, Integer> position = new HashMap<>();
		
		position.put("x", this.x);
		position.put("y", this.y);
		
		return position;
	}
	
	public void changeCellState(CellState newCellState) {
		this.cellState = newCellState;
	}
	
	public CellState getCellState() {
		return this.cellState;
	}
	
	public enum CellState {
		EMPTY,
		X,
		O,
	}
}