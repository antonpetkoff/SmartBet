package math.model;

import statistics.Keys;
import statistics.Statistics;

public class MathModel {

    private static final char HOME = 'H';
    private static final char AWAY = 'A';
    
    private Statistics stats;
    private String hostName;
    private String guestName;

    // iterate through records up to the start of the season
    private static final String SEASON_START_BOUND = "11/05/14";
    
    public MathModel(String hostName, String guestName) {
        setHostName(hostName);
        setGuestName(guestName);
        stats = new Statistics();
    }
    
    static long factorial(int n) {
        long result = 1;
        for (int i = n; i > 1; --i) {
            result *= i;
        }
        return result;
    }
    
    static double poisson(int k, double lambda) {
        return Math.pow(lambda, k) * Math.pow(Math.E, -lambda) / factorial(k);
    }
    
    static double mean(int... numbers) {
        long sum = 0;
        for (int i = 0; i < numbers.length; ++i) {
            sum += numbers[i];
        }
        return sum / (double) numbers.length;
    }

    /**
     * Calculates the average goals scored in the league at home or as guest.
     * Records are traversed from the give recordID until the SEASON_START_BOUND,
     * which marks the end of the last season so we now when this season starts.
     * 
     * @param where     : where are the goals scored? At HOME='H' or AWAY='A'.
     * @param recordID  : recordID in the matches table from which to move back in time
     * @return  the average goals scored in the league at home or away
     */
    double avgGoalsLeague(char where, int recordID) {
        if (where != HOME && where != AWAY) {
            throw new IllegalArgumentException("only H or A is accepted as \'where\' argument!");
        }
        
        long sum = 0, count = 0;
        
        int recordField = (where == HOME) ? Keys.FTHG.ordinal() : Keys.FTAG.ordinal();
        
        for (int i = stats.size() - 1; i > 1 && !(SEASON_START_BOUND.equals( stats.get(i).get(Keys.Date.ordinal())) ); --i) {
            sum += Integer.valueOf(stats.get(i).get(recordField));
            ++count;
        }

        return sum / (double) count;
    }
    
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    
    public Statistics getStats() {
        return stats;
    }
    
}
