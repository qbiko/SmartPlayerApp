package pl.smartplayer.smartplayerapp.main;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import pl.smartplayer.smartplayerapp.R;
import pl.smartplayer.smartplayerapp.api.ApiClient;
import pl.smartplayer.smartplayerapp.api.GameService;
import pl.smartplayer.smartplayerapp.api.TeamService;
import pl.smartplayer.smartplayerapp.field.ChooseFieldActivity;
import pl.smartplayer.smartplayerapp.field.Field;
import pl.smartplayer.smartplayerapp.player.PlayerListActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CHOOSE_FIELD_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.CodeRequests.CHOOSE_PLAYER_REQUEST;
import static pl.smartplayer.smartplayerapp.utils.UtilMethods.bleServiceReceiver;

public class MainActivity extends AppCompatActivity {

    private static final double FIELD_IMAGE_WIDTH_IN_PIXELS = 1061.0;
    private static final double FIELD_IMAGE_HEIGHT_IN_PIXELS = 701.0;
    private static final double FIELD_CENTER_IMAGE_WIDTH_IN_PIXELS = 345.0;
    private static final double FIELD_CENTER_IMAGE_HEIGHT_IN_PIXELS = 167.0;

    public static Map<String, Point> sActivePlayers = new HashMap<>();
    private PlayerOnGameListAdapter mPlayerOnGameListAdapter;

    private PlayerOnGame mSelectedPlayer = null;
    private MldpBluetoothService bleService;
    public static Field sSelectedField = null;
    public static List<PlayerOnGame> mPlayersOnGameList = new ArrayList<>();
    public static int sClubId = 1;
    public static final int sTeamId = 1;
    public static int sGameId = 0;
    public static String sTeamName = "Moja druzyna";


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
    @BindView(R.id.player_distance_text_view)
    TextView _playerDistanceTextView;

    private static Boolean sIsGameActive = false;

