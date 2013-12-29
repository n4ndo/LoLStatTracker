package com.rajohnson;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * LeagueItem is a class that defines the map of items and cost. This is taken from the LeagueLibExtended project on
 * BastBotSupport's github page. 
 * @author BastBotSupport
 * 
 */
public class LeagueItem {

	private static Map<Integer,Map<String,Object>> itemList = new HashMap<Integer, Map<String,Object>>();


	static{
		BufferedReader br = null;

		try {

			String sCurrentLine;
			br = new BufferedReader(new FileReader("res/configItems/itemlist.txt"));

			while ((sCurrentLine = br.readLine()) != null) {
				Map<String,Object> itemInfo = new HashMap<String,Object>();

				String[] itemID = sCurrentLine.split("=");
				String[] name = itemID[0].split("\\(");
				String itemCost = name[1].replace(")", "");
				itemInfo.put("Name", name[0]);
				itemInfo.put("Cost", itemCost.split(":")[1].trim());
				itemList.put(Integer.parseInt(itemID[1].trim()), itemInfo);
			}

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static Map<Integer,Map<String,Object>> getItemList(){
		return itemList;
	}

	public static String getItemNameFromId(Integer id){
		if(itemList.containsKey(id)){
			return itemList.get(id).get("Name").toString();
		}
		return "Empty";
	}

	
}
