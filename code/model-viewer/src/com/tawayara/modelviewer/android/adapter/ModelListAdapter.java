package com.tawayara.modelviewer.android.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tawayara.modelviewer.R;
import com.tawayara.modelviewer.android.adapter.data.ModelListItem;

public class ModelListAdapter extends ArrayAdapter<ModelListItem> {

	public ModelListAdapter(Context context, List<ModelListItem> objects) {
		super(context, 0, objects);
	}

	@Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        // Verify if the convertView should be used or the layout should be inflated
		View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_model_list_item, null);
        }

        if (getItem(position).iconResource != 0) {
	        ImageView icon = (ImageView) view.findViewById(R.id.image_icon);
	        icon.setImageResource(getItem(position).iconResource);
        }

        TextView name = (TextView) view.findViewById(R.id.text_model_name);
        name.setText(getItem(position).name);

        TextView description = (TextView) view.findViewById(R.id.text_model_description);
        description.setText(getItem(position).description);

        return view;
    }
}
