package com.tcss559;

public class RAM {
	int DDR;
	int amt;
	int latency;
	int frequency;
	
	public RAM() {
		DDR = 0;
		amt = 0;
		latency = 0;
		frequency = 1;
	}
	
	public RAM(int DDR, int amt, int latency, int frequency) {
		this.DDR = DDR;
		this.amt = amt;
		this.latency = latency;
		this.frequency = frequency;
	}
	
	public double evaluate() {
		return (this.latency * 2000) / (this.frequency-amt);
	}
	
	@Override
	public String toString() {
		return "RAM: -> " + "Memory Type: DDR" + DDR + " Total Memory: " + amt + " CAS Latency: C" + latency + " Frequency: " + frequency;
	}
}
