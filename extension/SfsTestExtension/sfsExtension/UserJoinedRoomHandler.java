package sfsExtension;

import java.util.ArrayList;
import java.util.List;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;

import jgam.JGame;

public class UserJoinedRoomHandler extends BaseServerEventHandler
{
	//@SuppressWarnings("unchecked")
    @Override
	public void handleServerEvent(ISFSEvent arg0) throws SFSException
	{
		Room room = (Room) arg0.getParameter(SFSEventParam.ROOM);
	
		if (room.isGame())
		{
			trace("room.isGame()");
			
			User user = (User) arg0.getParameter(SFSEventParam.USER);
			trace("user joined room: " + user.getName());


			// Get the Room instance
			JGame currGame = (JGame) ((SfsTestExtension) getParentExtension()).games.get(room.getId());
			
			
			// Sleep while game + players initialise 
			while( currGame == null)
			{
				currGame = (JGame) ((SfsTestExtension) getParentExtension()).games.get(room.getId());
				trace("currGame is null");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			trace("game created : Players p1=" + currGame.sPlayer1Name + " p2=" + currGame.sPlayer2Name);
			
			// Set name
			if( currGame.sPlayer1Name == "")
			{
				trace("add p1 = " + user.getName() + " to room=" + room.getId());
				// TODO: Set room variable here
				List<RoomVariable> listOfVars = new ArrayList<RoomVariable>();
				listOfVars.add( new SFSRoomVariable("player1Name", user.getName()) );
				((SfsTestExtension) getParentExtension()).getApi().setRoomVariables(user, room, listOfVars);


				currGame.sPlayer1Name = user.getName();
				currGame.iP1UserId = user.getId();
			}else if( currGame.sPlayer2Name == "")
			{
				trace("add p2 = " + user.getName() + " to room=" + room.getId());

				currGame.sPlayer2Name = user.getName();
				currGame.iP2UserId = user.getId();
			}

			
			// Ensure lobby is updating to anyone listening
			currGame.extension.sendRoomListToAll();
			

			currGame.checkState();
		}
		
	}

}
