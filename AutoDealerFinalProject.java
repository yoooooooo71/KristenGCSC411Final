/*
 * CSC 411 DBMS Design
 * Course Project
 * Automobile Company
 * 
 * Author: Kristen Gilmer
 * Date Due: 12/10/2024
 *
 */
package autodealer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.PreparedStatement;
import java.util.InputMismatchException;

// program
public class AutoDealer {

    //database access log in information
    static final String DB_URL = "jdbc:mysql://localhost:3306/dbmsfinal?zeroDateTimeBehavior=CONVERT_TO_NULL";
    static final String USER = "root";
    static final String PSWD = "D!v3rg3nt2023`";

    // exception catch for access denied
    public static class AccessDeniedException extends Exception {

        public AccessDeniedException(String message) {
            super(message);
        }
    }
    
    // Method to check access control from database 
    public static boolean checkAccess(Connection conn, String role, int queryType) throws SQLException, AccessDeniedException {
        if (queryType == 3 || queryType == 5) {
            throw new AccessDeniedException("Access  not granted to this query.");
        }
        String query = "SELECT role_permissions FROM roles "
                + "WHERE roleName = ? AND role_permissions = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, role);
            pstmt.setInt(2, queryType);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    throw new AccessDeniedException("Access denied.");
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        Connection conn;// = null;
        Statement stmt;// = null;
        PreparedStatement pstmt = null; // set to null
        try (Scanner scanner = new Scanner(System.in)) {
            // Correct usage of Class.forName() to load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            //connecting to database
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PSWD);

            //successful connection
            System.out.println("Connected to database successfully!");

            stmt = conn.createStatement();
            ResultSet rs = null;

            // creating the log in loop
            while (true) {

                System.out.println("""
                                   Choose an option:
                                        1. Log In
                                        2. Exit """);
                int login = scanner.nextInt();

                // creating option 1 log in statement
                if (login == 1) {
                    

                    //display  log in input prompt
                    System.out.println("Please enter user information: ");
                    
                    // username prompt
                    System.out.printf("Username: ");
                    String userName = scanner.nextLine();
                    userName = scanner.nextLine();
                    //Note: I know the above two lines should not work, but
                    //  this was required to make this line work appropriately
                    //  and likely won't work on anyone else's computer.
                    

                    // password prompt
                    System.out.printf("Password: ");
                    String password = scanner.nextLine();

                    
                    // query to confirm user log in credentials are in the database
                    String query = "SELECT roles.roleName "
                            + "FROM users "
                            + "JOIN roles ON roles.roleID = users.roleID "
                            + "WHERE users.userName = ? "
                            + "AND users.password = ?";
                    pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, userName.trim());
                    pstmt.setString(2, password.trim());
                    rs = pstmt.executeQuery();

                    
                    // how to handle the decision of the credential existance
                    if (rs.next()) {
                        String role = rs.getString("roleName");
                        System.out.println("User role: " + role);

                        // if username is manager
                        switch (role) {
                            case "manager_role" -> {

                                //setting up the loop for user access
                                boolean continueLoop = true;

                                
                                while (continueLoop) {
                                    
                                    // Ask user for input on what query they want to run
                                    System.out.println("""
                                   Enter the query type
                                      1: Body Style Popularity
                                      2: Purchase Trends
                                      3: Customers 
                                      4: Dealer Information
                                      5: Suppliers
                                      6: Recalls
                                      0: Log Out""");
                                    int queryType = scanner.nextInt();
                                    scanner.nextLine(); // consume the newline

                                    // if query is 0, log the user out
                                    if (queryType == 0) {
                                        System.out.println();
                                        continueLoop = false;
                                        
                                    // if query is valid, proceed here:
                                    } else if (queryType > 0 && queryType < 7) {

                                        //switch case for query type 1 for trends per body style type
                                        switch (queryType) {
                                            
                                            //case 1 for body style popularity
                                            case 1 -> {
                                                System.out.println("""
                                               Select body style type:  
                                                  1: Convertible  
                                                  2: Coupe 
                                                  3: Pickup 
                                                  4: Sedan  
                                                  5: SUV""");
                                                int caseOne = scanner.nextInt();

                                                //switch case for body styles
                                                switch (caseOne) {
                                                    
                                                    //convertible body style count
                                                    case 1 -> {
                                                        String sqlcase1 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as ConvertibleCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Convertible') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase1);
                                                        System.out.println("Convertible Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("ConvertibleCount");
                                                            System.out.println("     Month: " + month + " - Convertibles Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    
                                                    case 2 -> {
                                                        // coupe body style count
                                                        String sqlcase2 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as CoupeCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Coupe') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase2);
                                                        System.out.println("Coupe Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("CoupeCount");
                                                            System.out.println("     Month: " + month + " - Coupes Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    
                                                    case 3 -> {
                                                        // pickup body style count
                                                        String sqlcase3 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as PickupCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Pickup') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase3);
                                                        System.out.println("Pickup Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("PickupCount");
                                                            System.out.println("     Month: " + month + " - Pickups Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    
                                                    
                                                    case 4 -> {
                                                        //sedan body style count
                                                        String sqlcase4 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as SedanCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Sedan') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase4);
                                                        System.out.println("Sedan Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("SedanCount");
                                                            System.out.println("     Month: " + month + " - Sedans Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    case 5 -> {
                                                        //SUV body style count
                                                        String sqlcase5 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as SUVCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'SUV') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase5);
                                                        System.out.println("SUV Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("SUVCount");
                                                            System.out.println("     Month: " + month + " - SUVs Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    default ->
                                                        System.out.println("Invalid query type selected.");
                                                }

                                            }//end of case 1

                                            //case 2 for sales duration
                                            case 2 -> {
                                                //prompt for which duration to calculate
                                                System.out.println("""
                                               Trend Options:
                                                  1: 3 years
                                                  2: 1 year
                                                  3: 1 week
                                                  4: 1 week
                                                  5: All Sold
                                                  6: Top Brands by Dollar
                                                  7: Top Brands by Unit""");
                                                int caseTwo = scanner.nextInt();
                                                //new switch case 
                                                switch (caseTwo) {

                                                    //sale trends within 3 years
                                                    case 1 -> {
                                                        String sql21 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 3 YEAR) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql21);
                                                        System.out.println("Purchase trend for the past 3 years:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    //sale trends within 1 year
                                                    case 2 -> {
                                                        String sql22 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql22);
                                                        System.out.println("Purchase trend for the past year:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //sale trend within one month
                                                    case 3 -> {
                                                        String sql23 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql23);
                                                        System.out.println("Purchase trend for the past 1 month:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //sale trend within past week
                                                    case 4 -> {
                                                        String sql24 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql24);
                                                        System.out.println("Purchase trend for the past week:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //all sold vehicles
                                                    case 5 -> {
                                                        String sql25 = "SELECT vehicle.VIN, brand.brandName, model.modelName, vehicle.list_price "
                                                                + "FROM vehicle "
                                                                + "JOIN model ON vehicle.modelID = model.modelID "
                                                                + "JOIN brand ON model.brandID = brand.brandID "
                                                                + "WHERE vehicle.soldDate IS NOT NULL "
                                                                + "ORDER BY brand.brandName";
                                                        rs = stmt.executeQuery(sql25);
                                                        System.out.println("All vehicles sold: ");
                                                        while (rs.next()) {
                                                            String vin = rs.getString("VIN");
                                                            String brandName = rs.getString("brandName");
                                                            String modelName = rs.getString("modelName");
                                                            String listPrice = rs.getString("list_price");
                                                            System.out.println(brandName + " " + modelName + " " + vin + " $" + listPrice);
                                                        }
                                                        System.out.println();
                                                    }

                                                    // top brands by dollars
                                                    case 6 -> {
                                                        String sql26 = "SELECT brand.brandName, SUM(vehicle.list_price) as sold "
                                                                + "FROM brand "
                                                                + "JOIN model on model.brandID = brand.brandID "
                                                                + "JOIN vehicle on vehicle.modelID = model.modelID "
                                                                + "JOIN dealer on vehicle.dealerID = dealer.dealerID "
                                                                + "GROUP BY brand.brandName "
                                                                + "ORDER BY sold DESC "
                                                                + "LIMIT 2";
                                                        rs = stmt.executeQuery(sql26);
                                                        System.out.println("Top 2 Brands by Dollar Amount: ");
                                                        while (rs.next()) {
                                                            String name = rs.getString("brandName");
                                                            double sales = rs.getDouble("sold");
                                                            System.out.println("    $" + sales + " " + name);
                                                        }
                                                        System.out.println();
                                                    }

                                                    // top brands by unit count
                                                    case 7 -> {
                                                        String sql27 = "SELECT brand.brandName, COUNT(vehicle.soldDate) as sold "
                                                                + "FROM brand "
                                                                + "JOIN model on model.brandID = brand.brandID "
                                                                + "JOIN vehicle on vehicle.modelID = model.modelID "
                                                                + "JOIN dealer on vehicle.dealerID = dealer.dealerID "
                                                                + "GROUP BY brand.brandName "
                                                                + "ORDER BY sold DESC "
                                                                + "LIMIT 2";
                                                        rs = stmt.executeQuery(sql27);
                                                        System.out.println("Top 2 Brands by Unit Sales: ");
                                                        while (rs.next()) {
                                                            String name = rs.getString("brandName");
                                                            int sales = rs.getInt("sold");
                                                            System.out.println("    " + sales + " " + name);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //default to catch input not accounted for
                                                    default ->
                                                        System.out.println("Invalid query type selected.");
                                                }
                                            } //end case 2

                                            case 3 -> {
                                                // select all, by gender, or by annual income
                                                System.out.println("""
                                               Pick a view: 
                                                  1: All Customers
                                                  2: Gender
                                                  3: Annual income""");
                                                int caseThree = scanner.nextInt();

                                                //options for customer cases
                                                switch (caseThree) {
                                                    case 1 -> {
                                                        String sql31 = "SELECT customerID, custName, gender, annualIncome FROM customer ORDER BY customerID";
                                                        rs = stmt.executeQuery(sql31);
                                                        System.out.println("All customers:");
                                                        while (rs.next()) {
                                                            String custID = rs.getString("customerID");
                                                            String gen = rs.getString("gender");
                                                            String income = rs.getString("annualIncome");
                                                            String name = rs.getString("custName");
                                                            System.out.println(custID + " " + name + " " + gen + " " + income);
                                                        }
                                                        System.out.println();
                                                    }
                                                    // show gender
                                                    case 2 -> {
                                                        System.out.println("""
                                                       Show male or female?
                                                           1: Male
                                                           2: Female""");
                                                        int caseThreeTwo = scanner.nextInt();

                                                        switch (caseThreeTwo) {

                                                            //show males
                                                            case 1 -> {
                                                                String sql311 = "SELECT customer.custName, brand.brandName, model.modelName, model.bodyStyle, vehicle.soldDate "
                                                                        + "FROM customer "
                                                                        + "JOIN vehicle on vehicle.customerID = customer.customerID "
                                                                        + "JOIN model ON model.modelID = vehicle.modelID "
                                                                        + "JOIN brand ON model.brandID = brand.brandID "
                                                                        + "WHERE customer.gender = 'M' AND vehicle.soldDate IS NOT NULL "
                                                                        + "ORDER BY custName ASC";
                                                                rs = stmt.executeQuery(sql311);
                                                                System.out.println("All Male Customers - Vehicle purchased - Purchase date");
                                                                while (rs.next()) {
                                                                    String cname = rs.getString("custName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bname = rs.getString("brandName");
                                                                    String style = rs.getString("bodyStyle");
                                                                    String date = rs.getString("soldDate");
                                                                    System.out.println("    " + cname + " - " + bname + " " + mname + " " + style + " - " + date);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show females and the vehicles they purchased
                                                            case 2 -> {
                                                                String sql312;
                                                                sql312 = "SELECT customer.custName, brand.brandName, model.modelName, model.bodyStyle, vehicle.soldDate "
                                                                        + "FROM customer "
                                                                        + "JOIN vehicle on vehicle.customerID = customer.customerID "
                                                                        + "JOIN model ON model.modelID = vehicle.modelID "
                                                                        + "JOIN brand ON model.brandID = brand.brandID "
                                                                        + "WHERE customer.gender = 'F' AND vehicle.soldDate IS NOT NULL "
                                                                        + "ORDER BY custName ASC";
                                                                rs = stmt.executeQuery(sql312);
                                                                System.out.println("All Female Customers");
                                                                while (rs.next()) {
                                                                    String fem = rs.getString("custName");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String style = rs.getString("bodyStyle");
                                                                    String date = rs.getString("soldDate");
                                                                    System.out.println("   " + fem + " - " + bname + " " + mname + " " + style + " - " + date);
                                                                }
                                                                System.out.println();
                                                            }

                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        }
                                                    }
                                                    //show income ranges
                                                    case 3 -> {
                                                        System.out.println("""
                                                Select income range:
                                                    1. Less than $60k
                                                    2. Between $60k and $80k
                                                    3. Between $80k and $100k
                                                    4. Between $100k and $150k
                                                    5. Between $150k and $200k
                                                    6. Over $200k""");
                                                        int caseThreeThree = scanner.nextInt();

                                                        switch (caseThreeThree) {
                                                            //show under 60k
                                                            case 1 -> {
                                                                String sql331 = "SELECT custName, annualIncome FROM customer WHERE annualIncome < 60000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql331);
                                                                System.out.println("Income under $60k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 60-80k
                                                            case 2 -> {
                                                                String sql332 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 60000 AND annualIncome <80000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql332);
                                                                System.out.println("Income between $80k and $100k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 80-100k
                                                            case 3 -> {
                                                                String sql333 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 80000 AND annualIncome <100000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql333);
                                                                System.out.println("Income between $80k and $100k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 100k-150k
                                                            case 4 -> {
                                                                String sql334 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 100000 AND annualIncome <150000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql334);
                                                                System.out.println("Income between $100k and $150k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 150-200k
                                                            case 5 -> {
                                                                String sql335 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 150000 AND annualIncome <200000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql335);
                                                                System.out.println("Income between $150k and $200k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show over 200k
                                                            case 6 -> {
                                                                String sql336 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 200000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql336);
                                                                System.out.println("Income over $200k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }
                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        }

                                                    }
                                                    default ->
                                                        System.out.println("Invalid query type selected.");

                                                }
                                            }
                                            case 4 -> {
                                                // Dealer options 
                                                System.out.println("""
                                Show Dealer Options:
                                    1. All dealers
                                    2. Sales
                                    3. Available Vehicles
                                    4. Dealer Locations """);
                                                int caseFour = scanner.nextInt();

                                                switch (caseFour) {

                                                    //show all dealer options
                                                    case 1 -> {
                                                        String sql41 = "SELECT dealerName, location FROM dealer";
                                                        rs = stmt.executeQuery(sql41);
                                                        System.out.println("All dealers:");
                                                        while (rs.next()) {
                                                            String name = rs.getString("dealerName");
                                                            String loc = rs.getString("location");
                                                            System.out.println("   " + name + " " + loc);
                                                        }
                                                        System.out.println();
                                                    }

                                                    // list of dealer sales
                                                    case 2 -> {

                                                        String sql421 = "SELECT dealer.dealerName, COUNT(vehicle.soldDate) as sold "
                                                                + "FROM dealer "
                                                                + "JOIN vehicle on vehicle.dealerID=dealer.dealerID "
                                                                + "WHERE vehicle.soldDate IS NOT NULL "
                                                                + "GROUP BY dealer.dealerName "
                                                                + "ORDER BY sold DESC";
                                                        rs = stmt.executeQuery(sql421);
                                                        System.out.println("Dealer Sales Count:");
                                                        while (rs.next()) {
                                                            String name = rs.getString("dealerName");
                                                            int sales = rs.getInt("sold");
                                                            System.out.println("    " + name + " " + sales);

                                                        }
                                                    }

                                                    //dealer available vehicles
                                                    case 3 -> {
                                                        String sql443 = "SELECT dealer.dealerName, vehicle.VIN, brand.brandName, model.bodyStyle, options.color, options.engine, options.transmission, tire.tire_brand_name, tire.tire_style_name "
                                                                + "FROM dealer "
                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                + "JOIN options on options.option_id = model.option_id "
                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                + "WHERE vehicle.soldDate IS NULL "
                                                                + "ORDER BY dealerName";
                                                        rs = stmt.executeQuery(sql443);
                                                        System.out.println("Vehicles available at each dealer: ");
                                                        while (rs.next()) {
                                                            String name = rs.getString("dealerName");
                                                            String vin = rs.getString("VIN");
                                                            String brand = rs.getString("brandName");
                                                            String body = rs.getString("bodyStyle");
                                                            String col = rs.getString("color");
                                                            String eng = rs.getString("engine");
                                                            String trans = rs.getString("transmission");
                                                            String tireB = rs.getString("tire_brand_name");
                                                            String tireS = rs.getString("tire_style_name");
                                                            System.out.println("    " + name + " " + vin + " " + brand + " " + body + " " + col);
                                                            System.out.println("            " + eng + " " + trans + " " + tireB + " " + tireS);
                                                        }
                                                        System.out.println(" \n");
                                                    }
                                                    //dealer locations
                                                    case 4 -> {
                                                        String sql44 = "SELECT location, dealerName from dealer";
                                                        rs = stmt.executeQuery(sql44);
                                                        System.out.println("Dealer locations: ");
                                                        while (rs.next()) {
                                                            String loc = rs.getString("location");
                                                            String name = rs.getString("dealerName");
                                                            System.out.println("   " + loc + " " + name);
                                                        }
                                                        System.out.println("\n");
                                                    }

                                                    default ->
                                                        System.out.println("Invalid query type selected.");
                                                }

                                            }

                                            //suppliers and manufacturers
                                            case 5 -> {

                                                System.out.println("""
                                               Suppliers and Manufacturers:
                                                   1: Suppliers
                                                   2: Manufacturers
                                                   3: Plants by Type """);
                                                int caseFive = scanner.nextInt();

                                                switch (caseFive) {
                                                    // case 51: all suppliers 

                                                    case 1 -> {
                                                        System.out.println("""
                                                       Selection:
                                                            1: List of Suppliers
                                                            2: List of Vehicles Per Supplier""");
                                                        int caseFiveOne = scanner.nextInt();

                                                        switch (caseFiveOne) {

                                                            //case 511: list of all suppliers
                                                            case 1 -> {
                                                                String sql511 = "SELECT supplierID, supplierName, contactInfo "
                                                                        + "FROM supplier ";
                                                                rs = stmt.executeQuery(sql511);
                                                                while (rs.next()) {
                                                                    String id = rs.getString("supplierID");
                                                                    String name = rs.getString("supplierName");
                                                                    String contact = rs.getString("contactInfo");
                                                                    System.out.println("     " + id + " " + name + " " + contact);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //case 522: list of vehicles per supplier
                                                            case 2 -> {
                                                                System.out.println("""
                                                               Choose a Supplier:
                                                                    1: EngineWorks
                                                                    2: TransMax
                                                                    3: PaintPros
                                                                    4: TrimTech
                                                                    5: WheelDeal """);
                                                                int caseFiveTwoTwo = scanner.nextInt();

                                                                //switch
                                                                switch (caseFiveTwoTwo) {

                                                                    //case 1: EngineWorks
                                                                    case 1 -> {
                                                                        String sql5221 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '1'";
                                                                        rs = stmt.executeQuery(sql5221);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    }

                                                                    //case 2: TransMax
                                                                    case 2 -> {
                                                                        String sql5222 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '2'";
                                                                        rs = stmt.executeQuery(sql5222);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    } //end case 2

                                                                    //case PaintPros
                                                                    case 3 -> {
                                                                        String sql5223 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '3'";
                                                                        rs = stmt.executeQuery(sql5223);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    } //end case 3

                                                                    //case 4: TrimTech
                                                                    case 4 -> {
                                                                        String sql5224 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '4'";
                                                                        rs = stmt.executeQuery(sql5224);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    } //end case 4

                                                                    //case 5: WheelDeal
                                                                    case 5 -> {
                                                                        String sql5225 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '5'";
                                                                        rs = stmt.executeQuery(sql5225);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }

                                                                    } //end case 5

                                                                    // catch all
                                                                    default ->
                                                                        System.out.println("Invalid query type selected.");
                                                                }
                                                            } //end case

                                                        } //end case 1
                                                    }

                                                    // all manufacturers
                                                    case 2 -> {
                                                        System.out.println("""
                                                       Manufacturer Options:
                                                            1: All Manufacturers
                                                            2: List of Vehicles Associated with Each Manufacturer""");
                                                        int caseFiveTwo = scanner.nextInt();

                                                        switch (caseFiveTwo) {

                                                            //all manufacturers
                                                            case 1 -> {
                                                                String sql521 = "SELECT plantName, location, plantType from manufacturingplant";
                                                                rs = stmt.executeQuery(sql521);
                                                                while (rs.next()) {
                                                                    String plant = rs.getString("plantName");
                                                                    String loc = rs.getString("location");
                                                                    String type = rs.getString("plantType");
                                                                    System.out.println("     " + plant + " " + loc + " " + type);
                                                                }
                                                                System.out.println();

                                                            } //end of case 521 for all manufacturers

                                                            //list of vehicles associated with each
                                                            case 2 -> {
                                                                System.out.println("""
                                                               Select the manufacturer:
                                                                    1: Factory Zero in Detroit, MI
                                                                    2: GM Customer Care in Bolingbrook, IL
                                                                    3: South Gate Assembly in Los Angeles, CA
                                                                    4: West Point GM Part Center in Houston, TX
                                                                    5: Arlington Assembly in Arlington, TX """);
                                                                int caseFiveTwoTwo = scanner.nextInt();

                                                                switch (caseFiveTwoTwo) {

                                                                    //cases for each manufacturer
                                                                    case 1 -> {
                                                                        String sql5221 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '1'";
                                                                        rs = stmt.executeQuery(sql5221);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();

                                                                    } //end case 5221

                                                                    case 2 -> {
                                                                        String sql5222 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '2'";
                                                                        rs = stmt.executeQuery(sql5222);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 5222

                                                                    case 3 -> {
                                                                        String sql5223 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '3'";
                                                                        rs = stmt.executeQuery(sql5223);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 5223

                                                                    case 4 -> {
                                                                        String sql5224 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '4'";
                                                                        rs = stmt.executeQuery(sql5224);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 5224

                                                                    case 5 -> {
                                                                        String sql5225 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '5'";
                                                                        rs = stmt.executeQuery(sql5225);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 5223

                                                                } //end switch 522

                                                            }//end case 522

                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        } //end of switch case 52

                                                    } //end of case five two

                                                    //all plants
                                                    case 3 -> {
                                                        System.out.println("""
                                                       Please make a selection: 
                                                            1: List All Plants 
                                                            2: List Assembly Plants
                                                            3: List Parts Plants
                                                            4: List Vehicles for Associated with Each""");
                                                        int caseFiveThree = scanner.nextInt();

                                                        switch (caseFiveThree) {

                                                            //list all plants
                                                            case 1 -> {
                                                                String sql531 = "SELECT plantID, plantName, location, plantType FROM manufacturingplant ORDER BY plantType";
                                                                rs = stmt.executeQuery(sql531);
                                                                while (rs.next()) {
                                                                    String type = rs.getString("plantType");
                                                                    int id = rs.getInt("plantID");
                                                                    String pname = rs.getString("plantName");
                                                                    String ploc = rs.getString("location");
                                                                    System.out.println("    " + " " + type + " " + id + " " + pname + " " + ploc);
                                                                } //end of while loop
                                                                System.out.println();

                                                            } //end switch case 1

                                                            //list assembly plants
                                                            case 2 -> {
                                                                String sql532 = "SELECT plantID, plantName, location FROM manufacturingplant "
                                                                        + "WHERE plantType = 'Assembly'";
                                                                rs = stmt.executeQuery(sql532);
                                                                while (rs.next()) {
                                                                    int id = rs.getInt("plantID");
                                                                    String pname = rs.getString("plantName");
                                                                    String ploc = rs.getString("location");
                                                                    System.out.println("    " + id + " " + pname + " " + ploc);
                                                                }
                                                                System.out.println();

                                                            } // end switch case 2

                                                            //list all parts plants
                                                            case 3 -> {
                                                                String sql533 = "SELECT plantID, plantName, location from manufacturingplant "
                                                                        + "WHERE plantType = 'Parts'";
                                                                rs = stmt.executeQuery(sql533);
                                                                while (rs.next()) {
                                                                    int id = rs.getInt("plantID");
                                                                    String pname = rs.getString("plantName");
                                                                    String ploc = rs.getString("location");
                                                                    System.out.println("    " + id + " " + pname + " " + ploc);
                                                                }
                                                                System.out.println();

                                                            } // end switch case 3

                                                            //list vehicles by plant
                                                            case 4 -> {

                                                                System.out.println("""
                                                           Please choose your plant under the plant type:
                                                                Assembly: 
                                                                    1. Factory ZERO
                                                                    2. South Gate Assembly
                                                                    3. Arlington Assembly
                                                                Parts
                                                                    4. GM Customer Care
                                                                    5. West Point GM Part Center """);
                                                                int caseFiveThreeFour = scanner.nextInt();

                                                                //new switch
                                                                switch (caseFiveThreeFour) {

                                                                    //Factory ZERO, Detroit, MI, Assembly
                                                                    case 1 -> {
                                                                        String sql5341 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'Factory ZERO'";
                                                                        rs = stmt.executeQuery(sql5341);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    //GM Customer Care, Bolingbrook, IL, Parts
                                                                    case 2 -> {
                                                                        String sql5342 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'South Gate Assembly'";
                                                                        rs = stmt.executeQuery(sql5342);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 2

                                                                    //South Gate Assembly, Los Angeles, CA, Assembly
                                                                    case 3 -> {
                                                                        String sql5343 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'South Gate Assembly'";
                                                                        rs = stmt.executeQuery(sql5343);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 3

                                                                    //West Point GM Part Center, Houston, TX, Parts
                                                                    case 4 -> {
                                                                        String sql5344 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'West Point GM Part Center'";
                                                                        rs = stmt.executeQuery(sql5344);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 4

                                                                    //Arlington Assembly, Arlington, TX, Assembly
                                                                    case 5 -> {
                                                                        String sql5345 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'Arlington Assembly'";
                                                                        rs = stmt.executeQuery(sql5345);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 5

                                                                } //end switch case 534

                                                            } // case 54

                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        } //end switch case 5-53

                                                    } //end case 5-3

                                                    //catch all for number entries with no case defined
                                                    default ->
                                                        System.out.println("Invalid query type selected.");

                                                }//end switch case 5

                                            }//end case 5   

                                            //initial case recalls
                                            case 6 -> {
                                                System.out.println("""
                                               Please select how to find the recalled parts:
                                                    1. By Part
                                                    2. By Location """);
                                                int caseSix = scanner.nextInt();

                                                //main case 6 switch starter
                                                switch (caseSix) {

                                                    //case 1 - by part
                                                    case 1 -> {
                                                        System.out.println("""
                                                       Please select the part:
                                                            1. Paint
                                                            2. Engine
                                                            3. Transmission
                                                            4. Tires """);
                                                        int caseSixOne = scanner.nextInt();

                                                        //start switch case 61
                                                        switch (caseSixOne) {

                                                            //case 1 on poor paint quality
                                                            case 1 -> {
                                                                System.out.println("""
                                                               What is the recall:
                                                                    1. Specific Paint Color
                                                                    2. Specific Factory Fault """);
                                                                int caseSixOneOne = scanner.nextInt();

                                                                //switch sixOneOne for paint colors
                                                                switch (caseSixOneOne) {

                                                                    //list of paint colors
                                                                    case 1 -> {
                                                                        System.out.println("""
                                                                   Choose the specific color: 
                                                                         1. Red
                                                                         2. Blue
                                                                         3. Black
                                                                         4. White
                                                                         5. Green
                                                                         6. Silver
                                                                         7. Yellow
                                                                         8. Grey
                                                                         9. Purple
                                                                        10. Orange
                                                                        11. Brown
                                                                        12. Pink
                                                                        13. Gold
                                                                        14. Beige
                                                                        15. Turquoise 
                                                                       (Note: not all options match vehicles in this representation
                                                                                but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                                will have readable output, but all work. """);
                                                                        int caseSOOOne = scanner.nextInt();

                                                                        //switch set for paint colors
                                                                        switch (caseSOOOne) {

                                                                            case 1 -> {
                                                                                String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Red'";
                                                                                rs = stmt.executeQuery(sql6111);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end red

                                                                            //Blue
                                                                            case 2 -> {
                                                                                String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Blue'";
                                                                                rs = stmt.executeQuery(sql6112);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end blue

                                                                            //black
                                                                            case 3 -> {
                                                                                String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Black'";
                                                                                rs = stmt.executeQuery(sql6113);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end black

                                                                            //White
                                                                            case 4 -> {
                                                                                String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'White'";
                                                                                rs = stmt.executeQuery(sql6115);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end white

                                                                            //green
                                                                            case 5 -> {
                                                                                String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Green'";
                                                                                rs = stmt.executeQuery(sql6115);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end green

                                                                            //silver
                                                                            case 6 -> {
                                                                                String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Silver'";
                                                                                rs = stmt.executeQuery(sql6116);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end red

                                                                            //yellow
                                                                            case 7 -> {
                                                                                String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Yellow'";
                                                                                rs = stmt.executeQuery(sql6117);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end yellow

                                                                            //grey
                                                                            case 8 -> {
                                                                                String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Grey'";
                                                                                rs = stmt.executeQuery(sql6118);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end grey

                                                                            //purple
                                                                            case 9 -> {
                                                                                String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Purple'";
                                                                                rs = stmt.executeQuery(sql6119);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end purple

                                                                            //Orange 
                                                                            case 10 -> {
                                                                                String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Orange'";
                                                                                rs = stmt.executeQuery(sql61110);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end orange

                                                                            //brown
                                                                            case 11 -> {
                                                                                String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Brown'";
                                                                                rs = stmt.executeQuery(sql61111);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end brown

                                                                            //pink
                                                                            case 12 -> {
                                                                                String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Pink'";
                                                                                rs = stmt.executeQuery(sql61112);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end pink

                                                                            //Gold
                                                                            case 13 -> {
                                                                                String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Red'";
                                                                                rs = stmt.executeQuery(sql61113);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end gold

                                                                            //beige
                                                                            case 14 -> {
                                                                                String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Beige'";
                                                                                rs = stmt.executeQuery(sql61114);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end beige

                                                                            //turquoise
                                                                            case 15 -> {
                                                                                String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Turquoise' "
                                                                                        + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                                rs = stmt.executeQuery(sql61115);
                                                                                int itemCount = 0;

                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }

                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();

                                                                            } //end turquoise
                                                                            
                                                                            default ->
                                                                                System.out.println("Invalid selection.");

                                                                        } //end of color switch

                                                                    } //end of case 1

                                                                } //end of switch

                                                            } //end paint case

                                                            //case 2 on engine recalls
                                                            case 2 -> {

                                                                System.out.println("""
                                                                   Choose the specific engine: 
                                                                         1. Ford V6
                                                                         2. GM V8
                                                                         3. Tesla Electric
                                                                         4. Toyota Hybrid
                                                                         5. Cummins Diesel
                                                                         6. Honda V6
                                                                         7. Chevrolet V8
                                                                         8. Nissan Electric
                                                                         9. Hyundai Hybrid
                                                                        10. Isuzu Diesel
                                                                        11. Mitsubishi V6
                                                                        12. Chrysler V8
                                                                        13. BMW Electric
                                                                        14. Kia Hybrid
                                                                        15. Peugeot Diesel 
                                                                    (Note: all options do work, but for this database
                                                                            representation, only numbers 6, 7, 11, 12, 13, 15
                                                                            will show vehicle options. """);
                                                                int caseSOOTwo = scanner.nextInt();

                                                                //switch set for engines
                                                                switch (caseSOOTwo) {

                                                                    //engine 1
                                                                    case 1 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Ford V6'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    // GM V8
                                                                    case 2 -> {
                                                                        String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'GM V8'";
                                                                        rs = stmt.executeQuery(sql6112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end blue

                                                                    //Tesla Electric
                                                                    case 3 -> {
                                                                        String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Tesla Electric'";
                                                                        rs = stmt.executeQuery(sql6113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end black

                                                                    //Toyota Hybrid
                                                                    case 4 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Toyota Hybrid'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end white

                                                                    //Cummins Diesel
                                                                    case 5 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Cummins Diesel'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end 

                                                                    // Honda V6
                                                                    case 6 -> {
                                                                        String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Honda V6'";
                                                                        rs = stmt.executeQuery(sql6116);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 6

                                                                    //chevy v8
                                                                    case 7 -> {
                                                                        String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Chevrolet V8'";
                                                                        rs = stmt.executeQuery(sql6117);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 7

                                                                    // nissan electric
                                                                    case 8 -> {
                                                                        String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Nissan Electric'";
                                                                        rs = stmt.executeQuery(sql6118);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 8

                                                                    //Hyundai Hybrid
                                                                    case 9 -> {
                                                                        String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Hyundai Hybrid'";
                                                                        rs = stmt.executeQuery(sql6119);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 9

                                                                    // Isuzu Diesel
                                                                    case 10 -> {
                                                                        String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Isuzu Diesel'";
                                                                        rs = stmt.executeQuery(sql61110);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 10

                                                                    //Mitsubishi v6
                                                                    case 11 -> {
                                                                        String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Mitsubishi V6'";
                                                                        rs = stmt.executeQuery(sql61111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 11

                                                                    //Chrysler V8
                                                                    case 12 -> {
                                                                        String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Chrysler V8'";
                                                                        rs = stmt.executeQuery(sql61112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 12

                                                                    // BMW electric
                                                                    case 13 -> {
                                                                        String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'BMW Electric'";
                                                                        rs = stmt.executeQuery(sql61113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 13

                                                                    //kia hybrid
                                                                    case 14 -> {
                                                                        String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Kia Hybrid'";
                                                                        rs = stmt.executeQuery(sql61114);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 14

                                                                    //peugeot diesel
                                                                    case 15 -> {
                                                                        String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Peugeot Diesel' "
                                                                                + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                        rs = stmt.executeQuery(sql61115);
                                                                        int itemCount = 0;

                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }

                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();

                                                                    } //end case 15
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end of engine switch

                                                            } //end of case 2

                                                            //case 3 on transmission
                                                            case 3 -> {
                                                                System.out.println("""
                                                                   Choose the specific color: 
                                                                         1. Aisin Automatic
                                                                         2. Getrag Manual
                                                                         3. BorgWarner Automatic
                                                                         4. Jatco CVT
                                                                         5. Allison Automatic
                                                                         6. ZF Manual
                                                                         7. Magna Automatic
                                                                         8. Ricardo Manual
                                                                         9. Hyundai Transys CVT
                                                                        10. Valeo Manual
                                                                        11. Mitsubishi Automatic
                                                                        12. Chryslter Manual
                                                                        13. Continental Automatic
                                                                        14. Hyundai WIA CVT
                                                                        15. PSA Automatic
                                                                   (Note: not all options match vehicles in this representation
                                                                          but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                          will have readable output, but all work.""");
                                                                int caseSOOThree = scanner.nextInt();

                                                                //switch set for paint colors
                                                                switch (caseSOOThree) {

                                                                    //Aisin
                                                                    case 1 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Aisin Automatic'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    //Getrag
                                                                    case 2 -> {
                                                                        String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Getrag Manual'";
                                                                        rs = stmt.executeQuery(sql6112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 2

                                                                    //BorgWarner
                                                                    case 3 -> {
                                                                        String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'BorgWarner Automatic'";
                                                                        rs = stmt.executeQuery(sql6113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 3

                                                                    //Jatco
                                                                    case 4 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Jatco CVT'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 4

                                                                    //Allison
                                                                    case 5 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.color = 'Allison Automatic'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 5

                                                                    //ZF
                                                                    case 6 -> {
                                                                        String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'ZF Manual'";
                                                                        rs = stmt.executeQuery(sql6116);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 6

                                                                    //Magna
                                                                    case 7 -> {
                                                                        String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Magna Automatic'";
                                                                        rs = stmt.executeQuery(sql6117);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 7

                                                                    //Ricardo
                                                                    case 8 -> {
                                                                        String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Ricardo Manual'";
                                                                        rs = stmt.executeQuery(sql6118);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 8

                                                                    //Hyundai
                                                                    case 9 -> {
                                                                        String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Hyundai Transys CVT'";
                                                                        rs = stmt.executeQuery(sql6119);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 9

                                                                    //Valeo 
                                                                    case 10 -> {
                                                                        String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Valeo Manual'";
                                                                        rs = stmt.executeQuery(sql61110);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 10

                                                                    //Mitsubishi
                                                                    case 11 -> {
                                                                        String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Mitsubishi Automatic'";
                                                                        rs = stmt.executeQuery(sql61111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 11

                                                                    //Chrysler
                                                                    case 12 -> {
                                                                        String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Chrysler Manual'";
                                                                        rs = stmt.executeQuery(sql61112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 12

                                                                    //Continental
                                                                    case 13 -> {
                                                                        String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Continental Automatic'";
                                                                        rs = stmt.executeQuery(sql61113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 13

                                                                    //Hyundai
                                                                    case 14 -> {
                                                                        String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Hyundai WIA CVT'";
                                                                        rs = stmt.executeQuery(sql61114);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 14

                                                                    //PSA
                                                                    case 15 -> {
                                                                        String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'PSA Automatic' "
                                                                                + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                        rs = stmt.executeQuery(sql61115);
                                                                        int itemCount = 0;

                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }

                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();

                                                                    } //end case 15
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end of switch

                                                            } // end transmission case

                                                            //case 4 on tires
                                                            case 4 -> {
                                                                System.out.println("""
                                                                   Choose the specific tire brand: 
                                                                         1. Michelin
                                                                         2. Bridgestone
                                                                         3. Goodyear
                                                                         4. Continental
                                                                         5. Yokohama
                                                                         6. Pirelli
                                                                         7. Hankook
                                                                         8. Dunlop
                                                                         9. Falken
                                                                        10. Kumho
                                                                        11. BFGoodrich
                                                                        12. Nitto
                                                                        13. Cooper
                                                                        14. Toyo
                                                                        15. General 
                                                                  (Note: not all options match vehicles in this representation
                                                                         but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                         will have readable output, but all work.""");
                                                                int caseSOOFour = scanner.nextInt();

                                                                //switch set for paint colors
                                                                switch (caseSOOFour) {

                                                                    //Michelin
                                                                    case 1 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Michelin'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    //Bridgestone
                                                                    case 2 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Bridgestone'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 2

                                                                    //Goodyear
                                                                    case 3 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Goodyear'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 3

                                                                    //Continental
                                                                    case 4 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Continental'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 4

                                                                    //Yokohama
                                                                    case 5 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Yokohama'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 5

                                                                    //Pirelli
                                                                    case 6 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Pirelli'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 6

                                                                    //Hankook
                                                                    case 7 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Hankook'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 7

                                                                    //Dunlop
                                                                    case 8 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Dunlop'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 8

                                                                    //Falken
                                                                    case 9 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Falken'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 9

                                                                    //Kumho
                                                                    case 10 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Kumho'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 10

                                                                    //BFGoodrich
                                                                    case 11 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'BFGoodrich'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 11

                                                                    //Nitto
                                                                    case 12 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Nitto'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 12

                                                                    //Cooper
                                                                    case 13 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Cooper'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 13

                                                                    //Toyo
                                                                    case 14 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Toyo'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 14

                                                                    //General
                                                                    case 15 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'General'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 15
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end of switch

                                                            } // end tire case

                                                        } //end switch case 61

                                                    } //end of case 1

                                                    //case 2 - by location
                                                    case 2 -> {

                                                        System.out.println("""
                                                       Which location is affected: 
                                                            1. Factory ZERO in Detroit, MI
                                                            2. GM Customer Care in Bolingbrook, IL
                                                            3. SouthGate Assembly in Los Angeles, CA
                                                            4. West Point GM Part Center in Houston, TX
                                                            5. Arlington Assembly in Arlington, TX """);
                                                        int caseFinal = scanner.nextInt();

                                                        switch (caseFinal) {

                                                            //factory zero
                                                            case 1 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'Factory ZERO'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end factory zero

                                                            //GM Customer Care
                                                            case 2 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'GM Customer Care'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end GM Customer Care

                                                            //South Gate Assembly
                                                            case 3 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'South Gate Assembly'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end south gate assembly

                                                            //West Point GM Part Center
                                                            case 4 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'West Point GM Part Center'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end case 4 - west point GM part center

                                                            //Arlington Assembly
                                                            case 5 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'Arlington Assembly'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                } //end loop line 2699
                                                                System.out.println();

                                                            } //end case 5 - arlington assembly line 2690
                                                            
                                                            default ->
                                                                System.out.println("Invalid selection.");

                                                        } //end caseFinal switch line 2603

                                                    } //end of case 2 line 2592

                                                } //end of caseSix switch line 1086

                                            } //end of manager case 6 switch line 1078

                                            // catch all for entries with no case
                                            default ->
                                                System.out.println("Invalid query type selected.");

                                        } //end queryType switch line 105

                                    } else {  //input catcch for line 102
                                        System.out.println("Invalid query type selected.");
                                    }

                                } //end case manager_role while loop line 85

                            } //end case manager_role

                            // role worker loop
                            case "worker_role" -> {
                                boolean continueLoopOne = true;

                                while (continueLoopOne) {
                                    // Ask user for input on what query they want to run
                                    System.out.println("""
                                   Enter the query type
                                      1: Body Style Popularity
                                      2: Purchase Trends
                                      3: Customers 
                                      4: Dealer Information
                                      5: Suppliers
                                      6: Recalls
                                      0: Log Out""");
                                    int queryType = scanner.nextInt();
                                    scanner.nextLine(); // consume the newline

                                    if (queryType == 0) {
                                        System.out.println();
                                        continueLoopOne = false;
                                    } else if (queryType > 0 && queryType < 7) {

                                        //switch case for query type 1 for trends per body style type
                                        switch (queryType) {
                                            case 1 -> {
                                                System.out.println("""
                                               Select body style type:  
                                                  1: Convertible  
                                                  2: Coupe 
                                                  3: Pickup 
                                                  4: Sedan  
                                                  5: SUV""");
                                                int caseOne = scanner.nextInt();

                                                switch (caseOne) {
                                                    //convertible body style count
                                                    case 1 -> {
                                                        String sqlcase1 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as ConvertibleCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Convertible') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase1);
                                                        System.out.println("Convertible Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("ConvertibleCount");
                                                            System.out.println("     Month: " + month + " - Convertibles Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    case 2 -> {
                                                        // coupe body style count
                                                        String sqlcase2 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as CoupeCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Coupe') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase2);
                                                        System.out.println("Coupe Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("CoupeCount");
                                                            System.out.println("     Month: " + month + " - Coupes Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    case 3 -> {
                                                        // pickup body style count
                                                        String sqlcase3 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as PickupCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Pickup') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase3);
                                                        System.out.println("Pickup Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("PickupCount");
                                                            System.out.println("     Month: " + month + " - Pickups Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    case 4 -> {
                                                        //sedan body style count
                                                        String sqlcase4 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as SedanCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Sedan') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase4);
                                                        System.out.println("Sedan Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("SedanCount");
                                                            System.out.println("     Month: " + month + " - Sedans Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    case 5 -> {
                                                        //SUV body style count
                                                        String sqlcase5 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as SUVCount "
                                                                + "FROM vehicle "
                                                                + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'SUV') "
                                                                + "AND soldDate IS NOT NULL "
                                                                + "GROUP BY Month";
                                                        rs = stmt.executeQuery(sqlcase5);
                                                        System.out.println("SUV Purchase Trends by Month:");
                                                        while (rs.next()) {
                                                            String month = rs.getString("Month");
                                                            int count = rs.getInt("SUVCount");
                                                            System.out.println("     Month: " + month + " - SUVs Sold: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    default ->
                                                        System.out.println("Invalid query type selected.");
                                                }

                                            }//end of case 1

                                            //case 2 for sales duration
                                            case 2 -> {
                                                //prompt for which duration to calculate
                                                System.out.println("""
                                               Trend Options:
                                                  1: 3 years
                                                  2: 1 year
                                                  3: 1 week
                                                  4: 1 week
                                                  5: All Sold
                                                  6: Top Brands by Dollar
                                                  7: Top Brands by Unit""");
                                                int caseTwo = scanner.nextInt();
                                                //new switch case 
                                                switch (caseTwo) {

                                                    //sale trends within 3 years
                                                    case 1 -> {
                                                        String sql21 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 3 YEAR) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql21);
                                                        System.out.println("Purchase trend for the past 3 years:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }
                                                    //sale trends within 1 year
                                                    case 2 -> {
                                                        String sql22 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql22);
                                                        System.out.println("Purchase trend for the past year:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //sale trend within one month
                                                    case 3 -> {
                                                        String sql23 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql23);
                                                        System.out.println("Purchase trend for the past 1 month:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //sale trend within past week
                                                    case 4 -> {
                                                        String sql24 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK) "
                                                                + "GROUP BY brandName";
                                                        rs = stmt.executeQuery(sql24);
                                                        System.out.println("Purchase trend for the past week:");
                                                        while (rs.next()) {
                                                            String brandName = rs.getString("brandName");
                                                            int count = rs.getInt("SalesCount");
                                                            System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //all sold vehicles
                                                    case 5 -> {
                                                        String sql25 = "SELECT vehicle.VIN, brand.brandName, model.modelName, vehicle.list_price "
                                                                + "FROM vehicle "
                                                                + "JOIN model ON vehicle.modelID = model.modelID "
                                                                + "JOIN brand ON model.brandID = brand.brandID "
                                                                + "WHERE vehicle.soldDate IS NOT NULL "
                                                                + "ORDER BY brand.brandName";
                                                        rs = stmt.executeQuery(sql25);
                                                        System.out.println("All vehicles sold: ");
                                                        while (rs.next()) {
                                                            String vin = rs.getString("VIN");
                                                            String brandName = rs.getString("brandName");
                                                            String modelName = rs.getString("modelName");
                                                            String listPrice = rs.getString("list_price");
                                                            System.out.println(brandName + " " + modelName + " " + vin + " $" + listPrice);
                                                        }
                                                        System.out.println();
                                                    }

                                                    // top brands by dollars
                                                    case 6 -> {
                                                        String sql26 = "SELECT brand.brandName, SUM(vehicle.list_price) as sold "
                                                                + "FROM brand "
                                                                + "JOIN model on model.brandID = brand.brandID "
                                                                + "JOIN vehicle on vehicle.modelID = model.modelID "
                                                                + "JOIN dealer on vehicle.dealerID = dealer.dealerID "
                                                                + "GROUP BY brand.brandName "
                                                                + "ORDER BY sold DESC "
                                                                + "LIMIT 2";
                                                        rs = stmt.executeQuery(sql26);
                                                        System.out.println("Top 2 Brands by Dollar Amount: ");
                                                        while (rs.next()) {
                                                            String name = rs.getString("brandName");
                                                            double sales = rs.getDouble("sold");
                                                            System.out.println("    $" + sales + " " + name);
                                                        }
                                                        System.out.println();
                                                    }

                                                    // top brands by unit count
                                                    case 7 -> {
                                                        String sql27 = "SELECT brand.brandName, COUNT(vehicle.soldDate) as sold "
                                                                + "FROM brand "
                                                                + "JOIN model on model.brandID = brand.brandID "
                                                                + "JOIN vehicle on vehicle.modelID = model.modelID "
                                                                + "JOIN dealer on vehicle.dealerID = dealer.dealerID "
                                                                + "GROUP BY brand.brandName "
                                                                + "ORDER BY sold DESC "
                                                                + "LIMIT 2";
                                                        rs = stmt.executeQuery(sql27);
                                                        System.out.println("Top 2 Brands by Unit Sales: ");
                                                        while (rs.next()) {
                                                            String name = rs.getString("brandName");
                                                            int sales = rs.getInt("sold");
                                                            System.out.println("    " + sales + " " + name);
                                                        }
                                                        System.out.println();
                                                    }

                                                    //default to catch input not accounted for
                                                    default ->
                                                        System.out.println("Invalid query type selected.");
                                                }
                                            } //end case 2

                                            case 3 -> {
                                                // select all, by gender, or by annual income
                                                System.out.println("""
                                               Pick a view: 
                                                  1: All Customers
                                                  2: Gender
                                                  3: Annual income""");
                                                int caseThree = scanner.nextInt();

                                                //options for customer cases
                                                switch (caseThree) {
                                                    case 1 -> {
                                                        String sql31 = "SELECT customerID, custName, gender, annualIncome FROM customer ORDER BY customerID";
                                                        rs = stmt.executeQuery(sql31);
                                                        System.out.println("All customers:");
                                                        while (rs.next()) {
                                                            String custID = rs.getString("customerID");
                                                            String gen = rs.getString("gender");
                                                            String income = rs.getString("annualIncome");
                                                            String name = rs.getString("custName");
                                                            System.out.println(custID + " " + name + " " + gen + " " + income);
                                                        }
                                                        System.out.println();
                                                    }
                                                    // show gender
                                                    case 2 -> {
                                                        System.out.println("""
                                                       Show male or female?
                                                           1: Male
                                                           2: Female""");
                                                        int caseThreeTwo = scanner.nextInt();

                                                        switch (caseThreeTwo) {

                                                            //show males
                                                            case 1 -> {
                                                                String sql311 = "SELECT customer.custName, brand.brandName, model.modelName, model.bodyStyle, vehicle.soldDate "
                                                                        + "FROM customer "
                                                                        + "JOIN vehicle on vehicle.customerID = customer.customerID "
                                                                        + "JOIN model ON model.modelID = vehicle.modelID "
                                                                        + "JOIN brand ON model.brandID = brand.brandID "
                                                                        + "WHERE customer.gender = 'M' AND vehicle.soldDate IS NOT NULL "
                                                                        + "ORDER BY custName ASC";
                                                                rs = stmt.executeQuery(sql311);
                                                                System.out.println("All Male Customers - Vehicle purchased - Purchase date");
                                                                while (rs.next()) {
                                                                    String cname = rs.getString("custName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bname = rs.getString("brandName");
                                                                    String style = rs.getString("bodyStyle");
                                                                    String date = rs.getString("soldDate");
                                                                    System.out.println("    " + cname + " - " + bname + " " + mname + " " + style + " - " + date);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show females and the vehicles they purchased
                                                            case 2 -> {
                                                                String sql312;
                                                                sql312 = "SELECT customer.custName, brand.brandName, model.modelName, model.bodyStyle, vehicle.soldDate "
                                                                        + "FROM customer "
                                                                        + "JOIN vehicle on vehicle.customerID = customer.customerID "
                                                                        + "JOIN model ON model.modelID = vehicle.modelID "
                                                                        + "JOIN brand ON model.brandID = brand.brandID "
                                                                        + "WHERE customer.gender = 'F' AND vehicle.soldDate IS NOT NULL "
                                                                        + "ORDER BY custName ASC";
                                                                rs = stmt.executeQuery(sql312);
                                                                System.out.println("All Female Customers");
                                                                while (rs.next()) {
                                                                    String fem = rs.getString("custName");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String style = rs.getString("bodyStyle");
                                                                    String date = rs.getString("soldDate");
                                                                    System.out.println("   " + fem + " - " + bname + " " + mname + " " + style + " - " + date);
                                                                }
                                                                System.out.println();
                                                            }

                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        }
                                                    }
                                                    //show income ranges
                                                    case 3 -> {
                                                        System.out.println("""
                                                Select income range:
                                                    1. Less than $60k
                                                    2. Between $60k and $80k
                                                    3. Between $80k and $100k
                                                    4. Between $100k and $150k
                                                    5. Between $150k and $200k
                                                    6. Over $200k""");
                                                        int caseThreeThree = scanner.nextInt();

                                                        switch (caseThreeThree) {
                                                            //show under 60k
                                                            case 1 -> {
                                                                String sql331 = "SELECT custName, annualIncome FROM customer WHERE annualIncome < 60000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql331);
                                                                System.out.println("Income under $60k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 60-80k
                                                            case 2 -> {
                                                                String sql332 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 60000 AND annualIncome <80000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql332);
                                                                System.out.println("Income between $80k and $100k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 80-100k
                                                            case 3 -> {
                                                                String sql333 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 80000 AND annualIncome <100000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql333);
                                                                System.out.println("Income between $80k and $100k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 100k-150k
                                                            case 4 -> {
                                                                String sql334 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 100000 AND annualIncome <150000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql334);
                                                                System.out.println("Income between $100k and $150k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show 150-200k
                                                            case 5 -> {
                                                                String sql335 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 150000 AND annualIncome <200000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql335);
                                                                System.out.println("Income between $150k and $200k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //show over 200k
                                                            case 6 -> {
                                                                String sql336 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 200000 ORDER BY annualIncome ASC";
                                                                rs = stmt.executeQuery(sql336);
                                                                System.out.println("Income over $200k");
                                                                while (rs.next()) {
                                                                    String name = rs.getString("custName");
                                                                    String annInc = rs.getString("annualIncome");
                                                                    System.out.println("  " + name + " " + annInc);
                                                                }
                                                                System.out.println();
                                                            }
                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        }

                                                    }
                                                    default ->
                                                        System.out.println("Invalid query type selected.");

                                                }
                                            }
                                            case 4 -> {
                                                // Dealer options 
                                                System.out.println("""
                                Show Dealer Options:
                                    1. All dealers
                                    2. Sales
                                    3. Available Vehicles
                                    4. Dealer Locations """);
                                                int caseFour = scanner.nextInt();

                                                switch (caseFour) {

                                                    //show all dealer options
                                                    case 1 -> {
                                                        String sql41 = "SELECT dealerName, location FROM dealer";
                                                        rs = stmt.executeQuery(sql41);
                                                        System.out.println("All dealers:");
                                                        while (rs.next()) {
                                                            String name = rs.getString("dealerName");
                                                            String loc = rs.getString("location");
                                                            System.out.println("   " + name + " " + loc);
                                                        }
                                                        System.out.println();
                                                    }

                                                    // list of dealer sales
                                                    case 2 -> {

                                                        String sql421 = "SELECT dealer.dealerName, COUNT(vehicle.soldDate) as sold "
                                                                + "FROM dealer "
                                                                + "JOIN vehicle on vehicle.dealerID=dealer.dealerID "
                                                                + "WHERE vehicle.soldDate IS NOT NULL "
                                                                + "GROUP BY dealer.dealerName "
                                                                + "ORDER BY sold DESC";
                                                        rs = stmt.executeQuery(sql421);
                                                        System.out.println("Dealer Sales Count:");
                                                        while (rs.next()) {
                                                            String name = rs.getString("dealerName");
                                                            int sales = rs.getInt("sold");
                                                            System.out.println("    " + name + " " + sales);

                                                        }
                                                    }

                                                    //dealer available vehicles
                                                    case 3 -> {
                                                        String sql443 = "SELECT dealer.dealerName, vehicle.VIN, brand.brandName, model.bodyStyle, options.color, options.engine, options.transmission, tire.tire_brand_name, tire.tire_style_name "
                                                                + "FROM dealer "
                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                + "JOIN options on options.option_id = model.option_id "
                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                + "WHERE vehicle.soldDate IS NULL "
                                                                + "ORDER BY dealerName";
                                                        rs = stmt.executeQuery(sql443);
                                                        System.out.println("Vehicles available at each dealer: ");
                                                        while (rs.next()) {
                                                            String name = rs.getString("dealerName");
                                                            String vin = rs.getString("VIN");
                                                            String brand = rs.getString("brandName");
                                                            String body = rs.getString("bodyStyle");
                                                            String col = rs.getString("color");
                                                            String eng = rs.getString("engine");
                                                            String trans = rs.getString("transmission");
                                                            String tireB = rs.getString("tire_brand_name");
                                                            String tireS = rs.getString("tire_style_name");
                                                            System.out.println("    " + name + " " + vin + " " + brand + " " + body + " " + col);
                                                            System.out.println("            " + eng + " " + trans + " " + tireB + " " + tireS);
                                                        }
                                                        System.out.println(" \n");
                                                    }
                                                    //dealer locations
                                                    case 4 -> {
                                                        String sql44 = "SELECT location, dealerName from dealer";
                                                        rs = stmt.executeQuery(sql44);
                                                        System.out.println("Dealer locations: ");
                                                        while (rs.next()) {
                                                            String loc = rs.getString("location");
                                                            String name = rs.getString("dealerName");
                                                            System.out.println("   " + loc + " " + name);
                                                        }
                                                        System.out.println("\n");
                                                    }

                                                    default ->
                                                        System.out.println("Invalid query type selected.");
                                                }

                                            }

                                            //suppliers and manufacturers
                                            case 5 -> {

                                                System.out.println("""
                                               Suppliers and Manufacturers:
                                                   1: Suppliers
                                                   2: Manufacturers
                                                   3: Plants by Type """);
                                                int caseFive = scanner.nextInt();

                                                switch (caseFive) {
                                                    // case 51: all suppliers 

                                                    case 1 -> {
                                                        System.out.println("""
                                                       Selection:
                                                            1: List of Suppliers
                                                            2: List of Vehicles Per Supplier""");
                                                        int caseFiveOne = scanner.nextInt();

                                                        switch (caseFiveOne) {

                                                            //case 511: list of all suppliers
                                                            case 1 -> {
                                                                String sql511 = "SELECT supplierID, supplierName, contactInfo "
                                                                        + "FROM supplier ";
                                                                rs = stmt.executeQuery(sql511);
                                                                while (rs.next()) {
                                                                    String id = rs.getString("supplierID");
                                                                    String name = rs.getString("supplierName");
                                                                    String contact = rs.getString("contactInfo");
                                                                    System.out.println("     " + id + " " + name + " " + contact);
                                                                }
                                                                System.out.println();
                                                            }

                                                            //case 522: list of vehicles per supplier
                                                            case 2 -> {
                                                                System.out.println("""
                                                               Choose a Supplier:
                                                                    1: EngineWorks
                                                                    2: TransMax
                                                                    3: PaintPros
                                                                    4: TrimTech
                                                                    5: WheelDeal """);
                                                                int caseFiveTwoTwo = scanner.nextInt();

                                                                //switch
                                                                switch (caseFiveTwoTwo) {

                                                                    //case 1: EngineWorks
                                                                    case 1 -> {
                                                                        String sql5221 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '1'";
                                                                        rs = stmt.executeQuery(sql5221);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    }

                                                                    //case 2: TransMax
                                                                    case 2 -> {
                                                                        String sql5222 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '2'";
                                                                        rs = stmt.executeQuery(sql5222);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    } //end case 2

                                                                    //case PaintPros
                                                                    case 3 -> {
                                                                        String sql5223 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '3'";
                                                                        rs = stmt.executeQuery(sql5223);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    } //end case 3

                                                                    //case 4: TrimTech
                                                                    case 4 -> {
                                                                        String sql5224 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '4'";
                                                                        rs = stmt.executeQuery(sql5224);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }
                                                                    } //end case 4

                                                                    //case 5: WheelDeal
                                                                    case 5 -> {
                                                                        String sql5225 = "SELECT vehicle.VIN, brand.brandName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "WHERE supplierID = '5'";
                                                                        rs = stmt.executeQuery(sql5225);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String name = rs.getString("brandName");
                                                                            System.out.println("     " + vin + " " + name);
                                                                        }

                                                                    } //end case 5

                                                                    // catch all
                                                                    default ->
                                                                        System.out.println("Invalid query type selected.");
                                                                }
                                                            } //end case
                                                            
                                                            default ->
                                                                System.out.println("Invalid selection.");

                                                        } //end case 1
                                                    }

                                                    // all manufacturers
                                                    case 2 -> {
                                                        System.out.println("""
                                                       Manufacturer Options:
                                                            1: All Manufacturers
                                                            2: List of Vehicles Associated with Each Manufacturer""");
                                                        int caseFiveTwo = scanner.nextInt();

                                                        switch (caseFiveTwo) {

                                                            //all manufacturers
                                                            case 1 -> {
                                                                String sql521 = "SELECT plantName, location, plantType from manufacturingplant";
                                                                rs = stmt.executeQuery(sql521);
                                                                while (rs.next()) {
                                                                    String plant = rs.getString("plantName");
                                                                    String loc = rs.getString("location");
                                                                    String type = rs.getString("plantType");
                                                                    System.out.println("     " + plant + " " + loc + " " + type);
                                                                }
                                                                System.out.println();

                                                            } //end of case 521 for all manufacturers

                                                            //list of vehicles associated with each
                                                            case 2 -> {
                                                                System.out.println("""
                                                               Select the manufacturer:
                                                                    1: Factory Zero in Detroit, MI
                                                                    2: GM Customer Care in Bolingbrook, IL
                                                                    3: South Gate Assembly in Los Angeles, CA
                                                                    4: West Point GM Part Center in Houston, TX
                                                                    5: Arlington Assembly in Arlington, TX """);
                                                                int caseFiveTwoTwo = scanner.nextInt();

                                                                switch (caseFiveTwoTwo) {

                                                                    //cases for each manufacturer
                                                                    case 1 -> {
                                                                        String sql5221 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '1'";
                                                                        rs = stmt.executeQuery(sql5221);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();

                                                                    } //end case 5221

                                                                    case 2 -> {
                                                                        String sql5222 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '2'";
                                                                        rs = stmt.executeQuery(sql5222);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 5222

                                                                    case 3 -> {
                                                                        String sql5223 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '3'";
                                                                        rs = stmt.executeQuery(sql5223);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 5223

                                                                    case 4 -> {
                                                                        String sql5224 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '4'";
                                                                        rs = stmt.executeQuery(sql5224);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 5224

                                                                    case 5 -> {
                                                                        String sql5225 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on model.modelID = vehicle.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE vehicle.plantID = '5'";
                                                                        rs = stmt.executeQuery(sql5225);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String brand = rs.getString("brandName");
                                                                            String model = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + brand + " " + model);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 5223
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end switch 522
                                                                
                                                            }//end case 522

                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        } //end of switch case 52

                                                    } //end of case five two

                                                    //all plants
                                                    case 3 -> {
                                                        System.out.println("""
                                                       Please make a selection: 
                                                            1: List All Plants 
                                                            2: List Assembly Plants
                                                            3: List Parts Plants
                                                            4: List Vehicles for Associated with Each""");
                                                        int caseFiveThree = scanner.nextInt();

                                                        switch (caseFiveThree) {

                                                            //list all plants
                                                            case 1 -> {
                                                                String sql531 = "SELECT plantID, plantName, location, plantType FROM manufacturingplant ORDER BY plantType";
                                                                rs = stmt.executeQuery(sql531);
                                                                while (rs.next()) {
                                                                    String type = rs.getString("plantType");
                                                                    int id = rs.getInt("plantID");
                                                                    String pname = rs.getString("plantName");
                                                                    String ploc = rs.getString("location");
                                                                    System.out.println("    " + " " + type + " " + id + " " + pname + " " + ploc);
                                                                } //end of while loop
                                                                System.out.println();

                                                            } //end switch case 1

                                                            //list assembly plants
                                                            case 2 -> {
                                                                String sql532 = "SELECT plantID, plantName, location FROM manufacturingplant "
                                                                        + "WHERE plantType = 'Assembly'";
                                                                rs = stmt.executeQuery(sql532);
                                                                while (rs.next()) {
                                                                    int id = rs.getInt("plantID");
                                                                    String pname = rs.getString("plantName");
                                                                    String ploc = rs.getString("location");
                                                                    System.out.println("    " + id + " " + pname + " " + ploc);
                                                                }
                                                                System.out.println();

                                                            } // end switch case 2

                                                            //list all parts plants
                                                            case 3 -> {
                                                                String sql533 = "SELECT plantID, plantName, location from manufacturingplant "
                                                                        + "WHERE plantType = 'Parts'";
                                                                rs = stmt.executeQuery(sql533);
                                                                while (rs.next()) {
                                                                    int id = rs.getInt("plantID");
                                                                    String pname = rs.getString("plantName");
                                                                    String ploc = rs.getString("location");
                                                                    System.out.println("    " + id + " " + pname + " " + ploc);
                                                                }
                                                                System.out.println();

                                                            } // end switch case 3

                                                            //list vehicles by plant
                                                            case 4 -> {

                                                                System.out.println("""
                                                           Please choose your plant under the plant type:
                                                                Assembly: 
                                                                    1. Factory ZERO
                                                                    2. South Gate Assembly
                                                                    3. Arlington Assembly
                                                                Parts
                                                                    4. GM Customer Care
                                                                    5. West Point GM Part Center """);
                                                                int caseFiveThreeFour = scanner.nextInt();

                                                                //new switch
                                                                switch (caseFiveThreeFour) {

                                                                    //Factory ZERO, Detroit, MI, Assembly
                                                                    case 1 -> {
                                                                        String sql5341 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'Factory ZERO'";
                                                                        rs = stmt.executeQuery(sql5341);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    //GM Customer Care, Bolingbrook, IL, Parts
                                                                    case 2 -> {
                                                                        String sql5342 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'South Gate Assembly'";
                                                                        rs = stmt.executeQuery(sql5342);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 2

                                                                    //South Gate Assembly, Los Angeles, CA, Assembly
                                                                    case 3 -> {
                                                                        String sql5343 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'South Gate Assembly'";
                                                                        rs = stmt.executeQuery(sql5343);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } // end case 3

                                                                    //West Point GM Part Center, Houston, TX, Parts
                                                                    case 4 -> {
                                                                        String sql5344 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'West Point GM Part Center'";
                                                                        rs = stmt.executeQuery(sql5344);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 4

                                                                    //Arlington Assembly, Arlington, TX, Assembly
                                                                    case 5 -> {
                                                                        String sql5345 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                + "FROM vehicle "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                + "WHERE plantName = 'Arlington Assembly'";
                                                                        rs = stmt.executeQuery(sql5345);
                                                                        while (rs.next()) {
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            String mname = rs.getString("modelName");
                                                                            System.out.println("    " + vin + " " + bname + " " + mname);
                                                                        }
                                                                        System.out.println();
                                                                    } //end case 5
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end switch case 534
                                                                
                                                            } // case 54

                                                            default ->
                                                                System.out.println("Invalid query type selected.");

                                                        } //end switch case 5-53

                                                    } //end case 5-3

                                                    //catch all for number entries with no case defined
                                                    default ->
                                                        System.out.println("Invalid query type selected.");

                                                }//end switch case 5

                                            }//end case 5   

                                            //initial case recalls
                                            case 6 -> {
                                                System.out.println("""
                                               Please select how to find the recalled parts:
                                                    1. By Part
                                                    2. By Location """);
                                                int caseSix = scanner.nextInt();

                                                //main case 6 switch starter
                                                switch (caseSix) {

                                                    //case 1 - by part
                                                    case 1 -> {
                                                        System.out.println("""
                                                       Please select the part:
                                                            1. Paint
                                                            2. Engine
                                                            3. Transmission
                                                            4. Tires """);
                                                        int caseSixOne = scanner.nextInt();

                                                        //start switch case 61
                                                        switch (caseSixOne) {

                                                            //case 1 on poor paint quality
                                                            case 1 -> {
                                                                System.out.println("""
                                                               What is the recall:
                                                                    1. Specific Paint Color
                                                                    2. Specific Factory Fault """);
                                                                int caseSixOneOne = scanner.nextInt();

                                                                //switch sixOneOne for paint colors
                                                                switch (caseSixOneOne) {

                                                                    //list of paint colors
                                                                    case 1 -> {
                                                                        System.out.println("""
                                                                   Choose the specific color: 
                                                                         1. Red
                                                                         2. Blue
                                                                         3. Black
                                                                         4. White
                                                                         5. Green
                                                                         6. Silver
                                                                         7. Yellow
                                                                         8. Grey
                                                                         9. Purple
                                                                        10. Orange
                                                                        11. Brown
                                                                        12. Pink
                                                                        13. Gold
                                                                        14. Beige
                                                                        15. Turquoise 
                                                                       (Note: not all options match vehicles in this representation
                                                                                but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                                will have readable output, but all work. """);
                                                                        int caseSOOOne = scanner.nextInt();

                                                                        //switch set for paint colors
                                                                        switch (caseSOOOne) {

                                                                            case 1 -> {
                                                                                String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Red'";
                                                                                rs = stmt.executeQuery(sql6111);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end red

                                                                            //Blue
                                                                            case 2 -> {
                                                                                String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Blue'";
                                                                                rs = stmt.executeQuery(sql6112);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end blue

                                                                            //black
                                                                            case 3 -> {
                                                                                String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Black'";
                                                                                rs = stmt.executeQuery(sql6113);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end black

                                                                            //White
                                                                            case 4 -> {
                                                                                String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'White'";
                                                                                rs = stmt.executeQuery(sql6115);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end white

                                                                            //green
                                                                            case 5 -> {
                                                                                String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Green'";
                                                                                rs = stmt.executeQuery(sql6115);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end green

                                                                            //silver
                                                                            case 6 -> {
                                                                                String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Silver'";
                                                                                rs = stmt.executeQuery(sql6116);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end red

                                                                            //yellow
                                                                            case 7 -> {
                                                                                String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Yellow'";
                                                                                rs = stmt.executeQuery(sql6117);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end yellow

                                                                            //grey
                                                                            case 8 -> {
                                                                                String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Grey'";
                                                                                rs = stmt.executeQuery(sql6118);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end grey

                                                                            //purple
                                                                            case 9 -> {
                                                                                String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Purple'";
                                                                                rs = stmt.executeQuery(sql6119);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end purple

                                                                            //Orange 
                                                                            case 10 -> {
                                                                                String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Orange'";
                                                                                rs = stmt.executeQuery(sql61110);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end orange

                                                                            //brown
                                                                            case 11 -> {
                                                                                String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Brown'";
                                                                                rs = stmt.executeQuery(sql61111);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end brown

                                                                            //pink
                                                                            case 12 -> {
                                                                                String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Pink'";
                                                                                rs = stmt.executeQuery(sql61112);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end pink

                                                                            //Gold
                                                                            case 13 -> {
                                                                                String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Red'";
                                                                                rs = stmt.executeQuery(sql61113);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end gold

                                                                            //beige
                                                                            case 14 -> {
                                                                                String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Beige'";
                                                                                rs = stmt.executeQuery(sql61114);
                                                                                int itemCount = 0;
                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }
                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();
                                                                            } //end beige

                                                                            //turquoise
                                                                            case 15 -> {
                                                                                String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                        + "FROM dealer "
                                                                                        + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                                        + "JOIN options on model.option_id = options.option_id "
                                                                                        + "WHERE options.color = 'Turquoise' "
                                                                                        + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                                rs = stmt.executeQuery(sql61115);
                                                                                int itemCount = 0;

                                                                                while (rs.next()) {
                                                                                    String dname = rs.getString("dealerName");
                                                                                    String vin = rs.getString("VIN");
                                                                                    String bname = rs.getString("brandName");
                                                                                    System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                    itemCount++;
                                                                                }

                                                                                System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                System.out.println();

                                                                            } //end turquoise
                                                                            
                                                                            default ->
                                                                                System.out.println("Invalid selection.");

                                                                        } //end of color switch
                                                                        
                                                                    } //end of case 1
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end of switch

                                                            } //end paint case

                                                            //case 2 on engine recalls
                                                            case 2 -> {

                                                                System.out.println("""
                                                                   Choose the specific engine: 
                                                                         1. Ford V6
                                                                         2. GM V8
                                                                         3. Tesla Electric
                                                                         4. Toyota Hybrid
                                                                         5. Cummins Diesel
                                                                         6. Honda V6
                                                                         7. Chevrolet V8
                                                                         8. Nissan Electric
                                                                         9. Hyundai Hybrid
                                                                        10. Isuzu Diesel
                                                                        11. Mitsubishi V6
                                                                        12. Chrysler V8
                                                                        13. BMW Electric
                                                                        14. Kia Hybrid
                                                                        15. Peugeot Diesel 
                                                                    (Note: all options do work, but for this database
                                                                            representation, only numbers 6, 7, 11, 12, 13, 15
                                                                            will show vehicle options. """);
                                                                int caseSOOTwo = scanner.nextInt();

                                                                //switch set for engines
                                                                switch (caseSOOTwo) {

                                                                    //engine 1
                                                                    case 1 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Ford V6'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    // GM V8
                                                                    case 2 -> {
                                                                        String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'GM V8'";
                                                                        rs = stmt.executeQuery(sql6112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end blue

                                                                    //Tesla Electric
                                                                    case 3 -> {
                                                                        String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Tesla Electric'";
                                                                        rs = stmt.executeQuery(sql6113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end black

                                                                    //Toyota Hybrid
                                                                    case 4 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Toyota Hybrid'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end white

                                                                    //Cummins Diesel
                                                                    case 5 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Cummins Diesel'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end 

                                                                    // Honda V6
                                                                    case 6 -> {
                                                                        String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Honda V6'";
                                                                        rs = stmt.executeQuery(sql6116);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 6

                                                                    //chevy v8
                                                                    case 7 -> {
                                                                        String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Chevrolet V8'";
                                                                        rs = stmt.executeQuery(sql6117);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 7

                                                                    // nissan electric
                                                                    case 8 -> {
                                                                        String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Nissan Electric'";
                                                                        rs = stmt.executeQuery(sql6118);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 8

                                                                    //Hyundai Hybrid
                                                                    case 9 -> {
                                                                        String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Hyundai Hybrid'";
                                                                        rs = stmt.executeQuery(sql6119);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 9

                                                                    // Isuzu Diesel
                                                                    case 10 -> {
                                                                        String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Isuzu Diesel'";
                                                                        rs = stmt.executeQuery(sql61110);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 10

                                                                    //Mitsubishi v6
                                                                    case 11 -> {
                                                                        String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Mitsubishi V6'";
                                                                        rs = stmt.executeQuery(sql61111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 11

                                                                    //Chrysler V8
                                                                    case 12 -> {
                                                                        String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Chrysler V8'";
                                                                        rs = stmt.executeQuery(sql61112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 12

                                                                    // BMW electric
                                                                    case 13 -> {
                                                                        String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'BMW Electric'";
                                                                        rs = stmt.executeQuery(sql61113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 13

                                                                    //kia hybrid
                                                                    case 14 -> {
                                                                        String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Kia Hybrid'";
                                                                        rs = stmt.executeQuery(sql61114);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 14

                                                                    //peugeot diesel
                                                                    case 15 -> {
                                                                        String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.engine = 'Peugeot Diesel' "
                                                                                + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                        rs = stmt.executeQuery(sql61115);
                                                                        int itemCount = 0;

                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }

                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();

                                                                    } //end case 15
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end of engine switch

                                                            } //end of case 2
                                                            
                                                            //case 3 on transmission
                                                            case 3 -> {
                                                                System.out.println("""
                                                                   Choose the specific color: 
                                                                         1. Aisin Automatic
                                                                         2. Getrag Manual
                                                                         3. BorgWarner Automatic
                                                                         4. Jatco CVT
                                                                         5. Allison Automatic
                                                                         6. ZF Manual
                                                                         7. Magna Automatic
                                                                         8. Ricardo Manual
                                                                         9. Hyundai Transys CVT
                                                                        10. Valeo Manual
                                                                        11. Mitsubishi Automatic
                                                                        12. Chryslter Manual
                                                                        13. Continental Automatic
                                                                        14. Hyundai WIA CVT
                                                                        15. PSA Automatic
                                                                   (Note: not all options match vehicles in this representation
                                                                          but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                          will have readable output, but all work.""");
                                                                int caseSOOThree = scanner.nextInt();

                                                                //switch set for paint colors
                                                                switch (caseSOOThree) {

                                                                    //Aisin
                                                                    case 1 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Aisin Automatic'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    //Getrag
                                                                    case 2 -> {
                                                                        String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Getrag Manual'";
                                                                        rs = stmt.executeQuery(sql6112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 2

                                                                    //BorgWarner
                                                                    case 3 -> {
                                                                        String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'BorgWarner Automatic'";
                                                                        rs = stmt.executeQuery(sql6113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 3

                                                                    //Jatco
                                                                    case 4 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Jatco CVT'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 4

                                                                    //Allison
                                                                    case 5 -> {
                                                                        String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.color = 'Allison Automatic'";
                                                                        rs = stmt.executeQuery(sql6115);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 5

                                                                    //ZF
                                                                    case 6 -> {
                                                                        String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'ZF Manual'";
                                                                        rs = stmt.executeQuery(sql6116);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 6

                                                                    //Magna
                                                                    case 7 -> {
                                                                        String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Magna Automatic'";
                                                                        rs = stmt.executeQuery(sql6117);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 7

                                                                    //Ricardo
                                                                    case 8 -> {
                                                                        String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Ricardo Manual'";
                                                                        rs = stmt.executeQuery(sql6118);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 8

                                                                    //Hyundai
                                                                    case 9 -> {
                                                                        String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Hyundai Transys CVT'";
                                                                        rs = stmt.executeQuery(sql6119);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 9

                                                                    //Valeo 
                                                                    case 10 -> {
                                                                        String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Valeo Manual'";
                                                                        rs = stmt.executeQuery(sql61110);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 10

                                                                    //Mitsubishi
                                                                    case 11 -> {
                                                                        String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Mitsubishi Automatic'";
                                                                        rs = stmt.executeQuery(sql61111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 11

                                                                    //Chrysler
                                                                    case 12 -> {
                                                                        String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Chrysler Manual'";
                                                                        rs = stmt.executeQuery(sql61112);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 12

                                                                    //Continental
                                                                    case 13 -> {
                                                                        String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Continental Automatic'";
                                                                        rs = stmt.executeQuery(sql61113);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 13

                                                                    //Hyundai
                                                                    case 14 -> {
                                                                        String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'Hyundai WIA CVT'";
                                                                        rs = stmt.executeQuery(sql61114);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 14

                                                                    //PSA
                                                                    case 15 -> {
                                                                        String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "WHERE options.transmission = 'PSA Automatic' "
                                                                                + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                        rs = stmt.executeQuery(sql61115);
                                                                        int itemCount = 0;

                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }

                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();

                                                                    } //end case 15
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end of switch

                                                            } // end transmission case

                                                            //case 4 on tires
                                                            case 4 -> {
                                                                System.out.println("""
                                                                   Choose the specific tire brand: 
                                                                         1. Michelin
                                                                         2. Bridgestone
                                                                         3. Goodyear
                                                                         4. Continental
                                                                         5. Yokohama
                                                                         6. Pirelli
                                                                         7. Hankook
                                                                         8. Dunlop
                                                                         9. Falken
                                                                        10. Kumho
                                                                        11. BFGoodrich
                                                                        12. Nitto
                                                                        13. Cooper
                                                                        14. Toyo
                                                                        15. General 
                                                                  (Note: not all options match vehicles in this representation
                                                                         but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                         will have readable output, but all work.""");
                                                                int caseSOOFour = scanner.nextInt();

                                                                //switch set for paint colors
                                                                switch (caseSOOFour) {

                                                                    //Michelin
                                                                    case 1 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Michelin'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 1

                                                                    //Bridgestone
                                                                    case 2 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Bridgestone'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 2

                                                                    //Goodyear
                                                                    case 3 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Goodyear'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 3

                                                                    //Continental
                                                                    case 4 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Continental'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 4

                                                                    //Yokohama
                                                                    case 5 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Yokohama'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 5

                                                                    //Pirelli
                                                                    case 6 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Pirelli'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 6

                                                                    //Hankook
                                                                    case 7 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Hankook'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 7

                                                                    //Dunlop
                                                                    case 8 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Dunlop'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 8

                                                                    //Falken
                                                                    case 9 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Falken'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 9

                                                                    //Kumho
                                                                    case 10 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Kumho'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 10

                                                                    //BFGoodrich
                                                                    case 11 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'BFGoodrich'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 11

                                                                    //Nitto
                                                                    case 12 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Nitto'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 12

                                                                    //Cooper
                                                                    case 13 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Cooper'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 13

                                                                    //Toyo
                                                                    case 14 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'Toyo'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 14

                                                                    //General
                                                                    case 15 -> {
                                                                        String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                + "FROM dealer "
                                                                                + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                + "JOIN model on vehicle.modelID = model.modelID "
                                                                                + "JOIN brand on brand.brandID = model.brandID "
                                                                                + "JOIN options on model.option_id = options.option_id "
                                                                                + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                + "WHERE tire.tire_brand_name = 'General'";
                                                                        rs = stmt.executeQuery(sql6111);
                                                                        int itemCount = 0;
                                                                        while (rs.next()) {
                                                                            String dname = rs.getString("dealerName");
                                                                            String vin = rs.getString("VIN");
                                                                            String bname = rs.getString("brandName");
                                                                            System.out.println("     " + dname + " " + vin + " " + bname);
                                                                            itemCount++;
                                                                        }
                                                                        System.out.println("    How many vehicles are affected: " + itemCount);
                                                                        System.out.println();
                                                                    } //end case 15
                                                                    
                                                                    default ->
                                                                        System.out.println("Invalid selection.");

                                                                } //end of switch

                                                            } // end tire case
                                                            
                                                            default ->
                                                                System.out.println("Invalid selection.");

                                                        } //end switch case 61

                                                    } //end of case 1

                                                    //case 2 - by location
                                                    case 2 -> {

                                                        System.out.println("""
                                                       Which location is affected: 
                                                            1. Factory ZERO in Detroit, MI
                                                            2. GM Customer Care in Bolingbrook, IL
                                                            3. SouthGate Assembly in Los Angeles, CA
                                                            4. West Point GM Part Center in Houston, TX
                                                            5. Arlington Assembly in Arlington, TX """);
                                                        int caseFinal = scanner.nextInt();

                                                        switch (caseFinal) {

                                                            //factory zero
                                                            case 1 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'Factory ZERO'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end factory zero

                                                            //GM Customer Care
                                                            case 2 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'GM Customer Care'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end GM Customer Care

                                                            //South Gate Assembly
                                                            case 3 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'South Gate Assembly'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end south gate assembly

                                                            //West Point GM Part Center
                                                            case 4 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'West Point GM Part Center'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                }
                                                                System.out.println();

                                                            } //end case 4 - west point GM part center

                                                            //Arlington Assembly
                                                            case 5 -> {

                                                                String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                        + "FROM vehicle "
                                                                        + "JOIN model on vehicle.modelID = model.modelID "
                                                                        + "JOIN brand on brand.brandID = model.brandID "
                                                                        + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                        + "WHERE plantName = 'Arlington Assembly'";
                                                                rs = stmt.executeQuery(sqlFinal);
                                                                while (rs.next()) {
                                                                    String vin = rs.getString("VIN");
                                                                    String bname = rs.getString("brandName");
                                                                    String mname = rs.getString("modelName");
                                                                    String bstyle = rs.getString("bodyStyle");
                                                                    System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                } //end loop line 2699
                                                                System.out.println();

                                                            } //end case 5 - arlington assembly line 2690
                                                            
                                                            default ->
                                                                System.out.println("Invalid selection.");

                                                        } //end caseFinal switch line 2603

                                                    } //end of case 2 line 2592
                                                    
                                                    default ->
                                                        System.out.println("Invalid selection");

                                                } //end of caseSix switch line 1086

                                            } //end of manager case 6 switch line 1078

                                            // catch all for entries with no case
                                            default ->
                                                System.out.println("Invalid query type selected.");

                                        } //end queryType switch line 105

                                    } else {  //input catcch for line 102
                                        System.out.println("Invalid query type selected.");
                                    }


                                } //end case manager_role while loop line 85

                            } //end case worker_role

                            // role customer loop
                            case "customer_role" -> {

                                //break;
                                boolean continueLoop = true;

                                while (continueLoop) {
                                    // Ask user for input on what query they want to run
                                    System.out.println("""
                                   Enter the query type
                                      1: Body Style Popularity
                                      2: Purchase Trends
                                      3: Customers 
                                      4: Dealer Information
                                      5: Suppliers
                                      6: Recalls
                                      0: Log Out""");
                                    int queryType = scanner.nextInt();
                                    scanner.nextLine(); // consume the newline

                                    if (queryType == 0) {
                                        System.out.println();
                                        continueLoop = false;
                                    } else if (queryType > 0 && queryType < 7) {
                                        try {
                                            checkAccess(conn, role, queryType);

                                            //switch case for query type 1 for trends per body style type
                                            switch (queryType) {
                                                case 1 -> {

                                                    System.out.println("""
                                                Select body style type:  
                                                  1: Convertible  
                                                  2: Coupe 
                                                  3: Pickup 
                                                  4: Sedan  
                                                  5: SUV""");
                                                    int caseOne = scanner.nextInt();

                                                    switch (caseOne) {
                                                        //convertible body style count
                                                        case 1 -> {
                                                            String sqlcase1 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as ConvertibleCount "
                                                                    + "FROM vehicle "
                                                                    + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Convertible') "
                                                                    + "AND soldDate IS NOT NULL "
                                                                    + "GROUP BY Month";
                                                            rs = stmt.executeQuery(sqlcase1);
                                                            System.out.println("Convertible Purchase Trends by Month:");
                                                            while (rs.next()) {
                                                                String month = rs.getString("Month");
                                                                int count = rs.getInt("ConvertibleCount");
                                                                System.out.println("     Month: " + month + " - Convertibles Sold: " + count);
                                                            }
                                                            System.out.println();
                                                        }
                                                        case 2 -> {
                                                            // coupe body style count
                                                            String sqlcase2 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as CoupeCount "
                                                                    + "FROM vehicle "
                                                                    + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Coupe') "
                                                                    + "AND soldDate IS NOT NULL "
                                                                    + "GROUP BY Month";
                                                            rs = stmt.executeQuery(sqlcase2);
                                                            System.out.println("Coupe Purchase Trends by Month:");
                                                            while (rs.next()) {
                                                                String month = rs.getString("Month");
                                                                int count = rs.getInt("CoupeCount");
                                                                System.out.println("     Month: " + month + " - Coupes Sold: " + count);
                                                            }
                                                            System.out.println();
                                                        }
                                                        case 3 -> {
                                                            // pickup body style count
                                                            String sqlcase3 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as PickupCount "
                                                                    + "FROM vehicle "
                                                                    + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Pickup') "
                                                                    + "AND soldDate IS NOT NULL "
                                                                    + "GROUP BY Month";
                                                            rs = stmt.executeQuery(sqlcase3);
                                                            System.out.println("Pickup Purchase Trends by Month:");
                                                            while (rs.next()) {
                                                                String month = rs.getString("Month");
                                                                int count = rs.getInt("PickupCount");
                                                                System.out.println("     Month: " + month + " - Pickups Sold: " + count);
                                                            }
                                                            System.out.println();
                                                        }
                                                        case 4 -> {
                                                            //sedan body style count
                                                            String sqlcase4 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as SedanCount "
                                                                    + "FROM vehicle "
                                                                    + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'Sedan') "
                                                                    + "AND soldDate IS NOT NULL "
                                                                    + "GROUP BY Month";
                                                            rs = stmt.executeQuery(sqlcase4);
                                                            System.out.println("Sedan Purchase Trends by Month:");
                                                            while (rs.next()) {
                                                                String month = rs.getString("Month");
                                                                int count = rs.getInt("SedanCount");
                                                                System.out.println("     Month: " + month + " - Sedans Sold: " + count);
                                                            }
                                                            System.out.println();
                                                        }
                                                        case 5 -> {
                                                            //SUV body style count
                                                            String sqlcase5 = "SELECT DATE_FORMAT(soldDate, '%m') as Month, COUNT(*) as SUVCount "
                                                                    + "FROM vehicle "
                                                                    + "WHERE modelID IN (SELECT modelID FROM Model WHERE bodyStyle = 'SUV') "
                                                                    + "AND soldDate IS NOT NULL "
                                                                    + "GROUP BY Month";
                                                            rs = stmt.executeQuery(sqlcase5);
                                                            System.out.println("SUV Purchase Trends by Month:");
                                                            while (rs.next()) {
                                                                String month = rs.getString("Month");
                                                                int count = rs.getInt("SUVCount");
                                                                System.out.println("     Month: " + month + " - SUVs Sold: " + count);
                                                            }
                                                            System.out.println();
                                                        }
                                                        default ->
                                                            System.out.println("Invalid query type selected.");
                                                    }

                                                }//end of case 1

                                                //case 2 for sales duration
                                                case 2 -> {
                                                    //prompt for which duration to calculate
                                                    System.out.println("""
                                               Trend Options:
                                                  1: 3 years
                                                  2: 1 year
                                                  3: 1 week
                                                  4: 1 week
                                                  5: All Sold
                                                  6: Top Brands by Dollar
                                                  7: Top Brands by Unit""");
                                                    int caseTwo = scanner.nextInt();
                                                    //new switch case 
                                                    switch (caseTwo) {

                                                        //sale trends within 3 years
                                                        case 1 -> {
                                                            String sql21 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                    + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                    + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 3 YEAR) "
                                                                    + "GROUP BY brandName";
                                                            rs = stmt.executeQuery(sql21);
                                                            System.out.println("Purchase trend for the past 3 years:");
                                                            while (rs.next()) {
                                                                String brandName = rs.getString("brandName");
                                                                int count = rs.getInt("SalesCount");
                                                                System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                            }
                                                            System.out.println();
                                                        }
                                                        //sale trends within 1 year
                                                        case 2 -> {
                                                            String sql22 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                    + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                    + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 YEAR) "
                                                                    + "GROUP BY brandName";
                                                            rs = stmt.executeQuery(sql22);
                                                            System.out.println("Purchase trend for the past year:");
                                                            while (rs.next()) {
                                                                String brandName = rs.getString("brandName");
                                                                int count = rs.getInt("SalesCount");
                                                                System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                            }
                                                            System.out.println();
                                                        }

                                                        //sale trend within one month
                                                        case 3 -> {
                                                            String sql23 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                    + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                    + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH) "
                                                                    + "GROUP BY brandName";
                                                            rs = stmt.executeQuery(sql23);
                                                            System.out.println("Purchase trend for the past 1 month:");
                                                            while (rs.next()) {
                                                                String brandName = rs.getString("brandName");
                                                                int count = rs.getInt("SalesCount");
                                                                System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                            }
                                                            System.out.println();
                                                        }

                                                        //sale trend within past week
                                                        case 4 -> {
                                                            String sql24 = "SELECT brandName, COUNT(*) as SalesCount FROM vehicle, model, brand "
                                                                    + "WHERE vehicle.modelID = model.modelID AND model.brandID = brand.brandID "
                                                                    + "AND vehicle.soldDate IS NOT NULL AND vehicle.soldDate >= DATE_SUB(CURDATE(), INTERVAL 1 WEEK) "
                                                                    + "GROUP BY brandName";
                                                            rs = stmt.executeQuery(sql24);
                                                            System.out.println("Purchase trend for the past week:");
                                                            while (rs.next()) {
                                                                String brandName = rs.getString("brandName");
                                                                int count = rs.getInt("SalesCount");
                                                                System.out.println("     Brand: " + brandName + " - Sales: " + count);
                                                            }
                                                            System.out.println();
                                                        }

                                                        //all sold vehicles
                                                        case 5 -> {
                                                            String sql25 = "SELECT vehicle.VIN, brand.brandName, model.modelName, vehicle.list_price "
                                                                    + "FROM vehicle "
                                                                    + "JOIN model ON vehicle.modelID = model.modelID "
                                                                    + "JOIN brand ON model.brandID = brand.brandID "
                                                                    + "WHERE vehicle.soldDate IS NOT NULL "
                                                                    + "ORDER BY brand.brandName";
                                                            rs = stmt.executeQuery(sql25);
                                                            System.out.println("All vehicles sold: ");
                                                            while (rs.next()) {
                                                                String vin = rs.getString("VIN");
                                                                String brandName = rs.getString("brandName");
                                                                String modelName = rs.getString("modelName");
                                                                String listPrice = rs.getString("list_price");
                                                                System.out.println(brandName + " " + modelName + " " + vin + " $" + listPrice);
                                                            }
                                                            System.out.println();
                                                        }

                                                        // top brands by dollars
                                                        case 6 -> {
                                                            String sql26 = "SELECT brand.brandName, SUM(vehicle.list_price) as sold "
                                                                    + "FROM brand "
                                                                    + "JOIN model on model.brandID = brand.brandID "
                                                                    + "JOIN vehicle on vehicle.modelID = model.modelID "
                                                                    + "JOIN dealer on vehicle.dealerID = dealer.dealerID "
                                                                    + "GROUP BY brand.brandName "
                                                                    + "ORDER BY sold DESC "
                                                                    + "LIMIT 2";
                                                            rs = stmt.executeQuery(sql26);
                                                            System.out.println("Top 2 Brands by Dollar Amount: ");
                                                            while (rs.next()) {
                                                                String name = rs.getString("brandName");
                                                                double sales = rs.getDouble("sold");
                                                                System.out.println("    $" + sales + " " + name);
                                                            }
                                                            System.out.println();
                                                        }

                                                        // top brands by unit count
                                                        case 7 -> {
                                                            String sql27 = "SELECT brand.brandName, COUNT(vehicle.soldDate) as sold "
                                                                    + "FROM brand "
                                                                    + "JOIN model on model.brandID = brand.brandID "
                                                                    + "JOIN vehicle on vehicle.modelID = model.modelID "
                                                                    + "JOIN dealer on vehicle.dealerID = dealer.dealerID "
                                                                    + "GROUP BY brand.brandName "
                                                                    + "ORDER BY sold DESC "
                                                                    + "LIMIT 2";
                                                            rs = stmt.executeQuery(sql27);
                                                            System.out.println("Top 2 Brands by Unit Sales: ");
                                                            while (rs.next()) {
                                                                String name = rs.getString("brandName");
                                                                int sales = rs.getInt("sold");
                                                                System.out.println("    " + sales + " " + name);
                                                            }
                                                            System.out.println();
                                                        }

                                                        //default to catch input not accounted for
                                                        default ->
                                                            System.out.println("Invalid query type selected.");
                                                    }
                                                } //end case 2

                                                case 3 -> {
                                                    // select all, by gender, or by annual income
                                                    System.out.println("""
                                               Pick a view: 
                                                  1: All Customers
                                                  2: Gender
                                                  3: Annual income""");
                                                    int caseThree = scanner.nextInt();

                                                    //options for customer cases
                                                    switch (caseThree) {
                                                        case 1 -> {
                                                            String sql31 = "SELECT customerID, custName, gender, annualIncome FROM customer ORDER BY customerID";
                                                            rs = stmt.executeQuery(sql31);
                                                            System.out.println("All customers:");
                                                            while (rs.next()) {
                                                                String custID = rs.getString("customerID");
                                                                String gen = rs.getString("gender");
                                                                String income = rs.getString("annualIncome");
                                                                String name = rs.getString("custName");
                                                                System.out.println(custID + " " + name + " " + gen + " " + income);
                                                            }
                                                            System.out.println();
                                                        }
                                                        // show gender
                                                        case 2 -> {
                                                            System.out.println("""
                                                       Show male or female?
                                                           1: Male
                                                           2: Female""");
                                                            int caseThreeTwo = scanner.nextInt();

                                                            switch (caseThreeTwo) {

                                                                //show males
                                                                case 1 -> {
                                                                    String sql311 = "SELECT customer.custName, brand.brandName, model.modelName, model.bodyStyle, vehicle.soldDate "
                                                                            + "FROM customer "
                                                                            + "JOIN vehicle on vehicle.customerID = customer.customerID "
                                                                            + "JOIN model ON model.modelID = vehicle.modelID "
                                                                            + "JOIN brand ON model.brandID = brand.brandID "
                                                                            + "WHERE customer.gender = 'M' AND vehicle.soldDate IS NOT NULL "
                                                                            + "ORDER BY custName ASC";
                                                                    rs = stmt.executeQuery(sql311);
                                                                    System.out.println("All Male Customers - Vehicle purchased - Purchase date");
                                                                    while (rs.next()) {
                                                                        String cname = rs.getString("custName");
                                                                        String mname = rs.getString("modelName");
                                                                        String bname = rs.getString("brandName");
                                                                        String style = rs.getString("bodyStyle");
                                                                        String date = rs.getString("soldDate");
                                                                        System.out.println("    " + cname + " - " + bname + " " + mname + " " + style + " - " + date);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                //show females and the vehicles they purchased
                                                                case 2 -> {
                                                                    String sql312;
                                                                    sql312 = "SELECT customer.custName, brand.brandName, model.modelName, model.bodyStyle, vehicle.soldDate "
                                                                            + "FROM customer "
                                                                            + "JOIN vehicle on vehicle.customerID = customer.customerID "
                                                                            + "JOIN model ON model.modelID = vehicle.modelID "
                                                                            + "JOIN brand ON model.brandID = brand.brandID "
                                                                            + "WHERE customer.gender = 'F' AND vehicle.soldDate IS NOT NULL "
                                                                            + "ORDER BY custName ASC";
                                                                    rs = stmt.executeQuery(sql312);
                                                                    System.out.println("All Female Customers");
                                                                    while (rs.next()) {
                                                                        String fem = rs.getString("custName");
                                                                        String bname = rs.getString("brandName");
                                                                        String mname = rs.getString("modelName");
                                                                        String style = rs.getString("bodyStyle");
                                                                        String date = rs.getString("soldDate");
                                                                        System.out.println("   " + fem + " - " + bname + " " + mname + " " + style + " - " + date);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                default ->
                                                                    System.out.println("Invalid query type selected.");

                                                            }
                                                        }
                                                        //show income ranges
                                                        case 3 -> {
                                                            System.out.println("""
                                                Select income range:
                                                    1. Less than $60k
                                                    2. Between $60k and $80k
                                                    3. Between $80k and $100k
                                                    4. Between $100k and $150k
                                                    5. Between $150k and $200k
                                                    6. Over $200k""");
                                                            int caseThreeThree = scanner.nextInt();

                                                            switch (caseThreeThree) {
                                                                //show under 60k
                                                                case 1 -> {
                                                                    String sql331 = "SELECT custName, annualIncome FROM customer WHERE annualIncome < 60000 ORDER BY annualIncome ASC";
                                                                    rs = stmt.executeQuery(sql331);
                                                                    System.out.println("Income under $60k");
                                                                    while (rs.next()) {
                                                                        String name = rs.getString("custName");
                                                                        String annInc = rs.getString("annualIncome");
                                                                        System.out.println("  " + name + " " + annInc);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                //show 60-80k
                                                                case 2 -> {
                                                                    String sql332 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 60000 AND annualIncome <80000 ORDER BY annualIncome ASC";
                                                                    rs = stmt.executeQuery(sql332);
                                                                    System.out.println("Income between $80k and $100k");
                                                                    while (rs.next()) {
                                                                        String name = rs.getString("custName");
                                                                        String annInc = rs.getString("annualIncome");
                                                                        System.out.println("  " + name + " " + annInc);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                //show 80-100k
                                                                case 3 -> {
                                                                    String sql333 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 80000 AND annualIncome <100000 ORDER BY annualIncome ASC";
                                                                    rs = stmt.executeQuery(sql333);
                                                                    System.out.println("Income between $80k and $100k");
                                                                    while (rs.next()) {
                                                                        String name = rs.getString("custName");
                                                                        String annInc = rs.getString("annualIncome");
                                                                        System.out.println("  " + name + " " + annInc);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                //show 100k-150k
                                                                case 4 -> {
                                                                    String sql334 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 100000 AND annualIncome <150000 ORDER BY annualIncome ASC";
                                                                    rs = stmt.executeQuery(sql334);
                                                                    System.out.println("Income between $100k and $150k");
                                                                    while (rs.next()) {
                                                                        String name = rs.getString("custName");
                                                                        String annInc = rs.getString("annualIncome");
                                                                        System.out.println("  " + name + " " + annInc);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                //show 150-200k
                                                                case 5 -> {
                                                                    String sql335 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 150000 AND annualIncome <200000 ORDER BY annualIncome ASC";
                                                                    rs = stmt.executeQuery(sql335);
                                                                    System.out.println("Income between $150k and $200k");
                                                                    while (rs.next()) {
                                                                        String name = rs.getString("custName");
                                                                        String annInc = rs.getString("annualIncome");
                                                                        System.out.println("  " + name + " " + annInc);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                //show over 200k
                                                                case 6 -> {
                                                                    String sql336 = "SELECT custName, annualIncome FROM customer WHERE annualIncome >= 200000 ORDER BY annualIncome ASC";
                                                                    rs = stmt.executeQuery(sql336);
                                                                    System.out.println("Income over $200k");
                                                                    while (rs.next()) {
                                                                        String name = rs.getString("custName");
                                                                        String annInc = rs.getString("annualIncome");
                                                                        System.out.println("  " + name + " " + annInc);
                                                                    }
                                                                    System.out.println();
                                                                }
                                                                default ->
                                                                    System.out.println("Invalid query type selected.");

                                                            }

                                                        }
                                                        default ->
                                                            System.out.println("Invalid query type selected.");

                                                    }
                                                }
                                                case 4 -> {
                                                    // Dealer options 
                                                    System.out.println("""
                                Show Dealer Options:
                                    1. All dealers
                                    2. Sales
                                    3. Available Vehicles
                                    4. Dealer Locations """);
                                                    int caseFour = scanner.nextInt();

                                                    switch (caseFour) {

                                                        //show all dealer options
                                                        case 1 -> {
                                                            String sql41 = "SELECT dealerName, location FROM dealer";
                                                            rs = stmt.executeQuery(sql41);
                                                            System.out.println("All dealers:");
                                                            while (rs.next()) {
                                                                String name = rs.getString("dealerName");
                                                                String loc = rs.getString("location");
                                                                System.out.println("   " + name + " " + loc);
                                                            }
                                                            System.out.println();
                                                        }

                                                        // list of dealer sales
                                                        case 2 -> {

                                                            String sql421 = "SELECT dealer.dealerName, COUNT(vehicle.soldDate) as sold "
                                                                    + "FROM dealer "
                                                                    + "JOIN vehicle on vehicle.dealerID=dealer.dealerID "
                                                                    + "WHERE vehicle.soldDate IS NOT NULL "
                                                                    + "GROUP BY dealer.dealerName "
                                                                    + "ORDER BY sold DESC";
                                                            rs = stmt.executeQuery(sql421);
                                                            System.out.println("Dealer Sales Count:");
                                                            while (rs.next()) {
                                                                String name = rs.getString("dealerName");
                                                                int sales = rs.getInt("sold");
                                                                System.out.println("    " + name + " " + sales);

                                                            }
                                                        }

                                                        //dealer available vehicles
                                                        case 3 -> {
                                                            String sql443 = "SELECT dealer.dealerName, vehicle.VIN, brand.brandName, model.bodyStyle, options.color, options.engine, options.transmission, tire.tire_brand_name, tire.tire_style_name "
                                                                    + "FROM dealer "
                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                    + "JOIN options on options.option_id = model.option_id "
                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                    + "WHERE vehicle.soldDate IS NULL "
                                                                    + "ORDER BY dealerName";
                                                            rs = stmt.executeQuery(sql443);
                                                            System.out.println("Vehicles available at each dealer: ");
                                                            while (rs.next()) {
                                                                String name = rs.getString("dealerName");
                                                                String vin = rs.getString("VIN");
                                                                String brand = rs.getString("brandName");
                                                                String body = rs.getString("bodyStyle");
                                                                String col = rs.getString("color");
                                                                String eng = rs.getString("engine");
                                                                String trans = rs.getString("transmission");
                                                                String tireB = rs.getString("tire_brand_name");
                                                                String tireS = rs.getString("tire_style_name");
                                                                System.out.println("    " + name + " " + vin + " " + brand + " " + body + " " + col);
                                                                System.out.println("            " + eng + " " + trans + " " + tireB + " " + tireS);
                                                            }
                                                            System.out.println(" \n");
                                                        }
                                                        //dealer locations
                                                        case 4 -> {
                                                            String sql44 = "SELECT location, dealerName from dealer";
                                                            rs = stmt.executeQuery(sql44);
                                                            System.out.println("Dealer locations: ");
                                                            while (rs.next()) {
                                                                String loc = rs.getString("location");
                                                                String name = rs.getString("dealerName");
                                                                System.out.println("   " + loc + " " + name);
                                                            }
                                                            System.out.println("\n");
                                                        }

                                                        default ->
                                                            System.out.println("Invalid query type selected.");
                                                    }

                                                }

                                                //suppliers and manufacturers
                                                case 5 -> {

                                                    System.out.println("""
                                               Suppliers and Manufacturers:
                                                   1: Suppliers
                                                   2: Manufacturers
                                                   3: Plants by Type """);
                                                    int caseFive = scanner.nextInt();

                                                    switch (caseFive) {
                                                        // case 51: all suppliers 

                                                        case 1 -> {
                                                            System.out.println("""
                                                       Selection:
                                                            1: List of Suppliers
                                                            2: List of Vehicles Per Supplier""");
                                                            int caseFiveOne = scanner.nextInt();

                                                            switch (caseFiveOne) {

                                                                //case 511: list of all suppliers
                                                                case 1 -> {
                                                                    String sql511 = "SELECT supplierID, supplierName, contactInfo "
                                                                            + "FROM supplier ";
                                                                    rs = stmt.executeQuery(sql511);
                                                                    while (rs.next()) {
                                                                        String id = rs.getString("supplierID");
                                                                        String name = rs.getString("supplierName");
                                                                        String contact = rs.getString("contactInfo");
                                                                        System.out.println("     " + id + " " + name + " " + contact);
                                                                    }
                                                                    System.out.println();
                                                                }

                                                                //case 522: list of vehicles per supplier
                                                                case 2 -> {
                                                                    System.out.println("""
                                                               Choose a Supplier:
                                                                    1: EngineWorks
                                                                    2: TransMax
                                                                    3: PaintPros
                                                                    4: TrimTech
                                                                    5: WheelDeal """);
                                                                    int caseFiveTwoTwo = scanner.nextInt();

                                                                    //switch
                                                                    switch (caseFiveTwoTwo) {

                                                                        //case 1: EngineWorks
                                                                        case 1 -> {
                                                                            String sql5221 = "SELECT vehicle.VIN, brand.brandName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "WHERE supplierID = '1'";
                                                                            rs = stmt.executeQuery(sql5221);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String name = rs.getString("brandName");
                                                                                System.out.println("     " + vin + " " + name);
                                                                            }
                                                                        }

                                                                        //case 2: TransMax
                                                                        case 2 -> {
                                                                            String sql5222 = "SELECT vehicle.VIN, brand.brandName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "WHERE supplierID = '2'";
                                                                            rs = stmt.executeQuery(sql5222);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String name = rs.getString("brandName");
                                                                                System.out.println("     " + vin + " " + name);
                                                                            }
                                                                        } //end case 2

                                                                        //case PaintPros
                                                                        case 3 -> {
                                                                            String sql5223 = "SELECT vehicle.VIN, brand.brandName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "WHERE supplierID = '3'";
                                                                            rs = stmt.executeQuery(sql5223);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String name = rs.getString("brandName");
                                                                                System.out.println("     " + vin + " " + name);
                                                                            }
                                                                        } //end case 3

                                                                        //case 4: TrimTech
                                                                        case 4 -> {
                                                                            String sql5224 = "SELECT vehicle.VIN, brand.brandName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "WHERE supplierID = '4'";
                                                                            rs = stmt.executeQuery(sql5224);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String name = rs.getString("brandName");
                                                                                System.out.println("     " + vin + " " + name);
                                                                            }
                                                                        } //end case 4

                                                                        //case 5: WheelDeal
                                                                        case 5 -> {
                                                                            String sql5225 = "SELECT vehicle.VIN, brand.brandName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "WHERE supplierID = '5'";
                                                                            rs = stmt.executeQuery(sql5225);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String name = rs.getString("brandName");
                                                                                System.out.println("     " + vin + " " + name);
                                                                            }

                                                                        } //end case 5

                                                                        // catch all
                                                                        default ->
                                                                            System.out.println("Invalid query type selected.");
                                                                    }
                                                                } //end case
                                                                
                                                                default ->
                                                                    System.out.println("Invalid selection.");

                                                            } //end case 1
                                                        }

                                                        // all manufacturers
                                                        case 2 -> {
                                                            System.out.println("""
                                                       Manufacturer Options:
                                                            1: All Manufacturers
                                                            2: List of Vehicles Associated with Each Manufacturer""");
                                                            int caseFiveTwo = scanner.nextInt();

                                                            switch (caseFiveTwo) {

                                                                //all manufacturers
                                                                case 1 -> {
                                                                    String sql521 = "SELECT plantName, location, plantType from manufacturingplant";
                                                                    rs = stmt.executeQuery(sql521);
                                                                    while (rs.next()) {
                                                                        String plant = rs.getString("plantName");
                                                                        String loc = rs.getString("location");
                                                                        String type = rs.getString("plantType");
                                                                        System.out.println("     " + plant + " " + loc + " " + type);
                                                                    }
                                                                    System.out.println();

                                                                } //end of case 521 for all manufacturers

                                                                //list of vehicles associated with each
                                                                case 2 -> {
                                                                    System.out.println("""
                                                               Select the manufacturer:
                                                                    1: Factory Zero in Detroit, MI
                                                                    2: GM Customer Care in Bolingbrook, IL
                                                                    3: South Gate Assembly in Los Angeles, CA
                                                                    4: West Point GM Part Center in Houston, TX
                                                                    5: Arlington Assembly in Arlington, TX """);
                                                                    int caseFiveTwoTwo = scanner.nextInt();

                                                                    switch (caseFiveTwoTwo) {

                                                                        //cases for each manufacturer
                                                                        case 1 -> {
                                                                            String sql5221 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE vehicle.plantID = '1'";
                                                                            rs = stmt.executeQuery(sql5221);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String brand = rs.getString("brandName");
                                                                                String model = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + brand + " " + model);
                                                                            }
                                                                            System.out.println();

                                                                        } //end case 5221

                                                                        case 2 -> {
                                                                            String sql5222 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE vehicle.plantID = '2'";
                                                                            rs = stmt.executeQuery(sql5222);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String brand = rs.getString("brandName");
                                                                                String model = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + brand + " " + model);
                                                                            }
                                                                            System.out.println();
                                                                        } // end case 5222

                                                                        case 3 -> {
                                                                            String sql5223 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE vehicle.plantID = '3'";
                                                                            rs = stmt.executeQuery(sql5223);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String brand = rs.getString("brandName");
                                                                                String model = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + brand + " " + model);
                                                                            }
                                                                            System.out.println();
                                                                        } // end case 5223

                                                                        case 4 -> {
                                                                            String sql5224 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE vehicle.plantID = '4'";
                                                                            rs = stmt.executeQuery(sql5224);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String brand = rs.getString("brandName");
                                                                                String model = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + brand + " " + model);
                                                                            }
                                                                            System.out.println();
                                                                        } // end case 5224

                                                                        case 5 -> {
                                                                            String sql5225 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on model.modelID = vehicle.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE vehicle.plantID = '5'";
                                                                            rs = stmt.executeQuery(sql5225);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String brand = rs.getString("brandName");
                                                                                String model = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + brand + " " + model);
                                                                            }
                                                                            System.out.println();
                                                                        } //end case 5223
                                                                        
                                                                        default ->
                                                                            System.out.println("Invalid selection.");

                                                                    } //end switch 522
                                                                   
                                                                }//end case 522

                                                                default ->
                                                                    System.out.println("Invalid query type selected.");

                                                            } //end of switch case 52

                                                        } //end of case five two

                                                        //all plants
                                                        case 3 -> {
                                                            System.out.println("""
                                                       Please make a selection: 
                                                            1: List All Plants 
                                                            2: List Assembly Plants
                                                            3: List Parts Plants
                                                            4: List Vehicles for Associated with Each""");
                                                            int caseFiveThree = scanner.nextInt();

                                                            switch (caseFiveThree) {

                                                                //list all plants
                                                                case 1 -> {
                                                                    String sql531 = "SELECT plantID, plantName, location, plantType FROM manufacturingplant ORDER BY plantType";
                                                                    rs = stmt.executeQuery(sql531);
                                                                    while (rs.next()) {
                                                                        String type = rs.getString("plantType");
                                                                        int id = rs.getInt("plantID");
                                                                        String pname = rs.getString("plantName");
                                                                        String ploc = rs.getString("location");
                                                                        System.out.println("    " + " " + type + " " + id + " " + pname + " " + ploc);
                                                                    } //end of while loop
                                                                    System.out.println();

                                                                } //end switch case 1

                                                                //list assembly plants
                                                                case 2 -> {
                                                                    String sql532 = "SELECT plantID, plantName, location FROM manufacturingplant "
                                                                            + "WHERE plantType = 'Assembly'";
                                                                    rs = stmt.executeQuery(sql532);
                                                                    while (rs.next()) {
                                                                        int id = rs.getInt("plantID");
                                                                        String pname = rs.getString("plantName");
                                                                        String ploc = rs.getString("location");
                                                                        System.out.println("    " + id + " " + pname + " " + ploc);
                                                                    }
                                                                    System.out.println();

                                                                } // end switch case 2

                                                                //list all parts plants
                                                                case 3 -> {
                                                                    String sql533 = "SELECT plantID, plantName, location from manufacturingplant "
                                                                            + "WHERE plantType = 'Parts'";
                                                                    rs = stmt.executeQuery(sql533);
                                                                    while (rs.next()) {
                                                                        int id = rs.getInt("plantID");
                                                                        String pname = rs.getString("plantName");
                                                                        String ploc = rs.getString("location");
                                                                        System.out.println("    " + id + " " + pname + " " + ploc);
                                                                    }
                                                                    System.out.println();

                                                                } // end switch case 3

                                                                //list vehicles by plant
                                                                case 4 -> {

                                                                    System.out.println("""
                                                           Please choose your plant under the plant type:
                                                                Assembly: 
                                                                    1. Factory ZERO
                                                                    2. South Gate Assembly
                                                                    3. Arlington Assembly
                                                                Parts
                                                                    4. GM Customer Care
                                                                    5. West Point GM Part Center """);
                                                                    int caseFiveThreeFour = scanner.nextInt();

                                                                    //new switch
                                                                    switch (caseFiveThreeFour) {

                                                                        //Factory ZERO, Detroit, MI, Assembly
                                                                        case 1 -> {
                                                                            String sql5341 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE plantName = 'Factory ZERO'";
                                                                            rs = stmt.executeQuery(sql5341);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                String mname = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + bname + " " + mname);
                                                                            }
                                                                            System.out.println();
                                                                        } //end case 1

                                                                        //GM Customer Care, Bolingbrook, IL, Parts
                                                                        case 2 -> {
                                                                            String sql5342 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE plantName = 'South Gate Assembly'";
                                                                            rs = stmt.executeQuery(sql5342);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                String mname = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + bname + " " + mname);
                                                                            }
                                                                            System.out.println();
                                                                        } // end case 2

                                                                        //South Gate Assembly, Los Angeles, CA, Assembly
                                                                        case 3 -> {
                                                                            String sql5343 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE plantName = 'South Gate Assembly'";
                                                                            rs = stmt.executeQuery(sql5343);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                String mname = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + bname + " " + mname);
                                                                            }
                                                                            System.out.println();
                                                                        } // end case 3

                                                                        //West Point GM Part Center, Houston, TX, Parts
                                                                        case 4 -> {
                                                                            String sql5344 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE plantName = 'West Point GM Part Center'";
                                                                            rs = stmt.executeQuery(sql5344);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                String mname = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + bname + " " + mname);
                                                                            }
                                                                            System.out.println();
                                                                        } //end case 4

                                                                        //Arlington Assembly, Arlington, TX, Assembly
                                                                        case 5 -> {
                                                                            String sql5345 = "SELECT vehicle.VIN, brand.brandName, model.modelName "
                                                                                    + "FROM vehicle "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN manufacturingplant on vehicle.plantID = manufacturingplant.plantID "
                                                                                    + "WHERE plantName = 'Arlington Assembly'";
                                                                            rs = stmt.executeQuery(sql5345);
                                                                            while (rs.next()) {
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                String mname = rs.getString("modelName");
                                                                                System.out.println("    " + vin + " " + bname + " " + mname);
                                                                            }
                                                                            System.out.println();
                                                                        } //end case 5

                                                                    } //end switch case 534

                                                                } // case 54

                                                                default ->
                                                                    System.out.println("Invalid query type selected.");

                                                            } //end switch case 5-53

                                                        } //end case 5-3

                                                        //catch all for number entries with no case defined
                                                        default ->
                                                            System.out.println("Invalid query type selected.");

                                                    }//end switch case 5

                                                }//end case 5   

                                                //initial case recalls
                                                case 6 -> {
                                                    System.out.println("""
                                               Please select how to find the recalled parts:
                                                    1. By Part
                                                    2. By Location """);
                                                    int caseSix = scanner.nextInt();

                                                    //main case 6 switch starter
                                                    switch (caseSix) {

                                                        //case 1 - by part
                                                        case 1 -> {
                                                            System.out.println("""
                                                       Please select the part:
                                                            1. Paint
                                                            2. Engine
                                                            3. Transmission
                                                            4. Tires """);
                                                            int caseSixOne = scanner.nextInt();

                                                            //start switch case 61
                                                            switch (caseSixOne) {

                                                                //case 1 on poor paint quality
                                                                case 1 -> {
                                                                    System.out.println("""
                                                               What is the recall:
                                                                    1. Specific Paint Color
                                                                    2. Specific Factory Fault """);
                                                                    int caseSixOneOne = scanner.nextInt();

                                                                    //switch sixOneOne for paint colors
                                                                    switch (caseSixOneOne) {

                                                                        //list of paint colors
                                                                        case 1 -> {
                                                                            System.out.println("""
                                                                   Choose the specific color: 
                                                                         1. Red
                                                                         2. Blue
                                                                         3. Black
                                                                         4. White
                                                                         5. Green
                                                                         6. Silver
                                                                         7. Yellow
                                                                         8. Grey
                                                                         9. Purple
                                                                        10. Orange
                                                                        11. Brown
                                                                        12. Pink
                                                                        13. Gold
                                                                        14. Beige
                                                                        15. Turquoise 
                                                                       (Note: not all options match vehicles in this representation
                                                                                but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                                will have readable output, but all work. """);
                                                                            int caseSOOOne = scanner.nextInt();

                                                                            //switch set for paint colors
                                                                            switch (caseSOOOne) {

                                                                                case 1 -> {
                                                                                    String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Red'";
                                                                                    rs = stmt.executeQuery(sql6111);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end red

                                                                                //Blue
                                                                                case 2 -> {
                                                                                    String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Blue'";
                                                                                    rs = stmt.executeQuery(sql6112);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end blue

                                                                                //black
                                                                                case 3 -> {
                                                                                    String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Black'";
                                                                                    rs = stmt.executeQuery(sql6113);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end black

                                                                                //White
                                                                                case 4 -> {
                                                                                    String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'White'";
                                                                                    rs = stmt.executeQuery(sql6115);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end white

                                                                                //green
                                                                                case 5 -> {
                                                                                    String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Green'";
                                                                                    rs = stmt.executeQuery(sql6115);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end green

                                                                                //silver
                                                                                case 6 -> {
                                                                                    String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Silver'";
                                                                                    rs = stmt.executeQuery(sql6116);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end red

                                                                                //yellow
                                                                                case 7 -> {
                                                                                    String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Yellow'";
                                                                                    rs = stmt.executeQuery(sql6117);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end yellow

                                                                                //grey
                                                                                case 8 -> {
                                                                                    String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Grey'";
                                                                                    rs = stmt.executeQuery(sql6118);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end grey

                                                                                //purple
                                                                                case 9 -> {
                                                                                    String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Purple'";
                                                                                    rs = stmt.executeQuery(sql6119);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end purple

                                                                                //Orange 
                                                                                case 10 -> {
                                                                                    String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Orange'";
                                                                                    rs = stmt.executeQuery(sql61110);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end orange

                                                                                //brown
                                                                                case 11 -> {
                                                                                    String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Brown'";
                                                                                    rs = stmt.executeQuery(sql61111);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end brown

                                                                                //pink
                                                                                case 12 -> {
                                                                                    String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Pink'";
                                                                                    rs = stmt.executeQuery(sql61112);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end pink

                                                                                //Gold
                                                                                case 13 -> {
                                                                                    String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Red'";
                                                                                    rs = stmt.executeQuery(sql61113);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end gold

                                                                                //beige
                                                                                case 14 -> {
                                                                                    String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Beige'";
                                                                                    rs = stmt.executeQuery(sql61114);
                                                                                    int itemCount = 0;
                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }
                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();
                                                                                } //end beige

                                                                                //turquoise
                                                                                case 15 -> {
                                                                                    String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                            + "FROM dealer "
                                                                                            + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                                            + "JOIN options on model.option_id = options.option_id "
                                                                                            + "WHERE options.color = 'Turquoise' "
                                                                                            + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                                    rs = stmt.executeQuery(sql61115);
                                                                                    int itemCount = 0;

                                                                                    while (rs.next()) {
                                                                                        String dname = rs.getString("dealerName");
                                                                                        String vin = rs.getString("VIN");
                                                                                        String bname = rs.getString("brandName");
                                                                                        System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                        itemCount++;
                                                                                    }

                                                                                    System.out.println("    How many vehicles are affected: " + itemCount);
                                                                                    System.out.println();

                                                                                } //end turquoise

                                                                            } //end of color switch

                                                                        } //end of case 1

                                                                    } //end of switch

                                                                } //end paint case

                                                                //case 2 on engine recalls
                                                                case 2 -> {

                                                                    System.out.println("""
                                                                   Choose the specific engine: 
                                                                         1. Ford V6
                                                                         2. GM V8
                                                                         3. Tesla Electric
                                                                         4. Toyota Hybrid
                                                                         5. Cummins Diesel
                                                                         6. Honda V6
                                                                         7. Chevrolet V8
                                                                         8. Nissan Electric
                                                                         9. Hyundai Hybrid
                                                                        10. Isuzu Diesel
                                                                        11. Mitsubishi V6
                                                                        12. Chrysler V8
                                                                        13. BMW Electric
                                                                        14. Kia Hybrid
                                                                        15. Peugeot Diesel 
                                                                    (Note: all options do work, but for this database
                                                                            representation, only numbers 6, 7, 11, 12, 13, 15
                                                                            will show vehicle options. """);
                                                                    int caseSOOTwo = scanner.nextInt();

                                                                    //switch set for engines
                                                                    switch (caseSOOTwo) {

                                                                        //engine 1
                                                                        case 1 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Ford V6'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 1

                                                                        // GM V8
                                                                        case 2 -> {
                                                                            String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'GM V8'";
                                                                            rs = stmt.executeQuery(sql6112);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end blue

                                                                        //Tesla Electric
                                                                        case 3 -> {
                                                                            String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Tesla Electric'";
                                                                            rs = stmt.executeQuery(sql6113);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end black

                                                                        //Toyota Hybrid
                                                                        case 4 -> {
                                                                            String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Toyota Hybrid'";
                                                                            rs = stmt.executeQuery(sql6115);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end white

                                                                        //Cummins Diesel
                                                                        case 5 -> {
                                                                            String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Cummins Diesel'";
                                                                            rs = stmt.executeQuery(sql6115);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end 

                                                                        // Honda V6
                                                                        case 6 -> {
                                                                            String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Honda V6'";
                                                                            rs = stmt.executeQuery(sql6116);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 6

                                                                        //chevy v8
                                                                        case 7 -> {
                                                                            String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Chevrolet V8'";
                                                                            rs = stmt.executeQuery(sql6117);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 7

                                                                        // nissan electric
                                                                        case 8 -> {
                                                                            String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Nissan Electric'";
                                                                            rs = stmt.executeQuery(sql6118);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 8

                                                                        //Hyundai Hybrid
                                                                        case 9 -> {
                                                                            String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Hyundai Hybrid'";
                                                                            rs = stmt.executeQuery(sql6119);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 9

                                                                        // Isuzu Diesel
                                                                        case 10 -> {
                                                                            String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Isuzu Diesel'";
                                                                            rs = stmt.executeQuery(sql61110);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 10

                                                                        //Mitsubishi v6
                                                                        case 11 -> {
                                                                            String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Mitsubishi V6'";
                                                                            rs = stmt.executeQuery(sql61111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 11

                                                                        //Chrysler V8
                                                                        case 12 -> {
                                                                            String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Chrysler V8'";
                                                                            rs = stmt.executeQuery(sql61112);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 12

                                                                        // BMW electric
                                                                        case 13 -> {
                                                                            String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'BMW Electric'";
                                                                            rs = stmt.executeQuery(sql61113);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 13

                                                                        //kia hybrid
                                                                        case 14 -> {
                                                                            String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Kia Hybrid'";
                                                                            rs = stmt.executeQuery(sql61114);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 14

                                                                        //peugeot diesel
                                                                        case 15 -> {
                                                                            String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.engine = 'Peugeot Diesel' "
                                                                                    + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                            rs = stmt.executeQuery(sql61115);
                                                                            int itemCount = 0;

                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }

                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();

                                                                        } //end case 15

                                                                    } //end of engine switch

                                                                } //end of case 2

                                                                //case 3 on transmission
                                                                case 3 -> {
                                                                    System.out.println("""
                                                                   Choose the specific color: 
                                                                         1. Aisin Automatic
                                                                         2. Getrag Manual
                                                                         3. BorgWarner Automatic
                                                                         4. Jatco CVT
                                                                         5. Allison Automatic
                                                                         6. ZF Manual
                                                                         7. Magna Automatic
                                                                         8. Ricardo Manual
                                                                         9. Hyundai Transys CVT
                                                                        10. Valeo Manual
                                                                        11. Mitsubishi Automatic
                                                                        12. Chryslter Manual
                                                                        13. Continental Automatic
                                                                        14. Hyundai WIA CVT
                                                                        15. PSA Automatic
                                                                   (Note: not all options match vehicles in this representation
                                                                          but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                          will have readable output, but all work.""");
                                                                    int caseSOOThree = scanner.nextInt();

                                                                    //switch set for paint colors
                                                                    switch (caseSOOThree) {

                                                                        //Aisin
                                                                        case 1 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Aisin Automatic'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 1

                                                                        //Getrag
                                                                        case 2 -> {
                                                                            String sql6112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Getrag Manual'";
                                                                            rs = stmt.executeQuery(sql6112);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 2

                                                                        //BorgWarner
                                                                        case 3 -> {
                                                                            String sql6113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'BorgWarner Automatic'";
                                                                            rs = stmt.executeQuery(sql6113);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 3

                                                                        //Jatco
                                                                        case 4 -> {
                                                                            String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Jatco CVT'";
                                                                            rs = stmt.executeQuery(sql6115);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 4

                                                                        //Allison
                                                                        case 5 -> {
                                                                            String sql6115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.color = 'Allison Automatic'";
                                                                            rs = stmt.executeQuery(sql6115);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 5

                                                                        //ZF
                                                                        case 6 -> {
                                                                            String sql6116 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'ZF Manual'";
                                                                            rs = stmt.executeQuery(sql6116);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 6

                                                                        //Magna
                                                                        case 7 -> {
                                                                            String sql6117 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Magna Automatic'";
                                                                            rs = stmt.executeQuery(sql6117);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 7

                                                                        //Ricardo
                                                                        case 8 -> {
                                                                            String sql6118 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Ricardo Manual'";
                                                                            rs = stmt.executeQuery(sql6118);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 8

                                                                        //Hyundai
                                                                        case 9 -> {
                                                                            String sql6119 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Hyundai Transys CVT'";
                                                                            rs = stmt.executeQuery(sql6119);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 9

                                                                        //Valeo 
                                                                        case 10 -> {
                                                                            String sql61110 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Valeo Manual'";
                                                                            rs = stmt.executeQuery(sql61110);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 10

                                                                        //Mitsubishi
                                                                        case 11 -> {
                                                                            String sql61111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Mitsubishi Automatic'";
                                                                            rs = stmt.executeQuery(sql61111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 11

                                                                        //Chrysler
                                                                        case 12 -> {
                                                                            String sql61112 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Chrysler Manual'";
                                                                            rs = stmt.executeQuery(sql61112);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 12

                                                                        //Continental
                                                                        case 13 -> {
                                                                            String sql61113 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Continental Automatic'";
                                                                            rs = stmt.executeQuery(sql61113);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 13

                                                                        //Hyundai
                                                                        case 14 -> {
                                                                            String sql61114 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'Hyundai WIA CVT'";
                                                                            rs = stmt.executeQuery(sql61114);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 14

                                                                        //PSA
                                                                        case 15 -> {
                                                                            String sql61115 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "WHERE options.transmission = 'PSA Automatic' "
                                                                                    + "GROUP BY dealer.dealerName, vehicle.VIN, brand.brandName";
                                                                            rs = stmt.executeQuery(sql61115);
                                                                            int itemCount = 0;

                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }

                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();

                                                                        } //end case 15

                                                                    } //end of switch

                                                                } // end transmission case

                                                                //case 4 on tires
                                                                case 4 -> {
                                                                    System.out.println("""
                                                                   Choose the specific tire brand: 
                                                                         1. Michelin
                                                                         2. Bridgestone
                                                                         3. Goodyear
                                                                         4. Continental
                                                                         5. Yokohama
                                                                         6. Pirelli
                                                                         7. Hankook
                                                                         8. Dunlop
                                                                         9. Falken
                                                                        10. Kumho
                                                                        11. BFGoodrich
                                                                        12. Nitto
                                                                        13. Cooper
                                                                        14. Toyo
                                                                        15. General 
                                                                  (Note: not all options match vehicles in this representation
                                                                         but all are in the database. Only 6, 7, 11, 12, 13, 15
                                                                         will have readable output, but all work.""");
                                                                    int caseSOOFour = scanner.nextInt();

                                                                    //switch set for paint colors
                                                                    switch (caseSOOFour) {

                                                                        //Michelin
                                                                        case 1 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Michelin'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 1

                                                                        //Bridgestone
                                                                        case 2 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Bridgestone'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 2

                                                                        //Goodyear
                                                                        case 3 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Goodyear'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 3

                                                                        //Continental
                                                                        case 4 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Continental'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 4

                                                                        //Yokohama
                                                                        case 5 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Yokohama'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 5

                                                                        //Pirelli
                                                                        case 6 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Pirelli'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 6

                                                                        //Hankook
                                                                        case 7 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Hankook'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 7

                                                                        //Dunlop
                                                                        case 8 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Dunlop'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 8

                                                                        //Falken
                                                                        case 9 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Falken'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 9

                                                                        //Kumho
                                                                        case 10 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Kumho'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 10

                                                                        //BFGoodrich
                                                                        case 11 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'BFGoodrich'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 11

                                                                        //Nitto
                                                                        case 12 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Nitto'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 12

                                                                        //Cooper
                                                                        case 13 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Cooper'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 13

                                                                        //Toyo
                                                                        case 14 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'Toyo'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 14

                                                                        //General
                                                                        case 15 -> {
                                                                            String sql6111 = "SELECT dealer.dealerName , vehicle.VIN, brand.brandName "
                                                                                    + "FROM dealer "
                                                                                    + "JOIN vehicle on vehicle.dealerID = dealer.dealerID "
                                                                                    + "JOIN model on vehicle.modelID = model.modelID "
                                                                                    + "JOIN brand on brand.brandID = model.brandID "
                                                                                    + "JOIN options on model.option_id = options.option_id "
                                                                                    + "JOIN tire on tire.tire_id = options.tire_id "
                                                                                    + "WHERE tire.tire_brand_name = 'General'";
                                                                            rs = stmt.executeQuery(sql6111);
                                                                            int itemCount = 0;
                                                                            while (rs.next()) {
                                                                                String dname = rs.getString("dealerName");
                                                                                String vin = rs.getString("VIN");
                                                                                String bname = rs.getString("brandName");
                                                                                System.out.println("     " + dname + " " + vin + " " + bname);
                                                                                itemCount++;
                                                                            }
                                                                            System.out.println("    How many vehicles are affected: " + itemCount);
                                                                            System.out.println();
                                                                        } //end case 15

                                                                    } //end of switch

                                                                } // end tire case

                                                            } //end switch case 61

                                                        } //end of case 1

                                                        //case 2 - by location
                                                        case 2 -> {

                                                            System.out.println("""
                                                       Which location is affected: 
                                                            1. Factory ZERO in Detroit, MI
                                                            2. GM Customer Care in Bolingbrook, IL
                                                            3. SouthGate Assembly in Los Angeles, CA
                                                            4. West Point GM Part Center in Houston, TX
                                                            5. Arlington Assembly in Arlington, TX """);
                                                            int caseFinal = scanner.nextInt();

                                                            switch (caseFinal) {

                                                                //factory zero
                                                                case 1 -> {

                                                                    String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                            + "FROM vehicle "
                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                            + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                            + "WHERE plantName = 'Factory ZERO'";
                                                                    rs = stmt.executeQuery(sqlFinal);
                                                                    while (rs.next()) {
                                                                        String vin = rs.getString("VIN");
                                                                        String bname = rs.getString("brandName");
                                                                        String mname = rs.getString("modelName");
                                                                        String bstyle = rs.getString("bodyStyle");
                                                                        System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                    }
                                                                    System.out.println();

                                                                } //end factory zero

                                                                //GM Customer Care
                                                                case 2 -> {

                                                                    String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                            + "FROM vehicle "
                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                            + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                            + "WHERE plantName = 'GM Customer Care'";
                                                                    rs = stmt.executeQuery(sqlFinal);
                                                                    while (rs.next()) {
                                                                        String vin = rs.getString("VIN");
                                                                        String bname = rs.getString("brandName");
                                                                        String mname = rs.getString("modelName");
                                                                        String bstyle = rs.getString("bodyStyle");
                                                                        System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                    }
                                                                    System.out.println();

                                                                } //end GM Customer Care

                                                                //South Gate Assembly
                                                                case 3 -> {

                                                                    String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                            + "FROM vehicle "
                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                            + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                            + "WHERE plantName = 'South Gate Assembly'";
                                                                    rs = stmt.executeQuery(sqlFinal);
                                                                    while (rs.next()) {
                                                                        String vin = rs.getString("VIN");
                                                                        String bname = rs.getString("brandName");
                                                                        String mname = rs.getString("modelName");
                                                                        String bstyle = rs.getString("bodyStyle");
                                                                        System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                    }
                                                                    System.out.println();

                                                                } //end south gate assembly

                                                                //West Point GM Part Center
                                                                case 4 -> {

                                                                    String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                            + "FROM vehicle "
                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                            + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                            + "WHERE plantName = 'West Point GM Part Center'";
                                                                    rs = stmt.executeQuery(sqlFinal);
                                                                    while (rs.next()) {
                                                                        String vin = rs.getString("VIN");
                                                                        String bname = rs.getString("brandName");
                                                                        String mname = rs.getString("modelName");
                                                                        String bstyle = rs.getString("bodyStyle");
                                                                        System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                    }
                                                                    System.out.println();

                                                                } //end case 4 - west point GM part center

                                                                //Arlington Assembly
                                                                case 5 -> {

                                                                    String sqlFinal = "SELECT vehicle.VIN, brand.brandName, model.modelName, model.bodyStyle "
                                                                            + "FROM vehicle "
                                                                            + "JOIN model on vehicle.modelID = model.modelID "
                                                                            + "JOIN brand on brand.brandID = model.brandID "
                                                                            + "JOIN manufacturingplant on manufacturingplant.plantID = vehicle.plantID "
                                                                            + "WHERE plantName = 'Arlington Assembly'";
                                                                    rs = stmt.executeQuery(sqlFinal);
                                                                    while (rs.next()) {
                                                                        String vin = rs.getString("VIN");
                                                                        String bname = rs.getString("brandName");
                                                                        String mname = rs.getString("modelName");
                                                                        String bstyle = rs.getString("bodyStyle");
                                                                        System.out.println("    " + " " + vin + " " + bname + " " + mname + " " + bstyle);
                                                                    } //end loop line 2699
                                                                    System.out.println();

                                                                } //end case 5 - arlington assembly line 2690

                                                            } //end caseFinal switch line 2603

                                                        } //end of case 2 line 2592

                                                    } //end of caseSix switch line 1086

                                                } //end of manager case 6 switch line 1078

                                                // catch all for entries with no case
                                                default ->
                                                    System.out.println("Invalid query type selected.");

                                            } //end queryType switch line 105

                                        } catch (AccessDeniedException e) {
                                            System.out.println(e.getMessage());
                                        }

                                    } else {  //input catcch for line 102
                                        System.out.println("Invalid query type selected.");
                                    }

                                } //end case manager_role while loop line 85

                            } //end case customer_role

                            default -> {
                                System.out.println("Role not found. Please try again.");
                                break;
                            }
                        } //end role switch for line 80
                        //end of original if statement testing the user log in
                    } else {
                        //catch for invalid user input
                        System.out.println("User does not exist. Please try again.");

                    }


                    //end of log in loop
                } else if (login == 2) {
                    System.out.println("Exiting program.");
                    System.exit(0); //terminates the program
                } else {
                    System.out.println("Invalid entry");
                } // end log in switch loop

            } //end of log in while loop
 
        } catch (InputMismatchException e) {
            System.out.println("Invalid input.");

        } catch (SQLException se) {
            // Print SQL error details
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Thank you for testing the current code!");

        } //end of finally
    } //end of main
} // end public class AutoDealer
