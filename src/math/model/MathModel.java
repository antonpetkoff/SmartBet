package math.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import statistics.Keys;
import statistics.Statistics;

public class MathModel {

    private static final String HOST_WIN = "H";
    private static final String DRAW = "D";
    private static final String AWAY_WIN = "A";
    
    private static final double WIN_POINTS = 1.0;
    private static final double DRAW_POINTS = 0.95;
    private static final double LOSS_POINTS = 0.90;
    
    private static final char HOME = 'H';   // marks matches as host
    private static final char AWAY = 'A';   // marks matches as guest
    
    private static final char FOR = 'F';        // goals for
    private static final char AGAINST = 'A';    // goals against
    
    private static final int FORM_MATCH_COUNT = 8;
    
    private Statistics stats;
    private String[] teamNames;
    private String hostName;
    private String guestName;
    private Conditional<Boolean> iterateCondition; // if iterateCondition is false, stop iterating
    private int startRecordID;              // record ID from which we move back in time
    private boolean validateNames;
    
    // iterate through records up to the end of the last season !exclusive!
    private static final String LAST_SEASON_END = "11/05/14";
    
    // check if record is in current season
    public final Conditional<Boolean> isInCurrentSeason = new Conditional<Boolean>() {
        @Override
        public Boolean check(int recordID) {
            return ! LAST_SEASON_END.equals( stats.get(recordID).get(Keys.Date.ordinal()));
        }
    };
    
