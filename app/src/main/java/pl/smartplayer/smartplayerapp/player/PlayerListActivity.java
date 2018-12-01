package pl.smartplayer.smartplayerapp.player;

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
import pl.smartplayer.smartplayerapp.field.CreateFieldActivity;
import pl.smartplayer.smartplayerapp.field.Field;
import pl.smartplayer.smartplayerapp.field.FieldListAdapter;
import pl.smartplayer.smartplayerapp.main.PlayerOnGame;

import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CREATE_FIELD_REQUEST;

public class PlayerListActivity extends AppCompatActivity {

    private List<Player> mPlayers = new ArrayList<>();
    private PlayerListAdapter mPlayerListAdapter;
    private Player mSelectedPlayer;

    @BindView(R.id.players_list_view)
    ListView _playersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_players_list);
        ButterKnife.bind(this);

        mPlayers.add(new Player(1, "Wojciech", "Szczesny",
                26, 185, 73));
        mPlayers.add(new Player(2, "Robert", "Lewandowski",
                30, 187, 93));

        mPlayerListAdapter = new PlayerListAdapter(mPlayers,
                this.getApplicationContext());
        _playersListView.setAdapter(mPlayerListAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if(requestCode == CREATE_FIELD_REQUEST) {
            if(resultCode == RESULT_OK) {
                Player createdField = resultIntent.getExtras().getParcelable("createdField");
                if(createdField != null) {
                    mPlayers.add(createdField);
                }
                mPlayerListAdapter.notifyDataSetChanged();
            }
        }
    }

    @OnClick(R.id.confirm_button)
    public void onConfirmButtonClick() {
        Intent returnIntent = new Intent();
        PlayerOnGame playerOnGameToReturn = new PlayerOnGame(1, mSelectedPlayer);
        returnIntent.putExtra("mSelectedPlayer", playerOnGameToReturn);
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @OnItemClick(R.id.players_list_view)
    public void onFieldSelected(int position, View view) {
        mSelectedPlayer = mPlayerListAdapter.getPlayer(position);

        view.setSelected(true);
    }
}
