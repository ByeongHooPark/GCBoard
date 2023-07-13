package com.green.nowon.service;

import java.security.Principal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import com.green.nowon.domain.dto.CreateACDTO;
import com.green.nowon.domain.dto.CreateArgueDTO;

public interface ProjectService {

	String argueNewSubject(CreateArgueDTO dto , Principal principal);

	void mainArgueList(Model model);

	void goArgueBoard(long abno, Model model, Principal principal);

	void goArgueContent(long acno, Model model);

	void goInfo(Model model, Principal principal);

	void gptScore(long acno, Model model);

	ResponseEntity<Map<String, String>> getRandomData();

	void goWriteArgueContent(String abno, String parent, String type, Model model, Principal principal);

	String createArgueContent(CreateACDTO dto, Principal principal);

}
