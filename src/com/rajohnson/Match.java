package com.rajohnson;

import java.util.ArrayList;
import java.util.Date;

import org.codehaus.jackson.annotate.*;
import org.ektorp.support.CouchDbDocument;

import com.achimala.leaguelib.models.MatchHistoryStatType;

public class Match extends CouchDbDocument{
	
	
	private int gameId;
	private String matchmakingQueue;
	private String championPlayed;
	private int win;
	private int numKills;
	private int numAssists;
	private int numDeaths;
	private int minionsKilled;
	private int goldEarned;
	private ArrayList<Integer> itemsBought;
	private int trinket;
	private Date datePlayed;
	
	public Match() {
		gameId = 0;
		matchmakingQueue = "Custom";
		championPlayed = "Urf";
		win = -1;
		numKills = 0;
		numAssists = 0;
		numDeaths = 0;
		minionsKilled = 0;
		goldEarned = 0;
		itemsBought = new ArrayList<Integer>(6);
		trinket = -1;
		datePlayed = new Date();
	}
	
	//Standard getters and setters for all data
	public int getGameId()
	{
		return gameId;
	}
	
	public void setGameId(int id)
	{
		gameId = id;
	}
	
	public String getMatchmakingQueue()
	{
		return matchmakingQueue;
	}
	
	public void setMatchmakingQueue(String queue)
	{
		matchmakingQueue = queue;
	}
	
	public String getChampionPlayed()
	{
		return championPlayed;
	}
	
	public void setChampionPlayed(String champion)
	{
		championPlayed = champion;
	}
	
	public int getWin()
	{
		return win;
	}
	
	public void setWin(int winStatus)
	{
		win = winStatus;
	}
	
	public int getNumKills()
	{
		return numKills;
	}
	
	public void setNumKills(int killCount)
	{
		numKills = killCount;
	}
	
	public int getNumAssists()
	{
		return numAssists;
	}
	
	public void setNumAssists(int assistCount)
	{
		numAssists = assistCount;
	}
	
	public int getNumDeaths()
	{
		return numDeaths;
	}
	
	public void setNumDeaths(int deathCount)
	{
		numDeaths = deathCount;
	}
	
	public int getMinionsKilled()
	{
		return minionsKilled;
	}
	
	public void setMinionsKilled(int minionKillCount)
	{
		minionsKilled = minionKillCount;
	}
	
	public int getGoldEarned()
	{
		return goldEarned;
	}
	
	public void setGoldEarned(int amtGold)
	{
		goldEarned = amtGold;
	}
	
	public ArrayList<Integer> getItemsBought()
	{
		return itemsBought;
	}
	
	public void setItemsBought(ArrayList<Integer> newItems)
	{
		itemsBought = newItems;
	}
	
	public int getTrinket()
	{
		return trinket;
	}
	
	public void setTrinket(int newTrinket)
	{
		trinket = newTrinket;
	}
	
	public Date getDate()
	{
		return datePlayed;
	}
	
	public void setDate(Date matchDate)
	{
		datePlayed = matchDate;
	}

}
