package edu.unsw.comp9321.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import edu.unsw.comp9321.common.ServiceLocatorException;
import edu.unsw.comp9321.exception.EmptyResultException;

public class DerbyDAOImpl implements CastDAO {

	static Logger logger = Logger.getLogger(DerbyDAOImpl.class.getName());
	private Connection connection;
	DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
	
	public DerbyDAOImpl() throws ServiceLocatorException, SQLException{
		connection = DBConnectionFactory.getConnection();
		logger.info("Got connection");
	}
	
	
	public void deleteBookings(int customerId) {
		
		try{
			
			String query_cast = "DELETE FROM Booking WHERE Booking.consumer_id = ?";
			
			PreparedStatement stmnt = connection.prepareStatement(query_cast);
			stmnt.setInt(1, customerId);
			
			int result = stmnt.executeUpdate();
		
			logger.info("Statement successfully executed "+result);
			
			stmnt.close();
		}catch(Exception e){
			logger.severe("Unable to store comment! ");
			e.printStackTrace();
		}
		
		return;
		
		
	}
	
	public double totalPrice(boolean addBed, int price, Date start, Date end, String type, int numRooms, String city) {
		
		
		int addBedCost = 0;
		if ( (addBed)) {
			addBedCost = 1;
		}
		
		if (type.equals("Single Room")) {
			addBedCost = 0;
		}
		
		double totalPrice = 0;
		
		
		Calendar now = Calendar.getInstance();
		now.setTime(start);
		now.add(Calendar.DAY_OF_YEAR, 1);
		Date now2 = new Date(now.getTime().getTime());	
		System.out.println("NowDate: " + now2);
		
		int bedCostPrice = 0;
		if (addBedCost == 1) {
			bedCostPrice = 35;
		}

		String year = String.valueOf(now.get(Calendar.YEAR));
		
		String datePeak1s = "".concat(year).concat("-01-01");
		Date peak1s = null;
		String datePeak1e = "".concat(year).concat("-02-15");
		Date peak1e = null;
		String datePeak2s = "".concat(year).concat("-03-25");
		Date peak2s = null;
		String datePeak2e = "".concat(year).concat("-04-14");
		Date peak2e = null;
		String datePeak3s = "".concat(year).concat("-07-01");
		Date peak3s = null;
		String datePeak3e = "".concat(year).concat("-07-20");
		Date peak3e = null;
		String datePeak4s = "".concat(year).concat("-09-20");
		Date peak4s = null;
		String datePeak4e = "".concat(year).concat("-10-10");
		Date peak4e = null;
		String datePeak5s = "".concat(year).concat("-12-15");
		Date peak5s = null;
		String datePeak5e = "".concat(year).concat("-12-31");
		Date peak5e = null;
		
		try {
			peak1s = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak1s);
			peak1e = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak1e);
			peak2s = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak2s);
			peak2e = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak2e);
			peak3s = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak3s);
			peak3e = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak3e);
			peak4s = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak4s);
			peak4e = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak4e);
			peak5s = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak5s);
			peak5e = new SimpleDateFormat("yyyy-MM-dd").parse(datePeak5e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		double peakIncrease = 0;
		//find if peak periods
		if ((now2.after(peak1s) && now2.before(peak1e))  || (now2.after(peak2s) && now2.before(peak2e))
				 || (now2.after(peak3s) && now2.before(peak3e)) || (now2.after(peak4s) && now2.before(peak4e))
				 || (now2.after(peak5s) && now2.before(peak5e))) {
			
			peakIncrease = 0.4; 
		}
		
		totalPrice += ((price + bedCostPrice) * (1 + peakIncrease) * (1 - getDiscount(now2, type, city)));
		
		
		while (now2.compareTo(end) != 0) {
			now.add(Calendar.DAY_OF_YEAR, 1);
			now2 = new Date(now.getTime().getTime());	
			System.out.println("NowDate: " + now2);
			
			if ((now2.after(peak1s) && now2.before(peak1e))  || (now2.after(peak2s) && now2.before(peak2e))
					 || (now2.after(peak3s) && now2.before(peak3e)) || (now2.after(peak4s) && now2.before(peak4e))
					 || (now2.after(peak5s) && now2.before(peak5e))) {
				
				peakIncrease = 0.4; 
			}
			
			totalPrice += ((price + bedCostPrice) * (1 + peakIncrease) * (1 - getDiscount(now2, type, city)));
			
		}
		
		System.out.println("Total Price: $" + totalPrice);	
		System.out.println("For No. Rooms: " + numRooms + "Total: $" + (totalPrice * numRooms));	
		return (totalPrice * numRooms);
		
		
		
		
	}
	
	
	@Override
	public int getIdFromUrl(String url){
		
			try{
				
				String query_cast = "SELECT consumer_id as id FROM Consumer WHERE url = ?";
				
				PreparedStatement stmnt = connection.prepareStatement(query_cast);
				stmnt.setString(1, url);
				
				ResultSet res = stmnt.executeQuery();
				
				while(res.next()) {
					return(res.getInt("id"));
				}
				
				stmnt.close();
			}catch(Exception e){
				logger.severe("Unable to store comment! ");
				e.printStackTrace();
			}
			
			return 0;
		
	}
	
	@Override
	public int getPinFromUrl(String url){
		
			try{
				
				String query_cast = "SELECT pin FROM Consumer WHERE url = ?";
				
				PreparedStatement stmnt = connection.prepareStatement(query_cast);
				stmnt.setString(1, url);
				
				ResultSet res = stmnt.executeQuery();
				
				while(res.next()) {
					return(res.getInt("pin"));
				}
				
				stmnt.close();
			}catch(Exception e){
				logger.severe("Unable to store comment! ");
				e.printStackTrace();
			}
			
			return 0;
		
	}
	
	
	@Override
	public boolean more48Hours(String confirmId) {
		
		//look up first booking with the consumer with this confirmId
		//and then get their start date
		//then compare start date - 48 hours to current time
		
			try{
				
				String query_cast = "SELECT consumer_id as id FROM Consumer WHERE url = ?";
				
				PreparedStatement stmnt = connection.prepareStatement(query_cast);
				stmnt.setString(1, confirmId);
				
				ResultSet res = stmnt.executeQuery();
				
				int id = 0;
				while(res.next()) {
					id = res.getInt("id");
				}
				System.out.println("id: "+id);
				
				query_cast = "SELECT b.start_date as start FROM Booking b " +
						"join Consumer c on (c.consumer_id = b.consumer_id) " +
						"WHERE b.consumer_id = ?";
				
				stmnt = connection.prepareStatement(query_cast);
				stmnt.setInt(1, id);
				res = stmnt.executeQuery();
				
				int flag = 0;
				Date start = null;
				while (res.next() && flag != 1) {
					start = res.getDate("start");
					flag = 1;
				}
				
				Calendar c2 = Calendar.getInstance();
				c2.setTime(start);
				Date start2 = c2.getTime();
				System.out.println("start: "+start2);
				Date now = new Date();
				
				Calendar c = Calendar.getInstance();
				c.setTime(now);
				c.add(Calendar.DATE, 1);
				Date nowp48 = c.getTime();
				
				System.out.println("now: " + now);
				
				if (nowp48.before(start2)) {
					return true;
				}
				
				stmnt.close();
			}catch(Exception e){
				logger.severe("Unable to store comment! ");
				e.printStackTrace();
			}
			
			return false;
		
	}
	
	@Override
	public boolean existsId(String id) {
		
			try{
				
				String query_cast = "SELECT url FROM Consumer WHERE url = ?";
				
				PreparedStatement stmnt = connection.prepareStatement(query_cast);
				stmnt.setString(1, id);
				
				ResultSet res = stmnt.executeQuery();
				
				if(!res.next()) {
					return false;
				}
				
				
				stmnt.close();
			}catch(Exception e){
				logger.severe("Unable to store comment! ");
				e.printStackTrace();
			}
			
			return true;
		
	}
	
	@Override
	public boolean existsPin(int pin) {
		
			try{
				
				String query_cast = "SELECT pin FROM Consumer WHERE pin = ?";
				
				PreparedStatement stmnt = connection.prepareStatement(query_cast);
				stmnt.setInt(1, pin);
				
				ResultSet res = stmnt.executeQuery();
				
				if(!res.next()) {
					return false;
				}
				
				
				stmnt.close();
			}catch(Exception e){
				logger.severe("Unable to store comment! ");
				e.printStackTrace();
			}
			
			return true;
		
	}
	
	
	@Override
	public int getIdCity(String city) {
		
			int id = 0;
			
			try{
				String query_cast = "SELECT city_id as id FROM City WHERE name = ?";
				
				PreparedStatement stmnt = connection.prepareStatement(query_cast);
				stmnt.setString(1, city);
				
				ResultSet res = stmnt.executeQuery();
				
				while(res.next()) {
					id = res.getInt("id");
				}
				
				
				stmnt.close();
			}catch(Exception e){
				logger.severe("Unable to store comment! ");
				e.printStackTrace();
			}
			
			return id;
		
	}
	
	
	@Override
	public int getLastIdCustomer() {
		int id = 0;
		try{
			Statement stmnt = connection.createStatement();
			String query_cast = "SELECT consumer_id as id FROM Consumer";
			ResultSet res = stmnt.executeQuery(query_cast);
			//logger.info("The result set size is "+res.getFetchSize());
			while(res.next()) {
				id = res.getInt("id");
				//System.out.println("555--- " +id);
			}
			
			
			res.close();
			stmnt.close();
			
		}catch(Exception e){
			System.out.println("Caught Exception");
			e.printStackTrace();
		}
		
		
		return id;
		
	}
	
	@Override
	public int getLastId() {
		int id = 0;
		try{
			Statement stmnt = connection.createStatement();
			String query_cast = "SELECT booking_id as id FROM Booking";
			ResultSet res = stmnt.executeQuery(query_cast);
			//logger.info("The result set size is "+res.getFetchSize());
			while(res.next()) {
				id = res.getInt("id");
				//System.out.println("555--- " +id);
			}
			
			
			res.close();
			stmnt.close();
			
		}catch(Exception e){
			System.out.println("Caught Exception");
			e.printStackTrace();
		}
		
		
		return id;
		
	}
	
	
	@Override
	public void storeConsumer(int id, String email, int pin, String url) {

		try{
			String sqlStr = 
				"INSERT INTO Consumer (email, pin, url) "+
				"VALUES (?,?,?)";
			PreparedStatement stmnt = connection.prepareStatement(sqlStr);
			//stmnt.setInt(2, id);
			stmnt.setString(1, email);
			stmnt.setInt(2, pin);
			stmnt.setString(3, url);
			
			int result = stmnt.executeUpdate();
			logger.info("Statement successfully executed "+result);
			stmnt.close();
		}catch(Exception e){
			logger.severe("Unable to store comment! ");
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void storeBooking(Booking b) {

		try{
			String sqlStr = 
				"INSERT INTO Booking (start_date, end_date, room_type_id, " +
				"extra_bed, city_id, num_rooms, consumer_id) "+
				"VALUES (?,?,?,?,?,?,?)";
			PreparedStatement stmnt = connection.prepareStatement(sqlStr);
			//stmnt.setInt(1,b.getId());
			
			//String start = b.getStart().toString();		
			//Sat Jan 04 00:00:00 EST 2014
			java.sql.Date start2 = new java.sql.Date(b.getStart().getTime());
			java.sql.Date end2 = new java.sql.Date(b.getEnd().getTime());
			
			stmnt.setDate(1, start2);
			stmnt.setDate(2, end2);
			stmnt.setInt(3, b.getRoomId());
			stmnt.setBoolean(4, b.isExtraBed());
			stmnt.setInt(5, b.getCity());
			stmnt.setInt(6,b.getNumRooms());
			stmnt.setInt(7,b.getConsumerId());
			
			
			//System.out.println("startDATE:" + start2);
			
			int result = stmnt.executeUpdate();
			logger.info("Statement successfully executed "+result);
			stmnt.close();
		}catch(Exception e){
			logger.severe("Unable to store comment! ");
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public List<BookedRoom> viewBookings(int consumerId) {
		ArrayList<BookedRoom> bookings = new ArrayList<BookedRoom>();
		
		try{
			
			String query_cast = "SELECT b.booking_id as id, b.start_date as sd, b.end_date as ed, " +
					"b.num_rooms as num, c.name as cName, rt.name as rtName, rt.price as rPrice, " +
					"b.extra_bed as bed FROM Booking b " +
					"join RoomType rt on (rt.room_type_id = b.room_type_id) " +
					"join City c on (c.city_id = b.city_id) " +
					"WHERE b.consumer_id = ? ";
			PreparedStatement stmnt = connection.prepareStatement(query_cast);
			stmnt.setInt(1, consumerId);
			
		
			ResultSet res = stmnt.executeQuery();
			logger.info("The result set size is "+res.getFetchSize()); 
			while(res.next()){
				
				int id = res.getInt("id");
				Date start = res.getDate("sd");
				Date end = res.getDate("ed");
				int roomsBooked = res.getInt("num");
				boolean extraBed = res.getBoolean("bed");
				String city = res.getString("cName");
				String roomType = res.getString("rtName");
				int price = res.getInt("rPrice");
				
					System.out.println("Start: "+start);
					System.out.println("End: "+end);
					
					
				
					bookings.add(new BookedRoom(id, start, end, roomsBooked, extraBed, city, roomType, price));
			
			}
	
			
			res.close();
			stmnt.close();
			
		}catch(Exception e){
			System.out.println("Caught Exception");
			e.printStackTrace();
		}
		return bookings;
		
	}
	
	@Override
	public double getDiscount(Date begin, String roomType, String city) {
		
		ArrayList<DateRange> dates = new ArrayList<DateRange>();
		System.out.println("---"+roomType+"---"+city);
		try{
			
			//get cityid from city name
			String query_cast = "SELECT city_id as id FROM City WHERE name = ?";
			
			PreparedStatement stmnt = connection.prepareStatement(query_cast);
			stmnt.setString(1, city);
			
			ResultSet res = stmnt.executeQuery();
			int cityId = 0;
			while(res.next()) {
				cityId = res.getInt("id");
			}
			
			query_cast = "SELECT room_type_id as id FROM RoomType WHERE name = ?";
			stmnt = connection.prepareStatement(query_cast);
			stmnt.setString(1, roomType);
			
			res = stmnt.executeQuery();
			int roomTypeId = 0;
			while(res.next()) {
				roomTypeId = res.getInt("id");
			}
			
			System.out.println(roomTypeId + "---" + cityId);
			
			//find all discounts for given room and given city
			query_cast = "SELECT d.discount_id as id, d.start_date as start, d.end_date as ed, " +
					"d.reduced_rate as rate FROM Discount d " +
					"WHERE d.city_id = ? " +
					"AND d.room_type_id = ?";
			stmnt = connection.prepareStatement(query_cast);
			stmnt.setInt(1, cityId);
			stmnt.setInt(2, roomTypeId);
			res = stmnt.executeQuery();
			
			
			
			while(res.next()) {
				Date start = res.getDate("start");
				Date end = res.getDate("ed");
				int id = res.getInt("id");
				double rate = res.getDouble("rate");
				
				//for each discount, find if it conflicts with the start,end date

				if (((begin.after(start) || start.equals(begin)) && (begin.before(end) || start.equals(end))))
				{
					dates.add(new DateRange(id, start, end, rate));
						
				}
			}
			
			res.close();
			stmnt.close();
			
			if (dates.size() == 0) {
				return 0;
			} else {
				return dates.get(0).getRate();
			}
			
			
			
			
		}catch(Exception e){
			System.out.println("Caught Exception");
			e.printStackTrace();
		}
		return 0;
		
	}
	
	@Override
	public List<Room> searchRooms(Date begin, Date finish, String reqCity, float maxPrice, int reqNumRooms) {
		ArrayList<Booking> conflictingBookings = new ArrayList<Booking>();
		ArrayList<Room> rooms = new ArrayList<Room>();
		try{
			
			String query_cast = "SELECT b.booking_id as id, b.start_date as sd, b.end_date as ed, b.room_type_id as rid, " +
					"b.num_rooms as num FROM Booking b " +
					"join RoomType rt on (rt.room_type_id = b.room_type_id) " +
					"join City c on (c.city_id = b.city_id) " +
					"WHERE c.name = ? " +
					"AND rt.price <= ? " +
					"AND rt.total_available >= ?";
			PreparedStatement stmnt = connection.prepareStatement(query_cast);
			stmnt.setString(1, reqCity);
			stmnt.setFloat(2, maxPrice);
			stmnt.setInt(3, reqNumRooms);
			
			System.out.println(reqCity);
		
			ResultSet res = stmnt.executeQuery();
			logger.info("The result set size is "+res.getFetchSize()); 
			while(res.next()){
				
				int id = res.getInt("id");
				Date start = res.getDate("sd");
				Date end = res.getDate("ed");
				int roomId = res.getInt("rid");
				
				int roomsBooked = res.getInt("num");
				
					System.out.println("Start: "+start);
					System.out.println("End: "+end);
				
				//find any that would disallow this booking - 3 cases
				if (((start.before(begin) || start.equals(begin)) && end.after(begin)) || 
						((start.after(begin) || start.equals(begin)) && (end.before(finish) || end.equals(finish))) ||
						((end.after(finish) || end.equals(finish)) && start.before(finish)))
				{
					conflictingBookings.add(new Booking(id, start, end, roomId,false,0,roomsBooked,0,0));
					//add in roomId?
					//rooms that conflict with that range of time, but in the right  and right price
				}
						
			
			}
			
			//query_cast = "SELECT * FROM room r join capacity c on (c.roomId = r.id) " +
				//	"WHERE r.city = ? " +
				//	"AND r.price <= ?";
			query_cast = "SELECT rt.room_type_id as id, rt.name as type, rt.price as price, rt.total_available as numRooms" +
					" FROM RoomType rt WHERE rt.price <= ?";
			stmnt = connection.prepareStatement(query_cast);
			stmnt.setFloat(1, maxPrice);
			System.out.println(reqCity);
		
			res = stmnt.executeQuery();
			
			
			while(res.next()){
				int id = res.getInt("id");
				String type = res.getString("type");
				String price = res.getString("price");
				int numRooms = res.getInt("numRooms");
			
				rooms.add(new Room(id, type, price, "", numRooms));	//all possible rooms
			}
			//conflictingBookings - search it for roomIds and 
			//decrement associated rooms Array numRooms
			
			
			
			
			for (Booking b : conflictingBookings) {
				System.out.println(b.getStart());
				System.out.println(b.getEnd());
			}
				
					int x = -1;
				for (Room r : rooms) {
					x++;
					int roomid = r.getId();
					System.out.println("bb:"+r.getId());
					//look up room in list of available rooms
					
					for (Booking b : conflictingBookings) {
						System.out.println("aaa:"+b.getId());
						
						if (roomid == b.getRoomId()) {
							r.setNumRooms(r.getNumRooms() - b.getNumRooms());
							
							System.out.println(reqNumRooms);
							System.out.println(r.getNumRooms());
						}
					}
					if (r.getNumRooms() < reqNumRooms) {
						//System.out.println("5555");
						//remove that room
						//rooms.remove(x);
						//System.out.println("5555");
					}
					
				}
				
			
			
			
			
			res.close();
			stmnt.close();
			
		}catch(Exception e){
			System.out.println("Caught Exception");
			e.printStackTrace();
		}
		return rooms;
		
	}
	
	@Override
	public List<CharacterDTO> findAll() {
		ArrayList<CharacterDTO> cast = new ArrayList<CharacterDTO>();
		try{
			Statement stmnt = connection.createStatement();
			String query_cast = "SELECT MVCHAR_ID, MVCHAR_NAME, DIET, SOUNDS FROM TBL_CHARACTERS";
			ResultSet res = stmnt.executeQuery(query_cast);
			logger.info("The result set size is "+res.getFetchSize());
			while(res.next()){
				int id = res.getInt("MVCHAR_ID");
				logger.info(" "+id);
				String name = res.getString("MVCHAR_NAME");
				logger.info(name);
				String diet = res.getString("DIET");
				logger.info(diet);
				String soundsArr= res.getString("SOUNDS");
				logger.info(soundsArr);
				logger.info(name+" "+diet+" "+soundsArr);
				String[] sounds = soundsArr.split(",");
				cast.add(new CharacterDTO(id, name, diet, sounds));
			}
			
			res.close();
			stmnt.close();
			
		}catch(Exception e){
			System.out.println("Caught Exception");
			e.printStackTrace();
		}
		return cast;
	}
	
	
	@Override
	public void storeComment(CommentDTO comment) {
		
		PreparedStatement stmnt = null; 
		
		try{
			String sqlStr = 
				"INSERT INTO TBL_COMMENTS (MVCHAR_ID, MVCHAR_NAME, USERNAME, COMMENT_DATE, COMMENT) "+
				"VALUES (?,?,?,?,?)";
			stmnt = connection.prepareStatement(sqlStr);
			stmnt.setInt(1,comment.getCharacterID());
			stmnt.setString(2, comment.getCharacterName());
			stmnt.setString(3, comment.getUser());
			stmnt.setString(4, fmt.format(comment.getCommentDate()));
			stmnt.setString(5, comment.getComment());
			logger.info("sql string is "+stmnt.toString());
			int result = stmnt.executeUpdate();
			logger.info("Statement successfully executed "+result);
			stmnt.close();
		}catch(Exception e){
			logger.severe("Unable to store comment! ");
			e.printStackTrace();
		}
		
	}
	
	public List<CommentDTO> getAllComments() {
		
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		try{
			Statement stmnt = connection.createStatement();
			ResultSet results = stmnt.executeQuery("SELECT * FROM TBL_COMMENTS");
			logger.info("Fetched comments");
			while(results.next()){
				int charID = results.getInt("MVCHAR_ID");
				String charName = results.getString("MVCHAR_NAME");
				Date commentDate = fmt.parse(results.getString("COMMENT_DATE"));
				String user = results.getString("USERNAME");
				String comment = results.getString("COMMENT");
				comments.add(new CommentDTO(charID, charName, user, commentDate, comment));
			}
			results.close();
			stmnt.close();
		}catch(Exception e){
			logger.severe("Failed to get comments "+e.getStackTrace());
		}
		
		return comments;
	}

	public CharacterDTO findChar(String value) throws EmptyResultException{
		
		CharacterDTO mvChar = null;
		
		try{
			logger.info("The value passed is: "+value);
			
			String count_query = "SELECT COUNT(*) FROM TBL_CHARACTERS WHERE MVCHAR_NAME = ?";
			PreparedStatement count_stmnt = connection.prepareStatement(count_query);
			count_stmnt.setString(1, value);
			ResultSet count_res = count_stmnt.executeQuery();
			count_res.next();
			int numRows = count_res.getInt(1);
			logger.info("The result set size is "+numRows);
			//if(numRows==0) throw new EmptyResultException();
			
			String query_cast = "SELECT * FROM TBL_CHARACTERS WHERE MVCHAR_NAME = ?";
			PreparedStatement stmnt = connection.prepareStatement(query_cast);
			stmnt.setString(1, value);
			ResultSet res = stmnt.executeQuery();
			res.next();
			int id = res.getInt("MVCHAR_ID");
			logger.info(" "+id);
			String name = res.getString("MVCHAR_NAME");
			logger.info(name);
			String diet = res.getString("DIET");
			logger.info(diet);
			String soundsArr= res.getString("SOUNDS");
			logger.info(soundsArr);
			logger.info(name+" "+diet+" "+soundsArr);
			String[] sounds = soundsArr.split(",");
			mvChar = new CharacterDTO(id, name, diet, sounds);
			
		}catch(Exception e){
			System.out.println("Caught Exception");
			e.printStackTrace();
			throw new EmptyResultException();
		}
		
		return mvChar;
	}

	@Override
	public List<CommentDTO> getComments(String character) {
		List<CommentDTO> comments = new ArrayList<CommentDTO>();
		try{
			Statement stmnt = connection.createStatement();
			ResultSet results = stmnt.executeQuery("SELECT * FROM TBL_COMMENTS WHERE MVCHAR_NAME = '"+character+"'");
			logger.info("Fetched comments");
			while(results.next()){
				int charID = results.getInt("MVCHAR_ID");
				String charName = results.getString("MVCHAR_NAME");
				Date commentDate = fmt.parse(results.getString("COMMENT_DATE"));
				String user = results.getString("USERNAME");
				String comment = results.getString("COMMENT");
				comments.add(new CommentDTO(charID, charName, user, commentDate, comment));
			}
			results.close();
			stmnt.close();
		}catch(Exception e){
			logger.severe("Failed to get comments "+e.getStackTrace());
		}
		
		return comments;
	}
	

}