    public static Boolean isGameActive() {
        return sIsGameActive;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        sClubId = getIntent().getIntExtra("sClubId", 1);

        registerReceiver(bleServiceReceiver, new IntentFilter() {{
            addAction(MldpBluetoothService.ACTION_BLE_DATA_RECEIVED);
        }});
        getSupportActionBar().hide();
        mPlayerOnGameListAdapter = new PlayerOnGameListAdapter(mPlayersOnGameList,
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
        Intent bleServiceIntent = new Intent(this, MldpBluetoothService.class);                        //Create Intent to bind to the MldpBluetoothService
        this.bindService(bleServiceIntent, bleServiceConnection, BIND_AUTO_CREATE);                    //Bind to the  service and use bleServiceConnection callbacks for service connect and disconnect

        TeamService teamService = ApiClient.getClient().create(TeamService.class);
        Callback<List<Team>> teamCallback = new Callback<List<Team>>() {
            @Override
            public void onResponse(Call<List<Team>> call, Response<List<Team>> response) {
                if (response.isSuccessful()) {
                    if(!response.body().isEmpty()) {
                        sTeamName = response.body().get(0).getTeamName();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.retrieve_team_name_attempt_finish_failed,
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.retrieve_team_name_attempt_finish_failed,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Team>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), R.string.retrieve_team_name_attempt_finish_failed,
                        Toast.LENGTH_SHORT).show();
            }
        };
        Call<List<Team>> call = teamService.getTeamsByClubId(sClubId);
        call.enqueue(teamCallback);


        RepaintTask repaintTask = new RepaintTask(MainActivity.this);
        repaintTask.execute();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(sSelectedField != null){
            _fieldNameTextView.setText(sSelectedField.getName());
        }
        if(mSelectedPlayer != null){
            _playerNameTextView.setText(mSelectedPlayer.getPlayer().getFirstName() + " " +
                    "" + mSelectedPlayer.getPlayer().getLastName());
            _playerNumberTextView.setText(Integer.toString(mSelectedPlayer.getNumber()));

            _playerAgeTextView.setText(Integer.toString(mSelectedPlayer.getPlayer().getAge()));
            _playerHeightTextView.setText(Integer.toString(mSelectedPlayer.getPlayer().getHeight()));
            _playerWeightTextView.setText(Integer.toString(mSelectedPlayer.getPlayer().getWeight()));
            _playerDistanceTextView.setText(String.format("%.3f",mSelectedPlayer.getDistance()));
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(bleServiceConnection);
    }

    public void repaintImageView() {
        Log.i("Repainting","ImageView");
        int size = 30;
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

            for (PlayerOnGame player : mPlayersOnGameList) {

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

                Log.i("Repainting","Player:" + player.getNumber() + " X: " + player.getPosition().x + " Y: " + player.getPosition().y);
            }
            if(mSelectedPlayer!= null){
                _playerDistanceTextView.setText(String.valueOf(mSelectedPlayer.getDistance()));
            }
        }
        _fieldView.setImageDrawable(new BitmapDrawable(getResources(), newBitmap));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        if (requestCode == CHOOSE_FIELD_REQUEST) {
            if (resultCode == RESULT_OK) {
                sSelectedField = resultIntent.getExtras().getParcelable("sSelectedField");
                if (sSelectedField != null) {
                    _fieldNameTextView.setText(sSelectedField.getName());
                }
            }
        }
        if (requestCode == CHOOSE_PLAYER_REQUEST) {
            if (resultCode == RESULT_OK) {
                mSelectedPlayer = resultIntent.getExtras().getParcelable("mSelectedPlayer");
                if (mSelectedPlayer != null) {
                    mPlayersOnGameList.add(mSelectedPlayer);
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

        _playerNameTextView.setText(playerOnGame.getPlayer().getFirstName() + " " +
                "" + playerOnGame.getPlayer().getLastName());
        _playerNumberTextView.setText(Integer.toString(playerOnGame.getNumber()));

        _playerAgeTextView.setText(Integer.toString(playerOnGame.getPlayer().getAge()));
        _playerHeightTextView.setText(Integer.toString(playerOnGame.getPlayer().getHeight()));
        _playerWeightTextView.setText(Integer.toString(playerOnGame.getPlayer().getWeight()));

        view.setSelected(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @OnClick(R.id.start_stop_event_button)
    public void startGame() {
        if (!sIsGameActive) {
            if (sSelectedField == null) {
                Toast.makeText(getApplicationContext(), R.string.select_field_before_game_start, Toast.LENGTH_LONG).show();
                return;
            }
            if (mPlayersOnGameList.isEmpty()) {
                Toast.makeText(getApplicationContext(), R.string.add_player_before_game_start, Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.enter_opponent_name_dialog_title);

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT );
            builder.setView(input);

            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String opponent = input.getText().toString();

                    if(opponent.isEmpty()) {
                        Toast.makeText(getApplicationContext(), R.string.given_team_name_is_empty, Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }

                    JSONObject object = new JSONObject();
                    object.put("host",sTeamName);
                    object.put("opponent",opponent);
                    object.put("teamId",sTeamId);
                    object.put("fieldId",sSelectedField.getDbId());

                    GameService gameService = ApiClient.getClient().create(GameService.class);
                    Call<JSONObject> call = gameService.createNewGame(object);
                    call.enqueue(callback);

                    sIsGameActive = true;
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
    }

    @OnClick(R.id.add_player_button)
    public void onClickAddPlayerButton() {
        Intent intent = new Intent(getApplicationContext(), PlayerListActivity.class);
        startActivityForResult(intent, CHOOSE_PLAYER_REQUEST);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Callback for MldpBluetoothService service connection and disconnection
    private final ServiceConnection bleServiceConnection = new ServiceConnection() {                //Create new ServiceConnection interface to handle service connection and disconnection
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {                //Service MldpBluetoothService has connected
            MldpBluetoothService.LocalBinder binder = (MldpBluetoothService.LocalBinder) service;
            bleService = binder.getService();                                                       //Get a reference to the service
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {                            //Service disconnects - should never happen while activity is running
            bleService = null;                                                                        //Service has no connection
        }
    };

    private Callback<JSONObject> callback = new Callback<JSONObject>() {
        @Override
        public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {
            sGameId = (int) Double.parseDouble(response.body().get("id").toString());
        }

        @Override
        public void onFailure(Call<JSONObject> call, Throwable t) {

        }
    };
}
