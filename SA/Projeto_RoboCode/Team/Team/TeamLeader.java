package Team;

import robocode.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import robocode.util.Utils;

public class TeamLeader extends TeamRobot {
    /*
    Map<String,InfoRobot> myEnemies= new HashMap<>();
    Map<String,InfoRobot> myTeam8s = new HashMap<>();
    */

    Map<String,InfoRobot> robots = new HashMap<>();

    private boolean hampus = false;

    //Quando a energia do lider baixar deste valor, é feita uma eleição para um novo lider
    static final double Constante_Eleicao = 10;

    private boolean amIleader= false;

    public  void run(){
        if(this.getName().endsWith("(1)")){
            this.amIleader=true;
            System.out.println("SOU O LIDER ");



            //Codigo para escolher qual o alvo
            Random r = new Random();
            int x = r.nextInt(10);
            int y = r.nextInt(10);
            String s = "INIMIGO 99";
            informar_alvo(s,x,y);

        }

        initialize();

        while(true){
            turnRight(2);
            setTurnRadarRight(Double.POSITIVE_INFINITY);
            if(this.amIleader == true && this.getEnergy() <= Constante_Eleicao) {
                pedir_eleicao();
            }
            Vector2D dir = getOptimalDirection();
            move(new Point2D.Double(getX() + dir.x * 100, getY() + dir.y * 100));
            execute();
        }

    }

    private Vector2D getOptimalDirection() {
        double forceX = 0.0;
        double forceY = 0.0;
        double x = getX();
        double y = getY();
        for (InfoRobot r : this.robots.values()) {
            double weight = (r.isTeammate() ? 10 : -10);
            double dx = r.getX() - x;
            double dy = r.getY() - y;
            double distSquared = Math.pow(dx, 2) + Math.pow(dy, 2);
            forceX += weight * dx / distSquared;
            forceY += weight * dy / distSquared;
            // evitar colisões
            forceX -= 100000 * dx / Math.pow(distSquared, 2);
            forceY -= 100000 * dy / Math.pow(distSquared, 2);
        }
        // desviar das paredes
        forceX += 100000000 / Math.pow(x, 3);
        forceY += 100000000 / Math.pow(y, 3);
        forceX -= 100000000 / Math.pow(getBattleFieldWidth() - x, 3);
        forceY -= 100000000 / Math.pow(getBattleFieldHeight() - y, 3);

        return new Vector2D(forceX, forceY).normalize();
    }

    private void move(Point2D pos) {
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
        updateMapInfo(e);
        String scanned_name = e.getName();
        Util.findCoords(this, e.getBearing(),e.getDistance());
        String teammates[]=this.getTeammates();
        int flag=0;
        for(int i=0;i<teammates.length;i++) {
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


    public Collection<InfoRobot> getEnemies(){
        Collection<InfoRobot> enemies = new ArrayList<>();

        for(InfoRobot i : robots.values()){
            if(!i.isTeammate())
                enemies.add(i);
        }

        return enemies;
    }


    public Collection<InfoRobot> getTeam(){
        Collection<InfoRobot> team = new ArrayList<>();

        for(InfoRobot i : robots.values()){
            if(i.isTeammate())
                team.add(i);
        }

        return team;
    }

    public void pedir_eleicao(){

        System.out.println("Vou agora pedir para mudar de lider!");
        //Este codigo é para avisar que morreu
        Eleicao morri = new Eleicao(this.getEnergy());
        if(this.amIleader) {
            try {
                System.out.println("Vou enviar em BROADCAST MUDANCA LIDER");
                broadcastMessage(morri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void onHitRobot(HitRobotEvent e){
        // apenas fazer algo se o robot for parado, o que apenas acontece quando o sistema determina que a colisão foi a culpa dele
        if (e.isMyFault() == true) {
            setTurnRight(45);
            back(100);
        }
    }

    public void onRobotDeath(RobotDeathEvent e){
        String who_died = e.getName();
        if (this.robots.containsKey(who_died)) {
            this.robots.remove(who_died);
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

        //Se nao for da equipa, esse passa a ser o alvo -> Solução Provisória
        if(!isTeammate(e.getName())){
            Point2D ponto = Util.findCoords(this,e.getBearing(),e.getDistance());
            informar_alvo(e.getName(),ponto.getX(),ponto.getY());
        }
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



    //Método que irá informar os outros team8s sobre o alvo que agora deverão atacar
    public void informar_alvo(String name, double x, double y){
        //System.out.println("Vou agr criar o alvo");
        InfoAlvo novo_alvo = new InfoAlvo(name,x,y);

        try {
           // System.out.println("Vou enviar o meu alvo em broadcast!!!");
            broadcastMessage(novo_alvo);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMessageReceived(MessageEvent event) {
        Serializable mensagem = event.getMessage();
        String sender = event.getSender();
        //System.out.println("O sender desta mensagem é " + sender);
        //System.out.println("---- " + mensagem.getClass());


        if (mensagem instanceof InfoAlvo ){
            //System.out.println("O meu alvo agr é " + ((InfoAlvo) mensagem).getNome_alvo() + " e está em ( " + ((InfoAlvo) mensagem).getPos_x_alvo() + " , " + ((InfoAlvo) mensagem).getPos_y_alvo() + " )");
        }
        if (mensagem instanceof Eleicao){

            //Qd se recebe Eleicao e Signal==-1, tem q se avisar dos outros da nossa vida
            if(((Eleicao) mensagem).getSignal() == -1){

                System.out.println("O nosso lider morreu!!");

                //Quando so temos 1 team8 e ele diz q temos pouca vida, somos nos o lider
                if(getTeammates().length==1){
                    this.amIleader=true;
                    System.out.println("Como vou ficar sozinho, eu sou o lider!!");
                }

                else{

                    //Comportamento quando temos 1 ou mais  team8s
                    //Significa que o lider morreu. Temos que avisar os outros da nossa vida
                    Eleicao novo_lider = new Eleicao(this.getEnergy());
                    try {
                        broadcastMessage(novo_lider);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            else{

            //Significa que é para atribuir o novo lider

                System.out.println("Agora é para mudar de lider!!!");
                System.out.println(" -------- " + ((Eleicao) mensagem).getSignal());

                System.out.println("Tenho de Energia " + this.getEnergy() + " e recebi " + ((Eleicao) mensagem).getSignal());
                if(((Eleicao) mensagem).getSignal() > 0 && this.getEnergy() >= ((Eleicao) mensagem).getSignal()){
                    this.amIleader=true;
                    System.out.println("O lider agora é " + this.getName());
                }
                else{
                    this.amIleader=false;
                }
            }
        }
    }
}
