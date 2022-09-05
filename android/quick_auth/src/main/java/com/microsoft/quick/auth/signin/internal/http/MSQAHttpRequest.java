package com.microsoft.quick.auth.signin.internal.http;

import androidx.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

public class MSQAHttpRequest {

  private final String mUrl;
  private @NonNull final Map<String, String> mHeader;
  private @MSQAHttpMethod final String mHttpMethod;
  private final int mConnectTimeout;
  private final int mReadTimeout;

  public MSQAHttpRequest(Builder builder) {
    mUrl = builder.mUrl;
    mHeader = builder.mHeader;
    mHttpMethod = builder.mHttpMethod;
    mConnectTimeout = builder.mConnectTimeout;
    mReadTimeout = builder.mReadTimeout;
  }

  public String getUrl() {
    return mUrl;
  }

  @NonNull
  public Map<String, String> getHeader() {
    return mHeader;
  }

  public String getHttpMethod() {
    return mHttpMethod;
  }

  public int getConnectTimeout() {
    return mConnectTimeout;
  }

  public int getReadTimeout() {
    return mReadTimeout;
  }

  public static class Builder {
    private String mUrl;
    private @NonNull final Map<String, String> mHeader;
    private @MSQAHttpMethod String mHttpMethod;
    private int mConnectTimeout;
    private int mReadTimeout;

    public Builder() {
      mHeader = new HashMap<>();
      mConnectTimeout = MSQAAPIConstant.CONNECT_TIMEOUT;
      mReadTimeout = MSQAAPIConstant.READ_TIMEOUT;
    }

    public Builder setUrl(String url) {
      mUrl = url;
      return this;
    }

    public Builder addHeader(@NonNull String key, @NonNull String value) {
      mHeader.put(key, value);
      return this;
    }

    public Builder setHttpMethod(@MSQAHttpMethod String httpMethod) {
      mHttpMethod = httpMethod;
      return this;
    }

    public Builder setConnectTimeout(int connectTimeout) {
      mConnectTimeout = connectTimeout;
      return this;
    }

    public Builder setReadTimeout(int readTimeout) {
      mReadTimeout = readTimeout;
      return this;
    }

    public MSQAHttpRequest builder() {
      return new MSQAHttpRequest(this);
    }
  }
}