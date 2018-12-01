package pl.smartplayer.smartplayerapp.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.smartplayer.smartplayerapp.R;

public class PlayerOnGameListAdapter extends BaseAdapter {

    private List<PlayerOnGame> playersOnGames;
    private Context context;

    PlayerOnGameListAdapter(List<PlayerOnGame> playersOnGame, Context context) {
        this.playersOnGames = playersOnGame;
        this.context = context;
    }

    @Override
    public int getCount() {
        return playersOnGames.size();
    }

    @Override
    public Object getItem(int i) {
        return playersOnGames.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.player_on_game_list_layout, null);

        TextView numberTextView = view.findViewById(R.id.number_list_layout);
        TextView playerFirstNameTextView = view.findViewById(R.id.player_firstname_list_layout);
        TextView playerLastNameTextView = view.findViewById(R.id
                .player_lastname_list_layout);

        numberTextView.setText(Integer.toString(playersOnGames.get(i).getNumber()));
        playerFirstNameTextView.setText(playersOnGames.get(i).getPlayer().getFirstName());
        playerLastNameTextView.setText(playersOnGames.get(i).getPlayer().getLastName());

        return view;
    }

    public PlayerOnGame getPlayerOnGame(int position) {
        return (PlayerOnGame)getItem(position);
    }
}
