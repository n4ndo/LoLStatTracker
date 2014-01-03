package com.rajohnson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.codehaus.jackson.JsonProcessingException;

import org.ektorp.CouchDbConnector;
import org.ektorp.DocumentNotFoundException;
import org.ektorp.Revision;
import org.ektorp.ViewResult;
import org.ektorp.ViewResult.Row;

import org.ektorp.support.CouchDbRepositorySupport;

import com.achimala.leaguelib.connection.LeagueServer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;




public class MatchRepository extends CouchDbRepositorySupport<Match>{

	private String m_SummonerName;
	private boolean initialized = false;
	private static SimpleDateFormat dbDateFormat;
	private final String matchDocumentId = "_design/Match";
	private final String matchDocumentLocation = "res/configItems/match.json";
	
	//All matches should use the exact same date format, so only one date format needs to be created.
	static
	{
		dbDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.'SSSZ");
	}
	
	public MatchRepository(CouchDbConnector connector, String summonerName)
	{
		super(Match.class, connector);
		m_SummonerName = summonerName;
		initialized = true;
	}
	
	/**
	 * Adds an individual {@code Match} directly to the database.
	 */
	@Override
	public void add(Match entity)
	{
		super.add(entity);
	}
	
	/**
	 * Takes a list of Matches and adds the new matches to the database. 
	 * Because the LoLRTMPS client returns the 10 most recent matches, but 10 new matches may
	 * not have been played since the last time the database was updated, it is preferable to call this method over {@link add}.
	 * @param matches A {@code List} of matches to be added to the database. 
	 */
	public void addNewMatches(List<Match> matches)
	{
		ArrayList<Match> matchesToAdd = new ArrayList<Match>(getMatchesNotInDatabase(matches));
		for(Match m : matchesToAdd)
		{
			add(m);
		}
	}
	
