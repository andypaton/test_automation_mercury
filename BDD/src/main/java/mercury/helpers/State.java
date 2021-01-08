package mercury.helpers;

public enum State {

	ELEMENT_IS_CLICKABLE("elementIsClickable"),
	ELEMENT_IS_VISIBLE("elementIsVisible");

    private String value;

    State(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}