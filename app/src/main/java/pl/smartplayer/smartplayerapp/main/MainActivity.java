package pl.smartplayer.smartplayerapp.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.field.ChooseFieldActivity;
import pl.smartplayer.smartplayerapp.field.Field;
import pl.smartplayer.smartplayerapp.player.Player;
import pl.smartplayer.smartplayerapp.player.PlayerListActivity;
import pl.smartplayer.smartplayerapp.utils.PositionsCollector;

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
    public static List<PlayerOnGame> sDummyList = new ArrayList<>();

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

    private static Boolean sIsGameActive = false;

    public static Boolean isGameActive() {
        return sIsGameActive;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sDummyList.add(new PlayerOnGame(1, new Player(1, "Wojciech", "Szczesny",
                26, 185, 73)));
        sDummyList.add(new PlayerOnGame(9, new Player(2, "Robert", "Lewandowski",
                30, 187, 93)));

        mPlayerOnGameListAdapter = new PlayerOnGameListAdapter(sDummyList,
                this.getApplicationContext());
        _playersListView.setAdapter(mPlayerOnGameListAdapter);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        //set dynamically padding in main container
        int verticalPadding = screenHeight * 2 / 100;
        _mainContainer.setPadding(0, verticalPadding, 0, verticalPadding);

        //set dynamically size of field ImageView
        double fieldViewHeightDouble = screenHeight / 2;
        double compressPercentOfFieldImageSize = fieldViewHeightDouble / FIELD_IMAGE_HEIGHT_IN_PIXELS;
        int fieldViewHeight = (int) fieldViewHeightDouble;
        int fieldViewWidth = (int) (FIELD_IMAGE_WIDTH_IN_PIXELS * compressPercentOfFieldImageSize);

        _fieldView.getLayoutParams().width = fieldViewWidth;
        _fieldView.getLayoutParams().height = fieldViewHeight;

        //set dynamically width of left and right blocks under field ImageView
        _startStopEventAndPlayerDetailsContainer.getLayoutParams().width = fieldViewWidth;

        double containerMargin = fieldViewWidth * 5 / 100;

        _breakView.getLayoutParams().width = (int) containerMargin;

        _startStopEventContainer.getLayoutParams().width = (int) (fieldViewWidth * 3 / 8 -
                containerMargin);

        _playerDetailsContainer.getLayoutParams().width = fieldViewWidth -
                _startStopEventContainer.getLayoutParams().width;

        _playerDetailsContainerLeftBlock.getLayoutParams().width = _playerDetailsContainer
                .getLayoutParams().width / 2;

        _playerDetailsContainerRightBlock.getLayoutParams().width =
                _playerDetailsContainerLeftBlock.getLayoutParams().width;

        //set dynamically size of field center ImageView
        double fieldCenterImageViewWidthDouble = screenWidth / 5.0;
        double compressPercentOfFieldCenterImageSize =
                fieldCenterImageViewWidthDouble / FIELD_CENTER_IMAGE_WIDTH_IN_PIXELS;
        int fieldCenterViewWidth = (int) fieldCenterImageViewWidthDouble;
        int fieldCenterViewHeight = (int)
                (FIELD_CENTER_IMAGE_HEIGHT_IN_PIXELS * compressPercentOfFieldCenterImageSize);
        _rightPanel.getLayoutParams().width = fieldCenterViewWidth;
        _fieldCenterContainer.getLayoutParams().height = fieldCenterViewHeight;

        _chooseFieldContainer.getLayoutParams().height =
                (int) (fieldCenterViewHeight + fieldCenterViewHeight / 1.2);

        //set dynamically size of players list container and contained in this other elements
        int mainContainerHeight = screenHeight - 2 * verticalPadding;

        int playerListContainerHeight = mainContainerHeight - _chooseFieldContainer
                .getLayoutParams().height - verticalPadding;

        _spaceBetweenChooseFieldAndPlayerList.getLayoutParams().height = verticalPadding;

        _playersListContainer.getLayoutParams().height = playerListContainerHeight;

        _playersListView.getLayoutParams().height = playerListContainerHeight * 5 / 10;

        //add button size and padding
        _addPlayerButtonContainer.getLayoutParams().height = playerListContainerHeight * 3 / 10;

        //Point zawiera pozycję na boisku względem rogu. Ma wartości od 0 do 1000, automatycznie przetłumaczenie tego jest w repaint
        sDummyList.get(0).setPosition(50,500);
        sDummyList.get(1).setPosition(800,500);

        Thread thread = new Thread(new BTMock(getApplicationContext()));
        thread.start();

        RepaintTask repaintTask = new RepaintTask(this);
        repaintTask.execute();

        //Thread positionsCollector = new Thread(new PositionsCollector());
       // positionsCollector.run();
    }

    public void repaintImageView() {
        int size = 15;
        Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.field);
        Bitmap newBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint myRectPaint = new Paint();
        Paint textPaint = new Paint();
        //Create a new image bitmap and attach a brand new canvas to it
        Canvas tempCanvas = new Canvas(newBitmap);

        if (sIsGameActive) {

            myRectPaint.setColor(Color.RED);
            textPaint.setTextSize(size * 2.0f);
            textPaint.setColor(Color.WHITE);


            int maxHeight = _fieldView.getDrawable().getMinimumHeight();
            int maxWidth = _fieldView.getDrawable().getMinimumWidth();

            for (PlayerOnGame player : sDummyList) {

                int xPlayerPosition = (int) Math.round(player.getPosition().x / 1000.0 * maxWidth);
                int yPlayerPosition = (int) Math.round(player.getPosition().y / 1000.0 * maxHeight);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tempCanvas.drawOval(xPlayerPosition - size, yPlayerPosition - size, xPlayerPosition + size, yPlayerPosition + size, myRectPaint);
                } else {
                    tempCanvas.drawRect(xPlayerPosition - size, yPlayerPosition - size, xPlayerPosition + size, yPlayerPosition + size, myRectPaint);
                }

                String number = String.valueOf(player.getNumber());
                int xTextPosition = number.length() == 1 ? xPlayerPosition - size / 2 : xPlayerPosition - size; //Tak aby napis był mniejwięcej w centrum kształtu
                tempCanvas.drawText(number, xTextPosition, yPlayerPosition + size * 0.75f, textPaint);
            }
        }
        _fieldView.setImageDrawable(new BitmapDrawable(getResources(), newBitmap));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (requestCode == CHOOSE_FIELD_REQUEST) {
            if (resultCode == RESULT_OK) {
                mSelectedField = resultIntent.getExtras().getParcelable("mSelectedField");
                if (mSelectedField != null) {
                    _fieldNameTextView.setText(mSelectedField.getName());
                }
            }
        }
        if(requestCode == CHOOSE_PLAYER_REQUEST) {
            if(resultCode == RESULT_OK) {
                mSelectedPlayer = resultIntent.getExtras().getParcelable("mSelectedPlayer");
                if(mSelectedPlayer != null) {
                    sDummyList.add(mSelectedPlayer);
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

    @OnClick(R.id.start_stop_event_button)
    public void toggleGameStatus() {
        sIsGameActive = !sIsGameActive;
    }

    @OnClick(R.id.add_player_button)
    public void onClickAddPlayerButton() {
        Intent intent = new Intent(getApplicationContext(), PlayerListActivity.class);
        startActivityForResult(intent, CHOOSE_PLAYER_REQUEST);
    }
}
