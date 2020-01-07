package com.sdk.ltgame.ltfacebook;

import android.content.Context;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsConstants;
import com.facebook.appevents.AppEventsLogger;

import java.math.BigDecimal;
import java.util.Currency;

public class FacebookEventManager {

    /**
     * 开始
     */
    public static void start(Context context,String appID) {
        FacebookSdk.setApplicationId(appID);
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

    }

    /**
     * 注册
     */
    public static void register(Context context, int code) {
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
    public static void recharge(Context context, double money, String unit, String orderID) {
        AppEventsLogger logger = AppEventsLogger.newLogger(context);
        BigDecimal decimal = BigDecimal.valueOf(money);
        Currency currency = Currency.getInstance(unit.toUpperCase());
        Bundle parameters = new Bundle();
        parameters.putString("LTOrderId", orderID);
        logger.logPurchase(decimal, currency, parameters);
    }



}
