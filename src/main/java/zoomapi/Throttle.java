package zoomapi;

import java.sql.Time;
import java.time.LocalTime;

public class Throttle {

    private static Throttle uniqueInstance = null;
    private long startTime1=0;
    //private Time startTime2;
    private Throttle(){
    }

    public static synchronized Throttle getInstance(){
        if(uniqueInstance == null){
            uniqueInstance = new Throttle();
        }
        return uniqueInstance;
    }

    public void ThrottleOperation(){
        if((System.currentTimeMillis())-startTime1<=1000){
            //System.out.println((System.currentTimeMillis())-startTime1);
            try{
                Thread.sleep(1500);
                System.out.println("sleeping");
            }
            catch (Exception e){
                e.printStackTrace();
            }
            startTime1=System.currentTimeMillis();
            //System.out.println("startTime="+startTime1);
        }
        else {
            startTime1=System.currentTimeMillis();
            //System.out.println("startTime="+startTime1);
        }
    }

}
