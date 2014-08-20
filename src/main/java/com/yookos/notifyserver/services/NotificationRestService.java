package com.yookos.notifyserver.services;

import com.yookos.notifyserver.core.domain.UserAndroidDeviceRegistration;
import com.yookos.notifyserver.core.domain.UserRelationship;
import com.yookos.notifyserver.rest.domain.BatchNotificationResource;
import com.yookos.notifyserver.rest.domain.NotificationResource;

import java.util.List;

public interface NotificationRestService {
	public void sendNotification(NotificationResource notificationResource);

	public void addToUserRelationship(List<UserRelationship> recipients);

    public UserRelationship addToUserRelationship(UserRelationship recipient);

    public void deleteUserRelationship(UserRelationship relationship);

	public void sendTestNotification(NotificationResource notificationResource);

    void addDeviceToUserRelationship(List<UserAndroidDeviceRegistration> rows);

    String addorUpdateDeviceRegistration(int userId, String regId);

    String removeDeviceRegistration(String regId, int userId);

    void sendTempNotification(NotificationResource notificationResource);

    void sendNotification(BatchNotificationResource batchNotificationResource);
}
