package princeTron.Engine;

import java.util.Collection;

public abstract class GameNetwork {

	// pass GameEngine to GameNetwork so network can call back to GameEngine
	public abstract void setGameEngine(princeTron.Engine.GameEngine engine);
	// informs the Network that the user has turned
	public abstract void userTurn(Coordinate position, int time, boolean isLeft);
	// informs the Network that the user crashed
	public abstract void userCrash(Coordinate location, int time);
	// tells the network that we're ready to play, and potentially invites friends
	public abstract void readyToPlay(Collection<String> invites);
	// logs in with the given account name
	public abstract void logIn(String accountName);
	// accept an invitation to play
	public abstract void acceptInvitation();
	// disconnects socket
	public abstract void disconnect();
}
