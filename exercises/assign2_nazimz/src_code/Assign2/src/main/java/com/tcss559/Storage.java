package com.tcss559;

public class Storage {
	int speed;
	int space;
	
	public Storage() {}
	
	public Storage(int speed, int space) {
		this.speed = speed;
		this.space = space;
	}
	
	public double evaluate() {
		return this.speed * this.space;
	}
	
	@Override
	public String toString() {
		return "Storage: -> " + "Speed: " + speed + " Total Space: " + space;
	}
}
