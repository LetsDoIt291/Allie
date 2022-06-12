import java.util.ArrayList;

public class SpamPrevention {

    public int hours;
    public int minutes;
    public int seconds;

    public SpamPrevention(int minutes, int seconds){
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public SpamPrevention(int hours, int minutes, int seconds){
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    public static boolean checkTimeReports(String uID){
        ArrayList<ReportTicket> reports = Storage.reportSort();
        ArrayList<String> times = new ArrayList<>();
        int count = 0;
        boolean x = false;

        for(int i = 0; i < reports.size(); i ++){
            if(reports.get(i).userID.equals(uID)){
                count++;
                times.add(reports.get(i).time);
            }
        }

        if(count >= 5){
            String u = times.get(times.size() - 5);
            Long z = Long.valueOf(u);


            //System.out.println((System.currentTimeMillis() - z));
            if((System.currentTimeMillis() - z) < 3600000){
                x = true;
                return x;
            }

        }

        return x;
    }

    public static boolean checkAppealTime(String uID){
        ArrayList<AppealTicket> appeals = Storage.appealSort();
        ArrayList<String> times = new ArrayList<>();

        for(int i = 0; i < appeals.size(); i ++){
            if(appeals.get(i).userID.equals(uID)){
                times.add(appeals.get(i).time);
            }
        }

        if(!times.isEmpty()) {
            Long z = Long.valueOf(times.get(times.size() - 1));

            if ((System.currentTimeMillis() - z) < 43200000) {
                return true;
            }
        }

        return false;
    }

    public static SpamPrevention spamPreventionAppeal(String uID){
        ArrayList<AppealTicket> appeals = Storage.appealSort();
        ArrayList<String> times = new ArrayList<>();
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        for(int i = 0; i < appeals.size(); i ++){
            if(appeals.get(i).userID.equals(uID)){
                times.add(appeals.get(i).time);
            }
        }

        if(!times.isEmpty()){
            Long z = Long.valueOf(times.get(times.size() - 1));

            Long timeSince = System.currentTimeMillis() - z;

            Long timeRemaining = 43200000 - timeSince;

            if(timeSince < 43200000){
                hours = (int) (timeRemaining / 3600000);
                minutes = (int) ((timeRemaining % 3600000) / 60000);
                seconds = (int) ((timeRemaining % 60000) / 1000);

            }
        }

        return new SpamPrevention(hours, minutes, seconds);
    }

    public static SpamPrevention spamPreventionReport(String uID){
        ArrayList<ReportTicket> reports = Storage.reportSort();
        ArrayList<String> times = new ArrayList<>();
        int count = 0;
        int minutes = 0;
        int seconds = 0;

        for(int i = 0; i < reports.size(); i ++){
            if(reports.get(i).userID.equals(uID)){
                count++;
                times.add(reports.get(i).time);
            }
        }

        if(count >= 5){
            String u = times.get(times.size() - 5);
            Long z = Long.valueOf(u);

            Long timeSince = System.currentTimeMillis() - z;

            Long timeRemaining = 3600000 - timeSince;

            if(timeSince < 3600000){
                minutes = (int) (timeRemaining / 60000);
                seconds = (int) ((timeRemaining % 60000) / 1000);

            }
        }

        return new SpamPrevention(minutes, seconds);
    }

}