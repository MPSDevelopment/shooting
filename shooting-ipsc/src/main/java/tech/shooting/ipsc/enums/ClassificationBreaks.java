package tech.shooting.ipsc.enums;

public enum ClassificationBreaks {

    D("Breaks in range 0% to 39.99999%"), C("Breaks in range 40% to 59.9999%"), B("Breaks in range 60% to 74.9999%"), A("Breaks in range 75% to 84.9999%"), Master("Breaks in range 85% to 94.9999%"), GrandMaster("Breaks in range 95% to " +
        "100%");

    private String value;

    ClassificationBreaks (String value) {
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
