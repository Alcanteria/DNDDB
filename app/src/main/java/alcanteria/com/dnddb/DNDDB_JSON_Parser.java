package alcanteria.com.dnddb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Parses JSON strings into JSON Objects
 */
public class DNDDB_JSON_Parser {



    public DNDDB_JSON_Parser(){

    }

    /** Takes a raw string of JSON data and parses it into a String array. */
    public String[] ParseSpells(String jsonData) throws JSONException{

        /********************************* Tags for spell parsing */

        final String SPELL                =   "Spells";
        final String SPELL_ID             =   "Spell ID";
        final String SPELL_LEVEL          =   "Spell Level";
        final String SPELL_NAME           =   "Spell Name";
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
        String[] spellsArray = new String[jsonArray.length()];

        // Loop through the array to populate each index of the string array.
        for(int i = 0; i < jsonArray.length(); i++){

            // Pieces of information we want to put in the final output string for each spell
            String spellName;

            // Create a JSON object representing each spell.
            JSONObject spellObject = jsonArray.getJSONObject(i);

            // Extract the name.
            spellName = spellObject.getString(SPELL_NAME);

            // Format the extracted spell info into the final result array.
            spellsArray[i] = spellName;
        }

        return spellsArray;


    }
}
