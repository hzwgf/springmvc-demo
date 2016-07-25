package com.github.ford;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by ford on 16/3/31.
 */
public class LauncherTest {

  public static int connections=3000;

  public static final CyclicBarrier cyclicBarrier=new CyclicBarrier(connections+1);

  public static void main(String[] args) throws Exception{


    ThreadPoolExecutor executors= (ThreadPoolExecutor) Executors.newFixedThreadPool(connections);

    for(int i=0;i<connections;i++){
      executors.execute(new Runnable(){

        public void run() {
          try{
            Thread.sleep(10);
            final CloseableHttpClient httpClient= HttpClients.createDefault();
            HttpPost post=new HttpPost("http://127.0.0.1:8188/echo/hello");
            CloseableHttpResponse closeableHttpResponse=httpClient.execute(post);
            cyclicBarrier.await();

            closeableHttpResponse.getEntity();
          } catch(Exception e){

          }

        }
      });
    }

    cyclicBarrier.await();
    System.out.println("发起"+connections+"连接完毕。");
    synchronized (executors){
      executors.wait();
    }

  }

}
