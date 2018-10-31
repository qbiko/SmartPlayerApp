package pl.smartplayer.smartplayerapp.field;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.smartplayer.smartplayerapp.R;

public class CreateFieldActivity extends AppCompatActivity implements LocationListener {

    private static final double FIELD_IMAGE_WIDTH_IN_PIXELS = 768.0;
    private static final double FIELD_IMAGE_HEIGHT_IN_PIXELS = 554.0;

    private Location mLastLocation;
    private Location mCurrentLoadedLocation;
    private LocationManager mLocationManager;

    private int mCornerCounter = 0;
    private List<Location> mCornersCoordinates = new ArrayList<>();

    @BindView(R.id.field_view)
    ImageView _fieldView;
    @BindView(R.id.main_container)
    LinearLayout _mainContainer;
    @BindView(R.id.command_text_view)
    TextView _goToCornerTextView;
    @BindView(R.id.next_button)
    Button _nextButton;
    @BindView(R.id.field_name_edit_text)
    TextView _fieldNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_field);
        ButterKnife.bind(this);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        //TODO: it is NETWORK_PROVIDER to test inside, change to GPS_PROVIDER in final solution
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        //mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //set dynamically padding in main container
        int verticalPadding = screenHeight * 2 / 100;
        _mainContainer.setPadding(0, verticalPadding, 0, verticalPadding);

        //set dynamically size of field ImageView
        double fieldViewWidthDouble = screenWidth * 2 / 5;
        double compressPercentOfFieldImageSize = fieldViewWidthDouble / FIELD_IMAGE_WIDTH_IN_PIXELS;
        int fieldViewWidth = (int) fieldViewWidthDouble;
        int fieldViewHeight = (int) (FIELD_IMAGE_HEIGHT_IN_PIXELS * compressPercentOfFieldImageSize);

        _fieldView.getLayoutParams().width = fieldViewWidth;
        _fieldView.getLayoutParams().height = fieldViewHeight;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }

    @OnClick(R.id.load_button)
    public void onLoadButtonClick() {
        int latId = getResources().getIdentifier("lat"+ mCornerCounter, "id", getPackageName());
        EditText latEditText = findViewById(latId);
        int lonId = getResources().getIdentifier("lon"+ mCornerCounter, "id", getPackageName());
        EditText lonEditText = findViewById(lonId);

        latEditText.setText(Double.toString(mLastLocation.getLatitude()));
        lonEditText.setText(Double.toString(mLastLocation.getLongitude()));
        mCurrentLoadedLocation = mLastLocation;

        if(mCornerCounter ==3) {
            _goToCornerTextView.setText(R.string.enter_field_name);
            _nextButton.setText(R.string.confirm);
        }
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        if(mCurrentLoadedLocation !=null) {
            if(mCornerCounter ==3) {
                if(isFieldNameCorrect()) {
                    Intent returnIntent = new Intent();
                    String fieldName = _fieldNameEditText.getText().toString();
                    // TODO: handle post request to database and get id for field
                    Field createdField = new Field(123, fieldName, mCornersCoordinates);
                    returnIntent.putExtra("createdField", createdField);
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.you_must_enter_field_name, Toast
                            .LENGTH_SHORT).show();
                }
            }

            mCornerCounter++;
            mCornersCoordinates.add(mCurrentLoadedLocation);
            mCurrentLoadedLocation = null;

            int backgroundId = getResources().getIdentifier("field_" + mCornerCounter, "drawable",
                    getPackageName());
            _fieldView.setImageResource(backgroundId);
        } else {
            Toast.makeText(getApplicationContext(), R.string.you_must_load_coordinates, Toast
                    .LENGTH_SHORT).show();
        }
    }

    private boolean isFieldNameCorrect() {
        return _fieldNameEditText.getText().length() != 0;
    }
}
