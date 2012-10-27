package sfsExtension;

import java.util.*;

import utils.Commands;
import utils.Constants;


//import com.smartfoxserver.v2.SmartFoxServer;
import com.smartfoxserver.v2.api.ISFSApi;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.Zone;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.RoomVariable;
import com.smartfoxserver.v2.entities.variables.SFSRoomVariable;
import com.smartfoxserver.v2.entities.User;
//import com.smartfoxserver.v2.exceptions.SFSVariableException;
import com.smartfoxserver.v2.extensions.SFSExtension;

//import jgam.Game;
import jgam.JGame;


public class SfsTestExtension extends SFSExtension 
{
	public ISFSApi sfsApi;
	
	
	/** Current games */
	public HashMap<Integer, JGame> games = null;

	
	public static final String DATABASE_ID = "dbID";
	
	public Zone zone;
	private List<Room> rooms;


	@Override
	public void init()
	{
		trace("Extention * *START* *");
		
	
		// Initialise games list
		games = new HashMap<Integer, JGame>(1000);
		
		// Obtain a reference to the parent Zone handler
		zone = this.getParentZone();
		
		// Add games based on room list on server
		rooms = zone.getRoomList();
		try
		{
			for( int r = 0; r < rooms.size(); r++ )
			{	
				if( rooms.get(r).isGame())
				{
					// Add the game instance ..
					int iPlayersMax = 0;
					
					// Get room variables
					List<RoomVariable> roomVars = rooms.get(r).getVariables();
					
					trace("room varibles size = " + roomVars.size());
					
					for( int rv = 0; rv < roomVars.size(); rv++ )
					{	
						RoomVariable roomVar = roomVars.get(rv);
						
						if(roomVar.getName().equals("userCounter"))
						{ 
							// TODO: get the current room variable;
							trace(roomVar.getStringValue());
						}
					}
					
					trace("Adding room id=:" + rooms.get(r).getId() + " iPlayersMax=" + iPlayersMax);
					
					// Initialise the game and add to the games array
					JGame currGame = new JGame(rooms.get(r).getId(), "Room #" + rooms.get(r).getId(), this); //, rooms.get(r).getId(), rooms.get(r).getName()
					startGame(currGame);
					games.put(rooms.get(r).getId(), currGame);

				}
			}

		}catch (Exception e)
		{
			trace(e);
		}
		
		trace("* - registering handlers...");
		
		addRequestHandler(Commands.PLAYER_TURN, PlayerTurnRequestHandler.class);
		
		addEventHandler(SFSEventType.USER_LOGIN, UserLoginHandler.class);
		addEventHandler(SFSEventType.USER_JOIN_ROOM, UserJoinedRoomHandler.class);
		addEventHandler(SFSEventType.USER_LEAVE_ROOM, UserLeaveRoomHandler.class);
		addEventHandler(SFSEventType.USER_DISCONNECT, UserDisconnectHandler.class);
		addEventHandler(SFSEventType.ROOM_VARIABLES_UPDATE, RoomVarsUpdateHandler.class);
		addEventHandler(SFSEventType.ROOM_ADDED, RoomAddedHandler.class);
	}

	/* GETTERS & SETTERS */


	

	public SFSObject getGamesArrayObject()
	{
		SFSObject gamesArrayObject = new SFSObject();
		ISFSArray gamesArray = new SFSArray();
		
		// All the running games
		for (Map.Entry<Integer, JGame> entry : games.entrySet())
		{	
			JGame aGame = (JGame) entry.getValue();
		
			ISFSObject gameData = new SFSObject();
			gameData.putInt("id", aGame.roomId);
			gameData.putUtfString("name", aGame.sName);
			gameData.putUtfString("player1Name", aGame.sPlayer1Name);
			gameData.putUtfString("player2Name", aGame.sPlayer2Name);
			gameData.putInt("state", aGame.iState);
						
			gamesArray.addSFSObject(gameData);
			
			trace("adding game:" + aGame.roomId);
		}
		gamesArrayObject.putSFSArray("gamesArray", gamesArray);
		
		return gamesArrayObject;
	}
	
	
	
	public void sendRoomList(User sender)
	{		
		send(Commands.ROOM_LIST, getGamesArrayObject(), sender);
	}
	
	
	public void sendRoomListToAll()
	{
		trace("sendRoomListToAll");
		// TODO: better to use a Lobby room, instead of sending to everyone in zone
		send(Commands.ROOM_LIST, getGamesArrayObject(), zone.getUserManager().getAllUsers());
	}

