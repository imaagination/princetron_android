package princeTron.Engine;

import android.graphics.Point;
//import java.awt.Point;

public abstract class GameNetwork {

    //Abstract methods must be declared public
    // pass GameEngine to GameNetwork so network can call back to GameEngine
    public abstract void setGameEngine(princeTron.Engine.GameEngine engine);
    // informs the Network that the user has turned
    public abstract void userTurn(Point position, int time, boolean isLeft);
    // informs the Network that the user crashed
    public abstract void userCrash(Point location, int time);
	
	
}
