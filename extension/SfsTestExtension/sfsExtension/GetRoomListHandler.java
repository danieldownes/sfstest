package sfsExtension;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;


public class GetRoomListHandler extends BaseClientRequestHandler
{

	@Override
	public void handleClientRequest(User sender, ISFSObject arg)
	{
		trace("recieved ROOM_LIST request");
		
		((SfsTestExtension) getParentExtension()).sendRoomList(sender);
		
	}

}
