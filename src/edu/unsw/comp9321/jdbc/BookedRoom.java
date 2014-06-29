package edu.unsw.comp9321.jdbc;

import java.util.Date;
//import java.sql.Date;


public class BookedRoom {


	private int id;
	private Date start;
	private Date end;
	private int roomsBooked;
	private boolean extraBed;
	private String city;
	private String roomType;
	private int price;

	private double total;
	
	
	
	
	
	public BookedRoom(int id, Date start, Date end, int roomsBooked,
			boolean extraBed, String city, String roomType, int price) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.roomsBooked = roomsBooked;
		this.extraBed = extraBed;
		this.city = city;
		this.roomType = roomType;
		this.price = price;
	
	}




	public int getId() {
		return id;
	}







	public void setId(int id) {
		this.id = id;
	}







	public Date getStart() {
		return start;
	}







	public void setStart(Date start) {
		this.start = start;
	}







	public Date getEnd() {
		return end;
	}







	public void setEnd(Date end) {
		this.end = end;
	}







	public int getRoomsBooked() {
		return roomsBooked;
	}







	public void setRoomsBooked(int roomsBooked) {
		this.roomsBooked = roomsBooked;
	}







	public boolean isExtraBed() {
		return extraBed;
	}







	public void setExtraBed(boolean extraBed) {
		this.extraBed = extraBed;
	}







	public String getCity() {
		return city;
	}







	public void setCity(String city) {
		this.city = city;
	}







	public String getRoomType() {
		return roomType;
	}







	public void setRoomType(String roomType) {
		this.roomType = roomType;
	}







	public int getPrice() {
		return price;
	}







	public void setPrice(int price) {
		this.price = price;
	}




	public double getTotal() {
		return total;
	}




	public void setTotal(double total) {
		this.total = total;
	}
	

	

}