	/**
	 * Returns all the matches for a summoner.
	 * @return An {@code ArrayList<Match>} of all the matches stored for a given summoner
	 */
	public ArrayList<Match> getAllMatches()
	{
		ArrayList<Match> matches = new ArrayList<Match>();
		
		
		try
		{
			ViewResult result = db.queryView(createQuery("all"));
			for(Row r : result.getRows())
			{
				matches.add(getMatchFromRow(r));
			}
		}
		catch(DocumentNotFoundException e)
		{
			String fileLocation = matchDocumentLocation;
			try(BufferedReader inFile = new BufferedReader(new FileReader(fileLocation)))
			{
				createStandardDesignDocument();
				ViewResult result = db.queryView(createQuery("all"));
				for(Row r : result.getRows())
				{
					matches.add(getMatchFromRow(r));
				}
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			catch(DocumentNotFoundException ex)
			{
				ex.printStackTrace();
				
			}
		}
		
		return matches;
		
	}
	
	/**
	 * Returns a vector of matches 
	 * @return
	 */
	public Vector<Match> getAllMatchesWithDate()
	{
		ArrayList<Match> matches = new ArrayList<Match>();
		try
		{
			ViewResult result = db.queryView(createQuery("by_date"));
			for(Row r : result.getRows())
			{
				matches.add(getMatchFromRow(r));
			}
		
		}
		catch(DocumentNotFoundException e)
		{
			String fileLocation = matchDocumentLocation;
			try(BufferedReader inFile = new BufferedReader(new FileReader(fileLocation)))
			{
				createStandardDesignDocument();
				ViewResult result = db.queryView(createQuery("by_date"));
				for(Row r : result.getRows())
				{
					matches.add(getMatchFromRow(r));
				}
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			catch(DocumentNotFoundException ex)
			{
				ex.printStackTrace();
				
			}
		}

		Vector<Match> matchesToReturn = new Vector<Match>(matches);
		Collections.reverse(matchesToReturn);
		return matchesToReturn;
	}
	
	/**
	 * Takes a list of matches and returns a list of the matches that are not already in the database. 
	 * Use this function to ensure that an individual {@link Match} is not added to the player's match history twice.
	 * @param matches A {@code List} of matches which may or may not contain matches in the database already. 
	 * @return A list of the matches found in the input argument 'matches' that are not in the database. 
	 */
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
		
		return matchesToCheck;
	}
	
	/**
	 * Takes a {@link ViewResult.Row} and returns a {@link Match} initialized with the information from that row.
	 * @param r A {@link ViewResult.Row} that contains information about some League of Legends map.
	 * @return The {@ Match} generated by extracting the information from the row r.
	 */
	private Match getMatchFromRow(Row r)
	{
		Match matchToReturn = new Match();
		
		JsonNode rNode = r.getValueAsNode();
		
		matchToReturn.setMatchmakingQueue(rNode.get("matchmakingQueue").textValue());
		matchToReturn.setChampionPlayed(rNode.get("championPlayed").textValue());
		matchToReturn.setWin(rNode.get("win").intValue());
		matchToReturn.setNumKills(rNode.get("numKills").intValue());
		matchToReturn.setNumDeaths(rNode.get("numDeaths").intValue());
		matchToReturn.setNumAssists(rNode.get("numAssists").intValue());
		matchToReturn.setMinionsKilled(rNode.get("minionsKilled").intValue());
		matchToReturn.setGoldEarned(rNode.get("goldEarned").intValue());
		matchToReturn.setId(rNode.get("_id").textValue());
		matchToReturn.setRevision(rNode.get("_rev").textValue());
		matchToReturn.setChampionId(rNode.get("championId").intValue());
		ArrayList<Integer> itemsBought = new ArrayList<Integer>();
		for(int i = 0; i < 6; i++)
		{
			itemsBought.add(rNode.get("itemsBought").get(i).intValue());
		}
		matchToReturn.setItemsBought(itemsBought);
		matchToReturn.setTrinket(rNode.get("trinket").intValue());
		if(rNode.has("date"))
		{
			String dateString = rNode.get("date").textValue();
			if (dateString.length() == 0)
			{
				System.out.println("This was a match without a date assigned when it originally was created.");
			}
			else
			{
				try {
					matchToReturn.setDate(dbDateFormat.parse(dateString));
				} catch (ParseException e) {
					System.err.println("The date received from the database was in an unexpected format: " + dateString);
					e.printStackTrace();
				}
			}
		}
		
		return matchToReturn;
	}
	
	/**
	 * Returns true if this match repository is connected to a CouchDB database
	 */
	public boolean isRepoConnected()
	{
		return initialized;
	}
	
	/**
	 * Checks whether or not the matchmaking queue is a bot queue.
	 * @param matchmakingQueue The {@code String} representation of one of the matchmaking queue enumerations.
	 * @return True if the queue is a bot queue, false if it is a player versus player queue. 
	 */
	public boolean isBotGame(String matchmakingQueue)
	{
		if(matchmakingQueue.equals("BOT") || matchmakingQueue.equals("BOT_3x3"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Accesses the name of the summoner associated with this match repository. 
	 * @return The {@code String} summoner name.
	 */
	public String getSummonerName()
	{
		return m_SummonerName;
	}
	
	/**
	 * Creates a list of the {@code Match}es played where the summoner used the champion {@code champName}
	 * @param champName The champion for which the matches are played. 
	 * @return A list of matches played by the champion {@code champName}.
	 */
	public List<Match> getMatchesByChampionPlayed(String champName)
	{
		ViewResult result = db.queryView(createQuery("by_championPlayed").key(champName));
		
		ArrayList<Match> matchesForChampion = new ArrayList<Match>();
		for(Row r : result.getRows())
		{
			matchesForChampion.add(getMatchFromRow(r));
		}
		
		return matchesForChampion;
	}
	
	
    public void createStandardDesignDocument() throws JsonProcessingException, IOException
    {
    	if(db.contains(matchDocumentId))
    	{
    		for(Revision r : db.getRevisions(matchDocumentId))
    		{
    			db.delete(matchDocumentId, r.getRev());
    		}
    	}
    	ObjectMapper objMapper = new ObjectMapper();
    	JsonNode rootNode = objMapper.readTree(new File("res/configItems/match.json"));
		String id = rootNode.get("_id").textValue();
		Iterator<String> fieldIterator = rootNode.fieldNames();
		while(fieldIterator.hasNext())
		{
			String currentField = fieldIterator.next();
			if(currentField.equals("_id") || currentField.equals("language"))
			{
				continue;
			}
			
		}
		db.create(matchDocumentId, rootNode);
    }
    
}
