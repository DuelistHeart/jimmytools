package com.duelco.handlers;

import com.duelco.obj.general.PlotInfo;
import org.junit.jupiter.api.Test;

public class RegexHandlerTest {
    @Test
    public void testParseTabLine() {
        PlotInfo plotInfo = RegexHandler.parsePlotInfo("plot1 (district1) owned by player1");
        assert plotInfo != null;
        assert plotInfo.getPlot().equals("plot1");
        assert plotInfo.getDistrict().equals("district1");
        assert plotInfo.getOwner().equals("player1");

        PlotInfo plotInfo2 = RegexHandler.parsePlotInfo("plot2 (district 2) unowned");
        assert plotInfo2 != null;
        assert plotInfo2.getPlot().equals("plot2");
        assert plotInfo2.getDistrict().equals("district 2");
        assert plotInfo2.getOwner() == null;

        PlotInfo plotInfo3 = RegexHandler.parsePlotInfo("district3");
        assert plotInfo3 != null;
        assert plotInfo3.getPlot() == null;
        assert plotInfo3.getDistrict().equals("district3");
        assert plotInfo3.getOwner() == null;

        PlotInfo plotInfo4 = RegexHandler.parsePlotInfo("plot4 (district4) owned by __player2");
        assert plotInfo4 != null;
        assert plotInfo4.getPlot().equals("plot4");
        assert plotInfo4.getDistrict().equals("district4");
        assert plotInfo4.getOwner().equals("__player2");
    }
}
