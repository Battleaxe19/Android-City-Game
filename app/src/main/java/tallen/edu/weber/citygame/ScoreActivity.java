package tallen.edu.weber.citygame;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by Tyler on 4/12/2015.
 */
public class ScoreActivity extends Activity {

    TextView tvPoints;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        String score = bundle.getString("score");

        setContentView(R.layout.activity_score);
        tvPoints = (TextView) findViewById(R.id.tvPoints);
        tvPoints.setText(score);
    }
}
