package sfsExtension;

import java.util.List;

import jgam.JGame;
//import utils.Commands;


import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
//import com.smartfoxserver.v2.entities.data.ISFSObject;
//import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;


public class UserDisconnectHandler extends BaseServerEventHandler
{
	//private int userIndex;

	public void handleServerEvent(ISFSEvent event) throws SFSException
	{
		Room room = null;		
		
		@SuppressWarnings("unchecked")
		List<Room> rooms = (List<Room>)event.getParameter(SFSEventParam.JOINED_ROOMS);
		
		User user = (User) event.getParameter(SFSEventParam.USER);
		
		if (rooms.size() > 0)
		{
			// User can be only in 1 room at a time
			room = rooms.get(0);  		
			
			if( room.isGame())
			{
				
				if( ((SfsTestExtension) getParentExtension()).getGame(room.getId()) != null)
				{
					jgam.JGame currGame = (JGame) ((SfsTestExtension) getParentExtension()).getGame(room.getId());
					
					trace("disconnected id == " + user.getId());
					trace("white id == " + currGame.iP1UserId);
					trace("blue id == " + currGame.iP2UserId);
					
					// If Game started then start count down timer for this player
					if( currGame.iState != 0)
					{
						if( currGame.iP1UserId == user.getId() )
						{
							trace("White player disconnected");
							currGame.game.getPlayerWhite().disconnected();
						}
						
						else if( currGame.iP2UserId == user.getId())
						{					
							trace("Blue player disconnected");
							currGame.game.getPlayerBlue().disconnected();
						}
					}else
					{
						// The game hadn't started (eg players not initialised), just close the game
						((SfsTestExtension) getParentExtension()).removeGame(currGame);
					}
					
				
				}
			}
			
		}
	}
}
