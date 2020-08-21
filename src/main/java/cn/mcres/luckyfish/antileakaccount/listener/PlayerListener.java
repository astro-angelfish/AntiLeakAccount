package cn.mcres.luckyfish.antileakaccount.listener;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
import cn.mcres.luckyfish.antileakaccount.MessageHolder;
import cn.mcres.luckyfish.antileakaccount.email.EmailManager;
import cn.mcres.luckyfish.antileakaccount.mojang.MojangApiHelper;
import cn.mcres.luckyfish.antileakaccount.verify.VerifyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;

public class PlayerListener implements Listener {
    private final VerifyManager vm = AntiLeakAccount.getInstance().getVerifyManager();
    private final MessageHolder mh = AntiLeakAccount.getInstance().getMessageHolder();

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (message.startsWith(".check")) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            if (vm.isVerified(p)) {
                mh.sendMessage(p, "duplicate-verification", null);
                return;
            }

            String[] slices = message.split(" ");
            if (slices.length != 2) {
                mh.sendMessage(p, "invalid-authentication-command", null);
                return;
            }
            if (vm.hasRequest(p)) {
                mh.sendMessage(p, "duplicate-authentication", null);
                return;
            }
            if (MojangApiHelper.validateWithEmailAndPassword(slices[1], vm.fetchPassword(p), p.getUniqueId())) {
                mh.sendMessage(p, "authenticate-success", null);
                EmailManager.sendEmail(slices[1], vm.putRequest(p));
                return;
            } else {
                mh.sendMessage(p, "authenticate-fail", null);
            }
        }

        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucket(PlayerBucketEmptyEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerAnimation(PlayerAnimationEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!vm.isVerified((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!vm.isVerified((Player) event.getEntity())) {
                event.setCancelled(true);
            }
        }
        if (event.getDamager() instanceof Player) {
            if (!vm.isVerified((Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockMultiPlace(BlockMultiPlaceEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerFish(PlayerFishEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerTakeLecternBook(PlayerTakeLecternBookEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onTabComplete(TabCompleteEvent event) {
        if (event.getSender() instanceof Player) {
            if (!vm.isVerified((Player) event.getSender())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!vm.isPlayerLoaded(event.getPlayer())) {
            mh.sendMessage(event.getPlayer(), "loading", null);
        }

        if (!vm.isVerified(event.getPlayer())) {
            event.setJoinMessage("");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setQuitMessage("");
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!vm.isVerified(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
