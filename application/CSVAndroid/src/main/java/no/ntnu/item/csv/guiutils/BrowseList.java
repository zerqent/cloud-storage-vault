package no.ntnu.item.csv.guiutils;

import java.util.ArrayList;
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

		List<Map<String, Object>> dirList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> fileList = new ArrayList<Map<String, Object>>();

		for (String alias : currentFolder.keySet()) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (!alias.equals("..")) {
				map.put(TEXT, alias);
				if (currentFolder.get(alias).isFolder()) {
					map.put(ICON, R.drawable.folder);
					dirList.add(map);
				} else {
					map.put(ICON, R.drawable.text);
					fileList.add(map);
				}
			}
		}

		Map<String, Object> parMap = new HashMap<String, Object>();
		parMap.put(TEXT, "..");
		parMap.put(ICON, null);
		this.list.add(parMap);
		this.list.addAll(dirList);
		this.list.addAll(fileList);
	}

	public List<Map<String, Object>> getList() {
		return this.list;
	}

}
