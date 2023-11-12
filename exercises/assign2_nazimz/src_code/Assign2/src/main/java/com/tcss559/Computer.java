package com.tcss559;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.jws.WebService;

@WebService(targetNamespace = "http://tcss559.com/", portName = "ComputerPort", serviceName = "ComputerService")
public class Computer {
	double price;
	CPU processor;
	int cooling;
	RAM ram;
	Storage storage;
	GPU gcard;
	PSU power;
	double score = 0;
	
	public Computer() {
		price = 0;
		processor = new CPU();
		cooling = 0;
		ram = new RAM();
		storage = new Storage();
		gcard = new GPU();
		power = new PSU();
	}
	
	public Computer(double price, CPU processor, int cooling, RAM ram, Storage storage, GPU gcard, PSU power) {
		this.price = price;
		this.processor = processor;
		this.cooling = cooling;
		this.ram = ram;
		this.storage = storage;
		this.gcard = gcard;
		this.power = power;
	}
	
	public List<Double> metrics(){
		return new ArrayList<Double>(Arrays.asList(price, processor.evaluate(), (double) cooling, ram.evaluate(), storage.evaluate(), gcard.evaluate(), power.evaluate()));
	}
	
	@Override
	public String toString() {
		return "Computer Model \n" + "WSM Score: " + score + "\nPrice: $" + price + "\n" + processor + "\nCooling: " + cooling + "\n" + ram + "\n" + storage + "\n" + gcard + "\n" + power;
	}
}
