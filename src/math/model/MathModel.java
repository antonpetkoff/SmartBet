package math.model;

import java.util.HashMap;
import java.util.Map;

import statistics.Keys;
import statistics.Statistics;

public class MathModel {

    private static final char HOME = 'H';   // marks matches as host
    private static final char AWAY = 'A';   // marks matches as guest
    
    private static final char FOR = 'F';        // goals for
    private static final char AGAINST = 'A';    // goals against
    
    private Statistics stats;
    private String hostName;
    private String guestName;
    
    private int startRecordID;              // record ID from which we move back in time

    // iterate through records up to the start of the season
    private static final String SEASON_START_BOUND = "11/05/14";
    
    public MathModel(String hostName, String guestName, Statistics stats) {
        setHostName(hostName);
        setGuestName(guestName);
        this.stats = stats;
        startRecordID = stats.size() - 1;
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
    double avgLeagueGoals(char where) {
        if (where != HOME && where != AWAY) {
            throw new IllegalArgumentException("only \'H\' or \'A\' is accepted as \'where\' argument!");
        }
        
        long sum = 0, count = 0;
        
        int recordField = (where == HOME) ? Keys.FTHG.ordinal() : Keys.FTAG.ordinal();
        
        for (int i = stats.size() - 1; i > 1 && !(SEASON_START_BOUND.equals( stats.get(i).get(Keys.Date.ordinal()) )); --i) {
            sum += Integer.valueOf(stats.get(i).get(recordField));
            ++count;
        }

        return sum / (double) count;
    }
    
    /**
     * Calculates the average goals FOR or AGAINST at HOME or AWAY for the given
     * teamName.
     * 
     * @param where     : goals at HOME or AWAY for the teamName
     * @param forWho    : goals FOR or AGAINST the teamName
     * @param teamName  : for which team
     * @param recordID  : recordID in the matches table from which to move back in time
     * @return
     */
    double avgTeamGoals(char where, char forWho, String teamName) {
        if (forWho != FOR && forWho != AGAINST) {
            throw new IllegalArgumentException("only \'F\' or \'A\' is accepted as \'forWho\' argument!");
        }
        
        if (where != HOME && where != AWAY) {
            throw new IllegalArgumentException("only \'H\' or \'A\' is accepted as \'where\' argument!");
        }
        
        int teamField = 0, goalsField = 0;
        
        if (where == HOME) {
            if (forWho == FOR) {
                teamField = Keys.HomeTeam.ordinal();
                goalsField = Keys.FTHG.ordinal();
            } else if (forWho == AGAINST) {
                teamField = Keys.HomeTeam.ordinal();
                goalsField = Keys.FTAG.ordinal();
            }
        } else if (where == AWAY) {
            if (forWho == FOR) {
                teamField = Keys.AwayTeam.ordinal();
                goalsField = Keys.FTAG.ordinal();
            } else if (forWho == AGAINST) {
                teamField = Keys.AwayTeam.ordinal();
                goalsField = Keys.FTHG.ordinal();
            }
        }
        
        int sum = 0, count = 0;
        
        for (int i = stats.size() - 1; i > 1 && !(SEASON_START_BOUND.equals( stats.get(i).get(Keys.Date.ordinal()) )); --i) {
            if (stats.get(i).get(teamField).equals(teamName)) {
                sum += Integer.valueOf(stats.get(i).get(goalsField));
                ++count;
            }
        }
        
        return sum / (double) count;
    }
    
    String calculateProbabilities() {
        double avgLeagueGoalsAtHome = avgLeagueGoals(HOME);
        double avgLeagueGoalsAway = avgLeagueGoals(AWAY);
        
        double hostAttack = avgTeamGoals(HOME, FOR, hostName) / avgLeagueGoalsAtHome;
        double guestDefence = avgTeamGoals(AWAY, AGAINST, guestName) / avgLeagueGoalsAtHome;
        double avgHostGoals = hostAttack * guestDefence * avgLeagueGoalsAtHome;
        
        double guestAttack = avgTeamGoals(AWAY, FOR, guestName) / avgLeagueGoalsAway;
        double hostDefence = avgTeamGoals(HOME, AGAINST, hostName) / avgLeagueGoalsAway;
        double avgGuestGoals = guestAttack * hostDefence * avgLeagueGoalsAway;
        
        System.out.println(avgHostGoals + " " + avgGuestGoals);
        
        return distribute(avgHostGoals, avgGuestGoals);
    }
    
    String distribute(double avgHostGoals, double avgGuestGoals) {
        Map<Double, String> map = new HashMap<Double, String>();
        
        double hostWin = 0.0, draw = 0.0, guestWin = 0.0;
        for (int hostGoals = 0; hostGoals <= 12; ++hostGoals) {
            for (int guestGoals = 0; guestGoals <= 12; ++guestGoals) {
                double result = poisson(hostGoals, avgHostGoals) * poisson(guestGoals, avgGuestGoals);
                map.put(result, hostGoals + ":" + guestGoals);
                
                if (hostGoals > guestGoals) {
                    hostWin += result;
                } else if (hostGoals == guestGoals) {
                    draw += result;
                } else if (hostGoals < guestGoals) {
                    guestWin += result;
                }
            }
        }
        
        return hostWin + ", " + draw + ", " + guestWin + " / " + map.toString();
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        // TODO check argument for correctness
        this.hostName = hostName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        // TODO check argument for correctness
        this.guestName = guestName;
    }
    
    public Statistics getStats() {
        return stats;
    }
    
    public int getStartRecordID() {
        return startRecordID;
    }

    public void setStartRecordID(int startRecordID) {
        this.startRecordID = startRecordID;
    }
    
    public static void main(String[] args) {
        Statistics stats = new Statistics();
        MathModel mm = new MathModel("Man City", "Aston Villa", stats);
        System.out.println(mm.calculateProbabilities());
    }


}
