package pl.smartplayer.smartplayerapp.field;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
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

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.api.ApiClient;
import pl.smartplayer.smartplayerapp.api.FieldService;
import pl.smartplayer.smartplayerapp.utils.UtilMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.smartplayer.smartplayerapp.main.MainActivity.sClubId;

public class CreateFieldActivity extends AppCompatActivity implements LocationListener {

    private static final double FIELD_IMAGE_WIDTH_IN_PIXELS = 768.0;
    private static final double FIELD_IMAGE_HEIGHT_IN_PIXELS = 554.0;

    private Location mLastLocation;
    private Location mCurrentLoadedLocation;
    private LocationManager mLocationManager;

    private int mCornerCounter = 0;
    private Map<String,Map<String, Double>> mCornersCoordinates = new HashMap<>();

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

    private ProgressDialog _creatingFieldProgressDialog;
    private FieldService fieldService = null;

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
        //mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        fieldService = ApiClient.getClient().create(FieldService.class);
        _creatingFieldProgressDialog = new ProgressDialog(this);
        _creatingFieldProgressDialog.setMessage(getString(R.string.creating_field));
        _creatingFieldProgressDialog.setCancelable(false);

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
        if(mLastLocation == null) {
            Toast.makeText(getApplicationContext(), R.string.device_found_location, Toast
                    .LENGTH_SHORT).show();
        }
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
        if(mLastLocation != null) {
            int latId = getResources().getIdentifier("lat"+ mCornerCounter, "id", getPackageName());
            EditText latEditText = findViewById(latId);
            int lonId = getResources().getIdentifier("lon"+ mCornerCounter, "id", getPackageName());
            EditText lonEditText = findViewById(lonId);


            latEditText.setText(Double.toString(mLastLocation.getLatitude()));
            lonEditText.setText(Double.toString(mLastLocation.getLongitude()));
            mCurrentLoadedLocation = mLastLocation;

            if(mCornerCounter == 3) {
                _goToCornerTextView.setText(R.string.enter_field_name);
                _nextButton.setText(R.string.confirm);
            }
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.wait_until_device_find_location, Toast
                    .LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.next_button)
    public void onNextButtonClick() {
        if(mCurrentLoadedLocation !=null) {
            Map<String, Double> location = new HashMap<>();
            location.put("lat", mCurrentLoadedLocation
                    .getLatitude());
            location.put("lng", mCurrentLoadedLocation
                    .getLongitude());
            mCornersCoordinates.put(getString(getResources().getIdentifier("corner_" +
                            mCornerCounter, "string",
                    getPackageName())), location);

            if(mCornerCounter == 3) {
                if(isFieldNameCorrect()) {
                    _creatingFieldProgressDialog.show();
                    String fieldName = _fieldNameEditText.getText().toString();
                    Field field = new Field(fieldName, "", true,
                            mCornersCoordinates, sClubId);
                    Call<Field> call = fieldService.createField(field);
                    call.enqueue(callback);

                } else {
                    Toast.makeText(getApplicationContext(), R.string.you_must_enter_field_name, Toast
                            .LENGTH_SHORT).show();
                }
            }

            mCornerCounter++;
            mCurrentLoadedLocation = null;

            int backgroundId = getResources().getIdentifier("field_" + mCornerCounter, "drawable",
                    getPackageName());
            _fieldView.setImageResource(backgroundId);
        } else {
            Toast.makeText(getApplicationContext(), R.string.you_must_load_coordinates, Toast
                    .LENGTH_SHORT).show();
        }
    }

    private Callback<Field> callback = new Callback<Field>() {
        @Override
        public void onResponse(Call<Field> call, Response<Field> response) {
            if(response.isSuccessful()){
                Field createdField = response.body();
                _creatingFieldProgressDialog.dismiss();

                Intent returnIntent = new Intent();
                returnIntent.putExtra("createdField", createdField);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
            else {
                Dialog dialog = UtilMethods.createInvalidConnectWithApiDialog(CreateFieldActivity.this,
                        call, callback, _creatingFieldProgressDialog);
                dialog.show();
            }
        }

        @Override
        public void onFailure(final Call<Field> call, Throwable t) {
            Dialog dialog = UtilMethods.createInvalidConnectWithApiDialog(CreateFieldActivity.this,
                    call, callback, _creatingFieldProgressDialog);
            dialog.show();
        }
    };

    private boolean isFieldNameCorrect() {
        return _fieldNameEditText.getText().length() != 0;
    }
}
