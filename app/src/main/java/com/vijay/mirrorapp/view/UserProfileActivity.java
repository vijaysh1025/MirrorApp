package com.vijay.mirrorapp.view;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.vijay.mirrorapp.R;
import com.vijay.mirrorapp.core.platform.BaseActivity;
import com.vijay.mirrorapp.datastore.entities.user.AuthResponse;
import com.vijay.mirrorapp.datastore.entities.user.UserProfile;
import com.vijay.mirrorapp.viewmodel.UserAccountViewModel;

import javax.inject.Inject;

/*
Activity to allow user to vie their profile.
 */
public class UserProfileActivity extends BaseActivity {

    private static final String TAG = UserProfileActivity.class.getSimpleName();
    @Inject
    UserAccountViewModel viewModel;

    @BindView(R.id.birthday_text)
    TextView birthDayText;
    @BindView(R.id.email_text)
    TextView emailText;
    @BindView(R.id.location_text)
    TextView locationText;

    @BindView(R.id.btn_refresh)
    Button refreshButton;

    CompositeDisposable compositeDisposable;

    private Unbinder unbinder;

    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Test");
        setContentView(R.layout.activity_user_profile);

        getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Test");

        bindUserAcountService(viewModel);

        extras = getIntent().getExtras();
        if(extras != null && extras.containsKey("Reload_Profile") && extras.getBoolean("Reload_Profile"))
        {
            viewModel.sendUserProfileRequest(viewModel.getAuthToken().getValue());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        setTitle("Test Two");
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindState();

    }

    private void bindState(){
        compositeDisposable = new CompositeDisposable();
        setTitle(viewModel.getUserProfile().getValue().name);
        viewModel.getUserProfile()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserProfile>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(UserProfile userProfile) {
                        Log.i(TAG, "User Profile : " + userProfile.name);
                        Log.i(TAG, "User Profile : " + userProfile.email);
                        if(userProfile.errorMessage!=null && !userProfile.errorMessage.isEmpty()) {
                            showErrorMessage(userProfile.errorMessage);
                            return;
                        }

                        birthDayText.setText(userProfile.birthdate);
                        emailText.setText(userProfile.email);
                        locationText.setText(userProfile.location);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void unbindState(){
        compositeDisposable.clear();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindUserAccountService(viewModel);
    }
}
