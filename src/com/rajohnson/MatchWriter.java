package com.rajohnson;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.achimala.leaguelib.models.MatchHistoryEntry;
import com.achimala.leaguelib.models.MatchHistoryStatType;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.LeagueChampion;

public class MatchWriter {

	
	String m_SummonerName;
	
	public MatchWriter() {
		// TODO Auto-generated constructor stub
	}
	public MatchWriter(String summonerName)
	{
		if(summonerName.length() > 0)
		{
			m_SummonerName = summonerName;
		}
		
	}
	
	public int writeMatch(ArrayList<MatchHistoryEntry> matches)
	{
	
		String fileName = m_SummonerName.toLowerCase() + ".csv";
		PrintWriter outFile = null;
		try{
			outFile = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			for(MatchHistoryEntry match : matches)
			{
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return 1;
		}
		finally
		{
			if(outFile != null)
			{
				outFile.close();
			}
		}
		
		
		return 0;
	}

}
