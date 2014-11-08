package com.avoscloud.chat.entity;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVUtils;
import com.avoscloud.chat.R;
import com.avoscloud.chat.avobject.User;
import com.avoscloud.chat.base.App;
import com.avoscloud.chat.service.ChatService;
import com.avoscloud.chat.util.EmotionUtils;
import com.avoscloud.chat.util.PathUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lzw on 14-8-7.
 */
public class Msg {
  public enum Status{
    SendStart(0), SendSucceed(1), SendReceived(2),
    SendFailed(3), HaveRead(4);

    int value;

    Status(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static Status fromInt(int i){
      return values()[i];
    }
  }


  public enum Type{
    Text(0), Response(1), Image(2), Audio(3), Location(4);
    int value;

    Type(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static Type fromInt(int i){
      return values()[i];
    }
  }

  public enum RoomType{
    Single(0), Group(1);

    int value;
    private RoomType(int value){
      this.value=value;
    }

    public int getValue(){
      return value;
    }

    public static RoomType fromInt(int i){
      return values()[i];
    }
  }

  //long timestamp;
  //String fromPeerId;
  //List<String> toPeerIds;
  String content;
  String objectId;
  String convid;
  AVMessage internalMessage;

  RoomType roomType=RoomType.Single;
  Status status = Status.SendStart;
  Type type = Type.Text;


  public Msg() {
    internalMessage = new AVMessage();
  }

  public AVMessage getInternalMessage() {
    return internalMessage;
  }

  public void setInternalMessage(AVMessage internalMessage) {
    this.internalMessage = internalMessage;
  }

  public String getToPeerId() {
    List<String> toPeerIds = internalMessage.getToPeerIds();
    if (toPeerIds != null && toPeerIds.size() > 0) {
      return toPeerIds.get(0);
    } else {
      return null;
    }
  }

  private List<String> getToPeerIds() {
    return internalMessage.getToPeerIds();
  }

  public void setToPeerId(String toPeerId) {
    setToPeerIds(Arrays.asList(toPeerId));
  }

  private void setToPeerIds(List<String> toPeerIds) {
    internalMessage.setToPeerIds(toPeerIds);
  }

  public String getFromPeerId() {
    return internalMessage.getFromPeerId();
  }

  public void setFromPeerId(String fromPeerId) {
    internalMessage.setFromPeerId(fromPeerId);
  }

  public long getTimestamp() {
    return internalMessage.getTimestamp();
  }

  public String getConvid() {
    return convid;
  }

  public void setConvid(String convid) {
    this.convid = convid;
  }

  public RoomType getRoomType() {
    return roomType;
  }

  public void setRoomType(RoomType roomType) {
    this.roomType = roomType;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Type getType() {
    return type;
  }

  public void setType(Type type) {
    this.type = type;
  }

  public void setTimestamp(long timestamp) {
    internalMessage.setTimestamp(timestamp);
  }

  public String getContent() {
    return content;
  }

  public String getStatusDesc() {
    if (status == Status.SendStart) {
      return App.ctx.getString(R.string.sending);
    } else if (status == Status.SendReceived) {
      return App.ctx.getString(R.string.received);
    } else if (status == Status.SendSucceed) {
      return App.ctx.getString(R.string.sent);
    } else if (status == Status.SendFailed) {
      return App.ctx.getString(R.string.failed);
    } else if (status == Status.HaveRead) {
      return App.ctx.getString(R.string.haveRead);
    } else {
      throw new IllegalArgumentException("unknown status");
    }
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getObjectId() {
    return objectId;
  }

  public void setObjectId(String objectId) {
    this.objectId = objectId;
  }

  public boolean isComeMessage() {
    String fromPeerId = getFromPeerId();
    return !fromPeerId.equals(ChatService.getSelfId());
  }

  public String getChatUserId() {
    if (getRoomType()==RoomType.Group) {
      return getToPeerId();
    } else {
      String fromPeerId = getFromPeerId();
      String selfId = ChatService.getSelfId();
      if (fromPeerId.equals(selfId)) {
        return getToPeerId();
      } else {
        return fromPeerId;
      }
    }
  }

  public String getFromName() {
    String peerId = getFromPeerId();
    User user = App.lookupUser(peerId);
    return user.getUsername();
  }

  public CharSequence getNotifyContent() {
    switch (type) {
      case Audio:
        return App.ctx.getString(R.string.audio);
      case Text:
        if (EmotionUtils.haveEmotion(getContent())) {
          return App.ctx.getString(R.string.emotion);
        } else {
          return getContent();
        }
      case Image:
        return App.ctx.getString(R.string.image);
      case Location:
        return App.ctx.getString(R.string.position);
      default:
        return App.ctx.getString(R.string.newMessage);
    }
  }

  public static Msg fromAVMessage(AVMessage avMsg) {
    Msg msg = new Msg();
    msg.setInternalMessage(avMsg);
    if (!AVUtils.isBlankString(avMsg.getMessage())) {
      HashMap<String, Object> params = JSON.parseObject(avMsg.getMessage(), HashMap.class);
      String objectId=(String) params.get("objectId");
      String content=(String) params.get("content");
      Integer statusInt = (Integer) params.get("status");
      Integer typeInt = (Integer) params.get("type");
      Integer roomTypeInt = (Integer) params.get("roomType");
      String convid = (String) params.get("convid");
      if(objectId==null || content==null || statusInt==null ||
          typeInt==null || roomTypeInt==null || convid==null){
        throwNullException();
      }
      msg.setObjectId(objectId);
      msg.setContent(content);
      Status status=Status.fromInt(statusInt);
      msg.setStatus(status);
      Type type=Type.fromInt(typeInt);
      msg.setType(type);
      RoomType roomType=RoomType.fromInt(roomTypeInt);
      msg.setRoomType(roomType);
      msg.setConvid(convid);
    }
    return msg;
  }

  public AVMessage toAVMessage() {
    if(convid==null || content==null || objectId==null
        || roomType==null || status==null || type==null){
      throwNullException();
    }
    HashMap<String, Object> params = new HashMap<String, Object>();
    params.put("objectId", objectId);
    params.put("content", content);
    params.put("status", status.getValue());
    params.put("type", type.getValue());
    params.put("roomType", roomType.getValue());
    params.put("convid", convid);
    internalMessage.setMessage(JSON.toJSONString(params));
    return internalMessage;
  }

  public static void throwNullException() {
    throw new NullPointerException("at least one of these is null: convid,content,objectId, roomType, status, type");
  }

  @Override
  public String toString() {
    return "{content:" + getContent() + " objectId:" + getObjectId() + " status:" + getStatus() + " fromPeerId:" +
        getFromPeerId() + " toPeerIds:" + getToPeerIds()
        + " timestamp:" + getTimestamp() + " type=" + getType() + "}";
  }

  public String getAudioPath() {
    return PathUtils.getChatFileDir() + getObjectId();
  }
}
