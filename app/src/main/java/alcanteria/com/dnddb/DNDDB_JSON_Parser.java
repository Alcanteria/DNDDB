package alcanteria.com.dnddb;

import android.util.EventLogTags;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Parses JSON strings into JSON Objects
 */
public class DNDDB_JSON_Parser {

    public final String LOG_TAG = "DNDDB JSON Parser";

    /** Indices for spell attribute in the array. */
    public static final int SPELL_ID_INDEX              =   0;
    public static final int SPELL_LEVEL_INDEX           =   1;
    public static final int SPELL_NAME_INDEX            =   2;
    public static final int SPELL_RITUAL_INDEX          =   3;
    public static final int SPELL_CAST_TIME_INDEX       =   4;
    public static final int SPELL_RANGE_INDEX           =   5;
    public static final int SPELL_VOCAL_INDEX           =   6;
    public static final int SPELL_SOMATIC_INDEX         =   7;
    public static final int SPELL_MATERIAL_INDEX        =   8;
    public static final int SPELL_DURATION_INDEX        =   9;
    public static final int SPELL_CONCENTRATION_INDEX   =   10;
    public static final int SPELL_SCHOOL_INDEX          =   11;
    public static final int SPELL_DESCRIPTION_INDEX     =   12;

    // This is the total number of items a spell can have. The total of the above group of indices.
    public static final int NUMBER_OF_SPELL_ELEMENTS = 13;

    public DNDDB_JSON_Parser(){

    }

    /** Takes a raw string of JSON data and parses it into a String array. Only parses data relevant for the list view. */
    public String[] ParseSpellsForList(String jsonData) throws JSONException{

        /********************************* Tags for spell parsing */

        final String SPELL                =   "Spells";
        final String SPELL_ID             =   "Spell ID";
        final String SPELL_NAME           =   "Spell Name";

        // Create the JSON object to work with.
        JSONObject spellsJson = new JSONObject(jsonData);

        // Create the JSON array from the above object.
        JSONArray jsonArray = spellsJson.getJSONArray(SPELL);

        // Create a new string array to store each array object in the json array.
        String[] spellsArray = new String[jsonArray.length()];

        // Loop through the array to populate each index of the string array.
        for(int i = 0; i < jsonArray.length(); i++){

            // Pieces of information we want to put in the final output string for each spell
            String spellID, spellName;

            // Create a JSON object representing each spell.
            JSONObject spellObject = jsonArray.getJSONObject(i);

            // Extract the ID and name.
            spellID = spellObject.getString(SPELL_ID);
            spellName = spellObject.getString(SPELL_NAME);

            // Format the extracted spell info into the final result array.
            spellsArray[i] = spellID + spellName;
        }

        return spellsArray;


    }

    /** Takes a raw string of JSON data and parses it into a String array. Only parses data relevant for the detail view. */
    public String[][] ParseSpellForDetail(String jsonData) throws JSONException{

        /********************************* Tags for spell parsing */

        final String SPELL                =   "Spells";
        final String SPELL_ID             =   "Spell ID";
        final String SPELL_LEVEL          =   "Spell Level";
        final String SPELL_NAME           =   "Spell Name";
        final String RITUAL               =   "Ritual";
        final String CAST_TIME            =   "Cast Time";
        final String RANGE                =   "Range";
        final String COMPONENT_VOCAL      =   "Component Vocal";
        final String COMPONENT_SOMATIC    =   "Component Somatic";
        final String COMPONENT_MATERIAL   =   "Component Material";
        final String DURATION             =   "Duration";
        final String CONCENTRATION        =   "Concentration";
        final String SPELL_SCHOOL         =   "Spell School";
        final String SPELL_DESCRIPTION    =   "Description";

        // Create the JSON object to work with.
        JSONObject spellsJson = new JSONObject(jsonData);

        // Create the JSON array from the above object.
        JSONArray jsonArray = spellsJson.getJSONArray(SPELL);

        // Create a new string array to store each array object in the json array.
        String[][] spellsArray = new String[jsonArray.length()][NUMBER_OF_SPELL_ELEMENTS];

        // Loop through the array to populate each index of the string array.
        for(int i = 0; i < jsonArray.length(); i++){

            // Create a JSON object representing each spell.
            JSONObject spellObject = jsonArray.getJSONObject(i);

            // Extract the name.
            spellsArray[i][SPELL_ID_INDEX]              =   spellObject.getString(SPELL_ID);
            spellsArray[i][SPELL_LEVEL_INDEX]           =   spellObject.getString(SPELL_LEVEL);
            spellsArray[i][SPELL_NAME_INDEX]            =   spellObject.getString(SPELL_NAME);
            spellsArray[i][SPELL_RITUAL_INDEX]          =   spellObject.getString(RITUAL);
            spellsArray[i][SPELL_CAST_TIME_INDEX]       =   spellObject.getString(CAST_TIME);
            spellsArray[i][SPELL_RANGE_INDEX]           =   spellObject.getString(RANGE);
            spellsArray[i][SPELL_VOCAL_INDEX]           =   spellObject.getString(COMPONENT_VOCAL);
            spellsArray[i][SPELL_SOMATIC_INDEX]         =   spellObject.getString(COMPONENT_SOMATIC);
            spellsArray[i][SPELL_MATERIAL_INDEX]        =   spellObject.getString(COMPONENT_MATERIAL);
            spellsArray[i][SPELL_DURATION_INDEX]        =   spellObject.getString(DURATION);
            spellsArray[i][SPELL_CONCENTRATION_INDEX]   =   spellObject.getString(CONCENTRATION);
            spellsArray[i][SPELL_SCHOOL_INDEX]          =   spellObject.getString(SPELL_SCHOOL);
            spellsArray[i][SPELL_DESCRIPTION_INDEX]     =   spellObject.getString(SPELL_DESCRIPTION);

        }

        /*for(int i = 0; i < jsonArray.length(); i++){
            for(int j = 0; j < NUMBER_OF_SPELL_ELEMENTS; j++){
                Log.d(LOG_TAG, spellsArray[i][j]);
            }
        }*/

        return spellsArray;

    }

    /** Takes an input stream and converts it into a single string. */
    public String ConvertToString(InputStream stream) throws IOException {

        // Set up the readers and builder to convert from an input stream into a string.
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"), 8);
        String result;
        StringBuilder builder = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line + "\n");
        }
        result = builder.toString();

        try {
            if (stream != null)
                stream.close();
        } catch (Exception e) {
            Log.d(LOG_TAG, "Couldn't close input stream.");
        }

        return result;
    }
}
