package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.TokenResult;
import com.microsoft.quick.auth.signin.entity.MSQASignInTokenResult;
import com.microsoft.quick.auth.signin.signinclient.ISignInClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Schedulers;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.MSQATrackerUtil;

import java.util.List;

public class AcquireTokenTask implements Function<ISignInClientApplication,
        Task<TokenResult>> {
    private @NonNull
    final Activity mActivity;
    private @NonNull
    final List<String> mScopes;
    private @Nullable
    final String mLoginHint;
    private @NonNull
    final MSQATrackerUtil mTracker;
    private static final String TAG = AcquireTokenTask.class.getSimpleName();

    public AcquireTokenTask(@NonNull final Activity activity, @NonNull final List<String> scopes,
                            @Nullable final String loginHint,
                            @NonNull final MSQATrackerUtil tracker) {
        mActivity = activity;
        mScopes = scopes;
        mLoginHint = loginHint;
        mTracker = tracker;
    }

    @Override
    public Task<TokenResult> apply(@NonNull final ISignInClientApplication iSignInClientApplication) throws Exception {
        final Scheduler scheduler = Schedulers.io();
        return Task.create(new Task.OnSubscribe<TokenResult>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super TokenResult> consumer) {
                mTracker.track(TAG, "start request MSAL acquireToken api");
                IAccount iAccount = null;
                try {
                    iAccount = iSignInClientApplication.getCurrentAccount();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iSignInClientApplication.acquireToken(mActivity, iAccount, mScopes, mLoginHint,
                        new AuthenticationCallback() {
                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken api cancel");
                                        consumer.onCancel();
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken api " +
                                                "success");
                                        consumer.onSuccess(new MSQASignInTokenResult(authenticationResult));
                                    }
                                });
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        mTracker.track(TAG, "request MSAL acquireToken api " +
                                                "error:" + exception);
                                        consumer.onError(exception);
                                    }
                                });
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}