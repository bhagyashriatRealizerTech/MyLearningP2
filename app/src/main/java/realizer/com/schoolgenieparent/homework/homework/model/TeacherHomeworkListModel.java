package realizer.com.schoolgenieparent.homework.homework.model;

/**
 * Created by Win on 11/17/2015.
 */
public class TeacherHomeworkListModel {

    private String subject="";
    private String homework="";
    private String image="";
    private String givenBy="";
    private String hasSync="";

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHomework() {
        return homework;
    }

    public void setHomework(String homework) {
        this.homework = homework;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGivenBy() {
        return givenBy;
    }

    public void setGivenBy(String givenBy) {
        this.givenBy = givenBy;
    }

    public String getHasSync() {
        return hasSync;
    }

    public void setHasSync(String hasSync) {
        this.hasSync = hasSync;
    }
}
