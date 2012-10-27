package jgam;

import java.util.Timer;
import java.util.TimerTask;


public class LocalPlayer extends Player
{

	/** used for commication betw. threads */
	private int			lastMessage;

	public LocalPlayer(String name, int iId)
	{
		super(name, iId);
	}

	synchronized public int move() throws InterruptedException
	{
		while (true)
		{
			allowMoves.set(true);
			System.out.println("\n waiting for move()...\n");
			wait();
			allowMoves.set(false);
			System.out.println("\n process move()...\n");
			int m = (int) lastMessage;
			return m;
		}
	}

	synchronized public void handle(int msg)
	{
		System.out.println("player handle: P#" + this.iPlayerId + " msg:" + msg);
		lastMessage = msg;
		notify();
	}

	synchronized public int nextStep(boolean rollOnly) throws InterruptedException
	{

		int ret = -1;

		while (ret == -1)
		{
			wait();
		}

		return ret;
	}


	public void disconnected()
	{
		// Start their timer
		
		traceTimer.iTimerCountdown = utils.Constants.DISCONNECT_TIMEOUT_SECS;
		timer.schedule(traceTimer, 0, 1000);
		bPlaying = false;
	}

}


class MyTimerTask extends TimerTask
{
	Game thisGame;
	public int iTimerCountdown;
	Timer thisTimer;
	int iPlayerId;
	 
	
	public MyTimerTask(Game thisGame_, Timer thisTimer_, int iPlayerId_) //, int iAction_)
	{
		thisGame = thisGame_;
		thisTimer = thisTimer_;
		iPlayerId = iPlayerId_;
	}
	
   public void run()
   {
	   if( iTimerCountdown <= 0)
	   {
		   System.out.println(" * * TIMEOUT Player==" + iPlayerId); // iAction == " + iAction);
		   // Issue Resign for this player.
		   thisTimer.cancel();

		   thisGame.getJGam().playerResigned(iPlayerId);

	   }else
	   {
		   // Send out update on the countdown
		   System.out.println("\nP#" + iPlayerId + "  Countdown = " + iTimerCountdown + "\n");
		   
		   thisGame.getJGam().iPlayer1State = -iTimerCountdown;
		   thisGame.getJGam().extension.sendRoomVariables(thisGame.getJGam(), iPlayerId);
	   }
	   
	   iTimerCountdown--;      
   }
}
