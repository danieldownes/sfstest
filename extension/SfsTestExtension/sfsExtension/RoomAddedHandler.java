package sfsExtension;

import jgam.JGame;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.SFSRoomRemoveMode;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

public class RoomAddedHandler extends BaseServerEventHandler
{
	public void handleServerEvent(ISFSEvent event) throws SFSException
	{
		SfsTestExtension gameExt = ((SfsTestExtension) getParentExtension());
		
		Room newRoom = (Room) event.getParameter(SFSEventParam.ROOM);
		int r = newRoom.getId();
		
		newRoom.setDynamic(true);
		newRoom.setAutoRemoveMode(SFSRoomRemoveMode.NEVER_REMOVE);
		
		
		gameExt.trace("making new game..");
		
		// New Instance
		JGame newJG = new JGame(r, "Room #" + r, gameExt);
		
		
		while( newJG == null)
		{
			trace("newJG is null");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		gameExt.games.put(r, newJG);
		trace("NEW GAME ROOM ADDED! ID==" + r);
		
	}
}
