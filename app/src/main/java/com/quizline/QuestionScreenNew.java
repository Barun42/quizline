package com.quizline;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by decimal on 27/3/18.
 */

public class QuestionScreenNew extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.txt_option1) Button txtOption1;
    @BindView(R.id.txt_option2) Button txtOption2;
    @BindView(R.id.txt_option3) Button txtOption3;
    @BindView(R.id.txt_option4) Button txtOption4;
    @BindView(R.id.txt_question) TextView question;
    @BindView(R.id.btn_continue) Button btn_continue;
    private Context context;
    private String JsonResponse = "";

    public String TAG = QuestionScreen.class
            .getSimpleName();

    private ArrayList<Question> questions;
    private int questionNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_screen);
        context = QuestionScreenNew.this;
        ButterKnife.bind(this);
        setListeners();

        getQuestionData();

        btn_continue.setOnClickListener(this);

    }

    private void setListeners() {
        txtOption1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.get(questionNo).setAnswer(txtOption1.getText().toString());
                continueCall();
            }
        });
        txtOption2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    questions.get(questionNo).setAnswer(txtOption2.getText().toString());
                    continueCall();
            }
        });
        txtOption3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.get(questionNo).setAnswer(txtOption3.getText().toString());
                continueCall();

            }
        });
        txtOption4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                questions.get(questionNo).setAnswer(txtOption4.getText().toString());
                continueCall();

            }
        });
    }

    private void getQuestionData() {

        String url = "http://128.136.227.185:9002/GETOUESTIONS";
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject .put("login_id", "TEST01");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST,url,jsonObject, new Response.Listener<JSONObject>() {


            @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        pDialog.hide();
                        JsonResponse = response.toString();

                        setData(response);  //Parse and set data into view
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
                CommonClass.showToast(context,error.toString());
            }
        }) ;
// Adding request to request queue
        queue.add(sr);
    }

    private void setData(JSONObject response) {
        questions=new ArrayList<>();

        JSONArray jArray = response.optJSONArray("X_RESULT");
        for(int i=0;i<jArray.length();i++) {
            try {
                Question question = new Question(jArray.getJSONObject(i));
                questions.add(question);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        questionNo=0;
        setQuestion();

//        txtOption1.setOnClickListener(this);
//        txtOption2.setOnClickListener(this);
//        txtOption3.setOnClickListener(this);
//        txtOption4.setOnClickListener(this);

    }

    private void setQuestion(){
//        txtOption1.setChecked(false);
//        txtOption2.setChecked(false);
//        txtOption3.setChecked(false);
//        txtOption4.setChecked(false);
        question.setText(questions.get(questionNo).getQuestionNo()+". "+questions.get(questionNo).getQuestion());
        txtOption1.setText(questions.get(questionNo).getOption1());
        txtOption2.setText(questions.get(questionNo).getOption2());
        txtOption3.setText(questions.get(questionNo).getOption3());
        txtOption4.setText(questions.get(questionNo).getOption4());

    }


    private void submitCall() {

        String url = "http://128.136.227.185:9002/SUBMITOUESTIONS";
        JSONObject jsonObject = new JSONObject();
        JSONArray jArray = null;

        try {
            jArray = new JSONArray();

            int count=0;
            for (count=0;count<questions.size();count++) {
                JSONObject jObject = new JSONObject();
                jObject.put("login_id", SharedPreferences.getperferences(context, "LOGIN_ID"));
                jObject.put("question_no", questions.get(count).getQuestionNo());
                jObject.put("answer", questions.get(count).getAnswer());
                jArray.put(jObject);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonObject.put("x_questions",jArray);
            Log.d("FinalJson",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST,url,jsonObject, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());
                pDialog.hide();
                Intent intent = new Intent(context, ResultScreen.class);
                intent.putExtra("QuestionObj",JsonResponse);
                startActivity(intent);
                finish();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // hide the progress dialog
                pDialog.hide();
                CommonClass.showToast(context,error.toString());
            }
        }) ;
// Adding request to request queue
        queue.add(sr);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){

            case R.id.btn_continue:
                /*Intent intent = new Intent(context, QuestionScreen2.class);
                intent.putExtra("QuestionObj",JsonResponse);
                startActivity(intent);*/
                continueCall();
                break;
        }
    }

    private void continueCall(){
        questionNo++;
        if(questionNo>=questions.size()){
            submitCall();

        }else{
            setQuestion();
        }

    }

}

