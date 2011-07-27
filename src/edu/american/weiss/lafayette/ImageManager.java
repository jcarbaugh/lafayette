package edu.american.weiss.lafayette;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageManager {

	private static ImageManager instance;
	private Map<String, Image> cache;
	private Toolkit tk;
	
	static {
		instance = new ImageManager();
	}
	
	private ImageManager() {
		cache = new HashMap<String, Image>();
		tk = Toolkit.getDefaultToolkit();
	}
	
	public static ImageManager getInstance() {
		return instance;
	}
	
	public Object get(String id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		}
		return null;
	}
	
	public List<String> getIds() {
		return new ArrayList<String>(cache.keySet());
	}
	
	public Image getImage(String id) {
		return (Image) get(id);
	}
	
	public Collection<Image> getImages() {
		return cache.values();
	}
	
	public void loadImage(String id, String path) {
		Image img = tk.getImage(path);
		if (img != null) {
			cache.put(id, img);
		}
	}
	
	public void loadDirectory(String dirPath) {
		loadDirectory(dirPath, false);
	}
	
	public void loadDirectory(String dirPath, boolean shuffle) {
		if (!dirPath.endsWith("/")) {
			dirPath += "/";
		}
		File dir = new File(dirPath);
		List<String> paths = Arrays.asList(dir.list(imageFilter));
		if (shuffle) {
			Collections.shuffle(paths);
		}
		for (String path : paths) {
			loadImage(path, dirPath + path);
		}
	}

	FilenameFilter imageFilter = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			name = name.toLowerCase();
			return !name.startsWith(".") && (
					name.endsWith(".bmp") ||
					name.endsWith(".gif") ||
					name.endsWith(".jpeg") ||
					name.endsWith(".jpg") ||
					name.endsWith(".png"));
		}
	};

};
