package pl.smartplayer.smartplayerapp.field;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.smartplayer.smartplayerapp.R;

public class CreateFieldActivity extends AppCompatActivity implements LocationListener {

    private double FIELD_IMAGE_WIDTH_IN_PIXELS = 768.0;
    private double FIELD_IMAGE_HEIGHT_IN_PIXELS = 554.0;

    @BindView(R.id.field_view)
    ImageView _fieldView;
    @BindView(R.id.main_container)
    LinearLayout _mainContainer;

    private Location lastLocation;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_field);
        ButterKnife.bind(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
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
        //it is NETWORK_PROVIDER to test inside, change to GPS_PROVIDER in final solution
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);


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

    @OnClick(R.id.load_button)
    public void onLoadButtonClick() {
        TextView lat1 = findViewById(R.id.lat1);
        TextView lon1 = findViewById(R.id.lon1);

        lat1.setText(Double.toString(lastLocation.getLatitude()));
        lon1.setText(Double.toString(lastLocation.getLongitude()));
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
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
}
