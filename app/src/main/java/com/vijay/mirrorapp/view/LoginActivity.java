package com.vijay.mirrorapp.view;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vijay.mirrorapp.R;
import com.vijay.mirrorapp.core.platform.BaseActivity;
import com.vijay.mirrorapp.datastore.UserDataRepository;
import com.vijay.mirrorapp.entities.user.AuthResponse;
import com.vijay.mirrorapp.entities.user.UserProfile;
import com.vijay.mirrorapp.services.UserAccountService;
import com.vijay.mirrorapp.viewmodel.UserAccountViewModel;

import javax.inject.Inject;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_SIGNUP = 0;
    @Inject
    UserAccountViewModel viewModel;

    @Inject
    UserDataRepository userDataRepository;

    @BindView(R.id.input_email)
    EditText inputEmail;

    @BindView(R.id.input_password)
    EditText inputPassword;

    @BindView(R.id.btn_login)
    Button loginButton;

    @BindView(R.id.link_signup)
    TextView linkSignup;

    CompositeDisposable compositeDisposable;

    ProgressDialog progressDialog;


    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getAppComponent().inject(this);
        unbinder = ButterKnife.bind(this);

        //Start the service
        startUserAcountService();
        setUpView();


        Log.i(TAG, "Activity created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindState();
    }

    private void setUpView(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isServiceRunning = isServiceRunning(UserAccountService.class);
                Log.i(TAG, "Is service Running : " + isServiceRunning);
                login();
//                userDataRepository.loginResponse(inputEmail.getText().toString(), inputPassword.getText().toString())
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new SingleObserver<AuthResponse>() {
//                            @Override
//                            public void onSubscribe(Disposable d) {
//
//                            }
//
//                            @Override
//                            public void onSuccess(AuthResponse authResponse) {
//                                Log.i(TAG, "Auth Response : " + authResponse);
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.e(TAG, e.getMessage(),e);
//                            }
//                        });
            }
        });

        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void startUserAcountService(){
        Intent intent = new Intent("com.vijay.mirrorapp.services.UserAccountService");
        intent.setPackage("com.vijay.mirrorapp");
        startService(intent);
        bindService(intent,viewModel.serviceConnection,0);
//        boolean isServiceRunning = isServiceRunning(UserAccountService.class);
//        Log.i(TAG, "Is service Running : " + isServiceRunning);
    }

    private void stopUserAccountService(){
        try{
            viewModel.unbindService();
            unbindService(viewModel.serviceConnection);
        }catch (Throwable e){
            Log.w(TAG, "Failed to unbindState from the service");
        }
        Log.i(TAG, "Activity destroyed");
    }

    private void bindState(){
        compositeDisposable = new CompositeDisposable();

        viewModel.getAuthResponse()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<AuthResponse>() {
                @Override
                public void onSubscribe(Disposable d) {
                    compositeDisposable.add(d);
                }

                @Override
                public void onNext(AuthResponse authResponse) {
                    Log.i(TAG, authResponse.message);
                    //Log.i(TAG, authResponse.errorShortCode);
                    Log.i(TAG,authResponse.authToken);
                    // On complete call either onLoginSuccess or onLoginFailed
                    onLoginSuccess();
                    viewModel.sendUserProfileRequest(authResponse.authToken);
                }

                @Override
                public void onError(Throwable e) {
                    onLoginFailed();
                    progressDialog.dismiss();
                }

                @Override
                public void onComplete() {

                }
            });

        viewModel.getUserProfile()
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<UserProfile>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(UserProfile userProfile) {
                    Log.i(TAG, "User Profile : " + userProfile.name);
                    Log.i(TAG, "User Profile : " + userProfile.email);
                    progressDialog.dismiss();
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
        stopUserAccountService();
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        // TODO: Implement your own authentication logic here.
        viewModel.sendLoginRequest(inputEmail.getText().toString(), inputPassword.getText().toString());
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        loginButton.setEnabled(true);
        //finish();
    }

    public void onLoginFailed() {
        //Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            inputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            inputPassword.setError(null);
        }

        return valid;
    }
}
