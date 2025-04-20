package dev.ezpadaz.vanillaPlus.Features.Homes.Utils;

import java.util.UUID;

public record HomeData(UUID player, String homeName, SerializableLocation location) {
}
