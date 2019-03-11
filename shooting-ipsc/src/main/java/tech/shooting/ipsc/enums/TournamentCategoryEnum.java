package tech.shooting.ipsc.enums;

public enum TournamentCategoryEnum {

    LADY("Lady"), JUNIOR("Junior"), SUPER_JUNIOR("Super junior"), SENIOR("Senior"), SUPER_SENIOR("Super senior");

    private String value;

    TournamentCategoryEnum (String value) {
        this.value = value;
    }

    @Override
    public String toString () {
        return String.valueOf(value);
    }

    public String getValue () {
        return value;
    }
}
