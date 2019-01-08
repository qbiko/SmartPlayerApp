package pl.smartplayer.smartplayerapp.player;

import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import pl.smartplayer.smartplayerapp.R;

public class DeviceListAdapter extends BaseAdapter{

    private Context context;
    private List<String> bluetoothDevices;
    private OnPairButtonClickListener mListener;

    DeviceListAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<String> data) {
        bluetoothDevices = data;
    }

    public void setListener(OnPairButtonClickListener listener) {
        mListener = listener;
    }

    public int getCount() {
        return bluetoothDevices.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.device_list_layout, null);

        TextView deviceName = view.findViewById(R.id.device_name_text_view);
        TextView deviceAddress = view.findViewById(R.id.device_address_text_view);
        Button connectButton = view.findViewById(R.id.connect_button);


        //BluetoothDevice device = bluetoothDevices.get(i);

        deviceName.setText("Pejs");
        deviceAddress.setText("00:1E:C0:68:C8:7A");
        /*connectButton.setText((device.getBondState() == BluetoothDevice.BOND_BONDED) ? "Unpair"
                : "Pair");*/
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onPairButtonClick(i);
                }
            }
        });

        return view;
    }

    public interface OnPairButtonClickListener {
        public abstract void onPairButtonClick(int position);
    }
}
