package de.mide.langeberechnungmitfortschrittsanzeige;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Lange Berechnung in einem Hintergrund-Thread ausführen (mit Klasse {@link AsyncTask} )
 * mit einer Fortschrittsanzeige.
 * Es handelt sich um eine Modifikation der Beispiel-App <i>LangeBerechnung.</i>
 * <br><br>
 *
 * This project is licensed under the terms of the BSD 3-Clause License.
 */
public class MainActivity extends Activity implements OnClickListener {

    /** Tag für Log-Messages der ganzen App. */
    protected static final String TAG4LOGGING = "Fortschrittsanzeige";

    /** Widget für Eingabe der Zahl (Input-Parameter). */
    protected EditText _editTextInputParameter = null;

    /** Textview unten auf Activity zur Anzeige Ergebnis oder sonstige Dinge. */
    protected TextView _textViewAnzeige = null;

    /** Button um Berechnung zu starten. */
    protected Button _startButton = null;


    /**
     * Lifecycle-Method für Setup der UI.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.title_activity);

        _editTextInputParameter = findViewById( R.id.textEditFuerInputParameter );
        _textViewAnzeige        = findViewById( R.id.textViewZumAnzeigen        );
        _startButton            = findViewById( R.id.buttonBerechnungStarten    );

        _startButton.setOnClickListener(this);
    }


    /**
     * Einzige Methode aus Interface OnClickListener.
     * Event-Handler für alle drei Buttons!
     *
     * @parameter view  Button, der Event ausgelöst hat.
     */
    @Override
    public void onClick(View view) {

        // Zuerst wird überprüft, ob zulässige Zahl eingegeben
        String inputString = _editTextInputParameter.getText().toString();
        if (inputString == null || inputString.trim().length() == 0) {
            _textViewAnzeige.setText("Bitte Zahl in das Textfeld eingeben!");
            return;
        }

        keyboardEinklappen(_editTextInputParameter);

        int inputZahl = Integer.parseInt(inputString);
        Log.d(TAG4LOGGING,
                "Erwartetes Ergebnis für n=" + inputZahl + ": " + (int)Math.pow(inputZahl, 3));


        /* *** Eigentliche Berechnung durchführen *** */
        MeinAsyncTask mat = new MeinAsyncTask();
        mat.execute(inputZahl);
    }


    /**
     * Virtuelles Keyboard wieder "einklappen".
     * Lösung nach
     * <a href="https://stackoverflow.com/a/17789187/1364368">https://stackoverflow.com/a/17789187/1364368</a>
     *
     * @param view UI-Element, von dem Keyboard eingeblendet wurde.
     */
    public void keyboardEinklappen(View view) {

        InputMethodManager imm = null;

        imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /* **************************** */
    /* *** Start innere Klasse  *** */
    /* **************************** */

    /**
     * Generics Parameter:
     * <ol>
     *     <li>Integer: Eingabe-Parameter <i>n</i> für Methode <i>doInBackground()</i>,
     *                 wird als Parameter bei Aufruf der <i>execute()</i>-Methode am
     *                 <i>AsyncTask</i>-Objekt übergeben.</li><br>
     *
     *     <li>String: Als Fortschrittsanzeige darzustellender String, Input-Parameter
     *                 für die Methode <i>onProgressUpdate()</i>, wird durch Aufruf
     *                 von Methode <i>publishProgress()</i> in <i>doInBackground()</i>
     *                 übergeben.</li><br>
     *
     *     <li>String: Als Ergebnis darzustellende Nachricht; wird mit <i>return</i>
     *                 von der Methode <i>doInBackground()</i> zurückgegeben,
     *                 ist Input-Parameter für die Methode <i>onPostExecute()</i>.</li><br>
     * </ol>
     */
    public class MeinAsyncTask extends AsyncTask<Integer, String, String> {

        /**
         * Methode enthält Code mit langer Berechnung, der im Hintergrund-
         * Thread ausgeführt wird.
         *
         * @param params Varags, erste Komponente muss Zahl n sein, die
         *               potenziert werden soll.
         *
         * @return String mit Berechnungsergebnis, wird an Methode
         *         {@link MeinAsyncTask#onPostExecute(String)} übergeben.
         */
        @Override
        protected String doInBackground(Integer... params) {
            long    ergebnis               = 0;
            boolean mitFortschrittsanzeige = true;

            int inputZahl = params[0];

            if (inputZahl < 250) { mitFortschrittsanzeige = false; }

            long zeitpunktStart = System.nanoTime();

            if (mitFortschrittsanzeige)
                publishProgress("Berechnung für " + inputZahl + " gestartet ...");
            else
                publishProgress("Berechnung für " + inputZahl + " gestartet ..." +
                        "Keine Fortschritts-Anzeige" );

            for (int i1 = 1; i1 <= inputZahl; i1++) {
                for (int i2 = 1; i2 <= inputZahl; i2++) {
                    for (int i3 = 1; i3 <= inputZahl; i3++) {
                        ergebnis += 1;
                    }
                }

                if (mitFortschrittsanzeige  &&  i1 % 10 == 9) {
                    int prozentWert = (int) (i1 * 100.0 / inputZahl);
                    String fortschrittStr =
                            "Fortschritt: " + i1 + " von " + inputZahl +
                                    "\n(" + prozentWert + " %)";
                    publishProgress(fortschrittStr);
                }

            } // for i1


            long zeitpunktEnde  = System.nanoTime();
            final long laufzeitSekunden = (zeitpunktEnde - zeitpunktStart) /
                    ( 1000 * 1000 * 1000 );

            return  "Ergebnis: " + ergebnis +
                    "\nLaufzeit: " + laufzeitSekunden + " Sekunden";
        }

        /**
         * Aktualisiert die Fortschrittsanzeige; wird durch die
         * Methode {@link AsyncTask#publishProgress(Object[])} aufgerufen
         * (aber asynchron!).
         *
         * @param values  String mit aktuellem Fortschritt.
         */
        @Override
        protected void onProgressUpdate(String... values) {
            _textViewAnzeige.setText(values[0]);
        }

        /**
         * Methode wird nach Ende der Berechnung aufgerufen
         * (also wenn Methode {@link MeinAsyncTask#doInBackground(Integer...)}  }
         *  beendet ist) und stellt das Berechnungsergebnis auf der UI dar.
         *
         * @param ergebnisString  Berechnungsergebnis, wird auf UI dargestellt.
         */
        @Override
        protected void onPostExecute(String ergebnisString) {
            _textViewAnzeige.setText( ergebnisString );
            _startButton.setEnabled(true);
        }

    }

    /* **************************** */
    /* *** Ende innere Klassen  *** */
    /* **************************** */

};
