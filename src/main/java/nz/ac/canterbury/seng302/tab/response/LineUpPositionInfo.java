package nz.ac.canterbury.seng302.tab.response;

import nz.ac.canterbury.seng302.tab.entity.lineUp.LineUpPosition;

public class LineUpPositionInfo {

    public UserInfo userInfo;
    public int position;

    public LineUpPositionInfo(LineUpPosition lineUpPosition) {
        userInfo = new UserInfo(lineUpPosition.getPlayer());
        position = lineUpPosition.getPosition();
    }
}
