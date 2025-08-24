package com.duelco.handlers;

import com.duelco.obj.general.PlotInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHandler {
    public static PlotInfo parsePlotInfo(String input) {
        Pattern pattern = Pattern.compile("^(\\S+) \\(([^)]+)\\) (?:owned by (\\S+)|unowned)$|^(.+)$");
        Matcher matcher = pattern.matcher(input);

        if (matcher.matches()) {
            PlotInfo plotInfo = new PlotInfo();

            plotInfo.setPlot(matcher.group(1));         // "plot" or null
            plotInfo.setDistrict(matcher.group(2));     // from pattern 1 or 2
            plotInfo.setOwner(matcher.group(3));        // from "owned by ..."
            if (plotInfo.getPlot() == null) {
                plotInfo.setDistrict(matcher.group(4));        // fallback if no plot
            }

            return plotInfo;
        } else {
            System.out.println("Could not parse line: " + input);

            return null;
        }
    }
}
