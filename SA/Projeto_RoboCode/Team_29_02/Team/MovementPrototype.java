package Team;

import java.util.*;
import java.awt.geom.Point2D;
import robocode.*;
import robocode.util.Utils;
import standardOdometer.Odometer;

/*
 * Classe que representa um snapshot de um robot, guardando o seu nome, a sua posição, e a sua direção.
 */
class ScannedRobot {
    private String nome;
    private Point2D last_posicao;
    private Point2D posicao;
    private double heading;
    private boolean isTeammate;

    ScannedRobot() {
        this.nome = "";
        this.last_posicao = new Point2D.Double();
        this.posicao = new Point2D.Double();
        this.heading = 0.0;
        this.isTeammate = false;
    }

    ScannedRobot(String nome, Point2D.Double p, double h, boolean isTeammate) {
        this.nome = nome;
        this.last_posicao = null;
        this.posicao = (Point2D)p.clone();
        this.heading = h;
        this.isTeammate = isTeammate;
    }

    // Construtor que determina a posição do robot a partir de um scan feito pelo radar (ScannedRobotEvent)
    ScannedRobot(Point2D pos, double heading, ScannedRobotEvent e, boolean isTeammate) {
        this.nome = e.getName();
        double angle = Math.toRadians((heading + e.getBearing()) % 360);
        double d = e.getDistance();
        double x = pos.getX() + d * Math.sin(angle);
        double y = pos.getY() + d * Math.cos(angle);
        this.last_posicao = new Point2D.Double(x, y);
        this.posicao = new Point2D.Double(x, y);
        this.heading = e.getHeading();
        this.isTeammate = isTeammate;
    }

    String getName() {
        return this.nome;
    }

    Point2D getPos() {
        return (Point2D)this.posicao.clone();
    }

    double getHeading() {
        return this.heading;
    }

    boolean isTeammate() {
        return this.isTeammate;
    }

    void update(Point2D pos, double heading) {
        this.last_posicao = this.posicao;
        this.posicao = (Point2D)pos.clone();
        this.heading = heading;
    }
}

public class MovementPrototype extends TeamRobot {

    // representação do mundo (posições e direções dos robot detetados)
    private Map<String, ScannedRobot> scannedRobots = new HashMap<String, ScannedRobot>();

    public  void run(){
        int i=0;
        while(true) {
            // rodar radar sempre no sentido dos ponteiros do relógio
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            Vector2D dir = getOptimalDirection();
            move(new Point2D.Double(getX() + dir.x * 100, getY() + dir.y * 100));
            setTurnGunRight(1.0);
            setFire(3.0);
            System.out.println("-----" + i++);
            execute();
        }

    }

    private Vector2D getOptimalDirection() {
        double forceX = 0.0;
        double forceY = 0.0;
        double x = getX();
        double y = getY();
        for (ScannedRobot r : this.scannedRobots.values()) {
            double weight = (r.isTeammate() ? 100 : -100);
            Point2D p = r.getPos();
            double dx = p.getX() - x;
            double dy = p.getY() - y;
            double distSquared = Math.pow(dx, 2) + Math.pow(dy, 2);
            forceX += weight * dx / distSquared;
            forceY += weight * dy / distSquared;
        }
        // desviar das paredes
        forceX += 10000000 / Math.pow(x, 3);
        forceY += 10000000 / Math.pow(y, 3);
        forceX -= 10000000 / Math.pow(getBattleFieldWidth() - x, 3);
        forceY -= 10000000 / Math.pow(getBattleFieldHeight() - y, 3);

        return new Vector2D(forceX, forceY).normalize();
    }

    private void move(Point2D pos) {

        double x=pos.getX();
        double y=pos.getY();

        if(pos.getX() <= 100){
            x=100;
        }

        if(x >= (getBattleFieldWidth() - 100))
            x = getBattleFieldWidth() - 100;


        if(y <= 100){
            y=100;
        }

        if(y >= (getBattleFieldHeight() - 100))
            y = getBattleFieldHeight() - 100;

        pos = new Point2D.Double(x,y);


        double angle = 90 - Math.toDegrees(Math.atan2(pos.getY()-this.getY(), pos.getX()-this.getX()));
        double change = angle - this.getHeading() % 360;
        if (change >= -180 && change <= 180) {
            setTurnRight(change);
        } else if (change < -180) {
            setTurnRight(change + 360);
        } else {
            setTurnLeft(360 - change);
        }
        setAhead(Math.sqrt(Math.pow(this.getX()-pos.getX(), 2) + Math.pow(this.getY()-pos.getY(), 2)));

    }

    public void onScannedRobot(ScannedRobotEvent e) {
        Point2D p = new Point2D.Double(this.getX(), this.getY());
        ScannedRobot s = new ScannedRobot(p, this.getHeading(), e, this.isTeammate(e.getName()));
        updateScannedRobot(s);
    }

    private void updateScannedRobot(ScannedRobot s) {
        if (this.scannedRobots.containsKey(s.getName())) {
            this.scannedRobots.get(s.getName()).update(s.getPos(), s.getHeading());
        } else {
            this.scannedRobots.put(s.getName(), s);
        }
    }

    public void onCustomEvent(CustomEvent ev) {
        Condition cd = ev.getCondition();
    }

    public void onHitRobot(HitRobotEvent e){
        // apenas fazer algo se o robot for parado, o que apenas acontece quando o sistema determina que a colisão foi a culpa dele
        if (e.isMyFault() == true) {
            setTurnRight(45);
            back(100);
        }
    }

    public void onHitWall(HitWallEvent e) {
        if(Math.abs(e.getBearing()-this.getHeading())>90) setAhead(50);
        else setBack(50);
    }

}
