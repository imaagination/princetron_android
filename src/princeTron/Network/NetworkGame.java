package princeTron.Network;

public abstract class NetworkGame
{
    public abstract void opponentTurn(int snake_id, int time, boolean isLeft);
    public abstract void gameResult(int playerId, boolean wonGame);
}