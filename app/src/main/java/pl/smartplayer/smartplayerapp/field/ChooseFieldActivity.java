package pl.smartplayer.smartplayerapp.field;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.main.Player;
import pl.smartplayer.smartplayerapp.main.PlayerListAdapter;

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

        fields.add(new Field(0, "CSA PG", new double[4][2]));
        fields.add(new Field(1, "Stadion PGE Narodowy", new double[4][2]));

        fieldListAdapter = new FieldListAdapter(fields,
                this.getApplicationContext());
        _fieldsListView.setAdapter(fieldListAdapter);
    }

    @OnItemClick(R.id.fields_list_view)
    public void onFieldSelected(int position, View view) {
        selectedField = fieldListAdapter.getField(position);

        view.setSelected(true);
    }
}
