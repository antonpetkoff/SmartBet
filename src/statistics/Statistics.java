package statistics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

/**
 * holds statistics from the start of the 2014/2015 EPL season until
 * the last played match
 * @author tony
 *
 */
public class Statistics {
    
    private static final String EPL_MATCHES = "res/epl_matches.csv";
    
    private List<CSVRecord> records;
    
    public Statistics() {
        try(CSVParser parser = new CSVParser(new BufferedReader(new FileReader(EPL_MATCHES)), CSVFormat.DEFAULT);) {
            records = parser.getRecords();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public int size() {
        return records.size();
    }
    
    /**
     * The returned CSVRecord contains 21 fields in the following format:
     * 
     * Date = Match Date (DD/MM/YYYY)
     * HomeTeam = Home Team
     * AwayTeam = Away Team
     * FTHG = Full Time Home Team Goals
     * FTAG = Full Time Away Team Goals
     * FTR = Full Time Result (H=Home Win, D=Draw, A=Away Win)
     * HTHG = Half Time Home Team Goals
     * HTAG = Half Time Away Team Goals
     * HTR = Half Time Result (H=Home Win, D=Draw, A=Away Win)
     * 
     * HS = Home Team Shots
     * AS = Away Team Shots
     * HST = Home Team Shots on Target
     * AST = Away Team Shots on Target
     * HF = Home Team Fouls Committed
     * AF = Away Team Fouls Committed
     * HC = Home Team Corners
     * AC = Away Team Corners
     * HY = Home Team Yellow Cards
     * AY = Away Team Yellow Cards
     * HR = Home Team Red Cards
     * AR = Away Team Red Cards
     * 
     * @param index
     * @return CSVRecord representing the fields of the record at position index
     */
    public CSVRecord get(int index) {
        return records.get(index);
    }
    
}
