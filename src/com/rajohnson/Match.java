package com.rajohnson;

import java.util.ArrayList;
import java.util.Date;

import org.ektorp.support.CouchDbDocument;

@SuppressWarnings("serial")
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
		datePlayed = new Date(0);
	}
	
	/**
	 * Returns the gameId for the given {@code Match}
	 * @return The {@code int} gameId.
	 */
	public int getGameId()
	{
		return gameId;
	}
	
	/**
	 * Sets the gameId for the given {@code Match}.
	 * @param id The desired {@code id} for the {@code Match}.
	 */
	public void setGameId(int id)
	{
		gameId = id;
	}
	
	/**
	 * Returns the matchmaking queue for the given {@code Match}.
	 * @return The {@code String} representation of the matchmaking queue for the given {@code Match}.
	 */
	public String getMatchmakingQueue()
	{
		return matchmakingQueue;
	}
	
	/**
	 * Sets the matchmaking queue for the given {@code Match}.
	 * @param queue The desired matchmaking queue for the given {@code Match}.
	 */
	public void setMatchmakingQueue(String queue)
	{
		matchmakingQueue = queue;
	}
	
	/**
	 * Returns the champion used by the player in the given {@code Match}.
	 * @return The {@code String} representation of the summoner's champion for the given {@code Match}.
	 */
	public String getChampionPlayed()
	{
		return championPlayed;
	}
	
	/**
	 * Sets the champion played for the given {@code Match}.
	 * @param champion The desired champion for the given {@code Match}.
	 */
	public void setChampionPlayed(String champion)
	{
		championPlayed = champion;
	}
	
	/**
	 * Returns whether or not the player won a given {@code Match}.
	 * @return 1 if the game is a victory, 0 if it is a defeat.
	 */
	public int getWin()
	{
		return win;
	}
	
	/**
	 * Sets whether or not a game was a victory for the summoner.
	 * @param winStatus 1 if the game should be a victory, 0 if it should be a defeat.
	 */
	public void setWin(int winStatus)
	{
		win = winStatus;
	}
	
	/**
	 * Returns the number of champions killed by the player in a given {@code Match}.
	 * @return The number of champions killed on this {@code Match}.
	 */
	public int getNumKills()
	{
		return numKills;
	}
	
	/**
	 * Sets the number of champions killed by the player in a given {@code Match}.
	 * @param killCount The number of champions killed in this {@code Match}.
	 */
	public void setNumKills(int killCount)
	{
		numKills = killCount;
	}
	
	/**
	 * Returns the number of assists earned by the player in a given {@code Match}.
	 * @return The number of assists earned by the player in a given {@code Match}. 
	 */
	public int getNumAssists()
	{
		return numAssists;
	}
	
	/**
	 * Sets the number of assists earned in the given {@code Match}.
	 * @param assistCount The number of assists earned in the given {@code Match}. 
	 */
	public void setNumAssists(int assistCount)
	{
		numAssists = assistCount;
	}
	
	/**
	 * Gets the number of times the player died in a given {@code Match}.
	 * @return The number of times the player died in the {@code Match}.
	 */
	public int getNumDeaths()
	{
		return numDeaths;
	}
	
	/**
	 * Sets the number of times the player died in a given {@code Match}.
	 * @param deathCount The number of times the player died in this {@code Match}.
	 */
	public void setNumDeaths(int deathCount)
	{
		numDeaths = deathCount;
	}
	
	/**
	 * Gets the number of minions killed by the player (neutral and enemy minions) in a given {@code Match}.
	 * @return The number of neutral and enemy minions killed by the player.
	 */
	public int getMinionsKilled()
	{
		return minionsKilled;
	}
	
	/**
	 * Sets the number of minions killed by the player (neutral and enemy minions) in a given {@code Match}.
	 * @param minionKillCount The number of minions killed by the player in the {@code Match}.
	 */
	public void setMinionsKilled(int minionKillCount)
	{
		minionsKilled = minionKillCount;
	}
	
	/**
	 * Gets the amount of gold earned by the player in a given {@code Match}.
	 * @return The amount of gold earned by the player.
	 */
	public int getGoldEarned()
	{
		return goldEarned;
	}
	
	/**
	 * Sets the amount of gold earned by the player in a given {@code Match}.
	 * @param amtGold The amount of gold earned by the player.
	 */
	public void setGoldEarned(int amtGold)
	{
		goldEarned = amtGold;
	}
	
	/**
	 * Gets a list of the items bought by the player in a given {@code Match}.
	 * @return An {@code ArrayList<Integer>} corresponding to the items bought by the player.
	 */
	public ArrayList<Integer> getItemsBought()
	{
		return itemsBought;
	}
	
	/**
	 * Sets the items bought by the player in a given {@code Match}.
	 * @param newItems An {@code ArrayList<Integer>} corresponding to the items bought by the player in a given match.
	 */
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
	
	@Override
	public String toString()
	{
		String result = "Played game " + matchmakingQueue + " as " + championPlayed;
    	result += "\n\tKills: " + numKills + " Assists: " + numAssists + " Deaths: " + numDeaths;
    	result += "\n\tMinion Kills: " + minionsKilled + " Gold Earned: " + goldEarned;
    	result += "\n\t" + itemsBought;
    	result += "\n\tVictory: " + win;
    	result += "\n\tGame ID: " + gameId;
    	
    	return result;
	}

}
