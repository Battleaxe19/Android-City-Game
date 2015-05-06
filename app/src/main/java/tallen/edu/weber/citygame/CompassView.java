package tallen.edu.weber.citygame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CompassView extends ImageView {
    Paint paint1;

    int direction = 0;

    public CompassView(Context context) {
        super(context);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }


    private void init(Context context) {

        paint1 = new Paint();
        paint1.setColor(Color.parseColor("#3FA6F5"));
        paint1.setStrokeWidth(2);

        this.setImageResource(R.drawable.pointer2);
        this.setBackgroundColor(Color.parseColor("#626465"));
    }

    @Override
    public void onDraw(Canvas canvas) {
        int height = this.getHeight();
        int width = this.getWidth();

        canvas.rotate(direction, width / 2, height / 2);
        canvas.drawCircle(width/2, height/2, 230, paint1);

        super.onDraw(canvas);
    }

    public void setDirection(int direction) {
        this.direction = direction;
        this.invalidate();
    }

}