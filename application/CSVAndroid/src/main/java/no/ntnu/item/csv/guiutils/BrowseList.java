package no.ntnu.item.csv.guiutils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.ntnu.item.csv.CSVActivity;
import no.ntnu.item.csv.R;
import no.ntnu.item.csv.capability.Capability;

public class BrowseList {

	private static final String TEXT = "TEXT";
	private static final String ICON = "ICON";

	private List<Map<String, Object>> list;

	// Creating browse list for remote browsing
	public BrowseList(Map<String, Capability> currentFolder) {
		this.list = new ArrayList<Map<String, Object>>();
		Map<String, Capability> dirList = new HashMap<String, Capability>();
		Map<String, Capability> fileList = new HashMap<String, Capability>();
		List<Map<String, Object>> tmpList = new ArrayList<Map<String, Object>>(
				currentFolder.size());

		for (String alias : currentFolder.keySet()) {
			if (!alias.equals("..")) {
				if (currentFolder.get(alias).isFolder()) {
					dirList.put(alias, currentFolder.get(alias));

				} else {
					fileList.put(alias, currentFolder.get(alias));
				}
			}
		}

		Object[] dirKeys = dirList.keySet().toArray();

		Object[] fileKeys = fileList.keySet().toArray();
		Object[] keys = new Object[dirKeys.length + fileKeys.length];

		Arrays.sort(dirKeys);
		Arrays.sort(fileKeys);

		System.arraycopy(dirKeys, 0, keys, 0, dirKeys.length);
		System.arraycopy(fileKeys, 0, keys, dirKeys.length, fileKeys.length);

		for (int i = 0; i < keys.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put(TEXT, keys[i].toString());
			if (currentFolder.get(keys[i]).isFolder()) {
				map.put(ICON, R.drawable.folder);
				tmpList.add(map);
			} else {
				map.put(ICON, R.drawable.file);
				tmpList.add(map);
			}
		}

		if (!CSVActivity.fm.inRootDir()) {
			Map<String, Object> parMap = new HashMap<String, Object>();
			parMap.put(TEXT, "..");
			parMap.put(ICON, R.drawable.folder);
			this.list.add(parMap);
		}

		this.list.addAll(tmpList);
	}

	// Creating browse list for local browsing
	public BrowseList(String dir, List<String> files) {
		this.list = new ArrayList<Map<String, Object>>();

		for (String alias : files) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (!alias.equals("..")) {
				map.put(TEXT, alias);
				File tmpFile = new File(dir + alias);
				if (tmpFile.exists() && tmpFile.isDirectory()) {
					map.put(ICON, R.drawable.folder);
				} else if (tmpFile.exists()) {
					map.put(ICON, R.drawable.file);
				}
				this.list.add(map);
			}
		}

		Map<String, Object> parMap = new HashMap<String, Object>();
		parMap.put(TEXT, "..");
		parMap.put(ICON, null);
		this.list.add(0, parMap);
	}

	// Creating browse list from a normal list of items
	public BrowseList(List<String> files) {
		this.list = new ArrayList<Map<String, Object>>();

		for (String alias : files) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (!alias.equals("..")) {
				map.put(TEXT, alias);
				map.put(ICON, R.drawable.icon);
				this.list.add(map);
			}
		}
	}

	public List<Map<String, Object>> getList() {
		return this.list;
	}

}
