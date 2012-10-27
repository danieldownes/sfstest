package sfsExtension;

//import utils.Commands;

import jgam.JGame;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
//import com.smartfoxserver.v2.entities.data.ISFSObject;
//import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class UserLeaveRoomHandler extends BaseServerEventHandler
{
	// private int userIndex;

	public void handleServerEvent(ISFSEvent arg0) throws SFSException
	{
		Room room = (Room) arg0.getParameter(SFSEventParam.ROOM);
		User user = (User) arg0.getParameter(SFSEventParam.USER);
		
		//int iPlayerId = 0;
		
		trace("**user " + user.getName() + " left the room");
		
		if (room.isGame())//room.containsUser(user) &&
		{
			JGame currGame = (JGame) ((SfsTestExtension) getParentExtension()).games.get(room.getId());
			
			// If the game hadn't started, then just close it now
			if( currGame.iState == 0)
				((SfsTestExtension) getParentExtension()).removeGame(currGame);


		}
	}

}
