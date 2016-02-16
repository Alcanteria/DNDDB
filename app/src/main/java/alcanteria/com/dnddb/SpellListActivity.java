package alcanteria.com.dnddb;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class SpellListActivity extends AppCompatActivity {

    public final String LOG_TAG = "Spell List Activity - ";

    /** Array Adapter to use in the list view. */
    public ArrayAdapter<String> spellsAdapter;

    /** An array of all of the Spell ID keys for each spell in the list. */
    public String[] spellIDArray;

    /** Tool to parse JSON data into a string. */
    public DNDDB_JSON_Parser parser;

    /** Tool to connect to the internet. */
    public DNDDB_NetworkConnector network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_list);

        // Adapter to use in the list view.
        spellsAdapter = new ArrayAdapter<String>(this, R.layout.spell_list_item, R.id.spellList_item_textView, new ArrayList<String>());

        // Initialize the json and network tools.
        parser = new DNDDB_JSON_Parser();
        network = new DNDDB_NetworkConnector();

        // Create objects to poll network status.
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check to see if the network is available before attempting to connect.
        if(info != null && info.isConnected()){

            new RetrieveAllSpells().execute(DNDDB_NetworkConnector.ALL_SPELLS_BY_NAME);
        }
        else{
            Toast.makeText(this, "No Network Connection.", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "No Network Connection.");
        }

        // Link the list view to its resource.
        final ListView spellList = (ListView)findViewById(R.id.spellList_listView);

        // Set the list view's adapter.
        spellList.setAdapter(spellsAdapter);

        /************************************************* ON ITEM CLICK */

        /** This handles the click events for each item in the spell list view. */
        spellList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Extract the spell ID for the selected spell.
                String text = spellIDArray[position];
                //Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, text);

                // Create an intent and pack in the spell ID for the selected spell.
                Intent intent = new Intent(SpellListActivity.this, SpellDetailActivity.class);
                intent.putExtra("SpellID", text);
                startActivity(intent);
            }
        });

    }

    /**************************************************** Async Task to get the JSON results from the server FOR ALL SPELLS. */
    private class RetrieveAllSpells extends AsyncTask<String, Void, String> {

        @Override
        /** This is the method called first in the async task chain. This connects to the URL
         * and retrieves the query results as JSON data. */
        protected String doInBackground(String... url) {

            String result = null;

            try {
                // Connect to the web and store the query result in this string.
                result = network.GetJSONDataFromURL(url[0]);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Could not download JSON data.");
                e.printStackTrace();
            }

            return result;
        }

        @Override
        /** This method is the last called in the async task chain. It converts the JSON string into
         * an array of readable text. */
        protected void onPostExecute(String result) {

            try {
                if (result != null) {

                    // Store the final, parsed results into an array.
                    String[] finalResults = parser.ParseSpellsForList(result);

                    // Separate the spell names from the above results and store them into a new array.
                    String[] spellNameArray = FormatSpellArray(finalResults);

                    // Clear out the old adapter and insert the new array data.
                    spellsAdapter.clear();

                    for (String spellInfo : spellNameArray)
                        spellsAdapter.add(spellInfo);
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error parsing JSON data.");
            }
        }
    }

    /****************************************************************************************************** */

    /**
     * Separates the spell ID and spell name from each entry in the array passed to this method. The array sent to this method
     * has the spellID and the spell name merged together.
     */
    public String[] FormatSpellArray(String[] array) {

        // Set the size of the spell id array to match the passed array size.
        spellIDArray = new String[array.length];

        // Create a new array to store the names of the spells.
        String[] spellNames = new String[array.length];

        for (int i = 0; i < array.length; i++) {
            spellIDArray[i] = array[i].substring(0, 9);
            spellNames[i] = array[i].substring(9);
        }

        return spellNames;
    }

}
