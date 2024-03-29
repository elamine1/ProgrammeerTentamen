package avans.muuvi.presentation;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import avans.muuvi.service.Config;
import avans.muuvi.R;
import avans.muuvi.service.VolleyRequestQueue;

import static avans.muuvi.R.id.btnLogin;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername;
    private EditText editTextPassword;
    private TextView txtLoginErrorMsg;
    private TextView textViewLogin;
    private Button regbtn;
    private Button btnLogin;
    private String mUsername;
    private String mPassword;


    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //      WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        editTextUsername = (EditText) findViewById(R.id.edittextUsername);
        editTextPassword = (EditText) findViewById(R.id.edittextPassword);
        txtLoginErrorMsg = (TextView) findViewById(R.id.txtLoginErrorMessage);
        textViewLogin = (TextView) findViewById(R.id.textViewLogin);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        regbtn = (Button) findViewById(R.id.regbtn);

        ImageView logo = (ImageView) findViewById(R.id.imageView);
        logo.animate()
                .setDuration(900)
                .scaleX(1)
                .scaleY(1)
                .alpha(1);

        TextView textView = (TextView) findViewById(R.id.textViewLogin);
        textView.animate()
                .setStartDelay(880)
                .setDuration(600)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);


        EditText editTextUsername2 = (EditText) findViewById(R.id.edittextUsername);
        editTextUsername2.animate()
                .setStartDelay(1650)
                .setDuration(950)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        EditText editTextPassword2 = (EditText) findViewById(R.id.edittextPassword);
        editTextPassword2.animate()
                .setStartDelay(1650)
                .setDuration(950)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        Button btn = (Button) findViewById(R.id.btnLogin);
        btn.animate()
                .setStartDelay(3100)
                .setDuration(500)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        Button regbtn = (Button) findViewById(R.id.regbtn);
        regbtn.animate()
                .setStartDelay(3650)
                .setDuration(500)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername = editTextUsername.getText().toString();
                mPassword = editTextPassword.getText().toString();
                txtLoginErrorMsg.setText("");

                // TODO Checken of username en password niet leeg zijn
                // momenteel checken we nog niet

                handleLogin(mUsername, mPassword);
            }
        });

        regbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        /*
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.i(TAG, "Position " + position + " is geselecteerd");

            Film film = films.get(position);
            Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
            intent.putExtra(FILM_DATA, film);
            startActivity(intent);
        }
        */


    }

    private void handleLogin(String username, String password) {
        //
        // Maak een JSON object met username en password. Dit object sturen we mee
        // als request body (zoals je ook met Postman hebt gedaan)
        //
        String body = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        Log.i(TAG, "handleLogin - body = " + body);

        try {
            JSONObject jsonBody = new JSONObject(body);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, Config.URL_LOGIN, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // Succesvol response - dat betekent dat we een geldig token hebben.
                            txtLoginErrorMsg.setText("Response: " + response.toString());
                            displayMessage("Succesvol ingelogd!");

                            // We hebben nu het token. We kiezen er hier voor om
                            // het token in SharedPreferences op te slaan. Op die manier
                            // is het token tussen app-stop en -herstart beschikbaar -
                            // totdat het token expired.
                            try {
                                String token = response.getString("id_token");

                                Context context = getApplicationContext();
                                SharedPreferences sharedPref = context.getSharedPreferences(
                                        getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString(getString(R.string.saved_token), token);
                                editor.commit();

                                // Start the main activity, and close the login activity
                                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(main);
                                // Close the current activity
                                finish();

                            } catch (JSONException e) {
                                // e.printStackTrace();
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            handleErrorResponse(error);
                        }
                    });

            jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                    1500, // SOCKET_TIMEOUT_MS,
                    2, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            // Access the RequestQueue through your singleton class.
            VolleyRequestQueue.getInstance(this).addToRequestQueue(jsObjRequest);
        } catch (JSONException e) {
            txtLoginErrorMsg.setText(e.getMessage());
            // e.printStackTrace();
        }
        return;
    }

    /**
     * Handel Volley errors op de juiste manier af.
     *
     * @param error Volley error
     */
    public void handleErrorResponse(VolleyError error) {
        Log.e(TAG, "handleErrorResponse");

        if(error instanceof com.android.volley.AuthFailureError) {
            String json = null;
            NetworkResponse response = error.networkResponse;
            if (response != null && response.data != null) {
                json = new String(response.data);
                json = trimMessage(json, "error");
                if (json != null) {
                    json = "Error " + response.statusCode + ": " + json;
                    displayMessage(json);
                }
            } else {
                Log.e(TAG, "handleErrorResponse: kon geen networkResponse vinden.");
            }
        } else if(error instanceof com.android.volley.NoConnectionError) {
            Log.e(TAG, "handleErrorResponse: server was niet bereikbaar");
            txtLoginErrorMsg.setText(getString(R.string.error_server_offline));
        } else {
            Log.e(TAG, "handleErrorResponse: error = " + error);
        }
    }

    public String trimMessage(String json, String key){
        Log.i(TAG, "trimMessage: json = " + json);
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        return trimmedString;
    }

    // TODO Verplaats displayMessage naar een centrale 'utility class' voor gebruik in alle classes.
    public void displayMessage(String toastString){
        Toast.makeText(getApplicationContext(), toastString, Toast.LENGTH_LONG).show();
    }
}
