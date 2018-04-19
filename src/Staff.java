import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Staff extends Console{
    Connection mySQLDB;

    public Staff(Connection mySQLDB) {
        this.mySQLDB = mySQLDB;
        this.user = "spacecraft rental staff";
        this.options = new String[]{"1. Rent a spacecraft",
                "2. Return a spacecraft",
                "3. List all spacecraft currently rented out (on a mission) for a certain period",
                "4. List the number of spacecrafts currently rented out out by each Agency (in alphabetical order)"};
    }

    public void menu() {
        Set<Integer> valid = new HashSet(Arrays.asList(0,1,2,3,4));
        printMenu();
        System.out.println("Enter Your Choice: ");
        int answer = getAnswer(valid);

        switch (answer) {
            case 0:
                return;
            case 1:
                rentSpaceCraft();
                break;
            case 2:
                returnSpaceCraft();
                break;
            case 3:
                listAllRentedSpaceCrafts();
                break;
            case 4:
                listNumberOfRentedSpaceCraftPerAgency();
                break;
        }
    }

    public void rentSpaceCraft() {
        String agencyName = getAnswer("Enter the space agency name: ");
        String MID = getAnswer("Enter the MID: ");
        String SNum = getAnswer("Enter the SNum: ");

        try {
            Statement rentStatement = mySQLDB.createStatement();
            ResultSet results;

            if (!checkIfShipExists(agencyName, MID, SNum, rentStatement)) return;

            results = rentStatement.executeQuery("select *\n" +
                    "from rentalRecord\n" +
                    "where mid = "+MID+" and snum = "+SNum+" and agency = '"+agencyName+"'"+" and returnDate is NULL;");

            // Check if the result set is empty
            if (results.isBeforeFirst() ) {
                System.out.println("Rental not possible because the spacecraft has not yet been returned.");
                return;
            }
            SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
            String dateString = formatter.format(new Date());
            rentStatement.execute("update rentalRecord " +
                    "set checkoutDate = '"+dateString+"', returnDate = NULL "+
                    "where mid = '"+MID+"' and snum = "+SNum+" and agency = '"+agencyName+"';");
            System.out.println("Spacecraft rented successfully!");
        }
        catch (SQLException e) {
            System.out.println(e);
        }
    }

    private boolean checkIfShipExists(String agencyName, String MID, String SNum, Statement rentStatement) throws SQLException {
        ResultSet results = rentStatement.executeQuery("select *\n" +
                "from rentalRecord\n" +
                "where mid = "+MID+" and snum = "+SNum+" and agency = '"+agencyName+"';");

        // Check if the result set is empty
        if (!results.isBeforeFirst() ) {
            return false;
        }
        return true;
    }

    public void returnSpaceCraft() {
        String agencyName = getAnswer("Enter the space agency name: ");
        String MID = getAnswer("Enter the MID: ");
        String SNum = getAnswer("Enter the SNum: ");
        try {
            Statement rentStatement = mySQLDB.createStatement();
            ResultSet resultSet;

            if (!checkIfShipExists(agencyName, MID, SNum, rentStatement)) {
                System.out.println("Rental not possible because the spacecraft is not found.");
                return;
            }

            resultSet = rentStatement.executeQuery("select *\n" +
                    "from rentalRecord\n" +
                    "where mid = "+MID+" and snum = "+SNum+" and agency = '"+agencyName+"';");

            // Check if the ship has been rented (result set should not be empty)
            if (!resultSet.isBeforeFirst() ) {
                System.out.println("Return not possible because the spacecraft was not rented out.");
                return;
            }

            //Update the return date
            SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd");
            String dateString = formatter.format(new Date());
            rentStatement.execute("update rentalRecord " +
                    "set returnDate = '"+dateString+"' "+
                    "where mid = '"+MID+"' and snum = "+SNum+" and agency = '"+agencyName+"';");
            System.out.println("Spacecraft rented successfully!");

        } catch (SQLException e) {
            System.out.println(e);
        }
        System.out.println("Spacecraft returned successfully!");
    }

    public void listAllRentedSpaceCrafts() {
        String startDate = getAnswer("Enter the start date [DD-MM-YYYY]: ");
        String endDate = getAnswer("Enter the end date [DD-MM-YYYY]: ");
        try {
            Statement rentStatement = mySQLDB.createStatement();
            ResultSet resultSet = rentStatement.executeQuery("select agency, mid, snum, checkoutDate " +
                    "from rentalRecord " +
                    "where checkoutDate >= '"+startDate+"' and checkoutDate <= '"+endDate+"' and returnDate is NULL " +
                    "order by checkoutDate desc;");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void listNumberOfRentedSpaceCraftPerAgency() {
        try {
            Statement rentStatement = mySQLDB.createStatement();
            ResultSet resultSet = rentStatement.executeQuery("select agency as Agency, count(*) as Number " +
                    "from rentalRecord " +
                    "where returnDate is NULL " +
                    "group by agency;");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}

