package me.drawe.buildbattle.listeners;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.heads.Category;
import me.drawe.buildbattle.heads.HeadInventory;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.objects.*;
import me.drawe.buildbattle.objects.bbobjects.*;
import me.drawe.buildbattle.particles.BBParticle;
import me.drawe.buildbattle.particles.PlotParticle;
import me.drawe.buildbattle.utils.BungeeUtils;
import me.drawe.buildbattle.utils.LocationUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import me.drawe.buildbattle.objects.bbobjects.BBPlayerStats;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent e) {
        //TODO: UPDATE LOGIC
        if (BuildBattle.getInstance().isUseBungeecord() && BuildBattle.getInstance().isAutoJoinPlayers()) {
            BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin();
            if (arena == null) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(Message.NO_EMPTY_ARENA.getChatMessage());
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (BuildBattle.getInstance().isUseBungeecord() && BuildBattle.getInstance().isAutoJoinPlayers()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
                BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin();
                if (arena != null) {
                    arena.addPlayer(p);
                } else {
                    p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                    BungeeUtils.connectPlayerToServer(p, GameManager.getInstance().getRandomFallbackServer());
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if ((a != null) && (a.getBBArenaState() == BBArenaState.THEME_VOTING)) {
            if (!a.getThemeVoting().getVotedPlayers().containsKey(p)) {
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        p.openInventory(a.getThemeVoting().getVoteInventory());
                    }
                }.runTaskLater(BuildBattle.getInstance(), 1L);
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getClickedInventory();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null) {
            if (inv != null) {
                if (e.getSlotType() == InventoryType.SlotType.ARMOR && e.getCursor() != null) {
                    e.setCancelled(true);
                    p.sendMessage(Message.CANNOT_WEAR_ARMOR.getChatMessage());
                    return;
                }
                if (a.getBBArenaState() != BBArenaState.INGAME) {
                    e.setCancelled(true);
                    if (a.getBBArenaState() == BBArenaState.THEME_VOTING) {
                        if (inv.getTitle().equalsIgnoreCase(Message.GUI_THEME_VOTING_TITLE.getMessage())) {
                            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.SIGN) {
                                BBTheme selectedTheme = a.getThemeVoting().getThemeBySlot(e.getSlot());
                                if (selectedTheme != null) {
                                    if (a.getThemeVoting().getVotedPlayers().containsKey(p)) {
                                        BBTheme previousVoted = a.getThemeVoting().getVotedPlayers().get(p);
                                        int votes = previousVoted.getVotes();
                                        previousVoted.setVotes(votes - 1);
                                    }
                                    a.getThemeVoting().getVotedPlayers().put(p, selectedTheme);
                                    selectedTheme.setVotes(selectedTheme.getVotes() + 1);
                                    a.getThemeVoting().updateVoting();
                                }
                            }
                        }
                    } else if (a.getBBArenaState() == BBArenaState.LOBBY) {
                        if (inv.getTitle().equalsIgnoreCase(Message.GUI_TEAMS_TITLE.getMessage())) {
                            if (e.getCurrentItem() != null && e.getCurrentItem().getType() == Material.STAINED_CLAY) {
                                BBTeam team = a.getTeamByItemStack(e.getCurrentItem());
                                BBTeam playerTeam = a.getPlayerTeam(p);
                                if (team != null) {
                                    if ((playerTeam != null) && (playerTeam.equals(team))) {
                                        p.sendMessage(Message.ALREADY_IN_THAT_TEAM.getChatMessage());
                                        return;
                                    } else {
                                        team.joinTeam(p);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (e.getCurrentItem() != null && e.getCurrentItem().isSimilar(OptionsManager.getOptionsItem())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (inv.getTitle().equalsIgnoreCase(Message.GUI_OPTIONS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem().isSimilar(plot.getOptions().getCurrentFloorItem())) {
                                if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                                    if (p.hasPermission("buildbattlepro.changefloor")) {
                                        plot.getOptions().setCurrentFloorItem(e.getCursor());
                                        inv.setItem(e.getSlot(), plot.getOptions().getCurrentFloorItem());
                                        e.setCursor(null);
                                    } else {
                                        p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                    }
                                }
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getHeadsItem())) {
                                p.openInventory(HeadInventory.getInstance().getMainPage());
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getInstance().getWeatherItemStack(plot))) {
                                if (p.hasPermission("buildbattlepro.changeweather")) {
                                    if (plot.getOptions().getCurrentWeather() == WeatherType.CLEAR) {
                                        plot.getOptions().setCurrentWeather(WeatherType.DOWNFALL, false);
                                    } else {
                                        plot.getOptions().setCurrentWeather(WeatherType.CLEAR, false);
                                    }
                                    inv.setItem(e.getSlot(), OptionsManager.getInstance().getWeatherItemStack(plot));
                                } else {
                                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                }
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getParticlesItem())) {
                                p.openInventory(OptionsManager.getParticlesInventory());
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getTimeItem())) {
                                OptionsManager.getInstance().openTimeInventory(p, plot);
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getClearPlotItem())) {
                                plot.resetPlotFromGame();
                                p.closeInventory();
                            /*} else if (e.getCurrentItem().isSimilar(OptionsManager.getBiomesItem())) {
                                p.openInventory(OptionsManager.getBiomesInventory());
                            }
                            */
                            }
                        }
                    } else if (inv.getTitle().equalsIgnoreCase(Message.GUI_PARTICLES_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem() != null) {
                                if (e.getCurrentItem().isSimilar(OptionsManager.getRemoveParticlesItem())) {
                                    OptionsManager.getInstance().openActiveParticlesMenu(p, plot);
                                    return;
                                } else {
                                    if (!p.getInventory().contains(e.getCurrentItem())) {
                                        p.getInventory().setItem(7, e.getCurrentItem());
                                    }
                                }
                            }
                        }
                    } else if (inv.getTitle().equalsIgnoreCase(Message.GUI_PARTICLE_LIST_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLore()) {
                                PlotParticle particle = OptionsManager.getInstance().getPlotParticleFromLore(plot, e.getCurrentItem().getItemMeta().getLore());
                                if (particle != null) {
                                    plot.removeActiveParticle(particle);
                                    if (e.getCurrentItem().getAmount() == 1) {
                                        inv.remove(e.getCurrentItem());
                                    } else {
                                        e.getCurrentItem().setAmount(e.getCurrentItem().getAmount() - 1);
                                    }
                                }
                            }
                        }
                    } else if (inv.getTitle().equalsIgnoreCase(Message.GUI_TIME_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            BBPlotTime selectedTime = BBPlotTime.getTimeFromItemStack(e.getCurrentItem(), e.getSlot());
                            if (selectedTime != null) {
                                if (p.hasPermission("buildbattlepro.changetime")) {
                                    plot.getOptions().setCurrentTime(selectedTime, true);
                                    OptionsManager.getInstance().openTimeInventory(p, plot);
                                } else {
                                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                }
                            }
                        }
                    } else if (inv.getTitle().contains(Message.GUI_HEADS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        Inventory inventory = e.getClickedInventory();
                        HeadInventory headInventory = HeadInventory.getInstance();
                        ItemStack head = e.getCurrentItem();

                        if (inventory != null) {
                            if (inventory.equals(headInventory.getMainPage())) {
                                for (int i = 0; i < headInventory.getAmountOfCategories(); i++) {
                                    if (headInventory.getCategory(i).getIcon().equals(head)) {
                                        p.openInventory(headInventory.getCategory(i).getPage(0).getPage());
                                    }
                                }
                            } else {
                                for (int i = 0; i < headInventory.getAmountOfCategories(); i++) {
                                    for (int j = 0; j < headInventory.getCategory(i).getAmountOfPages(); j++) {
                                        if (inventory.equals(headInventory.getCategory(i).getPage(j).getPage())) {
                                            Category category = headInventory.getCategory(i);
                                            if (e.getSlot() == 45) {
                                                if (j == 0) {
                                                    p.openInventory(category.getPage(category.getAmountOfPages() - 1).getPage());
                                                } else {
                                                    p.openInventory(category.getPage(j - 1).getPage());
                                                }
                                            } else if (e.getSlot() == 49) {
                                                p.openInventory(headInventory.getMainPage());
                                            } else if (e.getSlot() == 53) {
                                                if (j == category.getAmountOfPages() - 1) {
                                                    p.openInventory(category.getPage(0).getPage());
                                                } else {
                                                    p.openInventory(category.getPage(j + 1).getPage());
                                                }
                                            } else {
                                                if (head != null) {
                                                    if (head.getData().getItemType() != Material.AIR) {
                                                        p.getInventory().addItem(head);
                                                        p.closeInventory();
                                                    }
                                                }
                                            }
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (inv != null) {
                if (inv.getTitle().equalsIgnoreCase(OptionsManager.getAllArenasInventory().getTitle())) {
                    e.setCancelled(true);
                    if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                        BBArena clickedArena = ArenaManager.getInstance().getArena(e.getCurrentItem().getItemMeta().getDisplayName());
                        if (clickedArena != null) {
                            clickedArena.addPlayer(p);
                        }
                    }
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onVote(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        if (arena != null) {
            if (arena.getBBArenaState() == BBArenaState.VOTING) {
                if (e.getItem() != null) {
                    if (e.getItem().isSimilar(OptionsManager.getReportItem())) {
                        if(GameManager.getStatsType() == StatsType.MYSQL) {
                            MySQLManager.getInstance().reportBuild(arena.getCurrentVotingPlot(), p);
                        } else {
                            p.sendMessage(Message.REPORT_FAILED.getChatMessage());
                        }
                        return;
                    }
                    Votes vote = Votes.getVoteByItemStack(e.getItem());
                    if (vote != null) {
                        BBPlot currentPlot = arena.getCurrentVotingPlot();
                        if (currentPlot != null) {
                            if (!currentPlot.getTeam().getPlayers().contains(p)) {
                                currentPlot.vote(p, vote);
                                e.setCancelled(true);
                            } else {
                                p.sendMessage(Message.CANT_VOTE_FOR_YOUR_PLOT.getChatMessage());
                            }
                        }
                    }
                }
            } else if (arena.getBBArenaState() == BBArenaState.INGAME) {
                BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
                if (plot != null) {
                    if (e.getItem() != null) {
                        if (e.getItem().isSimilar(OptionsManager.getOptionsItem())) {
                            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                OptionsManager.getInstance().openOptionsInventory(p,plot);
                            }
                        } else if(BBParticle.getBBParticle(e.getItem()) != null) {
                            e.setCancelled(true);
                            if(plot.isLocationInPlot(p.getLocation())) {
                                BBParticle particle = BBParticle.getBBParticle(e.getItem());
                                PlotParticle plotParticle = new PlotParticle(plot, particle, p.getLocation());
                                plot.addActiveParticle(p,plotParticle);
                            } else {
                                p.sendMessage(Message.CANT_PLACE_PARTICLE_OUTSIDE.getChatMessage());
                            }
                        }
                    }
                }
            } else if (arena.getBBArenaState() == BBArenaState.LOBBY) {
                if (e.getItem() != null) {
                    if (e.getItem().isSimilar(OptionsManager.getLeaveItem())) {
                        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            e.setCancelled(true);
                            arena.removePlayer(p);
                        }
                    } else if(e.getItem().isSimilar(OptionsManager.getTeamsItem())) {
                        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                            e.setCancelled(true);
                            p.openInventory(arena.getTeamsInventory());
                        }
                    }
                }
            }
        } else {
            if(e.getClickedBlock() != null) {
                if(e.getClickedBlock().getState() instanceof Sign) {
                    if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        Sign s = (Sign) e.getClickedBlock().getState();
                        BBSign arenaSign = ArenaManager.getInstance().getArenaSign(s);
                        if (arenaSign != null) {
                            arenaSign.getArena().addPlayer(p);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent e) {
        Entity ent = e.getEntity();
        Location loc = e.getLocation();
        for(Entity ent1 : ent.getNearbyEntities(5,5,5)) {
            if(ent1 instanceof Player) {
                Player p = (Player) ent1;
                BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                if(a != null) {
                    BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a,p);
                    if(plot != null) {
                        if(!plot.isLocationInPlot(loc)) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if(a != null) {
            a.removePlayer(p);
        }
    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent e) {
        if(GameManager.isRestrictPlayerMovement()) {
            Vehicle v = e.getVehicle();
            if (v.getPassenger() instanceof Player) {
                Player p = (Player) v.getPassenger();
                BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                if (a != null) {
                    BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                    if (!plot.isLocationInPlot(e.getTo())) {
                        v.teleport(e.getFrom());
                        p.sendMessage(Message.CANT_LEAVE_PLOT.getChatMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent e) {
        for (BBArena a : GameManager.getArenas()) {
            if (a.getLobbyLocation() != null) {
                if (e.getBlock().getWorld().equals(a.getLobbyLocation().getWorld())) {
                    e.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (GameManager.isRestrictPlayerMovement()) {
            Player p = e.getPlayer();
            BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
            if (arena != null) {
                if(arena.getBBArenaState() == BBArenaState.VOTING) {
                    BBPlot votePlot = arena.getCurrentVotingPlot();
                    if(votePlot != null) {
                        if (!votePlot.isLocationInPlot(e.getTo())) {
                            e.setTo(e.getFrom());
                            p.sendMessage(Message.CANT_LEAVE_PLOT.getChatMessage());
                        }
                    }
                } else {
                    BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
                    if (plot != null) {
                        if (!plot.isLocationInPlot(e.getTo())) {
                            e.setTo(e.getFrom());
                            p.sendMessage(Message.CANT_LEAVE_PLOT.getChatMessage());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if(a != null) {
            if(e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL || e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        Location loc = e.getBlockPlaced().getLocation();
        BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        if (arena != null) {
            switch(arena.getBBArenaState()) {
                case LOBBY:
                    e.setCancelled(true);
                    break;
                case INGAME:
                    BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
                    if (plot != null && plot.isLocationInPlot(loc)) {
                        if (e.getBlock().getType() == Material.CROPS || e.getBlock().getType() == Material.MELON_STEM || e.getBlock().getType() == Material.PUMPKIN_STEM) {
                            if (GameManager.isAutomaticGrow()) {
                                e.getBlock().setData(CropState.RIPE.getData());
                            }
                        }
                        if (BBParticle.getBBParticle(e.getItemInHand()) == null) {
                            BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);
                            if (stats != null) {
                                stats.setBlocksPlaced(stats.getBlocksPlaced() + 1);
                            }
                        } else {
                            e.setCancelled(true);
                        }
                    } else {
                        p.sendMessage(Message.CANT_BUILD_OUTSIDE.getChatMessage());
                        e.setCancelled(true);
                    }
                    break;
                case VOTING:
                    e.setCancelled(true);
                    break;
                case ENDING:
                    e.setCancelled(true);
                    break;
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        Player p = e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if(a != null) {
            BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a,p);
            if(plot != null) {
                if(!plot.isLocationInPlot(e.getBlockClicked().getLocation())) {
                    p.sendMessage(Message.CANT_BUILD_OUTSIDE.getChatMessage());
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignCreate(SignChangeEvent e) {
        Player p = e.getPlayer();
        if(e.getLine(0).equalsIgnoreCase("[bb]")) {
            if(p.hasPermission("buildbattlepro.create")) {
                BBArena arena = ArenaManager.getInstance().getArena(e.getLine(1));
                if(arena != null) {
                    BuildBattle.getFileManager().getConfig("signs.yml").get().createSection(arena.getName() + "." + LocationUtil.getStringFromLocation(e.getBlock().getLocation()));
                    BuildBattle.getFileManager().getConfig("signs.yml").save();
                    //BBSign constructor already adds this sign into BBArena signs and update it.
                    BBSign sign = new BBSign(arena, e.getBlock().getLocation());
                    p.sendMessage(GameManager.getPrefix() + " §aSign for arena §e" + arena.getName() + "§a successfully created!");
                } else {
                    p.sendMessage(Message.ARENA_NOT_EXISTS.getChatMessage());
                    e.setCancelled(true);
                    e.getBlock().breakNaturally();
                    return;
                }
            } else {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                e.setCancelled(true);
                e.getBlock().breakNaturally();
            }
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        Location loc = e.getBlock().getLocation();
        BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        Block block = e.getBlock();
        if(arena != null) {
            switch (arena.getBBArenaState()) {
                case LOBBY:
                    e.setCancelled(true);
                    break;
                case INGAME:
                    BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
                    if (!plot.isLocationInPlot(loc)) {
                        p.sendMessage(Message.CANT_BUILD_OUTSIDE.getChatMessage());
                        e.setCancelled(true);
                    }
                    break;
                case VOTING:
                    e.setCancelled(true);
                    break;
                case ENDING:
                    e.setCancelled(true);
                    break;
            }
        } else {
            if(block.getState() instanceof Sign) {
                Sign s = (Sign) e.getBlock().getState();
                BBSign bbSign = ArenaManager.getInstance().getArenaSign(s);
                if(bbSign != null) {
                    if(p.hasPermission("buildbattlepro.create")) {
                        bbSign.removeSign(p);
                    } else {
                        e.setCancelled(true);
                    }
                }
                return;
            }
        }
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
            if (arena != null) {
                e.setCancelled(true);
                p.setHealth(p.getMaxHealth());
                p.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onWaterFlowEvent(BlockFromToEvent e) {
        BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if(plot != null) {
            if(!plot.isLocationInPlot(e.getToBlock().getLocation())) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTntExplode(EntityExplodeEvent e) {
        BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getLocation());
        if(plot != null) {
            e.getEntity().getLocation().getBlock().setType(Material.AIR);
            e.blockList().clear();
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            BBArena a = PlayerManager.getInstance().getPlayerArena(p);
            if (a != null) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            if (e.getEntity() instanceof ArmorStand) {
                Player p = (Player) e.getDamager();
                BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                if (a != null) {
                    if (a.getBBArenaState() == BBArenaState.VOTING) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void LeaveDecay(LeavesDecayEvent e) {
        BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            if (plot.isInPlotRange(e.getBlock().getLocation(), 5)) {
                e.setCancelled(true);
            }
        }
    }


    @EventHandler
    public void onTreeGrow(StructureGrowEvent e) {
        Player p = e.getPlayer();
        BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        if(arena != null) {
            BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena,p);
            if(plot != null) {
                for (BlockState blockState : e.getBlocks()) {
                    if (!plot.isLocationInPlot(blockState.getLocation()))
                        blockState.setType(Material.AIR);
                }
            }
        }
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if(a != null && a.getBBArenaState() == BBArenaState.VOTING) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onChatIngame(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if(a != null) {
            e.getRecipients().clear();
            for(Player p1 : a.getPlayers()) {
                e.getRecipients().add(p1);
            }
        }
    }

    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if(a != null) {
            if(p.hasPermission("buildbattlepro.bypass")) {
                return;
            } else {
                if(e.getMessage().contains("bb") || e.getMessage().contains("buildbattle") || e.getMessage().contains("settheme")) {
                    return;
                }
                boolean valid = false;
                for(String cmd : GameManager.getAllowedCommands()) {
                    if(e.getMessage().contains(cmd)) {
                        valid = true;
                        break;
                    }
                }
                if(!valid) {
                    p.sendMessage(Message.COMMANDS_NOT_ALLOWED.getChatMessage());
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onDispense(BlockDispenseEvent e) {
        BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            if (!plot.isInPlotRange(e.getBlock().getLocation(), -1) && plot.isInPlotRange(e.getBlock().getLocation(), 5)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonExtendEvent(BlockPistonExtendEvent e) {
        BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            for (Block block : e.getBlocks()) {
                if (!plot.isInPlotRange(block.getLocation(), -1) && plot.isLocationInPlot(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                }
            }
        }
    }
}
