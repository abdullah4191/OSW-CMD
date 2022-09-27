package com.abdullah4191.mename;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import com.abdullah4191.mename.model.Constants;
import com.abdullah4191.mename.model.Properties;

/**
 * @author Abdullah Lubbadeh
 * 
 *         12 March 2020 Corona 2020
 * 
 *         This App can be used to send batch of OSW request based on date range
 *         and automatically skips weekend. The purpose behind this is the
 *         lengthy/ complicated/ slow process of the current system.
 *
 */
@SpringBootApplication
public class MenaoswCmdApplication {

	private Properties properties;

	public Properties getProperties() {
		return properties;
	}

	@Autowired
	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public static void main(String[] args) {
		new SpringApplicationBuilder(MenaoswCmdApplication.class).web(WebApplicationType.NONE).run(args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void doStuff() throws ParseException {
		System.out.println("This App can be used to send batch of OSW request based on date range\n"
				+ "and automatically skips weekend. The purpose behind this is to bypass the\n"
				+ "complicated process of the current system.\n" + "Author: Abdullah4191\n" + "12 March 2020 Covid-19"
				+ "Usage: java -jar "
				+ "date of tomorrow will substitute empty resume date and date of today will replace empty start date");
		System.out.println("==================================================================");
		System.out.println(properties.printProperties());
		System.out.println("==================================================================");

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("employee_code", properties.getUserName());
		map.add("password64", properties.getPassword64());
		map.add("password", properties.getPassword());
		map.add("company_code", "massar");
		map.add("lang1", "1");
		map.add("language_code", "1");
		map.add("submit_melogin_validation", "0");
		map.add("submit_login_validation", "0");
		map.add("su", "");
		map.add("ObjectGUID1", "");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		// do login
		ResponseEntity<String> response = restTemplate.exchange(Constants.LOGIN_URL, HttpMethod.POST, request,
				String.class);

		List<String> cookies = response.getHeaders().get(HttpHeaders.SET_COOKIE);
		headers.addAll(response.getHeaders());

		map = new LinkedMultiValueMap<>();
		map.add("trans_internal_type", "4");
		map.add("f_hour", "8");
		map.add("f_min", "0");
		map.add("f_AmPm", "AM");
		map.add("t_hour", "8");
		map.add("t_min", "0");
		map.add("t_AmPm", "PM");
		map.add("transaction_date", Constants.getDateFormatter().format(new Date()));
		map.add("Support_documents1", "");

		String strCookie = "";
		for (String cook : cookies) {
			strCookie += cook.split(";")[0] + ";";
		}
		headers.add("cookie", strCookie);

		final Calendar cal = Calendar.getInstance();
		Date leaveDate = (Date) properties.getStartDate().clone();
		while (leaveDate.before(properties.getResumeDate())) {
			cal.setTime(leaveDate);
			if (cal.get(Calendar.DAY_OF_WEEK) >= 6) {
				System.out.println("skipping day: " + Constants.getDateFormatter().format(cal.getTime()));
				cal.add(Calendar.DATE, 1);
				leaveDate = cal.getTime();
				continue;
			}
			map.remove("leave_date");
			map.add("leave_date", Constants.getDateFormatter().format(leaveDate));

			map.remove("notes");
			map.add("notes", getLeaveNote(properties.getNotes()));
			System.out.println(map.get("notes"));

			System.out.println("Sending request for Leave Day: " + Constants.getDateFormatter().format(leaveDate));
			request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
			response = restTemplate.postForEntity(Constants.OSW_URL, request, String.class);

			cal.add(Calendar.DATE, 1);
			leaveDate = cal.getTime();
		}
		System.out.println("==================================================================");
		System.out.println("Process completed!");
		// System.exit(0);
	}

	private static String getLeaveNote(String defaultNote) {
		return StringUtils.hasText(defaultNote) ? defaultNote
				: Constants.SAMPLE_NOTES.get(new Random().nextInt(Constants.SAMPLE_NOTES.size()));
	}

}
