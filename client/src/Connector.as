package
{
	import com.smartfoxserver.v2.entities.managers.SFSRoomManager;
	import com.smartfoxserver.v2.SmartFox;
	import com.smartfoxserver.v2.core.SFSEvent;
	import com.smartfoxserver.v2.entities.*;
	import com.smartfoxserver.v2.entities.data.*;
	import com.smartfoxserver.v2.requests.*;
	import com.smartfoxserver.v2.entities.variables.*;
	
	
	import flash.display.*
	import flash.events.*
	import flash.system.Security
	
	
	public class Connector extends Sprite
	{
		public var sfs:SmartFox;
				
		public var username:String;
		private var zoneName:String;
		private var roomName:String;
	
		public var bIsLogin:Boolean;
		
		
		public function Connector()
		{
			sfs = new SmartFox();
		}
		
		public function initConnect(zonename_:String):void
		{
			sfs.debug = true
			zoneName = zonename_;
			bIsLogin = false;
			
			// step 1 : load the configure file; if success
			sfs.loadConfig();
			
			sfs.addEventListener(SFSEvent.CONFIG_LOAD_SUCCESS, onConfigLoadSuccess);
			sfs.addEventListener(SFSEvent.CONFIG_LOAD_FAILURE, onConfigLoadFailure);
			
			sfs.addEventListener(SFSEvent.CONNECTION, onConnection);
			sfs.addEventListener(SFSEvent.CONNECTION_LOST, onConnectionLost);
			sfs.addEventListener(SFSEvent.SOCKET_ERROR, onConnectionLost);
			
			sfs.addEventListener(SFSEvent.LOGIN, onLogin);
			sfs.addEventListener(SFSEvent.LOGIN_ERROR, onLoginError);
			
			sfs.addEventListener(SFSEvent.ROOM_JOIN, onRoomJoin);
			sfs.addEventListener(SFSEvent.ROOM_JOIN_ERROR, onJoinError);
			
			sfs.addEventListener(SFSEvent.ROOM_VARIABLES_UPDATE, onRoomVariablesUpdate);
			sfs.addEventListener(SFSEvent.USER_VARIABLES_UPDATE, onUserVariablesUpdate);
			
			sfs.addEventListener(SFSEvent.PUBLIC_MESSAGE, onPublicMessage)
			sfs.addEventListener(SFSEvent.EXTENSION_RESPONSE, onExtensionRequest);
			
		}
	
		
		public function createRoom(roomName_:String):void //, aSettings_:Array):void
		{
			roomName = roomName_;
			
			var roomSets:RoomSettings = new RoomSettings(roomName_);
			roomSets.isGame = true;
			roomSets.maxVariables = 10;
			
			if( bIsLogin)
			{
				trace(" ** CreateRoomRequest roomName_=" + roomName_);
				sfs.send(new CreateRoomRequest(roomSets, true));
			}
		}
		
		public function joinRoom(roomId_:int):void
		{
			if( bIsLogin)
			{
				trace(" ** JoinRoomRequest");
				sfs.send(new JoinRoomRequest(roomId_));
			}
		}
		
		public function leaveRoom():void //roomId_:int
		{
			if( bIsLogin)
			{
				sfs.send(new LeaveRoomRequest());
			}
		}
		
		private function onPublicMessage(evt:SFSEvent):void
		{
			var sender:User = evt.params.sender;
			var msg:String = evt.params.message;
			
			trace("onPublicMessage: " + msg);
		}
		
		public function onExtensionRequest(eve:SFSEvent):void
		{
			var whichPlayer:int;
			var params:SFSObject = eve.params.params;
			var cmd:String = eve.params.cmd;
			
			trace("Respone:" + cmd);
			
			switch(cmd)
			{
				case PlayerTurn.PLAYER_TURN:
					dispatchEvent(new PlayerTurn(params.getInt("iPlayerId"), params.getInt("iFrom"), params.getInt("iTo")));
					
					break;
			}
		}
		
		private function onConfigLoadSuccess(evt:SFSEvent):void
		{
			trace("config load success");
			sfs.addEventListener(SFSEvent.CONNECTION, onConnection);
			sfs.addEventListener(SFSEvent.CONNECTION_LOST, onConnectionLost);
		}
		
		private function onConfigLoadFailure(evt:SFSEvent):void
		{
			trace("connect failure");
		}

		private function onConnection(evt:SFSEvent):void
		{
			if (evt.params.success)
			{
				trace("Connected!");
				sfs.send(new LoginRequest(username, "", zoneName, null));
			}
			else
			{
			}
		}
		
		private function onConnectionLost(evt:SFSEvent):void
		{
			trace("Connection was lost, try reloading. " + evt.params.reason);
		}
		
		private function onLogin(eve:SFSEvent):void
		{
			trace("Login seccess: " + eve.params.user.name);
			bIsLogin = true;
		}
		
		private function onLoginError(evt:SFSEvent):void
		{
			trace("Login failed: " + evt.params.errorMessage);
			bIsLogin = false;
		}
		
		private function onJoinError(evt:SFSEvent):void
		{
			trace("Join room error " + evt.params);
		}
		
		
		private function onRoomJoin(evt:SFSEvent):void
		{
			trace("Entered room: " + evt.params.room.name);
			
			var roomJoined:Room = evt.params.room;
			
			// List users in this room
			trace("user list: ...")
			for each (var u:User in roomJoined.userList)
			{
				trace("   Username: " + u.name);
			}
			
			// If user joined a game room switch to game scene
			if (roomJoined.isGame)
			{
				trace("Entered Game Room")
				
			}else  // User joined the lobby
			{
				trace("Entered Lobby");
			}
		}
				
		private function onRoomVariablesUpdate(evt:SFSEvent):void
		{
			trace("onRoomVariablesUpdate");
		}
		
		private function onUserVariablesUpdate(evt:SFSEvent):void
		{
			trace("Join failed: " + evt.currentTarget.error);
		}
	}
}