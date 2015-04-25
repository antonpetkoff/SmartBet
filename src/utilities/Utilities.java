package utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import statistics.Keys;
import statistics.Statistics;

public class Utilities {
    
    private static String toTwoDigits(int number) {
        String result = String.valueOf(number);
        
        if (result.length() == 2) {
            return result;
        } else if (result.length() < 2) {
            return "0" + result;
        }
        return result.substring(result.length() - 2, result.length());
    }
    
    public static void changeDateFormat(String destination) {
        Statistics stats = new Statistics();
        StringBuilder result = new StringBuilder();
        
        for (int i = 1; i < stats.size(); ++i) {
            String originalDate = stats.get(i).get(Keys.Date.ordinal());
            String[] parts = originalDate.split("/");
            parts[2] = toTwoDigits(Integer.valueOf(parts[2]));
            
            result.append(parts[0] + "/" + parts[1] + "/" + parts[2]);
            
            for(int j = 1; j < 21; ++j) {
                result.append("," + stats.get(i).get(j));
            }
            result.append('\n');
        }
        
        File writeFile = new File(destination);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(writeFile))) {
            writer.write(result.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
}
