package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import java.util.UUID;

public record TeleportRequest(UUID from, UUID to, boolean bring) {}
