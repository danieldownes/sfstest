package
{
	import flash.display.Sprite;
	import flash.events.*;
	
	import com.smartfoxserver.v2.core.SFSEvent;
	import com.smartfoxserver.v2.entities.data.SFSObject;
	import com.smartfoxserver.v2.requests.ExtensionRequest;
	
	public class Main extends Sprite
	{
		public static var singletron:Main;
		
		public var SFSConn:Connector;
		
		public var mScreen:testscreen = new testscreen();
		
		public function Main():void
		{
			if(stage)
				init();
			else
				addEventListener(Event.ADDED_TO_STAGE, init);
		}
		
		private function init(e:Event = null):void
		{
			removeEventListener(Event.ADDED_TO_STAGE, init);
			
			addChild(mScreen);
			
			mScreen.btnJoin.addEventListener(MouseEvent.CLICK, btnCreate_click);
			mScreen.btnSend.addEventListener(MouseEvent.CLICK, btnSend_click);
			
			singletron = this;
			
			SFSConn = new Connector();
			
			trace("Connecting...");
			
			
			// Connect
			Main.singletron.SFSConn.username = "Guest" + Math.floor(Math.random() * 9998 + 1).toString();
			Main.singletron.SFSConn.initConnect("SfsTest");
			
			// On connect, load lobby screen
			Main.singletron.SFSConn.sfs.addEventListener(SFSEvent.LOGIN, onLogin);
			
			Main.singletron.SFSConn.addEventListener(PlayerTurn.PLAYER_TURN, playerTurn_event);
		}
		
		
		private function onLogin(e:Event):void 
		{
			trace("In 'lobby', please create new game");
		}
		
		
		private function btnCreate_click(e:MouseEvent):void 
		{
			SFSConn.createRoom("sfsTest");
		}
		
		
		private function btnSend_click(e:MouseEvent):void 
		{
			for (var n:int = 1; n <= 4; n++)
				playerTurn_request(0, n, n);
		}
		
		private function playerTurn_request(iPlayerId_:int, iFrom_:int, iTo_:int):void
		{
			// Contruct SFS Object
			var paramsObj:SFSObject = new SFSObject();
			paramsObj.putInt("iPlayerId", iPlayerId_);
			paramsObj.putInt("iFrom", iFrom_);
			paramsObj.putInt("iTo", iTo_);
			
			trace("**send playerTurn request")
			Main.singletron.SFSConn.sfs.send(new ExtensionRequest("playerTurn", paramsObj));
		}
		
		private function playerTurn_event(e:PlayerTurn):void
		{
			trace("playerTurn_event -- P#:" + e.iPlayerId + " iFrom=" + e.iFrom);
		}
		
		
	}
	
}