package com.sdk.ltgame.ltfacebook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;
import com.gentop.ltgame.ltgamesdkcore.model.ResultModel;
import com.sdk.ltgame.ltnet.base.Constants;

import java.math.BigDecimal;
import java.util.Currency;


public class FacebookEventManager {


    private static CustomBroadCaseReceiver mReceiver = new CustomBroadCaseReceiver();
    private static FacebookEventManager sInstance;


    private FacebookEventManager() {
    }

    /**
     * 单例
     */
    public static FacebookEventManager getInstance() {
        if (sInstance == null) {
            synchronized (FacebookEventManager.class) {
                if (sInstance == null) {
                    sInstance = new FacebookEventManager();
                }
            }
        }
        return sInstance;
    }


    /**
     * 注册广播
     */
    public void registerBroadcast(Context context, boolean fbEnable, boolean googleEnable,
                                  boolean gpEnable, boolean guestEnable) {
        if (mReceiver == null) {
            mReceiver = new CustomBroadCaseReceiver();
            IntentFilter filter = new IntentFilter();
            if (gpEnable) {
                filter.addAction(Constants.GOOGLE_RECHARGE_RESULT_CODE);
            }
            if (googleEnable) {
                filter.addAction(Constants.GOOGLE_LOGIN_CODE);
            }
            if (fbEnable) {
                filter.addAction(Constants.FB_LOGIN_CODE);
            }
            if (guestEnable) {
                filter.addAction(Constants.GUEST_LOGIN_CODE);
            }
            context.registerReceiver(mReceiver, filter);
        } else {
            IntentFilter filter = new IntentFilter();
            if (gpEnable) {
                filter.addAction(Constants.GOOGLE_RECHARGE_RESULT_CODE);
            }
            if (googleEnable) {
                filter.addAction(Constants.GOOGLE_LOGIN_CODE);
            }
            if (fbEnable) {
                filter.addAction(Constants.FB_LOGIN_CODE);
            }
            if (guestEnable) {
                filter.addAction(Constants.GUEST_LOGIN_CODE);
            }
            context.registerReceiver(mReceiver, filter);
        }
    }

    /**
     * 反注册广播
     */
    public void unRegisterBroadcast(Context context) {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * 开始
     */
    public void start(Context context, String appID) {
        FacebookSdk.setApplicationId(appID);
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

    }

    /**
     * 注册
     */
  private   void register(Context context, int code) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        Bundle params = new Bundle();
        if (code == 0) {
            params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, "Facebook");
        } else if (code == 1) {
            params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, "Google");
        } else if (code == 2) {
            params.putString(AppEventsConstants.EVENT_PARAM_REGISTRATION_METHOD, "Guest");
        }
        logger.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, params);
    }

    /**
     * 内购
     */
    private void recharge(Context context, double money, String unit, String orderID) {
        try {
            AppEventsLogger logger = AppEventsLogger.newLogger(context);
            BigDecimal decimal = BigDecimal.valueOf(money);
            Currency currency = Currency.getInstance(unit.toUpperCase());
            Bundle parameters = new Bundle();
            parameters.putString("LTOrderId", orderID);
            logger.logPurchase(decimal, currency, parameters);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * 广播
     */
    private static class CustomBroadCaseReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                switch (intent.getAction()) {
                    case Constants.GOOGLE_RECHARGE_RESULT_CODE: {
                        if (intent.getExtras() != null) {
                            if (intent.getExtras().getSerializable(Constants.GOOGLE_RECHARGE_CODE) != null) {
                                ResultModel model = (ResultModel)
                                        intent.getExtras().getSerializable(Constants.GOOGLE_RECHARGE_CODE);
                                Log.e("TAG", model.getLt_price() + "===" + model.getLt_currency() + "===" + model.getLt_order_id());
                                FacebookEventManager.getInstance().recharge(context, model.getLt_price(), model.getLt_currency(), model.getLt_order_id());
                            }
                        }
                        break;
                    }
                    case Constants.GOOGLE_LOGIN_CODE: {
                        Log.e("TAG", "GOOGLE_LOGIN_CODE");
                        FacebookEventManager.getInstance().register(context, 1);
                        break;
                    }
                    case Constants.FB_LOGIN_CODE: {
                        Log.e("TAG", "FB_LOGIN_CODE");
                        FacebookEventManager.getInstance().register(context, 0);
                        break;
                    }
                    case Constants.GUEST_LOGIN_CODE: {
                        Log.e("TAG", "GUEST_LOGIN_CODE");
                        FacebookEventManager.getInstance().register(context, 2);
                        break;
                    }
                    default:
                        break;
                }

            }

        }
    }

}
