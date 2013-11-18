package com.rajohnson;

import org.ektorp.*;
import org.ektorp.impl.*;
import org.ektorp.http.*;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.achimala.leaguelib.connection.LeagueAccount;
import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.connection.LeagueServer;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueChampion;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.MatchHistoryEntry;
import com.achimala.leaguelib.models.MatchHistoryStatType;

@SuppressWarnings("serial")
public class StatController extends JFrame{

	
	public StatController()
	{
		setLayout(new BorderLayout());
		setTitle("LoL Stat Tracker");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		JTabbedPane mainWindow = new JTabbedPane(JTabbedPane.LEFT);
		
		JDialog loginDialog = new JDialog(this, "Login to update history");
		JButton loginButton = new JButton("Log On");
		loginButton.setActionCommand("logon");
		JPanel loginDialogPanel = new JPanel();
		loginDialogPanel.setLayout(new BoxLayout(loginDialogPanel, BoxLayout.Y_AXIS));
		JLabel loginLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		JTextField usernameField = new JTextField();
		JPasswordField passwordField = new JPasswordField();
		//passwordField.addActionListener(loginButton);
		loginDialogPanel.add(loginLabel);
		loginDialogPanel.add(usernameField);
		loginDialogPanel.add(passwordLabel);
		loginDialogPanel.add(loginButton);
		loginDialog.add(loginDialogPanel);
		
		HttpClient httpClient = new StdHttpClient.Builder().build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector db = dbInstance.createConnector("hydrophobic-stats", true);
     	MatchRepository mr = new MatchRepository(db, "hydrophobic");
		Vector<Match> matchesPlayed = mr.getAllMatches();
		JList<Match> matchesList = new JList<Match>(matchesPlayed);
		matchesList.setCellRenderer(new MatchCellRenderer<Match>());
		
		
		JScrollPane matchScroller =  new JScrollPane(matchesList);
		
		JPanel recentMatchPanel = new JPanel();
		recentMatchPanel.add(matchScroller);
		JPanel championPerformancePanel = new JPanel();
		
		JButton updateButton = new JButton("Update History");
		
		updateButton.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e)
			{
				//loginDialog.setVisible(true);
			}
			
		});
		
		
		mainWindow.addTab("Recent Matches", recentMatchPanel);
		mainWindow.addTab("Champion Performance", championPerformancePanel);
		
		this.add(mainWindow, BorderLayout.LINE_START);
		this.add(updateButton, BorderLayout.PAGE_END);
		
		setPreferredSize(new Dimension(700,700));
		
		
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		
		StatController sc = new StatController();
		sc.setLocationRelativeTo(null);
		sc.pack();
		sc.setVisible(true);
		
		
		HttpClient httpClient = new StdHttpClient.Builder().build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		
		
		String username, pword, summonerName;
		System.out.print("Enter your login name: ");
		Scanner scan = new Scanner(System.in);
		username = scan.nextLine();
		// Clear whitespace
		System.out.print("\nEnter your password: ");
		pword = scan.nextLine();
		System.out.print("Enter the summoner you would like to check stats for: ");
		summonerName = scan.nextLine();
		
		final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
		c.getAccountQueue().addAccount(new LeagueAccount(LeagueServer.NORTH_AMERICA, "3.13.xx", username,pword));
		System.out.println("Before connectAll()");
		Map<LeagueAccount, LeagueException> exceptions = c.getAccountQueue().connectAll();
        if(exceptions != null) {
            for(LeagueAccount account : exceptions.keySet())
                System.out.println(account + " error: " + exceptions.get(account));
            scan.close();
            return;
        }
        
        // if the second parameter is true, the database will be created if it doesn't exists
        String dbname = summonerName + "-stats";
     	CouchDbConnector db = dbInstance.createConnector(dbname, true);
     	MatchRepository mr = new MatchRepository(db, summonerName);
        System.out.println("After connectAll()");
        
        
        MatchWriter mw = new MatchWriter(summonerName);
        // Ifthe summoner doesn't exist then we won't open the dbConnector.
        //TODO: Move this line up. 
        LeagueSummoner testSummoner = c.getSummonerService().getSummonerByName(summonerName);
        c.getPlayerStatsService().fillMatchHistory(testSummoner);
        ArrayList<MatchHistoryEntry> recentMatches = (ArrayList<MatchHistoryEntry>) testSummoner.getMatchHistory();
        HashSet<LeagueChampion> champsPlayed = new HashSet<LeagueChampion>();
        
        //mw.writeMatches(recentMatches, testSummoner);
        Match matchToWrite;
        ArrayList<Match> matchesToWrite = new ArrayList<Match>();
        for(MatchHistoryEntry match : recentMatches)
        {
        	matchToWrite = new Match();
        	LeagueChampion  matchChamp = match.getChampionSelectionForSummoner(testSummoner);
        	System.out.println("Played game " + match.getQueue() + " as " + matchChamp);
        	System.out.println("\tKills: " + match.getStat(MatchHistoryStatType.CHAMPIONS_KILLED) + " Assists: " + match.getStat(MatchHistoryStatType.ASSISTS) + " Deaths: " + match.getStat(MatchHistoryStatType.NUM_DEATHS));
        	System.out.println("\tMinion Kills: " + Integer.toString(match.getStat(MatchHistoryStatType.MINIONS_KILLED) + match.getStat(MatchHistoryStatType.NEUTRAL_MINIONS_KILLED)) + 
        			" Gold Earned: " + match.getStat(MatchHistoryStatType.GOLD_EARNED));
        	System.out.println("\t" + match.getStat(MatchHistoryStatType.ITEM0) + " " + match.getStat(MatchHistoryStatType.ITEM1));
        	System.out.println("\tVictory: " + match.getStat(MatchHistoryStatType.WIN));
        	System.out.println("\tGame ID: " + match.getGameId());
        	
        	matchToWrite.setGameId(match.getGameId());
        	matchToWrite.setMatchmakingQueue(match.getQueue().name());
        	matchToWrite.setChampionPlayed(matchChamp.getName());
        	matchToWrite.setWin(match.getStat(MatchHistoryStatType.WIN));
        	matchToWrite.setNumKills(match.getStat(MatchHistoryStatType.CHAMPIONS_KILLED));
        	matchToWrite.setNumAssists(match.getStat(MatchHistoryStatType.ASSISTS));
        	matchToWrite.setNumDeaths(match.getStat(MatchHistoryStatType.NUM_DEATHS));
        	matchToWrite.setMinionsKilled(match.getStat(MatchHistoryStatType.MINIONS_KILLED) + match.getStat(MatchHistoryStatType.NEUTRAL_MINIONS_KILLED));
        	matchToWrite.setGoldEarned(match.getStat(MatchHistoryStatType.GOLD_EARNED));
        	matchToWrite.setDate(match.getCreationDate());
        	
        	ArrayList<Integer> itemsBought = new ArrayList<Integer>(6);
        	itemsBought.add(match.getStat(MatchHistoryStatType.ITEM0));
        	itemsBought.add(match.getStat(MatchHistoryStatType.ITEM1));
        	itemsBought.add(match.getStat(MatchHistoryStatType.ITEM2));
        	itemsBought.add(match.getStat(MatchHistoryStatType.ITEM3));
        	itemsBought.add(match.getStat(MatchHistoryStatType.ITEM4));
        	itemsBought.add(match.getStat(MatchHistoryStatType.ITEM5));
        	matchToWrite.setItemsBought(itemsBought);
        	
        	matchesToWrite.add(matchToWrite);
        	//mr.add(matchToWrite);
        }
        mr.addNewMatches(matchesToWrite);
        mr.printStatsByChampionPlayed("Doesn't matter");
        scan.close();
        
	}

}
