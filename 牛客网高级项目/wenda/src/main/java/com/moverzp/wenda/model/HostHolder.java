package com.moverzp.wenda.model;

import org.springframework.stereotype.Component;

@Component
public class HostHolder {
    //保证多线程操作时，每个线程只操作自己的对象
    private static ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}
