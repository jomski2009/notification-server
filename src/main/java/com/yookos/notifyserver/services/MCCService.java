package com.yookos.notifyserver.services;

import com.yookos.notifyserver.core.domain.PCLResult;
import com.yookos.notifyserver.core.domain.Username;

import java.util.List;

/**
 * Created by jome on 2014/02/27.
 */

public interface MCCService {
    List<PCLResult> checkCompliance(List<Username> usernames);
}
