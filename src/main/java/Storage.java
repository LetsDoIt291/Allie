import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    static final String reportsFilePath = "C:\\Users\\Letsd\\Desktop\\Reports.txt";
    static final String infoFilePath = "C:\\Users\\Letsd\\Desktop\\information.txt";
    static final String blackListFilePath = "C:\\Users\\Letsd\\Desktop\\Blacklist.txt";
    static final String availableModsFilePath = "C:\\Users\\Letsd\\Desktop\\Modcall.txt";
    static final String appealsFilePath = "C:\\Users\\Letsd\\Desktop\\Appeals.txt";
    static final String openTicketFilePath = "C:\\Users\\Letsd\\Desktop\\TicketOpen.txt";
    static final String activeAppealsFilePath ="C:\\Users\\Letsd\\Desktop\\ActiveAppeals.txt";

    public static String gameTicketC = readInfoFile(0);
    public static String ticketArchiveC = readInfoFile(1);
    public static String server = readInfoFile(2);
    public static String archiveC = readInfoFile(3);
    public static String modCallC = readInfoFile(4);
    public static String modCallMessage = readInfoFile(5);
    public static String discordTicketC = readInfoFile(6);

    public static void addUserToActiveAppeals(String userID){
        try {
            FileWriter myWriter = new FileWriter(activeAppealsFilePath, true);
            myWriter.write(userID + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the ActiveAppeals file. (addUserToActiveAppeals)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the ActiveAppeals file. (addUserToActiveAppeals)");
            e.printStackTrace();
        }
    }

    public static void clearActiveAppeals(){
        try {
            FileWriter myWriter = new FileWriter(activeAppealsFilePath);
            myWriter.write("");
            myWriter.close();
            System.out.println("Successfully wrote to the ActiveAppeals file. (clearActiveAppeals)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the ActiveAppeals file. (clearActiveAppeals)");
            e.printStackTrace();
        }
    }

    public static void removeUserFromActiveAppeals(String userID){
        String idList = "";

        //read the file and looks for the ID
        try {
            File myObj = new File(activeAppealsFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(!myReader.nextLine().equals(userID))
                    idList = idList + myReader.nextLine() + "\n";
            }
            myReader.close();
            System.out.println("Successfully read the ActiveAppeals file. (removeUserFromActiveAppeals)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the ActiveAppeals file. (removeUserFromActiveAppeals");
            e.printStackTrace();
        }

        System.out.println(idList);

        //rewrites the available mods file without the given ID
        try {
            FileWriter myWriter = new FileWriter(activeAppealsFilePath);
            myWriter.write(idList);
            myWriter.close();
            System.out.println("Successfully wrote to the ActiveAppeals file. (removeUserFromActiveAppeals)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the ActiveAppeals. (removeUserFromActiveAppeals)");
            e.printStackTrace();
        }
    }

    public static boolean checkUserActiveAppeal(String userID){
        //read the file and looks for the ID
        try {
            File myObj = new File(activeAppealsFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(myReader.nextLine().equals(userID)) {
                    myReader.close();
                    System.out.println("Successfully read the ActiveAppeals file. (checkUserActiveAppeal)");
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the ActiveAppeals file. (checkUserActiveAppeal");
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<AppealTicket> appealSort(){
        ArrayList<String> appeals = new ArrayList<>();

        ArrayList<AppealTicket> appealsObj = new ArrayList<>();

        //puts all the content of the file into an array. the index position is how report IDs are determined. (used within other methods)
        try {
            File myObj = new File(Storage.appealsFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                appeals.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the appealSort file. (appealSort)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the appealSort file. (appealSort)");
            e.printStackTrace();
        }

        for(int i = 0; i < (appeals.size() / 5); i++){
            appealsObj.add(new AppealTicket(appeals.get(i * 5), appeals.get((i * 5) + 1), appeals.get((i * 5) + 2), appeals.get((i * 5) + 3), appeals.get((i * 5) + 4)));
        }


        return appealsObj;
    }

    public static ArrayList<String> openTicketSort(){
        ArrayList<String> userIDs = new ArrayList<>();

        //puts all the content of the file into an array.
        try {
            File myObj = new File(openTicketFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                userIDs.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the openTicket file. (openTicketSort)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the openTicket file. (openTicketSort)");
            e.printStackTrace();
        }

        //returns the array
        return userIDs;
    }

    public static void addUserToOpenTicket(String userID){
        try {
            FileWriter myWriter = new FileWriter(openTicketFilePath, true);
            myWriter.write(userID + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the openTicket file. (addUserToOpenTicket)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the openTicket file. (addUserToOpenTicket)");
            e.printStackTrace();
        }
    }

    public static void removeUserFromOpenTicket(String userID){
        ArrayList<String> modList = openTicketSort();

        String idList = "";

        for(int i = 0; i < modList.size(); i++){
            if(!modList.get(i).startsWith(userID)){
                idList = idList + modList.get(i) + "\n";
            }
        }

        //rewrites the available mods file without the given ID
        try {
            FileWriter myWriter = new FileWriter(openTicketFilePath);
            myWriter.write(idList);
            myWriter.close();
            System.out.println("Successfully wrote to the openTicket file. (removeUserFromOpenTicket)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the openTicket. (removeUserFromOpenTicket)");
            e.printStackTrace();
        }
    }

    public static void clearOpenTicket(){
        try {
            FileWriter myWriter = new FileWriter(openTicketFilePath);
            myWriter.write("");
            myWriter.close();
            System.out.println("Successfully wrote to the openTicket file. (clearOpenTicket)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the openTicket file. (clearOpenTicket)");
            e.printStackTrace();
        }
    }

    public static boolean checkUserOpenTicket(String userID){
        //read the file and looks for the ID
        try {
            File myObj = new File(openTicketFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(myReader.nextLine().equals(userID)) {
                    myReader.close();
                    System.out.println("Successfully read the openTicket file. (checkUserOpenTicket)");
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the openTicket file. (checkUserOpenTicket");
            e.printStackTrace();
        }

        return false;
    }

    public static String getGameTicketC(){
        return gameTicketC;
    }

    public static String getModCallC(){
        return modCallC;
    }

    public static void changeInfoFile(String newID, int index){
        ArrayList<String> info = new ArrayList<>();

        //reads the file and adds every line found to the array list above
        try {
            File myObj = new File(infoFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                info.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file. (changeInfoFile)");
            e.printStackTrace();
        }

        info.set(index, newID);

        //rewrites the file with the updated changes
        try {
            FileWriter myWriter = new FileWriter(infoFilePath);
            for(int i = 0; i < info.size(); i++) {
                myWriter.write(info.get(i) + "\n");
            }
            myWriter.close();
            System.out.println("Successfully rewrote the info file. (changeInfoFile)");
        } catch (IOException e) {
            System.out.println("An error occurred while rewriting the info file. (changeInfoFile)");
            e.printStackTrace();
        }

    }

    public static String readInfoFile(int index){
        ArrayList<String> info = new ArrayList<>();

        //reads the file and adds every line found to the array list above
        try {
            File myObj = new File(infoFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                info.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file. (readInfoFile)");
            e.printStackTrace();
        }

        return info.get(index);
    }

    public static ArrayList<ReportTicket> reportSort(){
        ArrayList<String> reports = new ArrayList<>();

        ArrayList<ReportTicket> reportsObj = new ArrayList<>();

        //puts all the content of the file into an array. the index position is how report IDs are determined. (used within other methods)
        try {
            File myObj = new File(Storage.reportsFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                reports.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the reports file. (reportSort)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading file. (reportSort)");
            e.printStackTrace();
        }

        for(int i = 0; i < (reports.size() / 5); i++){
            reportsObj.add(new ReportTicket(reports.get(i * 5), reports.get((i * 5) + 2), reports.get((i * 5) + 4), reports.get((i * 5) + 1), reports.get((i * 5) + 3)));
        }


        return reportsObj;
    }

    public static void reportChangeUsername(String reportID, String newUsername){
        ArrayList<String> reports = new ArrayList<>();

        try {
            File myObj = new File(Storage.reportsFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                reports.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the reports file. (reportChangeUsername)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading file. (reportChangeUsername)");
            e.printStackTrace();
        }


        reports.set((Integer.parseInt(reportID) * 5) + 2, newUsername);

        try {
            FileWriter myWriter = new FileWriter(Storage.reportsFilePath);

            for(int i = 0; i < reports.size(); i++){
                myWriter.write(reports.get(i) + "\n");
            }

            myWriter.close();
            System.out.println("Successfully wrote to the reports file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the reports file.");
            e.printStackTrace();
        }
    }

    public static void addModToFile(String modID){

        try {
            FileWriter myWriter = new FileWriter(availableModsFilePath, true);
            myWriter.write(modID + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the modcall file. (addModToFile)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing in the modcall file. (addModToFile)");
            e.printStackTrace();
        }
    }

    public static void removeModFromFile(String modID){
        ArrayList<String> modList = modSort();

        String activeMods = "";

        for(int i = 0; i < modList.size(); i++){
            if(!modList.get(i).startsWith(modID)){
                activeMods = activeMods + modList.get(i) + "\n";
            }
        }

        System.out.println(activeMods);

        //rewrites the available mods file without the given ID
        try {
            FileWriter myWriter = new FileWriter(availableModsFilePath);
            myWriter.write(activeMods);
            myWriter.close();
            System.out.println("Successfully wrote to the modcall file. (removeModFromFile)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the modcall. (removeModFromFile)");
            e.printStackTrace();
        }
    }

    public static ArrayList<String> modSort(){
        ArrayList<String> mods = new ArrayList<>();

        //puts all the content of the file into an array.
        try {
            File myObj = new File(availableModsFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                mods.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the modcall file. (modSort)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the modcall file. (modSort)");
            e.printStackTrace();
        }

        //returns the array
        return mods;
    }

    public static void clearModCall(){
        try {
            FileWriter myWriter = new FileWriter(availableModsFilePath);
            myWriter.write("");
            myWriter.close();
            System.out.println("Successfully wrote to the modcall file. (clearModCall)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing in the modcall file. (clearModCall)");
            e.printStackTrace();
        }
    }
}