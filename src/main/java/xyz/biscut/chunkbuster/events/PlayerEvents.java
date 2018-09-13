package xyz.biscut.chunkbuster.events;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.biscut.chunkbuster.ChunkBuster;
import xyz.biscut.chunkbuster.timers.MessageTimer;
import xyz.biscut.chunkbuster.utils.HookType;

import java.util.HashMap;

public class PlayerEvents implements Listener {

    private ChunkBuster main;
    private HashMap<Player, Location> chunkBusterLocations = new HashMap<>();

    public PlayerEvents(ChunkBuster main) {
        this.main = main;
    }

    @EventHandler
    public void onChunkBusterPlace(BlockPlaceEvent e) {
        if (e.getItemInHand().getType().equals(main.getConfigValues().getChunkBusterMaterial()) && e.getItemInHand().getItemMeta().getEnchantLevel(Enchantment.LURE) > 0) {
            e.setCancelled(true);
            Player p = e.getPlayer();
            if (p.hasPermission("chunkbuster.use")) {
                if (main.getHookUtils().hasFaction(p)) {
                    if (main.getHookUtils().checkRole(p)) {
                        if (main.getHookUtils().compareLocToPlayer(e.getBlock().getLocation(), p) || main.getHookUtils().isWilderness(e.getBlock().getLocation())) {
                            if (main.getHookUtils().isWilderness(e.getBlock().getLocation()) && !main.getConfigValues().canPlaceInWilderness()) {
                                if (!main.getConfigValues().getOnlyClaimMessage().equals("")) {
                                    p.sendMessage(main.getConfigValues().getOnlyClaimMessage());
                                }
                            } else {
                                chunkBusterLocations.put(p, e.getBlock().getLocation());
                                if (!p.getOpenInventory().getTitle().contains(main.getConfigValues().getGUITitle())) {
                                    Inventory confirmInv = Bukkit.createInventory(null, 9 * main.getConfigValues().getGUIRows(), main.getConfigValues().getGUITitle());
                                    ItemStack acceptItem = main.getConfigValues().getConfirmBlockItemStack();
                                    ItemMeta acceptItemMeta = acceptItem.getItemMeta();
                                    acceptItemMeta.setDisplayName(main.getConfigValues().getConfirmName());
                                    acceptItemMeta.setLore(main.getConfigValues().getConfirmLore());
                                    acceptItem.setItemMeta(acceptItemMeta);
                                    ItemStack cancelItem = main.getConfigValues().getCancelBlockItemStack();
                                    ItemMeta cancelItemMeta = cancelItem.getItemMeta();
                                    cancelItemMeta.setDisplayName(main.getConfigValues().getCancelName());
                                    cancelItemMeta.setLore(main.getConfigValues().getCancelLore());
                                    cancelItem.setItemMeta(cancelItemMeta);
                                    int slotCounter = 1;
                                    for (int i = 0; i < 9 * main.getConfigValues().getGUIRows(); i++) {
                                        if (slotCounter < 5) {
                                            confirmInv.setItem(i, acceptItem);
                                        } else if (slotCounter > 5) {
                                            confirmInv.setItem(i, cancelItem);
                                        }
                                        if (slotCounter >= 9) {
                                            slotCounter = 1;
                                        } else {
                                            slotCounter++;
                                        }
                                    }
                                    p.openInventory(confirmInv);
                                }
                            }
                        } else {
                            if (main.getHookUtils().getHookType() == HookType.WORLDGUARD) {
                                if (!main.getConfigValues().getRegionProtectedMessage().equals("")) {
                                    p.sendMessage(main.getConfigValues().getRegionProtectedMessage());
                                }
                            } else {
                                if (main.getConfigValues().canPlaceInWilderness()) {
                                    if (!main.getConfigValues().getOnlyWildernessClaimMessage().equals("")) {
                                        p.sendMessage(main.getConfigValues().getOnlyWildernessClaimMessage());
                                    }
                                } else {
                                    if (!main.getConfigValues().getOnlyClaimMessage().equals("")) {
                                        p.sendMessage(main.getConfigValues().getOnlyClaimMessage());
                                    }
                                }
                            }
                        }
                    } else {
                        if (!main.getConfigValues().getMinimumRoleMessage().equals("")) {
                            p.sendMessage(main.getConfigValues().getMinimumRoleMessage());
                        }
                    }
                } else {
                    if (!main.getConfigValues().getNoFactionMessage().equals("")) {
                        p.sendMessage(main.getConfigValues().getNoFactionMessage());
                    }
                }
            } else {
                if (!main.getConfigValues().getNoPermissionMessagePlace().equals("")) {
                    p.sendMessage(main.getConfigValues().getNoPermissionMessagePlace());
                }
            }
        }
    }

