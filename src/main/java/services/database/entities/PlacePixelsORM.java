package services.database.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "place_pixels")
public class PlacePixelsORM implements Serializable {
    @EmbeddedId
    private PlacePixelsKey key;

    @Column(name = "x_coordinate")
    private int xCoordinate;

    @Column(name = "y_coordinate")
    private int yCoordinate;

    @Column(name = "image_color")
    private String imageColor;

    @Column(name = "alpha")
    private double alpha;

    @Column(name = "place_color")
    private String placeColor;

    public PlacePixelsORM() {}

    public PlacePixelsORM(PlacePixelsKey key, int xCoordinate, int yCoordinate, String imageColor, double alpha, String placeColor) {
        this.key = key;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.imageColor = imageColor;
        this.alpha = alpha;
        this.placeColor = placeColor;
    }

    public PlacePixelsKey getKey() {
        return key;
    }

    public void setKey(PlacePixelsKey key) {
        this.key = key;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public void setXCoordinate(int xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public void setYCoordinate(int yCoordinate) {
        this.yCoordinate = yCoordinate;
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

    @Embeddable
    public static class PlacePixelsKey implements Serializable {
        @Column(name = "project_id")
        private int projectId;

        @Column(name = "index")
        private int index;

        public PlacePixelsKey() {}

        public PlacePixelsKey(int projectId, int index) {
            this.projectId = projectId;
            this.index = index;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PlacePixelsKey that = (PlacePixelsKey) o;

            return index == that.index && projectId == that.projectId;
        }

        @Override
        public int hashCode() {
            return Objects.hash(projectId, index);
        }
    }
}
