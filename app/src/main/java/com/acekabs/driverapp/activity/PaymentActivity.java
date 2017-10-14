package com.acekabs.driverapp.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.acekabs.driverapp.ApplicationConstants;
import com.acekabs.driverapp.JSONParser;
import com.acekabs.driverapp.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.exception.AuthenticationException;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by frenzin05 on 10/6/2016.
 */
public class PaymentActivity extends AppCompatActivity {

    String strFullName;
    String strCardNo;
    String strExpiryDate;
    String strCVC;

    boolean fromMenu;
    boolean cardAvailable;
    boolean fromOrder;

//    Spinner etCardType;

    EditText etFullName;
    EditText etCardNo;
    EditText etExpiryDate;
    EditText etCVC;

    Button btnSave;

    ArrayList<String> listCardTypes = new ArrayList<>();
    ArrayList<String> listCardTypesLowerCase = new ArrayList<>();

    int MY_SCAN_REQUEST_CODE = 321;
    ProgressBar progressBar;

    JSONParser jsonParser=new JSONParser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        findviews();

        listCardTypes.add("Select card type");
        listCardTypes.add("American Express");
        listCardTypes.add("Diners Club");
        listCardTypes.add("Discover");
        listCardTypes.add("JCB");
        listCardTypes.add("MasterCard");
        listCardTypes.add("Visa");

        listCardTypesLowerCase.add("Select card type");
        listCardTypesLowerCase.add("americanexpress");
        listCardTypesLowerCase.add("dinersclub");
        listCardTypesLowerCase.add("discover");
        listCardTypesLowerCase.add("jcb");
        listCardTypesLowerCase.add("mastercard");
        listCardTypesLowerCase.add("visa");

//        ArrayAdapter arrayAdapter = new ArrayAdapter(PaymentActivity.this, android.R.layout.simple_spinner_item, listCardTypes);
//        etCardType.setAdapter(arrayAdapter);

