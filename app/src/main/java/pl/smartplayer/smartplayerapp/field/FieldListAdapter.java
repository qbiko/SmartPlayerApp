package pl.smartplayer.smartplayerapp.field;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import pl.smartplayer.smartplayerapp.R;

public class FieldListAdapter extends BaseAdapter {

    private List<Field> fields;
    private Context context;

    FieldListAdapter(List<Field> fields, Context context) {
        this.fields = fields;
        this.context = context;
    }

    @Override
    public int getCount() {
        return fields.size();
    }

    @Override
    public Object getItem(int i) {
        return fields.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.field_list_layout, null);

        TextView fieldNameTextView = view.findViewById(R.id.field_name_text_view);

        fieldNameTextView.setText(fields.get(i).getName());

        return view;
    }

    public Field getField(int position) {
        return (Field)getItem(position);
    }
}

