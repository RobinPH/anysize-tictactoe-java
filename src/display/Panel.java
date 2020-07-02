package display;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import component.Cell;
import component.Cell.CellState;
import game.Config;
import game.State;

public class Panel extends JPanel implements MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	private State state;
	private Graphics graphics;
	private Graphics2D graphics2D;
	private Config config;
	private String RENDER_THREAD = "renderChangesThread";
	private boolean winningAnimationDone = true;
	private boolean winningLineDrawn = false;
	
	public Panel(State state, Config config) {
		this.state = state;
		this.graphics = getGraphics();
		this.config = config;
	}
	
	public JPanel getJPanel() {
		int cellSize = this.config.getCellSize();
		int WIDTH = this.config.getWidth();
		int HEIGHT = this.config.getHeight();
		setPreferredSize(new Dimension(cellSize * WIDTH + (WIDTH + 1) * 2 - 1,
									   cellSize * HEIGHT + (HEIGHT + 1) * 2 - 1));
		
		this.addMouseListener(this);
		
		this.addKeyListener(this);
		setFocusable(true);
        requestFocus(); 
        
		return this;
	}
	
	public void render() {
		this.graphics = getGraphics();
		this.graphics2D = (Graphics2D) this.graphics;
		
		for (Cell cell : this.state.getCells().values())
			drawCell(cell);
		
		if (this.getThreadByName(RENDER_THREAD) == null)
			renderChanges();
	}
	
	public void renderChanges() {
		Thread renderChangesThread = new Thread(() -> {
			while (true) {
				List<Cell> cellsToRender = this.state.getCellsToRender();

				while (cellsToRender.size() != 0)
					drawCell(cellsToRender.remove(0));
				
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (this.state.isGameDone() && this.winningLineDrawn == false) {
					cellsToRender = this.state.getCellsToRender();

					while (cellsToRender.size() != 0)
						drawCell(cellsToRender.remove(0));
					
					new Thread(() -> {
						winningLineAnimation(this.state.getLinePosition());
					}).start();
					
					winningLineDrawn = true;
				}
			}
		});
		
		renderChangesThread.setName(RENDER_THREAD);
		renderChangesThread.start();
	}
	
	private void drawCell(Cell cell) {
		int cellSize = this.config.getCellSize();
		Map<String, Integer> position = cell.getPosition();
		int x = position.get("x") * (cellSize + 2);
		int y = position.get("y") * (cellSize + 2);
		int stroke = cellSize / 12;
		int strokeOffset = stroke / 2 - 1;
		
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.drawRect(x,
						  y,
						  cellSize + 2,
						  cellSize + 2);
		
		graphics.setColor(new Color(238, 238, 238));
		graphics.fillRect(x + 1, y + 1, cellSize + 1, cellSize + 1);
		
		graphics2D.setStroke(new BasicStroke(stroke));
		
		graphics.setColor(Color.LIGHT_GRAY);
		if (cell.getCellState() == CellState.O) {
			graphics2D.drawOval(x + cellSize / 5 + strokeOffset,
								y + cellSize / 5 + strokeOffset,
								cellSize * 3 / 5 + strokeOffset, 
								cellSize * 3 / 5 + strokeOffset);
		}
		
		if (cell.getCellState() == CellState.X) {
			graphics2D.drawLine(x + cellSize / 5 + strokeOffset,
								y + cellSize / 5 + strokeOffset,
								x + cellSize * 4 / 5 + strokeOffset,
								y + cellSize * 4 / 5 + strokeOffset);
			
			graphics2D.drawLine(x + cellSize * 4 / 5 + strokeOffset,
								y + cellSize / 5 + strokeOffset,
								x + cellSize / 5 + strokeOffset, 
								y + cellSize * 4 / 5 + strokeOffset);
		}
		
		graphics2D.setStroke(new BasicStroke(0));
	}
	
	public void drawWinningLine(float[] p, Color color) {
		int cellSize = this.config.getCellSize();
		int stroke = cellSize / 6;
		int offset = (cellSize + 2) / 2;
		
		graphics2D.setColor(color);
		
		graphics2D.setStroke(new BasicStroke(stroke));
		
		graphics2D.drawLine((int) (p[0] * (cellSize + 2) + offset),
							(int) (p[1] * (cellSize + 2) + offset),
							(int) (p[2] * (cellSize + 2) + offset),
							(int) (p[3] * (cellSize + 2) + offset));
		
		graphics2D.setStroke(new BasicStroke(0));
	}
	
	public void winningLineAnimation(Integer[] p) {
		this.winningAnimationDone = false;
		
		double time = (0.618 / 2) * 1000; // ms
		float fps = 60;
		
		float xDiff = (float) (((float)p[2] - (float)p[0]) / (fps * time / 1000));
		float yDiff = (float) (((float)p[3] - (float)p[1]) / (fps * time / 1000));
		
		float newX = p[0];
		float newY = p[1];
		
		boolean firstFrame = true; // Fixes the visual bug on diagonal lines
		
		while (time >= 0) {			
			drawWinningLine(new float[] { p[0], p[1], newX, newY }, firstFrame ? new Color(0f, 0f, 0f, 0f) : Color.RED);
			newX += xDiff;
			newY += yDiff;
			
			try {
				time -= 1000 / fps;
				firstFrame = false;
				Thread.sleep((long) (1000 / fps));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.winningAnimationDone = true;
	}
	
	public void restartGame() {
		if (this.winningAnimationDone) {			
			this.winningAnimationDone = false;
			this.winningLineDrawn = false;
			this.state.restartState();
			render();
		}
		return;
	}
	
	public Thread getThreadByName(String threadName) {
	    for (Thread thread : Thread.getAllStackTraces().keySet()) {
	        if (thread.getName().equals(threadName)) return thread;
	    }
	    return null;
	}
	
	public String getKeyByEvent(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int cellSize = this.config.getCellSize();
		
		int cellX = (x - (x % (cellSize + 2))) / (cellSize + 2);
		int cellY = (y - (y % (cellSize + 2))) / (cellSize + 2);
		String key = State.positionToKey(cellX, cellY);
		
		return key;
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (this.state.isGameDone()) {
			restartGame();
			return;
		}
		
		Cell clickedCell = this.state.getCell(this.getKeyByEvent(e));
		
		if (clickedCell.getCellState() != CellState.EMPTY) return;
		
		this.state.placeMove(clickedCell);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	

	@Override
	public void keyTyped(KeyEvent e) {
	}

	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case 32:
				if (this.state.isGameDone()) restartGame();
				return;
			default:
				return;
		}
	}
	

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
