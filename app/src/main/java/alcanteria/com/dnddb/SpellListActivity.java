package alcanteria.com.dnddb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class SpellListActivity extends AppCompatActivity {

    public final String LOG_TAG = "Spell List Activity - ";

    /** URL for spell list ordered by name. */
    public final String ALL_SPELLS_BY_NAME = "http://dnddb.site88.net/AllSpellsByName.php";

    /** URL for spell list ordered by spell level. */
    public final String ALL_SPELLS_BY_LEVEL = "http://dnddb.site88.net/AllSpellsByLevel.php";

    /** URL for spell list ordered by spell school. */
    public final String ALL_SPELLS_BY_SCHOOL = "http://dnddb.site88.net/AllSpellsBySchool.php";

    /** JSON Parser object */
    public DNDDB_JSON_Parser parser;

    /** Array Adapter to use in the list view. */
    public ArrayAdapter<String> spellsAdapter;

    /** Array to store the spell id's of each item in the spell array adapter. */
    public String[] spellIDArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spell_list);

        // Create the JSON parser.
        parser = new DNDDB_JSON_Parser();

        // Create objects to poll network status.
        ConnectivityManager manager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        // Check to see if the network is available.
        if(info != null && info.isConnected()){
            Toast.makeText(this, "Network is connected.", Toast.LENGTH_SHORT).show();
            //Log.d(LOG_TAG, "Network is connected.");

            new JSONRetriever().execute(ALL_SPELLS_BY_NAME);
        }
        else{
            Toast.makeText(this, "No Network Connection.", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "No Network Connection.");
        }

        spellsAdapter = new ArrayAdapter<String>(this, R.layout.spell_list_item, R.id.spellList_item_textView, new ArrayList<String>());

        final ListView spellList = (ListView)findViewById(R.id.spellList_listView);

        spellList.setAdapter(spellsAdapter);

        /************************************************* ON ITEM CLICK */

        /** This handles the click events for each item in the spell list view. */
        spellList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String text = spellIDArray[position];
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG, text);
            }
        });

        }

    /** Async Task to get the JSON results from the server. */
    private class JSONRetriever extends AsyncTask<String, Void, String>{

        @Override
        /** This is the method called first in the async task chain. This connects to the URL
         * and retrieves the query results as JSON data. */
        protected String doInBackground(String... url){

            String result = "Error.";

            try{
                // Connect to the web and store the query result in this string.
                result = GetJSONDataFromURL(url[0]);
            }
            catch(Exception e){
                Log.d(LOG_TAG, "Could not download JSON data.");
                e.printStackTrace();
            }

            return result;
        }

        @Override
        /** This method is the last called in the async task chain. It converts the JSON string into
         * an array of readable text. */
        protected void onPostExecute(String result){

            try {
                if(result != null){

                    // Store the final, parsed results into an array.
                    String[] finalResults = parser.ParseSpellsForList(result);

                    // Separate the spell names from the above results and store them into a new array.
                    String[] spellNameArray = FormatSpellArray(finalResults);

                    // Clear out the old adapter and insert the new array data.
                    spellsAdapter.clear();
                    for(String spellInfo : spellNameArray)
                        spellsAdapter.add(spellInfo);
                }
            }
            catch(Exception e){
                Log.d(LOG_TAG, "Error parsing JSON data.");
            }
        }

        /** Establishes a connection to the passed URL and retrieves the JSON data. */
        private String GetJSONDataFromURL(String target) throws IOException {

            InputStream input;
            String inputAsString = "Poop.";

            // Attempt to connect to the internet to make a database query. Store the query result as a string.
            try{
                URL url = new URL(target);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(15000);
                connection.setConnectTimeout(20000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                // Starts the query.
                connection.connect();
                int response = connection.getResponseCode();
                Log.d(LOG_TAG, "Connection response = " + response);
                input = connection.getInputStream();

                // Convert the input stream into a string.
                inputAsString = ConvertToString(input);
            }
            catch(IOException e){
                Log.d(LOG_TAG, "Problem connecting to URL and/or getting JSON data.");
            }

            return inputAsString;
        }

        /** Takes an input stream and converts it into a single string. */
        public String ConvertToString(InputStream stream) throws IOException{

            // Set up the readers and builder to convert from an input stream into a string.
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
            String result;
            StringBuilder builder = new StringBuilder();

            String line;
            while((line = reader.readLine()) != null){
                builder.append(line + "\n");
            }
            result = builder.toString();

            try{
                if(stream != null)
                    stream.close();
            }
            catch(Exception e){
                Log.d(LOG_TAG, "Couldn't close input stream.");
            }

            return result;
        }

        /** Separates the spell ID and spell name from each entry in the array passed to this method. The array sent to this method
         * has the spellID and the spell name merged together. */
        public String[] FormatSpellArray(String[] array){

            // Set the size of the spell id array to match the passed array size.
            spellIDArray = new String[array.length];

            // Create a new array to store the names of the spells.
            String[] spellNames = new String[array.length];

            for(int i = 0; i < array.length; i++){
                spellIDArray[i] = array[i].substring(0, 9);
                spellNames[i] = array[i].substring(9);
            }

            return spellNames;
        }
    }
}
