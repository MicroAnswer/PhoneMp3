package answer.android.phonemp3.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import answer.android.phonemp3.ACTION;
import answer.android.phonemp3.R;
import answer.android.phonemp3.tool.Tool;

/**
 * 定时设置弹出框
 * Created by Microanswer on 2017/7/18.
 */

public class TimeoutDialog extends BaseDailog implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

  private Button mButtonSet, mButtonCancel;
  private SeekBar seekBar;
  private TextView mShow;
  private long secl;
  private SharedPreferences sharedPreferences;

  TimeoutDialog(Context context) {
    super(context);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_timeout);
    mButtonCancel = (Button) findViewById(R.id.dialog_tool_timeout_cancel);
    mButtonSet = (Button) findViewById(R.id.dialog_tool_timeout_set);
    seekBar = (SeekBar) findViewById(R.id.seekBar);
    mShow = (TextView) findViewById(R.id.dialog_tool_timeout_show);

    mButtonCancel.setOnClickListener(this);
    mButtonSet.setOnClickListener(this);
    seekBar.setOnSeekBarChangeListener(this);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

    int timeout = sharedPreferences.getInt("timeout", 800);
    seekBar.setProgress(timeout);


    secl = (1200 + timeout) * 1000;
    setText(secl);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.dialog_tool_timeout_cancel:
        dismiss();
        break;
      case R.id.dialog_tool_timeout_set:
        Intent intent = new Intent(ACTION.SET_TIMEOUT);
        intent.putExtra(ACTION.EXTRA_TIME, secl);
        getContext().sendBroadcast(intent);
        dismiss();
        break;
    }
  }

  private void setText(long ss) {
    String s = Tool.parseTime2(ss);
    mShow.setText(String.format(getContext().getResources().getString(R.string.willstopat), s));
  }

  @Override
  public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    secl = (1200 + progress) * 1000;
    setText(secl);
  }

  @Override
  public void onStartTrackingTouch(SeekBar seekBar) {

  }

  @Override
  public void onStopTrackingTouch(SeekBar seekBar) {
    sharedPreferences.edit().putInt("timeout", seekBar.getProgress()).apply();
  }
}
