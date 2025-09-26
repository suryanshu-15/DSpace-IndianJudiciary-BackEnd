package org.dspace.app.rest.diracai.util;

import org.dspace.core.Constants;

public class PermissionUtils {

    public static String resolveActionName(int actionId) {
        if (actionId >= 0 && actionId < Constants.actionText.length) {
            return Constants.actionText[actionId];
        }
        return "UNKNOWN_ACTION";
    }
}
