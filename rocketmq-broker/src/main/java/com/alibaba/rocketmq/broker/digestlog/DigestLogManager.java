package com.alibaba.rocketmq.broker.digestlog;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.rocketmq.broker.BrokerController;


/**
 * ͳ�ƹ�����
 * 
 * @author boyan
 * @Date 2011-4-22
 * @author wuhua
 * @Date 2011-9-9
 * 
 */
public class DigestLogManager {
    

    private final BrokerController brokerController;
    private final boolean startRealTimeStat = Boolean.valueOf(System
        .getProperty("meta.realtime.stat", "true"));
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1,
        new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "DigestLogPrintSchedule");
            }
        });
    private  final PutStatsMoniter putStatsMoniter ;
    private  final GetStatsMoniter getStatsMoniter ;
    private  final StoreStatsMoniter storeStatsMoniter;

    public DigestLogManager(BrokerController brokerController) {
        this.brokerController = brokerController;
        putStatsMoniter = new PutStatsMoniter(brokerController);
        getStatsMoniter = new GetStatsMoniter(brokerController);
        storeStatsMoniter = new StoreStatsMoniter(brokerController);
    }
    
    public void init(){
        
    }
    public void start() {
        if (startRealTimeStat) {
            scheduler.scheduleWithFixedDelay(new DigestPrintOut(), 10, 20, TimeUnit.SECONDS);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    scheduler.shutdown();
                }
            });
        }
    }


    public void dispose() {
        scheduler.shutdown();
    }

    class DigestPrintOut implements Runnable {
        @Override
        public void run() {
            putStatsMoniter.tolog();
            getStatsMoniter.tolog();
            storeStatsMoniter.tolog();
        }
    }

    public PutStatsMoniter getPutStatsMoniter() {
        return putStatsMoniter;
    }


    public StoreStatsMoniter getStoreStatsMoniter() {
        return storeStatsMoniter;
    }

    public GetStatsMoniter getGetStatsMoniter() {
        return getStatsMoniter;
    }

}
