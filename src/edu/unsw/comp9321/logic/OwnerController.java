package edu.unsw.comp9321.logic;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import edu.unsw.comp9321.jdbc.DerbyDBDAO;

/**
 * Servlet implementation class OwnerController
 */
@WebServlet("/OwnerController")
public class OwnerController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	    // Map logic to request action.
        String action = request.getParameter("action");
        
        // Set default page.
        String nextPage = "ownerLogin.jsp";
        
        if(action.equals("ownerLoginRequest"))
        {
            // Get parameters.
            String pwd = request.getParameter("pwd");
            
            // Check that the password is correct.
            if(pwd.equals("12345"))
            {
                Connection myConn = (new DerbyDBDAO()).getDBConnection(); 
                
                Vector<Vector<Vector<String>>> results = this.getOccupancy(myConn);
                Vector<Vector<String>>  discountResults = this.getDiscounts(myConn);
                
                request.setAttribute("discountResults", discountResults);
                request.setAttribute("city1", results.get(0));
                request.setAttribute("city2", results.get(1));
                request.setAttribute("city3", results.get(2));
                request.setAttribute("city4", results.get(3));
                request.setAttribute("city5", results.get(4));
                
                nextPage = "/WEB-INF/restrictedOwner/ownerMain.jsp";
            }
            else
            {
                // Set error variable to be sent back to user.
                request.setAttribute("loginFailed", 1);
            }   
        }
        else if(action.equals("addDiscountRequest"))
        {
            // Validate parametres.
            boolean roomType_Flag = false;
            boolean city_Flag = false;
            boolean starting_Flag = false;
            boolean ending_Flag = false;
            boolean rate_Flag = false;
            
            // Set default page.
            nextPage = "/WEB-INF/restrictedOwner/addDiscount.jsp";
            
            if(!request.getParameter("roomType").matches("^[1-5]{1}$"))
            {
                roomType_Flag = true;
                request.setAttribute("roomType_Error", 1);
            }
            
            if(!request.getParameter("city").matches("^[1-5]{1}$"))
            {
                city_Flag = true;
                request.setAttribute("city_Error", 1);
            }
            
            if(!request.getParameter("starting").matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))
            {
                roomType_Flag = true;
                request.setAttribute("starting_Error", 1);
            }
            
            if(!request.getParameter("ending").matches("^[0-9]{4}-[0-9]{2}-[0-9]{2}$"))
            {
                roomType_Flag = true;
                request.setAttribute("ending_Error", 1);
            }
            
            if(!request.getParameter("rate").matches("^[0-9]{1,3}$|^[0-9]{1,2}.[0-9]{1,2}$"))
            {
                roomType_Flag = true;
                request.setAttribute("rate_Error", 1);
            }
            
            Object flag = (Object) request.getSession().getAttribute("flag");
            
            if(!roomType_Flag && !city_Flag && !starting_Flag && !ending_Flag && !rate_Flag)
            {   
                Connection myConn = (new DerbyDBDAO()).getDBConnection();
                
                if(flag == null)
                {
                
                    int roomTypeInt = Integer.parseInt(request.getParameter("roomType"));
                    int cityInt = Integer.parseInt(request.getParameter("city"));
                    String starting = request.getParameter("starting");
                    String ending = request.getParameter("ending");
                    float rate = Float.parseFloat(request.getParameter("rate"))/100;
                    
                    setNewDiscount(myConn, roomTypeInt, cityInt, starting, ending, rate);
                    
                    request.setAttribute("discountAdded", 1);
                    
                    Object newFlag = new Object();
                    request.getSession().setAttribute("flag", newFlag);
                }
                    
                    Vector<Vector<Vector<String>>> results = this.getOccupancy(myConn);
                    Vector<Vector<String>>  discountResults = getDiscounts(myConn);
                    
                    request.setAttribute("discountResults", discountResults);
                    request.setAttribute("city1", results.get(0));
                    request.setAttribute("city2", results.get(1));
                    request.setAttribute("city3", results.get(2));
                    request.setAttribute("city4", results.get(3));
                    request.setAttribute("city5", results.get(4));
                    
                
                nextPage = "/WEB-INF/restrictedOwner/ownerMain.jsp";
            }
          
        }
        else if(action.equals("discountNavigate"))
        {
            request.getSession().invalidate();
            
            nextPage = "/WEB-INF/restrictedOwner/addDiscount.jsp";
        }
	    
        
        // Dispatch Control.
        RequestDispatcher myRequestDispatcher = request.getRequestDispatcher("/"+nextPage);
        myRequestDispatcher.forward(request, response);
	}
	
	private Vector<Vector<Vector<String>>> getOccupancy(Connection aConn)
    {
        try{
            // Create sql statement and pass values in.
            String sqlQuery2 = "SELECT rt.name, count(rt.name) as numCount, c.name as cityName, r.city_id, r.room_type_id "+
                                "FROM Room r, RoomType rt, City c "+ 
                                "WHERE c.city_id = r.city_id and r.room_type_id=rt.room_type_id "+
                                "GROUP BY c.name, rt.name, r.city_id, r.room_type_id";
            
            PreparedStatement ps2 = aConn.prepareStatement(sqlQuery2);
            
            // Execute query and loop through saving results.
            ResultSet rset2 = ps2.executeQuery();
            Vector<Vector<Vector<String>>> overall = new Vector<Vector<Vector<String>>>();
            
            for(int i=0;i<5;i++)
            {
                Vector<Vector<String>> perCity = new Vector<Vector<String>>();
                
                for(int j=0;j<5;j++)
                {
                    rset2.next();
                    
                    Vector<String> temp = new Vector<String>();
        
                    temp.add(rset2.getString("name"));
                    
                    // Inner query.
                    String sqlQuery3 = "SELECT count(rt.name) as freeCount "+
					"FROM Room r, RoomType rt, City c "+
					"WHERE c.city_id = r.city_id and r.status='available' and r.room_type_id=rt.room_type_id "+
						"and c.city_id=? and r.room_type_id=?";
        
                    PreparedStatement ps3 = aConn.prepareStatement(sqlQuery3);
                    ps3.setInt(1, Integer.parseInt(rset2.getString("city_id")));
                    ps3.setInt(2, Integer.parseInt(rset2.getString("room_type_id")));
                    
                    ResultSet rset3 = ps3.executeQuery();
                    
                    rset3.next();
                    temp.add(rset3.getString("freeCount"));
                    temp.add(""+(5-(Integer.parseInt(rset3.getString("freeCount")))));
                    temp.add(rset2.getString("cityName"));
                    
                    
                    
                    perCity.add(temp);
                }
                
                overall.add(perCity);
            }
            
            return overall;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;   
    }
	
	private Vector<Vector<String>> getDiscounts(Connection aConn)
	{
	        try{
	            // Create sql statement and pass values in.
	            String sqlQuery2 = "SELECT d.discount_id, rt.name AS roomTypeName, d.start_date, d.end_date, "+
	                                        "c.name AS cityName, d.reduced_rate "+
                                            "FROM discount d, RoomType rt, City c "+
                                            "WHERE d.city_id = c.city_id and d.room_type_id = rt.room_type_id "+
                                            "ORDER BY d.discount_id DESC";
	            
	            PreparedStatement ps2 = aConn.prepareStatement(sqlQuery2);
	            
	            // Execute query and loop through saving results.
	            ResultSet rset2 = ps2.executeQuery();
	            Vector<Vector<String>> overall = new Vector<Vector<String>>();
	            
	            while(rset2.next())
                { 
                    Vector<String> temp = new Vector<String>();
        
                    temp.add(rset2.getString("discount_id"));
                    temp.add(rset2.getString("roomTypeName"));
                    temp.add(rset2.getString("start_date"));
                    temp.add(rset2.getString("end_date"));
                    temp.add(rset2.getString("cityName"));
                    
                    double dec = Double.parseDouble(rset2.getString("reduced_rate"))*100;
                    
                    DecimalFormat newFormat = new DecimalFormat("#.##");
                    double twoDecimal =  Double.valueOf(newFormat.format(dec));
                    
                    temp.add(""+twoDecimal);
    
                    overall.add(temp);
                }
            
                return overall;
            
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        
	        return null;   
	    }
	
	   private void setNewDiscount(Connection aConn, int aRoomTypeInt, int aCityInt, String aStarting, String aEnding, float aRate)
	    {
	            try{
	                // Create sql statement and pass values in.
	                String sqlQuery2 = "INSERT INTO discount (room_type_id, start_date, end_date, city_id, reduced_rate) "+ 
	                                    "VALUES (?, ?, ?, ?, ?)";
	                
	                PreparedStatement ps2 = aConn.prepareStatement(sqlQuery2);
	                
	                ps2.setInt(1, aRoomTypeInt);
	                ps2.setString(2, aStarting);
	                ps2.setString(3, aEnding);
	                ps2.setInt(4, aCityInt);
	                ps2.setFloat(5, aRate);
	                
	                // Execute query and loop through saving results.
	                int result = ps2.executeUpdate();
	                
	                assert(result == 1);
	        
	            } catch (SQLException e) {
                e.printStackTrace();
	            }
	    }
}
