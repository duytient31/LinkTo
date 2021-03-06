package com.project.linkto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.project.linkto.bean.Userbd;
import com.project.linkto.database.Userrepo;
import com.project.linkto.singleton.DataHelper;

import io.fabric.sdk.android.Fabric;
import java.util.List;

public class MainActivity extends BaseActivity {

    public static final String COMPLETED_ONBOARDING_PREF_NAME ="true" ;
    private static final String TAG = "tag";
    public static FirebaseAuth mAuth;
    public FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();



        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                getSavedUser();
            }
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (DataHelper.isConnected()) {
         //   goToHome();
            goToMain();
        } else {
            goToSignIn();
        }


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        //  updateUI(currentUser);
    }

    private void getSavedUser() {
        DataHelper.getInstance().initDB(this);
        Userrepo uRepo = DataHelper.getInstance().getuRepo();
        if (uRepo != null) {
            List<Userbd> list = (List<Userbd>) uRepo.findAll();
            if (list != null && !list.isEmpty()) {
                Userbd userbd = list.get(0);
                if (userbd != null) {
                    DataHelper.getInstance().setConnected(true);
                    DataHelper.getInstance().setmUserbd(userbd);
                }
            }
        }
    }

    public void removeSavedUser() {

        FirebaseAuth.getInstance().signOut();
        DataHelper.getInstance().initDB(this);
        Userrepo uRepo = DataHelper.getInstance().getuRepo();
        uRepo.delete(DataHelper.getInstance().getmUserbd());
        DataHelper.getInstance().setConnected(false);
        DataHelper.getInstance().setmUserbd(null);
        DataHelper.getInstance().setmUser(null);
        // onBackPressed();

        goToSignIn();
    }

    @Override
    protected void onStop() {
        try {
            super.onStop();
        } catch (Exception e) {
            Log.w(TAG, "onStop()", e);
            super.onStop();
        }
        SharedPreferences.Editor sharedPreferencesEditor =
                PreferenceManager.getDefaultSharedPreferences(this).edit();
        sharedPreferencesEditor.putBoolean(
                COMPLETED_ONBOARDING_PREF_NAME, true);
        sharedPreferencesEditor.apply();
    }
}
