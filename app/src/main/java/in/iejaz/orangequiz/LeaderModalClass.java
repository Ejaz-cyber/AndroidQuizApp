package in.iejaz.orangequiz;

public class LeaderModalClass {
    String imgUrl;
    String name;
    long tPoints;

    public LeaderModalClass(){}

    public LeaderModalClass(String imgUrl, String name, long tPoints) {
        this.imgUrl = imgUrl;
        this.name = name;
        this.tPoints = tPoints;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long gettPoints() {
        return tPoints;
    }

    public void settPoints(long tPoints) {
        this.tPoints = tPoints;
    }
}
