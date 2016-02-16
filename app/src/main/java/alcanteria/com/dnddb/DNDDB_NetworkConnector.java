package alcanteria.com.dnddb;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/** Creates a network connection and retrieves any desired data.  */
public class DNDDB_NetworkConnector {

    public final String LOG_TAG = "DNDDB_NetworkConnector";

    /** URL for spell list ordered by name. */
    public static final String ALL_SPELLS_BY_NAME = "http://dnddb.site88.net/AllSpellsByName.php";

    /** URL for spell list ordered by spell level. */
    public static final String ALL_SPELLS_BY_LEVEL = "http://dnddb.site88.net/AllSpellsByLevel.php";

    /** URL for spell list ordered by spell school. */
    public static final String ALL_SPELLS_BY_SCHOOL = "http://dnddb.site88.net/AllSpellsBySchool.php";

    /** URL for finding a spell by its Spell ID. */
    public static final String SPELL_BY_ID = "http://dnddb.site88.net/SpellByID.php?spell=";

    /** Tool to parse JSON data into a string. */
    private DNDDB_JSON_Parser parser;

    public DNDDB_NetworkConnector(){

        parser = new DNDDB_JSON_Parser();

    }

    /** Establishes a connection to the passed URL and retrieves the JSON data. */
    public String GetJSONDataFromURL(String target) throws IOException {

        InputStream input;
        String inputAsString = "Poop.";

        // Attempt to connect to the internet to make a database query. Store the query result as a string.
        try {
            URL url = new URL(target);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
            inputAsString = parser.ConvertToString(input);
        } catch (IOException e) {
            Log.d(LOG_TAG, "Problem connecting to URL and/or getting JSON data.");
        }

        return inputAsString;
    }
}
