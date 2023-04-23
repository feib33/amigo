package amigo.app.assisted;

public class Carer {


    private String name;

    private String relation;

    private int imageId;

    public Carer(String name, String relation, int imageId) {

        this.name = name;
        this.relation = relation;
        this.imageId = imageId;
    }

    public String getName() {

        return this.name;
    }

    public String getRelation() {

        return this.relation;
    }

    public int getImageId() {
        return this.imageId;
    }

}
