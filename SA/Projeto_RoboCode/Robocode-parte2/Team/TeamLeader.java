package Team;

import robocode.*;
import Team.Util;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import robocode.util.Utils;
import java.util.*;

public class TeamLeader extends TeamRobot {
    Map<String,InfoRobot> myEnemies= new HashMap<>();

    public  void run(){
        //Mover para a posicao de partida.
        //Util.move(this,new Point2D.Double(18,18));
        //turnLeft(this.getHeading());

        // Inicializacao dos Odometros
        /*addCustomEvent(odometer);
        addCustomEvent(new Condition("emMovimento") {
            @Override
            public boolean test() {
                return (getTime()!=0);
            }
        });
        */

        initialize();

        while(true){
           turnRight(2);
        }

    }

    public void onScannedRobot(ScannedRobotEvent e) {
    String scanned_name = e.getName();
    Util.findCoords(this, e.getBearing(),e.getDistance());
	String teammates[]=this.getTeammates();
	int flag=0;
	for(int i=0;i<teammates.length;i++){
	if(scanned_name.equals(teammates[i])) flag=1;	
	}
	if(flag==0 && this.getEnergy()>10){
    double bulletPower = 3;
    double headOnBearing = getHeadingRadians() + e.getBearingRadians();
    double linearBearing = headOnBearing + Math.asin(e.getVelocity() / Rules.getBulletSpeed(bulletPower) * Math.sin(e.getHeadingRadians() - headOnBearing));
    turnGunRightRadians(Utils.normalRelativeAngle(linearBearing - getGunHeadingRadians()));
    fire(bulletPower);
	}
    }

    public void onCustomEvent(CustomEvent ev) {

    }

    public void onHitRobot(HitRobotEvent e){
        // apenas fazer algo se o robot for parado, o que apenas acontece quando o sistema determina que a colisão foi a culpa dele
        if (e.isMyFault() == true) {
            setTurnRight(45);
            back(100);
        }
        // mover para target
        Util.move(this, new Point2D.Double(500,500));
    }



    public void updateMapInfo(ScannedRobotEvent e){

        final String name = e.getName();

        InfoRobot inform = this.myEnemies.get(name);

        if(inform == null){
            inform = new InfoRobot(this,e);
            this.myEnemies.put(name,inform);
        }

        Point2D ponto = Util.findCoords(this,e.getBearing(),e.getDistance());

        inform.updateInfoRobot(ponto,e);

    }



    private void initialize() {
        // Let the robot body, gun, and radar turn independently of each other
        setAdjustRadarForGunTurn(true);
        setAdjustGunForRobotTurn(true);

        // Set robot colors
        setBodyColor(new Color(0x5C, 0x33, 0x17)); // Chocolate Brown
        setGunColor(new Color(0x45, 0x8B, 0x74)); // Aqua Marine
        setRadarColor(new Color(0xD2, 0x69, 0x1E)); // Orange Chocolate
        setBulletColor(new Color(0xFF, 0xD3, 0x9B)); // Burly wood
        setScanColor(new Color(0xCA, 0xFF, 0x70)); // Olive Green
    }




}
