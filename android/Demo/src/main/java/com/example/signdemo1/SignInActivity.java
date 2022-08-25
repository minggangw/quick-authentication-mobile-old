package com.example.signdemo1;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.signdemo1.view.SignInButtonSettingPop;
import com.microsoft.identity.client.exception.MsalUiRequiredException;
import com.microsoft.quick.auth.signin.MSQASignInClientImp;
import com.microsoft.quick.auth.signin.MSQASignInClient;
import com.microsoft.quick.auth.signin.callback.OnCompleteListener;
import com.microsoft.quick.auth.signin.entity.AccountInfo;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.view.ButtonText;
import com.microsoft.quick.auth.signin.view.MSQASignInButton;

public class SignInActivity extends Activity {

    private MSQASignInButton mSignInButton;
    private TextView mStatus;
    private TextView mUserInfoResult;
    private ImageView mUserPhoto;
    private View mSignButtonSetting;
    private TextView mTokenResult;
    private View mSignOutButton;
    private View msAcquireTokenButton;
    private ViewGroup mRootView;

    private MSQASignInClient mSignInClient;
    private AccountInfo mAccountInfo;
    private SignInButtonSettingPop pop;
    private String[] scops;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mSignInButton = findViewById(R.id.ms_sign_button);
        mRootView = findViewById(R.id.root_view);
        mStatus = findViewById(R.id.status);
        mUserInfoResult = findViewById(R.id.userInfoResult);
        mUserPhoto = findViewById(R.id.userPhoto);
        mSignButtonSetting = findViewById(R.id.ms_sign_button_setting);
        mSignOutButton = findViewById(R.id.ms_sign_out_button);
        mTokenResult = findViewById(R.id.tv_token_result);
        msAcquireTokenButton = findViewById(R.id.ms_acquire_token_button);
        scops = new String[]{"user.read"};

        mSignInClient = new MSQASignInClientImp(this);
        mSignInButton.setOnClickListener(v -> {
            mSignInClient.signIn(this, (accountInfo, error) -> {
                if (accountInfo != null) {
                    uploadSignInfo(accountInfo, null);
                } else {
                    uploadSignInfo(null, error);
                }
            });
        });
        mSignOutButton.setOnClickListener(v -> {
            mSignInClient.signOut((aBoolean, error) -> uploadSignInfo(null, error));
            updateTokenResult(null, null);
        });
        mSignButtonSetting.setOnClickListener(v -> {
            if (pop != null && pop.isShowing()) return;
            if (pop == null) {
                pop = new SignInButtonSettingPop(this, mSignInButton);
            }
            pop.showAtLocation(mRootView, Gravity.BOTTOM, 0, 0);
        });
        msAcquireTokenButton.setOnClickListener(v -> {
            if (mAccountInfo != null) {
                mSignInClient.acquireTokenSilent(mAccountInfo, scops, (iTokenResult, error) -> {
                    /**
                     * If acquireTokenSilent() returns an error that requires an interaction
                     * (MsalUiRequiredException),
                     * invoke acquireToken() to have the user resolve the interrupt interactively.
                     *
                     * Some example scenarios are
                     *  - password change
                     *  - the resource you're acquiring a token for has a stricter set of requirement than
                     *  your Single Sign-On refresh token.
                     *  - you're introducing a new scope which the user has never consented for.
                     */
                    if (error instanceof MsalUiRequiredException) {
                        acquireToken();
                    } else {
                        updateTokenResult(iTokenResult, error);
                    }
                });
            } else {
                acquireToken();
            }
        });
        getCurrentAccount();
    }

    private void acquireToken() {
        mSignInClient.acquireToken(this, scops, null, this::updateTokenResult);
    }

    private void uploadSignInfo(AccountInfo accountInfo, Exception error) {
        if (accountInfo != null) {
            mUserPhoto.setImageBitmap(accountInfo.getPhoto());
            String userInfo = "MicrosoftAccountInfo{" +
                    ", fullName='" + accountInfo.getFullName() + '\'' +
                    ", userName='" + accountInfo.getUserName() + '\'' +
                    ", id='" + accountInfo.getId() + '\'' +
                    '}';
            mUserInfoResult.setText(userInfo);
        } else {
            mUserPhoto.setImageBitmap(null);
            mUserInfoResult.setText(error != null ? "login error: " + error.getMessage() : "");
        }
        mAccountInfo = accountInfo;
        updateStatus(accountInfo != null);
    }

    private void getCurrentAccount() {
        mSignInClient.getCurrentSignInAccount(this,
                (accountInfo, error) -> uploadSignInfo(accountInfo, error));
    }

    private void updateStatus(boolean signIn) {
        mStatus.setText(signIn ? "signed in" : "signed out");
        mSignInButton.setButtonText(signIn ? ButtonText.SIGN_OUT : ButtonText.SIGN_IN_WITH);

        mSignInButton.setVisibility(signIn ? View.GONE : View.VISIBLE);
        mSignOutButton.setVisibility(signIn ? View.VISIBLE : View.GONE);
    }

    private void updateTokenResult(ITokenResult iTokenResult, Exception error) {
        mTokenResult.setText(iTokenResult != null ? iTokenResult.getAccessToken() : error != null ?
                "error:" + error.getMessage() : "");
    }
}
