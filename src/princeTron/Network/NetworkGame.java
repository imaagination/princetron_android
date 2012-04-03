package princeTron.Network;

import android.graphics.Point;

public abstract class NetworkGame
{
    public abstract void startGame(int wait_time); 
    public abstract void opponentTurn(int snake_id, Point position, int time, boolean isLeft);
    public abstract void gameOver(boolean wonGame);
}