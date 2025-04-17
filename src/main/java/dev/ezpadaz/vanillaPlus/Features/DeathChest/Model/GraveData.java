package dev.ezpadaz.vanillaPlus.Features.DeathChest.Model;

import java.util.UUID;

public class GraveData {
    private final UUID playerId;
    private final String contents;
    private final String armor;
    private final String offhand;
    private final int totalExperience;
    private final String date;

    public GraveData(UUID playerId, String contents, String armor, String offhand, int totalExperience, String date) {
        this.playerId = playerId;
        this.contents = contents;
        this.armor = armor;
        this.offhand = offhand;
        this.totalExperience = totalExperience;
        this.date = date;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getContents() {
        return contents;
    }

    public String getArmor() {
        return armor;
    }

    public String getDate() {
        return date;
    }

    public String getOffhand() {
        return offhand;
    }

    public int getTotalExperience() {
        return totalExperience;
    }
}
