package edu.unsw.comp9321.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;



public class DerbyDBDAO {
    
    private InitialContext ic;
    private DataSource ds;
    private Connection myConnection;
    
    public DerbyDBDAO () 
    {
    
        try {
            // Get initial context to search through name/object bindings.
            this.ic = new InitialContext();
        
            // Get DataSource object via a cast as the result of object lookup.
            // Serves as a connection factory (makes connections) for the data source it represents.
            this.ds = (DataSource) this.ic.lookup("java:comp/env/jdbc/defaultDB");
            
            // Connect to database.
            this.myConnection = this.ds.getConnection();
            
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
    public Connection getDBConnection()
    {
        return this.myConnection;
    }
    
    public void getFirst()
    {
        
        try {
            PreparedStatement ps;
            
            ps = this.myConnection.prepareStatement("SELECT * FROM RoomType");
            
            ResultSet rset = ps.executeQuery();
            
            while(rset.next())
            {
                String type = rset.getString("name");
                System.out.println(type);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
}
