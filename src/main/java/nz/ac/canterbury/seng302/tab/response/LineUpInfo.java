package nz.ac.canterbury.seng302.tab.response;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUp;
import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;

import java.util.List;

public class LineUpInfo {


    public long lineupId;

    public String formationString;

    public List<Long> subs = List.of();

    public List<LineUpPositionInfo> lineUpPositions;

    public LineUpInfo(LineUp lineUp, List<LineUpPosition> lineUpPositions) {
        lineupId = lineUp.getLineUpId();
        formationString = lineUp.getFormation().getFormation();
        this.lineUpPositions = lineUpPositions.stream().map(
                x -> new LineUpPositionInfo(x)
        ).toList();
    }

    public void setSubs(List<Long> lis) {
        subs = lis;
    }
}
