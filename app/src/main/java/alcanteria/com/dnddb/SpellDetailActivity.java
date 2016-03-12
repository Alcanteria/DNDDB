/** Activity for displaying the full information of a particular spell. */

package alcanteria.com.dnddb;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SpellDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = SpellDetailActivity.class.getSimpleName();

    /** Array to store the selected spell's details. */
    public String[][] SPELL;

    /** Tool to parse the JSON data. */
    DNDDB_JSON_Parser parser;

    /** Tool to connect to the internet. */
    DNDDB_NetworkConnector network;

    /** Text views that display the spell information. */
    TextView spellNameTextView;
    TextView spellLevelTextView;
    TextView spellCastTimeTextView;
    TextView spellRangeTextView;
    TextView spellComponentTextView;
    TextView spellDurationTextView;
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
        spellCastTimeTextView      = (TextView)findViewById(R.id.spell_detail_castTime);
        spellRangeTextView         = (TextView)findViewById(R.id.spell_detail_range);
        spellComponentTextView     = (TextView)findViewById(R.id.spell_detail_components);
        spellDurationTextView      = (TextView)findViewById(R.id.spell_detail_duration);
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

                    FormatSpellDetailDisplay(result);

                }
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error parsing JSON data.");
            }

            findViewById(R.id.spell_detail_progressBar).setVisibility(View.GONE);
        }

        /************************************************************************************************************ */
    }


    /** Check what information exists in the queried spell and format the display accordingly. */
    public void FormatSpellDetailDisplay(String[][] spell){

        SPELL = spell;

        spellNameTextView.setText               (SPELL[0][DNDDB_JSON_Parser.SPELL_NAME_INDEX]);

        /** Convert the spell level to a int so we can check if it is less than 1, meaning it's a cantrip. */
        int level = Integer.parseInt(SPELL[0][DNDDB_JSON_Parser.SPELL_LEVEL_INDEX]);

        /** Format the spell level view to say "School - Cantrip" if it's a level 0 spell. */
        if(level < 1){
            spellLevelTextView.setText(SPELL[0][DNDDB_JSON_Parser.SPELL_SCHOOL_INDEX] + " Cantrip");
        }
        else{
            /** Format the spell level view to say "Level * School Spell" if the spell is not level 0. */
            spellLevelTextView.setText("Level " + SPELL[0][DNDDB_JSON_Parser.SPELL_LEVEL_INDEX] + " " + SPELL[0][DNDDB_JSON_Parser.SPELL_SCHOOL_INDEX]);
        }

        /** Check if the spell is a ritual and add that extra info at the end of the level text view if it is. */
        if(!SPELL[0][DNDDB_JSON_Parser.SPELL_RITUAL_INDEX].isEmpty()){
            spellLevelTextView.setText(spellLevelTextView.getText() + " (Ritual)");
        }

        spellCastTimeTextView.setText("Cast Time: " + SPELL[0][DNDDB_JSON_Parser.SPELL_CAST_TIME_INDEX]);
        spellRangeTextView.setText("Range : " + SPELL[0][DNDDB_JSON_Parser.SPELL_RANGE_INDEX]);

        /** Check which component values are present in this spell's detail and set the text view accordingly. */
        if(!SPELL[0][DNDDB_JSON_Parser.SPELL_VOCAL_INDEX].isEmpty())
            spellComponentTextView.setText("V");
        if(!SPELL[0][DNDDB_JSON_Parser.SPELL_SOMATIC_INDEX].isEmpty())
            spellComponentTextView.setText(spellComponentTextView.getText() + ", S");
        if(!SPELL[0][DNDDB_JSON_Parser.SPELL_MATERIAL_INDEX].isEmpty())
            spellComponentTextView.setText(spellComponentTextView.getText() + " M (" + SPELL[0][DNDDB_JSON_Parser.SPELL_MATERIAL_INDEX] + ")");

        /** Check if the spell requires concentration and set the spell duration text view accordingly. */
        if(!SPELL[0][DNDDB_JSON_Parser.SPELL_CONCENTRATION_INDEX].isEmpty())
            spellDurationTextView.setText(Html.fromHtml("<b>Concentration</b>, " + SPELL[0][DNDDB_JSON_Parser.SPELL_DURATION_INDEX]));
        else
            spellDurationTextView.setText(SPELL[0][DNDDB_JSON_Parser.SPELL_DURATION_INDEX]);

        spellDescriptionTextView.setText(Html.fromHtml(SPELL[0][DNDDB_JSON_Parser.SPELL_DESCRIPTION_INDEX]));

    }
}
