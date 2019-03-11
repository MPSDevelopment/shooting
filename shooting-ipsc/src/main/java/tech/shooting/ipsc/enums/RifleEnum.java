package tech.shooting.ipsc.enums;

/**
 * Класс карабин
 * <p>
 * Открытый класс, Стандартный класс, Открытый класс с ручным перезаряжанием, Стандартный класс с ручным перезаряжанием
 */

public enum RifleEnum {

    OPEN("Open"), STANDARD("Standard"), OPEN_MANUAL("Open manual"), STANDARD_MANUAL("Standard manual");

    private String value;

    RifleEnum (String value) {
        this.value = value;
    }

    @Override
    public String toString () {
        return String.valueOf(value);
    }

    public String getValue () {
        return value;
    }}
