// IRmServiceConnection.aidl
package core.left.io.framework;

import core.left.io.framework.IRmCommunicator;

interface IRmServiceConnection {

    void setProfile(in byte[] profileInfo);

    void setRmCommunicator(IRmCommunicator iRmCommunicator);

    void setServiceForeground(in boolean isForeGround);

    boolean resetCommunicator(IRmCommunicator iRmCommunicator);
}
