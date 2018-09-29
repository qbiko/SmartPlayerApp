package pl.smartplayer.smartplayerapp.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.utils.Player;

public class MainActivity extends AppCompatActivity {

    private int FIELD_IMAGE_WIDTH_IN_PIXELS = 1061;
    private int FIELD_IMAGE_HEIGHT_IN_PIXELS = 701;

    private SparseArray<Player> dummyPlayers;

    @BindView(R.id.players_list_view)
    ListView _playersListView;
    @BindView(R.id.field_view)
    ImageView _fieldView;
    @BindView(R.id.main_container)
    LinearLayout _mainContainer;
    @BindView(R.id.start_stop_event_and_player_details_container)
    RelativeLayout _startStopEventAndPlayerDetailsContainer;
    @BindView(R.id.start_stop_event_container)
    RelativeLayout _startStopEventContainer;
    @BindView(R.id.player_details_container)
    LinearLayout _playerDetailsContainer;

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

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //set dynamically padding in main container
        int verticalPadding = screenHeight*2/100;
        _mainContainer.setPadding(0, verticalPadding, 0, verticalPadding);

        //set dynamically size of field ImageView
        double fieldViewHeightDouble = screenHeight/2;
        double compressPercent = fieldViewHeightDouble/FIELD_IMAGE_HEIGHT_IN_PIXELS;
        int fieldViewHeight = (int)fieldViewHeightDouble;
        int fieldViewWidth = (int)(FIELD_IMAGE_WIDTH_IN_PIXELS*compressPercent);

        _fieldView.getLayoutParams().width = fieldViewWidth;
        _fieldView.getLayoutParams().height = fieldViewHeight;

        //set dynamically width of left and right blocks under field ImageView
        _startStopEventAndPlayerDetailsContainer.getLayoutParams().width = fieldViewWidth;

        double containerMargin = fieldViewWidth/100;

        _startStopEventContainer.getLayoutParams().width = (int)(fieldViewWidth*3/8 -
                containerMargin);

        _playerDetailsContainer.getLayoutParams().width = fieldViewWidth -
                _startStopEventContainer.getLayoutParams().width;


    }
}
