package org.sweetrazory.waystonesplus.menu.submenus;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.sweetrazory.waystonesplus.memoryhandlers.LangManager;
import org.sweetrazory.waystonesplus.menu.Menu;
import org.sweetrazory.waystonesplus.menu.MenuManager;
import org.sweetrazory.waystonesplus.utils.ColoredText;
import org.sweetrazory.waystonesplus.utils.DB;
import org.sweetrazory.waystonesplus.utils.ItemBuilder;
import org.sweetrazory.waystonesplus.utils.ItemUtils;
import org.sweetrazory.waystonesplus.waystone.Waystone;

import java.util.Arrays;
import java.util.function.Predicate;

public class NonBlockMenu extends Menu {
    private final Material[] elements = Arrays.stream(Material.values())
            .filter(check -> !check.isBlock())
            .toArray(Material[]::new);


    public NonBlockMenu(int page) {
        super(54, ColoredText.getText(LangManager.itemsMenuTitle), page);
    }

    @Override
    public void initializeItems(Player player, Waystone waystone) {
        ItemStack backButton = new ItemBuilder(Material.BARRIER)
                .displayName(ColoredText.getText(LangManager.returnText))
                .persistentData("action", "menu")
                .build();
        ItemStack filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).displayName(" ").build();
        inventory.setContents(new ItemStack[]
                {filler, filler, filler, filler, filler, filler, filler, filler, filler,
                        filler, null, null, null, null, null, null, null, filler,
                        filler, null, null, null, null, null, null, null, filler,
                        filler, null, null, null, null, null, null, null, filler,
                        filler, null, null, null, null, null, null, null, filler,
                        filler, filler, filler, filler, backButton, filler, filler, filler, filler});
        if (elements.length >= 28 * page + 1) {
            setItem(50, new ItemBuilder(Material.SNOWBALL).displayName(ColoredText.getText(LangManager.nextPage)).persistentData("action", "nextPage").persistentData("page", page).build());
        }

        if (page > 0) {
            setItem(48, new ItemBuilder(Material.SNOWBALL).displayName(ColoredText.getText(LangManager.prevPage)).persistentData("action", "prevPage").persistentData("page", page).build());
        }
        Material[] newElements = Arrays.copyOfRange(elements, page * 22, elements.length);
        int k = 0;
        for (int i = 1; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                if (newElements.length == k) {
                    break;
                }
                ItemStack item = new ItemBuilder(newElements[k]).persistentData("action", "setIcon").displayName(ColoredText.getText("&6" + newElements[k].name().toUpperCase())).build();
                setItem(i * 9 + j + 1, item);
                k++;
            }
        }
    }

    @Override
    public void handleClick(Player player, ItemStack item) {
        String action = ItemUtils.getPersistentString(item, "action");
        if (action != null) {
            Menu iconMenu = new IconMenu();
            switch (action) {
                case "menu":
                    MenuManager.openMenu(player, iconMenu, waystone);
                    break;
                case "nextPage":
                    Menu solidMenu = new NonBlockMenu(page + 1);
                    MenuManager.openMenu(player, solidMenu, waystone);
                    break;
                case "prevPage":
                    Menu solidMenu2 = new NonBlockMenu(page - 1);
                    MenuManager.openMenu(player, solidMenu2, waystone);
                    break;
                case "setIcon":
                    waystone.setIcon(item.getType());
                    MenuManager.openMenu(player, iconMenu, waystone);
                    DB.updateWaystone(waystone);
                    break;
            }
        }
    }
}
