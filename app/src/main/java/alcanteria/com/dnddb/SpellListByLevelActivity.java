package alcanteria.com.dnddb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class SpellListByLevelActivity extends AppCompatActivity {

    public final String LOG_TAG = SpellListByLevelActivity.class.getSimpleName();

    /** Tool to connect to the internet. */
    public DNDDB_NetworkConnector network;

    /** An array of all of the Spell ID keys for each spell in the list. */
    public String[] spellIDArray;

    /** The number of distinct spell levels present in the spell table. */
    public String numberOfSpellLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_list_by_level);

        network = new DNDDB_NetworkConnector();

        // Create objects to poll network status.
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check to see if the network is available before attempting to connect.
        if(info != null && info.isConnected()){

            new RetrieveNumberOfDistinctSpellLevels().execute(DNDDB_NetworkConnector.NUMBER_OF_DISTINCT_LEVELS);
        }
        else{
            Toast.makeText(this, "No Network Connection.", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "No Network Connection.");
        }
    }

    /**************************************************** Async Task to get the JSON results from the server FOR ALL SPELLS. */
    private class RetrieveNumberOfDistinctSpellLevels extends AsyncTask<String, Void, Void> {

        @Override
        /** This is the method called first in the async task chain. This connects to the URL
         * and retrieves the query results as JSON data. */
        protected Void doInBackground(String... url) {

            String result;
            try {
                // Connect to the web and store the query result in this string.
                result = network.GetStringDataFromURL(url[0]);

                /** The above result also return a bunch of java script garbage we don't need, so this cuts off the first
                    character returned since that is the only thing we actually need.*/
                numberOfSpellLevels = result.substring(0,1);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Could not download string data.");
                e.printStackTrace();
            }

            return null;

        }

        @Override
        /** This method is the last called in the async task chain. It converts the JSON string into
         * an array of readable text. */
        protected void onPostExecute(Void v) {

            Toast.makeText(getApplicationContext(), numberOfSpellLevels, Toast.LENGTH_LONG).show();

            // Hide the loading icon
            findViewById(R.id.spell_list_by_level_progressBar).setVisibility(View.GONE);
        }
    }
}
