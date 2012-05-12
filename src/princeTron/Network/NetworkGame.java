package princeTron.Network;

import android.graphics.Point;

public abstract class NetworkGame
{
    public abstract princeTron.Engine.Coordinate opponentTurn(int snake_id, int time, boolean isLeft);
    public abstract void gameResult(int playerId, boolean wonGame);
}