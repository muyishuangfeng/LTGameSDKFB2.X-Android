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


    private static CustomBroadCaseReceiver mReceiver;

    /**
     * 注册广播
     */
    public static void registerBroadcast(Context context) {
        mReceiver = new CustomBroadCaseReceiver();
        IntentFilter filter = new IntentFilter(Constants.GOOGLE_RECHARGE_RESULT_CODE);
        context.registerReceiver(mReceiver, filter);
    }

    /**
     * 反注册广播
     */
    public static void unRegisterBroadcast(Context context) {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
        }
    }

    /**
     * 开始
     */
    public static void start(Context context, String appID) {
        FacebookSdk.setApplicationId(appID);
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

    }

    /**
     * 注册
     */
    private static void register(Context context, int code) {
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
    private static void recharge(Context context, double money, String unit, String orderID) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        BigDecimal decimal = BigDecimal.valueOf(money);
        Currency currency = Currency.getInstance(unit.toUpperCase());
        Bundle parameters = new Bundle();
        parameters.putString("LTOrderId", orderID);
        logger.logPurchase(decimal, currency, parameters);
    }

    /**
     * 自定义广播
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
                                recharge(context, model.getLt_price(), model.getLt_currency(), model.getLt_order_id());
                            }
                        }
                        break;
                    }
                    case Constants.GOOGLE_LOGIN_CODE: {
                        register(context, 1);
                        break;
                    }
                    case Constants.FB_LOGIN_CODE: {
                        register(context, 0);
                        break;
                    }
                    case Constants.GUEST_LOGIN_CODE: {
                        register(context, 2);
                        break;
                    }
                }
            }

        }
    }

}
