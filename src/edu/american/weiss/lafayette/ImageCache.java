package edu.american.weiss.lafayette;

import java.awt.Image;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.imageio.ImageIO;

public class ImageCache {
	
	private Queue<Image> images = new LinkedList<Image>();
	private Queue<String> paths = new LinkedList<String>();
	
	private int minSize;
	
	public ImageCache() {
		this.minSize = 4;
	}

	public void fillQueue() {
		while (images.size() < this.minSize && paths.size() > 0) {
			String path = paths.poll();
			if (path != null) {
				try {
					Image img = ImageIO.read(new File(path));
					images.add(img);
					System.out.println("Caching " + path);
				} catch (IOException ioe) {
					// TODO: handle exception
				}
			}
		}
	}
	
	public Image getImage() {
		Image img = images.poll();
		System.out.println(img);
		fillQueue();
		return img;
	}
	
	public void loadImage(String path) {
		System.out.println("Adding " + path);
		paths.add(path);
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
			loadImage(dirPath + path);
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
	
}
