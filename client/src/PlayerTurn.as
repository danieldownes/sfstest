package 
{
	import flash.events.Event;
	
	public class PlayerTurn extends Event
	{
		public static const PLAYER_TURN:String = "playerTurn";
		
		public var iPlayerId:int;
		public var iTo:int;
		public var iFrom:int;
		
		public function PlayerTurn(iPlayerId_:int, iFrom_:int, iTo_:int)
		{
			super(PLAYER_TURN, true);
			
			iPlayerId = iPlayerId_;
			iFrom = iFrom_;
			iTo = iTo_;
		}
		
	}
}
