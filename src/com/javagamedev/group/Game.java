package com.javagamedev.group;

public class Game implements Runnable{
	
	private GameWindow gameWindow;
	private GamePanel gamePanel;
	
	public static final int FPS = 60;
	public static final int UPS = 1000; // lets us measure time in ~ milliseconds
	
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
		double timePerFrame = 1000000000.0/FPS;
		double timePerUpdate = 1000000000.0/UPS;
		
		long currentTime = System.nanoTime();
		long previousTime = System.nanoTime();
		
		long frames = 0;
		int updates = 0;
		long lastCheck = System.currentTimeMillis();
		
		double deltaU = 0;
		double deltaF = 0;
		
		while(gameThread != null) {
			currentTime = System.nanoTime();
			
			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;
			
			if(isRunning) {
				if(deltaU >= 1) {
					gamePanel.update(deltaU);
					updates++;
					deltaU--;
				}
				
				if(deltaF >= 1) {
					gamePanel.repaint();
					frames++;
					deltaF--;
				}
			}
			
			if(System.currentTimeMillis() - lastCheck >= 1000) {
				lastCheck = System.currentTimeMillis();
				if(gamePanel.inDebugMode()) {
					System.out.println("FPS: " + frames + " | UPS: " + updates);
				}
				frames = 0;
				updates = 0;
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