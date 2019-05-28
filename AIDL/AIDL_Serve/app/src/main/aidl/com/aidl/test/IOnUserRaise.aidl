// IOnUserRaise.aidl
package com.aidl.test;
import com.aidl.test.User;
interface IOnUserRaise {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void onUserRaise(in User user);
}
