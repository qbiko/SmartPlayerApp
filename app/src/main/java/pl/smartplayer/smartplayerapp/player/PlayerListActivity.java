package pl.smartplayer.smartplayerapp.player;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.api.ApiClient;
import pl.smartplayer.smartplayerapp.api.PlayerService;
import pl.smartplayer.smartplayerapp.main.PlayerOnGame;
import pl.smartplayer.smartplayerapp.utils.UtilMethods;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.smartplayer.smartplayerapp.main.MainActivity.sClubId;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CONNECT_WITH_DEVICE_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.ENABLE_BT_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.UtilMethods.updateUIList;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);
        ButterKnife.bind(this);

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

        IntentFilter filter = new IntentFilter();

        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(mReceiver, filter);

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
    }

    @OnClick(R.id.confirm_button)
    public void onConfirmButtonClick() {
        if(validateForm()){
            Intent returnIntent = new Intent();
            int number = Integer.parseInt(_playerNumberEditText.getText().toString());
            PlayerOnGame playerOnGameToReturn = new PlayerOnGame(number, mSelectedPlayer);
            returnIntent.putExtra("mSelectedPlayer", playerOnGameToReturn);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();
        }
    }

    @OnClick(R.id.connect_with_device_button)
    public void onConnectWithDeviceButtonClick() {
        mBluetoothAdapter.startDiscovery();
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

    @Override
    public void onDestroy() {
        unregisterReceiver(mReceiver);

        super.onDestroy();
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

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mDeviceList = new ArrayList<>();
                _scanningProgressDialog.show();
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                _scanningProgressDialog.dismiss();

                Intent newIntent = new Intent(PlayerListActivity.this, DeviceListActivity.class);
                newIntent.putParcelableArrayListExtra("devices", mDeviceList);

                startActivityForResult(newIntent, CONNECT_WITH_DEVICE_REQUEST);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mDeviceList.add(device);

                showToast(getString(R.string.found_device) + " " + device.getName());
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
}
