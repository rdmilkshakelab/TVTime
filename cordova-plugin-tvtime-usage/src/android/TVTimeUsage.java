package com.alfernado.tvtime;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Calendar;
import java.util.Map;

public class TVTimeUsage extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        try {
            if ("hasAccess".equals(action)) {
                callbackContext.success(hasUsageAccess() ? 1 : 0);
                return true;
            }

            if ("openSettings".equals(action)) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                cordova.getActivity().startActivity(intent);
                callbackContext.success();
                return true;
            }

            if ("queryToday".equals(action)) {
                if (!hasUsageAccess()) {
                    callbackContext.error("Usage Access is not enabled for TVTime");
                    return true;
                }

                callbackContext.success(getTodayStats());
                return true;
            }

        } catch (Exception e) {
            callbackContext.error(e.getMessage());
        }

        return false;
    }

    private boolean hasUsageAccess() {
        AppOpsManager appOps = (AppOpsManager) cordova.getActivity()
            .getSystemService(Context.APP_OPS_SERVICE);

        int mode;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mode = appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                cordova.getActivity().getPackageName()
            );
        } else {
            mode = appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(),
                cordova.getActivity().getPackageName()
            );
        }

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private JSONArray getTodayStats() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        long startMillis = start.getTimeInMillis();
        long endMillis = System.currentTimeMillis();

        UsageStatsManager manager = (UsageStatsManager) cordova.getActivity()
            .getSystemService(Context.USAGE_STATS_SERVICE);

        Map<String, UsageStats> usageMap =
            manager.queryAndAggregateUsageStats(startMillis, endMillis);

        JSONArray result = new JSONArray();

        for (Map.Entry<String, UsageStats> entry : usageMap.entrySet()) {
            UsageStats usage = entry.getValue();
            long foregroundMs = usage.getTotalTimeInForeground();

            if (foregroundMs < 60_000) {
                continue;
            }

            JSONObject item = new JSONObject();
            item.put("packageName", entry.getKey());
            item.put("minutes", Math.round(foregroundMs / 60000.0));
            result.put(item);
        }

        return result;
    }
}
