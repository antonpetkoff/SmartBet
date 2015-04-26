package math.model;

import statistics.Keys;
import statistics.Statistics;

public class TestModel {

    private MathModel mm;
    
    public TestModel() {
        mm = new MathModel("Arsenal", "Chelsea", Statistics.EPL_MATCHES, Statistics.EPL_TEAM_NAMES);
    }

    private boolean isOutcomeGuessed(String expected, String jsonActual) {
        // TODO
        return false;
    }
    
    /**
     * Test the math model for each match based on the last N rounds before the match.
     * 
     * @param rounds    : number of league rounds to use for calculation of probabilities
     * @return
     */
    public double getAccuraccy(int rounds) {
        int guessedMatches = 0, totalMatches = 0, testableMatches = 0;
        String expected = null, jsonForecast = null;
        
        mm.setIterateCondition(new Conditional<Boolean>() {
            @Override
            public Boolean check(int matchCount) {
                return matchCount < 20*19;
            }
        });
        
        mm.setStartRecordID(mm.getStats().size() - 1);
        
        while (mm.getStartRecordID() > 20*19) {     // 19*20 = 380 matches in one league season
            mm.setHostName(mm.getStats().get(mm.getStartRecordID()).get(Keys.HomeTeam.ordinal()));
            mm.setGuestName(mm.getStats().get(mm.getStartRecordID()).get(Keys.AwayTeam.ordinal()));
            
            // TODO check if the match is testable!
            
            expected = mm.getStats().get(mm.getStartRecordID()).get(Keys.FTR.ordinal());
            
            mm.setStartRecordID(mm.getStartRecordID() - 1);
            jsonForecast = mm.calculateProbabilities();
            
            if (isOutcomeGuessed(expected, jsonForecast)) {
                ++guessedMatches;
            }
            
            ++totalMatches;
        }
        
        return guessedMatches / (double) testableMatches;
    }
    
}
