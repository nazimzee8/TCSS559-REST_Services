package com.tcss559;

public class GPU {
	double VRAM;
	double frequency;
	double cores;
	double tdp;
	double bandwidth;
	
	public GPU() {}
	
	public GPU(double VRAM, double frequency, double cores, double tdp, double bandwidth) {
		this.VRAM = VRAM;
		this.frequency = frequency;
		this.cores = cores;
		this.tdp = tdp;
		this.bandwidth = bandwidth;
	}
	
	public double evaluate() {
		return (VRAM / 8) * (bandwidth * frequency * cores) / tdp;
	}
	
	@Override
	public String toString() {
		return "GPU: -> " + "VRAM: " + VRAM + " Memory Speed: " + frequency + " Cuda Cores: " + cores + " Energy Cost: " + tdp + " Bandwidth: " + bandwidth;
	}
	
}
