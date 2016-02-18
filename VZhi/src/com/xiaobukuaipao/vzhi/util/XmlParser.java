package com.xiaobukuaipao.vzhi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface XmlParser {
	/**
	 *  解析输入的XML流
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public List<String> parse(InputStream inputStream) throws IOException;
}
