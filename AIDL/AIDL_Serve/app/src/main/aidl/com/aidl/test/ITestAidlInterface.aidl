// ITestAidlInterface.aidl
package com.aidl.test;
import com.aidl.test.User;
import com.aidl.test.IOnUserRaise;
// Declare any non-default types here with import statements

interface ITestAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<User> getUsers();
    void addUser(in User user);
    void registerListener(IOnUserRaise listener);
    void unRegisterListener(IOnUserRaise listener);
}
