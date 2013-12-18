package com.rajohnson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.achimala.leaguelib.connection.LeagueServer;

public class InfoLoader {
	private ArrayList<String> summonerList;
	private String mainSummoner;
	private String loginId;
	private HashMap<String, LeagueServer> summonerServerMap;
	private LeagueServer mainServer;
	
	public InfoLoader() {
		 summonerList = new ArrayList<String>();
		 mainSummoner = new String();
		 loginId = "";
		 summonerServerMap = new HashMap<String,LeagueServer>();
		 mainServer = LeagueServer.NORTH_AMERICA;
	}
	
	public void addSummoner(String summonerName)
	{
		System.out.println("Summoner name: " + summonerName);
		if(!summonerList.contains(summonerName))
		{
			System.out.println(summonerName + " is not in the list of summoners.");
			summonerList.add(summonerName);
			System.out.println(summonerList);
		}
	}
	
	public void addSummonerAndServer(String summonerName, LeagueServer summonerServer)
	{
		if(!summonerList.contains(summonerName))
		{
			summonerServerMap.put(summonerName, summonerServer);
		}
	}
	
	public ArrayList<String> getSummonerList()
	{
		return summonerList;
	}
	
	/**
	 * Sets the main summoner for LoLStatTracker. If the summoner has not been first added to the list of summoners via
	 * {@link addSummoner} then setMainSummoner will do nothing.
	 * @param summonerName
	 */
	public void setMainSummoner(String summonerName)
	{
		if(summonerList.contains(summonerName))
		{
			mainSummoner = summonerName;
		}
	}
	
	public String getMainSummoner()
	{
		return mainSummoner;
	}
	
	public void setLoginId(String newId)
	{
		loginId = newId;
	}
	
	public String getLoginId()
	{
		return loginId;
	}
	
	public boolean loadInfoFromResourceFile()
	{
		ObjectMapper objMapper = new ObjectMapper();
		File inFile = new File("res/configItems/userConfig.json");
		if(!inFile.exists())
		{
			//The file doesn't exist. Create it with empty values. 
			writeInfoToResourceFile();
		}
		try {
			JsonNode rootNode = objMapper.readTree(new File("res/configItems/userConfig.json"));
			loginId = rootNode.path("loginId").getTextValue();
			mainSummoner = rootNode.path("mainSummoner").getTextValue();
			mainServer = LeagueServer.findServerByCode(rootNode.path("mainServer").getTextValue());
			Iterator<String> fieldIterator = rootNode.getFieldNames();
			while(fieldIterator.hasNext())
			{
				String currentField = fieldIterator.next();
				if(currentField.equals("loginId") || currentField.equals("mainSummoner")
						|| currentField.equals("mainServer"))
				{
					continue;
				}
				summonerList.add(rootNode.path(currentField).getTextValue());
			}
			
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	
	public boolean writeInfoToResourceFile()
	{
		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode infoObj = objMapper.createObjectNode();
		infoObj.put("loginId", loginId);
		infoObj.put("mainSummoner",mainSummoner);
		infoObj.put("mainServer", mainServer.getServerCode());

		int i = 0;
		for(String summonerName : summonerList)
		{
			infoObj.put("summoner" + i,summonerName);
			i++;
		}
		
		
		
		try {
			objMapper.writeValue(new File("res/configItems/userConfig.json"),infoObj);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * Checks whether or not the given summoner is already stored in the config file. 
	 * @param summonerName The name of the summoner to check.
	 * @return
	 */
	private boolean summonerIsStored(String summonerName)
	{
		for(String name : summonerList)
		{
			if(name.equalsIgnoreCase(summonerName))
			{
				return true;
			}
		}
		return false;
	}
}
