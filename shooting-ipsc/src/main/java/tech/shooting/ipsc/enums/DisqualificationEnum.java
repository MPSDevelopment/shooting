package tech.shooting.ipsc.enums;

import tech.shooting.ipsc.bean.DisqualificationBean;

import java.util.ArrayList;
import java.util.List;

public enum DisqualificationEnum {
	DISQUALIFICATION("DISQUALIFICATION", "Disqualification"),
	ABSENT("ABSENT", "Absent"),
	INJURED("INJURED", "Injured"),
	BROKEN_RULE("BROKEN_RULE", "Broken the rules");

	private String type;

	private String name;

	DisqualificationEnum (String name, String type) {
		this.name = name;
		this.type = type;
	}

	public static int getCount () {
		return DisqualificationEnum.values().length;
	}

	public String getName () {
		return name;
	}

	public String getType () {
		return type;
	}

	@Override
	public String toString () {
		return String.valueOf(type);
	}

	public static List<DisqualificationBean> getList () {
		List<DisqualificationBean> result = new ArrayList<>();
		DisqualificationEnum[] values = DisqualificationEnum.values();
		for(int i = 0; i < values.length; i++) {
			if(i == 0) {
				result.add(new DisqualificationBean().setChecked(true).setName(values[i].getName()));
			} else {
				result.add(new DisqualificationBean().setChecked(false).setName(values[i].getName()));
			}
		}
		return result;
	}
}
