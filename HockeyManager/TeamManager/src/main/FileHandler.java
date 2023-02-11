//
//  Copyright (C) 2022-2023 Rasmus Säämänen, all rights reserved.
//

package main;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class FileHandler extends JFrame{
	
	public static SimpleDateFormat formatter = new SimpleDateFormat("d.M.yyyy");

	// Reads players.csv and creates ArrayList.
	
	public static ArrayList<Player> getPlayerArray() throws Exception{
		
		File file = new File("src//players.csv");
		
		if ( !file.exists() )
			file.createNewFile();
		
		ArrayList<Player> playerArray = new ArrayList<Player>(); 		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;	
		
		try {
			while ((line = br.readLine()) != null) { 	
				String[] value = line.split(";");		
				playerArray.add(new Player(value[0], value[1]));
			}			
		} 
		catch (NumberFormatException e) {			
			e.printStackTrace();
		}
		
		br.close();	
		
		// Sort ArrayList by name.
		
		Collections.sort(playerArray, new Comparator<Player>(){
			@Override
			public int compare(Player o1, Player o2) {
				return o1.getName().compareToIgnoreCase(o2.getName());
			}
		});
		
		return playerArray;
	}
	
	// Reads matches.csv and creates ArrayList.
	
	public static ArrayList<Match> getMatchArray() throws Exception{
		
		File file = new File("src//matches.csv");
		
		if ( !file.exists() ) 
			file.createNewFile();
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		ArrayList<Match> matchArray = new ArrayList<Match>(); 			
		String line;
		
		try {
			while ( (line = br.readLine()) != null ) {
				String[] value = line.split(";");	
				matchArray.add( new Match(formatter.parse(value[0]), value[1]) );			
			}
			for ( Match m : matchArray ) {				
				for ( Goal g : getGoalArray() ) {
					if ( g.getDate().equals(m.getDate()) ) {
						m.setGoal(g);
					}
				}
			}
		} 
		
		catch (NumberFormatException e) {			
			e.printStackTrace();
		}
		
		// Sort matches by date.
		
		Collections.sort(matchArray, new Comparator<Match>() {
			
			@Override
			public int compare(Match o1, Match o2) {
				return o2.getDate().compareTo(o1.getDate());
			}
			
		});
		
		br.close();	
		return matchArray;
	}
	
	// Reards goals.csv and creates ArrayList.
	
	public static ArrayList<Goal> getGoalArray() throws Exception{
		
		File file = new File("src//goals.csv");
		
		if ( !file.exists() )
			file.createNewFile();
	
		ArrayList<Goal> goalArray = new ArrayList<Goal>(); 		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		try {
			while ( (line = br.readLine()) != null ) { 	
				
				String[] value = line.split(";");		
				goalArray.add(new Goal(formatter.parse(value[0]),value[1],value[2],
									   Integer.valueOf(value[3]),
									   Integer.valueOf(value[4]),
									   Integer.valueOf(value[5])));
				
			}
		} catch (NumberFormatException e) {			
			e.printStackTrace();
		}		
		br.close();	
		
		return goalArray;
	}
	
	// findLine i.e. Finds correct line in .csv file to delete.
	
	public static int findLine(String fileName,
							   String searchValue) throws Exception {	
		
		File file = new File(fileName);
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		int result = 0;
		
		try {
			while ( (line = br.readLine()) != null ) {
				
				result ++;
				String[] value = line.split(";");
					if ( value[0].equals(searchValue) ) {
						br.close();	
						return result;					
					}		
					
			}	
		}
		
		catch (Exception e){
			e.printStackTrace();
		}	
		return result;
	}
	
	// Notes: Filepath is given by the button.
	// Index is given by findLine().
	
	// removeLine i.e. Deletes line in .csv file.
	
	public static void removeLine(String filepath, 
								  int    index) {		
		
		String tempFile = "src//temp.txt";
		
		File oldFile = new File(filepath);
		File newFile = new File(tempFile);
		int line = 0;
		String currentLine;
		
		try {
			FileWriter fw = new FileWriter(tempFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			
			while ( (currentLine = br.readLine()) != null ) {
				line++;
				
				if ( index != line ) 
					pw.println(currentLine);
	
			}			
			pw.flush();
			pw.close();
			fr.close();
			br.close();
			bw.close();
			fw.close();
			
			oldFile.delete();
			File dump = new File(filepath);
			newFile.renameTo(dump);
		}
		catch(Exception e) {
			System.out.println(e);
		}
	}
	
	// removeGoals
	
	public static void removeGoals(String searchValue) {
		
		String filepath = "src//goals.csv";
		String tempFile = "src//tempGoals.txt";
		File oldFile = new File("src//goals.csv");
		File newFile = new File(tempFile);
		String currentLine;
		
		try {
			FileWriter fw = new FileWriter(tempFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			
			FileReader fr = new FileReader(filepath);
			BufferedReader br = new BufferedReader(fr);
			
			while ( (currentLine = br.readLine()) != null ) {	
				
				if(currentLine.contains(searchValue)) 
					continue;
				
				else
					pw.println(currentLine);

			}
			pw.flush();
			pw.close();
			fr.close();
			br.close();
			bw.close();
			fw.close();
			
			oldFile.delete();
			File dump = new File(filepath);
			newFile.renameTo(dump);
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	// Export player statistics to text file.
	
	public static void exportStats() throws Exception {
		
		String today = formatter.format(new Date());
		
		File file = new File("src//player statistics-" + today + ".txt");
		
		if ( !file.exists() ) {
			file.createNewFile();
		}
		
		StringBuilder sb = new StringBuilder();		
		for ( Player p : getPlayerArray() ) {
			sb.append("\n" + p.getName() + "," + p.getPosition() + "," + p.getPoints() + "," + p.exportGoals() + "," + p.exportAssists() + ",");
		}
		
		String string = sb.toString();
		String[] test = string.split(",");
		sb.setLength(0);
		
		sb.append("Player Name                 " + "|Player Position              " + "|Total Points                 " + "|Goals                        " + "|Assists\n");
		sb.append("-----------------------------------------------------------------------------------------------------------------------------------------------------");
		
		for ( String s : test ) {
			sb.append(s);
			int numSpaces = 30 - s.length() -1;
			
			for ( int i = 0; i < numSpaces; i++ )
				sb.append(" ");

			sb.append("|");
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(sb.toString());
		
		
		// Open .txt file.
		Desktop desktop = Desktop.getDesktop();  
		desktop.open(file);
		bw.close();
	}
}
	

