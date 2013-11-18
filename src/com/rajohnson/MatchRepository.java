package com.rajohnson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;
import org.ektorp.support.CouchDbRepositorySupport;

import com.fasterxml.jackson.databind.JsonNode;


public class MatchRepository extends CouchDbRepositorySupport<Match> {

	private String m_SummonerName;
	
	public MatchRepository(CouchDbConnector connector, String summonerName)
	{
		super(Match.class, connector);
		m_SummonerName = summonerName;
	}
	
	@Override
	public void add(Match entity)
	{
		super.add(entity);
	}
	
	public void addNewMatches(List<Match> matches)
	{
		ArrayList<Match> matchesToAdd = new ArrayList<Match>(getMatchesNotInDatabase(matches));
		for(Match m : matchesToAdd)
		{
			add(m);
		}
	}
	
	public Vector<Match> getAllMatches()
	{
		ViewResult result = db.queryView(createQuery("all"));
		ArrayList<Match> matches = new ArrayList<Match>();
		for(Row r : result.getRows())
		{
			matches.add(getMatchFromRow(r));
		}
		
		return new Vector<Match>(matches);
		
	}
	
	public void printStatsByChampionPlayed(String champion)
	{
		ViewResult result = db.queryView(createQuery("by_championPlayed").key("Olaf"));
		
		int kills = 0;
		int deaths = 0; 
		int assists = 0;
		int creepScore =  0;
		int numGames = 0;
		for(Row r : result.getRows())
		{
			System.out.println(r.getValueAsNode().get("numKills"));
			kills += r.getValueAsNode().get("numKills").intValue();
			deaths += r.getValueAsNode().get("numDeaths").intValue();
			assists += r.getValueAsNode().get("numAssists").intValue();
			creepScore += r.getValueAsNode().get("minionsKilled").intValue();
			numGames++;
		}
		
		System.out.println("Kills: " + (kills/numGames) + " Deaths: " + (deaths/numGames) + " Assists " + (assists/numGames) + " CS: " + (creepScore/numGames));
		
	}
	
	public void getGameIdAlreadyExists(int gameId)
	{
		try{
			ViewResult result = db.queryView(createQuery("by_gameId"));
			System.out.println(result.getRows().get(1).getKey());
		}
		catch(DocumentNotFoundException e)
		{
			System.out.println("The document doesn't exist. This is when I would need to copy my design document");
		}
		
	}
	
	public List<Match> getMatchesNotInDatabase(List<Match> matches)
	{
		
		ArrayList<Match> matchesToCheck = new ArrayList<Match>(matches);
		
		ViewResult gamesById = db.queryView(createQuery("by_gameId"));
		for(Row r : gamesById.getRows())
		{
			
			for(int i = 0;i < matchesToCheck.size();)
			{
				//If the match checked exists in the DB already, then remove it, else iterate again
				if(matchesToCheck.get(i).getGameId() == Integer.parseInt(r.getKey()))
				{
					matchesToCheck.remove(i);
				}
				else
				{
					i++;
				}
			}
		}
		
		System.out.println(matchesToCheck);
		
		return matchesToCheck;
	}
	
	private Match getMatchFromRow(Row r)
	{
		Match matchToReturn = new Match();
		
		
		/*
		 * gameId = 0;
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
		 */
		JsonNode rNode = r.getValueAsNode();
		
		matchToReturn.setMatchmakingQueue(rNode.get("matchmakingQueue").textValue());
		matchToReturn.setChampionPlayed(rNode.get("championPlayed").textValue());
		matchToReturn.setWin(rNode.get("win").intValue());
		matchToReturn.setNumKills(rNode.get("numKills").intValue());
		matchToReturn.setNumDeaths(rNode.get("numDeaths").intValue());
		matchToReturn.setNumAssists(rNode.get("numAssists").intValue());
		matchToReturn.setMinionsKilled(rNode.get("minionsKilled").intValue());
		matchToReturn.setGoldEarned(rNode.get("goldEarned").intValue());
		
		return matchToReturn;
	}
}
