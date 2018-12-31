package pl.smartplayer.smartplayerapp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.BaseAdapter;

import java.util.List;

import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.main.MldpBluetoothService;
import retrofit2.Call;
import retrofit2.Callback;

public class UtilMethods {

    public static <T> Dialog createInvalidConnectWithApiDialog(Context context, final Call<T>
            call, final Callback<T> callback, final ProgressDialog progressDialog) {

        progressDialog.dismiss();

        AlertDialog.Builder builder = new AlertDialog.Builder
                (context);

        builder.setTitle(R.string.invalid_connect_with_api_information);
        builder.setPositiveButton(R.string.yes, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                progressDialog.show();
                call.clone().enqueue(callback);
            }
        });
        builder.setNegativeButton(R.string.no, new DialogInterface
                .OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing, close dialog
            }
        });
        return builder.create();
    }

    public static <T> void updateUIList(List<T> sourceList, List<T> targetList, BaseAdapter
            adapter) {
        targetList.clear();
        targetList.addAll(sourceList);
        adapter.notifyDataSetChanged();
    }

    // ----------------------------------------------------------------------------------------------------------------
    // BroadcastReceiver handles various events fired by the MldpBluetoothService service.
    public static final BroadcastReceiver bleServiceReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MldpBluetoothService.ACTION_BLE_DATA_RECEIVED.equals(action)) {
                String data = intent.getStringExtra(MldpBluetoothService.INTENT_EXTRA_SERVICE_DATA);
                if (data != null) {
                    Log.d("Odebrano!",data);
                    //TODO: operacje na odebranych data
                }
            }
        }
    };
}
