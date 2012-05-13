package princeTron.Network;

public abstract class GameNetwork
{
    public abstract void startGame(int wait_time, int numPlayers); 
    public abstract void opponentTurn(int snake_id, princeTron.Engine.Coordinate position, int time, boolean isLeft);
    public abstract void gameOver(boolean wonGame);
}