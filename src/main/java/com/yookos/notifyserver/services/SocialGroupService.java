package com.yookos.notifyserver.services;

import com.yookos.notifyserver.core.domain.PageResultWrapper;
import com.yookos.notifyserver.core.domain.ResultWrapper;

/**
 * Created by jome on 2014/04/04.
 */
public interface SocialGroupService {
    ResultWrapper getActivityForGroup(String displayname);
    ResultWrapper getActivityForGroup(int groupid);

    PageResultWrapper getActivityForPage(String displayname);

    PageResultWrapper getActivityForPage(int spaceid);
}
