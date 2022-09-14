package com.microsoft.quick.auth.signin.consumer;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.quick.auth.signin.entity.MQAAccountInfo;
import com.microsoft.quick.auth.signin.entity.MQASignInOptions;
import com.microsoft.quick.auth.signin.signapplicationclient.IAccountClientApplication;
import com.microsoft.quick.auth.signin.task.Consumer;
import com.microsoft.quick.auth.signin.task.DirectToScheduler;
import com.microsoft.quick.auth.signin.task.Function;
import com.microsoft.quick.auth.signin.task.Scheduler;
import com.microsoft.quick.auth.signin.task.Task;
import com.microsoft.quick.auth.signin.util.TaskExecutorUtil;

public class SignInConsumer implements Function<IAccountClientApplication, Task<MQAAccountInfo>> {

    private final @NonNull
    Activity mActivity;
    private final @NonNull
    MQASignInOptions mOptions;

    public SignInConsumer(final @NonNull Activity activity, final @NonNull MQASignInOptions options) {
        mActivity = activity;
        mOptions = options;
    }

    @Override
    public Task<MQAAccountInfo> apply(@NonNull final IAccountClientApplication iAccountClientApplication) throws Exception {
        return Task.create(new Task.OnSubscribe<MQAAccountInfo>() {
            @Override
            public void subscribe(@NonNull final Consumer<? super MQAAccountInfo> consumer) {
                final Scheduler scheduler = TaskExecutorUtil.IO();
                iAccountClientApplication.signIn(mActivity, mOptions.getLoginHint(), mOptions.getScopes(),
                        new AuthenticationCallback() {

                            @Override
                            public void onSuccess(final IAuthenticationResult authenticationResult) {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        MQAAccountInfo account =
                                                MQAAccountInfo.getAccount(authenticationResult);
                                        consumer.onSuccess(account);
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

                            @Override
                            public void onCancel() {
                                scheduler.schedule(new Runnable() {
                                    @Override
                                    public void run() {
                                        consumer.onCancel();
                                    }
                                });
                            }
                        });
            }
        })
                .taskScheduleOn(DirectToScheduler.directToIOWhenCreateInMain());
    }
}