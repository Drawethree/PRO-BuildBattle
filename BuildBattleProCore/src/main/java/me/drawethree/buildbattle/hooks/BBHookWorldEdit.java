package me.drawethree.buildbattle.hooks;

import lombok.Getter;
import me.drawethree.buildbattle.BuildBattle;
import me.drawethree.buildbattle.managers.ReportManager;

public class BBHookWorldEdit extends BBHook {

    @Getter
    private ReportManager reportManager;

    public BBHookWorldEdit() {
        super("WorldEdit");
    }

    @Override
    protected void runHookAction(BuildBattle buildBattle) {
        this.reportManager = new ReportManager(buildBattle);
        this.reportManager.loadAllReports();
    }


}
