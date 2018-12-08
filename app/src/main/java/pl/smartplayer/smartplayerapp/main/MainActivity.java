package pl.smartplayer.smartplayerapp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.field.ChooseFieldActivity;
import pl.smartplayer.smartplayerapp.field.Field;
import pl.smartplayer.smartplayerapp.player.Player;
import pl.smartplayer.smartplayerapp.player.PlayerListActivity;

import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CHOOSE_FIELD_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CHOOSE_PLAYER_REQUEST;

public class MainActivity extends AppCompatActivity {

    private static final double FIELD_IMAGE_WIDTH_IN_PIXELS = 1061.0;
    private static final double FIELD_IMAGE_HEIGHT_IN_PIXELS = 701.0;
    private static final double FIELD_CENTER_IMAGE_WIDTH_IN_PIXELS = 345.0;
    private static final double FIELD_CENTER_IMAGE_HEIGHT_IN_PIXELS = 167.0;

    private SparseArray<Player> mDummyPlayers;
    private PlayerOnGameListAdapter mPlayerOnGameListAdapter;

    private Field mSelectedField = null;
    private PlayerOnGame mSelectedPlayer = null;
    List<PlayerOnGame> mDummyList = new ArrayList<>();

    @BindView(R.id.players_list_view)
    ListView _playersListView;
    @BindView(R.id.field_view)
    ImageView _fieldView;
    @BindView(R.id.main_container)
    LinearLayout _mainContainer;
    @BindView(R.id.start_stop_event_and_player_details_container)
    LinearLayout _startStopEventAndPlayerDetailsContainer;
    @BindView(R.id.start_stop_event_container)
    RelativeLayout _startStopEventContainer;
    @BindView(R.id.player_details_container)
    LinearLayout _playerDetailsContainer;
    @BindView(R.id.break_view)
    View _breakView;
    @BindView(R.id.right_panel)
    LinearLayout _rightPanel;
    @BindView(R.id.field_center_image_view)
    ImageView _fieldCenterImageView;
    @BindView(R.id.field_center_container)
    RelativeLayout _fieldCenterContainer;
    @BindView(R.id.choose_field_container)
    RelativeLayout _chooseFieldContainer;
    @BindView(R.id.space_between_choose_field_and_player_list)
    View _spaceBetweenChooseFieldAndPlayerList;
    @BindView(R.id.players_list_container)
    LinearLayout _playersListContainer;
    @BindView(R.id.add_player_button)
    ImageButton _addPlayerButton;
    @BindView(R.id.add_player_button_container)
    LinearLayout _addPlayerButtonContainer;
    @BindView(R.id.player_details_container_left_block)
    LinearLayout _playerDetailsContainerLeftBlock;
    @BindView(R.id.player_details_container_right_block)
    LinearLayout _playerDetailsContainerRightBlock;
    @BindView(R.id.player_name_text_view)
    TextView _playerNameTextView;
    @BindView(R.id.player_number_text_view)
    TextView _playerNumberTextView;
    @BindView(R.id.player_age_text_view)
    TextView _playerAgeTextView;
    @BindView(R.id.player_weight_text_view)
    TextView _playerWeightTextView;
    @BindView(R.id.player_height_text_view)
    TextView _playerHeightTextView;
    @BindView(R.id.field_name_text_view)
    TextView _fieldNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

/*        mDummyPlayers = new SparseArray<>();
        mDummyPlayers.append(1, new Player(1, "Wojciech", "Szczesny",
                1, 26, 185, 73));
        mDummyPlayers.append(2, new Player(2, "Robert", "Lewandowski",
                9, 30, 187, 93));

        final ArrayList<String> list = new ArrayList<>();

        for(int i = 0; i < mDummyPlayers.size(); i++) {
            int key = mDummyPlayers.keyAt(i);
            Player player = mDummyPlayers.get(key);
            list.add(player.getNumber() + " " + player.getFirstName() + " " + player.getLastName());
        }

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                list);

        _playersListView.setAdapter(adapter);*/

        mDummyList.add(new PlayerOnGame(1, new Player(1, "Wojciech", "Szczesny",
                26, 185, 73)));
        mDummyList.add(new PlayerOnGame(9, new Player(2, "Robert", "Lewandowski",
                30, 187, 93)));

        mPlayerOnGameListAdapter = new PlayerOnGameListAdapter(mDummyList,
                this.getApplicationContext());
        _playersListView.setAdapter(mPlayerOnGameListAdapter);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //set dynamically padding in main container
        int verticalPadding = screenHeight*2/100;
        _mainContainer.setPadding(0, verticalPadding, 0, verticalPadding);

