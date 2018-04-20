import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Admin extends Console {
    Connection mySQLDB;

    public Admin(Connection mySQLDB) {
        this.mySQLDB = mySQLDB;
        this.user = "administrator";
        this.options = new String[]{"1. Create all tables",
                "2. Delete all tables",
                "3. Load data from a data set",
                "4. Return to the main menu"};
    }

    public void createTables() {
        try {
            String neaSQL = "CREATE TABLE nea(";
            neaSQL += "nid VARCHAR(10) NOT NULL,";
            neaSQL += "distance FLOAT(2) NOT NULL,";
            neaSQL += "family VARCHAR(6) NOT NULL,";
            neaSQL += "duration INT(3) NOT NULL,";
            neaSQL += "energy FLOAT(2) NOT NULL,";
            neaSQL += "PRIMARY KEY (nid))";

            String resourceSQL = "CREATE TABLE resource(";
            resourceSQL += "Rtype VARCHAR(2) NOT NULL,";
            resourceSQL += "density FLOAT(2) NOT NULL,";
            resourceSQL += "value FLOAT(2) NOT NULL,";
            resourceSQL += "PRIMARY KEY (Rtype))";

            String containSQL = "CREATE TABLE contain(";
            containSQL += "nid VARCHAR(10) NOT NULL,";
            containSQL += "Rtype VARCHAR(2) NOT NULL,";
            containSQL += "PRIMARY KEY (nid),";
            containSQL += "FOREIGN KEY (nid) REFERENCES nea(nid) ON DELETE CASCADE ON UPDATE NO ACTION,";
            containSQL += "FOREIGN KEY (Rtype) REFERENCES resource(Rtype) ON DELETE CASCADE ON UPDATE NO ACTION)";

            String spacecraftModelSQL = "CREATE TABLE spacecraftModel(";
            spacecraftModelSQL += "agency VARCHAR(4) NOT NULL,";
            spacecraftModelSQL += "mid VARCHAR(4) NOT NULL,";
            spacecraftModelSQL += "num INT(2) NOT NULL,";
            spacecraftModelSQL += "charge INT(5) NOT NULL,";
            spacecraftModelSQL += "duration INT(3) NOT NULL,";
            spacecraftModelSQL += "energy FLOAT(2) NOT NULL,";
            spacecraftModelSQL += "PRIMARY KEY (agency, mid))";

            String aModelSQL = "CREATE TABLE aModel(";
            aModelSQL += "agency VARCHAR(4) NOT NULL,";
            aModelSQL += "mid VARCHAR(4) NOT NULL,";
            aModelSQL += "num INT(2) NOT NULL,";
            aModelSQL += "charge INT(5) NOT NULL,";
            aModelSQL += "duration INT(3) NOT NULL,";
            aModelSQL += "energy FLOAT(2) NOT NULL,";
            aModelSQL += "capacity INT(2) NOT NULL,";
            aModelSQL += "PRIMARY KEY (agency, mid),";
            aModelSQL += "FOREIGN KEY (agency, mid) REFERENCES spacecraftModel(agency, mid) ON DELETE CASCADE ON UPDATE NO ACTION)";

            //aModelSQL += "FOREIGN KEY (mid) REFERENCES spacecraftModel(mid),";
            //aModelSQL += "FOREIGN KEY (num) REFERENCES spacecraftModel(num),";
            //aModelSQL += "FOREIGN KEY (charge) REFERENCES spacecraftModel(charge),";
            //aModelSQL += "FOREIGN KEY (duration) REFERENCES spacecraftModel(duration),";
            //aModelSQL += "FOREIGN KEY (energy) REFERENCES spacecraftModel(energy))";

            String rentalRecordSQL = "CREATE TABLE rentalRecord(";
            rentalRecordSQL += "agency VARCHAR(4) NOT NULL,";
            rentalRecordSQL += "mid VARCHAR(4) NOT NULL,";
            rentalRecordSQL += "snum INT(2) NOT NULL,";
            rentalRecordSQL += "checkoutDate DATE NOT NULL,";
            rentalRecordSQL += "returnDate DATE,";
            rentalRecordSQL += "PRIMARY KEY (agency, mid, snum),";
            rentalRecordSQL += "FOREIGN KEY (agency,mid) REFERENCES spacecraftModel(agency,mid) ON DELETE CASCADE ON UPDATE NO ACTION)";

            Statement stmt = mySQLDB.createStatement();
            System.out.println("Processing...");

            System.err.println("Creating nea Table.");
            stmt.execute(neaSQL);
            System.err.println("Creating resource Table.");
            stmt.execute(resourceSQL);
            System.err.println("Creating contain Table.");
            stmt.execute(containSQL);
            System.err.println("Creating spacecraftModel Table.");
            stmt.execute(spacecraftModelSQL);
            System.err.println("Creating aModel Table.");
            stmt.execute(aModelSQL);
            System.err.println("Creating rentalRecord Table.");
            stmt.execute(rentalRecordSQL);

            System.out.println("Done! Database is initialized!");
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteTables() {
        try {
            Statement stmt = mySQLDB.createStatement();
            System.out.print("Processing...");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("DROP TABLE IF EXISTS nea ");
            stmt.execute("DROP TABLE IF EXISTS contain");
            stmt.execute("DROP TABLE IF EXISTS resource");
            stmt.execute("DROP TABLE IF EXISTS spacecraftModel");
            stmt.execute("DROP TABLE IF EXISTS aModel");
            stmt.execute("DROP TABLE IF EXISTS rentalRecord");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
            System.out.println("Done! Database is removed!");
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void loadTables() {
        String neaSQL = "INSERT INTO nea (nid, distance, family, duration, energy) VALUES (?,?,?,?,?)";
        String containSQL = "INSERT INTO contain (nid, Rtype) VALUES (?,?)";
        String resourceSQL = "INSERT INTO resource (Rtype, density, value) VALUES (?,?,?)";
        String spacecraftModelSQL = "INSERT INTO spacecraftModel (agency, mid, num, charge, duration, energy) VALUES (?,?,?,?,?,?)";
        String aModelSQL = "INSERT INTO aModel (agency, mid, num, charge, duration, energy, capacity) VALUES (?,?,?,?,?,?,?)";
        String rentalRecordSQL = "INSERT INTO rentalRecord (agency, mid, snum, checkoutDate, returnDate) VALUES (?,?,?,STR_TO_DATE(?,'%d-%m-%Y'),STR_TO_DATE(?,'%d-%m-%Y'))";

        String filePath = "";
        String targetTable = "";

        while (true) {
            System.out.println();
            System.out.print("Type in the Source Data Folder Path: ");
            filePath = main.input.nextLine();
            if ((new File(filePath)).isDirectory())
                break;
        }

        System.out.print("Processing...");

        try {
            PreparedStatement stmt = mySQLDB.prepareStatement(neaSQL);

            BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/Near-Earth Asteroids.txt"));
            dataReader.readLine();
            String line = null;
            while ((line = dataReader.readLine()) != null) {
                String[] dataFields = line.split("\t");
                stmt.setString(1, dataFields[0]);
                stmt.setFloat(2, Float.parseFloat(dataFields[1]));
                stmt.setString(3, dataFields[2]);
                stmt.setInt(4, Integer.parseInt(dataFields[3]));
                stmt.setFloat(5, Float.parseFloat(dataFields[4]));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            PreparedStatement stmt = mySQLDB.prepareStatement(resourceSQL);

            BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/Resources Details.txt"));
            dataReader.readLine();
            String line = null;
            while ((line = dataReader.readLine()) != null) {
                String[] dataFields = line.split("\t");
                stmt.setString(1, dataFields[0]);
                stmt.setFloat(2, Float.parseFloat(dataFields[1]));
                stmt.setFloat(3, Float.parseFloat(dataFields[2]));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            PreparedStatement stmt = mySQLDB.prepareStatement(containSQL);

            BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/Near-Earth Asteroids.txt"));
            dataReader.readLine();
            String line = null;
            while ((line = dataReader.readLine()) != null) {
                String[] dataFields = line.split("\t");
                if (dataFields[5].equals("null"))
                    ;
                else {
                    stmt.setString(1, dataFields[0]);
                    stmt.setString(2, dataFields[5]);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            PreparedStatement stmt = mySQLDB.prepareStatement(spacecraftModelSQL);

            BufferedReader dataReader = new BufferedReader(
                    new FileReader(filePath + "/Space Agencies' Spacecrafts.txt"));
            dataReader.readLine();
            String line = null;
            while ((line = dataReader.readLine()) != null) {
                String[] dataFields = line.split("\t");
                stmt.setString(1, dataFields[0]);
                stmt.setString(2, dataFields[1]);
                stmt.setInt(3, Integer.parseInt(dataFields[2]));
                stmt.setInt(4, Integer.parseInt(dataFields[7]));
                stmt.setInt(5, Integer.parseInt(dataFields[5]));
                stmt.setFloat(6, Float.parseFloat(dataFields[4]));
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            PreparedStatement stmt = mySQLDB.prepareStatement(aModelSQL);

            BufferedReader dataReader = new BufferedReader(
                    new FileReader(filePath + "/Space Agencies' Spacecrafts.txt"));
            dataReader.readLine();
            String line = null;
            while ((line = dataReader.readLine()) != null) {
                String[] dataFields = line.split("\t");
                if (dataFields[3].equals("A")) {
                    stmt.setString(1, dataFields[0]);
                    stmt.setString(2, dataFields[1]);
                    stmt.setInt(3, Integer.parseInt(dataFields[2]));
                    stmt.setInt(4, Integer.parseInt(dataFields[7]));
                    stmt.setInt(5, Integer.parseInt(dataFields[5]));
                    stmt.setFloat(6, Float.parseFloat(dataFields[4]));
                    stmt.setInt(7, Integer.parseInt(dataFields[6]));
                    stmt.addBatch();
                } else
                    continue;
            }
            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        try {
            PreparedStatement stmt = mySQLDB.prepareStatement(rentalRecordSQL);

            BufferedReader dataReader = new BufferedReader(new FileReader(filePath + "/Spacecraft Rental Records.txt"));
            dataReader.readLine();
            String line = null;
            while ((line = dataReader.readLine()) != null) {
                String[] dataFields = line.split("\t");
                if (dataFields[4].equals("null")) {
                    stmt.setString(1, dataFields[0]);
                    stmt.setString(2, dataFields[1]);
                    stmt.setInt(3, Integer.parseInt(dataFields[2]));
                    stmt.setString(4, dataFields[3]);
                    stmt.setString(5, null);
                    stmt.addBatch();
                } else {
                    stmt.setString(1, dataFields[0]);
                    stmt.setString(2, dataFields[1]);
                    stmt.setInt(3, Integer.parseInt(dataFields[2]));
                    stmt.setString(4, dataFields[3]);
                    stmt.setString(5, dataFields[4]);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
            stmt.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("Done! Data are successfully loaded!");
    }

    public void menu() {
        Set<Integer> valid = new HashSet(Arrays.asList(1,2,3,4));
        printMenu();
        System.out.println("Enter Your Choice: ");
        int answer = getAnswer(valid);

        switch (answer) {
            case 1:
                createTables();
                break;
            case 2:
                deleteTables();
                break;
            case 3:
                loadTables();
                break;
            case 4:
                break;
        }
    }
}
