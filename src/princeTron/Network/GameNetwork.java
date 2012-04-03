package princeTron.Network;

import android.graphics.Point;

public abstract class GameNetwork
{
    public abstract void startGame(int wait_time, int numPlayers); 
    public abstract void opponentTurn(int snake_id, Point position, int time, boolean isLeft);
    public abstract void gameOver(boolean wonGame);
}