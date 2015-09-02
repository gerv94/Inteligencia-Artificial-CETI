package printerController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogPrinter {
	private PrintWriter logWriter;
	private File file;
	
	public LogPrinter(){
		file = new File("log.txt");
		newLogWrite(false, "", false);
	}
	
	public LogPrinter(String fileName){
		file = new File(fileName);
		newLogWrite(false, "", false);
	}
	
	private void newLogWrite(boolean append, String str, boolean println){
		try {
			logWriter = new PrintWriter(new FileWriter(file, append));
			if(println){
				logWriter.println(str);
				System.out.println(str);
			}else{
				logWriter.print(str);
				System.out.print(str);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			logWriter.close();
		}
	}
	
	public void print(String str){
		newLogWrite(true, str, false);
	}
	public void println(String str){
		newLogWrite(true, str, true);
	}
}