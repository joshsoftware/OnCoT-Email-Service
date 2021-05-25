package com.josh.emailFunctionality.helper;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.josh.emailFunctionality.dto.EmailArrayRequestDto;
import com.josh.emailFunctionality.dto.HrDataRequestDto;


@Service
public class EmailTemplateHelper {
	
	@Autowired
	EmailServiceHelper emailServiceHelper;
	
	public void setEmailTemplate(EmailArrayRequestDto emailArrayRequestDto) throws Exception {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		emailServiceHelper.model.put("organization", emailArrayRequestDto.getDrive_details().getOrganization());
		emailServiceHelper.model.put("drive_name", emailArrayRequestDto.getDrive_details().getDrive());
		emailServiceHelper.model.put("start_time", formatter.parse(emailArrayRequestDto.getDrive_details().getStart_time()));
		System.out.println("Date ;"+formatter.parse(emailArrayRequestDto.getDrive_details().getStart_time()));
		emailServiceHelper.model.put("end_time", formatter.parse(emailArrayRequestDto.getDrive_details().getEnd_time()));
		Map<String,String> hrList = new HashMap<String, String>();
		for(HrDataRequestDto hr : emailArrayRequestDto.getDrive_details().getHr_contacts()) {
			hrList.put(hr.getName(), hr.getMobile_number());
		}
		emailServiceHelper.model.put("hr_contacts", hrList);		
	}

}
