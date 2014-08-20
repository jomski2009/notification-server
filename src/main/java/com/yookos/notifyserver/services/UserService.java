package com.yookos.notifyserver.services;

import com.yookos.notifyserver.core.domain.Activity;
import com.yookos.notifyserver.core.domain.User;
import com.yookos.notifyserver.rest.domain.UserResource;

import java.util.List;

public interface UserService {
	public User createUser(UserResource userResource);

	public void follow();

	public void deleteUser(Long userid);

	public void bulkAddUsers(int start, int end);
    public void bulkAddUsers(List<String> users);

	public void addToUserGraph(List<String> rows, int start, int end);

	public void createNodes();

    void addToUserGraph(List<String> rows);

    void addActivityToGraph(List<String> activities);
    void addActivityObjectToGraph(Activity activity);
    void addActivityObjectToGraph(List<Activity> activities);

    User createUser(User user);

    void updateUser(User user);
}
