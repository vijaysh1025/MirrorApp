package com.vijay.mirrorapp.view;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.vijay.mirrorapp.R;
import com.vijay.mirrorapp.core.platform.BaseActivity;
import com.vijay.mirrorapp.datastore.entities.user.AuthResponse;
import com.vijay.mirrorapp.datastore.entities.user.UserProfile;
import com.vijay.mirrorapp.viewmodel.UserAccountViewModel;

import java.util.Calendar;

import javax.inject.Inject;

/*
Activity to allow user to edit his/her profile.
 */
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

    TextWatcher birthDateTextWatcher = new TextWatcher() {
        private String current = "";
        private String ddmmyyyy = "DDMMYYYY";
        private Calendar cal = Calendar.getInstance();
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                    cal.set(Calendar.MONTH, mon-1);
                    year = (year<1900)?1900:(year>2100)?2100:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                inputBirthday.setText(current);
                inputBirthday.setSelection(sel < current.length() ? sel : current.length());
            }
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };

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

        inputBirthday.addTextChangedListener(birthDateTextWatcher);

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

        String modifiedBirthDay  = birthday.substring(6,10) + "-" + birthday.substring(3,5) + "-" + birthday.substring(0,2);
        Log.i(TAG, modifiedBirthDay);

        viewModel.sendUserUpdateRequest(name, modifiedBirthDay,location);
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
        if(progressDialog!=null)
            progressDialog.dismiss();
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
                        String modifiedBirthDay  = userProfile.birthdate.substring(8,10) + "/" + userProfile.birthdate.substring(5,7) + "/" + userProfile.birthdate.substring(0,4);
                        Log.i(TAG, modifiedBirthDay);
                        inputName.setText(userProfile.name);
                        inputEmail.setText(userProfile.email);
                        inputBirthday.setText(modifiedBirthDay);
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
