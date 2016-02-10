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
    public final String SPELLS_BY_NAME = "http://dnddb.site88.net/spellsByName.php";

    /** URL for spell list ordered by spell level. */
    public final String SPELLS_BY_LEVEL = "http://dnddb.site88.net/spellsByLevel.php";

    /** URL for spell list ordered by spell school. */
    public final String SPELLS_BY_SCHOOL = "http://dnddb.site88.net/spellsBySchool.php";

    /** JSON Parser object */
    public DNDDB_JSON_Parser parser;

    /** Array Adapter to use in the list view. */
    public ArrayAdapter<String> spellsAdapter;

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
            Log.d(LOG_TAG, "Network is connected.");

            new JSONRetriever().execute(SPELLS_BY_NAME);
        }
        else{
            Toast.makeText(this, "Access Denied.", Toast.LENGTH_SHORT).show();
            Log.d(LOG_TAG, "Access Denied.");
        }

        //String[] spells = new String[]{"Fireball", "Magic Missile", "Lightning Bolt", "Cloud kill"};

        spellsAdapter = new ArrayAdapter<String>(this, R.layout.spell_list_item, R.id.spellList_item_textView, new ArrayList<String>());

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

    /** Async Task to get the JSON results from the server. */
    private class JSONRetriever extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... url){

            String result = "Error.";

            try{
                result = GetJSONDataFromURL(url[0]);
            }
            catch(Exception e){
                Log.d(LOG_TAG, "Could not download JSON data.");
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result){

            try {
                if(result != null){
                    String[] finalResults = parser.ParseSpells(result);
                    spellsAdapter.clear();
                    for(String spellInfo : finalResults)
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

        public String ConvertToString(InputStream stream) throws IOException{
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
            String result;
            StringBuilder builder = new StringBuilder();

            String line = null;
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
    }
}
