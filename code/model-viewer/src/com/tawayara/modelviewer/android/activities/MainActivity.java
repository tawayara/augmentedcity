package com.tawayara.modelviewer.android.activities;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.tawayara.modelviewer.R;
import com.tawayara.modelviewer.android.adapter.ModelListAdapter;
import com.tawayara.modelviewer.android.adapter.data.ModelListItem;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
        ModelListAdapter adapter = new ModelListAdapter(this, this.createItemsList());

        final ListView listView = (ListView)findViewById(R.id.list_models);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                ModelListItem item = (ModelListItem) listView.getItemAtPosition(position);
            	Intent intent = new Intent(MainActivity.this, ViewActivity.class);
            	intent.putExtra(ViewActivity.EXTRA_MODEL_NAME, item.file);
            	MainActivity.this.startActivity(intent);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private List<ModelListItem> createItemsList() {
		List<ModelListItem> items = new ArrayList<ModelListItem>();
		items.add(createItem("Photo", "Photo", "the Hexplosives Expert", R.drawable.photo));
//		items.add(createItem("Edwin", "Edwin", "Marinho", R.drawable.edwin));
//		items.add(createItem("Haroldo", "Haroldo", "Gondim", R.drawable.haroldo));
//		items.add(createItem("Rafael", "Roballo", "Roballo", R.drawable.roballo));
		items.add(createItem("Android", "android", "Android model", R.drawable.ic_launcher));
		items.add(createItem("Amumu", "Amumu", "the Sad Mummy", R.drawable.amumu));
		items.add(createItem("Annie", "Annie", "the Dark Child", R.drawable.annie));
//		items.add(createItem("Fizz", "", "the Tidal Trickster", R.drawable.fizz));
		items.add(createItem("Heimerdinger", "Heimerdinger", "the Revered Inventor", R.drawable.heimerdinger));
		items.add(createItem("Kennen", "Kennen", "the Heart of the Tempest", R.drawable.kennen));
//		items.add(createItem("Kog'Maw", "", "the Mouth of the Abyss", R.drawable.kogmaw));
		items.add(createItem("Lulu", "Lulu", "the Fae Sorceress", R.drawable.lulu));
		items.add(createItem("Poppy", "Poppy", "the Iron Ambassador", R.drawable.poppy));
		items.add(createItem("Rammus", "Rammus", "the Armordillo", R.drawable.rammus));
		items.add(createItem("Teemo", "Teemo", "the Swift Scout", R.drawable.teemo));
		items.add(createItem("Tristana", "Tristana", "the Megling Gunner", R.drawable.tristana));
//		items.add(createItem("Veigar", "", "the Tiny Master of Evil", R.drawable.veigar));
		items.add(createItem("Ziggs", "Ziggs", "the Hexplosives Expert", R.drawable.ziggs));
		return items;
	}
	
	private ModelListItem createItem(String name, String file, String description, int iconResource) {
		ModelListItem item = new ModelListItem();
		item.iconResource = iconResource;
		item.name = name;
		item.description = description;
		item.file = file;
		return item;
	}

}
