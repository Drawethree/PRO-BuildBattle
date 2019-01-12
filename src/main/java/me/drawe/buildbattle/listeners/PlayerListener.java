package me.drawe.buildbattle.listeners;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.heads.Category;
import me.drawe.buildbattle.heads.HeadInventory;
import me.drawe.buildbattle.managers.*;
import me.drawe.buildbattle.objects.GUIItem;
import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.PlotBiome;
import me.drawe.buildbattle.objects.Votes;
import me.drawe.buildbattle.objects.bbobjects.*;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaEdit;
import me.drawe.buildbattle.objects.bbobjects.arena.BBArenaState;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlotParticle;
import me.drawe.buildbattle.objects.bbobjects.plot.BBPlotTime;
import me.drawe.buildbattle.utils.BungeeUtils;
import me.drawe.buildbattle.utils.LocationUtil;
import me.drawe.buildbattle.utils.compatbridge.model.CompMaterial;
import me.drawe.buildbattle.utils.compatbridge.model.CompSound;
import me.drawe.buildbattle.utils.compatbridge.model.CompatBridge;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class PlayerListener implements Listener {

    private final BuildBattle plugin;

    public PlayerListener(BuildBattle buildBattle) {
        this.plugin = buildBattle;
    }

    @EventHandler
    public void onPreJoin(final AsyncPlayerPreLoginEvent e) {
        if (BBSettings.isUseBungeecord() && BBSettings.isAutoJoinPlayers()) {
            final BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(null);
            if (arena == null) {
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                e.setKickMessage(Message.NO_EMPTY_ARENA.getChatMessage());
            }
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        if (BBSettings.isCreateStatsOnServerJoin()) {
            PlayerManager.getInstance().createPlayerStatsIfNotExists(p);
        }

        if (BBSettings.isUseBungeecord() && BBSettings.isAutoJoinPlayers()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
                final BBArena arena = ArenaManager.getInstance().getArenaToAutoJoin(null);
                if (arena != null) {
                    arena.addPlayer(p);
                } else {
                    p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                    BungeeUtils.connectPlayerToServer(p, BBSettings.getRandomFallbackServer());
                }
            }, 1L);
        } else if (BBSettings.getMainLobbyLocation() != null && BBSettings.isTeleportToMainLobbyOnJoin()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(BuildBattle.getInstance(), () -> {
                PlayerManager.getInstance().teleportToMainLobby(p);
            }, 1L);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory clickedInventory = e.getClickedInventory();
        Inventory inv = e.getInventory();
        BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (inv != null) {
            if (inv.getTitle().equalsIgnoreCase(ArenaManager.getAllArenasInventory().getTitle()) || inv.getTitle().equalsIgnoreCase(ArenaManager.getTeamArenasInventory().getTitle()) || inv.getTitle().equalsIgnoreCase(ArenaManager.getSoloArenasInventory().getTitle())) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                    BBArena clickedArena = ArenaManager.getInstance().getArena(e.getCurrentItem().getItemMeta().getDisplayName());
                    if (a == null) {
                        if (clickedArena != null) {
                            clickedArena.addPlayer(p);
                        }
                    } else {
                        p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                    }
                }
                return;
            } else if (inv.equals(ArenaManager.getInstance().getEditArenasInventory())) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    BBArenaEdit clickedEdit = ArenaManager.getInstance().getArenaEdit(e.getCurrentItem());
                    if (clickedEdit != null) {
                        p.openInventory(clickedEdit.getEditInventory());
                        p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                    }
                }
            } else if (inv.getTitle().contains("Editing Arena: ")) {
                e.setCancelled(true);
                BBArenaEdit currentEdit = ArenaManager.getInstance().getArenaEdit(inv);
                if (currentEdit != null) {
                    if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                        boolean edited = false;
                        if (e.getCurrentItem().equals(currentEdit.getGameModeItem())) {
                            edited = currentEdit.editGameMode();
                        } else if (e.getCurrentItem().equals(currentEdit.getGameTimeItem())) {
                            edited = currentEdit.editGameTime(e.getClick());
                        } else if (e.getCurrentItem().equals(currentEdit.getMinPlayersItem())) {
                            edited = currentEdit.editMinPlayers(e.getClick());
                        } else if (e.getCurrentItem().equals(currentEdit.getTeamSizeItem())) {
                            edited = currentEdit.editTeamSize(e.getClick());
                        } else if (e.getCurrentItem().equals(OptionsManager.getSaveItem())) {
                            currentEdit.saveOptions();
                            p.playSound(p.getLocation(), CompSound.LEVEL_UP.getSound(), 1.0F, 1.0F);
                            p.openInventory(ArenaManager.getInstance().getEditArenasInventory());
                            return;
                        } else if (e.getCurrentItem().equals(OptionsManager.getBackItem())) {
                            p.openInventory(ArenaManager.getInstance().getEditArenasInventory());
                            p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                            return;
                        } else if (e.getCurrentItem().equals(OptionsManager.getDeleteArenaItem())) {
                            ArenaManager.getInstance().removeArena(p, currentEdit.getArena());
                            p.openInventory(ArenaManager.getInstance().getEditArenasInventory());
                            p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                            return;
                        }

                        if (edited) {
                            p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                        } else {
                            p.playSound(p.getLocation(), CompSound.NOTE_BASS.getSound(), 1.0F, 1.0F);
                        }
                    }
                }
            } else if (inv.getTitle().contains(OptionsManager.getReportsInventoryTitle())) {
                e.setCancelled(true);
                if (e.getCurrentItem().isSimilar(GUIItem.NEXT_PAGE.getItemStack())) {
                    ReportManager.getInstance().openReports(p, ReportManager.getInstance().getNextPage(inv));
                } else if (e.getCurrentItem().isSimilar(GUIItem.PREV_PAGE.getItemStack())) {
                    ReportManager.getInstance().openReports(p, ReportManager.getInstance().getPrevPage(inv));
                } else if (e.getCurrentItem().isSimilar(GUIItem.CLOSE_GUI.getItemStack())) {
                    p.closeInventory();
                } else if (!e.getCurrentItem().isSimilar(GUIItem.FILL_ITEM.getItemStack())) {
                    BBBuildReport clickedReport = ReportManager.getInstance().getReport(e.getCurrentItem());
                    if (clickedReport != null) {
                        switch (e.getClick()) {
                            case LEFT:
                                if (clickedReport.selectSchematic(p)) {
                                    p.sendMessage(BBSettings.getPrefix() + "§aSchematic of report §e" + clickedReport.getReportID() + " §aloaded into your clipboard. Paste it by §e//paste");
                                } else {
                                    p.sendMessage(BBSettings.getPrefix() + "§cThere is some problem with loading schematic for report §e" + clickedReport.getReportID() + " !");
                                }
                                p.closeInventory();
                                break;
                            case RIGHT:
                                if (clickedReport.getReportStatus() == BBReportStatus.PENDING) {
                                    clickedReport.setReportStatus(BBReportStatus.SOLVED);
                                } else {
                                    clickedReport.setReportStatus(BBReportStatus.PENDING);
                                }
                                ReportManager.getInstance().openReports(p, ReportManager.getInstance().getCurrentPage(inv));
                                break;
                            case MIDDLE:
                                if (ReportManager.getInstance().deleteReport(clickedReport)) {
                                    p.sendMessage(BBSettings.getPrefix() + "§aReport deleted !");
                                    ReportManager.getInstance().openReports(p, ReportManager.getInstance().getCurrentPage(inv));
                                } else {
                                    p.sendMessage(BBSettings.getPrefix() + "§cThere is an issue with deleting this report ! Check console.");
                                    p.closeInventory();
                                }
                                break;
                        }
                    }
                }
            }
        }

        if (a != null) {
            if (clickedInventory != null) {
                if (e.getSlotType() == InventoryType.SlotType.ARMOR && e.getCursor() != null) {
                    e.setCancelled(true);
                    p.sendMessage(Message.CANNOT_WEAR_ARMOR.getChatMessage());
                    return;
                }
                if (e.getCurrentItem() != null) {
                    if (e.getCurrentItem().isSimilar(OptionsManager.getBackItem())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            OptionsManager.getInstance().openOptionsInventory(p, plot);
                        }
                        return;
                    }
                }
                if (a.getBBArenaState() != BBArenaState.INGAME) {
                    e.setCancelled(true);
                    if (a.getBBArenaState() == BBArenaState.THEME_VOTING && clickedInventory.getTitle().equalsIgnoreCase(Message.GUI_THEME_VOTING_TITLE.getMessage())) {
                        if (e.getCurrentItem() != null && (e.getCurrentItem().getType() == CompMaterial.SIGN.getMaterial() || e.getCurrentItem().getType() == CompMaterial.PAPER.getMaterial())) {
                            BBTheme selectedTheme = a.getThemeVoting().getThemeBySlot(e.getSlot());
                            if (selectedTheme != null) {
                                if (selectedTheme.isSuperVoteSlotClicked(e.getSlot())) {
                                    if (SuperVoteManager.getInstance().hasSuperVote(p)) {
                                        SuperVoteManager.getInstance().takeSuperVote(p, 1);
                                        a.getThemeVoting().superVote(p, selectedTheme);
                                        return;
                                    } else {
                                        p.sendMessage(Message.NOT_ENOUGH_SUPER_VOTES.getChatMessage());
                                    }
                                } else {
                                    if (a.getThemeVoting().getVotedPlayers().containsKey(p)) {
                                        BBTheme previousVoted = a.getThemeVoting().getVotedPlayers().get(p);
                                        int votes = previousVoted.getVotes();
                                        previousVoted.setVotes(votes - 1);
                                    }
                                    a.getThemeVoting().getVotedPlayers().put(p, selectedTheme);
                                    selectedTheme.setVotes(selectedTheme.getVotes() + 1);
                                }
                            }
                        }
                    } else if (a.getBBArenaState() == BBArenaState.LOBBY) {
                        if (inv.getTitle().equalsIgnoreCase(Message.GUI_TEAMS_TITLE.getMessage())) {
                            if (e.getCurrentItem() != null && (e.getCurrentItem().getType() == CompMaterial.LIME_TERRACOTTA.getMaterial() || e.getCurrentItem().getType() == CompMaterial.RED_TERRACOTTA.getMaterial() || e.getCurrentItem().getType() == CompMaterial.YELLOW_TERRACOTTA.getMaterial())) {
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
                    if (clickedInventory.getTitle().equalsIgnoreCase(Message.GUI_OPTIONS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem().isSimilar(plot.getOptions().getCurrentFloorItem())) {
                                if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                                    if (p.hasPermission("buildbattlepro.changefloor")) {
                                        plot.getOptions().setCurrentFloorItem(e.getCursor());
                                        clickedInventory.setItem(e.getSlot(), plot.getOptions().getCurrentFloorItem());
                                        e.setCursor(null);
                                    } else {
                                        p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                    }
                                }
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getHeadsItem())) {
                                p.openInventory(HeadInventory.getInstance().getMainPage());
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getBannerCreatorItem())) {
                                BBBannerCreator bbBannerCreator = BannerCreatorManager.getInstance().getBannerCreator(p);
                                if (bbBannerCreator == null) {
                                    bbBannerCreator = BannerCreatorManager.getInstance().addBannerCreator(p);
                                }
                                OptionsManager.getInstance().openColorsInventory(bbBannerCreator);
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getBiomesItem())) {
                                p.openInventory(OptionsManager.getBiomesInventory());
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getInstance().getWeatherItemStack(plot))) {
                                if (p.hasPermission("buildbattlepro.changeweather")) {
                                    if (plot.getOptions().getCurrentWeather() == WeatherType.CLEAR) {
                                        plot.getOptions().setCurrentWeather(WeatherType.DOWNFALL, false);
                                    } else {
                                        plot.getOptions().setCurrentWeather(WeatherType.CLEAR, false);
                                    }
                                    clickedInventory.setItem(e.getSlot(), OptionsManager.getInstance().getWeatherItemStack(plot));
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
                            } else if (e.getCurrentItem().isSimilar(OptionsManager.getBiomesItem())) {
                                p.openInventory(OptionsManager.getBiomesInventory());
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
                                        p.getInventory().addItem(e.getCurrentItem());
                                    }
                                }
                            }
                        }
                    } else if (inv.getTitle().equalsIgnoreCase(OptionsManager.getBiomesInventory().getTitle())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem() != null) {
                                PlotBiome selectedBiome = PlotBiome.getBiomeFromItemStack(e.getCurrentItem());
                                if (selectedBiome != null) {
                                    if (p.hasPermission("buildbattlepro.changebiome")) {
                                        plot.getOptions().setCurrentBiome(selectedBiome, true);
                                    } else {
                                        p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                    }
                                }
                            }
                        }
                    } else if (inv.getTitle().equalsIgnoreCase(Message.GUI_PARTICLE_LIST_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLore()) {
                                BBPlotParticle particle = OptionsManager.getInstance().getPlotParticleFromLore(plot, e.getCurrentItem().getItemMeta().getLore());
                                if (particle != null) {
                                    plot.removeActiveParticle(particle);
                                    if (e.getCurrentItem().getAmount() == 1) {
                                        clickedInventory.remove(e.getCurrentItem());
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
                                                    if (head.getData().getItemType() != CompMaterial.AIR.getMaterial()) {
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
                    } else if (inv.getTitle().equalsIgnoreCase(Message.GUI_COLORS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBBannerCreator bannerCreator = BannerCreatorManager.getInstance().getBannerCreator(p);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (bannerCreator != null) {
                                if (e.getCurrentItem() != null) {
                                    BBDyeColor dc = BBDyeColor.getByItem(e.getCurrentItem());
                                    if (dc != null) {
                                        bannerCreator.selectColor(dc.getColor());
                                        return;
                                    } else if (e.getCurrentItem().equals(bannerCreator.getCreatedBanner())) {
                                        bannerCreator.giveItem();
                                        p.closeInventory();
                                    } else if (e.getCurrentItem().equals(OptionsManager.getBackItem())) {
                                        OptionsManager.getInstance().openOptionsInventory(p, plot);
                                    }
                                }
                            }
                        }
                    } else if (inv.getTitle().equalsIgnoreCase(Message.GUI_PATTERNS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBBannerCreator bannerCreator = BannerCreatorManager.getInstance().getBannerCreator(p);
                        BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (bannerCreator != null) {
                                if (e.getCurrentItem() != null) {
                                    if (e.getCurrentItem().getType() == CompMaterial.WHITE_BANNER.getMaterial() && (!e.getCurrentItem().equals(bannerCreator.getCreatedBanner()))) {
                                        BannerMeta meta = (BannerMeta) e.getCurrentItem().getItemMeta();
                                        bannerCreator.addPattern(meta.getPatterns().get(0).getPattern());
                                    } else if (e.getCurrentItem().equals(bannerCreator.getCreatedBanner())) {
                                        bannerCreator.giveItem();
                                        p.closeInventory();
                                    } else if (e.getCurrentItem().equals(OptionsManager.getBackItem())) {
                                        OptionsManager.getInstance().openOptionsInventory(p, plot);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onVote(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        if (arena != null) {
            if ((e.getItem() != null) && (e.getItem().getType() == CompMaterial.COMPASS.getMaterial())) {
                e.setCancelled(true);
                return;
            }
            if (arena.getBBArenaState() == BBArenaState.VOTING) {
                if (e.getItem() != null) {
                    if (e.getItem().isSimilar(OptionsManager.getReportItem())) {
                        ReportManager.getInstance().attemptReport(arena.getCurrentVotingPlot(), p);
                        return;
                    }
                    Votes vote = Votes.getVoteByItemStack(e.getItem());
                    if (vote != null) {
                        BBPlot currentPlot = arena.getCurrentVotingPlot();
                        if (currentPlot != null) {
                            if (!currentPlot.getTeam().getPlayers().contains(p)) {
                                VotingManager.getInstance().vote(p, vote, currentPlot);
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
                                OptionsManager.getInstance().openOptionsInventory(p, plot);
                            }
                        } else if (BBParticle.getBBParticle(e.getItem()) != null) {
                            e.setCancelled(true);
                            if (plot.isLocationInPlot(p.getLocation())) {
                                BBParticle particle = BBParticle.getBBParticle(e.getItem());
                                if (p.hasPermission(particle.getRequiredPermission())) {
                                    plot.addActiveParticle(p, new BBPlotParticle(plot, particle, p.getLocation()));
                                } else {
                                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                }
                            } else {
                                p.sendMessage(Message.CANT_PLACE_PARTICLE_OUTSIDE.getChatMessage());
                            }
                        }
                    }
                }
            } else if (arena.getBBArenaState() == BBArenaState.LOBBY) {
                if (e.getItem() != null) {
                    if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        if (e.getItem().isSimilar(OptionsManager.getLeaveItem())) {
                            e.setCancelled(true);
                            arena.removePlayer(p);
                        } else if (e.getItem().isSimilar(OptionsManager.getTeamsItem())) {
                            e.setCancelled(true);
                            p.openInventory(arena.getTeamsInventory());
                        }
                    }
                }
            }
        } else {
            if (e.getClickedBlock() != null) {
                if (e.getItem() != null && e.getItem().isSimilar(ArenaManager.posSelectorItem)) {
                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);
                        ArenaManager.getInstance().setPos(p, e.getClickedBlock(), 2);
                    } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        e.setCancelled(true);
                        ArenaManager.getInstance().setPos(p, e.getClickedBlock(), 1);
                    }
                    return;
                }
                if (e.getClickedBlock().getState() instanceof Sign && e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    Sign s = (Sign) e.getClickedBlock().getState();
                    BBSign arenaSign = ArenaManager.getInstance().getArenaSign(s);
                    if (arenaSign != null) {
                        arenaSign.getArena().addPlayer(p);
                    } else {
                        BBArena arenaToAutoJoin = null;
                        if (s.getLine(0).equals(Message.SIGN_AUTO_JOIN_FIRST_LINE.getMessage()) && s.getLine(1).equals(Message.SIGN_AUTO_JOIN_SECOND_LINE.getMessage()) && s.getLine(2).equals(Message.SIGN_AUTO_JOIN_THIRD_LINE.getMessage()) && s.getLine(3).equals(Message.SIGN_AUTO_JOIN_FOURTH_LINE.getMessage())) {
                            arenaToAutoJoin = ArenaManager.getInstance().getArenaToAutoJoin(null);
                        } else if (s.getLine(0).equals(Message.SIGN_AUTO_JOIN_SOLO_FIRST_LINE.getMessage()) && s.getLine(1).equals(Message.SIGN_AUTO_JOIN_SOLO_SECOND_LINE.getMessage()) && s.getLine(2).equals(Message.SIGN_AUTO_JOIN_SOLO_THIRD_LINE.getMessage()) && s.getLine(3).equals(Message.SIGN_AUTO_JOIN_SOLO_FOURTH_LINE.getMessage())) {
                            arenaToAutoJoin = ArenaManager.getInstance().getArenaToAutoJoin(BBGameMode.SOLO);
                        } else if (s.getLine(0).equals(Message.SIGN_AUTO_JOIN_TEAM_FIRST_LINE.getMessage()) && s.getLine(1).equals(Message.SIGN_AUTO_JOIN_TEAM_SECOND_LINE.getMessage()) && s.getLine(2).equals(Message.SIGN_AUTO_JOIN_TEAM_THIRD_LINE.getMessage()) && s.getLine(3).equals(Message.SIGN_AUTO_JOIN_TEAM_FOURTH_LINE.getMessage())) {
                            arenaToAutoJoin = ArenaManager.getInstance().getArenaToAutoJoin(BBGameMode.TEAM);
                        }
                        if (arenaToAutoJoin != null) {
                            arenaToAutoJoin.addPlayer(p);
                        }
                    }
                }
            }
        }
    }


    @EventHandler
    public void onEntitySpawn(final EntitySpawnEvent e) {
        final Entity ent = e.getEntity();
        final Location loc = e.getLocation();
        for (Entity ent1 : ent.getNearbyEntities(5, 5, 5)) {
            if (ent1 instanceof Player) {
                final Player p = (Player) ent1;
                final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                if (a != null) {
                    final BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                    if (plot != null) {
                        if (!plot.isLocationInPlot(loc)) {
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null) {
            a.removePlayer(p);
        }
    }

    @EventHandler
    public void onVehicleMove(final VehicleMoveEvent e) {
        if (BBSettings.isRestrictPlayerMovement()) {
            final Vehicle v = e.getVehicle();
            if (v.getPassenger() instanceof Player) {
                final Player p = (Player) v.getPassenger();
                final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
                if (a != null) {
                    final BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
                    if (!plot.isLocationInPlot(e.getTo())) {
                        v.teleport(e.getFrom());
                        p.sendMessage(Message.CANT_LEAVE_PLOT.getChatMessage());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onIgnite(final BlockIgniteEvent e) {
        for (BBArena a : ArenaManager.getArenas().values()) {
            if (a.getLobbyLocation() != null) {
                if (e.getBlock().getWorld().equals(a.getLobbyLocation().getWorld())) {
                    e.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onMove(final PlayerMoveEvent e) {
        if (BBSettings.isRestrictPlayerMovement() || BBSettings.isRestrictOnlyPlayerYMovement()) {
            final Player p = e.getPlayer();
            final BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
            if (arena != null && arena.getBBArenaState() != BBArenaState.LOBBY) {
                BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
                switch (arena.getBBArenaState()) {
                    case VOTING:
                        plot = arena.getCurrentVotingPlot();
                        break;
                    case ENDING:
                        plot = arena.getWinner();
                        break;
                }
                if (plot != null) {
                    if ((BBSettings.isRestrictPlayerMovement() && !plot.isLocationInPlot(e.getTo())) || (BBSettings.isRestrictOnlyPlayerYMovement() && plot.getMaxPoint().getBlockY() <= e.getTo().getBlockY())) {
                        e.setTo(e.getFrom());
                    }
                }
            }
        }
    }


    @EventHandler
    public void onTeleport(final PlayerTeleportEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null && (e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) || (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            e.setCancelled(true);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        final Location loc = e.getBlockPlaced().getLocation();
        final BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        if (arena != null) {
            switch (arena.getBBArenaState()) {
                case LOBBY:
                case THEME_VOTING:
                case VOTING:
                case ENDING:
                    e.setCancelled(true);
                    break;
                case INGAME:
                    BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
                    if (plot != null && plot.isLocationInPlot(loc)) {
                        if (BBSettings.isAutomaticGrow() && (e.getBlock().getType() == CompMaterial.WHEAT_SEEDS.getMaterial() || e.getBlock().getType() == CompMaterial.MELON_STEM.getMaterial() || e.getBlock().getType() == CompMaterial.PUMPKIN_STEM.getMaterial())) {
                            CompatBridge.setData(e.getBlock(), (byte) 4);
                        }
                        if (BBParticle.getBBParticle(e.getItemInHand()) == null) {
                            final BBPlayerStats stats = PlayerManager.getInstance().getPlayerStats(p);
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
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(final PlayerBucketEmptyEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null) {
            final BBPlot plot = ArenaManager.getInstance().getPlayerPlot(a, p);
            if (plot != null) {
                System.out.println(plot.isLocationInPlot(e.getBlockClicked().getLocation()));
                System.out.println(plot.isLocationInPlot(e.getBlockClicked().getLocation().clone().add(0, 1, 0)));
                if (!plot.isLocationInPlot(e.getBlockClicked().getLocation()) || !plot.isLocationInPlot(e.getBlockClicked().getLocation().clone().add(0, 1, 0))) {
                    p.sendMessage(Message.CANT_BUILD_OUTSIDE.getChatMessage());
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onSignCreate(final SignChangeEvent e) {
        final Player p = e.getPlayer();
        if (e.getLine(0).equalsIgnoreCase("[bb]")) {
            if (p.hasPermission("buildbattlepro.create")) {
                final BBArena arena = ArenaManager.getInstance().getArena(e.getLine(1));
                if (arena != null) {
                    arena.getArenaSigns().add(new BBSign(arena, e.getBlock().getLocation()));
                    BuildBattle.getFileManager().getConfig("signs.yml").get().createSection(arena.getName() + "." + LocationUtil.getStringFromLocationXYZ(e.getBlock().getLocation()));
                    BuildBattle.getFileManager().getConfig("signs.yml").save();
                    p.sendMessage(BBSettings.getPrefix() + "§aJoin sign for arena §e" + arena.getName() + "§a successfully created!");
                    return;
                } else if (e.getLine(1).equalsIgnoreCase("autojoin")) {
                    if (e.getLine(2).equalsIgnoreCase("team")) {
                        e.setLine(0, Message.SIGN_AUTO_JOIN_TEAM_FIRST_LINE.getMessage());
                        e.setLine(1, Message.SIGN_AUTO_JOIN_TEAM_SECOND_LINE.getMessage());
                        e.setLine(2, Message.SIGN_AUTO_JOIN_TEAM_THIRD_LINE.getMessage());
                        e.setLine(3, Message.SIGN_AUTO_JOIN_TEAM_FOURTH_LINE.getMessage());
                        p.sendMessage(BBSettings.getPrefix() + "§aTeam Auto-Join sign successfully created!");
                    } else if (e.getLine(2).equalsIgnoreCase("solo")) {
                        e.setLine(0, Message.SIGN_AUTO_JOIN_SOLO_FIRST_LINE.getMessage());
                        e.setLine(1, Message.SIGN_AUTO_JOIN_SOLO_SECOND_LINE.getMessage());
                        e.setLine(2, Message.SIGN_AUTO_JOIN_SOLO_THIRD_LINE.getMessage());
                        e.setLine(3, Message.SIGN_AUTO_JOIN_SOLO_FOURTH_LINE.getMessage());
                        p.sendMessage(BBSettings.getPrefix() + "§aSolo Auto-Join sign successfully created!");
                    } else {
                        e.setLine(0, Message.SIGN_AUTO_JOIN_FIRST_LINE.getMessage());
                        e.setLine(1, Message.SIGN_AUTO_JOIN_SECOND_LINE.getMessage());
                        e.setLine(2, Message.SIGN_AUTO_JOIN_THIRD_LINE.getMessage());
                        e.setLine(3, Message.SIGN_AUTO_JOIN_FOURTH_LINE.getMessage());
                        p.sendMessage(BBSettings.getPrefix() + "§aAuto-Join sign successfully created!");
                        return;
                    }
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
    public void onBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        final Location loc = e.getBlock().getLocation();
        final BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        final Block block = e.getBlock();
        if (arena != null) {
            switch (arena.getBBArenaState()) {
                case LOBBY:
                case THEME_VOTING:
                case VOTING:
                case ENDING:
                    e.setCancelled(true);
                    break;
                case INGAME:
                    BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
                    if (plot != null && !plot.isLocationInPlot(loc)) {
                        p.sendMessage(Message.CANT_BUILD_OUTSIDE.getChatMessage());
                        e.setCancelled(true);
                    }
                    break;
            }
        } else {
            if (block.getState() instanceof Sign) {
                final Sign s = (Sign) e.getBlock().getState();
                final BBSign bbSign = ArenaManager.getInstance().getArenaSign(s);
                if (bbSign != null) {
                    if (p.hasPermission("buildbattlepro.create")) {
                        bbSign.removeSign(p);
                    } else {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFoodChange(final FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player) e.getEntity();
            final BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
            if (arena != null) {
                e.setCancelled(true);
                p.setHealth(p.getMaxHealth());
                p.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onWaterFlowEvent(final BlockFromToEvent e) {
        final BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onTntExplode(final EntityExplodeEvent e) {
        final BBPlot plot = ArenaManager.getInstance().getBBPlotFromNearbyLocation(e.getLocation());
        if (plot != null) {
            e.setCancelled(true);
            e.getEntity().getLocation().getBlock().setType(CompMaterial.AIR.getMaterial());
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onDamage(final EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            final Player p = (Player) e.getEntity();
            final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
            if (a != null) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            final Player p = (Player) e.getDamager();
            final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
            if (a != null && a.getBBArenaState() == BBArenaState.VOTING) {
                e.setCancelled(true);
            }
        }
    }

    /*@EventHandler
    public void LeaveDecay(LeavesDecayEvent e) {
        BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            if (plot.isInPlotRange(e.getBlock().getLocation(), 5)) {
                e.setCancelled(true);
            }
        }
    }
    */


    @EventHandler
    public void onTreeGrow(final StructureGrowEvent e) {
        final Player p = e.getPlayer();
        final BBArena arena = PlayerManager.getInstance().getPlayerArena(p);
        if (arena != null) {
            final BBPlot plot = ArenaManager.getInstance().getPlayerPlot(arena, p);
            if (plot != null) {
                for (BlockState blockState : e.getBlocks()) {
                    if (!plot.isLocationInPlot(blockState.getLocation()))
                        blockState.setType(CompMaterial.AIR.getMaterial());
                }
            }
        }
    }

    @EventHandler
    public void onPickup(final PlayerPickupItemEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null && a.getBBArenaState() == BBArenaState.VOTING) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatIngame(final AsyncPlayerChatEvent e) {
        if (BBSettings.isArenaChat() || BBSettings.isTeamChat()) {
            final Player p = e.getPlayer();
            final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
            if (a != null) {
                if (a.getGameType() == BBGameMode.TEAM) {
                    if (BBSettings.isTeamChat()) {
                        e.getRecipients().clear();
                        if (e.getMessage().charAt(0) == '!') {
                            e.setMessage(e.getMessage().substring(1, e.getMessage().length()));
                            for (Player p1 : a.getPlayers()) {
                                e.getRecipients().add(p1);
                            }
                        } else {
                            e.getRecipients().add(p);
                            for (Player p1 : a.getTeamMates(p)) {
                                e.getRecipients().add(p1);
                            }
                        }
                    } else if (BBSettings.isArenaChat()) {
                        e.getRecipients().clear();
                        for (Player p1 : a.getPlayers()) {
                            e.getRecipients().add(p1);
                        }
                    }
                } else {
                    if (BBSettings.isArenaChat()) {
                        e.getRecipients().clear();
                        for (Player p1 : a.getPlayers()) {
                            e.getRecipients().add(p1);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null) {
            if (p.hasPermission("buildbattlepro.bypass")) {
                return;
            } else {
                if (e.getMessage().contains("bb") || e.getMessage().contains("buildbattle") || e.getMessage().contains("settheme")) {
                    return;
                }

                boolean valid = false;

                for (String cmd : BBSettings.getAllowedCommands()) {
                    if (e.getMessage().contains(cmd)) {
                        valid = true;
                        break;
                    }
                }

                if (!valid) {
                    p.sendMessage(Message.COMMANDS_NOT_ALLOWED.getChatMessage());
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onDispense(final BlockDispenseEvent e) {
        final BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            if (!plot.isInPlotRange(e.getBlock().getLocation(), -1) && plot.isInPlotRange(e.getBlock().getLocation(), 5)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonExtendEvent(final BlockPistonExtendEvent e) {
        final BBPlot plot = ArenaManager.getInstance().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            for (Block block : e.getBlocks()) {
                if (!plot.isInPlotRange(block.getLocation(), -1) && plot.isLocationInPlot(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onConsume(final PlayerItemConsumeEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = PlayerManager.getInstance().getPlayerArena(p);
        if (a != null) {
            e.setCancelled(true);
        }
    }
}
