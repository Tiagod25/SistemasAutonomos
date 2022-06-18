package Team;

import robocode.AdvancedRobot;

import java.awt.*;
import java.awt.geom.Point2D;

public final class Util {

    public static Point2D.Double findCoords (AdvancedRobot r, double alpha, double dist){
        double angle = Math.toRadians((r.getHeading() + alpha) % 360);
        double d = dist;
        double x = r.getX() + d * Math.sin(angle);
        double y = r.getY() + d * Math.cos(angle);
        return new Point2D.Double(x, y);

    }

}
