package dev.ezpadaz.vanillaPlus.Features.DeathChest.Model;

import java.util.UUID;

public record GraveData(UUID playerId, String contents, String armor, String offhand, int totalExperience,
                        String date) {
}
