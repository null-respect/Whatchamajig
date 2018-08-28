package org.dimigo.whatchamajig;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class BootService extends Service {

    private static final String CHANNEL_ID = "100";
    private static final int NOTIFICATION_ID = 100;

    public BootService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent2, int flags, int startId) {
        Intent intent = new Intent(getApplicationContext(),
                FloatingService.class);
        

//        PendingIntent pendingIntent =
//                PendingIntent.getActivity(
//                        getApplicationContext()
//                        ,100
//                        ,intent
//                        ,0
//                );

        PendingIntent pendingIntent =
            PendingIntent.getService(
                    getApplicationContext()
                    ,100
                    ,intent
                    ,0
            );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        getApplicationContext(),
                        CHANNEL_ID
                );


        // 같은 타입 리턴해서 이어적기 가능
        builder.setContentTitle("새 메모 작성하기")
                .setAutoCancel(false)
                .setContentIntent(pendingIntent)
                .setSmallIcon(getNotificationIcon());

        String text = intent2.getStringExtra("titleText");
        if(text!=null)
        builder.setContentText("최근 내용 : " + intent2.getStringExtra("titleText"));

        NotificationManager manage = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 오레오 버젼부터는 모든 알람에 채널을 설정해야 함
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // 오레오 버젼 이상
            NotificationChannel channel =
                    new NotificationChannel(CHANNEL_ID
                            ,"MyChannel"
                            , NotificationManager.IMPORTANCE_DEFAULT);
            manage.createNotificationChannel(channel);
        }

        manage.notify(NOTIFICATION_ID,
                builder.build());

        stopSelf();
        return START_NOT_STICKY;
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.web_hi_res_512 : R.drawable.web_hi_res_512;
    }


}


