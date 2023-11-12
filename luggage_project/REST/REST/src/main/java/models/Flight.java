package models;

public class Flight {
	private String flightNum;
	private String departingAirport;
	private String arrivingAirport;
	private String flightStatus;
	
	public Flight(String flightNum, String departingAirport, 
			String arrivingAirport, String flightStatus) {
		this.flightNum = flightNum;
		this.departingAirport = departingAirport;
		this.arrivingAirport = arrivingAirport;
		this.flightStatus = flightStatus;
	}
	
	public String getFlightNum() {
		return flightNum;
	}
	
	public String getDepartingAirport() {
		return departingAirport;
	}
	
	public String getArrivingAirport() {
		return arrivingAirport;
	}
	
	public String getFlightStatus() {
		return flightStatus;
	}
}
