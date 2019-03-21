package tech.shooting.ipsc.enums;

import tech.shooting.ipsc.pojo.Stage;

import java.util.ArrayList;
import java.util.List;

public enum ClassifierIPSC {
	CLC_01("CLC-01", TypeOfCourse.ShortCourse, 2, 3, 3, 7, 35),
	CLC_03("CLC-03", TypeOfCourse.ShortCourse, 2, 2, 0, 6, 30),
	CLC_05("CLC-05", TypeOfCourse.ShortCourse, 3, 2, 2, 8, 40),
	CLC_11("CLC-11", TypeOfCourse.ShortCourse, 4, 4, 0, 12, 60),
	CLC_15("CLC-15", TypeOfCourse.ShortCourse, 4, 1, 0, 9, 45),
	CLC_21("CLC-21", TypeOfCourse.ShortCourse, 4, 2, 1, 12, 60),
	CLC_31("CLC-31", TypeOfCourse.ShortCourse, 2, 2, 1, 6, 30),
	CLC_33("CLC-33", TypeOfCourse.ShortCourse, 3, 0, 0, 6, 30),
	CLC_35("CLC-35", TypeOfCourse.ShortCourse, 2, 2, 2, 6, 30),
	CLC_37("CLC-37", TypeOfCourse.ShortCourse, 5, 2, 1, 12, 60),
	CLC_41("CLC-41", TypeOfCourse.ShortCourse, 6, 0, 3, 12, 60),
	CLC_45("CLC-45", TypeOfCourse.ShortCourse, 2, 4, 0, 8, 40),
	CLC_47("CLC-47", TypeOfCourse.ShortCourse, 2, 2, 0, 6, 30),
	CLC_51("CLC-51", TypeOfCourse.ShortCourse, 3, 0, 0, 6, 30),
	CLC_55("CLC-55", TypeOfCourse.ShortCourse, 5, 2, 3, 12, 60),
	CLC_57("CLC-57", TypeOfCourse.ShortCourse, 5, 2, 0, 12, 60),
	CLC_59("CLC-59", TypeOfCourse.ShortCourse, 6, 0, 3, 12, 60),
	CLC_65("CLC-65", TypeOfCourse.ShortCourse, 1, 4, 0, 6, 30),
	CLC_67("CLC-67", TypeOfCourse.ShortCourse, 6, 0, 1, 12, 60),
	CLC_71("CLC-71", TypeOfCourse.ShortCourse, 3, 2, 0, 8, 40),
	CLC_73("CLC-73", TypeOfCourse.ShortCourse, 2, 2, 3, 6, 30),
	CLC_07("CLC-07", TypeOfCourse.MediumCourse, 5, 4, 3, 14, 70),
	CLC_19("CLC-19", TypeOfCourse.MediumCourse, 3, 0, 0, 18, 90),
	CLC_25("CLC-25", TypeOfCourse.MediumCourse, 3, 3, 0, 21, 105),
	CLC_49("CLC-49", TypeOfCourse.MediumCourse, 5, 4, 0, 14, 70),
	CLC_53("CLC-53", TypeOfCourse.MediumCourse, 4, 6, 0, 14, 70),
	CLC_63("CLC-63", TypeOfCourse.MediumCourse, 7, 2, 3, 16, 80),
	CLC_69("CLC-69", TypeOfCourse.MediumCourse, 3, 0, 0, 24, 120);

	private String name;

	private TypeOfCourse typeOfCourse;

	private Integer targets;

	private Integer popper;

	private Integer noShoots;

	private Integer numberOfRoundToBeScored;

	private Integer maximumPoints;

	ClassifierIPSC (String name, TypeOfCourse typeOfCourse, Integer targets, Integer popper, Integer noShoots, Integer numberOfRoundToBeScored, Integer maximumPoints) {
		this.name = name;
		this.typeOfCourse = typeOfCourse;
		this.targets = targets;
		this.popper = popper;
		this.noShoots = noShoots;
		this.numberOfRoundToBeScored = numberOfRoundToBeScored;
		this.maximumPoints = maximumPoints;
	}

	public static List<Stage> getListStage () {
		List<Stage> result = new ArrayList<>();
		for(ClassifierIPSC value : ClassifierIPSC.values()) {
			result.add(new Stage().setName(value.name)
			                      .setMaximumPoints(value.maximumPoints)
			                      .setNumberOfRoundToBeScored(value.numberOfRoundToBeScored)
			                      .setPopper(value.popper)
			                      .setTargets(value.targets)
			                      .setNoShoots(value.noShoots));
		}
		return result;
	}

	public static Integer getcount () {
		return ClassifierIPSC.values().length;
	}
}
