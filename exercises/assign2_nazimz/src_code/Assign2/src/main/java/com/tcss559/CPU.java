package com.tcss559;

public class CPU {
	int cores;
	int threads;
	int[] cache;
	int socket;
	int cache3D;
	
	public CPU() {
		cores = 0;
		threads = 2;
		cache = new int[3];
	}
	
	public CPU(int cores, int threads, int[] cache, int socket, int cache3D) {
		this.cores = cores;
		this.threads = threads;
		this.cache = cache;
		this.socket = socket;
		this.cache3D = cache3D;
	}
	
	public double evaluate() {
		return (this.socket * ((this.cores * this.threads) + ( cache[0] + 0.5 * cache[1] + 2 * cache[2]) + 10 * cache3D)); 
	}
	
	@Override
	public String toString() {
		return "CPU: -> " + "Cores: " + cores + " threads: " + threads + " L1 Cache: " + cache[0] + " L2 Cache: " + cache[1] + " L3 Cache: " + cache[2] + " Socket type: " + socket + " 3D Cache: " + cache3D;
	}
}
