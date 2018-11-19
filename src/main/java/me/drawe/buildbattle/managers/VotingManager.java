package me.drawe.buildbattle.managers;

import me.drawe.buildbattle.objects.Message;
import me.drawe.buildbattle.objects.Votes;
import me.drawe.buildbattle.objects.bbobjects.BBArena;
import me.drawe.buildbattle.objects.bbobjects.BBPlot;
import org.bukkit.entity.Player;

public class VotingManager {
    private static VotingManager ourInstance = new VotingManager();

    public static VotingManager getInstance() {
        return ourInstance;
    }

    private VotingManager() {
    }

    public void vote(Player p, Votes vote, BBPlot plot) {
        if (plot.getVotedPlayers().containsKey(p)) {
            if (vote.getWeight() != plot.getVotedPlayers().get(p)) {
                plot.getVotedPlayers().put(p, vote.getWeight());
                p.sendMessage(Message.VOTE_CHANGED.getChatMessage().replaceAll("%vote%", vote.getPrefix()));
                if (GameManager.isShowVoteInSubtitle()) {
                    p.sendTitle("", vote.getPrefix());
                }
                p.playSound(p.getLocation(), vote.getSound().getSound(), 1L, vote.getPitch());
            }
        } else {
            plot.getVotedPlayers().put(p, vote.getWeight());
            p.sendMessage(Message.VOTED.getChatMessage().replaceAll("%vote%", vote.getPrefix()));
            if (GameManager.isShowVoteInSubtitle()) {
                p.sendTitle("", vote.getPrefix());
            }
            p.playSound(p.getLocation(), vote.getSound().getSound(), 1L, vote.getPitch());
        }
        if (GameManager.isScoreboardEnabled()) {
            plot.getArena().updateAllScoreboards(0);
        }
    }

    public void checkVotes(BBArena a) {
        for(Player p : a.getPlayers()) {
            int sum = 0;
            for(BBPlot plot : a.getBuildPlots()) {
                if(plot.getVotedPlayers().containsKey(p)) {
                    sum += plot.getVotedPlayers().get(p);
                    if(sum > 0) break;
                }
            }
            if(sum == 0) {
                for(BBPlot plot : a.getBuildPlots()) {
                    if(plot.getVotedPlayers().containsKey(p)) {
                        plot.setVotePoints(plot.getVotePoints() + Votes.OK.getWeight());
                    }
                }
            }
        }
    }
}
