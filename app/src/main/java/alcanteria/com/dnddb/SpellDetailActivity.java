/** Activity for displaying the full information of a particular spell. */

package alcanteria.com.dnddb;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpellDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = "Spell Detail Activity";

    /** Array to store the selected spell's details. */
    public String[][] SPELL;

    /** Tool to parse the JSON data. */
    DNDDB_JSON_Parser parser;

    /** Tool to connect to the internet. */
    DNDDB_NetworkConnector network;

    /** Text views that display the spell information. */
    TextView spellNameTextView;
    TextView spellLevelTextView;
    TextView spellSchoolTextView;
    TextView spellDescriptionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_detail);

        // Grab the bundle sent from the previous activity.
        Bundle spellInfo = getIntent().getExtras();

        // Extract the spell ID of item selected from the previous activity.
        final String SPELL_ID = spellInfo.getString("SpellID");

        // Initialize the parser and network tool.
        parser = new DNDDB_JSON_Parser();
        network = new DNDDB_NetworkConnector();

        // Link all of the views to their respective resources.
        spellNameTextView          = (TextView)findViewById(R.id.spell_detail_name);
        spellLevelTextView         = (TextView)findViewById(R.id.spell_detail_level);
        spellSchoolTextView        = (TextView)findViewById(R.id.spell_detail_school);
        spellDescriptionTextView   = (TextView)findViewById(R.id.spell_detail_description);

        // Create objects to poll network status.
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check to see if the network is available before you attempt to connect.
        if(info != null && info.isConnected()){

            new RetrieveOneSpell().execute(DNDDB_NetworkConnector.SPELL_BY_ID + SPELL_ID);
            Log.d(LOG_TAG, "Spell Retrieved.");
        }
        else{
            Toast.makeText(this, "No Network Connection.", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "No Network Connection.");
        }
    }

    /************************************************** Async Task to get the JSON results from the server FOR ALL SPELLS. */
    private class RetrieveOneSpell extends AsyncTask<String, Void, String[][]> {

        @Override
        /** This is the method called first in the async task chain. This connects to the URL
         * and retrieves the query results as JSON data. */
        protected String[][] doInBackground(String... url) {

            String result;
            String[][] spellDetail = null;
            try {
                // Connect to the web and store the query result in this string.
                result = network.GetJSONDataFromURL(url[0]);
                spellDetail = parser.ParseSpellForDetail(result);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Could not download JSON data.");
                e.printStackTrace();
            }

            return spellDetail;
        }

        @Override
        /** This method is the last called in the async task chain. It sets the text display values
         * for each of the text views in this activity. */
        protected void onPostExecute(String[][] result) {

            try {
                if (result != null) {

                   SPELL = result;

                    spellNameTextView.setText               (SPELL[0][DNDDB_JSON_Parser.SPELL_NAME_INDEX]);
                    spellLevelTextView.setText              (SPELL[0][DNDDB_JSON_Parser.SPELL_LEVEL_INDEX]);
                    spellSchoolTextView.setText             (SPELL[0][DNDDB_JSON_Parser.SPELL_SCHOOL_INDEX]);
                    spellDescriptionTextView.setText        (SPELL[0][DNDDB_JSON_Parser.SPELL_DESCRIPTION_INDEX]);
                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error parsing JSON data.");
            }
        }

        /************************************************************************************************************ */
    }
}
