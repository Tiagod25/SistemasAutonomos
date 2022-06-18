package Team;

/*
* API for RecordScratch Methods
* @author Joao Costa
* @Version 1.0
*/
import Team.RecordScratch;
import robocode.AdvancedRobot;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public final class TeamLeader_API {

    //FIXME: When execute a Test or a Battle change name of class to the name of your robot!

    //Weights
    private static final double Wi = 0.44; // Weight isolation
    private static final double Wp = 0.44; // Weight proximity
    private static final double We = 0.10; // Weight energy
    private static final double Wb = 0.02; // Weight bias
    private static final double Wx = 0.6; // Weight experince


    //Secondary Weights
    private static final double Wd = 0.4; // Weight distance
    private static final double Wm = 0.4; // Weight mean
    private static final double Ws = 0.75; // Weight of the Map Scaler (e.g 0.75 = Best Isolation is 75% Map of distance)!

    // Threshold that defines since when we accept the ambush
    private static final double THRESHOLD = 0; // Threshold that defines if we could Ambush or its too risky.


    private static double DSCALER = 1; // Scaler from px to [0,1]
    private static double ESCALER = 100; // Scaler from px to [0,1]




    // Method that gives us the degree of isolation of all enemies
    private static List<Pair> getIsolation(RecordScratch r){
        List<Pair> res = new ArrayList<>();
        List<List<Tuple>> dists = new ArrayList<>();

        if(r.getEnemies().size()==1){
            System.out.println("So existe 1 inimigo! Atacar!");
            String target = r.getEnemies().iterator().next().getNome();
            double iso = 1;
            res.add(new Pair(target,iso));
            return res;
        }
        for (InfoRobot i: r.getEnemies()) {
            List<Tuple> d = getDistsEnemies(i,r.getEnemies());
            dists.add(d);
        }
        for (List<Tuple> lp: dists) {
            if(lp.size()> 0) {
                String s = lp.get(0).getRobot1();
                double d = 99999999;
                double m = 0.0;
                for (Tuple t : lp) {
                    if (t.getDist() < d) d = t.getDist();
                    m += t.getDist();
                }
                if (lp.size() > 0) m = m / (lp.size());
                else m = 0.0;
                double ISOLATION = Wd * scale(d, DSCALER) + Wm * scale(m, DSCALER);
                res.add(new Pair(s, ISOLATION));
            }
        }
         return res;
    }
    private static  List<Tuple> getDistsEnemies(InfoRobot i, Collection<InfoRobot> enemies) {
        List<Tuple> res = new ArrayList<>();
        Point2D.Double pnt = new Point2D.Double(i.getX(),i.getY());
        for (InfoRobot j : enemies) {
            if(!(j.getNome().equals(i.getNome()))){
                double d = pnt.distance(j.getX(),j.getY());
                res.add(new Tuple(i.getNome(),j.getNome(),d));
                //System.out.println("Vou Guardar - " + new Tuple(i.getNome(),j.getNome(),d).toString());
            }
        }
        return  res;
    }

    // Method that gives us the proximity of each teammate to an especific target
    private static List<Pair> getTeamProximity(RecordScratch r){
        List<Pair> res = new ArrayList<>();
        List<List<Tuple>> tuples = new ArrayList<>();

        for (InfoRobot i: r.getEnemies()) {
            List<Tuple> d = getDistsTeammates(i,r.getTeam());
            tuples.add(d);
        }
        for (List<Tuple> lp: tuples) {
            if(lp.size()>0) {

                String enemy = lp.get(0).getRobot1();
                double ds = 99999999;
                double mean = 0.0;
                for (Tuple t : lp) {
                    if (t.getDist() < ds) ds = t.getDist();
                    mean += t.getDist();
                }
                if (lp.size() > 0) mean = mean / (lp.size());
                else mean = 0.0;
                double PROXIMITY = Wd * scale(ds, DSCALER) + Wm * scale(mean, DSCALER);
                res.add(new Pair(enemy, PROXIMITY));
            }
        }
        return res;
    }
    private static List<Tuple> getDistsTeammates(InfoRobot i, Collection<InfoRobot> teammates) {
        List<Tuple> res = new ArrayList<>();
        Point2D.Double pnt = new Point2D.Double(i.getX(),i.getY());
        for (InfoRobot e : teammates) {
            double dis = pnt.distance(e.getX(),e.getY());
            res.add(new Tuple(i.getNome(),e.getNome(),dis));
        }
        return res;
    }

    // Method that gives us the energy of the enemies
    private static List<Pair> getEnergy(RecordScratch r){
        List<Pair> res = new ArrayList<>();

        for (InfoRobot i: r.getEnemies()) {
            double energy = i.getEnergy() ;

            res.add(new Pair(i.getNome(),scale(energy,ESCALER)));
        }
        return res;
    }


    // Method that finds the target to Ambush
    public static String getTarget(AdvancedRobot r){

        DSCALER = Math.sqrt( Math.pow(r.getBattleFieldWidth(),2) + Math.pow(r.getBattleFieldHeight(),2) )  * Ws;

        List<Pair> I = getIsolation((RecordScratch) r);
        List<Pair> P = getTeamProximity((RecordScratch) r);
        List<Pair> E = getEnergy((RecordScratch) r);

        List<Pair> PossibleTargets = new ArrayList<>();
        String None = "None";
        Pair target;

        List<InfoRobot> info_robots = new ArrayList<>(((RecordScratch) r).getEnemies());
        List<String> robot = new ArrayList<>();
        for (InfoRobot ir: info_robots ) {
            robot.add(ir.getNome());
        }

        for (String s: robot) {
        //for (String s: tst) {
            Pair pi = I.stream().filter(pair -> s.equals(pair.getFirst())).findAny()
                    .orElse(null);

            Pair pp = P.stream().filter(pair -> s.equals(pair.getFirst())).findAny()
                    .orElse(null);
            Pair pe = E.stream().filter(pair -> s.equals(pair.getFirst())).findAny()
                    .orElse(null);
            double i=0,p=0.3,e=1,b=Bias();
            if (pi != null ) i = pi.getSecond();
            if (pp != null ) p = pp.getSecond();
            if (pe != null ) e = pe.getSecond();


            double A = Wi*i + Wp*p - We*e + Wb*b; // TODO: Add Experience
            System.out.println("(" + s + ", " +  A + "," + b +")");
            if (A > THRESHOLD) PossibleTargets.add(new Pair(s,A));
        }

        if(PossibleTargets.size() == 0 ){
            System.out.println("No robots to ambush");
            /*
            //---------------------------------
            // Print Area
            System.out.println("#### BEGIN ####");
            System.out.println("- ISOLATION -");System.out.println(I);System.out.println("- - -");
            System.out.println("- PROXIMITY -");System.out.println(P);System.out.println("- - -");
            System.out.println("- ENERGY -");System.out.println(E);System.out.println("- - -");
            System.out.println("### END ###");
            //--------------------------------

             */

            return None;
        }
        else{
            target = PossibleTargets.get(0);
            for (Pair pair: PossibleTargets) {
                if (pair.getSecond() > target.getSecond() ) target = pair;

            }
            /*
            //---------------------------------
            // Print Area
            System.out.println("#### BEGIN ####");
            System.out.println("- ISOLATION -");System.out.println(I);System.out.println("- - -");
            System.out.println("- PROXIMITY -");System.out.println(P);System.out.println("- - -");
            System.out.println("- ENERGY -");System.out.println(E);System.out.println("- - -");
            System.out.println("= AMBUSH =");System.out.println(PossibleTargets);System.out.println("- - -");
            System.out.println("### END ###");
            //--------------------------------

             */
            return target.getFirst();
        }
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
    
    //Class wich objective is keep the intermediate values of the functions
    private static class Tuple {
        private String robot1;
        private String robot2;
        private Double dist;

        public Tuple(String robot1,String robot2, double dist){this.robot1=robot1;this.robot2=robot2;this.dist=dist;}
        public Tuple(){ this.robot1="";this.robot2="";this.dist=0.0;}
        public Double getDist() {return dist;}
        public String getRobot1() {return robot1;}
        public String getRobot2() {return robot2;}
        public void setDist(Double dist) {this.dist = dist;}
        public void setRobot1(String robot1) {this.robot1 = robot1;}
        public void setRobot2(String robot2) {this.robot2 = robot2;}
        public boolean isEqual(Tuple t){
            if (t.getDist() == this.dist && t.getRobot1().equals(this.robot1) && t.getRobot2().equals(this.robot2))
                return true;
            return false;
        }

        @Override
        public String toString() {
            return "(" +
                    "r1:" + robot1 +
                    ", r2:" + robot2 +
                    ", d=" + dist +
                    ')';
        }
    }




}
