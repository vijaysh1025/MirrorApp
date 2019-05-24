package com.vijay.mirrorapp.view;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.vijay.mirrorapp.R;
import com.vijay.mirrorapp.core.platform.BaseActivity;
import com.vijay.mirrorapp.entities.user.AuthResponse;
import com.vijay.mirrorapp.entities.user.UserProfile;
import com.vijay.mirrorapp.viewmodel.UserAccountViewModel;

import javax.inject.Inject;

public class EditProfileActivity extends BaseActivity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    @Inject
    UserAccountViewModel viewModel;

    @BindView(R.id.input_name)
    EditText inputName;

    @BindView(R.id.input_email)
    EditText inputEmail;

    @BindView(R.id.input_birthday)
    EditText inputBirthday;

    @BindView(R.id.input_location)
    EditText inputLocation;

    @BindView(R.id.btn_update)
    Button updateButton;

    CompositeDisposable compositeDisposable;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getAppComponent().inject(this);
        ButterKnife.bind(this);

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        bindUserAcountService(viewModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindState();
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

    private void updateProfile(){
        Log.d(TAG, "Update Profile");

        if (!validate()) {
            onUpdateFailed();
            return;
        }

        updateButton.setEnabled(false);

        progressDialog = new ProgressDialog(EditProfileActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Updating Account...");
        progressDialog.show();

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String birthday = inputBirthday.getText().toString();
        String location = inputLocation.getText().toString();

        viewModel.sendUserUpdateRequest(name, birthday,location);
    }

    public boolean validate() {
        boolean valid = true;

        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String birthday = inputBirthday.getText().toString();
        String location = inputLocation.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        if (birthday.isEmpty()) {
            inputBirthday.setError("cannot be empty");
            valid = false;
        } else {
            inputBirthday.setError(null);
        }

        if (location.isEmpty()) {
            inputLocation.setError("cannot be empty");
            valid = false;
        } else {
            inputLocation.setError(null);
        }

        return valid;
    }

    public void onUpdateFailed(){
        updateButton.setEnabled(true);
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

    public void onUpdateSuccess() {
        updateButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    private void bindState(){
        compositeDisposable = new CompositeDisposable();

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
                        inputName.setText(userProfile.name);
                        inputEmail.setText(userProfile.email);
                        inputBirthday.setText(userProfile.birthdate);
                        inputLocation.setText(userProfile.location);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

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
                        if(authResponse.errorMessage!=null && !authResponse.errorMessage.isEmpty()) {
                            onUpdateSuccess();
                            showErrorMessage(authResponse.errorMessage);
                            return;
                        }
                        viewModel.setEmptyAuthResponse();
                        onUpdateSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        onUpdateFailed();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void unbindState(){
        compositeDisposable.clear();
    }



}
