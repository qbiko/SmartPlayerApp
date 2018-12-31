package pl.smartplayer.smartplayerapp.player;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.api.ApiClient;
import pl.smartplayer.smartplayerapp.api.PlayerService;
import pl.smartplayer.smartplayerapp.main.MldpBluetoothService;
import pl.smartplayer.smartplayerapp.main.PlayerOnGame;
import pl.smartplayer.smartplayerapp.utils.UtilMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.smartplayer.smartplayerapp.main.MainActivity.sClubId;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CONNECT_WITH_DEVICE_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.ENABLE_BT_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.UtilMethods.updateUIList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PlayerListActivity extends AppCompatActivity {

    private List<Player> mPlayers = new ArrayList<>();
    private PlayerListAdapter mPlayerListAdapter;
    private Player mSelectedPlayer;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<BluetoothDevice> mDeviceList = new ArrayList<BluetoothDevice>();
    private boolean mIsBluetoothEnabled = false;
    private Integer mPlayerPosition;


    @BindView(R.id.players_list_view)
    ListView _playersListView;
    @BindView(R.id.mac_address_text_view)
    TextView _macAddressTextView;
    @BindView(R.id.player_number_edit_text)
    EditText _playerNumberEditText;

    private ProgressDialog _scanningProgressDialog;
    private ProgressDialog _loadingPlayersProgressDialog;
    private MldpBluetoothService bleService;
    private Handler scanStopHandler;
    private boolean areScanning;
    private static final long SCAN_TIME = 10000;						                            //Length of time in milliseconds to scan for BLE devices

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);
        ButterKnife.bind(this);

        getSupportActionBar().hide();
        mPlayerListAdapter = new PlayerListAdapter(mPlayers,
                this.getApplicationContext());
        _playersListView.setAdapter(mPlayerListAdapter);

        PlayerService playerService = ApiClient.getClient().create(PlayerService.class);
        _loadingPlayersProgressDialog = new ProgressDialog(this);
        _loadingPlayersProgressDialog.setMessage(getString(R.string.loading_players));
        _loadingPlayersProgressDialog.setCancelable(false);
        _loadingPlayersProgressDialog.show();

        Call<List<Player>> call = playerService.getPlayersByClubId(sClubId);
        call.enqueue(callback);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            showToast(getString(R.string.unable_to_connect_by_bluetooth));
        }
        else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BT_REQUEST);
            }
        }

        _scanningProgressDialog = new ProgressDialog(this);

        _scanningProgressDialog.setMessage(getString(R.string.scanning));
        _scanningProgressDialog.setCancelable(false);
        _scanningProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                mBluetoothAdapter.cancelDiscovery();
            }
        });

        Intent bleServiceIntent = new Intent(this, MldpBluetoothService.class);	                    //Create Intent to bind to the MldpBluetoothService
        this.bindService(bleServiceIntent, bleServiceConnection, BIND_AUTO_CREATE);	                //Bind to the  service and use bleServiceConnection callbacks for service connect and disconnect
        scanStopHandler = new Handler();                                                            //Create a handler for a delayed runnable that will stop the scan after a time
    }
    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MldpBluetoothService.ACTION_BLE_SCAN_RESULT);
        registerReceiver (bleServiceReceiver, intentFilter);                                        //Register the receiver to receive the scan results broadcast by the service
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(bleService != null) {
            scanStopHandler.removeCallbacks(stopScan);                                              //Stop the scan timeout handler from calling the runnable to stop the scan
            scanStop();
        }
        unregisterReceiver(bleServiceReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(bleServiceConnection);                                                    //Unbind from the service
    }

    @OnClick(R.id.confirm_button)
    public void onConfirmButtonClick() {
        if(validateForm()){
            Intent returnIntent = new Intent();
            String mac = _macAddressTextView.getText().toString();
            int number = Integer.parseInt(_playerNumberEditText.getText().toString());
            PlayerOnGame playerOnGameToReturn = new PlayerOnGame(number, mSelectedPlayer,mac);
            returnIntent.putExtra("mSelectedPlayer", playerOnGameToReturn);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }

    @OnClick(R.id.connect_with_device_button)
    public void onConnectWithDeviceButtonClick() {

        Toast.makeText(this,R.string.connecting, Toast.LENGTH_LONG).show();
        if(bleService != null) {                                                                    //Service will not have started when activity first starts but this ensures a scan if resuming from pause
            scanStart();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (requestCode == ENABLE_BT_REQUEST) {
            if (resultCode == RESULT_OK) {
                mIsBluetoothEnabled = true;
            }
            else {
                showToast(getString(R.string.deny_turn_on_bluetooth));
            }
        }
        if (requestCode == CONNECT_WITH_DEVICE_REQUEST) {
            if (resultCode == RESULT_OK) {
                String addressMac = resultIntent.getExtras().getString("addressMac");
                _macAddressTextView.setText(addressMac);

                if(mPlayerPosition != null) {
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View view = inflater.inflate(R.layout.player_list_layout, null);
                    onFieldSelected(mPlayerPosition, view);
                }
            }
        }
    }

    @OnItemClick(R.id.players_list_view)
    public void onFieldSelected(int position, View view) {
        mSelectedPlayer = mPlayerListAdapter.getPlayer(position);
        mPlayerPosition = position;
        view.setSelected(true);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private boolean validateForm() {
        if(mSelectedPlayer == null) {
            showToast(getString(R.string.choose_player));
            return false;
        }
        if(TextUtils.isEmpty(_playerNumberEditText.getText()) || !TextUtils.isDigitsOnly
                (_playerNumberEditText.getText())) {
            showToast(getString(R.string.enter_correct_number));
            return false;
        }
        if(TextUtils.isEmpty(_macAddressTextView.getText())){
            showToast(getString(R.string.you_must_connect_with_device));
            return false;
        }
        return true;
    }


    // ----------------------------------------------------------------------------------------------------------------
    // BroadcastReceiver handles the scan result event fired by the MldpBluetoothService service
    private final BroadcastReceiver bleServiceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MldpBluetoothService.ACTION_BLE_SCAN_RESULT.equals(action)) {
                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText(context,R.string.found_device,Toast.LENGTH_SHORT).show();

                mDeviceList.add(device);
            }
        }
    };

    private Callback<List<Player>> callback = new Callback<List<Player>>() {
        @Override
        public void onResponse(Call<List<Player>> call, Response<List<Player>> response) {
            if(response.isSuccessful()) {
                updateUIList(response.body(), mPlayers, mPlayerListAdapter);
                _loadingPlayersProgressDialog.dismiss();
            }
            else {
                Dialog dialog = UtilMethods.createInvalidConnectWithApiDialog(PlayerListActivity.this,
                        call, callback, _loadingPlayersProgressDialog);
                dialog.show();
            }
        }

        @Override
        public void onFailure(Call<List<Player>> call, Throwable t) {
            Dialog dialog = UtilMethods.createInvalidConnectWithApiDialog(PlayerListActivity.this,
                    call, callback, _loadingPlayersProgressDialog);
            dialog.show();
        }
    };

    // ----------------------------------------------------------------------------------------------------------------
    // Callback for MldpBluetoothService service connection and disconnection
    private final ServiceConnection bleServiceConnection = new ServiceConnection() {		        //Create new ServiceConnection interface to handle service connection and disconnection
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {		        //Service MldpBluetoothService has connected
            MldpBluetoothService.LocalBinder binder = (MldpBluetoothService.LocalBinder) service;
            bleService = binder.getService();                                                       //Get a reference to the service
            //scanStart();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { 			                //Service disconnects - should never happen while activity is running
            bleService = null;								                                        //Service has no connection
        }
    };

    // ----------------------------------------------------------------------------------------------------------------
    // Runnable used by the scanStopHandler to stop the scan
    private Runnable stopScan = new Runnable() {
        @Override
        public void run() {
            scanStop();
            Log.i("Test","Action scan end");
            Intent newIntent = new Intent(PlayerListActivity.this, DeviceListActivity.class);
            Set<BluetoothDevice> set = new HashSet<>(mDeviceList);
            mDeviceList.clear();
            mDeviceList.addAll(set);
            newIntent.putParcelableArrayListExtra("devices", mDeviceList);

            startActivityForResult(newIntent, CONNECT_WITH_DEVICE_REQUEST);
        }
    };

    // ----------------------------------------------------------------------------------------------------------------
    // Stops a scan
    private void scanStop() {
        if (areScanning) {															                //See if still scanning
            bleService.scanStop();                                         							//Stop the scan in progress
            areScanning = false;						                							//Indicate that we are not scanning
            setProgressBarIndeterminateVisibility(false);                                           //Show circular progress bar
            invalidateOptionsMenu();                                                                //The options menu needs to be refreshed
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Starts a scan
    private void scanStart() {
        if (areScanning == false) {                                                                 //See if already scanning - possible if resuming after turning on Bluetooth
            if (bleService.isBluetoothRadioEnabled()) {                                             //See if the Bluetooth radio is on - may have been turned off
                areScanning = true;                                                                 //Indicate that we are scanning - used for menu context and to avoid starting scan twice
                bleService.scanStart();                                                             //Start scanning
                scanStopHandler.postDelayed(stopScan, SCAN_TIME);                                   //Create delayed runnable that will stop the scan when it runs after SCAN_TIME milliseconds
            } else {                                                                                //Radio needs to be enabled
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);         //Create an intent asking the user to grant permission to enable Bluetooth
                startActivityForResult(enableBtIntent,ENABLE_BT_REQUEST );                         //Fire the intent to start the activity that will return a result based on user input
             }
        }
    }
}
