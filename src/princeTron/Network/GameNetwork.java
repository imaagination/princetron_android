package princeTron.Network;

import android.graphics.Point;

public abstract class GameNetwork
{
    abstract void startGame(int wait_time); 
    abstract void opponentTurn(int snake_id, Point position, int time, boolean isLeft);
    abstract void gameOver(boolean wonGame);
}