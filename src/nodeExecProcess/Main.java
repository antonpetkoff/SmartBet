package nodeExecProcess;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Main {

	public static void main(String[] args) {
		String input = args[0];
		String outputFile = args[1];
		
		PrintWriter writer;
		try {
			writer = new PrintWriter(outputFile, "UTF-8");
			writer.println("{data:'hello'}");
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
