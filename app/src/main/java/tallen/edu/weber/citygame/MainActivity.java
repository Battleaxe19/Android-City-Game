package tallen.edu.weber.citygame;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title_screen);

        //get screen size to center circle
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        //paint circle
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#3FA6F5"));
        Bitmap bg = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bg);
        canvas.drawCircle(width/2, height/4, 250, paint);
        LinearLayout ll = (LinearLayout) findViewById(R.id.rect);
        ll.setBackgroundDrawable(new BitmapDrawable(bg));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

}
