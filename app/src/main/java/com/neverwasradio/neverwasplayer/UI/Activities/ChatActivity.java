package com.neverwasradio.neverwasplayer.UI.Activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.neverwasradio.neverwasplayer.Core.ConnectionHandler;
import com.neverwasradio.neverwasplayer.R;

import java.io.IOException;

public class ChatActivity extends Activity {

    ChatActivity thisActivity;

    EditText nameField;
    EditText textField;
    ImageButton sendButton;
    LinearLayout mainLayout;
    LinearLayout sentBanner;
    TextView sentGoBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        thisActivity=this;

        nameField = (EditText) findViewById(R.id.chatNameField);
        textField = (EditText) findViewById(R.id.chatTextField);
        sendButton = (ImageButton) findViewById(R.id.chatSendButton);
        mainLayout = (LinearLayout) findViewById(R.id.chatMainLayout);
        sentBanner = (LinearLayout) findViewById(R.id.chatSentBanner);
        sentGoBack = (TextView) findViewById(R.id.chatSentBack);


        initListeners();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.enter_from_left, R.anim.exit_on_right);
            return true;
        }
        return false;
    }

    private void initListeners(){

        this.textField.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i== EditorInfo.IME_ACTION_NEXT) {
                    //sendAction();
                    textView.clearFocus();
                }
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAction();
            }
        });

        sentGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void sendAction(){
        if(nameField.getText().toString().compareTo("")==0) {
            // insert name
            Toast.makeText(getApplicationContext(), "Inserisci un nome",
                    Toast.LENGTH_SHORT).show();
        }
        else if(textField.getText().toString().compareTo("")==0) {
            //insert text
            Toast.makeText(getApplicationContext(), "Inserisci un messaggio",
                    Toast.LENGTH_SHORT).show();
        }

        else if (!MainActivity.isNetworkConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Errore di rete",
                    Toast.LENGTH_SHORT).show();
        }

        else {
            SendChatMessage sendChatMessage = new SendChatMessage();
            String[] params = new String[2];
            params[0] = nameField.getText().toString();
            params[1] = textField.getText().toString();
            sendChatMessage.execute(params);

            mainLayout.setVisibility(View.INVISIBLE);
            sentBanner.setVisibility(View.VISIBLE);

            //Toast.makeText(getApplicationContext(), "Messaggio inviato!",
            //        Toast.LENGTH_LONG).show();

        }
    }


    private class SendChatMessage extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String url = "http://www.mpwebtest.altervista.org/nw-play/store-mex.php";

            String name = params[0];
            String text = params[1];

            name = ConnectionHandler.sanitizeUrlString(name);
            text = ConnectionHandler.sanitizeUrlString(text);

            url = url + "?name=" + name + "&text=" + text;

            try {
                ConnectionHandler.sendMessageToChat(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Executed";
        }
    }


}
