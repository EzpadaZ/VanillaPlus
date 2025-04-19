package dev.ezpadaz.vanillaPlus.Features.Teleport.Utils;

import java.util.UUID;

public record TeleportRequest(String requestUUID, UUID from, UUID to, boolean bring) {}
