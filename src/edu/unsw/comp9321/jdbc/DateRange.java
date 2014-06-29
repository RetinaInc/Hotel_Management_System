package edu.unsw.comp9321.jdbc;

import java.util.Date;
//import java.sql.Date;


public class DateRange {


	private int id;
	private Date start;
	private Date end;
	private double rate;
	
	
	
	
	
	public DateRange(int id, Date start, Date end, double rate) {
		this.id = id;
		this.start = start;
		this.end = end;
		this.rate = rate;
	
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





	public double getRate() {
		return rate;
	}





	public void setRate(double rate) {
		this.rate = rate;
	}





	

}
