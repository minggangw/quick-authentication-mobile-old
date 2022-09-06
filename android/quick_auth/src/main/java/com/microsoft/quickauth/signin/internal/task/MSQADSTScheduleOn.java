//  Copyright (c) Microsoft Corporation.
//  All rights reserved.
//
//  This code is licensed under the MIT License.
//
//  Permission is hereby granted, free of charge, to any person obtaining a copy
//  of this software and associated documentation files(the "Software"), to deal
//  in the Software without restriction, including without limitation the rights
//  to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
//  copies of the Software, and to permit persons to whom the Software is
//  furnished to do so, subject to the following conditions :
//
//  The above copyright notice and this permission notice shall be included in
//  all copies or substantial portions of the Software.
//
//  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
//  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
//  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
//  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
//  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
//  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
//  THE SOFTWARE.
package com.microsoft.quickauth.signin.internal.task;

import androidx.annotation.NonNull;

public class MSQADSTScheduleOn<T> extends MSQATask<T> {

  private final @NonNull MSQATask<T> mTask;
  private final @NonNull MSQAThreadSwitcher mSwitcher;

  public MSQADSTScheduleOn(@NonNull MSQATask<T> task, @NonNull MSQAThreadSwitcher switcher) {
    this.mTask = task;
    this.mSwitcher = switcher;
  }

  @Override
  protected void subscribeActual(@NonNull MSQAConsumer<? super T> consumer) {
    mTask.subscribe(new MSQADSTScheduleOnConsumer<>(mSwitcher, consumer));
  }

  static final class MSQADSTScheduleOnConsumer<T> implements MSQAConsumer<T> {

    private final @NonNull MSQAThreadSwitcher mSwitcher;
    private final @NonNull MSQAConsumer<? super T> mDownStreamConsumer;

    public MSQADSTScheduleOnConsumer(
        @NonNull MSQAThreadSwitcher switcher, @NonNull MSQAConsumer<? super T> consumer) {
      this.mSwitcher = switcher;
      this.mDownStreamConsumer = consumer;
    }

    @Override
    public void onSuccess(final T t) {
      mSwitcher.schedule(
          new Runnable() {
            @Override
            public void run() {
              mDownStreamConsumer.onSuccess(t);
            }
          });
    }

    @Override
    public void onError(final Exception t) {
      mSwitcher.schedule(
          new Runnable() {
            @Override
            public void run() {
              mDownStreamConsumer.onError(t);
            }
          });
    }

    @Override
    public void onCancel() {
      mDownStreamConsumer.onCancel();
    }
  }
}