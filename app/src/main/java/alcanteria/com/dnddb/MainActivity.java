package alcanteria.com.dnddb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public final String LOG_TAG = "Main Activity - ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Main Menu Buttons.
        Button spellListByNameButton = (Button)findViewById(R.id.main_spellList_button);
        Button spellListByLevelButton = (Button)findViewById(R.id.main_spellList_by_level_button);

        /**************************************** CLICK EVENTS */

            // Spell List By Name
            spellListByNameButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SpellListByNameActivity.class);
                    startActivity(intent);
                }
            });

            // Spell List By Level
            spellListByLevelButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(MainActivity.this, SpellListByLevelActivity.class);
                    startActivity((intent));
                }
            });
    }
}
