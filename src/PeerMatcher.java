import java.sql.*;
import java.util.*;


public class PeerMatcher{
    public static void main(String[] args){
        Connection conn = null;

        try{
            //establishes connection
            conn = DriverManager.getConnection
                    ("jdbc:mysql://localhost:3306/peer_matcher?serverTimezone=UTC&" + "user=root");

            Statement stmt = conn.createStatement();
            boolean working = true;
            Scanner in = new Scanner(System.in);
            ResultSet rs;
            String command = " ";
            String fName, lName, major, minor;
            int userID = 0;
            int graduationYear, favHobby, favMediaPlat, favCuisine;


            System.out.println("===================================");
            System.out.println("====  Welcome To Peer-Matcher!  ===");
            System.out.println("===================================\n\n\n");


            while(working){
                System.out.println("To exit type 'exit'");
                System.out.println("First off, are you a 'new' or 'existing' user? ");
                command = in.nextLine();

                //creates new user account
                if(command.equals("new")){
                    System.out.println("Great! Please enter the following;");
                    System.out.print("Four-digit Student ID: ");
                    userID = in.nextInt();
                    in.nextLine();

                    //checks that userID is unique
                    rs = stmt.executeQuery("SELECT * FROM student WHERE userId =" + userID);
                    boolean validID = !(rs.next());
                    if (validID == false){
                        System.out.println("Student ID already exists, please reboot program...");
                        break;
                    }

                    System.out.print("First Name (capitalize first letter): ");
                    fName = in.nextLine();
                    System.out.print("Last Name (capitalize first letter): ");
                    lName = in.nextLine();
                    System.out.print("Expected Graduation year (ex. 2023): ");
                    graduationYear = in.nextInt();
                    in.nextLine();
                    System.out.print("Major (capitalize first letter of every word): ");
                    major = in.nextLine();
                    System.out.print("Minor (if you don't have a minor type 'null'): ");
                    minor = in.nextLine();

                    //ensures the proper formatign of minor for SQL statement
                    if (!(minor.equals("null"))){
                        minor = "'" + minor + "'";
                    }

                    System.out.print("Which of these hobbies do you most prefer?\n" +
                            "1.) Video Games\n2.) Arts and Crafts\n3.) Gardening\n4.) Sports\n" +
                            "Please enter the corresponding integer: ");
                    favHobby = in.nextInt();

                    System.out.print("Which of these cuisines do you most prefer?\n" +
                            "1.) Twitch\n2.) YouTube\n3.) Netflix\n4.) Disney+\n" +
                            "Please enter the corresponding integer: ");
                    favMediaPlat = in.nextInt();

                    System.out.print("Which of these Media Platforms do you most prefer?\n" +
                            "1.) Japanese\n2.) Mexican\n3.) Pakistani\n4.) Italian\n" +
                            "Please enter the corresponding integer: ");
                    favCuisine = in.nextInt();
                    in.nextLine();

                    System.out.println("Please enter the classes you are taking (ex. 'CS-402')");

                    updateClasses(userID, rs, stmt);

                    System.out.println("Creating Profile...");

                    // attempts to populate table with user data
                    String sql = "INSERT INTO student VALUES (" + userID + ", '" + fName + "', '" + lName + "', " +
                            graduationYear + ", '" + major + "', " + minor + ", " + favHobby + ", " + favMediaPlat + ", " +
                            favCuisine + ")";

                    stmt.executeUpdate(sql);

                    System.out.println("Profile Created!, let's take you back to the start screen...");
                }

                if(command.equals("existing")){
                    System.out.println("Please Enter Your ID to start!\n");
                    System.out.print("Student ID: ");

                    command = in.nextLine();

                    if (command.equals("exit")){
                        working = false;
                    }

                    if (Integer.parseInt(command) <= 9999 && Integer.parseInt(command) > 999){
                        userID = Integer.parseInt(command);

                        System.out.print("\nPlease Enter Your First Name (capitalize first letter): ");
                        fName = in.nextLine();
                        System.out.print("\nPlease Enter Your Last Name (capitalize first letter): ");
                        lName = in.nextLine();

                        rs = stmt.executeQuery("SELECT * FROM student WHERE userId = " + userID);

                        //checks if user exists.
                        if (rs.next() == false){
                            System.out.println("\nID not found please enter a different ID, create a new profile, or type 'exit' to close program.");
                        } else if(!(rs.getString("fName").equals(fName)) || !(rs.getString("lName").equals(lName))){
                            System.out.println("ID and Name do not match, please try again.");
                        } else {//if user exists

                            // saves user data for comparisons
                            userID = rs.getInt("userId");
                            fName = rs.getString("fName");
                            lName = rs.getString("lName");
                            graduationYear = rs.getInt("graduationYear");
                            major = rs.getString("Major");
                            favHobby = rs.getInt("favHobbyID");
                            favMediaPlat = rs.getInt("favMediaPlatID");
                            favCuisine = rs.getInt("favCuisineID");

                            System.out.println("Welcome back " + fName + "!\n");

                            System.out.println("Here is your current profile:\n");

                            rs = stmt.executeQuery("SELECT * FROM student WHERE userId = '" + userID +"'");

                            //displays all associated information with the user
                            System.out.println("Student ID: " + userID);
                            System.out.println("First Name: " + fName);
                            System.out.println("Last Name: " + lName);
                            System.out.println("Graduation Year: " + graduationYear);
                            System.out.println("Major: " + major);
                            rs = stmt.executeQuery("SELECT * FROM hobby WHERE favHobbyID = " + favHobby);
                            rs.next();
                            System.out.println("Favorite Hobby: " + rs.getString("FavHobby"));
                            rs = stmt.executeQuery("SELECT * FROM cuisine WHERE favCuisineID = " + favCuisine);
                            rs.next();
                            System.out.println("Favorite Cuisine: " + rs.getString("faveCuisine"));
                            rs = stmt.executeQuery("SELECT * FROM mediaplatform WHERE favMediaPlatID = " + favMediaPlat);
                            rs.next();
                            System.out.println("Favorite Media Platform: " + rs.getString("FavMediaPlatform"));

                            rs = stmt.executeQuery("SELECT courseNumber FROM registration WHERE userID = " + userID);

                            System.out.println("\nYou are taking the classes below\n");
                            while (rs.next()){
                                System.out.println(rs.getString("courseNumber"));
                            }

                            System.out.println("\nWould you like to view compatible 'students'\n" +
                                    "or would you like to 'update' your info?");
                            command = in.nextLine();

                            if (command.equals("students")){//displays sorted student list
                                System.out.println("\n\n\n");
                                System.out.println("Here is a list of some compatible peers starting at most compatible!\n\n");

                                rs = stmt.executeQuery("SELECT * FROM student");

                                // creates a sorted link list of Strings that contain student names and
                                // their compatibility score based off of interests

                                LinkedList<String> studentList = new LinkedList<String>();
                                while (rs.next()){
                                    if (rs.getInt("userID") != userID){
                                        int compatibility = 0;

                                        if (major.equals(rs.getString("Major"))) compatibility++;
                                        if (favHobby == (rs.getInt("favHobbyID"))) compatibility++;
                                        if (favMediaPlat == (rs.getInt("favMediaPlatID"))) compatibility++;
                                        if (favCuisine == (rs.getInt("favCuisineID"))) compatibility++;



                                        String newEntry = rs.getString("userId") + "-" + compatibility;

                                        if(studentList.size() == 0){
                                            studentList.add(newEntry);
                                        } else {
                                            for (int n = 0; n < studentList.size(); n++){
                                                String currentEntry = studentList.get(n);

                                                if (Integer.parseInt(currentEntry.split("-")[1]) > compatibility){
                                                    studentList.add(n, newEntry);
                                                    n = 999999999;
                                                }else if (n == studentList.size() - 1){
                                                    studentList.add(newEntry);
                                                    n = 999999999;
                                                }
                                            }
                                        }

                                    }
                                }

                                String[] userClasses = new String[6];

                                rs = stmt.executeQuery("SELECT courseNumber FROM registration WHERE userID =" + userID);

                                int n = 0;

                                while (rs.next()){
                                    userClasses[n] = rs.getString("courseNumber");
                                    n++;
                                }

                                for (n = 0; n < studentList.size(); n++){
                                    //get value from current element from list
                                    String[] currentEntry = studentList.get(n).split("-");

                                    //get courseNumbers currently associated with userId in element
                                    rs = stmt.executeQuery("SELECT courseNumber FROM registration WHERE userID =" + currentEntry[0]);

                                    //while there is still room in the resultSet
                                    while (rs.next()){
                                        int x = 0;
                                        while (userClasses[x] != null && x < 6){
                                            if (rs.getString("courseNumber").equals(userClasses[x])){
                                                currentEntry[1] = Integer.toString(Integer.parseInt(currentEntry[1]) + 1);
                                                String newEntry = currentEntry[0] + "-" + currentEntry[1];

                                                for (int i = n; i < studentList.size(); i++){

                                                    if (Integer.parseInt(studentList.get(i).split("-")[1]) >= Integer.parseInt(currentEntry[1])){
                                                        studentList.remove(n);
                                                        studentList.add(i, newEntry);
                                                        i = 999999999;
                                                    }else if (i == studentList.size() - 1){
                                                        studentList.remove(n);
                                                        studentList.add(newEntry);
                                                        i = 999999999;
                                                    }
                                                }
                                            }
                                            x++;
                                        }

                                    }
                                }

                                boolean minValueReached = false;

                                while (studentList.size() != 0 && minValueReached == false){
                                    String[] currentEntry = (studentList.removeLast().split("-"));

                                    rs = stmt.executeQuery("SELECT fName, lName FROM student WHERE userId = " + currentEntry[0]);
                                    rs.next();

                                    if (studentList.size() != 0 && Integer.parseInt(currentEntry[1]) < 2){
                                        minValueReached = true;
                                    } else {
                                        System.out.println(rs.getString("fName") + " " + rs.getString("lName"));
                                        System.out.println("Compatibility Score: " + currentEntry[1] + "\n");
                                    }
                                }
                            }else if (command.equals("update")){//allows user to update certain data entries
                                boolean updateComplete = false;

                                while(updateComplete == false){
                                    System.out.print("Would you like to update your;\ngraduation 'year',\n'registration',\nor 'major'" +
                                            "\n\nIf 'done' updating enter 'done'\nCommand:");
                                    command = in.nextLine();
                                    if (command.equals("year")){
                                        System.out.print("\nNew Graduation Year: ");
                                        stmt.executeUpdate("UPDATE student SET graduationYear=" + in.nextLine() + " WHERE userId=" + userID);
                                    }else if (command.equals("major")){
                                        System.out.print("\nNew Major: ");
                                        stmt.executeUpdate("UPDATE student SET Major='" + in.nextLine() + "' WHERE userId=" + userID);
                                    }else if (command.equals("registration")){
                                        stmt.executeUpdate("DELETE FROM registration WHERE userID = " + userID);
                                        updateClasses(userID, rs, stmt);
                                    } else if (command.equals("done")){
                                        updateComplete = true;
                                    }
                                }
                            }
                        }
                    } else {
                        System.out.println("\nPlease Enter a valid ID. ex: '1234'");
                    }
                }//closes existing user branch

                if (command.equals("exit")){
                    working = false;
                }
            } // closes While loop
        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    public static boolean updateClasses(int userID, ResultSet rs, Statement stmt){
        //storage for registration loop
        Scanner in = new Scanner(System.in);
        String classNo, command, className;
        boolean validEntry = true;
        int i = 1;

        //loops while the user is still logging classes
        try {
            while (true){
                System.out.println("(Type 'done' when finished entering couses)");
                System.out.print("Class " + i + ": ");
                classNo = in.nextLine();

                if (classNo.equals("done") || i == 7){
                    return true;
                } else {
                    String sql = "SELECT courseNumber FROM classlist WHERE courseNumber= '" + classNo + "'";
                    rs = stmt.executeQuery(sql);
                    boolean courseExists = rs.next();

                    //if class does not exist in classList, allow the user to enter the info
                    if (courseExists == false){
                        validEntry = false;
                        System.out.println("The class " + classNo + " does not exist in our registry, would you like to create an entry?\n" +
                                "(Please ensure proper fomatting e.g. CS-101)");
                        System.out.print("Please type 'y' (yes) or 'n' (no): ");
                        command = in.nextLine();

                        if (command.equals("n")){
                            System.out.println("New entry will not be created.");
                        } else if (command.equals("y")){
                            System.out.println("Please enter name of course " + classNo + " ex. (Intro To Philosophy)");
                            System.out.print("Course Name: ");
                            className = in.nextLine();
                            System.out.println("Entering course information...");
                            sql = "INSERT INTO classlist (courseNumber, className) VALUES ('" + classNo + "', '" + className + "')";
                            stmt.executeUpdate(sql);
                            validEntry = true;
                        }
                    }

                    if (validEntry == true){
                        System.out.println("Marking you as enrolled in this class...");
                        sql = "INSERT INTO registration VALUES ('" + userID + "', '" + classNo + "')";
                        stmt.executeUpdate(sql);
                    }
                }
                //counts how many classes have been logged
                i++;
            }

        } catch (SQLException ex) {
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
    }
}