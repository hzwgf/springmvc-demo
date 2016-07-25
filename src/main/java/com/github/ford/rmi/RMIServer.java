package com.github.ford.rmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Created by ford on 16/3/31.
 */
public class RMIServer {


  public void start(int port) throws Exception{
    NotifyService notifyService=new NotifyServiceImpl();
    LocateRegistry.createRegistry(port);
    Naming.rebind("rmi://127.0.0.1:" + port + "/NotifyService", notifyService);
    System.out.println("server start.");
  }

  public static void main(String[] args) throws Exception{
    new RMIServer().start(6666);

  }

}
