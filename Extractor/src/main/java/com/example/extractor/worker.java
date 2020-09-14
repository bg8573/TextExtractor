package com.example.extractor;


import android.content.Context;
import android.util.Log;


import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.dialogflow.v2.DetectIntentRequest;
import com.google.cloud.dialogflow.v2.DetectIntentResponse;
import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import com.google.common.collect.Lists;

import java.io.InputStream;
import java.util.UUID;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
//import rx.Observable;
//import rx.schedulers.Schedulers;

public class worker {
    private SessionsClient sessionsClient;
    private SessionName sessionName;
    private String uuid = UUID.randomUUID().toString();
    private QueryInput input;
    private InputStream credentials;
    private Context Ctx;
    final String[] reply = new String[1];
    private io.reactivex.rxjava3.schedulers.Schedulers Schedulers;

    public worker(Context ctx, InputStream credentials) {
        this.credentials = credentials;
        Ctx = ctx;

    }

    public void setUpBot() {
        try {
            InputStream stream = credentials;
            GoogleCredentials credentials = GoogleCredentials.fromStream(stream)
                    .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
            String projectId = ((ServiceAccountCredentials) credentials).getProjectId();

            SessionsSettings.Builder settingsBuilder = SessionsSettings.newBuilder();
            SessionsSettings sessionsSettings = settingsBuilder.setCredentialsProvider(
                    FixedCredentialsProvider.create(credentials)).build();
            sessionsClient = SessionsClient.create(sessionsSettings);
            sessionName = SessionName.of(projectId, uuid);
            Log.d("abc", "projectId : " + projectId);
        } catch (Exception e) {
            Log.d("abc", "setUpBot: " + e.getMessage());
        }
    }

    public void sendMessageToBot(String message) {
        input = QueryInput.newBuilder()
                .setText(TextInput.newBuilder().setText(message).setLanguageCode("en-US")).build();
        //new SendMessageInBg(this, sessionName, sessionsClient, input).execute();
        Observable.fromCallable(() -> {

            DetectIntentRequest detectIntentRequest =
                DetectIntentRequest.newBuilder()
                        .setSession(sessionName.toString())
                        .setQueryInput(input)
                        .build();
            return sessionsClient.detectIntent(detectIntentRequest);

        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    Log.i("aaa",result.getQueryResult().getFulfillmentText());
                     reply[0] = result.getQueryResult().getFulfillmentText();
                     getReply(reply[0]);
                });
    }
public void getReply(String reply){
}

    /*public void showText(String reply) {
        this.reply = reply;

    }*/

    //@Override
    /*public DetectIntentResponse doInBackground(Void... voids) {
        try {
            DetectIntentRequest detectIntentRequest =
                    DetectIntentRequest.newBuilder()
                            .setSession(sessionName.toString())
                            .setQueryInput(input)
                            .build();
            return sessionsClient.detectIntent(detectIntentRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(DetectIntentResponse response) {
        if (response != null) {
            String botReply = response.getQueryResult().getFulfillmentText();
        }
    }*/

}
