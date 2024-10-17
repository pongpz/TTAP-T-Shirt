package com.project.ttaptshirt.service;

import com.project.ttaptshirt.entity.User;

public interface UserService extends CommonService<User> {

    User findUserByUsername(String userName);

    void insertDefaultUserRole(Long userId);


}