	public JGame getGame(Integer id)
	{
		return (JGame)games.get(id);
	}

	// Update RoomVar state of player (eg countdown and status updates)
	public void sendRoomVariables(JGame gameRoom, int iPlayerId)
	{
		Room theRoom = zone.getRoomById(gameRoom.roomId);		
		
		trace("iPlayerId == " + iPlayerId);
		
		RoomVariable rv;
		if( iPlayerId == 0)
			rv = new SFSRoomVariable(Constants.ROOM_VAR_P1STATE, gameRoom.iPlayer1State);
		else
			rv = new SFSRoomVariable(Constants.ROOM_VAR_P2STATE, gameRoom.iPlayer2State);
		
		// Update room variable
		ArrayList<RoomVariable> varList = new ArrayList<RoomVariable>();
		varList.add(rv);
		getApi().setRoomVariables(null, theRoom, varList, true, false, false);
	    
	    trace("Update room variable");	
	}
	
	//Send start game invocation
	public void startGame(JGame gameRoom)
	{
		trace("start game Room.roomId = " + gameRoom.roomId);
	
		Room theRoom = zone.getRoomById(gameRoom.roomId);
		List<User> recipients = theRoom.getUserList();
		
		// Object to send
		ISFSObject sfsData = SFSObject.newInstance();
		//sfsData.putInt("Dealer", currGame.iDealerPos);
		
		send(Commands.START_GAME, sfsData, recipients);		
	}



	public void playerTurn(JGame gameRoom, int iFrom, int iTo)
	{
		// Basically "Player Move", and handles single or multi-move automatically 
		// as based on iFrom and iTo (and the current player object)
		
		
		jgam.Player currPlayer = gameRoom.game.getCurrentPlayer();
		
		
		// Wait until able to move TODO: strange problem here, sometimes not waiting.
		// iProcesingMove++;   "iProcesingMove=" + iProcesingMove +" allowMoves=" +
		
		trace("P#" + currPlayer.iPlayerId + gameRoom.game.getCurrentPlayer().allowMoves.get());
		while(  !gameRoom.game.getCurrentPlayer().allowMoves.get() ) //|| iProcesingMove > 1
		{
			trace("P#" + currPlayer.iPlayerId + gameRoom.game.getCurrentPlayer().allowMoves.get());
			try {
				Thread.sleep(250);
				trace("waiting for player to allowMove");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		currPlayer.allowMoves.set(false);
		

		// Update server game
		trace(" do move.. " + iFrom);
		gameRoom.getGame().handle(iFrom);
		
		trace("finished move");
		
	}
	
	public void sfsSendMove(JGame gameRoom, int iPlayerId, int iFrom, int iTo)
	{	
		// Get list of all users
		Room theRoom = zone.getRoomById(gameRoom.roomId);
		List<User> recipients = theRoom.getUserList();
		
		// Invert positions to what client expects, if its the b. player
		//  ..and not an undo move
		if( iPlayerId == 1 && (iFrom != -100 || iTo != -100))
		{
			if( iFrom <= 24)		// Careful not to invert bar positions
				iFrom = 24 - iFrom + 1;
			if( iTo <= 24)		// Careful not to invert home or bar positions
				iTo = 24 - iTo + 1;
		}
				
		trace("Sending from=" + iFrom + " to=" + iTo + " playerId = " + iPlayerId);
		
		// Data Object to Send
		ISFSObject objectData = new SFSObject();
		objectData.putInt("iPlayerId", iPlayerId);
		objectData.putInt("iFrom", iFrom);
		objectData.putInt("iTo", iTo);
		send(Commands.PLAYER_TURN, objectData, recipients);
	}
	
	
	
	public void removeGame(JGame gameRoom)
	{
		trace("closeGame roomId=" + gameRoom.roomId);
		
		// Removes instance of game
		//if( gameRoom.game != null)
		gameRoom.clearGame();
		
		trace("REMOVING FROM ZONES");
		
		// Ensure the SFS Room has been removed
		zone.removeRoom(gameRoom.roomId);
		
		// Remove this game completely
		games.remove(gameRoom.roomId);
				
				
		// Issue lobby update
		sendRoomListToAll();
	}
	
	
	/**
	 * Destroy
	 */
	public void destroy()
	{
		trace("extension destroyed");
		
		removeEventHandler(SFSEventType.USER_JOIN_ROOM);
		removeEventHandler(SFSEventType.USER_LEAVE_ROOM);
		
		removeEventHandler(SFSEventType.USER_LOGOUT);
		removeEventHandler(SFSEventType.USER_DISCONNECT);
	}

}
