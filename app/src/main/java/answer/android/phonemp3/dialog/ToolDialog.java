package answer.android.phonemp3.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.PlayServiceBridgeAIDL;
import answer.android.phonemp3.R;
import answer.android.phonemp3.activity.CoverActivity;
import answer.android.phonemp3.activity.FeedBackActivity;
import answer.android.phonemp3.activity.MainActivity;

/**
 * 工具弹出框
 * Created by Microanswer on 2017/7/14.
 */

public class ToolDialog extends BaseDailog implements View.OnClickListener {

  private LinearLayout mTodayCover;
  //private LinearLayout mMusicScan;
  private LinearLayout mTimeOut;
  private LinearLayout mJunhengqi;
  private LinearLayout mPleaseWait;
  private TextView tittmemitmetet;
  private LinearLayout mFeedBack;
  private PlayServiceBridgeAIDL playServiceBridgeAIDL;
  private MainActivity aaaa;

  public ToolDialog(MainActivity context, PlayServiceBridgeAIDL playServiceBridgeAIDL) {
    super(context);
    aaaa = context;
    this.playServiceBridgeAIDL = playServiceBridgeAIDL;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_tool);

    mTodayCover = (LinearLayout) findViewById(R.id.dialog_tool_todaycover);
    // mMusicScan = (LinearLayout) findViewById(R.id.dialog_tool_musicscan);
    mTimeOut = (LinearLayout) findViewById(R.id.dialog_tool_timeout);
    mPleaseWait = (LinearLayout) findViewById(R.id.dialog_tool_pleasewait);
    tittmemitmetet = (TextView) findViewById(R.id.tittmemitmetet);
    mFeedBack = (LinearLayout) findViewById(R.id.dialog_tool_feedback);
    mJunhengqi = (LinearLayout) findViewById(R.id.dialog_tool_junhengqi);

    mTodayCover.setOnClickListener(this);
    //mMusicScan.setOnClickListener(this);
    mTimeOut.setOnClickListener(this);
    mJunhengqi.setOnClickListener(this);
    mPleaseWait.setOnClickListener(this);
    mFeedBack.setOnClickListener(this);
    if (playServiceBridgeAIDL != null) {
      try {
        if (playServiceBridgeAIDL.isSetTime()) {
          tittmemitmetet.setText(R.string.cancelTimeout);
        } else {
          tittmemitmetet.setText(R.string.timeout);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  protected void onStart() {
    super.onStart();
    if (playServiceBridgeAIDL != null && tittmemitmetet != null) {
      try {
        if (playServiceBridgeAIDL.isSetTime()) {
          tittmemitmetet.setText(R.string.cancelTimeout);
        } else {
          tittmemitmetet.setText(R.string.timeout);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void show() {
    if (playServiceBridgeAIDL != null && tittmemitmetet != null) {
      try {
        if (playServiceBridgeAIDL.isSetTime()) {
          tittmemitmetet.setText(R.string.cancelTimeout);
        } else {
          tittmemitmetet.setText(R.string.timeout);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    super.show();
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    Intent intent = new Intent();
    switch (id) {
      case R.id.dialog_tool_todaycover:
        intent.setClass(getContext(), CoverActivity.class);
        getContext().startActivity(intent);
        break;
      //case R.id.dialog_tool_musicscan:
      // break;
      case R.id.dialog_tool_pleasewait:
        Toast.makeText(getContext(), "更多功能敬请期待", Toast.LENGTH_LONG).show();
        break;
      case R.id.dialog_tool_feedback:
        intent.setClass(getContext(), FeedBackActivity.class);
        getContext().startActivity(intent);
        break;
      case R.id.dialog_tool_timeout:
        if (playServiceBridgeAIDL != null) {
          try {
            if (playServiceBridgeAIDL.isSetTime()) {
              Intent intensst = new Intent(ACTION.CANCEL_TIMEOUT);
              getContext().sendBroadcast(intensst);
            } else {
              new TimeoutDialog(getContext()).show();
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        break;
      case R.id.dialog_tool_junhengqi:
        if(aaaa.getAudioSeesionId() != -1) {
          final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
          effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, aaaa.getAudioSeesionId());
          effects.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, getContext().getPackageName());
          effects.putExtra(AudioEffect.EXTRA_CONTENT_TYPE, AudioEffect.CONTENT_TYPE_MUSIC);
          aaaa.startActivityForResult(effects, 1);
        }
        break;
    }
    hide();
  }

}
