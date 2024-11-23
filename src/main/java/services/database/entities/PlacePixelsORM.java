package services.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "place_pixels")
public class PlacePixelsORM {
    @Id
    @Column(name = "project_id")
    private int projectId;

    @Column(name = "index")
    private int index;

    @Column(name = "x_coordinate")
    private int xcoordinate;

    @Column(name = "y_coordinate")
    private int ycoordinate;

    @Column(name = "image_color")
    private String imageColor;

    @Column(name = "alpha")
    private double alpha;

    @Column(name = "place_color")
    private String placeColor;

    public PlacePixelsORM() {}

    public PlacePixelsORM(int projectId, int index, int xcoordinate, int ycoordinate, String imageColor, double alpha, String placeColor) {
        this.projectId = projectId;
        this.index = index;
        this.xcoordinate = xcoordinate;
        this.ycoordinate = ycoordinate;
        this.imageColor = imageColor;
        this.alpha = alpha;
        this.placeColor = placeColor;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getXcoordinate() {
        return xcoordinate;
    }

    public void setXcoordinate(int xcoordinate) {
        this.xcoordinate = xcoordinate;
    }

    public int getYcoordinate() {
        return ycoordinate;
    }

    public void setYcoordinate(int ycoordinate) {
        this.ycoordinate = ycoordinate;
    }

    public String getImageColor() {
        return imageColor;
    }

    public void setImageColor(String imageColor) {
        this.imageColor = imageColor;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public String getPlaceColor() {
        return placeColor;
    }

    public void setPlaceColor(String placeColor) {
        this.placeColor = placeColor;
    }
}
