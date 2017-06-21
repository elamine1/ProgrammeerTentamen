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



public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextUsernameRegister;
    private EditText editTextPasswordRegister;

    private TextView txtLoginErrorMsg;
    private TextView textViewRegister;

    private Button btnRegister;

    private String FirstName;
    private String LastName;
    private String RegisterUserName;
    private String RegisterPassword;


    public final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //      WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        editTextFirstName = (EditText) findViewById(R.id.edittextfirstname);
        editTextLastName = (EditText) findViewById(R.id.edittextlastname);
        editTextUsernameRegister = (EditText) findViewById(R.id.edittextusernameregister);
        editTextPasswordRegister = (EditText) findViewById(R.id.edittextpasswordregister);

        txtLoginErrorMsg = (TextView) findViewById(R.id.txtLoginErrorMessage);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);

        ImageView logo = (ImageView) findViewById(R.id.imageView);
        logo.animate()
                .setDuration(900)
                .scaleX(1)
                .scaleY(1)
                .alpha(1);

        TextView textViewRegister =(TextView)findViewById(R.id.textViewRegister);
        textViewRegister.animate()
                .setStartDelay(870)
                .setDuration(500)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);


        EditText editTextFirstName2 = (EditText) findViewById(R.id.edittextfirstname);
        editTextFirstName2.animate()
                .setStartDelay(2100)
                .setDuration(950)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        EditText editTextLastName2 = (EditText) findViewById(R.id.edittextlastname);
        editTextLastName2.animate()
                .setStartDelay(2100)
                .setDuration(950)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        EditText editTextUsernameRegister2 = (EditText) findViewById(R.id.edittextusernameregister);
        editTextUsernameRegister2.animate()
                .setStartDelay(2100)
                .setDuration(950)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        EditText editTextPasswordRegister2 = (EditText) findViewById(R.id.edittextpasswordregister);
        editTextPasswordRegister2.animate()
                .setStartDelay(2100)
                .setDuration(950)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);

        btnRegister = (Button) findViewById(R.id.btnregister);
        btnRegister.animate()
                .setStartDelay(3300)
                .setDuration(600)
                .scaleY(1)
                .scaleX(1)
                .alpha(1);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirstName = editTextFirstName.getText().toString();
                LastName = editTextLastName.getText().toString();
                RegisterUserName = editTextUsernameRegister.getText().toString();
                RegisterPassword = editTextPasswordRegister.getText().toString();
                txtLoginErrorMsg.setText("");

                // TODO Checken of username en password niet leeg zijn
                // momenteel checken we nog niet

                handleRegister(FirstName, LastName, RegisterUserName, RegisterPassword );
            }
        });


    }



    private void handleRegister(String firstname, String lastname, String username, String password) {
        //
        // Maak een JSON object met username en password. Dit object sturen we mee
        // als request body (zoals je ook met Postman hebt gedaan)
        //
        String body = "{\"firstName\":\"" + firstname + "\", \"lastName\":\"" + lastname + "\" , \"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        Log.i(TAG, "handleRegister - body = " + body);

        try {
            JSONObject jsonBody = new JSONObject(body);
            JsonObjectRequest jsObjRequest = new JsonObjectRequest
                    (Request.Method.POST, Config.URL_REGISTER, jsonBody, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            // Succesvol response - dat betekent dat we een geldig token hebben.
                             txtLoginErrorMsg.setText("Response: " + response.toString());
                            displayMessage("Succesvol een account aangemaakt.");

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
                                Intent main = new Intent(getApplicationContext(), LoginActivity.class);
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

