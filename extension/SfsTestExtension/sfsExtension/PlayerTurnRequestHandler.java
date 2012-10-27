package sfsExtension;

import java.util.List;

import jgam.JGame;
import utils.*;

import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class PlayerTurnRequestHandler extends BaseClientRequestHandler
{
	// When a player makes a move (move a checker)...

	// public static String cardNum;
	public static String cmd = Commands.PLAYER_TURN;
	public static int counter = 0;

	@Override
	public void handleClientRequest(User sender, ISFSObject arg)
	{
		int iFrom = 0;
		int iTo = 0;
		Boolean bOk = true; //false; 
		
		// Retrieve user's room
		Room room = null;
		List<Room> rooms = sender.getJoinedRooms();
		if (rooms.size() > 0)
			room = rooms.get(0);

		int iPlayerId = arg.getInt("iPlayerId");


		JGame currGame = (JGame) ((SfsTestExtension) getParentExtension()).games.get(room.getId());
		
		
		// Is this player playing
		if(currGame.game.getCurrentPlayer() != null)
		{
			//trace("PLAYER_TURN : player exists");
			if (currGame.game.getCurrentPlayer().bPlaying)
			{
				trace("PLAYER_TURN start : p#=" + currGame.game.getCurrentPlayer().iPlayerId + " " + iFrom + "/" + iTo + " real=" + arg.getBool("bRealMove"));
				
				// Is it this player's turn?
				if( currGame.game.getCurrentPlayer().iPlayerId == iPlayerId)
				{
					
					iFrom = arg.getInt("iFrom");
					iTo = arg.getInt("iTo");
					
					// TODO: Validate this move / undo
					bOk = true;
					
					// Force wait.
					/*
					int iPMove = currGame.game.iProcesingMove.addAndGet(1);
					trace("P#" + currGame.game.getCurrentPlayer().iPlayerId + " iProcesingMove=" + currGame.game.iProcesingMove.get());
					while( iPMove > 1)
					{
						trace("P#" + currGame.game.getCurrentPlayer().iPlayerId + " iProcesingMove=" + currGame.game.iProcesingMove.get());
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						iPMove = currGame.game.iProcesingMove.get();
					}
					*/

					// Okay to do action in game logic
					if( bOk)
					{
						trace("PLAYER_TURN Send : iFrom = " + iFrom + " iTo = " + iTo);
						((SfsTestExtension) getParentExtension()).playerTurn(currGame, iFrom, iTo);
						trace("PLAYER_TURN finished");
					}
					currGame.game.iProcesingMove.addAndGet(-1);
				}
			}
		}


	}
}
