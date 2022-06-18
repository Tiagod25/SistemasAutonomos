package Team;

import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Point2D;

public final class Util {

    public static void move(AdvancedRobot r, Point2D pos) {
        double angle = 90 - Math.toDegrees(Math.atan2(pos.getY()-r.getY(), pos.getX()-r.getX()));
        double change = angle - r.getHeading() % 360;
        if (change >= -180 && change <= 180) {
            r.turnRight(change);
        } else if (change < -180) {
            r.turnRight(change + 360);
        } else {
            r.turnLeft(360 - change);
        }
        r.ahead(Math.sqrt(Math.pow(r.getX()-pos.getX(), 2) + Math.pow(r.getY()-pos.getY(), 2)));
    }

    public static Point2D.Double findCoords (AdvancedRobot r, double alpha, double dist){
        double angle = Math.toRadians((r.getHeading() + alpha) % 360);
        double d = dist;
        double x = r.getX() + d * Math.sin(angle);
        double y = r.getY() + d * Math.cos(angle);
        return new Point2D.Double(x, y);

    }

}
