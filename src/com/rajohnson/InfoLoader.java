package com.rajohnson;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
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
	private String configFileLocation;
	
	public InfoLoader() throws URISyntaxException {
		 summonerList = new ArrayList<String>();
		 mainSummoner = new String();
		 loginId = "";
		 summonerServerMap = new HashMap<String,LeagueServer>();
		 mainServer = LeagueServer.NORTH_AMERICA;
		 clientVersion = "1.00";

		 String path = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().toString();
		 int indexOfColon = path.indexOf(':');
		 String parentPath = "";
		 if(path.endsWith("bin/"))
		 {
			 parentPath = (new File(path.substring(indexOfColon+1,path.length()))).getParentFile().getPath() + "/";
		 } 
		 //If the path doesn't end with bin/, then the runtime may not be development. The path will be taken from the normal install location (%USERPROFILE%\LoLStatTracker
		 else
		 {
			 parentPath = System.getenv("USERPROFILE") + "/LoLStatTracker/";
		 }
		 configFileLocation = parentPath + "res/configItems/userConfig.json";

	}
	
	public void addSummoner(String summonerName)
	{
		if(!summonerList.contains(summonerName))
		{
			summonerList.add(summonerName);
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
		//Get the location of the class files at runtime, backtrack a level to the root directory, 
		//Then get the path to the resource directory. 
		try {
			File inFile = new File(configFileLocation);
		
			if(!inFile.exists())
			{
				//The file doesn't exist. Create it with default empty values. 
				writeInfoToResourceFile();
			}
		
			//JsonNode rootNode = objMapper.readTree(new File("./res/configItems/userConfig.json"));
			//Get the location of the class files at runtime, backtrack a level to the root directory, 
			//Then get the path to the resource directory. 
			JsonNode rootNode = objMapper.readTree(inFile);
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
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
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
			objMapper.writeValue(new File(resourceFileName), infoObj);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean writeInfoToResourceFile()
	{
		return writeInfoToResourceFile(configFileLocation);
	}
}
