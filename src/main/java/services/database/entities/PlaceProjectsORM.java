package services.database.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "place_projects")
public class PlaceProjectsORM {
    @Id
    @Column(name = "project_id")
    private int projectId;

    @Column(name = "pixels_drawn")
    private int pixelsDrawn;

    @Column(name = "discord_user_id")
    private String discordUserId;

    public PlaceProjectsORM() {}

    public PlaceProjectsORM(int projectId, int pixelsDrawn, String discordUserId) {
        this.projectId = projectId;
        this.pixelsDrawn = pixelsDrawn;
        this.discordUserId = discordUserId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getPixelsDrawn() {
        return pixelsDrawn;
    }

    public void setPixelsDrawn(int pixelsDrawn) {
        this.pixelsDrawn = pixelsDrawn;
    }

    public String getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(String discordUserId) {
        this.discordUserId = discordUserId;
    }
}
