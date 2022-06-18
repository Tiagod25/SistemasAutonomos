package AdvancedRobot;

import java.util.*;
import java.awt.geom.Point2D;
import robocode.*;
import robocode.util.Utils;
import standardOdometer.Odometer;

/*
 * Classe que representa um ponto a ser atingido, implementando o teste de igualdade com uma tolerância definida.
 */
class Target {
    private Point2D pos;
    private double tol;

    Target() {
        this.pos = new Point2D.Double();
        this.tol = 0.0;
    }

    Target(double x, double y, double tol) {
        this.pos = new Point2D.Double(x, y);
        this.tol = tol;
    }

    Point2D getPos() {
        return (Point2D)this.pos.clone();
    }

    boolean isReached(Point2D pos) {
        double tx = this.pos.getX();
        double ty = this.pos.getY();
        double x = pos.getX();
        double y = pos.getY();

        if (this.tol == 0.0) {
            return x == tx && y == ty;
        } else {
            return (tx - tol <= x && x <= tx + tol &&
                    ty - tol <= y && y <= ty + tol);
        }
    }
}

/*
 * Classe que representa um snapshot de um robot, guardando o seu nome, a sua posição, e a sua direção.
 */
class ScannedRobot {
    private String nome;
    private Point2D last_posicao;
    private Point2D posicao;
    private double heading;

    ScannedRobot() {
        this.nome = "";
        this.last_posicao = new Point2D.Double();
        this.posicao = new Point2D.Double();
        this.heading = 0.0;
    }

    ScannedRobot(String nome, Point2D.Double p, double h) {
        this.nome = nome;
        this.last_posicao = null;
        this.posicao = (Point2D)p.clone();
        this.heading = h;
    }

    // Construtor que determina a posição do robot a partir de um scan feito pelo radar (ScannedRobotEvent)
    ScannedRobot(Point2D pos, double heading, ScannedRobotEvent e) {
        this.nome = e.getName();
        double angle = Math.toRadians((heading + e.getBearing()) % 360);
        double d = e.getDistance();
        double x = pos.getX() + d * Math.sin(angle);
        double y = pos.getY() + d * Math.cos(angle);
        this.last_posicao = new Point2D.Double(x, y);
        this.posicao = new Point2D.Double(x, y);
        this.heading = e.getHeading();
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

    boolean isStopped() {
        if (this.last_posicao == null) return false;
        Target pos = new Target(this.posicao.getX(), this.posicao.getY(), 0.1);
        return pos.isReached(this.last_posicao);
    }

    void update(Point2D pos, double heading) {
        this.last_posicao = this.posicao;
        this.posicao = (Point2D)pos.clone();
        this.heading = heading;
    }
}

public class CircumNavigator2 extends AdvancedRobot {

    //Variáveis de classe
    private double distanciaPercorrida=0;
    private Odometer odometer = new Odometer("isRacing",this);
    private double x_atual;
    private double y_atual;
    private double x_ultimo= 18;
    private double y_ultimo=18;

    // representação do mundo (posições e direções dos robot detetados)
    private Map<String, ScannedRobot> scannedRobots = new HashMap<String, ScannedRobot>();

    private int fase = 0;

    public  void run(){
        // Inicializacao dos Odometros
        addCustomEvent(odometer);
        addCustomEvent(new Condition("emMovimento") {
            @Override
            public boolean test() {
                return (getTime()!=0);
            }
        });

        while(true) {
            // rodar radar sempre no sentido dos ponteiros do relógio
            setTurnRadarRight(Double.POSITIVE_INFINITY);

            switch (this.fase){
                case 0:
                    if (this.getQuadrants() != null) {
                        this.fase = 1;
                    }
                    doNothing();
                    break;
                default:
                    contornarRobots();
                    break;
            }
        }

    }

    private void move(Point2D pos) {
        double angle = 90 - Math.toDegrees(Math.atan2(pos.getY()-this.getY(), pos.getX()-this.getX()));
        double change = angle - this.getHeading() % 360;
        if (change >= -180 && change <= 180) {
            turnRight(change);
        } else if (change < -180) {
            turnRight(change + 360);
        } else {
            turnLeft(360 - change);
        }
        ahead(Math.sqrt(Math.pow(this.getX()-pos.getX(), 2) + Math.pow(this.getY()-pos.getY(), 2)));
    }

