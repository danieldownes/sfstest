package jgam;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Player
{
	public int iPlayerId;		// 0 - white, 1 = black
	public int iUserID = -1;	// SFS User ID
	public boolean bPlaying;
	
	
	public static final int		NOT_CONNECTED 	= 0;
	public static final int		MOVE			= 2;
	public int iState = NOT_CONNECTED;		// Turn State
	
	
	private String			name;
	private Game			game;
	
	
	// Independent timer for each player, used for timeout on move, or disconnection
	public Timer timer = new Timer();
	public MyTimerTask traceTimer;
	
	// if this is true moves with the mouse may be done 
	public AtomicBoolean			allowMoves = new AtomicBoolean(false);
	

	// number of chips placed during initboard

	public Player(String name, int iId)
	{
		this.name = name;
		this.iPlayerId = iId;

		bPlaying = true;		
		System.out.print("Player Ready:" + iId + "\n");
	}

	public Player()
	{
	}

	public void setGame(Game game) throws IOException
	{
		this.game = game;
		
		traceTimer = new MyTimerTask(game, timer, iPlayerId);
	}



	public boolean isValidMove(int from, int length)
	{
		return true;
	}

	public void performMove(int m)
	{
		// Simulate processing time
		for(int n = m; n < 10000; n++)
		{
			if(  (n %1000) == 500 )
				System.out.print("Processing move " + m + "\n");
		}
	}

	public String getName()
	{
		return name;
	}

	public void setName(String n)
	{
		name = n;
	}

	public String toString()
	{
		return getName();
	}

	public abstract void handle(int msg);


	public abstract int move() throws Exception;


	public int nextStep()
	{
		return 1;
	}


	public void dispose()
	{
	}


	abstract public void disconnected();
	
	
	public Game getGame()
	{
		return game;
	}


}
