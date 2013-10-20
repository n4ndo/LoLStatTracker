package com.rajohnson;
public class KDA
{
		private int m_kills;
		private int m_deaths;
		private int m_assists;
		public KDA()
		{
			this(0,0,0);
		}
		
		public KDA(int kills, int deaths, int assists)
		{
			m_kills = kills;
			m_deaths = deaths;
			m_assists = assists;
		}
		
		
		public double calculateKDA()
		{
			if(m_deaths > 0)
			{
				return (m_kills + m_assists)/((double)m_deaths);
			}
			else
			{
				return (double)(m_kills + m_assists);
			}
			
		}
		
		public void addGame(int kills, int deaths, int assists)
		{
			m_kills += kills;
			m_deaths += deaths;
			m_assists += assists;
		}
		
		public void addGame(KDA newGameKDA)
		{
			m_kills += newGameKDA.m_kills;
			m_deaths += newGameKDA.m_deaths;
			m_assists += newGameKDA.m_assists;
		}
		
	}
