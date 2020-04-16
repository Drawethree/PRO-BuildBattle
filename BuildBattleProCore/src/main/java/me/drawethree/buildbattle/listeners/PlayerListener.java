package me.drawethree.buildbattle.listeners;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.hooks.BBHook;
import me.drawethree.buildbattle.hooks.BBHookWorldEdit;
import me.drawethree.buildbattle.objects.GUIItem;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.PlotBiome;
import me.drawethree.buildbattle.objects.Votes;
import me.drawethree.buildbattle.objects.bbobjects.*;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArenaState;
import me.drawethree.buildbattle.objects.bbobjects.arena.editor.BBArenaEdit;
import me.drawethree.buildbattle.objects.bbobjects.arena.editor.options.BBArenaEditOption;
import me.drawethree.buildbattle.objects.bbobjects.gui.ArenaDeleteGUI;
import me.drawethree.buildbattle.objects.bbobjects.gui.ClearPlotGUI;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlotParticle;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlotTime;
import me.drawethree.buildbattle.objects.bbobjects.sign.BBArenaJoinSign;
import me.drawethree.buildbattle.objects.bbobjects.sign.BBArenaSpectateSign;
import me.drawethree.buildbattle.objects.bbobjects.sign.BBAutoJoinSign;
import me.drawethree.buildbattle.objects.bbobjects.sign.BBSign;
import me.drawethree.buildbattle.utils.BungeeUtils;
import me.drawethree.buildbattle.utils.compatbridge.model.CompMaterial;
import me.drawethree.buildbattle.utils.compatbridge.model.CompSound;
import me.drawethree.buildbattle.utils.compatbridge.model.CompatBridge;
import me.drawethree.headsapi.Category;
import me.drawethree.headsapi.HeadInventory;
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
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class PlayerListener implements Listener {

    private final BuildBattle plugin;

    public PlayerListener(BuildBattle buildBattle) {
        this.plugin = buildBattle;
    }

    @EventHandler
    public void onPreJoin(final AsyncPlayerPreLoginEvent e) {
        if (this.plugin.getSettings().isUseBungeecord() && this.plugin.getSettings().isAutoJoinPlayers()) {
            BBArena arena = this.plugin.getArenaManager().getArenaToAutoJoin(null);
            if (arena == null) {

                if (this.plugin.getSettings().isAutoJoinSpectate()) {
                    arena = this.plugin.getArenaManager().getArenaToAutoSpectate();
                }

                if (arena == null) {
                    e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                    e.setKickMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                }
            }
        }
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();

        this.plugin.getPlayerManager().loadPlayerData(p);

        if (this.plugin.getSettings().isUseBungeecord() && this.plugin.getSettings().isAutoJoinPlayers()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                BBArena arena = this.plugin.getArenaManager().getArenaToAutoJoin(null);
                if (arena != null) {
                    arena.addPlayer(p);
                } else {
                    if (this.plugin.getSettings().isAutoJoinSpectate()) {
                        arena = this.plugin.getArenaManager().getArenaToAutoSpectate();
                        if (arena != null) {
                            this.plugin.getSpectatorManager().spectate(p, arena);
                            return;
                        }
                    }

                    p.sendMessage(Message.NO_EMPTY_ARENA.getChatMessage());
                    BungeeUtils.connectPlayerToServer(p, this.plugin.getSettings().getRandomFallbackServer());

                }
            }, 1L);
        } else if (this.plugin.getSettings().getMainLobbyLocation() != null && this.plugin.getSettings().isTeleportToMainLobbyOnJoin()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> {
                this.plugin.getPlayerManager().teleportToMainLobby(p);

            }, 1L);
        }
        if (plugin.getSettings().isMainLobbyScoreboardEnabled()) {
            plugin.getPlayerManager().showMainLobbyScoreboard(p);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory clickedInventory = e.getClickedInventory();
        Inventory inv = e.getInventory();
        InventoryView invView = e.getView();
        BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);

        if (inv != null) {
            if (invView.getTitle().equalsIgnoreCase(Message.GUI_ARENA_LIST_TITLE.getMessage()) || invView.getTitle().equalsIgnoreCase(Message.GUI_ARENA_LIST_TEAM_TITLE.getMessage()) || invView.getTitle().equalsIgnoreCase(Message.GUI_ARENA_LIST_SOLO_TITLE.getMessage())) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                    BBArena clickedArena = this.plugin.getArenaManager().getArena(e.getCurrentItem().getItemMeta().getDisplayName());
                    if (a == null) {
                        if (clickedArena != null) {
                            clickedArena.addPlayer(p);
                        }
                    } else {
                        p.sendMessage(Message.ALREADY_IN_ARENA.getChatMessage());
                    }
                }
                return;
            } else if (inv.equals(this.plugin.getArenaManager().getEditArenasInventory())) {
                e.setCancelled(true);
                if (e.getCurrentItem() != null) {
                    BBArenaEdit clickedEdit = this.plugin.getArenaManager().getArenaEdit(e.getCurrentItem());
                    if (clickedEdit != null) {
                        p.openInventory(clickedEdit.getEditInventory());
                        p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                    }
                }
            } else if (invView.getTitle().contains("Editing Arena: ")) {
                e.setCancelled(true);
                BBArenaEdit currentEdit = this.plugin.getArenaManager().getArenaEdit(inv);
                if (currentEdit != null) {
                    if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()) {
                        BBArenaEditOption selectedOption = currentEdit.getOption(e.getSlot());
                        if (selectedOption != null) {
                            if (selectedOption.handleClick(e.getClick())) {
                                p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                                currentEdit.refreshGUI();
                            } else {
                                p.playSound(p.getLocation(), CompSound.NOTE_BASS.getSound(), 1.0F, 1.0F);
                            }
                        } else if (e.getCurrentItem().equals(this.plugin.getOptionsManager().getSaveItem())) {
                            currentEdit.saveOptions();
                            p.playSound(p.getLocation(), CompSound.LEVEL_UP.getSound(), 1.0F, 1.0F);
                            p.openInventory(this.plugin.getArenaManager().getEditArenasInventory());
                            return;
                        } else if (e.getCurrentItem().equals(this.plugin.getOptionsManager().getBackItem())) {
                            p.openInventory(this.plugin.getArenaManager().getEditArenasInventory());
                            p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                            return;
                        } else if (e.getCurrentItem().equals(this.plugin.getOptionsManager().getDeleteArenaItem())) {
                            p.openInventory(new ArenaDeleteGUI(currentEdit).getInventory());
                            p.playSound(p.getLocation(), CompSound.CLICK.getSound(), 1.0F, 1.0F);
                            return;
                        }

                    }
                }
            } else if (invView.getTitle().contains(this.plugin.getOptionsManager().getReportsInventoryTitle())) {
                e.setCancelled(true);

                if (e.getCurrentItem() == null) {
                    return;
                }

                BBHookWorldEdit hookWorldEdit = (BBHookWorldEdit) BBHook.getHookInstance("WorldEdit");
                if (hookWorldEdit == null || !hookWorldEdit.isEnabled()) {
                    return;
                }

                if (e.getCurrentItem().isSimilar(GUIItem.NEXT_PAGE.getItemStack())) {
                    hookWorldEdit.getReportManager().openReports(p, hookWorldEdit.getReportManager().getNextPage(invView));
                } else if (e.getCurrentItem().isSimilar(GUIItem.PREV_PAGE.getItemStack())) {
                    hookWorldEdit.getReportManager().openReports(p, hookWorldEdit.getReportManager().getPrevPage(invView));
                } else if (e.getCurrentItem().isSimilar(GUIItem.CLOSE_GUI.getItemStack())) {
                    p.closeInventory();
                } else if (!e.getCurrentItem().isSimilar(GUIItem.FILL_ITEM.getItemStack())) {
                    BBBuildReport clickedReport = hookWorldEdit.getReportManager().getReport(e.getCurrentItem());
                    if (clickedReport != null) {
                        switch (e.getClick()) {
                            case LEFT:
                                clickedReport.pasteSchematic(p);
                                p.closeInventory();
                                break;
                            case RIGHT:
                                if (clickedReport.getReportStatus() == BBReportStatus.PENDING) {
                                    clickedReport.setReportStatus(BBReportStatus.SOLVED);
                                } else {
                                    clickedReport.setReportStatus(BBReportStatus.PENDING);
                                }
                                hookWorldEdit.getReportManager().openReports(p, hookWorldEdit.getReportManager().getCurrentPage(invView));
                                break;
                            case MIDDLE:
                                if (hookWorldEdit.getReportManager().deleteReport(clickedReport)) {
                                    p.sendMessage(this.plugin.getSettings().getPrefix() + "§aReport deleted !");
                                    hookWorldEdit.getReportManager().openReports(p, hookWorldEdit.getReportManager().getCurrentPage(invView));
                                } else {
                                    p.sendMessage(this.plugin.getSettings().getPrefix() + "§cThere is an issue with deleting this report ! Check console.");
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
                    if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getBackItem())) {
                        e.setCancelled(true);
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
                        if (plot != null) {
                            this.plugin.getOptionsManager().openOptionsInventory(p, plot);
                        }
                        return;
                    }
                }
                if (a.getBBArenaState() != BBArenaState.INGAME) {
                    e.setCancelled(true);
                    if (a.getBBArenaState() == BBArenaState.THEME_VOTING && invView.getTitle().equalsIgnoreCase(Message.GUI_THEME_VOTING_TITLE.getMessage())) {
                        if (e.getCurrentItem() != null && (e.getCurrentItem().getType() == CompMaterial.OAK_SIGN.getMaterial() || e.getCurrentItem().getType() == CompMaterial.PAPER.getMaterial())) {
                            BBTheme selectedTheme = a.getThemeVoting().getThemeBySlot(e.getSlot());
                            if (selectedTheme != null) {
                                if (selectedTheme.isSuperVoteSlotClicked(e.getSlot())) {
                                    if (this.plugin.getSuperVoteManager().hasSuperVote(p)) {
                                        this.plugin.getSuperVoteManager().takeSuperVote(p, 1);
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
                        if (invView.getTitle().equalsIgnoreCase(Message.GUI_TEAMS_TITLE.getMessage())) {
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
                    if (e.getCurrentItem() != null && e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getOptionsItem())) {
                        e.setCancelled(true);
                        return;
                    }
                    if (invView.getTitle().equalsIgnoreCase(Message.GUI_OPTIONS_TITLE.getMessage())) {
                        if (clickedInventory.equals(invView.getTopInventory()) || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                            e.setCancelled(true);
                        }
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
                        if (plot != null && e.getCurrentItem() != null) {
                            if (e.getCurrentItem().isSimilar(plot.getOptions().getCurrentFloorItem())) {
                                if (e.getAction() == InventoryAction.SWAP_WITH_CURSOR) {
                                    if (p.hasPermission("buildbattlepro.changefloor")) {
                                        plot.getOptions().setCurrentFloorItem(p, e.getCursor());
                                        clickedInventory.setItem(e.getSlot(), plot.getOptions().getCurrentFloorItem());
                                        e.setCursor(null);
                                    } else {
                                        p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                    }
                                }
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getHeadsItem())) {
                                p.openInventory(plugin.getHeadInventory().getMainPage());
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getBannerCreatorItem())) {
                                BBBannerCreator bbBannerCreator = this.plugin.getBannerCreatorManager().getBannerCreator(p);
                                if (bbBannerCreator == null) {
                                    bbBannerCreator = this.plugin.getBannerCreatorManager().addBannerCreator(p);
                                }
                                this.plugin.getOptionsManager().openColorsInventory(bbBannerCreator);
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getBiomesItem())) {
                                p.openInventory(this.plugin.getOptionsManager().getBiomesInventory());
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getWeatherItemStack(plot))) {
                                if (p.hasPermission("buildbattlepro.changeweather")) {
                                    if (plot.getOptions().getCurrentWeather() == WeatherType.CLEAR) {
                                        plot.getOptions().setCurrentWeather(p, WeatherType.DOWNFALL, false);
                                    } else {
                                        plot.getOptions().setCurrentWeather(p, WeatherType.CLEAR, false);
                                    }
                                    clickedInventory.setItem(e.getSlot(), this.plugin.getOptionsManager().getWeatherItemStack(plot));
                                } else {
                                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                }
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getParticlesItem())) {
                                p.openInventory(this.plugin.getOptionsManager().getParticlesInventory());
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getTimeItem())) {
                                this.plugin.getOptionsManager().openTimeInventory(p, plot);
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getClearPlotItem())) {
                                p.openInventory(new ClearPlotGUI(plot).getInventory());
                            } else if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getBiomesItem())) {
                                p.openInventory(this.plugin.getOptionsManager().getBiomesInventory());
                            }
                        }
                    } else if (invView.getTitle().equalsIgnoreCase(Message.GUI_PARTICLES_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem() != null) {
                                if (e.getCurrentItem().isSimilar(this.plugin.getOptionsManager().getRemoveParticlesItem())) {
                                    this.plugin.getOptionsManager().openActiveParticlesMenu(p, plot);
                                    return;
                                } else {
                                    if (!p.getInventory().contains(e.getCurrentItem())) {
                                        p.getInventory().addItem(e.getCurrentItem());
                                    }
                                }
                            }
                        }
                    } else if (invView.getTitle().equalsIgnoreCase(Message.GUI_BIOMES_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem() != null) {
                                PlotBiome selectedBiome = PlotBiome.getBiomeFromItemStack(e.getCurrentItem());
                                if (selectedBiome != null) {
                                    if (p.hasPermission("buildbattlepro.changebiome")) {
                                        plot.getOptions().setCurrentBiome(p, selectedBiome, true);
                                    } else {
                                        p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                    }
                                }
                            }
                        }
                    } else if (invView.getTitle().equalsIgnoreCase(Message.GUI_PARTICLE_LIST_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasLore()) {
                                BBPlotParticle particle = this.plugin.getOptionsManager().getPlotParticleFromLore(plot, e.getCurrentItem().getItemMeta().getLore());
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
                    } else if (invView.getTitle().equalsIgnoreCase(Message.GUI_TIME_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
                        if (plot != null) {
                            BBPlotTime selectedTime = BBPlotTime.getTimeFromItemStack(e.getCurrentItem(), e.getSlot());
                            if (selectedTime != null) {
                                if (p.hasPermission("buildbattlepro.changetime")) {
                                    plot.getOptions().setCurrentTime(p, selectedTime, true);
                                    this.plugin.getOptionsManager().openTimeInventory(p, plot);
                                } else {
                                    p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                                }
                            }
                        }
                    } else if (invView.getTitle().contains(Message.GUI_HEADS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        Inventory inventory = e.getClickedInventory();
                        HeadInventory headInventory = plugin.getHeadInventory();
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
                    } else if (invView.getTitle().equalsIgnoreCase(Message.GUI_COLORS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBBannerCreator bannerCreator = this.plugin.getBannerCreatorManager().getBannerCreator(p);
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
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
                                    } else if (e.getCurrentItem().equals(this.plugin.getOptionsManager().getBackItem())) {
                                        this.plugin.getOptionsManager().openOptionsInventory(p, plot);
                                    }
                                }
                            }
                        }
                    } else if (invView.getTitle().equalsIgnoreCase(Message.GUI_PATTERNS_TITLE.getMessage())) {
                        e.setCancelled(true);
                        BBBannerCreator bannerCreator = this.plugin.getBannerCreatorManager().getBannerCreator(p);
                        BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
                        if (plot != null) {
                            if (bannerCreator != null) {
                                if (e.getCurrentItem() != null) {
                                    if (e.getCurrentItem().getType() == CompMaterial.WHITE_BANNER.getMaterial() && (!e.getCurrentItem().equals(bannerCreator.getCreatedBanner()))) {
                                        BannerMeta meta = (BannerMeta) e.getCurrentItem().getItemMeta();
                                        bannerCreator.addPattern(meta.getPatterns().get(0).getPattern());
                                    } else if (e.getCurrentItem().equals(bannerCreator.getCreatedBanner())) {
                                        bannerCreator.giveItem();
                                        p.closeInventory();
                                    } else if (e.getCurrentItem().equals(this.plugin.getOptionsManager().getBackItem())) {
                                        this.plugin.getOptionsManager().openOptionsInventory(p, plot);
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
        BBArena arena = this.plugin.getPlayerManager().getPlayerArena(p);

        if (arena != null) {
            if ((e.getItem() != null) && (e.getItem().getType() == CompMaterial.COMPASS.getMaterial())) {
                e.setCancelled(true);
                return;
            }
            if (arena.getBBArenaState() == BBArenaState.VOTING) {
                if (e.getItem() != null) {

                    if (e.getItem().isSimilar(this.plugin.getOptionsManager().getReportItem())) {
                        BBHookWorldEdit hookWorldEdit = (BBHookWorldEdit) BBHook.getHookInstance("WorldEdit");
                        if (hookWorldEdit == null || !hookWorldEdit.isEnabled()) {
                            return;
                        }
                        hookWorldEdit.getReportManager().attemptReport(arena.getCurrentVotingPlot(), p);
                        return;
                    }

                    Votes vote = Votes.getVoteByItemStack(e.getItem());
                    if (vote != null) {
                        BBPlot currentPlot = arena.getCurrentVotingPlot();
                        if (currentPlot != null) {
                            if (!currentPlot.getTeam().getPlayers().contains(p)) {
                                this.plugin.getVotingManager().vote(p, vote, currentPlot);
                                e.setCancelled(true);
                            } else {
                                p.sendMessage(Message.CANT_VOTE_FOR_YOUR_PLOT.getChatMessage());
                            }
                        }
                    }
                }
            } else if (arena.getBBArenaState() == BBArenaState.INGAME) {
                BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(arena, p);
                if (plot != null) {
                    if (e.getItem() != null) {
                        if (e.getItem().isSimilar(this.plugin.getOptionsManager().getOptionsItem())) {
                            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                                this.plugin.getOptionsManager().openOptionsInventory(p, plot);
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
                        if (e.getItem().isSimilar(this.plugin.getOptionsManager().getLeaveItem())) {
                            e.setCancelled(true);
                            arena.removePlayer(p);
                        } else if (e.getItem().isSimilar(this.plugin.getOptionsManager().getTeamsItem())) {
                            e.setCancelled(true);
                            p.openInventory(arena.getTeamsInventory());
                        }
                    }
                }
            }
        } else {
            if (e.getClickedBlock() != null) {
                if (e.getItem() != null && e.getItem().isSimilar(this.plugin.getArenaManager().getPosSelectorItem())) {
                    if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                        e.setCancelled(true);
                        this.plugin.getArenaManager().setPos(p, e.getClickedBlock(), 2);
                    } else if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                        e.setCancelled(true);
                        this.plugin.getArenaManager().setPos(p, e.getClickedBlock(), 1);
                    }
                    return;
                }
                if (e.getClickedBlock().getState() instanceof Sign) {
                    BBSign arenaSign = this.plugin.getSignManager().getSignAtLocation(e.getClickedBlock().getLocation());
                    if (arenaSign != null) {
                        e.setCancelled(true);
                        arenaSign.handleClick(p, e.getAction());
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
                final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
                if (a != null) {
                    final BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
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
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);

        if (a != null) {
            a.removePlayer(p);
        }

        this.plugin.getPlayerManager().unloadPlayerData(p);
    }

    @EventHandler
    public void onVehicleMove(final VehicleMoveEvent e) {
        if (this.plugin.getSettings().isRestrictPlayerMovement()) {
            final Vehicle v = e.getVehicle();
            if (v.getPassenger() instanceof Player) {
                final Player p = (Player) v.getPassenger();
                final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
                if (a != null) {
                    final BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
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
        for (BBArena a : this.plugin.getArenaManager().getArenas().values()) {
            if (a.getLobbyLocation() != null) {
                if (e.getBlock().getWorld().equals(a.getLobbyLocation().getWorld())) {
                    e.setCancelled(true);
                }
            }
        }
    }


    /*@EventHandler
    public void onMove(final PlayerMoveEvent e) {
        System.out.println("Firing event");
        if (this.plugin.getSettings().isRestrictPlayerMovement() || this.plugin.getSettings().isRestrictOnlyPlayerYMovement()) {
            final Player p = e.getPlayer();
            final BBArena arena = this.plugin.getPlayerManager().getPlayerArena(p);
            if (arena != null && arena.getBBArenaState() != BBArenaState.LOBBY) {
                BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(arena, p);
                switch (arena.getBBArenaState()) {
                    case VOTING:
                        plot = this.plugin.getArenaManager().getBBPlotFromLocation(p.getLocation());
                        break;
                    case ENDING:
                        plot = arena.getWinner();
                        break;
                }
                if (plot != null) {
                    if ((this.plugin.getSettings().isRestrictPlayerMovement() && !plot.isLocationInPlot(e.getTo())) || (this.plugin.getSettings().isRestrictOnlyPlayerYMovement() && plot.getMaxPoint().getBlockY() <= e.getTo().getBlockY())) {
                        p.setVelocity(plot.getCenter().toVector().subtract(p.getLocation().toVector()).normalize());
                    }
                }
            }
        }
    }*/


    @EventHandler
    public void onTeleport(final PlayerTeleportEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
        if ((a != null) && ((e.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) || (e.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(final BlockPlaceEvent e) {
        final Player p = e.getPlayer();
        final Location loc = e.getBlockPlaced().getLocation();
        final BBArena arena = this.plugin.getPlayerManager().getPlayerArena(p);
        if (arena != null) {
            switch (arena.getBBArenaState()) {
                case LOBBY:
                case THEME_VOTING:
                case VOTING:
                case ENDING:
                    e.setCancelled(true);
                    break;
                case INGAME:

                    if (this.plugin.getSettings().getRestricedBlocks().contains(e.getBlock().getType())) {
                        p.sendMessage(Message.BLOCK_RESTRICTED.getChatMessage());
                        e.setCancelled(true);
                        return;
                    }

                    BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(arena, p);
                    if (plot != null && plot.isLocationInPlot(loc)) {
                        if (this.plugin.getSettings().isAutomaticGrow() && (e.getBlock().getType() == CompMaterial.WHEAT_SEEDS.getMaterial() || e.getBlock().getType() == CompMaterial.MELON_STEM.getMaterial() || e.getBlock().getType() == CompMaterial.PUMPKIN_STEM.getMaterial())) {
                            CompatBridge.setData(e.getBlock(), (byte) 4);
                        }
                        if (BBParticle.getBBParticle(e.getItemInHand()) == null) {
                            final BBPlayerStats stats = this.plugin.getPlayerManager().getPlayerStats(p);
                            if (stats != null) {
                                stats.setStat(BBStat.BLOCKS_PLACED, (Integer) stats.getStat(BBStat.BLOCKS_PLACED) + 1);
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
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
        if (a != null) {
            final BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(a, p);
            if (plot != null) {
                if (!plot.isLocationInPlot(e.getBlockClicked().getLocation()) && !plot.isLocationInPlot(e.getBlockClicked().getLocation().clone().add(0, 1, 0))) {
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

            if (!p.hasPermission("buildbattlepro.create")) {
                p.sendMessage(Message.NO_PERMISSION.getChatMessage());
                e.setCancelled(true);
                e.getBlock().breakNaturally();
                return;
            }

			BBArena arena = this.plugin.getArenaManager().getArena(e.getLine(1));

            if (arena != null) {
                if (this.plugin.getSignManager().createSign(new BBArenaJoinSign(this.plugin, arena, e.getBlock().getLocation()))) {
                    p.sendMessage(this.plugin.getSettings().getPrefix() + "§aJoin sign for arena §e" + arena.getName() + "§a successfully created!");
                } else {
                    e.setCancelled(true);
                    e.getBlock().breakNaturally();
                    p.sendMessage(this.plugin.getSettings().getPrefix() + "§cPlease specify valid arena!");
                }
                return;
			} else if (e.getLine(1).equalsIgnoreCase("autojoin")) {
				BBGameMode type = null;
				if (e.getLine(2).equalsIgnoreCase("team")) {
					type = BBGameMode.TEAM;
				} else if (e.getLine(2).equalsIgnoreCase("solo")) {
					type = BBGameMode.SOLO;
				}

				if (this.plugin.getSignManager().createSign(new BBAutoJoinSign(this.plugin, type, e.getBlock().getLocation()))) {
					p.sendMessage(this.plugin.getSettings().getPrefix() + "§aAuto-Join sign successfully created!");
				} else {
					p.sendMessage(this.plugin.getSettings().getPrefix() + "§cSomething went wrong. Please contact developer.");
					e.setCancelled(true);
					e.getBlock().breakNaturally();
				}
			} else if (e.getLine(1).equalsIgnoreCase("spectate")) {
				arena = this.plugin.getArenaManager().getArena(e.getLine(2));

				if (arena != null && this.plugin.getSignManager().createSign(new BBArenaSpectateSign(this.plugin, arena, e.getBlock().getLocation()))) {
					p.sendMessage(this.plugin.getSettings().getPrefix() + "§aSpectate sign for arena §e" + arena.getName() + " §asuccessfully created!");
				} else {
					e.setCancelled(true);
					e.getBlock().breakNaturally();
					p.sendMessage(this.plugin.getSettings().getPrefix() + "§cPlease specify valid arena!");
					return;
				}
			}
        }
    }


    @EventHandler
    public void onBreak(final BlockBreakEvent e) {
        final Player p = e.getPlayer();
        final Location loc = e.getBlock().getLocation();
        final BBArena arena = this.plugin.getPlayerManager().getPlayerArena(p);
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
                    BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(arena, p);
                    if (plot != null && !plot.isLocationInPlot(loc)) {
                        p.sendMessage(Message.CANT_BUILD_OUTSIDE.getChatMessage());
                        e.setCancelled(true);
                    }
                    break;
            }
        } else {
            if (block.getState() instanceof Sign) {
                final Sign s = (Sign) e.getBlock().getState();
                final BBSign bbSign = this.plugin.getSignManager().getSignAtLocation(s.getLocation());
                if (bbSign != null) {
                    if (p.hasPermission("buildbattlepro.create")) {
                        this.plugin.getSignManager().removeSign(p,bbSign);
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
            final BBArena arena = this.plugin.getPlayerManager().getPlayerArena(p);
            if (arena != null) {
                e.setCancelled(true);
                p.setHealth(p.getMaxHealth());
                p.setFoodLevel(20);
            }
        }
    }

    @EventHandler
    public void onWaterFlowEvent(final BlockFromToEvent e) {
        final BBPlot plot = this.plugin.getArenaManager().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null && !plot.isLocationInPlot(e.getToBlock().getLocation())) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onTntExplode(final EntityExplodeEvent e) {
        final BBPlot plot = this.plugin.getArenaManager().getBBPlotFromNearbyLocation(e.getLocation());
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
            final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
            if (a != null) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(final EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            final Player p = (Player) e.getDamager();
            final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
            if (a != null && a.getBBArenaState() == BBArenaState.VOTING) {
                e.setCancelled(true);
            }
        }
    }

    /*@EventHandler
    public void LeaveDecay(LeavesDecayEvent e) {
        BBPlot plot = this.plugin.getArenaManager().getBBPlotFromLocation(e.getBlock().getLocation());
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
        final BBArena arena = this.plugin.getPlayerManager().getPlayerArena(p);
        if (arena != null) {
            final BBPlot plot = this.plugin.getArenaManager().getPlayerPlot(arena, p);
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
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
        if (a != null && a.getBBArenaState() == BBArenaState.VOTING) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatIngame(final AsyncPlayerChatEvent e) {

        if (e.isCancelled())
            return;

        final Player p = e.getPlayer();
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);

        if (a != null) {
            if (a.getGameType() == BBGameMode.TEAM) {
                if (this.plugin.getSettings().isTeamChat()) {
                    e.getRecipients().clear();
                    if (e.getMessage().charAt(0) == '!') {
                        e.setMessage(e.getMessage().substring(1));
                        for (Player p1 : a.getPlayers()) {
                            e.getRecipients().add(p1);
                        }
                    } else {
                        e.getRecipients().add(p);
                        for (Player p1 : a.getTeamMates(p)) {
                            e.getRecipients().add(p1);
                        }
                    }
                } else if (this.plugin.getSettings().isArenaChat()) {
                    e.getRecipients().clear();
                    for (Player p1 : a.getPlayers()) {
                        e.getRecipients().add(p1);
                    }
                }
            } else {
                if (this.plugin.getSettings().isArenaChat()) {
                    e.getRecipients().clear();
                    for (Player p1 : a.getPlayers()) {
                        e.getRecipients().add(p1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCommand(final PlayerCommandPreprocessEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
        if (a != null) {
            if (p.hasPermission("buildbattlepro.bypass")) {
                return;
            } else {
                if (e.getMessage().contains("bb") || e.getMessage().contains("buildbattle") || e.getMessage().contains("settheme")) {
                    return;
                }

                boolean valid = false;

                for (String cmd : this.plugin.getSettings().getAllowedCommands()) {
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
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
        if (a != null) {
            e.setCancelled(true);
        }
    }


    @EventHandler
    public void onDispense(final BlockDispenseEvent e) {
        final BBPlot plot = this.plugin.getArenaManager().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            if (!plot.isInPlotRange(e.getBlock().getLocation(), -1) && plot.isInPlotRange(e.getBlock().getLocation(), 5)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonExtendEvent(final BlockPistonExtendEvent e) {
        final BBPlot plot = this.plugin.getArenaManager().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            for (Block block : e.getBlocks()) {
                if (!plot.isInPlotRange(block.getLocation(), -1) && plot.isLocationInPlot(e.getBlock().getLocation())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPistonRetractEvent(final BlockPistonRetractEvent e) {
        final BBPlot plot = this.plugin.getArenaManager().getBBPlotFromLocation(e.getBlock().getLocation());
        if (plot != null) {
            for (Block block : e.getBlocks()) {
                if (!plot.isLocationInPlot(block.getLocation())) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onConsume(final PlayerItemConsumeEvent e) {
        final Player p = e.getPlayer();
        final BBArena a = this.plugin.getPlayerManager().getPlayerArena(p);
        if (a != null) {
            e.setCancelled(true);
        }
    }
}
