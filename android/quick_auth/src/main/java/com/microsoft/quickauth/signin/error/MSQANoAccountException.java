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
package com.microsoft.quickauth.signin.error;

import androidx.annotation.Nullable;

/**
 * This error class is a wrapper msal no account error, created when error code is {@link
 * MSQAErrorString#NO_CURRENT_ACCOUNT}.
 */
public class MSQANoAccountException extends MSQAException {
  public MSQANoAccountException(String errorCode) {
    super(errorCode);
  }

  public MSQANoAccountException(String errorCode, String errorMessage) {
    super(errorCode, errorMessage);
  }

  public MSQANoAccountException(String errorCode, String errorMessage, Throwable throwable) {
    super(errorCode, errorMessage, throwable);
  }

  public static MSQANoAccountException create(@Nullable Exception exception) {
    return new MSQANoAccountException(
        MSQAErrorString.NO_CURRENT_ACCOUNT,
        exception != null
            ? exception.getMessage()
            : MSQAErrorString.NO_CURRENT_ACCOUNT_ERROR_MESSAGE);
  }
}
