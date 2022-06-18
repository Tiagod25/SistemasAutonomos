package Team;

import robocode.*;
import robocode.util.Utils;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import Team.TeamLeader_API;
import Team.Attack_API;

public class CircleDroid extends  TeamRobot implements Droid {

    Map<String,InfoRobot> robots = new HashMap<>();
    Map<Integer,String> bullets= new HashMap<>();


    private boolean ambush = false;

    private String local_enemy="";
    private String global_enemy="";

    //Quando a energia do lider baixar deste valor, é feita uma eleição para um novo lider
    static final double Constante_Eleicao = 20;

    private boolean amIleader= false;




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
            String myNextAttack = Attack_API_Droid.nextAttack(this);
            this.local_enemy=myNextAttack;

            if(getX() <= 30 || getX() <= (getBattleFieldWidth()- 30) || getY()<= 30 || getY() >= (getBattleFieldHeight() - 30) ){
                setTurnRight(60);
                ahead(100);
            }

			setTurnRight(9999999);
            setAhead(85);
            updateDisparo();
            execute();
        }

    }


    private void initialize() {
        // Let the robot body, gun, and radar turn independently of each other
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        // Set robot colors
        setBodyColor(new Color(  243, 156, 18  )); // Orage
        setGunColor(new Color(  46, 204, 113  )); // Grey
        setRadarColor(new Color(255, 112, 77)); // Orange Chocolate
        setBulletColor(new Color( 255, 255, 0 )); // yellow
        setScanColor(new Color( 247, 220, 111 )); // yellow
    }


    public String getLocal_enemy(){
        return local_enemy;
    }

    private void updateDisparo() {
        int flag=0;
        String nomeAlvo = getLocal_enemy();
        InfoRobot alvo = this.robots.get(nomeAlvo);
        if (alvo == null) return;

        Vector2D alvoPos = alvo.getPos();
        Vector2D pos = new Vector2D(getX(), getY());

        double distancia = alvoPos.subtract(pos).length();
        double energia = alvo.getEnergy();
        double speed = alvo.getVelocidade();

        double bulletPower = calcBulletPower(distancia, energia, speed);
        double bulletSpeed = Rules.getBulletSpeed(bulletPower);

        // estimativa de rotação da arma necessária
        double headOnBearing = Math.PI / 2 - alvoPos.subtract(pos).angle();
        double relativeBearing = headOnBearing - (Math.PI / 2 - pos.angle());

        // calcular posição no fim de rodar a arma
        double timeToAim = relativeBearing / (20 * Math.PI / 180); // 20 graus por turno
        Vector2D velocity = Vector2D.fromPolarCoords(Math.PI / 2 - getHeadingRadians(), getVelocity());
        Vector2D distanceAiming = velocity.scale(timeToAim).rotate(Math.PI / 4);
        Vector2D futurePos = pos.add(distanceAiming);

        System.out.println("Velocity X" + velocity.x + " Y: " + velocity.y );


        // calcular novo ângulo
        headOnBearing = Math.PI / 2 - alvoPos.add(distanceAiming).subtract(futurePos).angle();
        relativeBearing = headOnBearing - (Math.PI / 2 - futurePos.angle());


        //	for(InfoRobot mate:this.robots.values()){
        //	if (mate.isTeammate()){
        //	    bearing=Math.toRadians(mate.getHeading())-this.getHeadingRadians();
        //	if (linearBearing+0.04>bearing && linearBearing-0.04<bearing) flag=1;
        //System.out.println("NAO PODE");
        //     }
        //    }
        //	if(flag==0){

        double linearBearing = headOnBearing + alvo.getFactor() * Math.asin(speed / bulletSpeed * Math.sin(relativeBearing - headOnBearing));

        setTurnGunRightRadians(Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
        Bullet bul= setFireBullet(bulletPower);
        if (bul!=null) {
            this.bullets.put(bul.hashCode(), nomeAlvo);
        }
    }

    public String getGlobal_enemy() {
        return global_enemy;
    }


    public Collection<InfoRobot> getEnemies(){
        Collection<InfoRobot> enemies = new ArrayList<>();

        for(InfoRobot i : robots.values()){
            if(!i.isTeammate())
                enemies.add(i);
        }

        return enemies;
    }

    private double calcBulletPower(double distance, double energy, double rapidez) {
        double bulletPower;
        if (distance<200||(rapidez==0 && distance<250)) bulletPower=3;
        else if (distance<350) bulletPower=2;
        else if (energy<10) bulletPower=0.1;
        else bulletPower=1;
        return bulletPower;
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
        String mates[]=this.getTeammates();
        String scanned_name=e.getName();
        int flag=0;
        if(mates != null) {
            for (int i = 0; i < mates.length; i++) {
                if (mates[i].equals(scanned_name)) flag = 1;
            }
        }
        if (flag == 0) {
			double change = (e.getBearing()+(this.getHeading()-this.getGunHeading())) % 360;
        	if (change >= -180 && change <= 180) {
            	turnRight(change);
        	} else if (change < -180) {
            	turnRight(change + 360);
        	} else {
            	turnLeft(360 - change);
			}
            fire(3);
            System.out.println("mandei um tiro");
        } else {
			if(e.isMyFault() == true){
            	setTurnRight(45);
            	back(100);
			}
    	}
    }


    public void onHitWall(HitWallEvent e) {


        if(e.getBearing() > -90 && e.getBearing() <= 90){
                back(50);
            }

        else{
            ahead(50);
        }
    }

    public void update_informacoes(InfoRobot ir){
        final String name = ir.getNome();
        if (this.robots.containsKey(name)) {
            this.robots.get(name).updateInfoAlvo(ir);
        } else {
            this.robots.put(name, ir);
        }


    }

    public void onBulletMissed(BulletMissedEvent event){
        Bullet bullet = event.getBullet();
        String nomeAlvo = this.bullets.get(bullet.hashCode());
        InfoRobot alvo = this.robots.get(nomeAlvo);
        if(alvo != null){
            double fator = alvo.getFactor();
            alvo.setFactor(fator*0.99);
        }
    }


    public void updateMapInfo(ScannedRobotEvent e){

        final String name = e.getName();

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
            turnGunRight(Utils.normalRelativeAngleDegrees(theta - getGunHeading()));
            setFire(2);
            setBack(10);
        }

        if(mensagem instanceof InfoRobot){
            update_informacoes((InfoRobot) mensagem);
        }


    }

}
