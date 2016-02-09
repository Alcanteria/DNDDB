package alcanteria.com.dnddb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class SpellListActivity extends AppCompatActivity {

    public final String LOG_TAG = "Spell List Activity - ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_list);

        String[] spells = new String[]{"Fireball", "Magic Missile", "Lightning Bolt", "Cloud kill"};

        ArrayAdapter<String> spellsAdapter = new ArrayAdapter<String>(this, R.layout.spell_list_item, R.id.spellList_item_button, spells);

        final ListView spellList = (ListView)findViewById(R.id.spellList_listView);

        spellList.setAdapter(spellsAdapter);

        spellList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String text = (String)spellList.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, text);
            }
        });

        }
}
