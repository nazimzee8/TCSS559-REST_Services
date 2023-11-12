package com.tcss559;

public class PSU {
	double efficiency;
	double wattage;
	
	public PSU() {}
	
	public PSU(double efficiency, double wattage) {
		this.efficiency = efficiency;
		this.wattage = wattage;
	}
	
	public double evaluate() {
		return (efficiency * wattage);
	}
	
	@Override
	public String toString() {
		return "Power Supply: -> " + "Efficiency: " + efficiency + "% Wattage: " + wattage; 
	}
}
