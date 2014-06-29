package edu.unsw.comp9321.logic;

import edu.unsw.comp9321.*;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.unsw.comp9321.common.ServiceLocatorException;
import edu.unsw.comp9321.exception.EmptyResultException;
import edu.unsw.comp9321.exception.InvalidActionException;
import edu.unsw.comp9321.jdbc.BookedRoom;
import edu.unsw.comp9321.jdbc.CastDAO;
import edu.unsw.comp9321.jdbc.CommentDTO;
import edu.unsw.comp9321.jdbc.DerbyDAOImpl;
import edu.unsw.comp9321.jdbc.CharacterDTO;
import edu.unsw.comp9321.jdbc.Booking;
import edu.unsw.comp9321.jdbc.Room;
import edu.unsw.comp9321.mail.MailSender;

/**
 * Servlet implementation class Controller
 */
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(Controller.class.getName());
	private CastDAO cast;
       
    /**
     * @throws ServletException 
     * @see HttpServlet#HttpServlet()
     */
    public Controller() throws ServletException {
    	// TODO Auto-generated constructor stub
        super();
        try {
			cast = new DerbyDAOImpl();
		} catch (ServiceLocatorException e) {
			logger.severe("Trouble connecting to database "+e.getStackTrace());
			throw new ServletException();
		} catch (SQLException e) {
			logger.severe("Trouble connecting to database "+e.getStackTrace());
			throw new ServletException();
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String forwardPage = "welcome.jsp";
		
		String action = request.getParameter("action");
		
		String confirmId = null;
		if (request.getParameter("confirmId") != null) {
			confirmId = request.getParameter("confirmId");
		}
		
		if (confirmId != null && action == null) {
			System.out.println("4wtq4tq3---: " + confirmId);
			request.setAttribute("confirmId", confirmId);
			
			
			//check if more than 48 hours away from start date
			
			if (cast.existsId(confirmId) && cast.more48Hours(confirmId)) {
				request.setAttribute("valid", true);
				
				//request.setAttribute("pin",cast.getPinFromUrl(confirmId));
				
			} else {
				request.setAttribute("valid", false);
				
			}
			
			forwardPage = "confirm.jsp";
		
		} else if (action.equals("searchRoom")) {
			
			// Validation Code -----------------------------------------// 
				
			boolean begin_Flag = false;
            boolean finish_Flag = false;
            boolean city_Flag = false;
            boolean numrooms_Flag = false;
            boolean maxprice_Flag = false;
            
            if(!request.getParameter("city").matches("^[a-zA-Z]+$"))
            {
                city_Flag = true;
                request.setAttribute("city_Error", 1);
            }
            
            if(!request.getParameter("begin").matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))
            {
                begin_Flag = true;
                request.setAttribute("begin_Error", 1);
            }
            
            if(!request.getParameter("finish").matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))
            {
                finish_Flag = true;
                request.setAttribute("finish_Error", 1);
                
            } else {
            	Date f = null;
            	Date s = null;		
            	try {
					f = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("finish"));
					s = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("begin"));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	if (!(f.after(s))) {
            		finish_Flag = true;
                    request.setAttribute("finish_Error", 1);
            	}
            }
            
            if(!request.getParameter("numrooms").matches("^[0-9]+$"))
            {
                numrooms_Flag = true;
                request.setAttribute("numrooms_Error", 1);
            }
			
            if(!request.getParameter("maxprice").matches("^[0-9]+$"))
            {
                numrooms_Flag = true;
                request.setAttribute("maxprice_Error", 1);
            }
            
			// ------------------------------------------------------------- //
			
            if(!begin_Flag && !finish_Flag && !city_Flag && !numrooms_Flag && !maxprice_Flag)
            {
            
				//System.out.println(UUID.randomUUID().toString());
				
				if (request.getParameter("existingCustomer") != null) {
					request.setAttribute("existingCustomer", request.getParameter("existingCustomer"));
				}
			
				String begin2 = request.getParameter("begin");
				String finish2 = request.getParameter("finish");
				Date begin = null;
				Date finish = null;
				try {
					begin = new SimpleDateFormat("yyyy-MM-dd").parse(begin2);
					finish = new SimpleDateFormat("yyyy-MM-dd").parse(finish2);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("--8687--" + begin);
				String city = request.getParameter("city");
				float maxPrice = Float.parseFloat(request.getParameter("maxprice"));
				int numRooms = Integer.parseInt(request.getParameter("numrooms"));
				System.out.println("7777");
				List<Room> rooms = cast.searchRooms(begin, finish, city, maxPrice, numRooms);
				//System.out.println("qqq: "+rooms.get(0).getNumRooms());
				
				boolean allFull = true;
				for (Room r : rooms) {
					if ((r.getNumRooms() - numRooms) >= 0) {
						allFull = false;
					}
				}
				if (allFull) {
					request.setAttribute("resultsFound", false);
					
					if (request.getParameter("existingCustomer") != "") {
						//give option to delete existing booking
						request.setAttribute("delete", true);
					} 
					
				} else {
					request.setAttribute("resultsFound", true);
				}
				System.out.println("8888");
				request.setAttribute("rooms", rooms);
				
				
				request.setAttribute("begin", begin2);
				request.setAttribute("finish", finish2);
				request.setAttribute("city", city);
				request.setAttribute("maxPrice", maxPrice);
				request.setAttribute("numRooms", numRooms);
				request.getSession().setAttribute("madeBooking", null);
				
				
				forwardPage = "searchResults.jsp";
            }
			
		} else if (action.equals("bookRoom")) {
			
			if (request.getParameter("existingCustomer") != null) {
				request.setAttribute("existingCustomer", request.getParameter("existingCustomer"));
			}
			
			String bookings = request.getParameter("radioButton");
			String addBed = request.getParameter("checkBox");
			
			System.out.println("--d1--" + addBed);
			 //{r.id}~${r.type}~${r.price}~${city}~${r.numRooms}"/>
			 String[] elems = bookings.split("~");
			 int nextId = cast.getLastId() + 1;
			 //System.out.println("----" + nextId);
			 Date start = null;
			 Date end = null;
			 //System.out.println("--d1--" + elems[0]);
			 //System.out.println("--d2--" + elems[1]);
			try {
				end = new SimpleDateFormat("yyyy-MM-dd").parse(elems[1]);
				start = new SimpleDateFormat("yyyy-MM-dd").parse(elems[0]);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			String city = elems[3];
			int numRooms = Integer.parseInt(elems[4]);
			
			String type = elems[5];
			
			int addBedCost = 0;
			if ( ! (addBed == null)) {
				addBedCost = 1;
			}
			
			if (type.equals("Single Room")) {
				addBedCost = 0;
			}
			
			double totalPrice = 0;
			int price = Integer.parseInt(elems[6]);
			
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
			
			totalPrice += ((price + bedCostPrice) * (1 + peakIncrease) * (1 - cast.getDiscount(now2, type, city)));
			
			System.out.println(totalPrice);
			while (now2.compareTo(end) != 0) {
				now.add(Calendar.DAY_OF_YEAR, 1);
				now2 = new Date(now.getTime().getTime());	
				System.out.println("NowDate: " + now2);
				
				if ((now2.after(peak1s) && now2.before(peak1e))  || (now2.after(peak2s) && now2.before(peak2e))
						 || (now2.after(peak3s) && now2.before(peak3e)) || (now2.after(peak4s) && now2.before(peak4e))
						 || (now2.after(peak5s) && now2.before(peak5e))) {
					
					peakIncrease = 0.4; 
				}
				
				totalPrice += ((price + bedCostPrice) * (1 + peakIncrease) * (1 - cast.getDiscount(now2, type, city)));
				
				System.out.println(totalPrice);
			}
			
			System.out.println("Total Price: $" + totalPrice);	
			System.out.println("For No. Rooms: " + numRooms + "Total: $" + (totalPrice * numRooms));
			double total = (totalPrice * numRooms);
			
			request.setAttribute("total", total);
			
			
			/////////////Multiple totalPrice by number of rooms bought
			////////////////Fails if a user enters a start year of x and the end date year is >=x+2
			request.setAttribute("nextId", nextId);
			request.setAttribute("start", elems[0]);
			request.setAttribute("end", elems[1]);
			request.setAttribute("city", city);
			request.setAttribute("roomId", elems[2]);
			request.setAttribute("numrooms", numRooms);
			request.setAttribute("addBed", addBedCost);
			
			request.setAttribute("type", type);
			
			
			
			forwardPage = "checkout.jsp";
			//forwardPage = "welcome.jsp";
			
		} else if (action.equals("back")) {
			
			if (request.getParameter("existingCustomer") != null) {
				request.setAttribute("existingCustomer", request.getParameter("existingCustomer"));
			}
			forwardPage = "welcome.jsp";
			
		} else if (action.equals("makeBooking")) {
			
			if ((Object) request.getSession().getAttribute("madeBooking") == null) {
				
			
			boolean email_Error = false;
			System.out.println(request.getParameter("email"));
			
			 if((!(request.getParameter("existingCustomer") != "")) && !request.getParameter("email").matches("^[A-Za-z0-9]+@[A-Za-z0-9]+\\.com$"))
	         {
				 System.out.println("test456");
	                email_Error = true;
	                request.setAttribute("email_Error", 1);
    
	         }
			 System.out.println("test789");
			if (!email_Error) {
			
			int nextId = Integer.parseInt(request.getParameter("nextId"));
			Date start = null;
			Date end = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			try {
				sdf.parse(request.getParameter("start"));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				start = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("start"));
				end = new SimpleDateFormat("yyyy-MM-dd").parse(request.getParameter("end"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int roomId = Integer.parseInt(request.getParameter("roomId"));
			String type = request.getParameter("type");
			float totalPrice =  Float.parseFloat(request.getParameter("totalPrice"));
			
			String city = request.getParameter("city"); //find associated city
			
			
			int numRooms = Integer.parseInt(request.getParameter("numrooms"));
			System.out.println("--addBed3--" + request.getParameter("addBed"));
			boolean addBed = (!request.getParameter("addBed").equals("0"));
			
			
			
			int cityId = cast.getIdCity(city);
			
			
			int nextCustomerId = cast.getLastIdCustomer() + 1;
			System.out.println("--custId--" + nextCustomerId);
			String email = request.getParameter("email");
			Random rand = new Random();
			
			int pin = rand.nextInt(8999) + 1000; //range of 1000-9999
			while (cast.existsPin(pin)) {
				pin = rand.nextInt(8999) + 1000;
			}
			
			String url = UUID.randomUUID().toString();
			
			// Email Code ---------------------------------- //
			MailSender sender = null;
			try{
				sender = MailSender.getMailSender();
				//Replace as necessary
				String fromAddress = "comp9321@cse.unsw.edu.au";
				String toAddress = email;
				String subject = "COMP9321 Booking";
				StringBuffer mailBody = new StringBuffer();
				mailBody.append(
						"Starting:" +start+"\n"+
						"Ending: "+end+"\n"+ 
						"City: "+city+"\n"+ 
						"Type: "+type+"\n"+
						"No. Rooms Booked: "+numRooms+"\n"+ 
						"Extra Bed: "+addBed+"\n"+ 
						"Total Price: "+totalPrice+"\n"+ 
						"URL: "+request.getRequestURL()+"?confirmId="+url+"\n"+ 
						"PIN: "+pin+"\n"
						);
				sender.sendMessage(fromAddress, toAddress, subject, mailBody);
	 		}catch(Exception e){
				e.printStackTrace();
			}
			
			// --------------------------------------------- //
			
			if (request.getParameter("existingCustomer") != "") {
				
				//add another booking with same consumerId
				//cast.storebooking
				int existingCustomer = Integer.parseInt(request.getParameter("existingCustomer"));
				
				cast.storeBooking(new Booking(nextId, start, end, roomId, addBed, cityId, numRooms, existingCustomer, 0));
				request.setAttribute("otherBooking", true);
				
			} else {
			
				cast.storeConsumer(nextCustomerId, email, pin, url);
			
				System.out.println("--d1--" + nextId);
				System.out.println("--d1--" + start);
				System.out.println("--d1--" + end);
				System.out.println("--d1--" + roomId);
				System.out.println("--d1--" + addBed);
				System.out.println("--d1--" + cityId);
				System.out.println("--d1--" + numRooms);
				System.out.println("--d1--" + nextCustomerId);
			
				cast.storeBooking(new Booking(nextId, start, end, roomId, addBed, cityId, numRooms, nextCustomerId, 0));
			
				String fullUrl = request.getRequestURL() + "?confirmId=" + url;
				request.setAttribute("fullUrl", fullUrl);
				request.setAttribute("pin", pin);
			
			}
			
			request.setAttribute("madeBooking", true);
			
			Object obj = new Object();
			request.getSession().setAttribute("madeBooking", obj);
			
			forwardPage = "welcome.jsp";
			
			}
			
			} else {
			
				forwardPage = "welcome.jsp";
			}
				
		} else if (action.equals("enteredPin")) {
			
			//Validation
			boolean pin_Error = false;
			
			 if(!request.getParameter("pinNum").matches("^[0-9]+$"))
	         {
	                pin_Error = true;
	                request.setAttribute("pin_Error", 1);
	                //System.out.println(request.getRequestURI());
	                //forwardPage = request.getRequestURL() + "?confirmId=" + request.getParameter("confirmId");
	                request.setAttribute("confirmId", request.getParameter("confirmId"));
	                forwardPage = "confirm.jsp";
	         }
			
			if (!pin_Error) {
			
			int pinNum = Integer.parseInt(request.getParameter("pinNum"));
			String url = request.getParameter("confirmId");
			int pin = cast.getPinFromUrl(url);
			int consumerId = cast.getIdFromUrl(url);  //used to get all bookings for a consumer 
			
			request.setAttribute("consumerId", consumerId);
			
			System.out.println(pinNum);
			System.out.println(url);
			System.out.println(pin);
			System.out.println(consumerId);
			
			if (pinNum == pin) {
				//valid password
				
				request.setAttribute("access", true);
				
				//view all bookings
				List<BookedRoom> bookings = cast.viewBookings(consumerId); 
				for (int i = 0; i < bookings.size(); i++) {
					BookedRoom b = bookings.get(i);
					double total = cast.totalPrice(b.isExtraBed(), b.getPrice(), b.getStart(), 
							b.getEnd(), b.getRoomType(), b.getRoomsBooked(), b.getCity());
					
					bookings.get(i).setTotal(total);
					System.out.println("total: $"+total);
				}
				request.setAttribute("bookings", bookings);
				//for (BookedRoom br : bookings) {
				//	System.out.println(br.getId());
				//}
				
			} else {
				
				request.setAttribute("access", false);
			}
			
			
			forwardPage = "bookings.jsp";
			
			}
			
		} else if (action.equals("deleteBookings")) {
			
			int customerId = Integer.parseInt(request.getParameter("existingCustomer"));
			cast.deleteBookings(customerId);
			
			forwardPage = "welcome.jsp";
			
		}
		
		
		
		/*try{
			if((action==null)||(character==null)){
				throw new InvalidActionException();
			}
			if(action.equals("info")){
				request.setAttribute("mcharacter", cast.findChar(character));
				request.setAttribute("character", character);
				forwardPage = "cast.jsp";
			}else if(action.equals("comments")){
				request.setAttribute("comments", cast.getComments(character));
				request.setAttribute("character", character);
				forwardPage ="comments.jsp";
			}else if(action.equals("postcomment"))
				forwardPage = handlePostcomment(request,response);
			else forwardPage = "error.jsp";
		}catch(EmptyResultException e){
			String message = "Character was not found. Please go back and try again.";
			request.setAttribute("message", message);
			forwardPage = "error.jsp";
		} catch (InvalidActionException e) {
			String message = "Action or character was not selected. Please go back and try again.";
			request.setAttribute("message", message);
			forwardPage = "error.jsp";
		}
		*/
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/"+forwardPage);
		dispatcher.forward(request, response);
	}
	
	private String handlePostcomment(HttpServletRequest request, HttpServletResponse response){
		String forwardPage = "";
		String character = (String) request.getParameter("character");
		logger.info("Comment on character: "+character);
		try{
			CharacterDTO mchar = cast.findChar(character);
			String commentString = request.getParameter("comments");
			CommentDTO comment = new CommentDTO(mchar.getId(), mchar.getName(), "SKV", new Date(), commentString);
			cast.storeComment(comment);
			request.setAttribute("comments", cast.getComments(character));
			forwardPage = "success.jsp";
		}catch(Exception e){
			e.printStackTrace();
			forwardPage = "error.jsp";
		}
		return forwardPage;
	}

}
