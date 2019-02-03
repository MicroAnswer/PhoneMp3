package answer.android.phonemp3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

import org.xutils.x;

import answer.android.phonemp3.ACTION;

public class MediaButtonRecver extends BroadcastReceiver {
  private static int pushCount = 0;
  private static Context context;

  @Override
  public void onReceive(Context context, Intent intent) {
    MediaButtonRecver.context = context;
    // 获得Action
    String intentAction = intent.getAction();
    // 获得KeyEvent对象
    KeyEvent keyEvent = (KeyEvent) intent
            .getParcelableExtra(Intent.EXTRA_KEY_EVENT);

//    Log.i("xxxxx", "Action ---->" + intentAction + "  KeyEvent----->"
//            + keyEvent.toString());
    // 按下 / 松开 按钮
    int keyAction = keyEvent.getAction();

    if (Intent.ACTION_MEDIA_BUTTON.equals(intentAction)
            // 获得按键字节码
            //int keyCode = keyEvent.getKeyCode();

            // 获得事件的时间
            //long downtime = keyEvent.getEventTime();
            //
            //      获取按键码 keyCode
            //      StringBuilder sb = new StringBuilder();
            // 这些都是可能的按键码 ， 打印出来用户按下的键
            // if (KeyEvent.KEYCODE_MEDIA_NEXT == keyCode) {
            //  sb.append("KEYCODE_MEDIA_NEXT");
            // }
            // 说明：当我们按下MEDIA_BUTTON中间按钮时，实际出发的是 KEYCODE_HEADSETHOOK 而不是
            // KEYCODE_MEDIA_PLAY_PAUSE
            //if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == keyCode) {
            // sb.append("KEYCODE_MEDIA_PLAY_PAUSE");

            //}
            //  if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) {
            // sb.append("KEYCODE_HEADSETHOOK");
            // }
            //  if (KeyEvent.KEYCODE_MEDIA_PREVIOUS == keyCode) {
            // sb.append("KEYCODE_MEDIA_PREVIOUS");
            // }
            //   if (KeyEvent.KEYCODE_MEDIA_STOP == keyCode) {
            // sb.append("KEYCODE_MEDIA_STOP");
            // }
            //  输出点击的按键码
            //  Log.i("xxxx", sb.toString());
            // Toast.makeText(context, sb.toString(), Toast.LENGTH_SHORT).show();
            && (KeyEvent.ACTION_DOWN == keyAction)) {
    } else if (KeyEvent.ACTION_UP == keyAction) {
      // Log.i("xxxx", "aaa");

      int keyCode = keyEvent.getKeyCode();
      if (KeyEvent.KEYCODE_HEADSETHOOK == keyCode) {
        // sb.append("KEYCODE_HEADSETHOOK");
        pushCount++;
        // Log.i("xxx", "pushCount=" + pushCount + ", this=" + this.toString());
        if (pushCount == 1) {
          x.task().removeCallbacks(nextRunn);
          x.task().postDelayed(pauseplayRunn, 500);
        } else if (pushCount == 2) {
          x.task().removeCallbacks(pauseplayRunn);
          x.task().postDelayed(nextRunn, 10);
        }
      }
    }
  }

  private static Runnable pauseplayRunn = new Runnable() {
    @Override
    public void run() {
      if (context == null) {
        return;
      }
      // 暂停/播放
      Intent intent = new Intent();
      intent.setAction(ACTION.ASK.PLAY$PAUSE);
      context.sendBroadcast(intent);
      pushCount = 0;
    }
  };
  private static Runnable nextRunn = new Runnable() {
    @Override
    public void run() {
      if (context == null) {
        return;
      }
      // 暂停/播放
      Intent intent = new Intent();
      intent.setAction(ACTION.ASK.NEXT);
      context.sendBroadcast(intent);
      pushCount = 0;
    }
  };
}