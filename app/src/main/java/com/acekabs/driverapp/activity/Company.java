package com.acekabs.driverapp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.acekabs.driverapp.R;


public class Company extends Activity implements View.OnClickListener {

    EditText company_edit_name, company_edit_addr1, company_edit_addr2, company_edit_city;
    Button company_btn_confirm;
    String NAME = "", ADD1 = "", ADD2 = "", CITY = "";
    View company_header;
    ImageView back;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        company_header = findViewById(R.id.company_header);
        back = (ImageView) company_header.findViewById(R.id.back);
        title = (TextView) company_header.findViewById(R.id.title);
        title.setText("COMPANY INFORMATION");

        company_edit_name = (EditText) findViewById(R.id.company_edit_name);
        company_edit_addr1 = (EditText) findViewById(R.id.company_edit_addr1);
        company_edit_addr2 = (EditText) findViewById(R.id.company_edit_addr2);
        company_edit_city = (EditText) findViewById(R.id.company_edit_city);

        company_btn_confirm = (Button) findViewById(R.id.company_btn_confirm);
        company_btn_confirm.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.company_btn_confirm) {

            NAME = company_edit_name.getText().toString().trim();
            ADD1 = company_edit_addr1.getText().toString().trim();
            ADD2 = company_edit_addr2.getText().toString().trim();
            CITY = company_edit_city.getText().toString().trim();

            if (NAME.length() == 0 && ADD1.length() == 0 &&
                    ADD2.length() == 0 && CITY.length() == 0) {
                ShowAlert("Warning !!", "Please Enter All Fields");
            } else if (NAME.length() == 0) {
                ShowAlert("Warning !!", "Please Enter Company Name");
            } else if (ADD1.length() == 0) {
                ShowAlert("Warning !!", "Please Enter Address1");
            } else if (ADD2.length() == 0) {
                ShowAlert("Warning !!", "Please Enter Address2");
            } else if (CITY.length() == 0) {
                ShowAlert("Warning !!", "Please Enter City");
            } else {
                Intent li = new Intent(Company.this, Vehicle.class);
                li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(li);
            }


        } else if (v.getId() == R.id.back) {
            Intent li = new Intent(Company.this, OTPActivity.class);
            li.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(li);

        }

    }

    public void ShowAlert(String title, String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alert_dialog);
        dialog.show();

        TextView tit = (TextView) dialog.findViewById(R.id.dialog_title);
        tit.setText(title);
        TextView mes = (TextView) dialog.findViewById(R.id.dialog_message);
        mes.setText(message);
        Button ok = (Button) dialog.findViewById(R.id.dialog_submit);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
                dialog.cancel();

            }
        });

    }
}