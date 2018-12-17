package pl.smartplayer.smartplayerapp.field;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.api.ApiClient;
import pl.smartplayer.smartplayerapp.api.FieldService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.smartplayer.smartplayerapp.main.MainActivity.sClubId;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CREATE_FIELD_REQUEST;

public class ChooseFieldActivity extends AppCompatActivity {

    private List<Field> mFields = new ArrayList<>();
    private FieldListAdapter mFieldListAdapter;
    private Field mSelectedField;

    @BindView(R.id.fields_list_view)
    ListView _fieldsListView;

    private ProgressDialog _loadingFieldsProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_field);
        ButterKnife.bind(this);

        mFieldListAdapter = new FieldListAdapter(mFields,
                this.getApplicationContext());
        _fieldsListView.setAdapter(mFieldListAdapter);

        FieldService fieldService = ApiClient.getClient().create(FieldService.class);
        _loadingFieldsProgressDialog = new ProgressDialog(this);
        _loadingFieldsProgressDialog.setMessage(getString(R.string.loading_fields));
        _loadingFieldsProgressDialog.setCancelable(false);
        _loadingFieldsProgressDialog.show();

        Call<List<Field>> call = fieldService.getFieldsByClubId(sClubId);
        call.enqueue(new Callback<List<Field>>() {
            @Override
            public void onResponse(Call<List<Field>> call, Response<List<Field>> response) {
                updateFieldList(response.body());
                _loadingFieldsProgressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<List<Field>> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if(requestCode == CREATE_FIELD_REQUEST) {
            if(resultCode == RESULT_OK) {
                Field createdField = resultIntent.getExtras().getParcelable("createdField");
                if(createdField != null) {
                    mFields.add(createdField);
                }
                mFieldListAdapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.confirm_button)
    public void onConfirmButtonClick() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("mSelectedField", mSelectedField);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @OnClick(R.id.create_field_button)
    public void onCreateFieldButtonClick() {
        Intent intent = new Intent(getApplicationContext(), CreateFieldActivity.class);
        startActivityForResult(intent, CREATE_FIELD_REQUEST);
    }

    @OnItemClick(R.id.fields_list_view)
    public void onFieldSelected(int position, View view) {
        mSelectedField = mFieldListAdapter.getField(position);

        view.setSelected(true);
    }

    private void updateFieldList(List<Field> fieldList) {
        for (Field field : fieldList) {
            if(!mFields.contains(field)) {
                mFields.add(field);
            }
        }
        mFieldListAdapter.notifyDataSetChanged();
    }
}
