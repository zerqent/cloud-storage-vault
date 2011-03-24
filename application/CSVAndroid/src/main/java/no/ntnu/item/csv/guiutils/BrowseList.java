package no.ntnu.item.csv.guiutils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;

public class BrowseList {

	private static final String TEXT = "TEXT";
	private static final String ICON = "ICON";

	private List<Map<String, Object>> list;

	public BrowseList(Map<String, Capability> currentFolder) {
		this.list = new ArrayList<Map<String, Object>>();

		List<String> tmpList = new ArrayList<String>();
		tmpList.addAll(currentFolder.keySet());
		Collections.sort(tmpList);
		tmpList.add(0, "..");
		for (String alias : tmpList) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(TEXT, alias);
			if (!alias.equals("..")) {
				if (currentFolder.get(alias).isFolder()) {
					map.put(ICON, R.drawable.icon);
				} else {
					map.put(ICON, null);
				}
			}
			this.list.add(map);
		}
	}

	public List<Map<String, Object>> getList() {
		return this.list;
	}

}
