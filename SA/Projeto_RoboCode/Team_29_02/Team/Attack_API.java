package Team;

/*
* API for each Robot Knows Who to Atack now
* @author Joao Costa
* @Version 1.0
*/
import robocode.AdvancedRobot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public final class Attack_API {

    //FIXME: When execute a Test or a Battle change name of class to the name of your robot!

    //Weights
    private static final double Wv = 0.48; // Weight Velocity
    private static final double We = 0.02; // Weight energy
    private static final double Wd = 0.4; // Weight distance


    // Threshold that defines since when we accept the ambush
    private static final double THRESHOLD = 0; // Threshold that defines if we could Ambush or its too risky.
    private static final double LT=0.2; // Weight of the Local Target in the decision;
    private static final double GT=0.8; // Weight of the Global Target in the decision;
    private static double CONSTANTE_PROXIMIDADE_GLOBAL = 250;


    public static String nextAttack(AdvancedRobot r){
        RecordScratch me = (RecordScratch) r;
        String globalTargert = me.getGlobal_enemy();
        List<Transfer> robots = getInfosEnemies(me);
        List<Pair> lt = new ArrayList<>();

        for (Transfer t : robots) {
            double Y;
            if(t.nome.equals(globalTargert)) {
                if(t.d <= CONSTANTE_PROXIMIDADE_GLOBAL)
                    return globalTargert;
                else
                Y = (Wd * t.d + We * t.e + Wv * t.v) -GT ;
            }
            else {
                Y = (Wd * t.d + We * t.e + Wv*t.v) + LT;
            }
            lt.add(new Pair(t.nome,Y));
        }
        if (lt.size()>0) {
            Pair T = lt.iterator().next();
            for (Pair p : lt) {
                if (p.getSecond() < T.getSecond()) T = p;

            }
            return T.getFirst();
        }
        return "None";
    }

    private static List<Transfer> getInfosEnemies(RecordScratch r){
        List<Transfer> res = new ArrayList<>();
        List<Pair> tmp = new ArrayList<>();
        Point2D.Double my_loc = new Point2D.Double(r.getX(),r.getY());
        for (InfoRobot rob : r.getEnemies() ) {
            // FIXME: Podem Existir outros parametros
            res.add(new Transfer (rob.getNome(),rob.getEnergy(),my_loc.distance(rob.getX(),rob.getY()), rob.getVelocidade()));
        }
        return res;
    }

    private static class Transfer {
        String nome;
        double e; //energia
        double d; // distancia
        double v; // Velocidade

        public Transfer(String n,double e, double d,double v) {this.nome=n; this.e=e; this.d=d;this.v=v; }
    }


    private static double scale(double v,double scaler){
        double s = (v)/scaler;
        return s;
    }

    private static double Bias(){
        Random rand = new Random();
        int op = rand.nextInt(4);
        double num1 = Math.random();
        double num2 = Math.random();
        switch (op) {
            case 0:
                return num1/num2;
            case 1:
                return num1+num2;
            case 2:
                return num1-num2;
            case 3:
                return num1*num2;
        }
        return 0;
    }

    // Class that keeps a pair (Robot, Value)
    public static class Pair{
        private String robot;
        private double value;
        public Pair(){
            this.robot = "";
            this.value = 0.0;
        }
        public Pair(String robot, double value){
            this.robot = robot;
            this.value = value;
        }
        public String getFirst(){
            return this.robot;
        }
        public double getSecond(){
            return this.value;
        }
        public void setFirst(String robot){
            this.robot = robot;
        }
        public void setSecond(double value){
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + "r='" + robot + '\'' + ", v=" + value + ')';
        }
    }
}
