package resources;

public enum EMOTES {
    RDG(":RubberDuckyGreen:820700438084845568"),
    RDR(":RubberDuckyRed:820700478467604502");

    private String id;

    private EMOTES(String id) {
        this.id = id;
    }

    public String getAsReaction() {
        return this.id;
    }
}
