package com.github.ford.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by ford on 16/3/31.
 */
public interface NotifyService extends Remote {

  boolean notify(Message m) throws RemoteException;

}
