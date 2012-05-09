package princeTron.UserInterface;

import princeTron.Engine.GameEngine;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class ArenaView extends SurfaceView implements SurfaceHolder.Callback {
	
	private GameEngine engine;
	private boolean surfaceReady;
	private int[][] gameboard;
	private int xSize;
	private int ySize;
	public int status;
	
	public Paint[] pallette;
	
	private static final int TILE_SIZE = 4;
	private int SCREEN_WIDTH;
	private int SCREEN_HEIGHT;
	private int X_OFF;
	
	public static final int WAITING = 1;
	public static final int READY = 2;
	public static final int PLAYING = 3;
	
	public ArenaView(Context context, AttributeSet as) {
		this(context);
	}
	
	public ArenaView(Context context) {
		super(context);
		
		initializePallette();
		
		getHolder().addCallback(this);
		setFocusable(true);
		surfaceReady = false;
		status = WAITING;
	}
	
	public void setGameboard(int[][] newGameBoard) {
		this.gameboard = newGameBoard;
		xSize = gameboard.length;
		ySize = gameboard[0].length;
	}
	
	public void setGameEngine(GameEngine engine) {
		this.engine = engine;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		surfaceReady = true;
		renderArena();
	}
		 
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		surfaceReady = false;
	}
	
	private void initializePallette() {
		pallette = new Paint[5];
		for (int i = 0; i < pallette.length; i++) pallette[i] = new Paint();
		pallette[0].setColor(Color.BLACK);
		pallette[1].setColor(Color.GREEN);
		pallette[2].setColor(Color.YELLOW);
		pallette[3].setColor(Color.MAGENTA);
		pallette[4].setColor(Color.BLUE);
	}
	
	private Paint getPlayerPaint(int id) {
		if (id == -1) return pallette[0];
		else if (id >= 0 && id <= 4) return pallette[id + 1];
		return pallette[0];
	}
		 
	public void renderArena() {
		Log.d("ArenaView", "Rendering arena in state " + status);
		if (!surfaceReady) return;
		Canvas canvas;
		Paint textPaint = pallette[2]; 
		textPaint.setTextSize(60); 
		
		switch (status) {
		case WAITING: 
			canvas = getHolder().lockCanvas();
			canvas.drawColor(Color.BLACK);
			canvas.drawText("Waiting for opponents...", 50, 500, textPaint);
			getHolder().unlockCanvasAndPost(canvas);
			break;
		case READY:
			canvas = getHolder().lockCanvas();
			canvas.drawColor(Color.BLACK);
			canvas.drawText("Starting!", 50, 500, textPaint);
			getHolder().unlockCanvasAndPost(canvas);
			break;
		case PLAYING:
			if (gameboard == null) break;
			drawBoard();
			break;
		}
	}
	
	public void drawBoard() {
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.BLACK);
		SCREEN_WIDTH = getWidth();
		SCREEN_HEIGHT = getHeight();
		X_OFF = (SCREEN_WIDTH-xSize*TILE_SIZE)/2;
		for (int i = 0; i < xSize; i++) {
			for (int j = 0; j < ySize; j++) {
				canvas.drawRect(i*TILE_SIZE+X_OFF, (ySize-j-1)*TILE_SIZE, (i+1)*TILE_SIZE+X_OFF, (ySize-j)*TILE_SIZE, getPlayerPaint(gameboard[i][j]));
			}
		}
		getHolder().unlockCanvasAndPost(canvas);
	}
	
	public void fillSquare(int x, int y, int id) {
		if (status == PLAYING) {
			Canvas canvas = getHolder().lockCanvas();
			canvas.drawRect(x*TILE_SIZE+X_OFF, (ySize-y-1)*TILE_SIZE, (x+1)*TILE_SIZE+X_OFF, (ySize-y)*TILE_SIZE, getPlayerPaint(id));
			getHolder().unlockCanvasAndPost(canvas);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (status == PLAYING) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (event.getX() > getWidth() / 2) {
					Log.d("ArenaView", "Right turn");
					engine.turn(false);
				} else {
					Log.d("ArenaView", "Left turn");
					engine.turn(true);
				}
			}
		}
		return super.onTouchEvent(event);
	}
}