package pl.smartplayer.smartplayerapp.player;

import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.widget.ListView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.main.MldpBluetoothService;

public class DeviceListActivity extends Activity {

    @BindView(R.id.devices_list_view)
    ListView _devicesListView;

    private DeviceListAdapter mAdapter;
    private ArrayList<BluetoothDevice> mDeviceList;
    private String mCurrentAddressMac;
    private MldpBluetoothService bleService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);
        ButterKnife.bind(this);

        mDeviceList = getIntent().getExtras().getParcelableArrayList("devices");

        mAdapter = new DeviceListAdapter(this);

        mAdapter.setData(mDeviceList);
        mAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {
            @Override
            public void onPairButtonClick(int position) {
                BluetoothDevice device = mDeviceList.get(position);

                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    unpairDevice(device);
                } else {
                    showToast(getString(R.string.connecting));

                    pairDevice(device);
                }
            }
        });

        _devicesListView.setAdapter(mAdapter);

        registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));

        Intent bleServiceIntent = new Intent(this, MldpBluetoothService.class);	                    //Create Intent to bind to the MldpBluetoothService
        this.bindService(bleServiceIntent, bleServiceConnection, BIND_AUTO_CREATE);	                //Bind to the  service and use bleServiceConnection callbacks for service connect and disconnect

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mPairReceiver);

        unbindService(bleServiceConnection);
        super.onDestroy();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void pairDevice(BluetoothDevice device) {
        try {
            //mCurrentAddressMac = device.getAddress();
            bleService.connect(device.getAddress());
            Intent returnIntent = new Intent();
            returnIntent.putExtra("addressMac", device.getAddress());
            DeviceListActivity.this.setResult(Activity.RESULT_OK, returnIntent);
            finish();
            //Method method = device.getClass().getMethod("createBond", (Class[]) null);
           // method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE,
                        BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice
                        .EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    showToast(getString(R.string.connected));
                    Intent returnIntent = new Intent();
                    String addressMac = mCurrentAddressMac;
                    returnIntent.putExtra("addressMac", addressMac);
                    DeviceListActivity.this.setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                }

/*                else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    showToast("Unpaired");
                }*/

                mAdapter.notifyDataSetChanged();
            }
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

}
