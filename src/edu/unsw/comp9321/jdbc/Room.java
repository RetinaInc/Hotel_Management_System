package edu.unsw.comp9321.jdbc;


public class Room {


	private int id;
	private String type;
	private String price;
	private String city;
	private int numRooms;
	
	
	
	public Room(int id, String type, String price, String city, int numRooms) {

		this.id = id;
		this.type = type;
		this.price = price;
		this.city = city;
		this.numRooms = numRooms;
	}
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public int getNumRooms() {
		return numRooms;
	}

	public void setNumRooms(int numRooms) {
		this.numRooms = numRooms;
	}	
	

}
