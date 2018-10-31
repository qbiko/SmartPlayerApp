package pl.smartplayer.smartplayerapp.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.smartplayer.smartplayerapp.R;

public class PlayerListAdapter extends BaseAdapter {

    private List<Player> players;
    private Context context;

    PlayerListAdapter(List<Player> players, Context context) {
        this.players = players;
        this.context = context;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int i) {
        return players.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.player_list_layout, null);

        TextView numberTextView = view.findViewById(R.id.number_list_layout);
        TextView playerFirstNameTextView = view.findViewById(R.id.player_firstname_list_layout);
        TextView playerLastNameTextView = view.findViewById(R.id
                .player_lastname_list_layout);

        numberTextView.setText(Integer.toString(players.get(i).getNumber()));
        playerFirstNameTextView.setText(players.get(i).getFirstName());
        playerLastNameTextView.setText(players.get(i).getLastName());

        return view;
    }

    public Player getPlayer(int position) {
        return (Player)getItem(position);
    }
}
