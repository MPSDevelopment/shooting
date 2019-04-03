package tech.shooting.ipsc.enums;

import java.time.LocalTime;

public enum TypeOfInterval {
	MORNING("MORNING", Constants.START_OF, Constants.MIDDLE),
	EVENING("EVENING", Constants.MIDDLE, Constants.END_OF),
	DAY("DAY", Constants.START_OF, Constants.END_OF),
	WEEK("WEEK", Constants.START_OF, Constants.END_OF),
	MONTH("MONTH", Constants.START_OF, Constants.END_OF);

	private String state;

	private LocalTime start;

	private LocalTime end;

	TypeOfInterval (String state, LocalTime start, LocalTime end) {
		this.state = state;
		this.start = start;
		this.end = end;
	}

	public String getState () {
		return state;
	}

	public LocalTime getStart () {
		return start;
	}

	public LocalTime getEnd () {
		return end;
	}

	private static class Constants {
		private static final LocalTime START_OF = LocalTime.of(0, 0, 0, 0);

		private static final LocalTime MIDDLE = LocalTime.of(12, 0, 0, 0);

		private static final LocalTime END_OF = LocalTime.of(23, 59, 59, 0);
	}
}
