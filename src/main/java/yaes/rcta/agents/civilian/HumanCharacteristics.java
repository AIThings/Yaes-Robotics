package yaes.rcta.agents.civilian;

public interface HumanCharacteristics {

    public enum AgeGroup {
        CHILD, YOUNGSTER, SENIOR
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum HairType {
        BALD, LONG, SHORT;
    }

    public enum ChinType {
        SQUARE, ROUND, POINTED, DOUBLE, OVAL, HEART
    }

    public enum CivilianClassType {
        MALE_CHILD,
        FEMALE_CHILD,
        MALE_YOUNGSTER,
        FEMALE_YOUNGSTER,
        MALE_SENIOR,
        FEMALE_SENIOR

    }
}
