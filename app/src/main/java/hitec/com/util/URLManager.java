package hitec.com.util;

import hitec.com.ApplicationContext;

public class URLManager {
    public static String getRegisterURL() {
        return ApplicationContext.HTTP_HOST + "/RegisterDevice.php";
    }

    public static String getUsersURL() {
        return ApplicationContext.HTTP_HOST + "/GetRelatedUsers.php";
    }

    public static String getSendNotificationURL() {
        return ApplicationContext.HTTP_HOST + "/sendSinglePush.php";
    }

    public static String getSendLocationURL() {
        return ApplicationContext.HTTP_HOST + "/sendLocation.php";
    }

    public static String getSendOfflineLocationURL() {
        return ApplicationContext.HTTP_HOST + "/sendOfflineLocation.php";
    }

    public static String getUserMessagesURL() {
        return ApplicationContext.HTTP_HOST + "/GetUserMessages.php";
    }

    public static String getLocationsURL() {
        return ApplicationContext.HTTP_HOST + "/GetLocations.php";
    }

    public static String getRecentStatusURL() {
        return ApplicationContext.HTTP_HOST + "/GetRecentStatus.php";
    }

    public static String getSendAdminNotificationURL() {
        return ApplicationContext.HTTP_HOST + "/SendPushToAdmin.php";
    }

    public static String getUploadImageURL() {
        return ApplicationContext.HTTP_HOST + "/UploadImage.php";
    }

    public static String getImageURL() {
        return ApplicationContext.HTTP_HOST + "/Images/";
    }
}
