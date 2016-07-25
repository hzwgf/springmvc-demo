package com.github.ford.rmi;

import java.rmi.Naming;

/**
 * Created by ford on 16/3/31.
 */
public class RMIClient {

  public boolean notify(String instance,Message message) throws Exception{
    NotifyService notifyService=
        (NotifyService) Naming.lookup("rmi://" + instance + "/NotifyService");

    return notifyService.notify(message);

  }


  public static void main(String[] args) throws Exception{
    new RMIClient().notify("127.0.0.1:6666",new Message());

  }

}
