package sfsExtension;

import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;


public class UserLoginHandler extends BaseServerEventHandler
{
	
    public void handleServerEvent(ISFSEvent event) throws SFSException
    {
        String name = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
        trace("User Logged in:" + name);

        Room room = (Room) event.getParameter(SFSEventParam.ROOM);
        int userCount = room.getUserList().size();
		trace("userCount = " + userCount);
	
		// TODO: Confirm this user is real via web request.
		
		
		String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
		String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
		//ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
		
		trace(userName + cryptedPass);
		

		
    }
}
