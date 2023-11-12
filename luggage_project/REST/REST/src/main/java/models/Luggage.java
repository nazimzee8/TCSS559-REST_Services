package models;

public class Luggage {
	private int userID;
	private String rfid;
	private String location;
	private String flightNum;
	
	public Luggage(int userID, String rfid, String location, String flightNum) {
		this.userID = userID;
		this.rfid = rfid;
		this.location = location;
		this.flightNum = flightNum;
	}
	
	public int getUserID() {
		return userID;
	}
	
	
	public String getRFID() {
		return rfid;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getFlightNum() {
		return flightNum;
	}
}