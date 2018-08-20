package xyz.biscut.chunkbuster.commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import xyz.biscut.chunkbuster.ChunkBuster;

public class ChunkBusterCommand implements CommandExecutor {

    private ChunkBuster main;

    public ChunkBusterCommand(ChunkBuster main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("chunkbuster.admin") || sender.isOp()) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "give":
                        if (args.length > 1) {
                            Player p = Bukkit.getPlayerExact(args[1]);
                            if (p != null) {
                                int chunkArea;
                                if (args.length > 2) {
                                    try {
                                        chunkArea = Integer.parseInt(args[2]);
                                    } catch (NumberFormatException ex) {
                                        sender.sendMessage(ChatColor.RED + "This isn't a valid number!");
                                        return false;
                                    }
                                    if (chunkArea == 1 || chunkArea == 3 || chunkArea == 5) {
                                        int giveAmount = 1;
                                        if (args.length > 3) {
                                            try {
                                                giveAmount = Integer.parseInt(args[3]);
                                            } catch (NumberFormatException ex) {
                                                sender.sendMessage(ChatColor.RED + "This isn't a valid number!");
                                                return false;
                                            }
                                        }
                                        if (giveAmount < 65) {
                                            if (p.getInventory().firstEmpty() != -1) {
                                                ItemStack item = new ItemStack(Material.ENDER_PORTAL_FRAME, giveAmount);
                                                ItemMeta itemMeta = item.getItemMeta();
                                                itemMeta.setDisplayName(main.getConfigValues().getChunkBusterName());
                                                itemMeta.setLore(main.getConfigValues().getChunkBusterLore(chunkArea));
                                                item.setItemMeta(itemMeta);
                                                item = main.getUtils().addGlow(item, chunkArea);
                                                p.getInventory().addItem(item);
                                                sender.sendMessage(main.getConfigValues().getGiveMessage(p, giveAmount));
                                                p.sendMessage(main.getConfigValues().getReceiveMessage(giveAmount));

                                            } else {
                                                sender.sendMessage(ChatColor.RED + "This player doesn't have any empty slots!");
                                            }
                                        } else {
                                            sender.sendMessage(ChatColor.RED + "You can only give 64 at a time!");
                                        }
                                    } else {
                                        sender.sendMessage(ChatColor.RED + "The area must be 1, 3, or 5.");
                                    }
                                } else {
                                    sender.sendMessage(ChatColor.RED + "Please specify a chunk area!");
                                }
                            } else {
                                sender.sendMessage(ChatColor.RED + "This player is not online!");
                            }
                        } else {
                            sender.sendMessage(ChatColor.RED + "Please specify a player!");
                        }
                        break;
                    case "reload":
                        main.reloadConfig();
                        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config. Most values have been instantly updated.");
                        break;
                    default:
                        sender.sendMessage(ChatColor.RED + "Invalid argument!");
                }
            } else {
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------" + ChatColor.GRAY +"[" + ChatColor.GREEN + ChatColor.BOLD + " ChunkBuster " + ChatColor.GRAY + "]" + ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "--------------");
                sender.sendMessage(ChatColor.GREEN + "● /cb give <player> <chunk-area> [amount] " + ChatColor.GRAY + "- Give a player a chunk buster");
                sender.sendMessage(ChatColor.GREEN + "● /cb reload " + ChatColor.GRAY + "- Reload the config");
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.ITALIC + "v" + main.getDescription().getVersion() + " by Biscut");
                sender.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + "-------------------------------------------");
            }
        } else {
            sender.sendMessage(main.getConfigValues().getNoPermissionMessage());
        }
        return false;
    }
}
