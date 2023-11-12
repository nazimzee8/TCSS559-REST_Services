package com.tcss559;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.jws.WebService; 
import javax.jws.soap.SOAPBinding; 
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult; 
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@SuppressWarnings("unused")
@WebService()
@SOAPBinding( 
          style = SOAPBinding.Style.DOCUMENT, 
          use = SOAPBinding.Use.LITERAL, 
          parameterStyle = SOAPBinding.ParameterStyle.WRAPPED) 
public class WeightedSumModel {
	Document doc;
	List<Double> weights;
	List<Computer> computers;
	List<ArrayList<Double>> matrix = new ArrayList<ArrayList<Double>>();
	public Computer bestComputer;
	
	@WebMethod(exclude=true)
	public void initialize() throws ParserConfigurationException, SAXException, IOException {
		File file = new File("C:/Users/nazimz/eclipse-workspace/Assign2/src/main/java/com/tcss559/Dataset.xml"); 
		computers = new ArrayList<Computer>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
		DocumentBuilder db = dbf.newDocumentBuilder();   
		doc = db.parse(file);
		doc.getDocumentElement().normalize();
		
		NodeList pcList = doc.getElementsByTagName("computer"); 
		for (int i = 0; i < pcList.getLength(); i++) {
			matrix.add(new ArrayList<Double>());
			Node pc = pcList.item(i);
			CPU processor;
			RAM ram;
			Storage storage;
			GPU gpu;
			PSU psu;
			if (pc.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) pc;
				double price = Double.parseDouble(element.getElementsByTagName("price").item(0).getTextContent());
				int cooler = Integer.parseInt(element.getElementsByTagName("cooler").item(0).getTextContent());
				
				NodeList cpuList = element.getElementsByTagName("cpu");
				int cores = 0;
				int threads = 0;
				int socket = 0;
				int cache3D = 0;
				int[] cache = new int[3];
				for (int j = 0; j < cpuList.getLength(); j++) {
					if (cpuList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element node = (Element) cpuList.item(j);
						cores = Integer.parseInt(node.getElementsByTagName("cores").item(0).getTextContent());
						threads = Integer.parseInt(node.getElementsByTagName("threads").item(0).getTextContent());
						socket = Integer.parseInt(node.getElementsByTagName("socket").item(0).getTextContent());
						cache3D = Integer.parseInt(node.getElementsByTagName("cache3D").item(0).getTextContent());
						List<String> str = Arrays.asList(node.getElementsByTagName("cache").item(0).getTextContent().split(","));
						int k = 0;
						for (String s : str) {
							cache[k++] = Integer.parseInt(s);
						}
					}			
				}
				processor = new CPU(cores, threads, cache, socket, cache3D);
				
				NodeList ramList = element.getElementsByTagName("ram");
				int ddr = 0;
				int amt = 0;
				int latency = 0;
				int frequency = 0;
				for (int j = 0; j < ramList.getLength(); j++) {
					if (ramList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element node = (Element) ramList.item(j);
						ddr = Integer.parseInt(node.getElementsByTagName("ddr").item(0).getTextContent());
						amt = Integer.parseInt(node.getElementsByTagName("amt").item(0).getTextContent());
						latency = Integer.parseInt(node.getElementsByTagName("latency").item(0).getTextContent());
						frequency = Integer.parseInt(node.getElementsByTagName("frequency").item(0).getTextContent());
					}
				}
				ram = new RAM(ddr, amt, latency, frequency);
				
				NodeList storList = element.getElementsByTagName("storage");
				int speed = 0;
				int space = 0;
				for (int j = 0; j < storList.getLength(); j++) {
					if (storList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element node = (Element) storList.item(j);
						speed = Integer.parseInt(node.getElementsByTagName("speed").item(0).getTextContent());
						space = Integer.parseInt(node.getElementsByTagName("space").item(0).getTextContent());
					}
				}
				storage = new Storage(speed, space);
				
				NodeList gpuList = element.getElementsByTagName("gpu");
				double vram = 0;
				double clockspeed = 0;
				double cudas = 0;
				double tdp = 0;
				double bandwidth = 0;
				for (int j = 0; j < gpuList.getLength(); j++) {
					if (gpuList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element node = (Element) gpuList.item(j);
						vram = Double.parseDouble(node.getElementsByTagName("vram").item(0).getTextContent());
						clockspeed = Double.parseDouble(node.getElementsByTagName("frequency").item(0).getTextContent());
						cudas = Double.parseDouble(node.getElementsByTagName("cores").item(0).getTextContent());
						tdp = Double.parseDouble(node.getElementsByTagName("tdp").item(0).getTextContent());
						bandwidth = Double.parseDouble(node.getElementsByTagName("bandwidth").item(0).getTextContent());
					}
				}
				gpu = new GPU(vram, clockspeed, cudas, tdp, bandwidth);
				
				NodeList psuList = element.getElementsByTagName("psu");
				int efficiency = 0;
				int wattage = 0;
				for (int j = 0; j < psuList.getLength(); j++) {
					if (psuList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element node = (Element) psuList.item(j);
						efficiency = Integer.parseInt(node.getElementsByTagName("efficiency").item(0).getTextContent());
						wattage = Integer.parseInt(node.getElementsByTagName("wattage").item(0).getTextContent());
					}		
				}
				psu = new PSU(efficiency, wattage);
				Computer computer = new Computer(price, processor, cooler, ram, storage, gpu, psu);
				computers.add(computer);
				for (double metric : computer.metrics()) {
					matrix.get(i).add(metric);
				}
			}
		}
	}
	
	@WebMethod(exclude=true)
	public List<Computer> getComputers() {
		return computers;
	}
	
	@WebMethod(exclude=true)
	public String retrieveMatrix(double w1, double w2, double w3, double w4, double w5, double w6, double w7) throws ParserConfigurationException, SAXException, IOException {
		initialize();
		weights = new ArrayList<Double>(Arrays.asList(w1, w2, w3, w4, w5, w6, w7));
		normalize();
		StringBuilder sb = new StringBuilder(128);
		for (int i = 0; i < matrix.size(); i++) {
			sb.append(matrix.get(i) + "\n");
		}
		return "" + sb;
	}
	
	@WebMethod(exclude=true)
	public void normalize() {
		for (int i = 0; i < weights.size(); i++) {
			double sum = 0;
			for (int j = 0; j < matrix.size(); j++) {
				sum += matrix.get(j).get(i);
			}
			for (int j = 0; j < matrix.size(); j++) {
				double value = matrix.get(j).get(i);
				matrix.get(j).set(i, value / sum);
			}
		}
		//System.out.println(this);
	}
	
	@WebMethod
	@WebResult(name = "finalizedMatrix")
	public String finalizeMatrix(double w1, double w2, double w3, double w4, double w5, double w6, double w7) throws ParserConfigurationException, SAXException, IOException {
		return retrieveMatrix(w1, w2, w3, w4, w5, w6, w7);
	}
	
	@WebMethod(exclude=true)
	public List<Double> WeightedSum() {
		List<Double> weightedSums = new ArrayList<Double>();
		for (int i = 0; i < matrix.size(); i++) {
			double sum = 0;
			for (int j = 0; j < weights.size(); j++) { 
				sum += (weights.get(j) * matrix.get(i).get(j));
			}
			weightedSums.add(sum);
			computers.get(i).score = sum;
		}
		return weightedSums;
	}
	
	@WebMethod(exclude=true)
	public int PreferredSumChoice() {
		int index = 0;
		double max = 0;
		List<Double> weightedSums = WeightedSum();
		for (int i = 1; i < weightedSums.size(); i++) {
			if (weightedSums.get(i-1) <= weightedSums.get(i)) {
				index = i; 
				max = weightedSums.get(i);
			}
		}
		return index;
	}
	
	/*@WebMethod(exclude=true)
	@WebResult(name = "bestPrice")
	public String retrievePrice() {
		return "Price: $" + getBestSumComputer().price;
		//return "Price: $" + getBestProductComputer().price;
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "bestCPU")
	public String retrieveCPU() {
		return getBestSumComputer().processor.toString();
		//return getBestProductComputer().processor.toString();
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "bestCooling")
	public String retrieveCooling() {
		return "Cooling: " + getBestSumComputer().cooling;
		//return "Cooling: " + getBestProductComputer().cooling;
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "bestRAM")
	public String retrieveRam() {
		return getBestSumComputer().ram.toString();
		//return getBestProductComputer().ram.toString();
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "bestStorage")
	public String retrieveStorage() {
		return getBestSumComputer().storage.toString();
		//return getBestProductComputer().ram.toString();
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "bestGPU")
	public String retrieveGPU() {
		return getBestSumComputer().gcard.toString();
		//return getBestProductComputer().ram.toString();
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "bestPSU")
	public String retrievePSU() {
		return getBestSumComputer().power.toString();
		//return getBestProductComputer().ram.toString();
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "bestScore")
	public String retrieveScore() {
		return "WSM Score: " + getBestSumComputer().score;
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "BestSum")
	public Computer getBestSumComputer() {
		//bestComputer = computers.get(PreferredSumChoice());
		return computers.get(PreferredSumChoice());
	}*/
	
	@WebMethod
	@WebResult(name = "bestAlternative")
	public Computer retrieveWSM() {
		/*return retrieveScore() + "\n"
				+ retrievePrice() + "\n"
				+ retrieveCPU() + "\n"
				+ retrieveCooling() + "\n"
				+ retrieveRam() + "\n"
				+ retrieveStorage() + "\n"
				+ retrieveGPU() + "\n" 
				+ retrievePSU(); */
		return computers.get(PreferredSumChoice());
	}
	
	@WebMethod(exclude=true)
	public List<Double> WeightedProduct() {
		List<Double> weightedProducts = new ArrayList<Double>();
		for (int i = 0; i < matrix.size(); i++) {
			double product = 1;
			for (int j = 0; j < weights.size(); j++) {
				product = product * Math.pow(matrix.get(i).get(j), weights.get(j));
			}
			weightedProducts.add(product);
			computers.get(i).score = product;
		}
		return weightedProducts;
	}
	
	@WebMethod(exclude=true)
	public int PreferredProdChoice() {
		int index = 0;
		List<Double> weightedProducts = WeightedProduct();
		for (int i = 1; i < weightedProducts.size(); i++) {
			if (weightedProducts.get(i-1) <= weightedProducts.get(i)) {
				index = i;
			}
		} 
		return index;
	}
	
	@WebMethod(exclude=true)
	@WebResult(name = "BestProduct")
	public Computer getBestProdComputer() {
		return computers.get(PreferredProdChoice());
	}
	
	@WebMethod(exclude=true)
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(128);
		for (int i = 0; i < matrix.size(); i++) {
			sb.append(matrix.get(i) + "\n");
		}
		return "" + sb;
	}
	
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {
		//WeightedSumModel model = new WeightedSumModel(0.01, 0.19, 0.50, 0.05, 0.05, 0.10, 0.10);
		WeightedSumModel model = new WeightedSumModel();
		System.out.println(model.finalizeMatrix(0.25, 0.05, 0.15, 0.1, 0.25, 0.0, 0.20));
		System.out.println(model.retrieveWSM());
		/*// Used to output XML file after changes.
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		// initialize StreamResult with File object to save to file
		StreamResult result = new StreamResult(new StringWriter());
		DOMSource source = new DOMSource(model.doc);
		transformer.transform(source, result);
		String xmlString = result.getWriter().toString();
		System.out.println(xmlString);*/
	}
}
