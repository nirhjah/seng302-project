package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

/**
 * Grade level for sports.
 * For example, "Under 13s mens",
 * Or "Senior womens".
 * -
 * Note that we don't have a repository / service for this class.
 * This is because this entity should ALWAYS be owned by a parent entity,
 * with cascadeType = ALL.
 * (For example, owned by a team.)
 */
@Entity(name = "GradeLevel")
public class GradeLevel {
    public GradeLevel() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gradeId;

    public enum Age {
        UNDER_19s("Under 19s"),
        UNDER_18s("Under 18s"),
        UNDER_17s("Under 17s"),
        UNDER_16s("Under 16s"),
        UNDER_15s("Under 15s"),
        UNDER_14s("Under 14s"),
        UNDER_13S("Under 13s"),
        UNDER_12S("Under 12s"),
        UNDER_11S("Under 11s"),
        UNDER_10S("Under 10s"),
        UNDER_9S("Under 9s"),
        UNDER_8S("Under 8s"),
        UNDER_7S("Under 7s"),
        UNDER_6S("Under 6s"),
        UNDER_5S("Under 5s"),
        JUNIOR("Junior"),
        ADULT("Adults"),
        SENIOR("Senior"),
        OVER_50s("Over 50s"),
        OVER_60s("Over 60s"),
        OVER_70s("Over 70s");

        private final String description;

        public String getDescription() {
            return description;
        }

        Age(String description) {
            this.description = description;
        }
    }

    public enum Sex {
        MENS,
        WOMENS,
        MIXED,
        OTHER
    }

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Enumerated(EnumType.STRING)
    private Age age;

    public GradeLevel(Age age, Sex sex) {
        this.age = age;
        this.sex = sex;
    }

    public String getDisplayName() {
        String sexDisplay = switch (sex) {
            case MENS -> "Mens";
            case WOMENS -> "Womens";
            case MIXED -> "Mixed";
            case OTHER -> "Other";
        };

        String ageDisplay = age.getDescription();

        return sexDisplay + " " + ageDisplay;
    }

    public Sex getSex() {
        return sex;
    }

    public Age getAge() {
        return age;
    }
}
