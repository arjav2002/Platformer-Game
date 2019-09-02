package com.arjav.platformergame.utils;

public class FrameTimer {

	private double lastTime, numberOfTicks;
	private final double millisInOne = 1000;
	
	public FrameTimer(double numberOfTicks) {
		lastTime = System.currentTimeMillis() / millisInOne * numberOfTicks;
		this.numberOfTicks = numberOfTicks;
	}
	
	public double mark() {
		double currentTime = System.currentTimeMillis() / millisInOne * numberOfTicks;
		double dt = currentTime - lastTime;
		lastTime = currentTime;
		return dt;
	}
	
}
