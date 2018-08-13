package com.seg2.edudata;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * @author Team2-I
 *
 * Splash screen for loading components
 */
public class SplashScreen extends Activity {
    //required for processing splash screen
    private Thread splashThread;
    private SplashScreen splashScreen;

    /**
     * Creates the elements on the screen
     * @param savedInstanceState The activity's saved instance state
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        splashScreen=this;

        splashThread=new Thread(){
          @Override
        public void run() {
              try {
                  synchronized (this) {
                      wait(2500);
                  }
              } catch(InterruptedException e){
                  e.printStackTrace();
              }
              finish();
              //Start mainactivity
              Intent intent=new Intent();
              intent.setClass(splashScreen, MainActivity.class);
              startActivity(intent);
          }
        };
        splashThread.start();
    }

    /**
     * Touch event for disabling splash screen
     * @param event Motion event
     * @return Always returns true
     */
    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
            synchronized (splashThread){
                splashThread.notifyAll();
            }
        }
        return true;
    }
}