    private void contornarRobots() {
        Target[] targets = getTargets();

        if (targets == null) {
            System.out.println("Robots ainda não estão em posição.");
            doNothing();
            return;
        }

        Point2D pos = new Point2D.Double(this.getX(), this.getY());
        if (this.fase <= 11) {
            Target t = targets[this.fase - 1];
            if (t.isReached(pos)) {
                this.fase++;
                doNothing();
            } else {
                move(t.getPos());
            }
        } else if (this.fase == 12) {
            this.fase++;
            System.out.println("Distancia total percorrida: " + this.distanciaPercorrida);
            System.out.println("Distancia exata: " + this.odometer.getRaceDistance());
        } else {
            doNothing();
        }
    }

    private Target[] getTargets() {
        Target[] targets = new Target[11];

        ScannedRobot[] robots = getQuadrants();
        if (robots == null) return null;

        ScannedRobot r1 = robots[0];
        ScannedRobot r2 = robots[1];
        ScannedRobot r3 = robots[2];

        // determinar pontos a atravessar (lado mais longe do centro do respetivo robot)
        // diagonal (centro até canto do robot): sqrt(18^2 + 18^2) ~= 26
        // distância segura (ao segir em frente): 26 + 18 = 44
        // distância segura (diagonal + diagonal)
        // componentes x e y do vetor do centro até a distância segura na diagonal = comprimento do lado do robô = 36
        double k1 = 44;
        double k2 = 36;

        double tol = 1.0;

        // canto
        targets[0] = new Target(18, 18, tol);

        // 1o quadrante
        targets[1] = new Target(r1.getPos().getX() - k1, r1.getPos().getY()     , tol);
        targets[2] = new Target(r1.getPos().getX() - k2, r1.getPos().getY() + k2, tol);
        targets[3] = new Target(r1.getPos().getX()     , r1.getPos().getY() + k1, tol);

        // 2o quadrante
        targets[4] = new Target(r2.getPos().getX()     , r2.getPos().getY() + k1, tol);
        targets[5] = new Target(r2.getPos().getX() + k2, r2.getPos().getY() + k2, tol);
        targets[6] = new Target(r2.getPos().getX() + k1, r2.getPos().getY()     , tol);

        // 3o quadrante
        targets[7] = new Target(r3.getPos().getX() + k1, r3.getPos().getY()     , tol);
        targets[8] = new Target(r3.getPos().getX() + k2, r3.getPos().getY() - k2, tol);
        targets[9] = new Target(r3.getPos().getX()     , r3.getPos().getY() - k1, tol);

        // voltar ao canto
        targets[10] = new Target(18, 18, tol);

        return targets;
    }

    private ScannedRobot[] getQuadrants() {
        ScannedRobot[] res = new ScannedRobot[3];

        // TODO: obter dimensões reais (estas são as default)
        int width = 800;
        int height = 600;

        // encontrar robots nos quadrantes
        for (ScannedRobot r : this.scannedRobots.values()) {
            if (r.isStopped() == false) continue; // robot tem de estar parado
            double x = r.getPos().getX();
            double y = r.getPos().getY();
                 if (x <= width / 2 && y >  height / 2) res[0] = r; // 1o quadrante
            else if (x >  width / 2 && y >  height / 2) res[1] = r; // 2o quadrante
            else if (x >  width / 2 && y <= height / 2) res[2] = r; // 3o quadrante
        }

        if (res[0] == null || res[1] == null || res[2] == null) return null;
        return res;
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        Point2D p = new Point2D.Double(this.getX(), this.getY());
        ScannedRobot s = new ScannedRobot(p, this.getHeading(), e);
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
        if(cd.getName().equals("emMovimento")){
            double x_atual = getX();
            double y_atual = getY();
            if (this.fase >= 2 && this.fase <= 11) {
                double dist = Math.sqrt(Math.pow((x_atual -this.x_ultimo),2) + Math.pow((y_atual - this.y_ultimo), 2));
                this.distanciaPercorrida += dist;
            }
            this.x_ultimo = x_atual;
            this.y_ultimo = y_atual;

        }

        if(cd.getName().equals("IsRacing"))
            this.odometer.getRaceDistance();
    }

    public void onHitRobot(HitRobotEvent e){
        // apenas fazer algo se o robot for parado, o que apenas acontece quando o sistema determina que a colisão foi a culpa dele
        if (e.isMyFault() == true) {
            setTurnRight(45);
            back(100);
        }
    }

    public void onHitWall(HitWallEvent e) {
        if (this.fase <= 1 || this.fase >=11) return; // não fazer nada se estiver a ir para o canto
        if(Math.abs(e.getBearing()-this.getHeading())>90) ahead(50);
        else back(50);
    }

}
