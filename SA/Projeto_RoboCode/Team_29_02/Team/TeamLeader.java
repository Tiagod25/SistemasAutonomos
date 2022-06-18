package Team;

import robocode.*;

import Team.TeamLeader_API;
import Team.Attack_API;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import robocode.util.Utils;


public class TeamLeader extends TeamRobot {
    /*
    Map<String,InfoRobot> myEnemies= new HashMap<>();
    Map<String,InfoRobot> myTeam8s = new HashMap<>();
    */
	Map<Integer,String> bullets= new HashMap<>();
    Map<String,InfoRobot> robots = new HashMap<>();

    private boolean ambush = false;

    private String local_enemy="";
    private String global_enemy="";

    //Quando a energia do lider baixar deste valor, é feita uma eleição para um novo lider
    static final double Constante_Eleicao = 20;

    private boolean amIleader= false;
    private boolean leaderFound= false;

    // Rodar no sentido dos ponteiros do relógio ou ao contrário
    // Aleatóreamente alternar a direção
    private double rotation = 1;

    private Vector2D lastPos = null;
    private double lastPosTime = 0;

    public  void run(){
        if(this.getName().endsWith("(1)")){
            this.amIleader=true;
            System.out.println("SOU O LIDER ");


        }

        initialize();

        while(true){
            //turnRight(2);
            setTurnRadarRight(Double.POSITIVE_INFINITY);

            if(this.amIleader == true && this.getEnergy() <= Constante_Eleicao && posso_eleger()) {
                pedir_eleicao();
            }

                //Quando não tenho nenhuma missao
                String myNextAttack = Attack_API.nextAttack(this);
                this.local_enemy = myNextAttack;
                System.out.println("Global Target ----- " + global_enemy);
                System.out.println("My Target ----- " + myNextAttack);

                if(ambush == false && this.amIleader == true && leaderFound ) {
                    //Codigo para ir atacar o inimigo global
                    String globalTarget = TeamLeader_API.getTarget(this);
                    ambush=true;
                    informar_alvo(globalTarget);
                }



            updateMovimento();
            updateDisparo();
            execute();


        }

    }

