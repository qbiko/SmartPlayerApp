package pl.smartplayer.smartplayerapp.field;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.main.Player;
import pl.smartplayer.smartplayerapp.main.PlayerListAdapter;

import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CHOOSE_FIELD_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CREATE_FIELD_REQUEST;

public class ChooseFieldActivity extends AppCompatActivity {

    private List<Field> fields = new ArrayList<>();
    FieldListAdapter fieldListAdapter;
    Field selectedField;

    @BindView(R.id.fields_list_view)
    ListView _fieldsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_field);
        ButterKnife.bind(this);

        fields.add(new Field(0, "CSA PG", new ArrayList<Location>()));
        fields.add(new Field(1, "Stadion PGE Narodowy", new ArrayList<Location>()));

        fieldListAdapter = new FieldListAdapter(fields,
                this.getApplicationContext());
        _fieldsListView.setAdapter(fieldListAdapter);
    }

    @OnItemClick(R.id.fields_list_view)
    public void onFieldSelected(int position, View view) {
        selectedField = fieldListAdapter.getField(position);

        view.setSelected(true);
    }

    @OnClick(R.id.confirm_button)
    public void onConfirmButtonClick() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("selectedField",selectedField);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @OnClick(R.id.create_field_button)
    public void onCreateFieldButtonClick() {
        Intent intent = new Intent(getApplicationContext(), CreateFieldActivity.class);
        startActivityForResult(intent, CREATE_FIELD_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if(requestCode == CREATE_FIELD_REQUEST) {
            if(resultCode == RESULT_OK) {
                Field createdField = resultIntent.getExtras().getParcelable("createdField");
                if(createdField != null)
                    fields.add(createdField);
                fieldListAdapter.notifyDataSetChanged();
            }
        }
    }
}
