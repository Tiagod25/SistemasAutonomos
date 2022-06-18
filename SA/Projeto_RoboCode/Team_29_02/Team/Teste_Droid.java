package Team;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import Team.TeamLeader_API;
import Team.Attack_API;

public class Teste_Droid extends  TeamRobot implements Droid {

    Map<String,InfoRobot> robots = new HashMap<>();


    private boolean ambush = false;

    private String local_enemy="";
    private String global_enemy="";

    //Quando a energia do lider baixar deste valor, é feita uma eleição para um novo lider
    static final double Constante_Eleicao = 20;

    private boolean amIleader= false;

/*
    public  void run(){
        int i=0;

        initialize();
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

    */


    public  void run(){
        System.out.println("Sou da classe " + this.getClass().toString());
        if(this.getName().endsWith("(1)") && !this.getClass().toString().endsWith("Droid")){
            this.amIleader=true;
            System.out.println("SOU O LIDER ");



        }

        initialize();

        while(true){
            setTurnRadarRight(Double.POSITIVE_INFINITY);

            //Quando não tenho nenhuma missao
            //String myNextAttack = Attack_API.nextAttack(this);

            if(ambush == false && this.amIleader == true) {
                //Codigo para ir atacar o inimigo global
                //String globalTarget = TeamLeader_API.getTarget(this);
                //ambush=true;
                //informar_alvo(globalTarget);
            }


            if(getX() <= 30 || getX() <= (getBattleFieldWidth()- 30) || getY()<= 30 || getY() >= (getBattleFieldHeight() - 30) ){
                setTurnRight(60);
                ahead(100);
            }

            //Vector2D dir = getOptimalDirection();
            //move(new Point2D.Double(getX() + dir.x * 80, getY() + dir.y * 80));

			setTurnRight(9999999);
            ahead(85);
            execute();
        }

    }


    private void initialize() {
        // Let the robot body, gun, and radar turn independently of each other
        //setAdjustRadarForGunTurn(true);
        //setAdjustGunForRobotTurn(true);

        // Set robot colors
        setBodyColor(new Color(0x5C, 0x33, 0x17)); // Chocolate Brown
        setGunColor(new Color(0x45, 0x8B, 0x74)); // Aqua Marine
        setRadarColor(new Color(0xD2, 0x69, 0x1E)); // Orange Chocolate
        setBulletColor(new Color(0xFF, 0xD3, 0x9B)); // Burly wood
        setScanColor(new Color(0xCA, 0xFF, 0x70)); // Olive Green
    }


    private Vector2D getOptimalDirection() {
        double forceX = 0.0;
        double forceY = 0.0;
        double x = getX();
        double y = getY();
        for (InfoRobot r : this.robots.values()) {
            double weight = (r.isTeammate() ? 100 : -100);
            Point2D p =  new Point2D.Double(r.getX(),r.getY());
            double dx = p.getX() - x;
            double dy = p.getY() - y;
            double distSquared = Math.pow(dx, 2) + Math.pow(dy, 2);
            forceX += weight * dx / distSquared;
            forceY += weight * dy / distSquared;
        }
        // desviar das paredes
        forceX += 100000 / Math.pow(x, 3);
        forceY += 100000 / Math.pow(y, 3);
        forceX -= 100000 / Math.pow(getBattleFieldWidth() - x, 3);
        forceY -= 100000 / Math.pow(getBattleFieldHeight() - y, 3);

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

    public void onRobotDeath(RobotDeathEvent e){
        String who_died = e.getName();
        if (this.robots.containsKey(who_died)) {
            this.robots.remove(who_died);
        }

        if(who_died.equals(global_enemy)){
            ambush=false;
            this.global_enemy="";
        }

        if(who_died.equals(local_enemy))
            local_enemy="";
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

        /*
        if(Math.abs(e.getBearing()-this.getHeading())>90) back(50);
        else ahead(50);

         */

        if(e.getBearing() > -90 && e.getBearing() <= 90){
                back(50);
            }

        else{
            ahead(50);
        }
    }


    public void updateMapInfo(ScannedRobotEvent e){

        final String name = e.getName();
        System.out.println("O nome do gajo escaneado é " + name);

        if (this.robots.containsKey(name)) {
            Point2D ponto = Util.findCoords(this,e.getBearing(),e.getDistance());
            this.robots.get(name).updateInfoRobot(ponto, e);
        } else {
            this.robots.put(name, new InfoRobot(this, e));
        }

    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Serializable mensagem = event.getMessage();
        String sender = event.getSender();
        //System.out.println("O sender desta mensagem é " + sender);
        //System.out.println("---- " + mensagem.getClass());


        if (mensagem instanceof InfoAlvo) {
            System.out.println("O meu alvo agr é " + ((InfoAlvo) mensagem).getNome_alvo() + "");
            this.global_enemy = ((InfoAlvo) mensagem).getNome_alvo();
            double dx = ((InfoAlvo) mensagem).getPos_x_alvo() - this.getX();
            System.out.println("O meu dx é " + dx);
            double dy = ((InfoAlvo) mensagem).getPos_y_alvo() - this.getY();
            System.out.println("O meu dy é " + dx);
            double theta = Math.toDegrees(Math.atan2(dx, dy));
            turnRight(Utils.normalRelativeAngleDegrees(theta - getGunHeading()));
            fire(2);
            back(10);

        }

    }

}
