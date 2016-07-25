package com.github.ford.rmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by ford on 16/3/31.
 */
public class NotifyServiceImpl extends UnicastRemoteObject implements NotifyService {

  public NotifyServiceImpl() throws RemoteException{
    super();
  }

  public boolean notify(Message m) throws RemoteException{
    System.out.println("receive message.");
    //notify client
    return true;
  }


}
