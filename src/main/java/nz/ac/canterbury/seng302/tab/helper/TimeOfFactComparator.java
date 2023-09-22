package nz.ac.canterbury.seng302.tab.helper;

import nz.ac.canterbury.seng302.tab.entity.fact.Fact;

import java.util.Comparator;

public class TimeOfFactComparator implements Comparator<Fact> {

    /**
     * Adapted from https://howtodoinjava.com/java/sort/stream-sort-with-null-values/
     * @param fact1 the first fact to be compared.
     * @param fact2 the second fact to be compared.
     * @return
     */
    @Override
    public int compare(Fact fact1, Fact fact2) {
        if (fact1.getTimeOfEvent() == null && fact2.getTimeOfEvent() == null) {
            return 0;
        } else if(fact1.getTimeOfEvent() == null) {
            return -1;
        } else if(fact2.getTimeOfEvent() == null) {
            return 1;
        } else {
            Integer f1Time = Integer.parseInt(fact1.getTimeOfEvent());
            Integer f2Time = Integer.parseInt(fact2.getTimeOfEvent());
            return f1Time.compareTo(f2Time);
        }
    }
}
