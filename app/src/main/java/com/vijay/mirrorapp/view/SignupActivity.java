package com.vijay.mirrorapp.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vijay.mirrorapp.R;
import com.vijay.mirrorapp.core.platform.BaseActivity;
import com.vijay.mirrorapp.entities.user.AuthResponse;
import com.vijay.mirrorapp.viewmodel.UserAccountViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SignupActivity extends BaseActivity {
    private static final String TAG = "SignupActivity";

    @Inject
    UserAccountViewModel viewModel;

    CompositeDisposable compositeDisposable;
    ProgressDialog progressDialog;

    @BindView(R.id.input_name) EditText _nameText;
    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getAppComponent().inject(this);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        bindUserAcountService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindState();
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        // TODO: Implement your own signup logic here.

        viewModel.sendSignupRequest(name, email, password, reEnterPassword);

    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String reEnterPassword = _reEnterPasswordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;
        } else {
            _reEnterPasswordText.setError(null);
        }

        return valid;
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
                        Log.i(TAG,authResponse.authToken);
                        onSignupSuccess();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onSignupFailed();
                        progressDialog.dismiss();
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

    private void bindUserAcountService(){
        Intent intent = new Intent("com.vijay.mirrorapp.services.UserAccountService");
        intent.setPackage("com.vijay.mirrorapp");
        bindService(intent,viewModel.serviceConnection,0);
    }

    private void unbindUserAccountService(){
        try{
            viewModel.unbindService();
            unbindService(viewModel.serviceConnection);
        }catch (Throwable e){
            Log.w(TAG, "Failed to unbindState from the service");
        }
        Log.i(TAG, "Activity destroyed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindUserAccountService();
    }

}