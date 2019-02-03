package answer.android.phonemp3.interfaces;

import answer.android.phonemp3.PlayServiceBridgeAIDL;

/**
 * 服务绑定成功监听器
 * Created by Micro on 2017/6/20.
 */

public interface ServiceBindListener {
  void onConnected(PlayServiceBridgeAIDL playServiceBridgeAIDL);
  void onDisConnected();
}
