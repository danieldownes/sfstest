package jgam;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


// The application Main-class. basically holds the room state, not the game logic

public class JGame implements ActionListener
{
	public Game				game;		// Game logic, note that this contains its own thread 
	
	public sfsExtension.SfsTestExtension extension;	// Back reference to parent
	
	
	public int 				roomId;
		
	
	// Room Settings
	public String			sName;
	public String			sPlayer1Name;
	public String			sPlayer2Name;
	public int				iP1UserId;			// SFS User Id
	public int				iP2UserId;
	public int	 			iPlayer1State;		// 0 == Not ready, 1 == ready/playing, 
	public int	 			iPlayer2State;		// < 0 == playing but disconnected (used as countdown)
	
	public int				iState;		// 0 = Waiting for players / Players deciding game settings
										// 1 = Playing game normally
										// 2 = Game Over
	public int				iMatch;
	public int 				iLength;

	public JGame(int roomId_, String sName_, sfsExtension.SfsTestExtension extention_)
	{
		this.roomId = roomId_;
		this.sName = sName_;
		this.extension = extention_;
		
		this.sPlayer1Name = "";
		this.sPlayer2Name = "";
		this.iP1UserId = -1;
		this.iP2UserId = -1;
		this.iPlayer1State = 0;
		this.iPlayer2State = 0;
		this.iState = 0;
		this.iLength = 3;
	}
	

	public int process()
	{
		int iEvent = 0;
		return iEvent;
	}
	
	
	public void actionPerformed(ActionEvent e)
	{
		handle(e.getActionCommand());
	}

	
	synchronized public void handle(String command)
	{
		
		System.out.println("handle: " + command);
		
		if (command.equals("newgame"))
		{

			LocalPlayer player1 = new LocalPlayer(sPlayer1Name, 0);
			LocalPlayer player2 = new LocalPlayer(sPlayer2Name, 1);
			try
			{
				game = new Game(player1, player2, this);
			} catch (Exception ex)
			{
				ex.printStackTrace();
			}

			if (game != null)
			{
				game.start();
				iState = 1;
			}else
				System.out.print("GAME WAS NULL!!!");
			


		} else if (command.equals("close"))
		{
			clearGame();
		
		} else
		{
			if (game != null)
			{
				//game.handle(command);
			}
		}

	}
	

	public void checkState()
	{
		//extension.trace("checkState() iState==" + iState);
		
		// Check if game is ready to start
		if( iState == 0 )
		{
			// Both players are ready then start
			//if( iPlayer1State == 1 ) //&& iPlayer2State == 1)
			//{
				handle("newgame");
			//}
		}
			
	}
	
	
	public Game getGame()
	{
		return game;
	}


	
	public void clearGame()
	{		
		if (game != null)
			game.abort();
		game = null;
		
	}


	public void playerResigned(int iPlayerId)
	{
		// Called from player disconnected timeout
		
		// TODO: issue game over event to other player
		// TODO: send game result over API
		
		extension.removeGame(this);
	}

	
}

