import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Staff extends Console{
    Connection mySQLDB;
    String description = "spacecraft rental staff";
    String[] options = {"1. Rent a spacecraft",
            "2. Return a spacecraft",
            "3. List all spacecraft currently rented out (on a mission) for a certain period",
            "4. List the number of spacecrafts currently rented out out by each Agency (in alphabetical order)"};


    public Staff(Connection mySQLDB) {
        this.mySQLDB = mySQLDB;
    }

    public void menu() {
        Set<Integer> valid = new HashSet(Arrays.asList(0,1,2,3,4));
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
            ResultSet results = rentStatement.executeQuery("select * from rentalRecord");
            while (results.next()) {
                System.out.println(results.getString("lol"));
            }
        }
        catch (SQLException e) {
            System.out.println(e);
        }

        System.out.println("Spacecraft rented successfully!");
    }

    public void returnSpaceCraft() {
        String agencyName = getAnswer("Enter the space agency name: ");
        String MID = getAnswer("Enter the MID: ");
        String SNum = getAnswer("Enter the SNum: ");

        System.out.println("Spacecraft return successfully!");
    }

    public  void listAllRentedSpaceCrafts() {
        String startDate = getAnswer("Enter the start date [DD-MM-YYYY]: ");
        String endDate = getAnswer("Enter the end date [DD-MM-YYYY]: ");
    }

    public  void listNumberOfRentedSpaceCraftPerAgency() {

    }
}

