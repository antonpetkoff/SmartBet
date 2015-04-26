package nodeExecProcess;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import statistics.Statistics;
import math.model.MathModel;

public class Main {

	public static void main(String[] args) {
        int hostID = Integer.valueOf(args[0]);
		int guestID = Integer.valueOf(args[1]);
		String outputFile = args[2];

        MathModel mm = new MathModel("Arsenal", "Chelsea", Statistics.EPL_MATCHES, Statistics.EPL_TEAM_NAMES);
        String[] teamNames = mm.getLeagueTeams();
        mm.setHostName(teamNames[hostID]);
        mm.setGuestName(teamNames[guestID]);
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(outputFile, "UTF-8");
			writer.println(mm.calculateProbabilities());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

}
