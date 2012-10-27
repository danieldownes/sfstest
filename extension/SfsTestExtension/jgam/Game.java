package jgam;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * This is the game its-self - the logic etc. The players are contained in here
 * as well.
 * 
 * After construction a game is started by the start()-method. It then runs in
 * its own thread.
 * 
 * After finding out the beginning party, the players take turns. (method
 * play()).
 * 
 * A game can be stopped via the abort-method.
 * 
 */
public class Game implements Runnable
{

	private Player			player1, player2;
	private Player			currentPlayer;
	private JGame			jgam;
		

	// the game runs in its own thread
	public Thread			gameThread;


	private Player			winner			= null;
	private Player			nullPlayer		= null;

	int						winType			= 1;

	
	public AtomicInteger iProcesingMove = new AtomicInteger(0);

	
	public Game(Player p1, Player p2, JGame jgam) throws IOException
	{		
		player1 = p1;
		player2 = p2;
		player1.setGame(this);
		player2.setGame(this);
		this.jgam = jgam;
		jgam.extension.trace("game ready!!");
		currentPlayer = player1;
	}

	public Player getPlayerWhite()
	{
		return player1;
	}

	public Player getPlayerBlue()
	{
		return player2;
	}

	public Player getCurrentPlayer()
	{
		return currentPlayer;
	}

	public Player getOtherPlayer()
	{
		return (currentPlayer == player1) ? player2 : player1;
	}


	public Player getOtherPlayer(Player p)
	{
		return p == player1 ? player2 : player1;
	}


	/**
	 * announce an action to the game. This called from the awt thread. The
	 * message is passed to the current player.
	 * 
	 * @param msg
	 *            the object describing the message.
	 */
	synchronized public void handle(int msg)
	{
		jgam.extension.trace("Game.handle msg=" + msg);
		getCurrentPlayer().handle(msg);
	}

	/**
	 * start a thread and save in gameThread.
	 */
	public void start()
	{
		assert gameThread == null;
		gameThread = new Thread(this, "Game-Thread");
		gameThread.start();
	}

	private void play() throws Exception
	{

		while(true)
		{
			int move = currentPlayer.move();
			
			currentPlayer.performMove(move);
			
			jgam.extension.trace("performMove completed");
			jgam.extension.playerTurn(jgam, move, move);
		}
	}
	
	

	public void run()
	{
		try
		{
			// TODO: Consider extended match (for draws)
			jgam.extension.trace("jgam.iLength = " + jgam.iLength);
			
			jgam.iMatch = 0;
			int iP1Wins = 0;
			int iP2Wins = 0;
			boolean bLastMatch = false; 
			while(jgam.iMatch < jgam.iLength )
			{

				jgam.extension.trace("winner == nullPlayer" + (winner == nullPlayer) );
				while(winner == nullPlayer)
				{
					play();
				}
				
				jgam.extension.trace("winner=" + winner.iPlayerId);
	
				
				
				
				// TODO: Save match results
				
				if( winner.iPlayerId == 0)
					iP1Wins++;
				else
					iP2Wins++;
				winner = nullPlayer;
				
				// TODO: Game is draw so far?
				jgam.iMatch++;
				if( jgam.iMatch == jgam.iLength)
				{
					// Draw?
					if( iP1Wins == iP2Wins)
						jgam.iLength++;
					else
						bLastMatch = true; 
				}
				
				if( !bLastMatch)
				{
					jgam.iState = 1;
				}
			}
			
			// Who own game, compute game winner from match results
			if( iP1Wins < iP2Wins)
				winner = player1;
			else
				winner = player2;
			
			//JOptionPane.showMessageDialog(jgam.getFrame(), M, msg.getString("gameover"), JOptionPane.INFORMATION_MESSAGE, winner.getChipIcon());
			jgam.extension.trace(" *** GAME OVER *** " + winType);
			
			jgam.clearGame();
			
			
			// TODO: SFS GameOver call
			

		} catch (InterruptedIOException ex)
		{
			// this is ok.
			jgam.extension.trace("Thread has been interrupted to end this thread:");
			ex.printStackTrace();
		} catch (InterruptedException ex)
		{
			// this is ok.
			jgam.extension.trace("Thread has been interrupted to end this thread:");
			ex.printStackTrace();
		} catch (Exception ex)
		{
			ex.printStackTrace();
			//JOptionPane.showMessageDialog(getJGam().getFrame(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			jgam.extension.trace(" * ERROR : " + ex.getMessage());
			jgam.clearGame();
		}
	}


	/**
	 * to abort a game the connection must be reset and the running tasked must
	 * interrupted (if waiting for input)
	 */
	synchronized public void abort()
	{

		gameThread.interrupt();
		try
		{
			gameThread.join();
		} catch (InterruptedException ex)
		{
			//ex.printStackTrace();
			jgam.extension.trace("thread aboarted, probably game over..");
		}
		if (player1 != null)
		{
			player1.dispose();
		}
		if (player2 != null)
		{
			player2.dispose();
		}
	}


	public JGame getJGam()
	{
		return jgam;
	}


}