package realizer.com.schoolgenieparent.communication.model;

/**
 * Created by Win on 1/6/2016.
 */
public class TeacherQuerySendModel {

    public int ConversationId=0;
    public String SchoolCode ="";
    public String std ="";
    public String div ="";
    public String msgId="";
    public String fromUserId ="";
    public String fromUserName ="";
    public String text ="";

    public String getProfUrl() {
        return profUrl;
    }

    public void setProfUrl(String profUrl) {
        this.profUrl = profUrl;
    }

    public String profUrl="";
    public String sentTime ="";
    public String hassync="";

    public String getStd() {
        return std;
    }

    public void setStd(String std) {
        this.std = std;
    }

    public String getDiv() {
        return div;
    }

    public void setDiv(String div) {
        this.div = div;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public int getSentDate() {
        return sentDate;
    }

    public void setSentDate(int sentDate) {
        this.sentDate = sentDate;
    }

    public int sentDate = 0;
    public int getConversationId() {
        return ConversationId;
    }

    public void setConversationId(int conversationId) {
        ConversationId = conversationId;
    }

    public String getSchoolCode() {
        return SchoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        SchoolCode = schoolCode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getHassync() {
        return hassync;
    }

    public void setHassync(String hassync) {
        this.hassync = hassync;
    }
}
