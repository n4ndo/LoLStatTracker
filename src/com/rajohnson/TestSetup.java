package com.rajohnson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.achimala.leaguelib.connection.LeagueAccount;
import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.connection.LeagueServer;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueChampion;
import com.achimala.leaguelib.models.LeagueGame;
import com.achimala.leaguelib.models.LeagueRankedStatType;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.LeagueSummonerLeagueStats;
import com.achimala.leaguelib.models.MatchHistoryEntry;
import com.achimala.leaguelib.models.MatchHistoryStatType;
import com.achimala.util.Callback;
import com.rajohnson.KDA;
import com.gvaneyck.rtmp.*;
import com.google.gson.*;

public class TestSetup {

	
	 private static int count = 0;
	    private static ReentrantLock lock = new ReentrantLock();
	    private static Condition done = lock.newCondition();
	    
	    private static void incrementCount() {
	        lock.lock();
	        count++;
	        // System.out.println("+ count = " + count);
	        lock.unlock();
	    }
	    
	    private static void decrementCount() {
	        lock.lock();
	        count--;
	        if(count == 0)
	            done.signal();
	        // System.out.println("- count = " + count);
	        lock.unlock();
	    }
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		String username, pword, summonerName;
		System.out.print("Enter your login name: ");
		Scanner sc = new Scanner(System.in);
		username = sc.nextLine();
		// Clear whitespace
		System.out.print("\nEnter your password: ");
		pword = sc.nextLine();
		System.out.println("ID: " + username + " PWD: " + pword);
		System.out.print("Enter the summoner you would like to check stats for: ");
		summonerName = sc.nextLine();
		
		final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
		c.getAccountQueue().addAccount(new LeagueAccount(LeagueServer.NORTH_AMERICA, "3.12.xx", username,pword));
		
		Map<LeagueAccount, LeagueException> exceptions = c.getAccountQueue().connectAll();
        if(exceptions != null) {
            for(LeagueAccount account : exceptions.keySet())
                System.out.println(account + " error: " + exceptions.get(account));
            return;
        }
        
        
        
        LeagueSummoner testSummoner = c.getSummonerService().getSummonerByName(summonerName);
        c.getPlayerStatsService().fillMatchHistory(testSummoner);
        ArrayList<MatchHistoryEntry> recentMatches = (ArrayList<MatchHistoryEntry>) testSummoner.getMatchHistory();
        HashSet<LeagueChampion> champsPlayed = new HashSet<LeagueChampion>();
        HashMap<LeagueChampion,KDA> champPerformance = new HashMap<LeagueChampion,KDA>();
        for(MatchHistoryEntry match : recentMatches)
        {
        	LeagueChampion  matchChamp = match.getChampionSelectionForSummoner(testSummoner);
        	KDA matchKDA = new KDA(match.getStat(MatchHistoryStatType.CHAMPIONS_KILLED), match.getStat(MatchHistoryStatType.NUM_DEATHS),match.getStat(MatchHistoryStatType.ASSISTS));
        	System.out.println("Played game " + match.getQueue() + " as " + matchChamp);
        	System.out.println("\tKills: " + match.getStat(MatchHistoryStatType.CHAMPIONS_KILLED) + " Assists: " + match.getStat(MatchHistoryStatType.ASSISTS) + " Deaths: " + match.getStat(MatchHistoryStatType.NUM_DEATHS));
        	System.out.println("\tMinion Kills: " + match.getStat(MatchHistoryStatType.MINIONS_KILLED) + " Gold Earned: " + match.getStat(MatchHistoryStatType.GOLD_EARNED));
        	System.out.println("\t" + match.getStat(MatchHistoryStatType.ITEM0) + " " + match.getStat(MatchHistoryStatType.ITEM1));
        	
      
        }
      	for(LeagueChampion champ : champPerformance.keySet())
    	{
    		System.out.println("Your performance with " + champ + " is a KDA of " + champPerformance.get(champ).calculateKDA());
    	}
    	
        
	}

}
