package sfsExtension;

import java.util.List;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
//import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.entities.variables.RoomVariable;

import jgam.JGame;
import utils.Constants;

public class RoomVarsUpdateHandler extends BaseServerEventHandler
{
	public void handleServerEvent(ISFSEvent event) throws SFSException
	{		
		Room room = (Room)event.getParameter(SFSEventParam.ROOM);
		
		@SuppressWarnings("unchecked")
		List<RoomVariable> vars = (List<RoomVariable>)event.getParameter(SFSEventParam.VARIABLES);
		
		JGame currGame = (JGame) ((SfsTestExtension) getParentExtension()).games.get(room.getId());
		
		//trace("ROOM VARS UPDATED! in room=" + room.getId());
		
		
		for( int v = 0; v < vars.size(); v++ )
		{
			trace("UPDATED VAR=" + vars.get(v).getName() + ".");
			
			String sVarName = vars.get(v).getName();


			if( sVarName.equals(Constants.ROOM_VAR_P1NAME))
				currGame.sPlayer1Name = vars.get(v).getStringValue();
			
			else if( sVarName.equals(Constants.ROOM_VAR_P2NAME))
				currGame.sPlayer2Name = vars.get(v).getStringValue();
			
			else if( sVarName.equals(Constants.ROOM_VAR_P1STATE))
			{
				currGame.iPlayer1State = vars.get(v).getIntValue();
				currGame.checkState();
			}
			else if( sVarName.equals(Constants.ROOM_VAR_P2STATE))
			{
				currGame.iPlayer2State = vars.get(v).getIntValue();
				currGame.checkState();
			}

			
			else if( sVarName.equals(Constants.ROOM_VAR_LENGTH))
				currGame.iLength = vars.get(v).getIntValue();
			

		}
		
	}
}
