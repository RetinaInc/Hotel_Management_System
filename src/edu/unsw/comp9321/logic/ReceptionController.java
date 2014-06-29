package edu.unsw.comp9321.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.unsw.comp9321.jdbc.DerbyDBDAO;

/**
 * Servlet implementation class myController
 */
@WebServlet("/ReceptionController")
public class ReceptionController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    // Map logic to request action.
        String action = request.getParameter("action");
        
        // Set default page.
        String nextPage = "receptionLogin.jsp";
        
        if(action.equals("checkinRequest"))
        {
            Connection myConn = (new DerbyDBDAO()).getDBConnection();
            
            if(request.getParameter("b_rt_c_id") != null)
            {
                String[] values = request.getParameter("b_rt_c_id").split("-");
                int bookingId = Integer.parseInt(values[0]);
                int roomTypeId = Integer.parseInt(values[1]);
                int cityId = Integer.parseInt(values[2]);
                String cityName = request.getParameter("city");
                
                Vector<Vector<String>> availRoomResults = getAvailRooms(myConn, roomTypeId, cityId);
                
                request.setAttribute("city", cityName);
                request.setAttribute("bookingId", bookingId);
                request.setAttribute("roomResults", availRoomResults);
                
                nextPage = "/WEB-INF/restrictedReception/receptionChooseRoom.jsp";
            }
            else
            {
                request.setAttribute("noSelection", 1);
            }
        }
	    
        // Dispatch Control.
        RequestDispatcher myRequestDispatcher = request.getRequestDispatcher("/"+nextPage);
        myRequestDispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	    // Map logic to request action.
        String action = request.getParameter("action");
        
        // Set default page.
        String nextPage = "receptionLogin.jsp";
        
        if(action.equals("receptionLoginRequest"))
        {
            // Get parameters.
            String pwd = request.getParameter("pwd");
            String city = request.getParameter("city");
            
            // Check that the password is correct.
            if(pwd.equals("12345"))
            {
                Connection myConn = (new DerbyDBDAO()).getDBConnection(); 
                
                Vector<Vector<String>>roomResults = this.getOccupiedRooms(myConn, city);
                Vector<Vector<String>>cityBookings = this.getCityBookings(myConn, city);
                
                request.setAttribute("city", city);
                request.setAttribute("results1", roomResults);
                request.setAttribute("results2", cityBookings);
                nextPage = "/WEB-INF/restrictedReception/receptionMain.jsp";
                    
            }
            else
            {
                // Set error variable to be sent back to user.
                request.setAttribute("loginFailed", 1);
                
                nextPage = "receptionLogin.jsp";
            }
        }
        else if(action.equals("allocateRequest"))
        {
            Connection myConn = (new DerbyDBDAO()).getDBConnection(); 
            
            int bookingId = Integer.parseInt(request.getParameter("bookingID"));
            String[] roomSet = request.getParameterValues("roomId");
            String cityName = request.getParameter("city");
            
            Vector<String> bookingDetails = getBookingDetails(myConn, bookingId);
            int numRooms = Integer.parseInt(bookingDetails.get(0));
            
            if(roomSet != null && (roomSet.length == numRooms))
            {
                for(int j=0;j<numRooms;j++)
                {
                    int roomId = Integer.parseInt(roomSet[j]);
                    setRoomToBooking(myConn, bookingId, roomId);
                }
                
                // Send user back to main page.
                Vector<Vector<String>>roomResults = this.getOccupiedRooms(myConn, cityName);
                Vector<Vector<String>>cityBookings = this.getCityBookings(myConn, cityName);
                 
                request.setAttribute("success", 1);
                request.setAttribute("city", cityName);
                request.setAttribute("results1", roomResults);
                request.setAttribute("results2", cityBookings);
                nextPage = "/WEB-INF/restrictedReception/receptionMain.jsp";
            }
            else
            {
                request.setAttribute("mismatch", 1);
            }
            
        } 
        else if(action.equals("checkoutRequest"))
        {
            Connection myConn = (new DerbyDBDAO()).getDBConnection(); 
            
             String bookingId = request.getParameter("bookingId"); 
             String city = request.getParameter("city");
                    
            if(bookingId != null)
            {
                int bId = Integer.parseInt(bookingId);
                unsetRoomToBooking(myConn, bId);
                
                // Send user back to main page.
                Vector<Vector<String>>roomResults = this.getOccupiedRooms(myConn, city);
                Vector<Vector<String>>cityBookings = this.getCityBookings(myConn, city);
                 
                request.setAttribute("successCheckout", 1);
                request.setAttribute("city", city);
                request.setAttribute("results1", roomResults);
                request.setAttribute("results2", cityBookings);
                
                nextPage = "/WEB-INF/restrictedReception/receptionMain.jsp";
            }
            else
            {
                request.setAttribute("noSelectionCheckout", 1);
            }
        }
        
        // Dispatch Control.
        RequestDispatcher myRequestDispatcher = request.getRequestDispatcher("/"+nextPage);
        myRequestDispatcher.forward(request, response);
	}
	
	private Vector<Vector<String>> getOccupiedRooms(Connection aConn, String aCity)
	{
        try {
            // Create sql statement and pass values in.
            String sqlQuery1 = "SELECT R.room_id, RT.name, B.start_date, B.end_date, CON.email, b.booking_id " +
                    "FROM Room R, RoomType RT, Booking B, Consumer CON, City C "+
                    "WHERE C.name = ? and B.city_id = C.city_id and "+
                            "R.status = ? and R.booking_id = B.booking_id and "+
                            "R.room_type_id = RT.room_type_id and CON.consumer_id = B.consumer_id and "+
                            "R.city_id = B.city_id and B.room_type_id = R.room_type_id";
            
            PreparedStatement ps1 = aConn.prepareStatement(sqlQuery1);
            
            ps1.setString(1, aCity);
            ps1.setString(2, "occupied");
            
            // Execute query and loop through saving results.
            ResultSet rset1 = ps1.executeQuery();
            Vector<Vector<String>> r1 = new Vector<Vector<String>>();
            
            while(rset1.next())
            {
                Vector<String> temp = new Vector<String>();
                
                temp.add(rset1.getString("room_id"));
                temp.add(rset1.getString("name"));
                temp.add(rset1.getString("booking_id"));
                temp.add(rset1.getString("start_date"));
                temp.add(rset1.getString("end_date"));
                temp.add(rset1.getString("email"));
                temp.add(rset1.getString("booking_id"));
                
                r1.add(temp);
            }
            
            return r1;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
	}
	
	private Vector<Vector<String>> getCityBookings(Connection aConn, String aCity)
	{
	    try{
    	    // Create sql statement and pass values in.
            String sqlQuery2 = "SELECT b.booking_id, b.start_date, b.end_date, rt.name, rt.num_beds, " +
                        "b.extra_bed, b.num_rooms, con.email, rt.room_type_id, c.city_id "+
                        "FROM Booking b, RoomType rt, Consumer con, City c "+
                        "WHERE b.city_id = c.city_id and c.name = ? and b.consumer_id = con.consumer_id "+
                        "and rt.room_type_id = b.room_type_id and "+
                        "b.booking_id NOT IN (SELECT booking_id FROM Room WHERE booking_id IS NOT NULL)";
            
            PreparedStatement ps2 = aConn.prepareStatement(sqlQuery2);
            
            ps2.setString(1, aCity);
            
            // Execute query and loop through saving results.
            ResultSet rset2 = ps2.executeQuery();
            Vector<Vector<String>> r2 = new Vector<Vector<String>>();
            
            while(rset2.next())
            {
                Vector<String> temp = new Vector<String>();
    
                temp.add(rset2.getString("booking_id"));
                temp.add(rset2.getString("name"));
                temp.add(rset2.getString("start_date"));
                temp.add(rset2.getString("end_date"));
                temp.add(rset2.getString("num_beds"));
                
                if(rset2.getInt("extra_bed") == 1 )
                {
                    temp.add("Yes");
                }
                else
                {
                    temp.add("No");
                }
                
                temp.add(rset2.getString("num_rooms"));
                temp.add(rset2.getString("email"));
                temp.add(rset2.getString("room_type_id"));
                temp.add(rset2.getString("city_id"));
                
                r2.add(temp);
            }
            
            return r2;
            
	    } catch (SQLException e) {
            e.printStackTrace();
        }
        
	    return null;   
	}
	
	private Vector<Vector<String>> getAvailRooms(Connection aConn, int aRoomTypeID, int aCityId)
    {
        try{
            // Create sql statement and pass values in.
            String sqlQuery = "SELECT r.room_id, rt.name "+
                                "FROM Room r, RoomType rt "+
                                "WHERE r.room_type_id = rt.room_type_id and "+ 
                                        "r.room_type_id = ? and r.city_id = ? and r.status = ?";
            
            PreparedStatement ps = aConn.prepareStatement(sqlQuery);
            
            ps.setInt(1, aRoomTypeID);
            ps.setInt(2, aCityId);
            ps.setString(3, "available");
            
            // Execute query and loop through saving results.
            ResultSet rset = ps.executeQuery();
            Vector<Vector<String>> r = new Vector<Vector<String>>();
            
            while(rset.next())
            {
                Vector<String> temp = new Vector<String>();
    
                temp.add(rset.getString("room_id"));
                temp.add(rset.getString("name"));
                
                r.add(temp);
            }
            
            return r;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;   
    }
	
    private Vector<String> getBookingDetails(Connection aConn, int aBookingId)
    {
        try{
            // Create sql statement and pass values in.
            String sqlQuery = "SELECT num_rooms, city_id "+
                        "FROM Booking "+
                        "WHERE booking_id = ? ";
            
            PreparedStatement ps = aConn.prepareStatement(sqlQuery);
            
            ps.setInt(1, aBookingId);
            
            // Execute query and loop through saving results.
            ResultSet rset = ps.executeQuery();
            rset.next();

            Vector<String> temp = new Vector<String>();
    
            temp.add(rset.getString("num_rooms"));
            temp.add(rset.getString("city_id"));

            
            return temp;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;   
    }
    
    private void setRoomToBooking(Connection aConn, int aBookingId, int aRoomId)
    {
        try{
            // Create sql statement and pass values in.
            String sqlQuery = "UPDATE Room SET booking_id=?, status=? WHERE room_id=?";
            
            PreparedStatement ps = aConn.prepareStatement(sqlQuery);
            
            ps.setInt(1, aBookingId);
            ps.setString(2, "occupied");
            ps.setInt(3, aRoomId);
            
            int x = ps.executeUpdate();
            
            assert(x == 1);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }  
    }
    
    private void unsetRoomToBooking(Connection aConn, int aBookingId)
    {
        try{
            // Create sql statement and pass values in.
            String sqlQuery = "UPDATE Room SET booking_id=NULL, status=? WHERE booking_id=?";
            
            PreparedStatement ps = aConn.prepareStatement(sqlQuery);
            
            ps.setString(1, "available");
            ps.setInt(2, aBookingId);
            
            int x = ps.executeUpdate();
            
            assert(x >= 1);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }  
    }
}
