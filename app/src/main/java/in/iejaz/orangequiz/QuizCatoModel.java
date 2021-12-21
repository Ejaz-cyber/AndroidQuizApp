package in.iejaz.orangequiz;

public class QuizCatoModel {

    String Title;
    String ImgUrl;

    QuizCatoModel(){}

    public QuizCatoModel(String title, String imgUrl) {
        Title = title;
        ImgUrl = imgUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }
}
