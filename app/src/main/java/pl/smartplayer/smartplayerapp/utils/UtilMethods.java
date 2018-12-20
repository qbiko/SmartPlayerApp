package pl.smartplayer.smartplayerapp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.BaseAdapter;

import java.util.List;

import pl.smartplayer.smartplayerapp.R;
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
}