    /**
     * sets the iterateCondition to the default isInCurrentSeason
     * sets the startRecordID to the default last record ID in the statistics table
     * sets validateName boolean to true by default
     * 
     * @param hostName
     * @param guestName
     * @param stats
     */
    public MathModel(String hostName, String guestName, String statsPath, String teamNamesPath) {
        try {
            teamNames = readTeamNames(teamNamesPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        
        setHostName(hostName);
        setGuestName(guestName);
        this.stats = new Statistics(statsPath);
        startRecordID = stats.size() - 1;
        setIterateCondition(isInCurrentSeason);
        setValidateNames(true);
    }
    
    private String[] readTeamNames(String jsonPath) throws JSONException {
        String jsonText = null;
        try(BufferedReader br = new BufferedReader(new FileReader(new File(jsonPath)));) {
            jsonText = br.readLine();
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        JSONArray json = new JSONArray(jsonText);
        
        String[] teamNames = new String[json.length()];
        for (int i = 0; i < json.length(); ++i) {
            teamNames[i] = json.getJSONObject(i).getString("name");
        }
        return teamNames;
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

    /**
     * Calculates the average goals scored in the league at home or as guest.
     * Records are traversed from the give recordID until the SEASON_START_BOUND,
     * which marks the end of the last season so we now when this season starts.
     * 
     * @param where     : where are the goals scored? At HOME='H' or AWAY='A'.
     * @param recordID  : recordID in the matches table from which to move back in time
     * @return  the average goals scored in the league at home or away
     */
    private double avgLeagueGoals(char where) {
        if (where != HOME && where != AWAY) {
            throw new IllegalArgumentException("only \'H\' or \'A\' is accepted as \'where\' argument!");
        }
        
        long sum = 0, count = 0;
        
        int recordField = (where == HOME) ? Keys.FTHG.ordinal() : Keys.FTAG.ordinal();
        
        for (int i = stats.size() - 1; i > 1 && getIterateCondition().check(i); --i) {
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
    private double avgTeamGoals(char where, char forWho, String teamName) {
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
        
        for (int i = stats.size() - 1; i > 1 && getIterateCondition().check(i); --i) {
            if (stats.get(i).get(teamField).equals(teamName)) {
                sum += Integer.valueOf(stats.get(i).get(goalsField));
                ++count;
            }
        }
        
        return sum / (double) count;
    }
    
    public String calculateProbabilities() {
        double avgLeagueGoalsAtHome = avgLeagueGoals(HOME);
        double avgLeagueGoalsAway = avgLeagueGoals(AWAY);
        
        double hostAttack = avgTeamGoals(HOME, FOR, hostName) / avgLeagueGoalsAtHome;
        double guestDefence = avgTeamGoals(AWAY, AGAINST, guestName) / avgLeagueGoalsAtHome;
        double avgHostGoals = hostAttack * guestDefence * avgLeagueGoalsAtHome;
        avgHostGoals *= Math.sqrt(evaluateForm(hostName, FORM_MATCH_COUNT));
        //avgHostGoals *= Math.sqrt(evaluateHistory(hostName, guestName, 5));
        
        double guestAttack = avgTeamGoals(AWAY, FOR, guestName) / avgLeagueGoalsAway;
        double hostDefence = avgTeamGoals(HOME, AGAINST, hostName) / avgLeagueGoalsAway;
        double avgGuestGoals = guestAttack * hostDefence * avgLeagueGoalsAway;
        avgGuestGoals *= Math.sqrt(evaluateForm(guestName, FORM_MATCH_COUNT));
        //avgGuestGoals *= Math.sqrt(evaluateHistory(guestName, hostName, 5));
        
        String jsonString = null;
        try {
            jsonString = distribute(avgHostGoals, avgGuestGoals);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return jsonString;
    }
    
    String distribute(double avgHostGoals, double avgGuestGoals) throws JSONException {
        TreeMap<Double, String> map = new TreeMap<Double, String>();
        
        double hostWin = 0.0, draw = 0.0, guestWin = 0.0;
        double under = 0.0, over = 0.0, bothTeamsScore = 0.0, oneTeamScores = 0.0;
        
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
                
                if (hostGoals + guestGoals < 2.5) {
                    under += result;
                } else {
                    over += result;
                }
                
                if (hostGoals > 0 && guestGoals > 0) {
                    bothTeamsScore += result;
                } else {
                    oneTeamScores += result;
                }
            }
        }
        
        JSONObject outcomes = new JSONObject();
        outcomes.put("1", hostWin).put("X", draw).put("2", guestWin);
        
        JSONObject top5 = new JSONObject();
        int i = 0;
        for (Double key : map.descendingKeySet()) {
            if (i == 5) {
                break;
            }
            top5.put(map.get(key), key);
            ++i;
        }
        
        JSONObject overUnder = new JSONObject();
        overUnder.put("over", over).put("under", under);
        
        JSONObject bts = new JSONObject();
        bts.put("both", bothTeamsScore).put("one", oneTeamScores);
        
        
        return new JSONArray().put(outcomes).put(top5).put(overUnder).put(bts).toString();
    }

    double evaluateHistory(String hostName, String guestName, int matchCount) {
        double sum = 0.0;
        int recordID = getStartRecordID(), count = 0;
        
        while (count < matchCount && recordID > 1) {
            String fullTimeOutcome = stats.get(recordID).get(Keys.FTR.ordinal());
            
            if (stats.get(recordID).get(Keys.HomeTeam.ordinal()).equals(hostName) 
                && stats.get(recordID).get(Keys.AwayTeam.ordinal()).equals(guestName)) {
                if (fullTimeOutcome.equals(HOST_WIN)) {
                    sum += WIN_POINTS;
                } else if (fullTimeOutcome.equals(DRAW)) {
                    sum += DRAW_POINTS;
                } else if (fullTimeOutcome.equals(AWAY_WIN)) {
                    sum += LOSS_POINTS;
                }
                ++count;
                //System.out.println(sum);
            } else if (stats.get(recordID).get(Keys.HomeTeam.ordinal()).equals(guestName) 
                && stats.get(recordID).get(Keys.AwayTeam.ordinal()).equals(hostName)) {
                if (fullTimeOutcome.equals(HOST_WIN)) {
                    sum += LOSS_POINTS;
                } else if (fullTimeOutcome.equals(DRAW)) {
                    sum += DRAW_POINTS;
                } else if (fullTimeOutcome.equals(AWAY_WIN)) {
                    sum += WIN_POINTS;
                }
                //System.out.println(sum);
                ++count;
            }
            --recordID;
        }
        
        if (sum == 0) {
            return 1;
        }
        
        return sum / (double) matchCount;
    }
    
    double evaluateForm(String teamName, int matchCount) {
        int sum = 0, count = 0, recordID = getStartRecordID();
        while (count < matchCount && recordID > 1) {
            String fullTimeOutcome = stats.get(recordID).get(Keys.FTR.ordinal());
            
            if (stats.get(recordID).get(Keys.HomeTeam.ordinal()).equals(teamName)) {
                if (fullTimeOutcome.equals(HOST_WIN)) {
                    sum += 2;
                } else if (fullTimeOutcome.equals(DRAW)) {
                    sum += 1;
                }
                ++count;
            } else if (stats.get(recordID).get(Keys.AwayTeam.ordinal()).equals(teamName)) {
                if (fullTimeOutcome.equals(AWAY_WIN)) {
                    sum += 2;
                } else if (fullTimeOutcome.equals(DRAW)) {
                    sum += 1;
                }
                ++count;
            }
            --recordID;
        }
        return sum / (double) (2 * matchCount);
    }
    
    public String getHostName() {
        return hostName;
    }

    private boolean isValidTeamName(String teamName) {
        if (!validateNames) {
            return true;
        }
        
        for (String name : teamNames) {
            if (teamName.equals(name)) {
                return true;
            }
        }
        return false;
    }
    
    public void setHostName(String hostName) {
        if (!isValidTeamName(hostName)) {
            throw new IllegalArgumentException("hostName doesn\'t exist in teamNames!");
        }
        this.hostName = hostName;
    }

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        if (!isValidTeamName(guestName)) {
            throw new IllegalArgumentException("guestName doesn\'t exist in teamNames!");
        }
        this.guestName = guestName;
    }
    
    public Statistics getStats() {
        return stats;
    }
    
    public String[] getLeagueTeams() {
        return teamNames;
    }
    
    public int getStartRecordID() {
        return startRecordID;
    }

    public void setStartRecordID(int startRecordID) {
        this.startRecordID = startRecordID;
    }
    
    public static void main(String[] args) {
        MathModel mm = new MathModel("Arsenal", "Chelsea", Statistics.EPL_MATCHES, Statistics.EPL_TEAM_NAMES);
        System.out.println(mm.calculateProbabilities());
    }

    public Conditional<Boolean> getIterateCondition() {
        return iterateCondition;
    }

    public void setIterateCondition(Conditional<Boolean> iterateCondition) {
        this.iterateCondition = iterateCondition;
    }

    public boolean validatesNames() {
        return validateNames;
    }

    public void setValidateNames(boolean validateNames) {
        this.validateNames = validateNames;
    }

}
