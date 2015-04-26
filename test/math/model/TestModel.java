package math.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import statistics.Keys;
import statistics.Statistics;

public class TestModel {

    private static final String HOST_WIN = "H";
    private static final String DRAW = "D";
    private static final String AWAY_WIN = "A";
    private static final int MATHCES_PER_ROUND = 10;
    private static final double MATCH_COUNT_TESTABLE_THRESHOLD = 0.75;
    
    private MathModel mm;
    
    public TestModel() {
        mm = new MathModel("Arsenal", "Chelsea", Statistics.EPL_MATCHES, Statistics.EPL_TEAM_NAMES);
        mm.setValidateNames(false);
    }

    /**
     * Count the number of matches played by teamName from mm.getStartRecordID() to 
     * mm.getStartRecordID() - matchCount.
     * 
     * @param teamName
     * @param matchCount
     * @return
     */
    private int countPlayedMatches(String teamName, int matchCount) {
        if (mm.getStartRecordID() - matchCount < 1) {
            return 0;
        }
        
        int count = 0;
        for (int i = mm.getStartRecordID(); i > mm.getStartRecordID() - matchCount; --i) {
            if (mm.getStats().get(i).get(Keys.HomeTeam.ordinal()).equals(teamName)
                    || mm.getStats().get(i).get(Keys.AwayTeam.ordinal()).equals(teamName)) {
                ++count;
            }
        }
        return count;
    }
    
    private boolean isMatchTestable(String hostName, String guestName, int matchCount) {
        return countPlayedMatches(hostName, matchCount) > matchCount / MATHCES_PER_ROUND * MATCH_COUNT_TESTABLE_THRESHOLD
                && countPlayedMatches(guestName, matchCount) > matchCount / MATHCES_PER_ROUND * MATCH_COUNT_TESTABLE_THRESHOLD;
    }
    
    private boolean isOutcomeGuessed(String expected, String jsonActual) throws JSONException {
        JSONArray json = new JSONArray(jsonActual);
        JSONObject outcome = json.getJSONObject(0);
        
        String mostPossibleString = "1";
        double mostPossibleDouble = outcome.getDouble("1");
        
        if (outcome.getDouble("X") > mostPossibleDouble) {
            mostPossibleDouble = outcome.getDouble("X");
            mostPossibleString = "X";
        }
        
        if (outcome.getDouble("2") > mostPossibleDouble) {
            mostPossibleDouble = outcome.getDouble("2");
            mostPossibleString = "2";
        }
        
        if (expected.equals(HOST_WIN)) {
            return mostPossibleString.equals("1");
        } else if (expected.equals(DRAW)) {
            return mostPossibleString.equals("X");
        } else if (expected.equals(AWAY_WIN)) {
            return mostPossibleString.equals("2");
        }
        
        return false;
    }
    
    /**
     * Test the math model for each match based on the last N rounds before the match.
     * 
     * @param rounds    : number of league rounds to use for calculation of probabilities
     * @return
     */
    public double getAccuraccy(int rounds) {
        int totalMatches = 0;
        int guessedMatches = 0, testableMatches = 0;
        int pivotRecordID = 0;
        String expected = null, jsonForecast = null;
        
        mm.setStartRecordID(mm.getStats().size() - 1);
        
        while (mm.getStartRecordID() > rounds * MATHCES_PER_ROUND) {     // each round has 10 matches
            mm.setHostName(mm.getStats().get(mm.getStartRecordID()).get(Keys.HomeTeam.ordinal()));
            mm.setGuestName(mm.getStats().get(mm.getStartRecordID()).get(Keys.AwayTeam.ordinal()));
            expected = mm.getStats().get(mm.getStartRecordID()).get(Keys.FTR.ordinal());
            
            mm.setStartRecordID(mm.getStartRecordID() - 1);
            pivotRecordID = mm.getStartRecordID();
            
            mm.setIterateCondition(new MatchCountConditional(pivotRecordID, rounds * MATHCES_PER_ROUND));
            
            if (isMatchTestable(mm.getHostName(), mm.getGuestName(), rounds * MATHCES_PER_ROUND)) {
                ++testableMatches;
                jsonForecast = mm.calculateProbabilities();
                
                boolean isGuessed = false;
                try {
                    isGuessed = isOutcomeGuessed(expected, jsonForecast);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                
                if (isGuessed) {
                    ++guessedMatches;
                }
            }
            
            ++totalMatches;
        }
        System.out.println("totalMatches: " + totalMatches);
        System.out.println("testableMatches: " + testableMatches);
        System.out.println("guessedMatches: " + guessedMatches);
        return guessedMatches / (double) testableMatches;
    }
    
    public static void main(String[] args) {
        TestModel test = new TestModel();
        System.out.println(test.getAccuraccy(10));
    }
    
}
