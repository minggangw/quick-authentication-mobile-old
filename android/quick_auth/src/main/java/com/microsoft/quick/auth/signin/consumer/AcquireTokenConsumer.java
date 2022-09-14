package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.ITokenResult;
import com.microsoft.quick.auth.signin.entity.MQASignInTokenResult;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class AcquireTokenConsumer implements Function<IAccountClientApplication, Task<ITokenResult>> {
    private final @NonNull
    Activity mActivity;
    private @NonNull
    final String[] mScopes;
    private @Nullable
    final String mLoginHint;

    public AcquireTokenConsumer(final @NonNull Activity activity, @NonNull final String[] scopes,
                                @Nullable final String loginHint) {
        mActivity = activity;
        mScopes = scopes;
        mLoginHint = loginHint;
    }

    @Override
    public Task<ITokenResult> apply(@NonNull final IAccountClientApplication iAccountClientApplication) throws Exception {
        final Scheduler scheduler = TaskExecutorUtil.IO();
        return Task.create(new Task.OnSubscribe<ITokenResult>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super ITokenResult> consumer) {
                iAccountClientApplication.acquireToken(mActivity, mScopes, mLoginHint,
                        new AuthenticationCallback() {
                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        consumer.onCancel();
                                    }
                                });
                            }

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        consumer.onSuccess(new MQASignInTokenResult(authenticationResult));
                                    }
                                });
                            }

                            @Override
                            public void onError(final MsalException exception) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
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