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
	private String clientVersion;
	
	public InfoLoader() {
		 summonerList = new ArrayList<String>();
		 mainSummoner = new String();
		 loginId = "";
		 summonerServerMap = new HashMap<String,LeagueServer>();
		 mainServer = LeagueServer.NORTH_AMERICA;
		 clientVersion = "1.00";
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
	
	/**
	 * Removes the summoner {@code summonerName}. If this summoner was the 
	 * main summoner, then the main summoner is changed to either no one (if 
	 * there are no more summoners in the {@code summonerList}) or the first summoner
	 * in the {@code summonerList}.
	 * @param summonerName The name of a summoner that is currently being tracked by
	 * this machine's instance of LoLStatTracker. 
	 */
	public void removeSummoner(String summonerName)
	{
		if(summonerList.contains(summonerName))
		{
			summonerList.remove(summonerName);
		}
		if(mainSummoner.equals(summonerName))
		{
			if(summonerList.size() < 1)
			{
				mainSummoner = new String();
			}
			else
			{
				mainSummoner = summonerList.get(0);
			}
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
			clientVersion = rootNode.path("client version").getTextValue();
			Iterator<String> fieldIterator = rootNode.getFieldNames();
			while(fieldIterator.hasNext())
			{
				String currentField = fieldIterator.next();
				if(!currentField.startsWith("summoner"))
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
	
	public String getClientVersion()
	{
		return clientVersion;
	}
	
	/**
	 * Sets the {@code clientVersion} parameter to be {@code newClientVersion} as long
	 * as the client version string matches the exact format: "#.##". If the format
	 * is updated then the config file is updated automatically.
	 * @param newClientVersion
	 * @return True if the client version is set successfully.
	 */
	public boolean setClientVersion(String newClientVersion)
	{
		if(newClientVersion.matches("[0-9]\\.[0-9]{2}"))
		{
			System.out.println("The new client version is better!");
			clientVersion = newClientVersion;
			writeInfoToResourceFile();
			return true;
		}
		return false;
	}
	
	public boolean writeInfoToResourceFile(String resourceFileName)
	{
		ObjectMapper objMapper = new ObjectMapper();
		ObjectNode infoObj = objMapper.createObjectNode();
		infoObj.put("loginId", loginId);
		infoObj.put("mainSummoner",mainSummoner);
		infoObj.put("client version", clientVersion);
		infoObj.put("mainServer", mainServer.getServerCode());
		

		int i = 0;
		for(String summonerName : summonerList)
		{
			infoObj.put("summoner" + i,summonerName);
			i++;
		}
		
		try {
			objMapper.writeValue(new File(resourceFileName),infoObj);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean writeInfoToResourceFile()
	{
		return writeInfoToResourceFile("res/configItems/userConfig.json");
	}
}
