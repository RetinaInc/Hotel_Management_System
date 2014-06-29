package edu.unsw.comp9321.jdbc;

import java.util.Date;
//import java.sql.Date;


public class Booking {


	private int id;
	private Date start;
	private Date end;
	private int roomId;
	private boolean extraBed;
	private int city;
	private int numRooms;
	private int consumerId;
	private int discountId;
	
	
	
	
	
	public Booking(int id, Date start ,Date end, int roomId, boolean extraBed, int city, int numRooms, 
			int consumerId, int discountId) {

		this.id = id;
		this.start = start;
		this.end = end;
		this.roomId = roomId;
		this.extraBed = extraBed;
		this.city = city;
		this.numRooms = numRooms;
		this.consumerId = consumerId;
		this.discountId = discountId;
		
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

	public int getRoomId() {
		return roomId;
	}

	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}

	public boolean isExtraBed() {
		return extraBed;
	}

	public void setExtraBed(boolean extraBed) {
		this.extraBed = extraBed;
	}

	public int getCity() {
		return city;
	}

	public void setCity(int city) {
		this.city = city;
	}

	public int getNumRooms() {
		return numRooms;
	}

	public void setNumRooms(int numRooms) {
		this.numRooms = numRooms;
	}

	public int getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(int consumerId) {
		this.consumerId = consumerId;
	}

	public int getDiscountId() {
		return discountId;
	}

	public void setDiscountId(int discountId) {
		this.discountId = discountId;
	}
	

}
