package com.abdullah4191.mename.model;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

public interface Constants {
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(DATE_FORMAT);
	String LOGIN_URL = "http://hr.massar.com/MenaITech/application/hrms/mename/verify_user.php";
	String OSW_URL = "http://hr.massar.com/MenaITech/application/hrms/mename/ESS/online_request/leave_request1.php";

	List<String> SAMPLE_NOTES = Arrays.asList("worked late on release", "Too many meetings", "Doctor appointment");

	static SimpleDateFormat getDateFormatter() {
		return DATE_FORMATTER;
	}
}