    @EventHandler
    public void onConfirmClick(InventoryClickEvent e) {
        if (e.getClickedInventory() != null && e.getClickedInventory().getName() != null &&
                e.getClickedInventory().getName().contains(main.getConfigValues().getGUITitle())) {
            e.setCancelled(true);
            Player p = (Player)e.getWhoClicked();
            Location chunkBusterLocation = chunkBusterLocations.get(e.getWhoClicked());
            if (chunkBusterLocation != null) {
                if (p.getItemInHand() != null && p.getItemInHand().getType().equals(main.getConfigValues().getChunkBusterMaterial()) && p.getItemInHand().hasItemMeta() && p.getItemInHand().getItemMeta().getEnchantLevel(Enchantment.LURE) > 0) {
                    int chunkBusterDiameter = e.getWhoClicked().getItemInHand().getItemMeta().getEnchantLevel(Enchantment.LURE);
                    if (main.getHookUtils().hasFaction(p)) {
                        if (main.getHookUtils().checkRole(p)) {
                            if (main.getHookUtils().compareLocToPlayer(chunkBusterLocation, p) || main.getHookUtils().isWilderness(chunkBusterLocation)) {
                                if (main.getHookUtils().isWilderness(chunkBusterLocation) && !main.getConfigValues().canPlaceInWilderness()) {
                                    if (!main.getConfigValues().getOnlyClaimMessage().equals("")) {
                                        p.sendMessage(main.getConfigValues().getOnlyClaimMessage());
                                    }
                                } else {
                                    if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getDisplayName().contains(main.getConfigValues().getConfirmName())) {
                                        if (p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE)) {
                                            if (p.getItemInHand().getAmount() <= 1) {
                                                p.getInventory().setItemInHand(null);
                                            } else {
                                                ItemStack chunkBusterItem = p.getItemInHand();
                                                chunkBusterItem.setAmount(p.getItemInHand().getAmount() - 1);
                                                p.getInventory().setItemInHand(chunkBusterItem);
                                            }
                                        }
                                        if (main.getConfigValues().getChunkBusterWarmup() > 0) {
                                            int seconds = main.getConfigValues().getChunkBusterWarmup();
                                            new MessageTimer(seconds, p, main).runTaskTimer(main, 0L, 20L);
                                            Bukkit.getScheduler().runTaskLater(main, () -> main.getUtils().clearChunks(chunkBusterDiameter, chunkBusterLocation, p), 20L * seconds);
                                        } else {
                                            main.getUtils().clearChunks(chunkBusterDiameter, chunkBusterLocation, p);
                                        }
                                    }
                                }
                            } else {
                                if (main.getHookUtils().getHookType() == HookType.WORLDGUARD) {
                                    if (!main.getConfigValues().getRegionProtectedMessage().equals("")) {
                                        p.sendMessage(main.getConfigValues().getRegionProtectedMessage());
                                    }
                                } else {
                                    if (main.getConfigValues().canPlaceInWilderness()) {
                                        if (!main.getConfigValues().getOnlyWildernessClaimMessage().equals("")) {
                                            p.sendMessage(main.getConfigValues().getOnlyWildernessClaimMessage());
                                        }
                                    } else {
                                        if (!main.getConfigValues().getOnlyClaimMessage().equals("")) {
                                            p.sendMessage(main.getConfigValues().getOnlyClaimMessage());
                                        }
                                    }
                                }
                            }
                        } else {
                            if (!main.getConfigValues().getMinimumRoleMessage().equals("")) {
                                p.sendMessage(main.getConfigValues().getMinimumRoleMessage());
                            }
                        }
                    } else {
                        if (!main.getConfigValues().getNoFactionMessage().equals("")) {
                            p.sendMessage(main.getConfigValues().getNoFactionMessage());
                        }
                    }
                }
            } else {
                p.sendMessage(ChatColor.RED + "Error, please re-place your chunk buster.");
            }
            e.getWhoClicked().closeInventory();
            chunkBusterLocations.remove(e.getWhoClicked());
        }
    }

    @EventHandler
    public void onGUIClose(InventoryCloseEvent e) {
        if (e.getInventory().getName().contains(main.getConfigValues().getGUITitle())) {
            chunkBusterLocations.remove(e.getPlayer());
        }
    }
}
