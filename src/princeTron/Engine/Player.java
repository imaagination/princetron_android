package princeTron.Engine;


public class Player {
	public int dir;
	public int x;
	public int y;
        public boolean active;
        public int id;

    public Player(int x, int y, int dir, int id){
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.active = true;
		this.id = id;
	}

	public Player() {}

}

