package com.abdullah4191.mename.model;

import java.text.ParseException;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.abdullah4191.mename.exception.ApplicationException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class Properties {
	private Environment env;
	private static final String USER_NAME_KEY = "emp.code";
	private static final String USER_PASSWORD_KEY = "emp.passwd";
	private static final String RESUME_DATE_KEY = "date.resume";
	private static final String START_NAME_KEY = "date.start";
	private static final String NOTES_KEY = "note";

	private static String userName;
	private static String password;
	private static Date resumeDate;
	private static Date startDate;
	private static String notes;

	@Autowired
	public void setEnvironment(Environment env) throws ApplicationException {
		Optional.of(env.getProperty(USER_NAME_KEY)).ifPresentOrElse(name -> setUserName(name),
				() -> new Exception("User name is requiered"));
		Optional.of(env.getProperty(USER_PASSWORD_KEY)).ifPresentOrElse(passwd -> setPassword(passwd),
				() -> new Exception("User password is requiered"));

		Optional.of(env.getProperty(RESUME_DATE_KEY)).ifPresentOrElse(
				dateResume -> setResumeDate(parseDate(dateResume)), () -> new Exception("resume date is requiered"));
		Optional.of(env.getProperty(START_NAME_KEY)).ifPresentOrElse(dateStart -> setStartDate(parseDate(dateStart)),
				() -> new Exception("Start date is requiered"));

		setNotes(env.getProperty(NOTES_KEY));
		if (!StringUtils.hasText(getUserName())) {
			throw new ApplicationException("User name is requiered");
		}
		if (!StringUtils.hasText(getPassword())) {
			throw new ApplicationException("User password is requiered");
		}

		Date today = new Date();
		try {
			today = Constants.getDateFormatter().parse(Constants.getDateFormatter().format(today));
		} catch (ParseException e) {
			// batata
		}

		if (getStartDate() == null) {
			// throw new ApplicationException("Start date is requiered");
			setStartDate(today);
		}
		if (getResumeDate() == null) {
			// throw new ApplicationException("resume date is requiered");
			final Calendar cal = Calendar.getInstance();
			cal.setTime(today);
			cal.add(Calendar.DAY_OF_MONTH, 1);
			setResumeDate(cal.getTime());
		}
	}

	public Environment getEnv() {
		return env;
	}

	public void setEnv(Environment env) {
		this.env = env;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		Properties.userName = userName;
	}

	public String getPassword64() {
		return password;
	}

	public String getPassword() {
		return new String(Base64.getEncoder().encode(password.getBytes()));
	}

	public void setPassword(String password) {
		Properties.password = password;
	}

	public Date getResumeDate() {
		return resumeDate;
	}

	public void setResumeDate(Date resumeDate) {
		Properties.resumeDate = resumeDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		Properties.startDate = startDate;
	}

	public String getNotes() {
		return !StringUtils.hasText(notes) ? "" : notes;
	}

	public void setNotes(String notes) {
		Properties.notes = notes;
	}

	private Date parseDate(String date) {
		if (StringUtils.hasText(date)) {
			try {
				return Constants.getDateFormatter().parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public String printProperties() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getResumeDate());
		cal.add(Calendar.DATE, -1);

		return "Properties: employee code-> " + getUserName() + ", passwd-> " + getPassword64()
				+ ", date range inclusive-> [" + getStartDate() + " - " + cal.getTime() + "], notes-> " + getNotes();
	}

}
