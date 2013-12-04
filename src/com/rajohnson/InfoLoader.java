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
		 mainSummoner = "";
		 loginId = "";
		 summonerServerMap = new HashMap<String,LeagueServer>();
		 mainServer = LeagueServer.NORTH_AMERICA;
	}
	
	public void addSummoner(String summonerName)
	{
		if(!summonerList.contains(summonerName))
		{
			summonerList.add(summonerName);
		}
	}
	
	public void addSummonerAndServer(String summonerName, LeagueServer summonerServer)
	{
		if(!summonerList.contains(summonerName))
		{
			summonerServerMap.put(summonerName, summonerServer);
		}
	}
	
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
		File inFile = new File("res/userConfig.json");
		if(!inFile.exists())
		{
			//The file doesn't exist. Create it with empty values. 
			writeInfoToResourceFile();
		}
		try {
			JsonNode rootNode = objMapper.readTree(new File("res/userConfig.json"));
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
		//infoObj.putPOJO("summonerList", summonerList);
		/*for(String key : summonerServerMap.keySet())
		{
			infoObj.put(key, summonerServerMap.get(key).getServerCode());
		}*/
		int i = 0;
		for(String summonerName : summonerList)
		{
			infoObj.put("summoner" + i,summonerName);
		}
		
		
		
		try {
			objMapper.writeValue(new File("res/userConfig.json"),infoObj);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return true;
	}
	
	
	
	

}
