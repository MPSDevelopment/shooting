package tech.shooting.ipsc.enums;

public enum ClassifierIPSC {
	CLC01(
		"CLC-01",
		TypeOfCourse.ShortCourse,
		2,
		3,
		3,
		7,
		35,
		"Standing relaxed in area ‘A’, facing downrange with both hands relaxed by sides.",
		"Handgun loaded and holstered.",
		"After the audible start signal engage targets, strong hand only, from within the designated area ‘A’.",
		"Set IPSC Targets/No Shoots to 1.52m (5’) to the top of the targets.Area ‘A’ is 1m (3’3”) by 1m (3’3”)."),
	CLC03(
		"CLC-03",
		TypeOfCourse.ShortCourse,
		2,
		2,
		0,
		6,
		30,
		"Start seated in area ‘A’, facing downrange with both hands on top of a table palms flat.",
		"Loaded handgun pointing downrange will be located on the ‘X’ in between hands. The handgun must lie flat on one side and may not be propped up in any artificial manner. ",
		"After the audible start signal, using the weak hand only, pick up the handgun and engage targets, weak hand only, from within the designated area ‘A’.",
		"Set IPSC Targets to 1.52m (5’) to the top of the targets. Area ‘A’ is 1m (3’3”) by 1m (3’3”). Chair must be inside the confines of area ‘A’. Table immediately in front of area ‘A’. Center of ‘X’ should be 0.25m (10 +) from the edge facing area ‘A’."),
	CLC_05(
		"CLC-05",
		TypeOfCourse.ShortCourse,
		3,
		2,
		2,
		8,
		40,
		"Standing relaxed in area ‘A’, facing downrange with both hands relaxed by sides.",
		"Handgun loaded and holstered.",
		"After the audible start signal engage targets from within the designated area ‘A’",
		"Set IPSC Targets/No Shoots to 1.52m (5’) to the top of the targets. Area ‘A’ is 1m (3’3”) by 1m (3’3”)."),
	CLC_11(
		"CLC-11",
		TypeOfCourse.ShortCourse,
		4,
		4,
		0,
		12,
		60,
		"Standing relaxed in center of area ‘A’, facing center of barricade.",
		"Handgun loaded and holstered. ",
		"After the audible start signal engage targets P1, P2, T1 and T2 only rom left side of the barricade, make a mandatory reload and engage targets P3, P4, T3 and T4 from the right side of the barricade.Targets may only be shot from the designated side of the barricade.",
		"Set IPSC Targets to 1.52m (5’) to the top of the targets. Area ‘A’ is 1.22m (4’) wide by 0.92m (3’) deep. Barricade is 2.44m (8’) high and 1.22m (4’) wide. "),
	CLC_15(
		"CLC-15",
		TypeOfCourse.ShortCourse,
		4,
		1,
		0,
		9,
		45,
		"Standing relaxed in area ‘A’, with both wrists above respective shoulders.",
		"Handgun loaded and holstered.",
		"After the audible start signal engage targets from within the designated area ‘A’. ",
		"Set IPSC Targets to 1.52m (5’) to the top of the targets. Area ‘A’ is 0.92m (3’) by 0.92m (3’). Angle T1, T2 and T4 to face shooting area."),
	CLC_21(
		"CLC-21",
		TypeOfCourse.ShortCourse,
		4,
		2,
		1,
		12,
		60),
	CLC_31(
		"CLC-31",
		TypeOfCourse.ShortCourse,
		2,
		2,
		1,
		6,
		30),
	CLC_33(
		"CLC-33",
		TypeOfCourse.ShortCourse,
		3,
		0,
		0,
		6,
		30),
	CLC_35(
		"CLC-35",
		TypeOfCourse.ShortCourse,
		2,
		2,
		2,
		6,
		30),
	CLC_37(
		"CLC-37",
		TypeOfCourse.ShortCourse,
		5,
		2,
		1,
		12,
		60),
	CLC_41(
		"CLC-41",
		TypeOfCourse.ShortCourse,
		6,
		0,
		3,
		12,
		60),
	CLC_45(
		"CLC-45",
		TypeOfCourse.ShortCourse,
		2,
		4,
		0,
		8,
		40),
	CLC_47(
		"CLC-47",
		TypeOfCourse.ShortCourse,
		2,
		2,
		0,
		6,
		30),
	CLC_51(
		"CLC-51",
		TypeOfCourse.ShortCourse,
		3,
		0,
		0,
		6,
		30),
	CLC_55(
		"CLC-55",
		TypeOfCourse.ShortCourse,
		5,
		2,
		3,
		12,
		60),
	CLC_57(
		"CLC-57",
		TypeOfCourse.ShortCourse,
		5,
		2,
		0,
		12,
		60),
	CLC_59(
		"CLC-59",
		TypeOfCourse.ShortCourse,
		6,
		0,
		3,
		12,
		60),
	CLC_65(
		"CLC-65",
		TypeOfCourse.ShortCourse,
		1,
		4,
		0,
		6,
		30),
	CLC_67(
		"CLC-67",
		TypeOfCourse.ShortCourse,
		6,
		0,
		1,
		12,
		60),
	CLC_71(
		"CLC-71",
		TypeOfCourse.ShortCourse,
		3,
		2,
		0,
		8,
		40),
	CLC_73(
		"CLC-73",
		TypeOfCourse.ShortCourse,
		2,
		2,
		3,
		6,
		30);

	private String name;
	private TypeOfCourse typeOfCourse;
	private Integer targets;
	private Integer popper;
	private Integer noShoots;
	private Integer NumberOfRoundToBeScored;
	private Integer maximumPoints;
	private String startPosition;
	private String handgunReadyCondition;
	private String procedure;
	private String setupNotice;

	ClassifierIPSC (String name, TypeOfCourse typeOfCourse, Integer targets, Integer popper, Integer noShoots, Integer numberOfRoundToBeScored, Integer maximumPoints) {
		this.name = name;
		this.typeOfCourse = typeOfCourse;
		this.targets = targets;
		this.popper = popper;
		this.noShoots = noShoots;
		NumberOfRoundToBeScored = numberOfRoundToBeScored;
		this.maximumPoints = maximumPoints;
	}

	ClassifierIPSC (String name, TypeOfCourse typeOfCourse, Integer targets, Integer popper, Integer noShoots, Integer numberOfRoundToBeScored, Integer maximumPoints, String startPosition, String handgunReadyCondition, String procedure,
		String setupNotice) {
		this.name = name;
		this.typeOfCourse = typeOfCourse;
		this.targets = targets;
		this.popper = popper;
		this.noShoots = noShoots;
		this.NumberOfRoundToBeScored = numberOfRoundToBeScored;
		this.maximumPoints = maximumPoints;
		this.startPosition = startPosition;
		this.handgunReadyCondition = handgunReadyCondition;
		this.procedure = procedure;
		this.setupNotice = setupNotice;
	}

	public TypeOfCourse getTypeOfCourse () {
		return typeOfCourse;
	}

	public Integer getTargets () {
		return targets;
	}

	public Integer getPopper () {
		return popper;
	}

	public Integer getNoShoots () {
		return noShoots;
	}

	public Integer getNumberOfRoundToBeScored () {
		return NumberOfRoundToBeScored;
	}

	public Integer getMaximumPoints () {
		return maximumPoints;
	}

	public String getStartPosition () {
		return startPosition;
	}

	public String getHandgunReadyCondition () {
		return handgunReadyCondition;
	}

	public String getProcedure () {
		return procedure;
	}

	public String getSetupNotice () {
		return setupNotice;
	}

	public String getName () {
		return name;
	}
}
