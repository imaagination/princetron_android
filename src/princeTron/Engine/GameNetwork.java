package princeTron.Engine;

public abstract class GameNetwork {

	// pass GameEngine to GameNetwork so network can call back to GameEngine
	public abstract void setGameEngine(princeTron.Engine.GameEngine engine);
	// informs the Network that the user has turned
	public abstract void userTurn(Coordinate position, int time, boolean isLeft);
	// informs the Network that the user crashed
	public abstract void userCrash(Coordinate location, int time);
	
	
}
