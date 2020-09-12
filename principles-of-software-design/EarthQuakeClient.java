import java.util.*;
import edu.duke.*;

public class EarthQuakeClient {
    public EarthQuakeClient() {
    }

    public ArrayList<QuakeEntry>
    filterByMagnitude(ArrayList<QuakeEntry> quakeData, double magMin) {
        ArrayList<QuakeEntry> answer = new ArrayList<QuakeEntry>();
        if (quakeData == null || quakeData.isEmpty()) return answer;

        for (QuakeEntry qe : quakeData)
            if (qe.getMagnitude() > magMin)
                answer.add(qe);

        return answer;
    }

    public ArrayList<QuakeEntry>
    filterByDistanceFrom(ArrayList<QuakeEntry> quakeData, double distMax, Location from) {
        ArrayList<QuakeEntry> answer = new ArrayList<QuakeEntry>();
        if (quakeData == null || quakeData.isEmpty()) return answer;
        if (from == null) return answer;

        for (QuakeEntry qe : quakeData)
            if (qe.getLocation().distanceTo(from) < distMax)
                answer.add(qe);

        return answer;
    }

    public ArrayList<QuakeEntry>
    filterByDepth(ArrayList<QuakeEntry> quakeData, double minDepth, double maxDepth) {
        ArrayList<QuakeEntry> answer = new ArrayList<QuakeEntry>();
        if (quakeData == null || quakeData.isEmpty()) return answer;
        // make sure minDepth really is <= maxDepth
        if (minDepth > maxDepth) {
            double temp = minDepth;
            minDepth = maxDepth;
            maxDepth = temp;
        }

        for (QuakeEntry qe : quakeData)
            if (minDepth < qe.getDepth() && qe.getDepth() < maxDepth)
                answer.add(qe);

        return answer;
    }

    private boolean
    phraseMatches (String text, String where, String phrase) {
        if (where.equals("start"))
            return text.startsWith(phrase);
        else if (where.equals("end"))
            return text.endsWith(phrase);
        else
            return text.contains(phrase);
    }

    public ArrayList<QuakeEntry>
    filterByPhrase(ArrayList<QuakeEntry> quakeData, String where, String phrase) {
        ArrayList<QuakeEntry> answer = new ArrayList<QuakeEntry>();
        // verify parameters
        if (quakeData == null || quakeData.isEmpty()) return answer;
        if (where == null || !(where.equals("start") || where.equals("end") || where.equals("any")))
            return answer;
        if (phrase == null || phrase.isEmpty()) return answer;

        for (QuakeEntry qe : quakeData)
            if (phraseMatches(qe.getInfo(), where, phrase))
                answer.add(qe);

        return answer;
    }

    public void dumpCSV(ArrayList<QuakeEntry> list){
        System.out.println("Latitude,Longitude,Magnitude,Info");
        for(QuakeEntry qe : list){
            System.out.printf("%4.2f,%4.2f,%4.2f,%s\n",
                qe.getLocation().getLatitude(),
                qe.getLocation().getLongitude(),
                qe.getMagnitude(),
                qe.getInfo());
        }

    }

    public void bigQuakes() {
        EarthQuakeParser parser = new EarthQuakeParser();
        //String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        String source = "test/data/nov20quakedata.atom";
        //String source = "test/data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list  = parser.read(source);
        System.out.println("read data for "+list.size()+" quakes");
        list = filterByMagnitude(list, 5.0);
        for (QuakeEntry qe : list)
            System.out.println(qe);
        System.out.println("Found " + list.size() + " quakes that match that criteria");
    }

    public void closeToMe(){
        EarthQuakeParser parser = new EarthQuakeParser();
        //String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        //String source = "test/data/nov20quakedata.atom";
        String source = "test/data/nov20quakedatasmall.atom";
        ArrayList<QuakeEntry> list  = parser.read(source);
        System.out.println("read data for "+list.size()+" quakes");

        // This location is Durham, NC
        //Location city = new Location(35.988, -78.907);

        // This location is Bridgeport, CA
        Location city =  new Location(38.17, -118.82);

        list = filterByDistanceFrom(list, 1000000, city);
        for (QuakeEntry qe : list) {
            System.out.println(qe.getLocation().distanceTo(city)/1000.0 + " "+ qe.getInfo());
        }
        System.out.println("Found " + list.size() + " quakes that match that criteria");
    }

    public void createCSV(){
        EarthQuakeParser parser = new EarthQuakeParser();
        String source = "test/data/nov20quakedatasmall.atom";
        //String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        ArrayList<QuakeEntry> list  = parser.read(source);
        dumpCSV(list);
        System.out.println("# quakes read: " + list.size());
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
    }

    public void quakesOfDepth() {
        EarthQuakeParser parser = new EarthQuakeParser();
        String source = "test/data/nov20quakedatasmall.atom";
        //String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        ArrayList<QuakeEntry> list  = parser.read(source);
        System.out.println("read data for " + list.size() + " quakes");
        double minDepth = -10000.0;
        double maxDepth = -5000.0;
        System.out.println("Find quakes with depth between "+minDepth+" and "+maxDepth);
        list = filterByDepth(list, minDepth, maxDepth);
        for (QuakeEntry qe : list) {
            System.out.println(qe);
        }
        System.out.println("Found "+list.size()+" quakes that match that criteria");
    }

    private void printPhraseStep (ArrayList<QuakeEntry> list, String where, String phrase) {
        ArrayList<QuakeEntry> filtered = filterByPhrase(list, where, phrase);
        for (QuakeEntry qe : filtered)
            System.out.println(qe);
        System.out.println("Found "+filtered.size()+" that match "+phrase+" at "+where);
    }

    public void quakesByPhrase() {
        EarthQuakeParser parser = new EarthQuakeParser();
        String source = "test/data/nov20quakedatasmall.atom";
        //String source = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/all_week.atom";
        ArrayList<QuakeEntry> list  = parser.read(source);
        System.out.println("read data for " + list.size() + " quakes");
        printPhraseStep(list, "end", "California");
        printPhraseStep(list, "any", "Can");
        printPhraseStep(list, "start", "Explosion");
    }
    
}