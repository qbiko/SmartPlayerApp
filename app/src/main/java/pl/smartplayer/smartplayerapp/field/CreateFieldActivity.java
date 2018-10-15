package pl.smartplayer.smartplayerapp.field;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.smartplayer.smartplayerapp.R;

public class CreateFieldActivity extends AppCompatActivity {

    private double FIELD_IMAGE_WIDTH_IN_PIXELS = 768.0;
    private double FIELD_IMAGE_HEIGHT_IN_PIXELS = 554.0;

    @BindView(R.id.field_view)
    ImageView _fieldView;
    @BindView(R.id.main_container)
    LinearLayout _mainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_field);
        ButterKnife.bind(this);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //set dynamically padding in main container
        int verticalPadding = screenHeight*2/100;
        _mainContainer.setPadding(0, verticalPadding, 0, verticalPadding);

        //set dynamically size of field ImageView
        double fieldViewWidthDouble = screenWidth*2/5;
        double compressPercentOfFieldImageSize = fieldViewWidthDouble/FIELD_IMAGE_WIDTH_IN_PIXELS;
        int fieldViewWidth = (int)fieldViewWidthDouble;
        int  fieldViewHeight = (int)(FIELD_IMAGE_HEIGHT_IN_PIXELS*compressPercentOfFieldImageSize);

        _fieldView.getLayoutParams().width = fieldViewWidth;
        _fieldView.getLayoutParams().height = fieldViewHeight;
    }
}
