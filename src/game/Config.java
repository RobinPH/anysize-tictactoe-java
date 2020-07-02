package game;

import component.Cell.CellState;

public class Config {
	private int width = 3;
	private int height = 3;
	private int cellSize = 100;
	private int length = 3;
	private CellState firstPlayer = CellState.X;
	
	public Config(int width, int height, int cellSize, int length, CellState firstPlayer) {
		this.setWidth(width);
		this.setHeight(height);
		this.setCellSize(cellSize);
		this.setLength(length);
		this.setFirstPlayer(firstPlayer);
	}
	
	public Config() {}
	
	public void setWidth(int width) {
		this.width = width <= 0 ? 3 : width;
	}
	
	public void setHeight(int height) {
		this.height = height <= 0 ? 3 : height;
	}
	
	public void setCellSize(int cellSize) {
		this.cellSize = cellSize <= 0 ? 50 : cellSize;
	}
	
	public void setLength(int length) {
		this.length = length > width || length > height || length <= 0 ? Math.min(this.width, this.height) : length;
	}
	
	public void setFirstPlayer(CellState cellState) {
		this.firstPlayer = cellState == CellState.EMPTY ? CellState.X : cellState;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public int getCellSize() {
		return this.cellSize;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public CellState getFirstPlayer() {
		return this.firstPlayer;
	}
}
