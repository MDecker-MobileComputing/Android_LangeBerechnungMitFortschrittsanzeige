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
import android.widget.ProgressBar;
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

    /** Element für prozentuale Fortschrittsanzeige (von 0% bis 100%). */
    protected ProgressBar _progressBar = null;


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
        _progressBar            = findViewById( R.id.fortschrittsanzeige        );

        _startButton.setOnClickListener( this );
    }


    /**
     * Event-Handler-Methode für den Button. Started die Berechnung (wenn Zahl eingegeben).
     * Bevor die Berechnung gestartet wird, wird aber noch das Keyboard eingeklappt
     * und die Fortschritts-Anzeige (ProgressBar) auf "0%" gesetzt.
     *
     * @parameter view  Button, der Event ausgelöst hat.
     */
    @Override
    public void onClick(View view) {

        if (view != _startButton) {

            _textViewAnzeige.setText("Interner Fehler.");
            Log.e(TAG4LOGGING, "Event-Handler-Methode wurde von unerwartetem Button-Objekt aufgerufen.");
            return;
        }

        // Zuerst wird überprüft, ob zulässige Zahl eingegeben
        String inputString = _editTextInputParameter.getText().toString();
        if (inputString == null || inputString.trim().length() == 0) {

            _textViewAnzeige.setText("Bitte Zahl in das Textfeld eingeben!");
            return;
        }

        keyboardEinklappen(_editTextInputParameter);
        _progressBar.setProgress(0);

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
     * <a href="https://stackoverflow.com/a/17789187/1364368">https://stackoverflow.com/a/17789187/1364368</a>.
     *
     * @param view  UI-Element, von dem das Keyboard eingeblendet wurde.
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
     * Eigenes Unterklasse der Android-spezifischen Klasse {@link AsyncTask}.
     * Die Berechnung wird nach Instanziierung dieser Klasse durch den
     * Aufruf der Methode <code>execute</code> gestartet.
     * <br><br>
     *
     * <b>Generics Parameter:</b>
     * <ol>
     *     <li><code>Integer</code>: 
     *                 Eingabe-Parameter <i>n</i> für Methode <i>doInBackground()</i>,
     *                 wird als Parameter bei Aufruf der <i>execute()</i>-Methode am
     *                 <i>AsyncTask</i>-Objekt übergeben.</li><br>
     *
     *     <li><code>Integer</code>: 
     *                  Als Fortschrittsanzeige darzustellender Prozent-Wert, wird durch
     *                  Aufruf der Methode <i>publishProgress()</i> "verschickt" und von
     *                  der Methode <i>onProgressUpdate</i> empfangen.
     *
     *     <li><code>String</code>: 
     *                 Als Ergebnis darzustellende Nachricht; wird mit <i>return</i>
     *                 von der Methode <i>doInBackground()</i> zurückgegeben,
     *                 ist Input-Parameter für die Methode <i>onPostExecute()</i>.</li>
     * </ol>
     */
    public class MeinAsyncTask extends AsyncTask<Integer, Integer, String> {

        /**
         * Methode enthält Code mit langer Berechnung, der im Hintergrund-
         * Thread (Worker-Thread) ausgeführt wird.
         *
         * @param params  Varags, erste Komponente muss Zahl <i>n</i> sein,
         *                die potenziert werden soll.
         *
         * @return  String mit Berechnungsergebnis, wird an Methode
         *          {@link MeinAsyncTask#onPostExecute(String)} übergeben.
         */
        @Override
        protected String doInBackground(Integer... params) {

            long    ergebnis               = 0;
            boolean mitFortschrittsanzeige = true;

            int inputZahl = params[0];

            if (inputZahl < 250) { mitFortschrittsanzeige = false; }

            long zeitpunktStart = System.nanoTime();

            for (int i1 = 1; i1 <= inputZahl; i1++) {

                for (int i2 = 1; i2 <= inputZahl; i2++) {

                    for (int i3 = 1; i3 <= inputZahl; i3++) {

                        ergebnis += 1;
                    }
                }

                if (mitFortschrittsanzeige &&  i1 % 10 == 9) {

                    int prozentWert = (int) (i1 * 100.0 / inputZahl);
                    publishProgress(prozentWert);
                }

            } // for i1


            long zeitpunktEnde  = System.nanoTime();
            final long laufzeitSekunden = (zeitpunktEnde - zeitpunktStart) /
                                          ( 1000 * 1000 * 1000 );

            return  "Ergebnis: "   + ergebnis +
                    "\nLaufzeit: " + laufzeitSekunden + " Sekunden";
        }


        /**
         * Aktualisiert die Fortschrittsanzeige (Text und ProessBar); wird durch
         * die Methode {@link AsyncTask#publishProgress(Object[])} aufgerufen
         * (aber asynchron!).
         *
         * @param values  Varags, erste Komponente ist int-Wert mit Fortschritt in Prozent.
         */
        @Override
        protected void onProgressUpdate(Integer...values) {

            int prozentwert = values[0];

            _progressBar.setProgress( prozentwert );
            _textViewAnzeige.setText( prozentwert + "%" );
        }


        /**
         * Methode wird nach Ende der Berechnung aufgerufen
         * (also wenn Methode {@link MeinAsyncTask#doInBackground(Integer...)}
         *  beendet ist) und stellt das Berechnungsergebnis auf der UI dar.
         *
         * @param ergebnisString  Berechnungsergebnis, wird auf UI dargestellt.
         */
        @Override
        protected void onPostExecute(String ergebnisString) {

            _textViewAnzeige.setText( ergebnisString );
            _startButton.setEnabled( true );
            _progressBar.setProgress( 100 ); // 100%
        }

    } // Ende von class MeinAsyncTask

    /* **************************** */
    /* *** Ende innere Klassen  *** */
    /* **************************** */

};
