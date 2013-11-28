package com.rajohnson;

import org.ektorp.*;
import org.ektorp.impl.*;
import org.ektorp.http.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
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
import javax.swing.ListModel;

import com.achimala.leaguelib.connection.LeagueAccount;
import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.connection.LeagueServer;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueChampion;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.MatchHistoryEntry;
import com.achimala.leaguelib.models.MatchHistoryStatType;

@SuppressWarnings("serial")
public class StatController extends JFrame implements ActionListener{

	
	private JDialog loginDialog;
	private JTextField usernameField;
	private JPasswordField passwordField;
	//JButton loginButton;
	private MatchRepository mr;
	private String currentSummoner = "Hydrophobic";
	private JList<Match> matchesList;
	private DefaultListModel<Match> matchListModel;
	
	/**
	 * Default constructor.
	 * Initializes the gui and creates a connection to the CouchDB for the default summoner. 
	 */
	public StatController()
	{
		setLayout(new BorderLayout());
		setTitle("LoL Stat Tracker");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setPreferredSize(new Dimension(948,800));
		
		JTabbedPane mainWindow = new JTabbedPane(JTabbedPane.LEFT);
		
		HttpClient httpClient = new StdHttpClient.Builder().build();
		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		CouchDbConnector db = dbInstance.createConnector("hydrophobic-stats", true);
     	mr = new MatchRepository(db, "hydrophobic");
		Vector<Match> matchesPlayed = mr.getAllMatchesWithDate();
		
		matchListModel = new DefaultListModel<Match>();
		for(Match m : matchesPlayed)
		{
			matchListModel.addElement(m);
		}
		matchesList = new JList<Match>(matchListModel);
		matchesList.setCellRenderer(new MatchCellRenderer<Match>());
		JScrollPane matchScroller =  new JScrollPane(matchesList);
		matchScroller.setOpaque(false);
		
		//Set constraints and create panel for recent matches
		JPanel recentMatchPanel = new JPanel();
		recentMatchPanel.setOpaque(true);
		recentMatchPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		
		recentMatchPanel.add(matchScroller,c);
		JPanel championPerformancePanel = new JPanel(){
				@Override
				public void paintComponent(Graphics g) {
			        super.paintComponent(g);
			        Graphics2D g2d = (Graphics2D) g;
			        Color color1 = new Color(241,234,212);
				    Color color2 = new Color(222,204,155);
				    int w = getWidth();
				    int h = getHeight();
				    GradientPaint gp = new GradientPaint(
				        0, 0, color1, 0, h, color2);
				    g2d.setPaint(gp);
				    g2d.fillRect(0, 0, w, h);
				}
		};
		JScrollPane champPerformancePane = new JScrollPane(addChampIconsToChampPerformance(), JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		champPerformancePane.setPreferredSize(new Dimension(760,700));
		championPerformancePanel.add(champPerformancePane);
		
		//championPerformancePanel.add(champPerformancePane);
		JButton updateButton = new JButton("Update History");
		updateButton.setActionCommand("update");
		updateButton.addActionListener(this);
		
		System.out.println(updateButton.getActionCommand());
		
		
		mainWindow.addTab("Recent Matches", recentMatchPanel);
		mainWindow.addTab("Champion Performance", championPerformancePanel);
		
		this.add(mainWindow, BorderLayout.LINE_START);
		this.add(updateButton, BorderLayout.PAGE_END);
		this.pack();	
	}
	
	public void actionPerformed(ActionEvent e)
	{
		System.out.println(e.getActionCommand());
		if(e.getActionCommand().equals("update"))
		{
			initializePasswordDialog();
			loginDialog.pack();
			loginDialog.setLocationRelativeTo(null);
			loginDialog.setVisible(true);
		}
		else if(e.getActionCommand().equals("logon"))
		{
			logonAndUpdate();
			loginDialog.setVisible(false);
		}
		else
		{
			if(mr.isRepoConnected())
			{
				mr.printStatsByChampionPlayed(e.getActionCommand());
			}
			else
			{
				HttpClient httpClient = new StdHttpClient.Builder().build();
				CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
				String dbname = currentSummoner + "-stats";
				CouchDbConnector db = dbInstance.createConnector(dbname, true);
				MatchRepository mr = new MatchRepository(db, currentSummoner);
			}
		}
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
		
		
		/*String username, pword, summonerName;
		System.out.print("Enter your login name: ");
		Scanner scan = new Scanner(System.in);
		username = scan.nextLine();
		// Clear whitespace
		System.out.print("\nEnter your password: ");
		pword = scan.nextLine();
		System.out.print("Enter the summoner you would like to check stats for: ");
		summonerName = scan.nextLine();
		
		final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
		c.getAccountQueue().addAccount(new LeagueAccount(LeagueServer.NORTH_AMERICA, "3.14.xx", username,pword));
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
        scan.close();*/
        
	}
	
	/**
	 * Creates the JPanel of buttons with each champion's icon that is used to bring up a player's performance with an individual champion.
	 * @return A 
	 */
	private JPanel addChampIconsToChampPerformance()
	{
		TreeSet<String> championNames = new TreeSet<String>(MatchCellRenderer.getChampionIconMap().keySet());
		int numRows = 0;
		if(championNames.size() % 6 == 0)
		{
			numRows = championNames.size() / 6;
		}
		else
		{
			numRows = (championNames.size() / 6) + 1;
		}
		JPanel championIconPanel = new JPanel()
		{
			@Override
			public void paintComponent(Graphics g) {
		        super.paintComponent(g);
		        Graphics2D g2d = (Graphics2D) g;
		        Color color1 = new Color(241,234,212);
			    Color color2 = new Color(222,204,155);
			    int w = getWidth();
			    int h = getHeight();
			    GradientPaint gp = new GradientPaint(
			        0, 0, color1, 0, h, color2);
			    g2d.setPaint(gp);
			    g2d.fillRect(0, 0, w, h);
			}
		};
		championIconPanel.setLayout(new GridBagLayout());
		int columnNum = 0;
		int rowNumberX = 0;
		for(String champ : championNames)
		{
			if(champ.equals("none"))
			{
				continue;
			}
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = columnNum;
			c.gridy = rowNumberX;
			JPanel champPanel = new JPanel();
			champPanel.setLayout(new GridLayout(2,1));
			ImageIcon champIcon = new ImageIcon(MatchCellRenderer.getChampIconByName(champ));
			JButton champButton = new JButton(champIcon);
			champButton.addActionListener(this);
			champButton.setActionCommand(champ);
			JLabel champName = new JLabel(champ);
			champName.setOpaque(false);
			
			champPanel.add(champButton);
			champPanel.add(champName);
			champPanel.setPreferredSize(new Dimension(90,100));
			champPanel.setOpaque(false);
			championIconPanel.add(champPanel,c);
			columnNum = (columnNum + 1) % 6;
			if(columnNum == 0)
			{
				rowNumberX++;
			}
		}
		return championIconPanel;
	}
	
	/**
	 * Connects to the League of Legends server using the username and password from the JTextField and JPassword field respectively
	 * in the logon dialog. 
	 */
	private void logonAndUpdate()
	{
		final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
		try {
			c.getAccountQueue().addAccount(new LeagueAccount(LeagueServer.NORTH_AMERICA, "3.14.xx", usernameField.getText(),
					new String(passwordField.getPassword())));
		} catch (LeagueException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//Connect 
		Map<LeagueAccount, LeagueException> exceptions = c.getAccountQueue().connectAll();
        if(exceptions != null) {
            for(LeagueAccount account : exceptions.keySet())
                System.out.println(account + " error: " + exceptions.get(account));
            return;
        }
        
        //Get summoner info by summoner name, get recent matches and write them to the database. 
        LeagueSummoner testSummoner;
        String summonerName = "Hydrophobic";
		try {
			testSummoner = c.getSummonerService().getSummonerByName(summonerName);
			c.getPlayerStatsService().fillMatchHistory(testSummoner);
	        ArrayList<MatchHistoryEntry> recentMatches = (ArrayList<MatchHistoryEntry>) testSummoner.getMatchHistory();
	        HttpClient httpClient = new StdHttpClient.Builder().build();
    		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
    		String dbname = summonerName + "-stats";
         	CouchDbConnector db = dbInstance.createConnector(dbname, true);
         	mr = new MatchRepository(db, summonerName);
         	
	        ArrayList<Match> mostRecentMatches = getMatchesToWrite(recentMatches, testSummoner);
	        
	        //Add the new matches to the display.
	        ArrayList<Match> matchesToAddToDisplay = (ArrayList<Match>)mr.getMatchesNotInDatabase(mostRecentMatches); 
	        for(Match m : matchesToAddToDisplay)
	        {
	        	 matchListModel.add(0,m);
	        }
	        mr.addNewMatches(mostRecentMatches);

	        
		} catch (LeagueException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}	
	
	protected ArrayList<Match> getMatchesToWrite(ArrayList<MatchHistoryEntry> recentMatches, LeagueSummoner player)
	{

			Match matchToWrite;
	        ArrayList<Match> matchesToWrite = new ArrayList<Match>();
	        for(MatchHistoryEntry match : recentMatches)
	        {
	        	matchToWrite = new Match();
	        	LeagueChampion  matchChamp = match.getChampionSelectionForSummoner(player);
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
	        }
	        return matchesToWrite;
	}
	
	/**
	 * Initializes the dialog that prompts the user for their login credentials to the League of Legends server. 
	 */
	private void initializePasswordDialog()
	{
		loginDialog = new JDialog(this, "Login to update history");
		JButton loginButton = new JButton("Log On");
		loginButton.setActionCommand("logon");
		loginButton.addActionListener(this);
		JPanel loginDialogPanel = new JPanel();
		loginDialogPanel.setLayout(new BoxLayout(loginDialogPanel, BoxLayout.Y_AXIS));
		JLabel loginLabel = new JLabel("Username:");
		JLabel passwordLabel = new JLabel("Password:");
		usernameField = new JTextField();
		passwordField = new JPasswordField();
		passwordField.addActionListener(this);
		loginDialogPanel.add(loginLabel);
		loginDialogPanel.add(usernameField);
		loginDialogPanel.add(passwordLabel);
		loginDialogPanel.add(passwordField);
		loginDialogPanel.add(loginButton);
		loginDialog.add(loginDialogPanel);
	}
	
	protected class PasswordDialog extends JDialog implements ActionListener
	{

		protected JTextField usernameField;
		JPasswordField passwordField;
		public PasswordDialog(Frame owner, String title)
		{
			super(owner, title);
			
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("logon"))
			{
				//Get a league connection
				final LeagueConnection c = new LeagueConnection(LeagueServer.NORTH_AMERICA);
				try {
					c.getAccountQueue().addAccount(new LeagueAccount(LeagueServer.NORTH_AMERICA, "3.14.xx", usernameField.getText(),
							new String(passwordField.getPassword())));
				} catch (LeagueException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				//Connect 
				Map<LeagueAccount, LeagueException> exceptions = c.getAccountQueue().connectAll();
		        if(exceptions != null) {
		            for(LeagueAccount account : exceptions.keySet())
		                System.out.println(account + " error: " + exceptions.get(account));
		            return;
		        }
		        
		        //Get summoner info by summoner name, get recent matches and write them to the database. 
		        LeagueSummoner testSummoner;
		        String summonerName = "Hydrophobic";
				try {
					testSummoner = c.getSummonerService().getSummonerByName(summonerName);
					c.getPlayerStatsService().fillMatchHistory(testSummoner);
			        ArrayList<MatchHistoryEntry> recentMatches = (ArrayList<MatchHistoryEntry>) testSummoner.getMatchHistory();
			        HttpClient httpClient = new StdHttpClient.Builder().build();
		    		CouchDbInstance dbInstance = new StdCouchDbInstance(httpClient);
		    		String dbname = summonerName + "-stats";
		         	CouchDbConnector db = dbInstance.createConnector(dbname, true);
		         	MatchRepository mr = new MatchRepository(db, summonerName);
		         	
			        JFrame owner = (JFrame) getOwner();
			        if(owner instanceof StatController)
			        {
			        	ArrayList<Match> mostRecentMatches = ((StatController)owner).getMatchesToWrite(recentMatches, testSummoner);
			         	mr.addNewMatches(mostRecentMatches);
			        }
			        
				} catch (LeagueException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}	
		}	
	}
}