    private void updateMovimento() {
        // 2.5% de probabilidade de alternar a direção de rotação caso esteja a andar para a frente
        // 20% de alternar caso esteja a andar para trás
        double rand = ThreadLocalRandom.current().nextDouble(0.0, 1.0);
        if (this.rotation == 1) {
            this.rotation = (rand <= 0.025 ? -this.rotation : this.rotation);
        } else {
            this.rotation = (rand <= 0.20 ? -this.rotation : this.rotation);
        }

        Vector2D dir = getOptimalDirection();
        move(new Point2D.Double(getX() + dir.x * 100, getY() + dir.y * 100));
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
        Vector2D futurePos = pos.add(velocity.scale(timeToAim));

        // calcular novo ângulo
        headOnBearing = Math.PI / 2 - alvoPos.subtract(futurePos).angle();
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

    private double calcBulletPower(double distance, double energy, double rapidez) {
        double bulletPower;
        if (distance<200||(rapidez==0 && distance<250)) bulletPower=3;
        else if (distance<350) bulletPower=2;
        else if (energy<10) bulletPower=0.1;
        else bulletPower=1;
        return bulletPower;
    }

    //Este método é para determinar se existe pelo menos um robot que tem vida superior À constante definida
    //Se nenhum colega de equipa tiver vida superior à constante, o melhor é não trocar de lider
    public boolean posso_eleger(){
        String s = null;
        for(InfoRobot ir : this.robots.values()){
            if(ir.getEnergy() >= Constante_Eleicao && isTeammate(ir.getNome()))
                return true;
        }

        return false;
    }


    public String getGlobal_enemy() {
        return global_enemy;
    }

    public String getLocal_enemy(){
        return local_enemy;
    }

    public void setGlobal_enemy(String enemy){
        this.global_enemy=enemy;
    }

    public void setLocal_enemy(String local_enemy) {
        this.local_enemy = local_enemy;
    }

    private Vector2D getOptimalDirection() {
        Vector2D pos = new Vector2D(getX(), getY());
        Vector2D center = new Vector2D(getBattleFieldWidth()/2, getBattleFieldHeight()/2);

        // movimento em espiral à volta do centro
        Vector2D force = pos.subtract(center).rotate(90 + 20).normalize().scale(5);

        // evitar colisões com outros robôs
        for (InfoRobot r : this.robots.values()) {
            Vector2D p = new Vector2D(r.getX(),r.getY());
            Vector2D diff = p.subtract(pos);
            double distSquared = diff.lengthSquared();
            force = force.subtract(diff.scale(this.rotation * 150 / distSquared));
        }

        // aproximar-se do alvo global
        InfoRobot r = this.robots.get(getGlobal_enemy());
        if (r != null) {
            Vector2D rPos = new Vector2D(r.getX(), r.getY());
            Vector2D diff = rPos.subtract(pos);
            force = force.add(diff.scale(this.rotation * 1.0 / 100));
        }

        // desviar das paredes
        force = force.add((new Vector2D(20000 / Math.pow(pos.x, 3), 20000 / Math.pow(pos.y, 3))).scale(this.rotation));
        force = force.subtract((new Vector2D(20000 / Math.pow(getBattleFieldWidth() - pos.x, 3), 20000 / Math.pow(getBattleFieldHeight() - pos.y, 3))).scale(this.rotation));

        // evitar estar no centro
        Vector2D c = center.subtract(pos);
        force = force.subtract(c.scale(this.rotation * 200 / c.lengthSquared()));

        return force.normalize();
    }

    private void move(Point2D pos) {
        double x=pos.getX();
        double y=pos.getY();

        if(pos.getX() <= 100)
            x=150;

        if(x >= (getBattleFieldWidth() - 100))
            x = getBattleFieldWidth() - 150;

        if(y <= 100)
            y=150;

        if(y >= (getBattleFieldHeight() - 100))
            y = getBattleFieldHeight() - 150;

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
        setAhead(this.rotation * Math.sqrt(Math.pow(this.getX()-pos.getX(), 2) + Math.pow(this.getY()-pos.getY(), 2)));
    }




    public void update_informacoes(InfoRobot ir){
        final String name = ir.getNome();
        if (this.robots.containsKey(name)) {
            this.robots.get(name).updateInfoAlvo(ir);
        } else {
            this.robots.put(name, ir);
            }


    }

    public void onScannedRobot(ScannedRobotEvent e) {
        final String name = e.getName();
        //System.out.println("O nome do gajo escaneado é " + name);

        if (this.robots.containsKey(name)) {
            Point2D ponto = Util.findCoords(this,e.getBearing(),e.getDistance());
            this.robots.get(name).updateInfoRobot(ponto, e);
        } else {
            if(e.getEnergy() > 121 && e.getEnergy()<=200 && leaderFound==false && !isTeammate(e.getName())){
                System.out.println("#Encontrei o Lider#");
                this.global_enemy=e.getName();
                informar_alvo(e.getName());
            }
            this.robots.put(name, new InfoRobot(this, e));
        }

        InfoRobot inform = this.robots.get(name);
        try {
            broadcastMessage(inform);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        //Se nao for da equipa, esse passa a ser o alvo -> Solução Provisória
        /*if(!isTeammate(e.getName())){
            Point2D ponto = Util.findCoords(this,e.getBearing(),e.getDistance());
            informar_alvo(e.getName());
        }*/
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

    public void onHitWall(HitWallEvent e) {
        if(Math.abs(e.getBearing()-this.getHeading())>90) ahead(50);
        else back(50);
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

        if(who_died.equals(global_enemy)){
            ambush=false;
            this.global_enemy="";
        }

        if(who_died.equals(local_enemy))
            local_enemy="";
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
    public void informar_alvo(String name){
        //System.out.println("Vou agr criar o alvo");
        InfoAlvo novo_alvo = new InfoAlvo(name);

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
            //System.out.println("O meu alvo agr é " + ((InfoAlvo) mensagem).getNome_alvo() + " )");
            if(amIleader){
                this.ambush = true;
                this.leaderFound = true;
            }
            this.global_enemy = ((InfoAlvo) mensagem).getNome_alvo();

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
                if(((Eleicao) mensagem).getSignal() > 0 && this.getEnergy() >= ((Eleicao) mensagem).getSignal() && !event.getSender().contains("Droid")){
                    this.amIleader=true;
                    System.out.println("O lider agora é " + this.getName());
                }
                else{
                    this.amIleader=false;
                }
            }
        }

        if(mensagem instanceof InfoRobot){
            System.out.println("Vou atualizar infos do " + ((InfoRobot) mensagem).getNome());
            update_informacoes((InfoRobot) mensagem);
        }

    }
}
