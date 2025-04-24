package dev.ezpadaz.vanillaPlus.Features.Backpack.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import dev.ezpadaz.vanillaPlus.Features.Backpack.Model.BackpackModel;
import dev.ezpadaz.vanillaPlus.Utils.InventoryHelper;
import dev.ezpadaz.vanillaPlus.Utils.MessageHelper;
import dev.ezpadaz.vanillaPlus.VanillaPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BackpackManager {
    private static final Map<UUID, Inventory> inventories = new HashMap<>();

    public static void openBackpack(Player sender) {
        UUID uuid = sender.getUniqueId();
        Inventory inv = inventories.get(uuid);

        String playerName = sender.getName();
        Component title = Component.text()
                .append(Component.text(playerName + "'s ", NamedTextColor.DARK_GREEN, TextDecoration.BOLD))
                .append(Component.text("Backpack", NamedTextColor.GOLD, TextDecoration.BOLD))
                .build();


        // TODO: Maybe i could make the chests be dynamically sized by config,
        //  this would require verifying sizes and copying items over to new size if needed.
        if (inv == null) {
            inv = Bukkit.createInventory(null, 72, title);
            inventories.put(uuid, inv);
        }

        sender.openInventory(inv);
    }

    public static void saveInventoriesToFile() {
        File file = new File(VanillaPlus.getInstance().getDataFolder(), "data/backpack/save.json");
        file.getParentFile().mkdirs();

        Map<String, String> serialized = new HashMap<>();
        for (Map.Entry<UUID, Inventory> entry : inventories.entrySet()) {
            serialized.put(entry.getKey().toString(), InventoryHelper.toBase64(entry.getValue()));
        }

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(serialized, writer);
            MessageHelper.console("&6Backpack Data: &a[SAVED]");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadInventoriesFromFile() {
        File file = new File(VanillaPlus.getInstance().getDataFolder(), "data/backpack/save.json");

        if (!file.exists()) {
            MessageHelper.console("&6Backpack Data: &c[EMPTY]");
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>() {
            }.getType(); // UUID as String, inventory as base64
            Map<String, String> loaded = gson.fromJson(reader, type);

            if (loaded != null) {
                inventories.clear();
                for (Map.Entry<String, String> entry : loaded.entrySet()) {
                    inventories.put(UUID.fromString(entry.getKey()), InventoryHelper.fromBase64(entry.getValue()));
                }
            }
            MessageHelper.console("&6Backpack Data: &a[OK]");
        } catch (IOException | JsonParseException e) {
            e.printStackTrace();
            MessageHelper.console("&6Backpack Data: &c[ERROR]");
        }
    }
}
