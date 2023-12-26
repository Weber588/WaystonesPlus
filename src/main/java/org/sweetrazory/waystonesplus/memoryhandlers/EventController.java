package org.sweetrazory.waystonesplus.memoryhandlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.LootGenerateEvent;
import org.bukkit.inventory.ItemStack;
import org.sweetrazory.waystonesplus.enums.Visibility;
import org.sweetrazory.waystonesplus.eventhandlers.*;
import org.sweetrazory.waystonesplus.items.WaystoneSummonItem;
import org.sweetrazory.waystonesplus.menu.MenuManager;
import org.sweetrazory.waystonesplus.menu.TeleportMenu;
import org.sweetrazory.waystonesplus.types.WaystoneType;
import org.sweetrazory.waystonesplus.utils.ColoredText;
import org.sweetrazory.waystonesplus.waystone.Waystone;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EventController implements Listener {
    private final WaystoneInteract waystoneInteract;

    public EventController() {
        waystoneInteract = new WaystoneInteract();
    }

    @EventHandler
    public void onWaystoneCraft(CraftItemEvent event) {
        new WaystoneCraft(event);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onWaystoneInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Waystone waystone = waystoneInteract.getInteractedWaystone(event);

        if (waystone != null) {
            // Cancel event to allow users to right click waystones with blocks without placing them
            event.setCancelled(true);
            TeleportMenu teleportMenu = new TeleportMenu(0);
            MenuManager.openMenu(player, teleportMenu, waystone);
        }
    }

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        List<ItemStack> loot = new ArrayList<>(event.getLoot());
        if (new Random().nextInt(100) + 1 <= ConfigManager.lootSpawnChance) {
            Object[] waystoneTypes = WaystoneMemory.getWaystoneTypes().values().stream().map(WaystoneType::getTypeName).toArray();
            int randomNumber = new Random().nextInt(WaystoneMemory.getWaystoneTypes().size());
            ItemStack waystoneItem = WaystoneSummonItem
                    .getLodestoneHead(
                            ColoredText.getText(LangManager.newWaystoneName),
                            waystoneTypes[randomNumber].toString(),
                            null,
                            null,
                            Visibility.PRIVATE
                    );
            loot.add(waystoneItem);
        }
        event.setLoot(loot);
    }

    @EventHandler
    public void anvilRenameEvent(PrepareAnvilEvent event) {
        WaystoneRename.anvilRename(event);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void chatRenameEvent(AsyncPlayerChatEvent event) {
        WaystoneRename.changeNameChat(event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        new WaystonePlace().onBlockPlace(e);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWaystoneBreak(BlockBreakEvent e) {
        new WaystoneBreak(e);
    }
}
