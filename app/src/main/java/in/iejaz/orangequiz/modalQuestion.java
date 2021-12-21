package in.iejaz.orangequiz;

public class modalQuestion {

    String oA;
    String oAns;
    String oB;
    String oC;
    String oD;
    String qImgUrl;
    String question;
    String hasImg;

    // for firebase
    public modalQuestion(){

    }


    public modalQuestion(String oA, String oAns, String oB, String oC, String oD, String qImgUrl, String question,String hasImg) {
        this.oA = oA;
        this.oAns = oAns;
        this.oB = oB;
        this.oC = oC;
        this.oD = oD;
        this.qImgUrl = qImgUrl;
        this.question = question;
        this.hasImg = hasImg;
    }

    public String getHasImg() {
        return hasImg;
    }

    public void setHasImg(String hasImg) {
        this.hasImg = hasImg;
    }

    public String getoA() {
        return oA;
    }

    public void setoA(String oA) {
        this.oA = oA;
    }

    public String getoB() {
        return oB;
    }

    public void setoB(String oB) {
        this.oB = oB;
    }

    public String getoC() {
        return oC;
    }

    public void setoC(String oC) {
        this.oC = oC;
    }

    public String getoD() {
        return oD;
    }

    public void setoD(String oD) {
        this.oD = oD;
    }

    public String getoAns() {
        return oAns;
    }

    public void setoAns(String oAns) {
        this.oAns = oAns;
    }

    public String getqImgUrl() {
        return qImgUrl;
    }

    public void setqImgUrl(String qImgUrl) {
        this.qImgUrl = qImgUrl;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
