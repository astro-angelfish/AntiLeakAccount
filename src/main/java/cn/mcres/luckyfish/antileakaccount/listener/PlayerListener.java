package cn.mcres.luckyfish.antileakaccount.listener;

import cn.mcres.luckyfish.antileakaccount.AntiLeakAccount;
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
import org.bukkit.event.player.*;
import org.bukkit.event.server.TabCompleteEvent;

public class PlayerListener implements Listener {
    private final VerifyManager vm = AntiLeakAccount.getInstance().getVerifyManager();

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        if (message.startsWith(".check")) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            if (vm.isVerified(p)) {
                p.sendMessage("你已经验证过了，无需再次验证");
                return;
            }

            String[] slices = message.split(" ");
            if (slices.length != 2) {
                p.sendMessage("无效的验证命令，用法：.check <邮箱>");
                return;
            }
            if (MojangApiHelper.validateWithEmailAndPassword(slices[1], vm.fetchPassword(p), p.getUniqueId())) {
                p.sendMessage("第一步验证完成，请前往邮箱完成第二步验证，并修改你的minecraft账户的密码");
                EmailManager.sendEmail(slices[1], vm.putRequest(p));
                return;
            } else {
                p.sendMessage("无法验证你的帐号:(");
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
}
