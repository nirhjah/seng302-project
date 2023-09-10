package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;
import org.thymeleaf.util.StringUtils;

import java.util.*;

/**
 * Grade level for sports.
 * For example, "Under 13s mens",
 * Or "Senior womens".
 * -
 * IMPORTANT THING TO NOTE:
 * We don't have a repository / service for this class.
 * This is because this entity should ALWAYS be owned by a parent entity,
 * with cascadeType = ALL.
 * (For example, owned by a team.)
 */
@Entity(name = "Grade")
public class Grade {
    public Grade() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;

    public enum Age {
        ADULT("Open", 20, false, false),
        UNDER_5S("Under 5s", 5, true),
        UNDER_6S("Under 6s", 6, true),
        UNDER_7S("Under 7s", 7, true),
        UNDER_8S("Under 8s", 8, true),
        UNDER_9S("Under 9s", 9, true),
        UNDER_10S("Under 10s", 10, true),
        UNDER_11S("Under 11s", 11, true),
        UNDER_12S("Under 12s", 12, true),
        UNDER_13S("Under 13s", 13, true),
        UNDER_14S("Under 14s", 14, true),
        UNDER_15S("Under 15s", 15, true),
        UNDER_16S("Under 16s", 16, true),
        UNDER_17S("Under 17s", 17, true),
        UNDER_18S("Under 18s", 18, true),
        UNDER_19S("Under 19s", 19, true),
        OVER_50S("Over 50s", 50, false, true),
        OVER_60S("Over 60s", 60, false, true),
        OVER_70S("Over 70s", 70, false, true);


        private final boolean isUnder;
        private final boolean isOver;
        private final int age;

        /**
         * Checks whether this grade object can participate in another grade.
         * For example:
         * U16s can participate in U19s.
         * But U14s CANNOT participate in U13s.
         * @param ageRange The ageRange to check if we can participate in
         * @return True if participation allowed, false otherwise.
         */
        private boolean canParticipateIn(Age ageRange) {
            if (ageRange.isUnder) {
                return this.age <= ageRange.age;
            } else if (ageRange.isOver) {
                return this.age >= ageRange.age;
            }
            // Open grade.
            return true;
        }

        private final String description;

        public String getDescription() {
            return description;
        }

        Age(String description, int age, boolean isUnder) {
            this.age = age;
            this.isUnder = isUnder;
            this.isOver = false;
            this.description = description;
        }

        /**
         * Represents an AgeRange for checking whether grades are compatible.
         * For example:
         * U16s can participate in U19s.
         * But U14s CANNOT participate in U13s.
         */
        Age(String description, int age, boolean isUnder, boolean isOver) {
            this.age = age;
            this.isUnder = isUnder;
            this.isOver = isOver;
            this.description = description;
        }

    }

    public enum Sex {
        MENS("Men's"),
        WOMENS("Women's"),
        NONBINARY("Non-Binary"),
        MIXED("Mixed"),
        OTHER("Other");

        private final String description;

        Sex(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum Competitiveness {

        // Unspecified implies Competitive league.
        // This is because serious sportspeople will likely
        // be the mai users of our app.  (Also this is what most people assume too)
        PROFESSIONAL("Professional"),
        SEMIPRO("Semi-Pro"),
        SOCIAL("Social");

        private final String description;

        Competitiveness(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

    }

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Age age;

    @Enumerated(EnumType.STRING)
    private Competitiveness competitiveness;

    public Grade(Age age, Sex sex, Competitiveness competitiveness) {
        this.age = age;
        this.sex = sex;
        this.competitiveness = competitiveness;
    }

    public Grade(Age age, Sex sex) {
        this.age = age;
        this.sex = sex;
        this.competitiveness = DEFAULT_COMPETITIVENESS;
    }

    // Any age under 13 is regarded as "Boy / girl" as opposed to "Men / women"
    private final Set<Age> youngAges = Set.of(
        Age.UNDER_5S,Age.UNDER_6S,Age.UNDER_7S,Age.UNDER_8S,Age.UNDER_9S,Age.UNDER_10S,Age.UNDER_11S,Age.UNDER_12S,Age.UNDER_13S
    );

    /**
     * "Adult" should be the default age range.
     * aka, the open age range.
     * @param sex The sex
     */
    public Grade(Sex sex) {
        this.sex = sex;
        this.age = DEFAULT_AGE;
        this.competitiveness = DEFAULT_COMPETITIVENESS;
    }

    private static Random random = new Random();

    public static Grade randomGrade() {
        List<Sex> array = Arrays.stream(Sex.values()).toList();
        Sex sex = array.get(random.nextInt(array.size()));

        List<Age> array2 = Arrays.stream(Age.values()).toList();
        Age age = array2.get(random.nextInt(array2.size()));

        List<Competitiveness> array3 = Arrays.stream(Competitiveness.values()).toList();
        Competitiveness comp = array3.get(random.nextInt(array3.size()));

        return new Grade(age, sex, comp);
    }

    public static final Age DEFAULT_AGE = Age.ADULT;

    public static final Competitiveness DEFAULT_COMPETITIVENESS = Competitiveness.SOCIAL;

    private String getSexString() {
        boolean isYoung = youngAges.contains(age);
        if (isYoung) {
            return switch (sex) {
                case MENS -> "Boy's";
                case WOMENS -> "Girl's";
                case NONBINARY -> "Non-Binary";
                case MIXED -> "Mixed";
                case OTHER -> "Other";
            };
        }
        return switch (sex) {
            case MENS -> "Men's";
            case WOMENS -> "Women's";
            case NONBINARY -> "Non-Binary";
            case MIXED -> "Mixed";
            case OTHER -> "Other";
        };
    }

    public String getDisplayString() {
        String sexDisplay = getSexString();
        String ageDisplay = age.getDescription();
        String compDisplay = StringUtils.capitalize(competitiveness.name().toLowerCase());

        if (competitiveness != DEFAULT_COMPETITIVENESS) {
            // If competitiveness is custom, display it.
            if (ageDisplay.length() > 0) {
                return sexDisplay + " " + ageDisplay + " " + compDisplay;
            }
            return sexDisplay + " " + compDisplay;
        }
        if (age != DEFAULT_AGE) {
            // If ageDisplay is not ADULT, display it.
            return sexDisplay + " " + ageDisplay;
        }
        return sexDisplay;
    }

    /**
     * Checks whether this grade object can participate in another grade.
     * For example:
     * U16s boys can participate in U19s boys.
     * But U14s girls CANNOT participate in U13s girls.
     * @param other The grade to check if we can participate in
     * @return True if participation allowed, false otherwise.
     */
    public boolean canParticipateIn(Grade other) {
        boolean sexOk = (sex == other.sex);
        boolean ageOk = age.canParticipateIn(other.age);
        return sexOk && ageOk;
    }

    public Sex getSex() {
        return sex;
    }

    public Age getAge() {
        return age;
    }

    public Competitiveness getCompetitiveness() {
        return competitiveness;
    }
}
