package me.drawethree.buildbattle.managers;

import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.objects.Message;
import me.drawethree.buildbattle.objects.Votes;
import me.drawethree.buildbattle.objects.bbobjects.arena.BBArena;
import me.drawethree.buildbattle.objects.bbobjects.plot.BBPlot;
import org.bukkit.entity.Player;

public class VotingManager {

    private BuildBattle plugin;

    public VotingManager(BuildBattle plugin) {
        this.plugin = plugin;
    }

    public void vote(Player p, Votes vote, BBPlot plot) {
        if (plot.getVotedPlayers().containsKey(p)) {
            if (vote.getWeight() != plot.getVotedPlayers().get(p)) {
                p.sendMessage(Message.VOTE_CHANGED.getChatMessage().replace("%vote%", vote.getPrefix()));

                if (this.plugin.getSettings().isShowVoteInSubtitle()) {
                    p.sendTitle("", vote.getPrefix());
                }

                p.playSound(p.getLocation(), vote.getSound().getSound(), 1L, vote.getPitch());

                plot.getVotedPlayers().put(p, vote.getWeight());
            }
        } else {
            p.sendMessage(Message.VOTED.getChatMessage().replace("%vote%", vote.getPrefix()));

            if (this.plugin.getSettings().isShowVoteInSubtitle()) {
                p.sendTitle("", vote.getPrefix());
            }

            p.playSound(p.getLocation(), vote.getSound().getSound(), 1L, vote.getPitch());

            plot.getVotedPlayers().put(p, vote.getWeight());
        }
    }

    public void checkVotes(BBArena a) {
        for (Player p : a.getPlayers()) {
            int sum = 0;
            for (BBPlot plot : a.getBuildPlots()) {
                if (plot.getVotedPlayers().containsKey(p)) {
                    sum += plot.getVotedPlayers().get(p);
                    if (sum > 0) break;
                }
            }
            if (sum == 0) {
                for (BBPlot plot : a.getBuildPlots()) {
                    if (plot.getVotedPlayers().containsKey(p)) {
                        plot.setVotePoints(plot.getVotePoints() + Votes.OK.getWeight());
                    }
                }
            }
        }
    }
}
