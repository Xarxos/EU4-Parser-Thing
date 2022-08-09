import java.sql.*;

public class SQLConnection {
    static final String DB_URL = "jdbc:mysql://localhost:3306/eu4data";
    static final String USER = "root";
    static final String PASS = "tY4mM0Ll2991";
    static final String QUERY = "SELECT tag, name, GovForm, GovReform, GovRank FROM nations";

    public static void main(String[] args) {
        // Open a connection
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(QUERY);) {
            // Extract data from result set
            while (rs.next()) {
                // Retrieve by column name
                System.out.print("Tag: " + rs.getString("tag"));
                System.out.print(", Name: " + rs.getString("name"));
                System.out.print(", GovForm: " + rs.getString("GovForm"));
                System.out.print(", GovReform: " + rs.getString("GovReform"));
                System.out.println(", GovRank: " + rs.getInt("GovRank"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}