        fromMenu = false;
        fromOrder = false;
        cardAvailable = false;

        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            fromMenu = intent.getBooleanExtra("fromMenu", false);
            cardAvailable = intent.getBooleanExtra("cardAvailable", false);
            fromOrder = intent.getBooleanExtra("fromOrder", false);
        }

        etExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*if (etExpiryDate.getText().toString().length() == 3){
                    String str = etExpiryDate.getText().toString();
                    Log.e("EDIT", "before etExpiryDate is " + str);
                    str = str + "/";
                    Log.e("EDIT", "after etExpiryDate is " + str);
                    etExpiryDate.setText(str);
                    etExpiryDate.setSelection(3);
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.e("EDIT", "length is " + etExpiryDate.getText().toString().length());
                if (etExpiryDate.getText().toString().length() == 2) {
                    String str = etExpiryDate.getText().toString();
                    Log.e("EDIT", "before etExpiryDate is " + str);
                    str = str + "/";
                    Log.e("EDIT", "after etExpiryDate is " + str);
                    etExpiryDate.setText(str);
                    etExpiryDate.setSelection(3);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCardNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*if (etExpiryDate.getText().toString().length() == 3){
                    String str = etExpiryDate.getText().toString();
                    Log.e("EDIT", "before etExpiryDate is " + str);
                    str = str + "/";
                    Log.e("EDIT", "after etExpiryDate is " + str);
                    etExpiryDate.setText(str);
                    etExpiryDate.setSelection(3);
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.e("EDIT", "length is " + etExpiryDate.getText().toString().length());
                String str;
                switch (etCardNo.getText().toString().length()) {
                    case 4:
                        str = etCardNo.getText().toString();
                        Log.e("EDIT", "before etExpiryDate is " + str);
                        str = str + "-";
                        Log.e("EDIT", "after etExpiryDate is " + str);
                        etCardNo.setText(str);
                        etCardNo.setSelection(5);
                        break;
                    case 9:
                        str = etCardNo.getText().toString();
                        Log.e("EDIT", "before etExpiryDate is " + str);
                        str = str + "-";
                        Log.e("EDIT", "after etExpiryDate is " + str);
                        etCardNo.setText(str);
                        etCardNo.setSelection(10);
                        break;
                    case 14:
                        str = etCardNo.getText().toString();
                        Log.e("EDIT", "before etExpiryDate is " + str);
                        str = str + "-";
                        Log.e("EDIT", "after etExpiryDate is " + str);
                        etCardNo.setText(str);
                        etCardNo.setSelection(15);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCardNo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    etCardNo.setText("");
                }
                return false;
            }
        });

        etExpiryDate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //You can identify which key pressed buy checking keyCode value with KeyEvent.KEYCODE_
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    //this is for backspace
                    etExpiryDate.setText("");
                }
                return false;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("Card", "btn clicked.");
                getValues();


                if (strFullName.equals("")) {
                    showToast("Please enter full name");
                    return;
                }

                if (strCardNo.equals("")) {
                    showToast("Please enter card number");
                    return;
                }

                if (strExpiryDate.equals("")) {
                    showToast("Please enter expiry date");
                    return;
                }

                if (strCVC.equals("")) {
                    showToast("Please enter CVC");
                    return;
                }

                getToken();
            }
        });

        /*etCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

    }

    void findviews() {
//        etCardType = (Spinner) findViewById(R.id.etCardType);
        etFullName = (EditText) findViewById(R.id.etFullName);
        etCardNo = (EditText) findViewById(R.id.etCardNo);
        etExpiryDate = (EditText) findViewById(R.id.etExpiryDate);
        etCVC = (EditText) findViewById(R.id.etCVC);

        btnSave = (Button) findViewById(R.id.btnSave);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void getValues() {
//        strCardType = listCardTypes.get(etCardType.getSelectedItemPosition());
        strFullName = etFullName.getText().toString();
        strCardNo = etCardNo.getText().toString();
        strExpiryDate = etExpiryDate.getText().toString();
        strCVC = etCVC.getText().toString();
    }

    private void getToken() {
        Log.e("Card", "getToken is called");
        String[] strings = strExpiryDate.split("/");
        int month;
        int year;

        try {
            month = Integer.parseInt(strings[0]);
            year = Integer.parseInt(strings[1]);
        } catch (Exception e) {
            month = -1;
            year = -1;
        }
        Log.e("Card", "getToken is called1");
        if (month == -1) {
            Toast.makeText(getApplicationContext(), "Please enter proper expiry date", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.e("Card", "month is " + month + " year is " + year);

        Card card = new Card(strCardNo, month, year, strCVC);
        card.validateNumber();
        card.validateCVC();

        if (!card.validateCard()) {
            Toast.makeText(getApplicationContext(), "Please enter proper card details", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("Card", "getToken is called2");
        Stripe stripe = new Stripe(this,"pk_test_bodTMYa7YBLWADdRAahAb3Wn");
        Log.e("Card", "getToken is called3");
        progressBar.setVisibility(View.VISIBLE);
        stripe.createToken(
                card,
                new TokenCallback() {
                    public void onSuccess(Token token) {
                        // Send token to your server
                        Log.e("Card", "token is " + token.getId());
                        addCard(token.getId());
                        /*Toast.makeText(PaymentActivity.this,
                                "Token is " + token.getId(),
                                Toast.LENGTH_LONG
                        ).show();*/

                        progressBar.setVisibility(View.GONE);

                        new PaymentAPI().execute(token.getId());
                    }

                    public void onError(Exception error) {
                        // Show localized error message
                        Toast.makeText(PaymentActivity.this,
                                error.toString(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );

    }
    private class PaymentAPI extends AsyncTask<String,String,String>
    {

        private JSONObject response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);

            try {
                JSONObject jsonObject=new JSONObject(s);
                if(jsonObject.getBoolean("status"))
                {
                    AlertDialog.Builder dialog=new AlertDialog.Builder(PaymentActivity.this);
                    dialog.setTitle("Payment Alert");
                    dialog.setMessage("Payment successful.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PaymentActivity.this.finish();
                        }
                    });
                    dialog.show();
                }
                else {

                    AlertDialog.Builder dialog=new AlertDialog.Builder(PaymentActivity.this);
                    dialog.setTitle("Payment Alert");
                    dialog.setMessage("Payment Failed.");
                    dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PaymentActivity.this.finish();
                        }
                    });
                    dialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("token",params[0]));
            response=jsonParser.makeHttpRequest(ApplicationConstants.PAYMENT_API,"POST",param);
            Log.e("Payment",response.toString());
            return response.toString();
        }


    }

    private void addCard(String token) {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("amount", 1000);
        params.put("currency", "usd");
        params.put("description", "Example charge");
        params.put("source", token);

        //Charge charge = Charge.create(params);
    }

    public class AddCard extends AsyncTask<String, Void, String> {
        String server_response;
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = new ProgressDialog(PaymentActivity.this);
            dialog.setMessage("Please wait....");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                int responseCode = urlConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    server_response = readStream(urlConnection.getInputStream());
                    Log.v("CatalogClient", server_response);
                    //response = new JSONArray(responseString);
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return server_response;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            if (dialog != null && dialog.isShowing())
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException e) {
                    Log.e("Exception", "error is " + e.toString());
                }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("url" + " Response : ", "" + server_response);
            if (dialog != null && dialog.isShowing())
                try {
                    dialog.dismiss();
                } catch (IllegalArgumentException e) {
                    Log.e("Exception", "error is " + e.toString());
                }
            if (server_response != null) {
                try {
                    JSONObject object = new JSONObject(server_response);

                    if (object.getString("status").equalsIgnoreCase("true")) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "" + e.toString());
                }
            }
        }
    }

    public static String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

}
