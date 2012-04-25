package princeTron.Network;

import android.graphics.Point;

public abstract class NetworkGame
{
    public abstract void opponentTurn(int snake_id, princeTron.Engine.Coordinate position, int time, boolean isLeft);
    public abstract void gameResult(int playerId, boolean wonGame);
}