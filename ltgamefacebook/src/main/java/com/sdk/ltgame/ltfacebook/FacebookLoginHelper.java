package com.sdk.ltgame.ltfacebook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.FacebookSdkNotInitializedException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.gentop.ltgame.ltgamesdkcore.base.BaseEntry;
import com.gentop.ltgame.ltgamesdkcore.exception.LTGameError;
import com.gentop.ltgame.ltgamesdkcore.impl.OnLoginStateListener;
import com.sdk.ltgame.ltnet.impl.OnLoginSuccessListener;
import com.sdk.ltgame.ltnet.manager.LoginRealizeManager;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class FacebookLoginHelper {


    private static CallbackManager mFaceBookCallBack;
    private int mLoginTarget;
    private static WeakReference<Activity> mActivityRef;
    private OnLoginStateListener mListener;
    //统计
    private boolean isStats;


    FacebookLoginHelper(Activity activity,boolean isStats, OnLoginStateListener listener, int loginTarget) {
        mActivityRef = new WeakReference<>(activity);
        this.mListener = listener;
        this.isStats=isStats;
        this.mLoginTarget = loginTarget;
    }


    /**
     * 初始化
     */
    void login(String appID, Context context, boolean isLoginOut) {
        FacebookSdk.setApplicationId(appID);
        FacebookSdk.sdkInitialize(context);
        if (isLoginOut) {
            loginOutAction();
        } else {
            loginAction();
        }

    }


    /**
     * 设置登录结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    void setOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (mFaceBookCallBack != null) {
            mFaceBookCallBack.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * 登录
     */
    private void loginAction() {
        try {
            mFaceBookCallBack = CallbackManager.Factory.create();
            LoginManager.getInstance()
                    .logInWithReadPermissions(mActivityRef.get(),
                            Arrays.asList("public_profile"));
            if (mFaceBookCallBack != null) {
                LoginManager.getInstance().registerCallback(mFaceBookCallBack,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                if (loginResult != null) {
                                    LoginRealizeManager.facebookLogin(mActivityRef.get(),
                                            loginResult.getAccessToken().getToken(), isStats,mListener);
                                }

                            }

                            @Override
                            public void onCancel() {
                                mListener.onState(mActivityRef.get(), com.gentop.ltgame.ltgamesdkcore.model.LoginResult.cancelOf());
                            }

                            @Override
                            public void onError(FacebookException error) {
                                mListener.onState(mActivityRef.get(),
                                        com.gentop.ltgame.ltgamesdkcore.model.LoginResult
                                                .failOf(LTGameError.make(LTGameError.CODE_COMMON_ERROR,
                                                        error.toString())));
                            }
                        });
            }
        } catch (FacebookSdkNotInitializedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 退出登录
     */
    private void loginOutAction() {
        LoginManager.getInstance().logOut();
        mListener.onState(mActivityRef.get(),
                com.gentop.ltgame.ltgamesdkcore.model.LoginResult
                        .loginOut(LTGameError.make("Facebook LoginOut")));
        mActivityRef.get().finish();
    }

    /**
     * 获取Token
     */
    public static void getToken(Context context, String appID, final OnLoginSuccessListener<String> mListener) {
        FacebookSdk.setApplicationId(appID);
        FacebookSdk.sdkInitialize(context);
        LoginManager.getInstance().logOut();
        try {
            mFaceBookCallBack = CallbackManager.Factory.create();
            LoginManager.getInstance()
                    .logInWithReadPermissions(mActivityRef.get(),
                            Arrays.asList("public_profile"));
            if (mFaceBookCallBack != null) {
                LoginManager.getInstance().registerCallback(mFaceBookCallBack,
                        new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                if (loginResult != null) {
                                    BaseEntry<String> result = new BaseEntry<>();
                                    result.setResult(loginResult.getAccessToken().getToken());
                                    mListener.onSuccess(result);
                                }

                            }

                            @Override
                            public void onCancel() {

                            }

                            @Override
                            public void onError(FacebookException error) {

                            }
                        });
            }
        } catch (FacebookSdkNotInitializedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 设置登录结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    public static void getTokenResult(int requestCode, int resultCode, Intent data) {
        if (mFaceBookCallBack != null) {
            mFaceBookCallBack.onActivityResult(requestCode, resultCode, data);
        }
    }
}
