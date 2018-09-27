package pl.smartplayer.smartplayerapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.utils.Player;

public class MainActivity extends AppCompatActivity {

    private SparseArray<Player> dummyPlayers;

    @BindView(R.id.players_list_view)
    ListView _playersListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dummyPlayers = new SparseArray<>();
        dummyPlayers.append(1, new Player(1, "Wojciech", "Szczesny",
                1, 26, 185, 73));
        dummyPlayers.append(2, new Player(2, "Robert", "Lewandowski",
                9, 30, 187, 93));

        final ArrayList<String> list = new ArrayList<>();

        for(int i = 0; i < dummyPlayers.size(); i++) {
            int key = dummyPlayers.keyAt(i);
            Player player = dummyPlayers.get(key);
            list.add(player.getNumber() + " " + player.getFirstname() + " " + player.getLastname());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                list);

        _playersListView.setAdapter(adapter);
    }
}
