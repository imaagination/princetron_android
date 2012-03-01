package princeTron.UserInterface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Toast;

public class ArenaView extends View {
	private int xMin = 0;          // This view's bounds
	private int xMax;
	private int yMin = 0;
	private int yMax;
	private Paint paint;           // The paint (e.g. style, color) used for drawing
	private int xCoord = 0;
	private int yCoord = 50;
	private float[][] pointsVisited;   //points visited for all players
	private int pointIndex;
	private int numPlayers = 1;
	private Context context;
	private int duration = Toast.LENGTH_LONG;
	private Toast toast;
	private CharSequence test1;

	// Constructor
	public ArenaView(Context context) {
		super(context);
		this.context = context;
		paint = new Paint();
		paint.setColor(Color.rgb(255,140,0));
		paint.setStrokeWidth(6);
//		paint.setStrokeCap(Paint.Cap.ROUND);
		pointsVisited = new float[1][5000];
		pointIndex = 0;

	}

	public void addPoints(float[] points){
		int j = 0;

//		test1 = "Number of players: " + numPlayers;
//		toast = Toast.makeText(context, test1, duration);
//		toast.show();

		//update player positions
			pointsVisited[0][pointIndex++] = points[j++];
			pointsVisited[0][pointIndex++] = points[j];
	}

	// Called back to draw the view. Also called by invalidate().
	@Override
	protected void onDraw(Canvas canvas) {

		xCoord++;
		
		// Update the position of the head, including collision detection and reaction.
		update(xCoord, yCoord);

		
		canvas.drawPoints(pointsVisited[0], paint);
	


		// Delay
//		try {  
//			Thread.sleep(10);  
//		} catch (InterruptedException e) { }


		invalidate();  // Force a re-draw
	}

	// Detect collision and update the position of the ball.
	private void update(int x, int y) {
		// Get new (x,y) position
		this.xCoord = x;
		this.yCoord = y;


		// Detect collision and react
		if (xCoord > xMax) {
			xCoord = 0;
			yCoord += 5;
		} else if (xCoord < xMin) {
			xCoord = 0;
		}
		if (yCoord > yMax) {
			yCoord = 0;
		} else if (yCoord < yMin) {
			yCoord = 0;
		}

//		float[] points = new float[(numPlayers*2)];
		
		float[] points = {xCoord,yCoord};
		

		addPoints(points);
	}


	// Called back when the view is first created or its size changes.
	@Override
	public void onSizeChanged(int w, int h, int oldW, int oldH) {
		// Set the movement bounds for the ball
		xMax = w-1;
		yMax = h-1;
	}
}