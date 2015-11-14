package com.gamicarts.mydict;

import android.app.Service;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import com.gamicarts.mydict.definer.Definer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private Button addTerm;
    private EditText term;
    private EditText definitions;

    private ArrayList<String> terms = new ArrayList<String>();

    private static boolean showedLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!showedLoading) setContentView(R.layout.loading_screen);

        new Setup().execute(null, null);
    }

    private class Setup extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!showedLoading) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            showedLoading = true;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setContentView(R.layout.activity_main);

                    AdView mAdView = (AdView) findViewById(R.id.adView);
                    AdRequest adRequest = new AdRequest.Builder().build();
                    mAdView.loadAd(adRequest);

                    addTerm = (Button) findViewById(R.id.button2);

                    term = (EditText) findViewById(R.id.editText);
                    definitions = (EditText) findViewById(R.id.editText2);

                    if (term.requestFocus()) { //Bringing up the keyboard.
                        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);

                        imm.showSoftInput(term, 0);
                    }

                    View.OnClickListener onClickListener = new View.OnClickListener() {
                        public void onClick(View v) {
                            if (!terms.contains(term.getText().toString())
                                    && term.getText().toString().length() > 0) {
                                new Define().execute(null, null);
                            }
                        }
                    };

                    addTerm.setOnClickListener(onClickListener);
                }
            });

            return null;
        }
    }

    private class Define extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addTerm.setText(R.string.grabbing_definition);
                    addTerm.setEnabled(false);
                }
            });

            terms.add(term.getText().toString());

            final String definition = Definer.getDefinition(term.getText().toString().trim());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (definitions.getText().toString().length() == 0) {
                        definitions.setText((term.getText().toString().trim() + ": " + definition));
                    } else {
                        definitions.setText(definitions.getText().toString() + "\n\n" + (term.getText().toString().trim() + ": " + definition));
                    }

                    addTerm.setText(R.string.add_word);
                    addTerm.setEnabled(true);
                }
            });

            return null;
        }
    }

}
