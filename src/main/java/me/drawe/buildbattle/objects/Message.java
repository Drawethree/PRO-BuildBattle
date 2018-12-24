package me.drawe.buildbattle.objects;

import me.drawe.buildbattle.BuildBattle;
import me.drawe.buildbattle.managers.GameManager;
import net.md_5.bungee.api.ChatColor;

public enum Message {

    NOT_ENOUGH_PLAYERS("messages.not_enough_players"),
    GAME_STARTS_IN("messages.game_starts_in"),
    GAME_ENDS_IN("messages.game_ends_in"),
    TIMES_UP("messages.times_up"),
    THEME("messages.theme"),
    PLAYER_JOINED("messages.player_joined"),
    PLAYER_LEFT("messages.player_left"),
    ARENA_EXISTS("messages.arena_exists"),
    ARENA_NOT_EXISTS("messages.arena_not_exists"),
    NO_SELECTION("messages.no_selection"),
    NOT_IN_PLOT("messages.not_in_plot"),
    PLOT_REMOVED("messages.plot_removed"),
    NOT_IN_ARENA("messages.not_in_arena"),
    ALREADY_IN_ARENA("messages.already_in_arena"),
    ARENA_REMOVED("messages.arena_removed"),
    FLOOR_CHANGED("messages.floor_changed"),
    WEATHER_CHANGED("messages.weather_changed"),
    FLOOR_DENY_CHANGE("messages.floor_deny_change"),
    NOT_PLAYED("messages.not_played"),
    SCOREBOARD_TITLE("scoreboard.in-game.title"),
    SCOREBOARD_TIME_LEFT("scoreboard.in-game.time_left"),
    SCOREBOARD_THEME("scoreboard.in-game.theme"),
    SCOREBOARD_BUILDER("scoreboard.in-game.builder"),
    SCOREBOARD_YOUR_VOTE("scoreboard.in-game.your_vote"),
    SCOREBOARD_PLAYERS("scoreboard.in-game.players"),
    SCOREBOARD_MIN_PLAYERS("scoreboard.in-game.min_players"),
    SCOREBOARD_SERVER("scoreboard.in-game.server"),
    SCOREBOARD_BUILDERS("scoreboard.in-game.builders"),
    SCOREBOARD_TEAM_MODE("scoreboard.in-game.team_mode"),
    SCOREBOARD_SOLO_MODE("scoreboard.in-game.solo_mode"),
    SCOREBOARD_TEAMS("scoreboard.in-game.teams"),
    SCOREBOARD_TEAMMATE("scoreboard.in-game.teammate"),
    SCOREBOARD_STARTING_IN("scoreboard.in-game.starting_in"),
    SCOREBOARD_WAITING("scoreboard.in-game.waiting"),
    SCOREBOARD_MAIN_LOBBY_TITLE("scoreboard.main-lobby.title"),
    SCOREBOARD_MAIN_LOBBY_PLAYED("scoreboard.main-lobby.played"),
    SCOREBOARD_MAIN_LOBBY_WINS("scoreboard.main-lobby.wins"),
    SCOREBOARD_MAIN_LOBBY_MOST_POINTS("scoreboard.main-lobby.most_points"),
    SCOREBOARD_MAIN_LOBBY_BLOCKS_PLACED("scoreboard.main-lobby.blocks_placed"),
    SCOREBOARD_MAIN_LOBBY_PARTICLES_PLACED("scoreboard.main-lobby.particles_placed"),
    SCOREBOARD_MAIN_LOBBY_SERVER("scoreboard.main-lobby.server"),
    SCOREBOARD_MAIN_LOBBY_SUPER_VOTES("scoreboard.main-lobby.super_votes"),
    STATS_TITLE("stats.title"),
    STATS_WINS("stats.wins"),
    STATS_PLAYED("stats.played"),
    STATS_MOST_POINTS("stats.most_points"),
    STATS_SUPER_VOTES("stats.super_votes"),
    ARENA_FULL("messages.arena_full"),
    ARENA_ALREADY_STARTED("messages.arena_already_started"),
    VOTE_CHANGED("messages.vote_changed"),
    VOTED("messages.voted"),
    ARENA_ENDED("messages.arena_ended"),
    STATS_BLOCKS_PLACED("stats.blocks_placed"),
    CANT_BUILD_OUTSIDE("messages.cant_build"),
    COMMANDS_NOT_ALLOWED("messages.commands_not_allowed"),
    NO_EMPTY_ARENA("messages.no_empty_arena"),
    YOU_PLACED("messages.you_placed"),
    VOTING_NONE("voting.none"),
    VOTING_VERY_BAD("voting.very_bad"),
    VOTING_OK("voting.ok"),
    VOTING_BAD("voting.bad"),
    VOTING_NICE("voting.nice"),
    VOTING_EPIC("voting.epic"),
    VOTING_LEGENDARY("voting.legendary"),
    RELOAD("messages.server_reload"),
    ALL_ARENAS("messages.all_arenas"),
    FORCE_STOP("messages.force_stop"),
    ARENA_NOT_RUNNING("messages.arena_not_running"),
    CANT_VOTE_FOR_YOUR_PLOT("messages.cant_vote_for_your_plot"),
    VOTE_TIME("messages.voting_ends"),
    MAX_PARTICLES("messages.max_particles"),
    PARTICLE_REMOVED("messages.particle_removed"),
    CANT_PLACE_PARTICLE_OUTSIDE("messages.cant_place_particle"),
    STATS_PARTICLES_PLACED("stats.particles_placed"),
    MOST_POINTS_ANNOUNCE("messages.most_points_announce"),
    MOST_POINTS_OLD("messages.old_most_points"),
    MOST_POINTS_NEW("messages.new_most_points"),
    TIME_CHANGED("messages.time_changed"),
    PLOT_CLEARED("messages.plot_cleared"),
    ARENA_LIST_STATUS("gui.arena_list.status"),
    ARENA_LIST_PLAYERS("gui.arena_list.players"),
    ARENA_LIST_CLICK_TO_JOIN("gui.arena_list.join"),
    NO_PERMISSION("messages.no_permission"),
    THANKS_FOR_PLAYING("messages.thanks_for_playing"),
    YOUR_POSITION("messages.your_position"),
    TIME_YEARS("time.years"),
    TIME_DAYS("time.days"),
    TIME_HOURS("time.hours"),
    TIME_MINUTES("time.minutes"),
    TIME_YEAR("time.year"),
    TIME_DAY("time.day"),
    TIME_HOUR("time.hour"),
    TIME_MINUTE("time.minute"),
    TITLE_VOTING("messages.voting"),
    SUBTITLE_VOTING("messages.prepare_vote"),
    TIME_SECONDS("time.seconds"),
    TIME_SECOND("time.second"),
    LINE_SPACER("messages.line_spacer"),
    CANT_LEAVE_PLOT("messages.cant_leave_plot"),
    NO_PLAYERS("messages.no_players"),
    MOTD_LOBBY("motd.lobby"),
    MOTD_INGAME("motd.ingame"),
    MOTD_VOTING("motd.voting"),
    MOTD_ENDING("motd.ending"),
    BIOME_CHANGED("messages.biome_changed"),
    CANNOT_WEAR_ARMOR("messages.cant_wear_equipment"),
    CANNOT_SET_THEME("messages.cannot_set_theme"),
    REPORT_SUCCESS("messages.report_success"),
    ALREADY_REPOTED("messages.already_reported"),
    REPORT_FAILED("messages.report_failed"),
    CANNOT_REPORT_YOURSELF("messages.cant_report_yourself"),
    VOTING_BUILDER("voting.builder"),
    SIGN_JOIN_FIRST_LINE("signs.join.first_line"),
    SIGN_JOIN_SECOND_LINE("signs.join.second_line"),
    SIGN_JOIN_THIRD_LINE("signs.join.third_line"),
    SIGN_JOIN_FOURTH_LINE("signs.join.fourth_line"),
    SIGN_AUTO_JOIN_FIRST_LINE("signs.auto-join.first_line"),
    SIGN_AUTO_JOIN_SECOND_LINE("signs.auto-join.second_line"),
    SIGN_AUTO_JOIN_THIRD_LINE("signs.auto-join.third_line"),
    SIGN_AUTO_JOIN_FOURTH_LINE("signs.auto-join.fourth_line"),
    ITEMS_LEAVE_ITEM_DISPLAYNAME("items.leave_item.displayname"),
    ITEMS_REPORT_ITEM_DISPLAYNAME("items.report_item.displayname"),
    ITEMS_OPTIONS_ITEM_DISPLAYNAME("items.options_item.displayname"),
    GUI_OPTIONS_TITLE("gui.options.title"),
    GUI_OPTIONS_HEADS_ITEM_DISPLAYNAME("gui.options.items.heads_item.displayname"),
    GUI_OPTIONS_CHANGE_FLOOR_ITEM_DISPLAYNAME("gui.options.items.change_floor_item.displayname"),
    GUI_OPTIONS_CHANGE_TIME_ITEM_DISPLAYNAME("gui.options.items.time_item.displayname"),
    GUI_OPTIONS_PARTICLES_ITEM_DISPLAYNAME("gui.options.items.particles_item.displayname"),
    GUI_OPTIONS_PARTICLE_LIST_ITEM_DISPLAYNAME("gui.options.items.particle_list_item.displayname"),
    GUI_OPTIONS_CLEAR_PLOT_ITEM_DISPLAYNAME("gui.options.items.clear_plot_item.displayname"),
    GUI_OPTIONS_CHANGE_WEATHER_ITEM_DISPLAYNAME("gui.options.items.change_weather_item.displayname"),
    GUI_PARTICLES_TITLE("gui.particles.title"),
    GUI_TIME_TITLE("gui.time.title"),
    GUI_TIME_MORNING_ITEM_DISPLAYNAME("gui.time.items.morning.displayname"),
    GUI_TIME_MIDMORNING_ITEM_DISPLAYNAME("gui.time.items.mid_morning.displayname"),
    GUI_TIME_NOON_ITEM_DISPLAYNAME("gui.time.items.noon.displayname"),
    GUI_TIME_AFTERNOON_ITEM_DISPLAYNAME("gui.time.items.afternoon.displayname"),
    GUI_TIME_EVENING_ITEM_DISPLAYNAME("gui.time.items.evening.displayname"),
    GUI_TIME_NIGHT_ITEM_DISPLAYNAME("gui.time.items.night.displayname"),
    GUI_TIME_MIDNIGHT_ITEM_DISPLAYNAME("gui.time.items.midnight.displayname"),
    GUI_TIME_AFTERMIDNIGHT_ITEM_DISPLAYNAME("gui.time.items.after_midnight.displayname"),
    GUI_HEADS_TITLE("gui.heads.title"),
    GUI_HEADS_PREV_PAGE("gui.heads.items.previous_page"),
    GUI_HEADS_NEXT_PAGE("gui.heads.items.next_page"),
    GUI_HEADS_MAIN_PAGE("gui.heads.items.main_page"),
    GUI_THEME_VOTING_TITLE("gui.theme_voting.title"),
    GUI_THEME_VOTING_INVENTORY_THEMES_DISPLAYNAME("gui.theme_voting.themes.displayname"),
    GUI_THEME_VOTING_INVENTORY_SUPER_VOTE_DISPLAYNAME("gui.theme_voting.supervote_item.displayname"),
    GUI_ARENA_LIST_TITLE("gui.arena_list.title"),
    GUI_ARENA_LIST_STATUS("gui.arena_list.status"),
    GUI_ARENA_LIST_PLAYERS("gui.arena_list.players"),
    GUI_ARENA_LIST_CLICK_TO_JOIN("gui.arena_list.join"),
    GUI_PARTICLE_LIST_TITLE("gui.particle_list.title"),
    GUI_PARTICLE_LIST_ITEMS_LOCATION("gui.particle_list.items.location"),
    GUI_PARTICLE_LIST_ITEMS_CLICK_TO_REMOVE("gui.particle_list.items.click_to_remove"),
    GAMESTATE_LOBBY("gamestate.lobby"),
    GAMESTATE_THEME_VOTING("gamestate.theme_voting"),
    GAMESTATE_INGAME("gamestate.ingame"),
    GAMESTATE_VOTING("gamestate.voting"),
    GAMESTATE_ENDING("gamestate.ending"),
    LEADERBOARDS_WINS_TITLE("leaderboards.wins.title"),
    LEADERBOARDS_WINS_FORMAT("leaderboards.wins.format"),
    LEADERBOARDS_PLAYED_TITLE("leaderboards.played.title"),
    LEADERBOARDS_PLAYED_FORMAT("leaderboards.played.format"),
    LEADERBOARDS_BLOCKS_PLACED_TITLE("leaderboards.blocks_placed.title"),
    LEADERBOARDS_BLOCKS_PLACED_FORMAT("leaderboards.blocks_placed.format"),
    LEADERBOARDS_PARTICLES_PLACED_TITLE("leaderboards.particles_placed.title"),
    LEADERBOARDS_PARTICLES_PLACED_FORMAT("leaderboards.particles_placed.format"),
    CHANGE_FLOOR_NPC_NAME("change_floor_npc.name"),
    GAMEMODE_TEAMS("gamemode.team"),
    GAMEMODE_SOLO("gamemode.solo"),
    VOTING_BUILDERS("voting.builders"),
    YOU_JOINED_TEAM("messages.you_joined_team"),
    YOU_LEFT_TEAM("messages.you_left_team"),
    YOUR_TEAMMATE("messages.your_teammate"),
    GUI_TEAMS_TITLE("gui.teams.title"),
    ITEMS_TEAMS_ITEM_DISPLAYNAME("items.teams_item.displayname"),
    TEAM_IS_FULL("messages.team_is_full"),
    PARTY_JOIN("messages.party_join"),
    PARTY_LEFT("messages.party_leave"),
    PARTY_ALREADY_INVITED("messages.party_already_invited"),
    PARTY_INVITE("messages.party_invite"),
    PARTY_INVITE_EXPIRED("messages.party_invite_expired"),
    PARTY_CREATED("messages.party_created"),
    PARTY_DISBANDED("messages.party_disband"),
    PARTY_ALREADY_IN_PARTY("messages.party_already_in_party"),
    PARTY_CREATE_FAILED("messages.party_create_failed"),
    PARTY_NO_PENDING_INVITES("messages.party_no_pending_invites"),
    PARTY_INVITE_DECLINE("messages.party_invite_decline"),
    PARTY_NOT_IN_PARTY("messages.party_not_in_party"),
    PARTY_INVALID_USAGE("messages.party_invalid_usage"),
    PARTY_PLAYER_NOT_ONLINE("messages.party_player_not_online"),
    PARTY_NOT_ALLOWED_TO_INVITE("messages.party_not_allowed_to_invite"),
    PARTY_PLAYER_JOINED("messages.party_player_joined"),
    PARTY_PLAYER_LEFT("messages.party_player_left"),
    PARTY_NO_SPACE_FOR_YOUR_PARTY("messages.party_arena_full"),
    PARTY_YOU_HAVE_BEEN_INVITED("messages.party_you_have_been_invited"),
    PARTY_ACCEPT_INFO("messages.party_accept_info"),
    PARTY_DECLINE_INFO("messages.party_decline_info"),
    PARTIES_NOT_ALLOWED("messages.party_not_allowed"),
    PARTY_FULL("messages.party_full"),
    ALREADY_IN_THAT_TEAM("messages.already_in_that_team"),
    GUI_TEAMS_ITEMS_DISPLAYNAME("gui.teams.items.displayname"),
    GUI_TEAM_ITEMS_NOBODY("gui.teams.items.nobody"),
    ARENA_LIST_MODE("gui.arena_list.mode"),
    ITEMS_BACK_ITEM_DISPLAYNAME("items.back_item.displayname"),
    GUI_BIOMES_PLAINS_DISPLAYNAME("gui.biomes.items.plains.displayname"),
    GUI_BIOMES_MESA_DISPLAYNAME("gui.biomes.items.mesa.displayname"),
    GUI_BIOMES_OCEAN_DISPLAYNAME("gui.biomes.items.ocean.displayname"),
    GUI_BIOMES_DESERT_DISPLAYNAME("gui.biomes.items.desert.displayname"),
    GUI_BIOMES_FOREST_DISPLAYNAME("gui.biomes.items.forest.displayname"),
    GUI_BIOMES_JUNGLE_DISPLAYNAME("gui.biomes.items.jungle.displayname"),
    GUI_BIOMES_SWAMP_DISPLAYNAME("gui.biomes.items.swamp.displayname"),
    GUI_BIOMES_SAVANNA_DISPLAYNAME("gui.biomes.items.savanna.displayname"),
    GUI_BIOMES_BEACH_DISPLAYNAME("gui.biomes.items.beach.displayname"),
    GUI_BIOMES_ICE_SPIKES_DISPLAYNAME("gui.biomes.items.ice_spikes.displayname"),
    GUI_OPTIONS_PLOT_BIOME_ITEM_DISPLAYNAME("gui.options.items.change_biome_item.displayname"),
    GUI_BIOMES_TITLE("gui.biomes.title"),
    ITEMS_BANNER_CREATOR_ITEM_DISPLAYNAME("gui.options.items.banner_creator_item.displayname"),
    GUI_COLORS_TITLE("gui.colors.title"),
    GUI_PATTERNS_TITLE("gui.patterns.title"),
    FINAL_BANNER_ITEM_DISPLAYNAME("gui.banner_creator.items.final_banner.displayname"),
    THEME_BLACKLISTED("messages.theme_blacklisted"),
    KICKED_DUE_TO_VIP_JOIN("messages.kicked_due_vip_join"),
    NO_VIP_SLOT_FREE("messages.no_vip_slot_free"),
    UNABLE_TO_JOIN("messages.unable_to_join"),
    ORDINAL_ST("ordinals.st"),
    ORDINAL_ND("ordinals.nd"),
    ORDINAL_RD("ordinals.rd"),
    ORDINAL_TH("ordinals.th"),
    GUI_ARENA_LIST_SOLO_TITLE("gui.arena_list.solo_title"),
    GUI_ARENA_LIST_TEAM_TITLE("gui.arena_list.team_title"),
    NOT_ENOUGH_SUPER_VOTES("messages.not_enough_super_votes"),
    THEME_WAS_SUPER_VOTED("messages.theme_was_super_voted"),
    PARTY_CANT_INVITE_YOURSELF("messages.party_cant_invite_yourself"),
    GUI_BIOMES_NETHER_DISPLAYNAME("gui.biomes.items.nether.displayname"),
    GUI_BIOMES_THE_END_DISPLAYNAME("gui.biomes.items.the_end.displayname"),
    GUI_BIOMES_MOUNTAINS_DISPLAYNAME("gui.biomes.items.mountains.displayname"),
    GUI_BIOMES_TAIGA_DISPLAYNAME("gui.biomes.items.taiga.displayname"),
    GUI_BIOMES_RIVER_DISPLAYNAME("gui.biomes.items.river.displayname"),
    GUI_BIOMES_MUSHROOM_DISPLAYNAME("gui.biomes.items.mushroom.displayname"),
    GUI_BIOMES_WARM_OCEAN_DISPLAYNAME("gui.biomes.items.warm_ocean.displayname"),
    BUILD_SOMETHING_RELEVANT("messages.build_something_relevant");

    private String message;

    Message(String path) {
        this.message = ChatColor.translateAlternateColorCodes('&', BuildBattle.getFileManager().getConfig("translates.yml").get().getString(path));
    }

    public String getChatMessage() {
        return GameManager.getPrefix() + message;
    }

    public String getMessage() {
        return message;
    }
}
