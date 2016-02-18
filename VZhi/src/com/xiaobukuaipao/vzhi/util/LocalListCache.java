package com.xiaobukuaipao.vzhi.util;

import java.util.ArrayList;
import java.util.List;

import com.xiaobukuaipao.vzhi.domain.user.Education;
import com.xiaobukuaipao.vzhi.domain.user.Experience;
@Deprecated
public class LocalListCache {

	private static final int CAPACITY = 2;
	private static final List<Education> educationList = new ArrayList<Education>(
			CAPACITY);
	private static final List<Experience> jobList = new ArrayList<Experience>(
			CAPACITY);

	public static void addElementBy(Education education) {
		educationList.add(education);
	}

	public static List<Education> getList() {
		return educationList;
	}

	public static void addElementBy(Experience jobExperience) {
		jobList.add(jobExperience);
	}

	public static void removeJobListByLastElement() {
		if (jobList.size() != 0)
			jobList.remove(jobList.size() - 1);
	}

	public static void removeEducationListByLastElement() {
		if (educationList.size() != 0)
			educationList.remove(educationList.size() - 1);
	}

	public static void removeAllJob() {
		jobList.clear();
	}

	public static List<Experience> getJobList() {
		return jobList;
	}
}
