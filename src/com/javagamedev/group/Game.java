package com.javagamedev.group;

public class Game implements Runnable{
	
	private GameWindow gameWindow;
	private GamePanel gamePanel;
	
	public static final int FPS = 60;
	public static final long FRAME_MS = 1000 / FPS;
	
	private boolean isRunning = true;
	Thread gameThread;
	
	public Game() {
		gamePanel = new GamePanel();
		gameWindow = new GameWindow(gamePanel);
		gamePanel.requestFocus();
		this.start();
	}
	
	public void start() {
		gameThread = new Thread(this);
		gameThread.start();
	}
	
	@Override
	public void run() {
		double timePerFrame = 1000000000/FPS;
		long lastFrame = System.nanoTime();
		long now = System.nanoTime();
		long elapsedMs = 0;
		long frames = 0;
		long lastCheck = 0;
		
		while(gameThread != null) {
			now = System.nanoTime();
			elapsedMs = now - lastFrame;
			
			if(elapsedMs>=timePerFrame && isRunning) {
				gamePanel.update(elapsedMs);
				gamePanel.repaint();
				lastFrame = now;
				frames++;
			}
			
			if(System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				System.out.println("FPS: " + frames);
				frames = 0;
			}
		}
	}
	
	public void stop() {
		isRunning = false;
	}
	
	public boolean isRunning() {
		return this.isRunning;
	}
	
}
