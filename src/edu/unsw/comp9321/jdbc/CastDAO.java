package edu.unsw.comp9321.jdbc;

import java.util.Date;
import java.util.List;

import edu.unsw.comp9321.exception.EmptyResultException;

public interface CastDAO {
	
	public List<CharacterDTO> findAll();
	
	public void storeComment(CommentDTO comment);
	
	public List<CommentDTO> getAllComments();

	public CharacterDTO findChar(String value) throws EmptyResultException;

	public List<CommentDTO> getComments(String character);

	public List<Room> searchRooms(Date begin, Date finish, String city, float maxPrice, int numRooms);

	public void storeBooking(Booking b);

	public int getLastId();

	public int getLastIdCustomer();

	public int getIdCity(String city);

	public void storeConsumer(int nextCustomerId, String email, int pin, String url);

	public boolean existsPin(int pin);

	public boolean existsId(String confirmId);

	public int getPinFromUrl(String confirmId);

	public int getIdFromUrl(String url);

	public List<BookedRoom> viewBookings(int consumerId);

	public boolean more48Hours(String confirmId);
	
	public double totalPrice(boolean addBed, int price, Date start, Date end, String type, int numRooms, String city);

	public void deleteBookings(int customerId);

	public double getDiscount(Date start, String roomTypeId, String cityId);

}
