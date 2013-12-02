package com.rajohnson;

import org.ektorp.*;
import org.ektorp.impl.*;
import org.ektorp.http.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
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
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import com.achimala.leaguelib.connection.LeagueAccount;
import com.achimala.leaguelib.connection.LeagueConnection;
import com.achimala.leaguelib.connection.LeagueServer;
import com.achimala.leaguelib.errors.LeagueException;
import com.achimala.leaguelib.models.LeagueChampion;
import com.achimala.leaguelib.models.LeagueSummoner;
import com.achimala.leaguelib.models.LeagueMatchmakingQueue;
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
	private JTabbedPane mainWindow;
	private static final int MatchmakingQueueLength = LeagueMatchmakingQueue.values().length;
	
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
		
		mainWindow = new JTabbedPane(JTabbedPane.LEFT);
		
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
		matchesList.setCellRenderer(new MatchCellRenderer<Match>(MatchCellRenderer.CellSize.MEDIUM));
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
		else if(isInteger(e.getActionCommand()))
		{
			mainWindow.removeTabAt(Integer.parseInt(e.getActionCommand()));
		}
		else 
		{
			if(mr.isRepoConnected())
			{
				mr.printStatsByChampionPlayed(e.getActionCommand());
				JPanel newChampPanel = createCloseableChampPerfTab(e.getActionCommand());
				if(mainWindow.indexOfTab(e.getActionCommand()) == -1)
				{
					//Construct the champion performance panel and give it a button to close the tab. 
					mainWindow.addTab(e.getActionCommand(), newChampPanel);
					JPanel champTabPanel = new JPanel(new GridBagLayout());
					GridBagConstraints c = new GridBagConstraints();
					JLabel champLabel = new JLabel(e.getActionCommand());
					JButton closeTabButton = new JButton("x");
					closeTabButton.setBorder(null);
					closeTabButton.setPreferredSize(new Dimension(15,15));
					int indexOfTab = mainWindow.indexOfTab(e.getActionCommand());
					closeTabButton.setActionCommand(Integer.toString(indexOfTab));
					closeTabButton.addActionListener(this);
					closeTabButton.setOpaque(false);
					c.anchor = GridBagConstraints.WEST;
					c.gridx = 0;
					c.gridy = 0;
					//c.weightx = 1.0;
					c.fill = GridBagConstraints.HORIZONTAL;
					champLabel.setOpaque(false);
					champTabPanel.add(champLabel,c);
					
					c.anchor = GridBagConstraints.EAST;
					c.gridx = 1;
					c.gridy = 0;
					c.fill = GridBagConstraints.HORIZONTAL;
					champTabPanel.add(closeTabButton, c);
					champTabPanel.setOpaque(false);
					mainWindow.setTabComponentAt(indexOfTab,champTabPanel);
				}
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
	 * @return A JPanel full of buttons that have a champion's icon on them. The buttons have an action listener with the name of their individual champion. 
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
	
	private JPanel createCloseableChampPerfTab(String championName)
	{
		JPanel champPerformancePanel = new JPanel(){
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
	
		Font serifFontBold = new Font(Font.SERIF, Font.BOLD, 14);
		Font serifFont = new Font(Font.SERIF, Font.PLAIN, 14);
		champPerformancePanel.setLayout(new GridBagLayout());
		ArrayList<Match> matchesForChamp = (ArrayList<Match>)mr.getMatchesByChampionPlayed(championName);
		
		//Sort this champ's matches by game
		Collections.sort(matchesForChamp, new Comparator<Match>(){
			@Override
		    public int compare(Match o1, Match o2) {
				//If the date of o1 is 0, then it has no date and should be sorted after
		        if(o1.getDate().compareTo(new Date(0)) == 0)
		        {
		        	return -1;
		        }
		        //Date o1 has a date. If o2 has no date then it should be placed after o1
		        else if(o2.getDate().compareTo(new Date(0)) == 0)
		        {
		        	return 1;
		        }
		        //Else use the compareTo method already defined by date to determine the correct ordering.
		        else
		        {
		        	return o1.getDate().compareTo(o2.getDate());
		        }
		    }
		});
		//The dates are sorted in ascending order. Reverse them so they are in descending order. 
		Collections.reverse(matchesForChamp);
		
		//Create the champ icon
		ImageIcon champIcon = new ImageIcon(MatchCellRenderer.getChampIconByName(championName));
		GridBagConstraints c = new GridBagConstraints();
		c.gridx=0;
		c.gridy=0;
		JLabel champIconLabel = new JLabel(champIcon);
		champPerformancePanel.add(champIconLabel, c);
		
		JLabel championWinPercentageLabel = new JLabel("Win Percentage: ");
		championWinPercentageLabel.setFont(serifFontBold);
		c.gridy=1;
		champPerformancePanel.add(championWinPercentageLabel, c);
		
		DecimalFormat dformat = new DecimalFormat("##0.00");
		JLabel championWinRate = new JLabel(dformat.format(winRateAsPercent(matchesForChamp)) + "%");
		championWinRate.setFont(serifFont);
		c.gridx=1;
		champPerformancePanel.add(championWinRate, c);
		
		JPanel champStatPanel = new JPanel();
		champStatPanel.setLayout(new GridBagLayout());
		champStatPanel.setOpaque(false);
		
		//Create the label "Kills: #kills"
		JPanel killPanel = new JPanel();
		killPanel.setOpaque(false);
		JLabel killLabel = new JLabel("Kills: ");
		killLabel.setFont(serifFontBold);
		JLabel killCount = new JLabel(dformat.format(calculateKillsPerMatch(matchesForChamp)));
		killCount.setFont(serifFont);
		killPanel.add(killLabel);
		killPanel.add(killCount);
		c.gridx=0;
		c.gridy=0;
		champStatPanel.add(killPanel, c);
		
		//Create the label "Assists: #assists"
		JPanel assistPanel = new JPanel();
		assistPanel.setOpaque(false);
		JLabel assistLabel = new JLabel("Assists: ");
		assistLabel.setFont(serifFontBold);
		JLabel assistCount = new JLabel(dformat.format(calculateAssistsPerMatch(matchesForChamp)));
		assistCount.setFont(serifFont);
		assistPanel.add(assistLabel);
		assistPanel.add(assistCount);
		c.gridy=2;
		champStatPanel.add(assistPanel, c);
		
		//Create the label "Deaths: #deaths"
		JPanel deathPanel = new JPanel();
		deathPanel.setOpaque(false);
		JLabel deathLabel = new JLabel("Deaths: ");
		deathLabel.setFont(serifFontBold);
		JLabel deathCount = new JLabel(dformat.format(calculateDeathsPerMatch(matchesForChamp)));
		deathCount.setFont(serifFont);
		deathPanel.add(deathLabel);
		deathPanel.add(deathCount);
		c.gridy=1;
		champStatPanel.add(deathPanel, c);
		
		//Create the label "Minions Killed: #minions killed"
		JPanel minionKillPanel = new JPanel();
		minionKillPanel.setOpaque(false);
		JLabel minionKillLabel = new JLabel("Minions Killed: ");
		minionKillLabel.setFont(serifFontBold);
		JLabel minionKillCount = new JLabel(dformat.format(calculateMinionKillsPerMatch(matchesForChamp)));
		minionKillCount.setFont(serifFont);
		minionKillPanel.add(minionKillLabel);
		minionKillPanel.add(minionKillCount);
		c.gridx=1;
		c.gridy=0;
		champStatPanel.add(minionKillPanel, c);
		
		//Add the champion average stats to the overall JPanel
		c.gridx=2;
		c.gridy=2;
		c.fill = GridBagConstraints.HORIZONTAL;
		champPerformancePanel.add(champStatPanel, c);
		
		//Create a scroll pane of all the matches played on this champion that are stored in the database.
		DefaultListModel<Match> champMatchListModel = new DefaultListModel<Match>();
		for(Match m : matchesForChamp)
		{
			champMatchListModel.addElement(m);
		}
		System.out.println("Create model champ match list model size: " + champMatchListModel.getSize());
		JList<Match> champMatchList = new JList<Match>(champMatchListModel);
		champMatchList.setCellRenderer(new MatchCellRenderer<Match>(MatchCellRenderer.CellSize.SMALL));
		JScrollPane champMatchScrollPane = new JScrollPane(champMatchList);
		champMatchScrollPane.setOpaque(false);
		champMatchScrollPane.getViewport().setOpaque(false);
		c.gridy=3;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 0.75;
		c.weighty = 0.75;
		champPerformancePanel.add(champMatchScrollPane, c);
		//TODO: HAVE MATCHCHECKPANEL TAKE THE DEFAULTLISTMODEL SO I CAN WORK ON IT DIRECTLY.  
		MatchCheckPanel checkboxPanel = new MatchCheckPanel(championName, matchesForChamp, champMatchScrollPane, this);
		c.gridx=2;
		c.gridy=0;
		champPerformancePanel.add(checkboxPanel,c);
		return champPerformancePanel;
	}
	
	public void updateStatPanelWithMatches(String championName, ArrayList<Match> matchesPlayedOnChamp)
	{
		
	}
	
	/**
	 * Takes an {@code ArrayList} of matches and returns the percentage of games that are victorious.
	 * @param matches The list of matches to check.
	 * @return A double value which is the percentage of victories in {@code matches}
	 */
	private double winRateAsPercent(ArrayList<Match> matches)
	{
		int gamesWon = 0;
		int gamesTotal = 0;
		for(Match m : matches)
		{
			if(m.getWin() == 1)
			{
				gamesWon++;
			}
			gamesTotal++;
		}
		
		if(gamesTotal < 1)
		{
			return 0;
		}
		else
		{
			return (gamesWon/(double)gamesTotal)*100;
		}
	}
	
	/**
	 * Takes an {@code ArrayList} of matches and returns the average number of kills per game.
	 * @param matches The list of matches to check.
	 * @return A double value which is the average amount of kills per  {@link Match}
	 */
	private double calculateKillsPerMatch(ArrayList<Match> matches)
	{
		int killsTotal = 0;
		int gamesTotal = 0;
		for(Match m : matches)
		{
			killsTotal += m.getNumKills();
			gamesTotal++;
		}
		if(gamesTotal < 1)
		{
			return 0;
		}
		else
		{
			return killsTotal/(double)gamesTotal;
		}
	}
	/**
	 * Takes an {@code ArrayList} of matches and returns the average number of assists per game.
	 * @param matches The list of matches to check.
	 * @return A double value which is the average amount of assists per  {@link Match}
	 */
	private double calculateAssistsPerMatch(ArrayList<Match> matches)
	{
		int assistsTotal = 0;
		int gamesTotal = 0;
		for(Match m : matches)
		{
			assistsTotal += m.getNumAssists();
			gamesTotal++;
		}
		if(gamesTotal < 1)
		{
			return 0;
		}
		else
		{
			return assistsTotal/(double)gamesTotal;
		}
	}
	/**
	 * Takes an {@code ArrayList} of matches and returns the average number of deaths per game.
	 * @param matches The list of matches to check.
	 * @return A double value which is the average amount of deaths per  {@link Match}
	 */
	private double calculateDeathsPerMatch(ArrayList<Match> matches)
	{
		int deathsTotal = 0;
		int gamesTotal = 0;
		for(Match m : matches)
		{
			deathsTotal += m.getNumDeaths();
			gamesTotal++;
		}
		if(gamesTotal < 1)
		{
			return 0;
		}
		else
		{
			return deathsTotal/(double)gamesTotal;
		}
	}
	/**
	 * Takes an {@code ArrayList} of matches and returns the average number of minion kills per game.
	 * @param matches The list of matches to check.
	 * @return A double value which is the average amount of minion kills per  {@link Match}
	 */
	private double calculateMinionKillsPerMatch(ArrayList<Match> matches)
	{
		int minionKillsTotal = 0;
		int gamesTotal = 0;
		for(Match m : matches)
		{
			minionKillsTotal += m.getMinionsKilled();
			gamesTotal++;
		}
		if(gamesTotal < 1)
		{
			return 0;
		}
		else
		{
			return minionKillsTotal/(double)gamesTotal;
		}
	}
	
	private boolean isInteger(String s)
	{
		try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
	
	private class MatchCheckPanel extends JPanel implements ItemListener
	{

		private String m_ChampName;
		private JCheckBox bot5v5Box, bot3v3Box, normal5v5Box, normal3v3Box;
		private JCheckBox aramBox, rankedSoloBox, ranked5v5Box, ranked3v3Box;
		private JCheckBox oneForAllBox, dominionBox;
		HashSet<String> matchesToShow;
		private String bot5v5, bot3v3, normal5v5, normal3v3;
		private String aram,rankedSolo,ranked5v5,ranked3v3;
		private String oneForAll, dominion;
		private ArrayList<Match> matchesForChamp;
		private JScrollPane matchScrollPane;
		private StatController parent;
		
		
		public MatchCheckPanel(LayoutManager layout, String champName, List<Match> champGames, JScrollPane champScrollPane)
		{
			super(layout);
			m_ChampName = champName;
			matchesToShow = new HashSet<String>();
			setOpaque(false);
			matchesForChamp = (ArrayList<Match>)champGames;
			matchScrollPane = champScrollPane;
		}
		
		public MatchCheckPanel(String champName, List<Match> champGames, JScrollPane champScrollPane, StatController sc)
		{
			super(new GridBagLayout());
			setOpaque(false);
			parent = sc;
			bot5v5 = LeagueMatchmakingQueue.BOT.toString();
			bot3v3 = LeagueMatchmakingQueue.BOT_3x3.toString();
			normal5v5 = LeagueMatchmakingQueue.NORMAL.toString();
			normal3v3 = LeagueMatchmakingQueue.NORMAL_3x3.toString();
			aram = LeagueMatchmakingQueue.ARAM_UNRANKED_5x5.toString();
			rankedSolo = LeagueMatchmakingQueue.RANKED_SOLO_5x5.toString();
			ranked5v5 = LeagueMatchmakingQueue.RANKED_TEAM_5x5.toString();
			ranked3v3 = LeagueMatchmakingQueue.RANKED_TEAM_3x3.toString();
			oneForAll = LeagueMatchmakingQueue.ONEFORALL_5x5.toString();
			dominion = LeagueMatchmakingQueue.ODIN_UNRANKED.toString();
			matchesForChamp = (ArrayList<Match>)champGames;
			matchScrollPane = champScrollPane;
			Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			setBorder(etchedBorder);
			
			m_ChampName = champName;
			matchesToShow = new HashSet<String>();
			matchesToShow.add(bot5v5);
			matchesToShow.add(bot3v3);
			matchesToShow.add(normal5v5);
			matchesToShow.add(normal3v3);
			matchesToShow.add(aram);
			matchesToShow.add(rankedSolo);
			matchesToShow.add(ranked5v5);
			matchesToShow.add(ranked3v3);
			matchesToShow.add(oneForAll);
			matchesToShow.add(dominion);
			
			Font serifFontBold = new Font(Font.SERIF, Font.BOLD, 12);
			Font serifFont = new Font(Font.SERIF, Font.PLAIN, 12);
			
			bot5v5Box = new JCheckBox();
			createBoxAndLabel(0, 0, "Summoner's Rift (Bot)", bot5v5Box);

			bot3v3Box = new JCheckBox();
			createBoxAndLabel(2,0,"Twisted Treeline (Bot)", bot3v3Box);
			
			normal5v5Box = new JCheckBox();
			createBoxAndLabel(0,1,"Summoner's Rift (Normal)",normal5v5Box);
			
			normal3v3Box = new JCheckBox();
			createBoxAndLabel(2,1,"Twisted Treeline (Normal)",normal3v3Box);
			
			aramBox = new JCheckBox();
			createBoxAndLabel(0,2,"Howling Abyss", aramBox);
			
			rankedSoloBox = new JCheckBox();
			createBoxAndLabel(2,2,"Summoner's Rift (Ranked Solo Queue)",rankedSoloBox);
			
			ranked5v5Box = new JCheckBox();
			createBoxAndLabel(0,3,"Summoner's Rift (Ranked Team Queue)",ranked5v5Box);
			
			ranked3v3Box = new JCheckBox();
			createBoxAndLabel(2,3,"Twisted Treeline (Ranked Team Queue)",ranked3v3Box);
			
			oneForAllBox = new JCheckBox();
			createBoxAndLabel(0,4,"One For All (Summoner's Rift)",oneForAllBox);
			
			dominionBox = new JCheckBox();
			createBoxAndLabel(2,4,"Dominion",dominionBox);
			
		}
		
		private void createBoxAndLabel(int startX, int startY, String matchmakingQueueName, JCheckBox checkBoxToUse)
		{
			GridBagConstraints c = new GridBagConstraints();
			Font serifFontBold = new Font(Font.SERIF, Font.BOLD, 12);
			
			JLabel queueLabel = new JLabel(matchmakingQueueName);
			queueLabel.setHorizontalAlignment(SwingConstants.LEFT);
			queueLabel.setFont(serifFontBold);
			checkBoxToUse.setSelected(true);
			checkBoxToUse.setOpaque(false);
			checkBoxToUse.addItemListener(this);
			c.gridx=startX;
			c.gridy=startY;
			c.anchor=GridBagConstraints.WEST;
			add(queueLabel,c);
			c.gridx=startX+1;
			add(checkBoxToUse,c);
		}
		
		@Override
		public void itemStateChanged(ItemEvent arg0) {
			Object boxSelected = arg0.getItem();
			int startSize = matchesToShow.size();
			if(boxSelected == bot5v5Box)
			{
				addOrRemoveMatch(arg0,matchesToShow,bot5v5);
			}
			else if(boxSelected == bot3v3Box)
			{
				addOrRemoveMatch(arg0,matchesToShow,bot3v3);
			}
			else if(boxSelected == normal5v5Box)
			{
				addOrRemoveMatch(arg0,matchesToShow,normal5v5);
			}
			else if(boxSelected == normal3v3Box)
			{
				addOrRemoveMatch(arg0,matchesToShow,normal5v5);
			}
			else if(boxSelected == aramBox)
			{
				addOrRemoveMatch(arg0,matchesToShow,aram);
			}
			else if(boxSelected == rankedSoloBox)
			{
				addOrRemoveMatch(arg0,matchesToShow,rankedSolo);
			}
			else if(boxSelected == ranked5v5Box)
			{
				addOrRemoveMatch(arg0,matchesToShow,ranked5v5);
			}
			else if(boxSelected == ranked3v3Box)
			{
				addOrRemoveMatch(arg0,matchesToShow,ranked3v3);
			}
			else if(boxSelected == oneForAllBox)
			{
				addOrRemoveMatch(arg0,matchesToShow,oneForAll);
			}
			else if(boxSelected == dominionBox)
			{
				addOrRemoveMatch(arg0,matchesToShow,dominion);
			}
			
			int tabIndex = mainWindow.indexOfTab(m_ChampName);
			JPanel champPerfPanel = (JPanel)mainWindow.getComponent(tabIndex);
			int componentCount = champPerfPanel.getComponentCount();
			if(matchesToShow.size() <= startSize)
			{
				removeMatchesFromView(matchScrollPane,matchesToShow);
			}
			else
			{
				addMatchesToView(matchScrollPane, matchesToShow);
			}
		}
		private void addOrRemoveMatch(ItemEvent e, HashSet<String> matchSet, String matchmakingQueue)
		{
			if(e.getStateChange() == ItemEvent.DESELECTED)
			{
				matchSet.remove(matchmakingQueue);
			}
			else
			{
				matchSet.add(matchmakingQueue);
			}
		}
		@SuppressWarnings("unchecked")
		private void removeMatchesFromView(JScrollPane matchPane, HashSet<String> matchesToShow)
		{
			// Get the list model.
			DefaultListModel<Match> matchList = (DefaultListModel<Match>) ((JList<Match>)(matchPane.getViewport().getComponent(0))).getModel();
			for(int i = 0; i < matchList.getSize(); )
			{
				Match currentMatch = matchList.get(i);
				if(!matchesToShow.contains(currentMatch.getMatchmakingQueue()))
				{
					matchList.removeElementAt(i);
				}
				else
				{
					i++;
				}
			}
		}
		
		@SuppressWarnings("unchecked")
		private void addMatchesToView(JScrollPane matchPane, HashSet<String> matchesToShow)
		{
			DefaultListModel<Match> matchList = (DefaultListModel<Match>) ((JList<Match>)(matchPane.getViewport().getComponent(0))).getModel();
			matchList.removeAllElements();
			for(Match m : matchesForChamp)
			{
				if(matchesToShow.contains(m.getMatchmakingQueue()))
				{
					matchList.addElement(m);
				}
			}
		}
		
		
		
	}
}
