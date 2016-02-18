package com.xiaobukuaipao.vzhi.flingswipe;

// 方向--有序还是无序
public class Orientations {
	
	public enum Orientation {
		Ordered, Disordered;
		public static Orientation fromIndex(int index) {
			Orientation[] values = Orientation.values();
			if (index < 0 || index > values.length) {
				throw new IndexOutOfBoundsException();
			}
			
			return values[index];
		}
	}
	
}