        //set dynamically size of field ImageView
        double fieldViewHeightDouble = screenHeight/2;
        double compressPercentOfFieldImageSize = fieldViewHeightDouble/FIELD_IMAGE_HEIGHT_IN_PIXELS;
        int fieldViewHeight = (int)fieldViewHeightDouble;
        int fieldViewWidth = (int)(FIELD_IMAGE_WIDTH_IN_PIXELS*compressPercentOfFieldImageSize);

        _fieldView.getLayoutParams().width = fieldViewWidth;
        _fieldView.getLayoutParams().height = fieldViewHeight;

        //set dynamically width of left and right blocks under field ImageView
        _startStopEventAndPlayerDetailsContainer.getLayoutParams().width = fieldViewWidth;

        double containerMargin = fieldViewWidth*5/100;

        _breakView.getLayoutParams().width = (int)containerMargin;

        _startStopEventContainer.getLayoutParams().width = (int)(fieldViewWidth*3/8 -
                containerMargin);

        _playerDetailsContainer.getLayoutParams().width = fieldViewWidth -
                _startStopEventContainer.getLayoutParams().width;

        _playerDetailsContainerLeftBlock.getLayoutParams().width = _playerDetailsContainer
                .getLayoutParams().width/2;

        _playerDetailsContainerRightBlock.getLayoutParams().width =
                _playerDetailsContainerLeftBlock.getLayoutParams().width;

        //set dynamically size of field center ImageView
        double fieldCenterImageViewWidthDouble = screenWidth/5.0;
        double compressPercentOfFieldCenterImageSize =
                fieldCenterImageViewWidthDouble/FIELD_CENTER_IMAGE_WIDTH_IN_PIXELS;
        int fieldCenterViewWidth = (int)fieldCenterImageViewWidthDouble;
        int fieldCenterViewHeight = (int)
                (FIELD_CENTER_IMAGE_HEIGHT_IN_PIXELS*compressPercentOfFieldCenterImageSize);
        _rightPanel.getLayoutParams().width = fieldCenterViewWidth;
        _fieldCenterContainer.getLayoutParams().height = fieldCenterViewHeight;

        _chooseFieldContainer.getLayoutParams().height =
                (int)(fieldCenterViewHeight+fieldCenterViewHeight/1.2);

        //set dynamically size of players list container and contained in this other elements
        int mainContainerHeight = screenHeight-2*verticalPadding;

        int playerListContainerHeight = mainContainerHeight - _chooseFieldContainer
                .getLayoutParams().height - verticalPadding;

        _spaceBetweenChooseFieldAndPlayerList.getLayoutParams().height = verticalPadding;

        _playersListContainer.getLayoutParams().height = playerListContainerHeight;

        _playersListView.getLayoutParams().height = playerListContainerHeight*5/10;

        //add button size and padding
        _addPlayerButtonContainer.getLayoutParams().height = playerListContainerHeight*3/10;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if(requestCode == CHOOSE_FIELD_REQUEST) {
            if(resultCode == RESULT_OK) {
                mSelectedField = resultIntent.getExtras().getParcelable("mSelectedField");
                if(mSelectedField != null) {
                    _fieldNameTextView.setText(mSelectedField.getName());
                }
            }
        }
        if(requestCode == CHOOSE_PLAYER_REQUEST) {
            if(resultCode == RESULT_OK) {
                mSelectedPlayer = resultIntent.getExtras().getParcelable("mSelectedPlayer");
                if(mSelectedPlayer != null) {
                    mDummyList.add(mSelectedPlayer);
                    mPlayerOnGameListAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @OnClick(R.id.choose_field_button)
    public void onClickChooseFieldButton() {
        Intent intent = new Intent(getApplicationContext(), ChooseFieldActivity.class);
        startActivityForResult(intent, CHOOSE_FIELD_REQUEST);
    }

    @OnItemClick(R.id.players_list_view)
    public void onPlayerSelected(int position, View view) {
        PlayerOnGame playerOnGame = mPlayerOnGameListAdapter.getPlayerOnGame(position);

        _playerNameTextView.setText(playerOnGame.getPlayer().getFirstName()+" " +
                ""+playerOnGame.getPlayer().getLastName());
        _playerNumberTextView.setText(Integer.toString(playerOnGame.getNumber()));

        _playerAgeTextView.setText(Integer.toString(playerOnGame.getPlayer().getAge()));
        _playerHeightTextView.setText(Integer.toString(playerOnGame.getPlayer().getHeight()));
        _playerWeightTextView.setText(Integer.toString(playerOnGame.getPlayer().getWeight()));

        view.setSelected(true);
    }

    @OnClick(R.id.add_player_button)
    public void onClickAddPlayerButton() {
        Intent intent = new Intent(getApplicationContext(), PlayerListActivity.class);
        startActivityForResult(intent, CHOOSE_PLAYER_REQUEST);
    }
}
