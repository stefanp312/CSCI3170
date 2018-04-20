import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Company extends Console {
    Connection mySQLDB;

    public Company(Connection mySQLDB) {
        this.mySQLDB = mySQLDB;
        this.user = "exploration companies (rental customers)";
        this.options = new String[]{"1. Search for NEAs based on some criteria",
                "2. Search for spacecrafts based on some criteria",
                "3. A certain NEA exploration mission design",
                "4. The most beneficial NEA exploration mission design",
                "0. Return to the main menu"};
    }

    private void searchNEAs() {
        try {
            String ans = null, keyword = null, criterion = null;
            String searchSQL = "";
            PreparedStatement stmt = null;

            searchSQL += "SELECT nea.nid, nea.distance, nea.family, nea.duration, nea.energy, nea.Rtype, nea.Rtype ";
            // I use Near-Earth Asteroids to refer to the first table, you may change it based on your definition.
            searchSQL += "FROM nea ";
            searchSQL += "WHERE";

            while(true){
                System.out.println("Choose the Search criterion:");
                System.out.println("1. ID");
                System.out.println("2. Family");
                System.out.println("3. Resource type");
                System.out.print("Choose the search criterion: ");
                ans = main.input.nextLine();
                if(ans.equals("1")||ans.equals("2")||ans.equals("3")) break;
                else{
                    System.out.println("Invalid Input, please try again!");
                }
            }
            criterion = ans;
            while(true){
                System.out.print("Type in the search keyword:");
                ans = main.input.nextLine();
                if(!ans.isEmpty()) break;
            }
            keyword = ans;

            if(criterion.equals("1")){
                searchSQL += " nea.nid = ? ;";
                stmt = mySQLDB.prepareStatement(searchSQL);
                stmt.setString(1, keyword);
            }else if(criterion.equals("2")){
                searchSQL += " nea.family LIKE ? ";
                stmt = mySQLDB.prepareStatement(searchSQL);
                stmt.setString(1, "%" + keyword + "%");
            }else if(criterion.equals("3")){
                searchSQL += " nea.Rtype LIKE ? ";
                stmt = mySQLDB.prepareStatement(searchSQL);
                stmt.setString(1, "%" + keyword + "%");
            }

            String[] field_name = {"ID", "Distance", "Family", "Duration", "Energy", "Resources"};
            for (int i = 0; i < 6; i++){
                System.out.print("| " + field_name[i] + " ");
            }
            System.out.println("|");

            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()){
                for (int i = 1; i <= 6; i++){
                    System.out.print("| " + resultSet.getString(i) + " ");
                }
                System.out.println("|");
            }
            System.out.println("End of Query");
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void searchSCs() {
        try {
            String ans = null, keyword = null, criterion = null;
            String searchSQL = "";
            //Int energy, workingTime, capacity;
            PreparedStatement stmt = null;

            searchSQL += "SELECT SC.agency, SC.mid, RR.snum, SC.type, SC.energy, SC.duration, SC.capacity, SC.charge ";
            //I refer to the third table by SpaceAgenciesSpacecrafts, you may change it based on your definition.
            //I refer to the forth table by Table4
            searchSQL += "FROM spacecraftModel SC, rentalRecord RR ";
            searchSQL += "WHERE SC.mid = RR.mid AND SC.agency = RR.agency";

            while(true){
                System.out.println("Choose the Search criterion:");
                System.out.println("1. Agency Name");
                System.out.println("2. Type");
                System.out.println("3. Least energy [km/s]");
                System.out.println("4. Least working time [days]");
                System.out.println("5. Least capacity [m^s]");
                System.out.print("Choose the search criterion: ");
                ans = main.input.nextLine();
                if(ans.equals("1")||ans.equals("2")||ans.equals("3")||ans.equals("4")||ans.equals("5")) break;
                else{
                    System.out.println("Invalid Input, please try again!");
                }
            }
            criterion = ans;

            while(true){
                System.out.print("Type in the search keyword:");
                ans = main.input.nextLine();
                if(!ans.isEmpty()) break;
            }
            keyword = ans;

            if(criterion.equals("1")){
                searchSQL += " AND SC.agency = ? ";
            }else if(criterion.equals("2")){
                searchSQL += " AND SC.type = ? ";
            }else if(criterion.equals("3")){
                searchSQL += " AND SC.energy >= ? ";
            }else if(criterion.equals("4")){
                searchSQL += " AND SC.duration >= ? ";
            }else if(criterion.equals("5")){
                searchSQL += " AND SC.capacity >= ? ";
            }
            stmt = mySQLDB.prepareStatement(searchSQL);
            stmt.setString(1, keyword);

            String[] field_name = {"Agency", "MID", "SNum", "Type", "Energy", "T", "Capacity", "Charge"};
            for (int i = 0; i < 8; i++){
                System.out.print("| " + field_name[i] + " ");
            }
            System.out.println("|");

            ResultSet resultSet = stmt.executeQuery();
            while(resultSet.next()){
                for (int i = 1; i <= 8; i++){
                    System.out.print("| " + resultSet.getString(i) + " ");
                }
                System.out.println("|");
            }
            System.out.println("End of Query");
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void searchNEAmission() {
        try {
            String ans = null, nid = null;
            String searchSQL = "";
            PreparedStatement stmt = null;

            searchSQL += "SELECT S.agency, S.mid, rentalRecord.snum, S.charge * nea.duration AS 'Cost', R.value * R.density * S.capacity * 100 * 100 * 100 - S.charge * nea.duration AS 'Benefit'";
            searchSQL += "FROM nea LEFT JOIN resource R ON nea.Rtype = R.Rtype, ";
            searchSQL += "spacecraftModel S LEFT JOIN rentalRecord ON S.agency = rentalRecord.agency AND S.mid = rentalRecord.mid ";
            searchSQL += "WHERE S.type = 'A' ";
            searchSQL += "AND S.energy > nea.energy ";
            searchSQL += "AND S.duration > nea.duration ";
            searchSQL += "AND nea.Rtype IS NOT NULL ";
            searchSQL += "AND rentalRecord.returnDate IS NOT NULL ";
            searchSQL += "AND nea.nid = ? ";
            searchSQL += "ORDER BY Benefit DESC";

            while (true) {
                System.out.print("Type in the NEA ID: ");
                ans = main.input.nextLine();
                if (!ans.isEmpty()) break;
            }
            nid = ans;

            stmt = mySQLDB.prepareStatement(searchSQL);
            stmt.setString(1, nid);

            ResultSet resultSet = stmt.executeQuery();
            if (!resultSet.next()) {
                System.out.println("No result returned based on the given condition!");
            } else {
                String[] field_name = {"Agency", "MID", "SNum", "Cost", "Benefit"};
                System.out.print(String.format("| %6s ", field_name[0]));  //Agency
                System.out.print(String.format("| %4s ", field_name[1]));  //MID
                System.out.print(String.format("| %4s ", field_name[2]));  //SNum
                System.out.print(String.format("| %10s ", field_name[3])); //Cost
                System.out.print(String.format("| %13s ", field_name[4])); //Benefit
                // for (int i = 0; i < 5; i++){
                // 	 System.out.print(String.format("| %8s ", field_name[i]));
                // }
                System.out.println("|");
                do {
                    System.out.print(String.format("| %6s ", resultSet.getString(1)));  //Agency
                    System.out.print(String.format("| %4s ", resultSet.getString(2)));  //MID
                    System.out.print(String.format("| %4s ", resultSet.getString(3)));  //SNum
                    System.out.print(String.format("| %10s ", resultSet.getString(4))); //Cost
                    System.out.print(String.format("| %13s ", resultSet.getString(5))); //Benefit
                    // for (int i = 1; i <= 8; i++){
                    // 	System.out.print(String.format("| %8s ", resultSet.getString(i)));
                    // }
                    System.out.println("|");
                } while (resultSet.next());

                System.out.println("End of Query");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    private void searchBestMission() {
        try {
            String ans = null, budget = null, resource = null;
            String searchSQL = "";
            PreparedStatement stmt = null;

            searchSQL += "SELECT nea.nid, nea.family, S.agency, S.mid, rentalRecord.snum, nea.duration, ";
            searchSQL += "S.charge * nea.duration AS 'Cost',  R.value * R.density * S.capacity * 100 * 100 * 100 - S.charge * nea.duration AS 'Benefit' ";
            searchSQL += "FROM nea LEFT JOIN resource R ON nea.Rtype = R.Rtype, ";
            searchSQL += "spacecraftModel S LEFT JOIN rentalRecord ON S.agency = rentalRecord.agency AND S.mid = rentalRecord.mid ";
            searchSQL += "WHERE S.type = 'A' ";
            searchSQL += "AND S.energy > nea.energy ";
            searchSQL += "AND S.duration > nea.duration ";
            searchSQL += "AND rentalRecord.returnDate IS NOT NULL ";
            searchSQL += "AND nea.Rtype = ? ";
            searchSQL += "AND S.charge * nea.duration <= ? ";
            searchSQL += "ORDER BY Benefit DESC ";
            searchSQL += "LIMIT 0, 1";

            while(true){
                System.out.print("Type in your budget [$]: ");
                ans = main.input.nextLine();
                if(!ans.isEmpty()) break;
            }
            budget = ans;
            while(true){
                System.out.print("Type in the source type: ");
                ans = main.input.nextLine();
                if(!ans.isEmpty()) break;
            }
            resource = ans;

            stmt = mySQLDB.prepareStatement(searchSQL);
            stmt.setString(1, resource);
            stmt.setDouble(2, Double.parseDouble(budget));

            ResultSet resultSet = stmt.executeQuery();
            if(!resultSet.next()){
                System.out.println("No result returned based on the given condition!");
            }
            else{
                String[] field_name = {"NEA ID", "Family", "Agency", "MID", "SNum", "Duration", "Cost", "Benefit"};
                System.out.print(String.format("|%10s", field_name[0]));  //NEA ID
                System.out.print(String.format("|%6s", field_name[1]));  //Family
                System.out.print(String.format("|%6s", field_name[2]));  //Agency
                System.out.print(String.format("|%4s", field_name[3]));  //MID
                System.out.print(String.format("|%4s", field_name[4]));  //SNum
                System.out.print(String.format("|%7s", field_name[5])); //Duration
                System.out.print(String.format("|%10s", field_name[6])); //Cost
                System.out.print(String.format("|%7s", field_name[7])); //Benefit
                // for (int i = 0; i < 5; i++){
                // 	 System.out.print(String.format("| %8s ", field_name[i]));
                // }
                System.out.println("|");
                do{
                    System.out.print(String.format("|%10s", resultSet.getString(1)));  //NEA ID
                    System.out.print(String.format("|%6s", resultSet.getString(2)));  //Family
                    System.out.print(String.format("|%6s", resultSet.getString(3)));  //Agency
                    System.out.print(String.format("|%4s", resultSet.getString(4)));  //MID
                    System.out.print(String.format("|%4s", resultSet.getString(5)));  //SNum
                    System.out.print(String.format("|%8s", resultSet.getString(6))); //Duration
                    System.out.print(String.format("|%10s", resultSet.getString(7))); //Cost
                    System.out.print(String.format("|%20s", resultSet.getString(8))); //Benefit
                    // for (int i = 1; i <= 8; i++){
                    // 	System.out.print(String.format("| %8s ", resultSet.getString(i)));
                    // }
                    System.out.println("|");
                } while(resultSet.next());

                System.out.println("End of Query");
            }
            resultSet.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void menu() {
        Set<Integer> valid = new HashSet(Arrays.asList(0,1,2,3,4));
        printMenu();
        System.out.println("Enter Your Choice: ");
        int answer = getAnswer(valid);

        switch (answer) {
            case 1:
                searchNEAs();
                break;
            case 2:
                searchSCs();
                break;
            case 3:
                searchNEAmission();
                break;
            case 4:
                searchBestMission();
                break;
            case 0:
                break;
        }
    }
}
