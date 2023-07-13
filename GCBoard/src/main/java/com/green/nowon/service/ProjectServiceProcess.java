package com.green.nowon.service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.green.nowon.domain.ArgueBoardEntity;
import com.green.nowon.domain.ArgueBoardRepo;
import com.green.nowon.domain.ArgueContentEntity;
import com.green.nowon.domain.ArgueContentRepo;
import com.green.nowon.domain.MemberEntity;
import com.green.nowon.domain.MemberRepo;
import com.green.nowon.domain.NickEntity;
import com.green.nowon.domain.NickRepo;
import com.green.nowon.domain.dto.CreateArgueDTO;
import com.green.nowon.domain.dto.GoCreateACDTO;
import com.green.nowon.domain.dto.InfoDTO;
import com.green.nowon.domain.dto.ScoringDTO;
import com.green.nowon.domain.dto.SetWithGptDTO;
import com.green.nowon.domain.dto.ACBoardDTO;
import com.green.nowon.domain.dto.ACContentDTO;
import com.green.nowon.domain.dto.ACDetailDTO;
import com.green.nowon.domain.dto.ArgueListDTO;
import com.green.nowon.domain.dto.CreateACDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProjectServiceProcess implements ProjectService {
	
	private final ArgueBoardRepo argueBoardRepo;
	private final ArgueContentRepo argueContentRepo;
	private final NickRepo nickRepo;
	private final MemberRepo memberRepo;
	private final ProjectServiceUtil psUtil;
	
	
	/* 토론 주제 만들기(+토론 주장 만들기) */
	/* 토론 */
	@Override
	public String argueNewSubject(CreateArgueDTO dto, Principal principal) {
		//1. 토론 주제 저장
		//2. 닉네임 처리
		//3. gpt 메서드(주장 저장, member 업데이트)
		
		
		String userId = principal.getName();
		MemberEntity mE = memberRepo.findByUserId(userId).get();
		ArgueBoardEntity abE =ArgueBoardEntity.builder()
										.title(dto.getTitle())
										.detail(dto.getDetail())
										.build();
		
		//토론 주제 entity
		argueBoardRepo.save(abE);
		
		//토론 내용 entity >>> gpt util에서 완성해야함
		
		//닉네임 entity
		String nick;
		while(true) {//닉네임 중복여부 확인
			nick = psUtil.randomNickname();
			if(nickRepo.findByNick(nick).isEmpty())break;
		}
		
		nickRepo.save(NickEntity.builder()
						.nick(nick)
						.member(mE)
						.argueBoard(abE)
						.build());
		
		//gpt 메서드로
		SetWithGptDTO gptDTO = SetWithGptDTO.builder()
				.presentArgument(dto.getArguements())
				.presentReason(dto.getReason())
				.mE(mE)
				.abE(abE)
				.build();
		
		return psUtil.setWithGPT(gptDTO);
//		return "redirect:/argue/gpt-scoring"+acno;
		
	}
	
	/* 새로운 주장 등록하기 */
	@Override
	public String createArgueContent(CreateACDTO dto, Principal principal) {
		//1. 닉네임 처리(dto.getNick()의 유무)
		//2. gpt 평가 및 멤버 업데이트 setWithGPT()
		//dto.getParent()의 유무. 상위가 있냐 없냐
		
		System.out.println("dto.getAbno() : " + dto.getAbno());
		System.out.println("dto.getParent() : " + dto.getParent());
		
		String userId = principal.getName();
		MemberEntity mE = memberRepo.findByUserId(userId).get();
		ArgueBoardEntity abE = argueBoardRepo.findById(dto.getAbno()).get();
		ArgueContentEntity pAcE = null;
		
		if(dto.getParent()==null) {
			pAcE=null;
		} else {
			pAcE = argueContentRepo.findById(dto.getParent()).get();
		}
		
		
		String nick;
		if(dto.getNick()==null) {
			while(true) {//닉네임 중복여부 확인
				nick = psUtil.randomNickname();
				if(nickRepo.findByNick(nick).isEmpty())break;
			}
			
			nickRepo.save(NickEntity.builder()
					.nick(nick)
					.member(mE)
					.argueBoard(abE)
					.build());
			
		} else {
			nick = dto.getNick();
		}
		

		System.out.println("dto.getPastArgument() : "+dto.getPastArgument());
		System.out.println("dto.getPastReason() : "+dto.getPastReason());
		System.out.println("dto.getPresentArgument() : "+dto.getPresentArgument());
		System.out.println("dto.getPresentReason() : "+dto.getPresentReason());
		
		
		SetWithGptDTO gptDTO = SetWithGptDTO.builder()
				.pastArgument(dto.getPastArgument())
				.pastReason(dto.getPastReason())
				.presentArgument(dto.getPresentArgument())
				.presentReason(dto.getPresentReason())
				.mE(mE)
				.abE(abE)
				.type(dto.getType())
				.pAcE(pAcE)
				.build();
		
		return psUtil.setWithGPT(gptDTO);
	}
	
	
	/* 토론 메인페이지의 리스트 가져오기 */
	@Override
	public void mainArgueList(Model model) {
		
		List<ArgueListDTO> listDTO = new ArrayList<>();
		
		List<ArgueBoardEntity> abEList = argueBoardRepo.findAll();
		
		for(ArgueBoardEntity abE : abEList) {
			listDTO.add(ArgueListDTO.builder()
							.abno(abE.getAbno())
							.title(abE.getTitle())
							.createdDate(abE.getCreatedDate().toString().substring(0, 10))
							.build());
		}
		model.addAttribute("list", listDTO);
	}
	
	/* 토론 주제로 가기 + 토론 내용들 뿌리기 */
	@Override
	public void goArgueBoard(long abno, Model model, Principal principal) {
		//모델 2개에 담기.
		//1. board에 대한 정보. : title, abno
		//2. content에 대한 정보. >> list로 뿌릴것들 : 
		//acno, parent(반박 대상)+type, arguments, 이 글에 달린 comment 수, gptScore.
		//0. 유틸에 하위 카테고리들을 찾는 로직을 구현하자(entity list 리턴)
		
		//1.
		ArgueBoardEntity abE = argueBoardRepo.findById(abno).get();
		MemberEntity mE = memberRepo.findByUserId(principal.getName()).get();
		NickEntity nE = nickRepo.findByMemberAndArgueBoard(mE, abE).orElse(null);
		
		String nickTemp = nE==null? null: nE.getNick();
		
		model.addAttribute("bdto", ACBoardDTO.builder()
								.abno(abE.getAbno())
								.title(abE.getTitle())
								.detail(abE.getDetail())
								.nick(nickTemp)
								.build());
		
		//2.
		List<ArgueContentEntity> acEList = argueContentRepo.findByArgueBoard(abE);
		List<ACContentDTO> cDtoList = new ArrayList<>();
		
		String pno = null;
		String type = null;
		
		for(ArgueContentEntity acE : acEList) {
			//해당 주장에 달린 찬, 반, 중 구하기
			long agree = 0;
			long opposite = 0;
			long neutrality = 0;
			
			List<ArgueContentEntity> cChildList = argueContentRepo.findByParent(acE).get();
			
			for(ArgueContentEntity cChild : cChildList) {
				type = cChild.getType();
				if(type.equals("찬성")) {
					agree++;
				} else if(type.equals("반대")) {
					opposite++;
				} else {
					neutrality++;
				}
			}
			
			if(acE.getParent()!=null) {
				pno=Long.toString(acE.getParent().getAcno());
			}
			
			cDtoList.add(ACContentDTO.builder()
								.acno(acE.getAcno())
								.arguments(acE.getArguments())
								.nick(acE.getNickName().getNick())
								.pno(pno)
								.type(acE.getType())
								.gptScore((int)acE.getGptScore())
								.agree(agree)
								.opposite(opposite)
								.neutrality(neutrality)
								.build());
		}
		model.addAttribute("clist", cDtoList);
	}

	/* 주장 상세 페이지 */
	@Override
	public void goArgueContent(long acno, Model model) {
		//가져올 목록
		//abno : '목록으로' 버튼에 넣을 데이터
		//acno >> 상위 acno(상위 주장으로), 해당 acno(comment 쓰기/ 비동기 comment 보기) 
		//주장, 논거, 생성일, 닉네임
		//comment(pk값으로)+type
		//gpt점수, gpt평가
		
		
		ArgueContentEntity acE = argueContentRepo.findById(acno).get();
		
		Long pno = null;
		ArgueContentEntity parent = acE.getParent();
		
		if(parent==null) {
			pno = null;
		} else {
			pno = parent.getAcno();
		}
		
		
		model.addAttribute("dto", ACDetailDTO.builder()
									.abno(acE.getArgueBoard().getAbno())
									.acno(acno)
									.arguments(acE.getArguments())
									.reason(acE.getReason())
									.createdDate(acE.getCreatedDate().toString().substring(0, 10))
									.nick(acE.getNickName().getNick())
									.pno(pno)
									.type(acE.getType())
									.gptScore((int)acE.getGptScore())
									.gptEval(acE.getGptEval())
									.build());
	}
	
	
	/* 개인정보로 가기 */
	@Override
	public void goInfo(Model model, Principal principal) {
		
		MemberEntity mE = memberRepo.findByUserId(principal.getName()).get();
		
		model.addAttribute("dto", InfoDTO.builder()
				.userId(mE.getUserId())
				.score(mE.getScore())
				.count(mE.getCount())
				.build());
	}

	
	/* gpt 채점 페이지(토론주장 등록한 뒤에만 가게되는 페이지) */
	@Override
	public void gptScore(long acno, Model model) {
		
		ArgueContentEntity acE = argueContentRepo.findById(acno).get();
		
		model.addAttribute("dto", ScoringDTO.builder()
										.gptScore((int)acE.getGptScore())
										.gptEval(acE.getGptEval())
										.acno(acno)
										.build());

	}
	
	
	/* 랜덤 토론 주제 만들기 : 토론 주제 등록 페이지 */
	@Override
	public ResponseEntity<Map<String, String>> getRandomData() {
		
		String prompt = "임의의 토론 주제를 선정하고 json형식으로 출력해주는데 토론주제의 name은 'title', 상세내용의 name은 'detail', 각각의 value는 한국어로 출력해줘. 다른 내용은 출력하지말고 json내용만 출력해줘.";
		String name1 = "title";
		String name2 = "detail";
		
		Map<String, String> result = new HashMap<>();
		result = psUtil.gptProgress(prompt, name1, name2);
		
		if(result.get(name1).equals("오류")) {
			result.put(name1,"오류가 발생했습니다. 다시 시도해주세요");
			result.put(name2,"");
		}
		
		return ResponseEntity.ok().body(result);
	}
	
	
	
	/* 새로운 주장 생성 페이지로 이동하기*/
	@Override
	public void goWriteArgueContent(String abno, String parent, String type, Model model, Principal principal) {
		//가져갈것
		//닉네임 : 닉네임 써주기 / 닉네임은 글 작성 후에 생성됩니다
		//comment : ~~에 대한 반박 / 상위 주장이 없습니다
		
		type = type.equals("없음") ? null : type;
		ArgueContentEntity parentAcE = null;
		
		if(parent.equals("없음")) {
			parent = null;
			parentAcE = null;
		} else {
			parentAcE = argueContentRepo.findById(Long.parseLong(parent)).orElse(null);
		}
		
		
		ArgueBoardEntity abE = argueBoardRepo.findById(Long.parseLong(abno)).get();
		
		NickEntity nE = nickRepo.findByMemberAndArgueBoard(memberRepo.findByUserId(principal.getName()).get(), abE).orElse(null);
		
		String parentArguments = parentAcE == null ? null : parentAcE.getArguments();
		String parentReasons = parentAcE == null ? null : parentAcE.getReason();
		String nick = nE==null?null:nE.getNick();
		
		GoCreateACDTO dto = GoCreateACDTO.builder()
								.abno(Long.parseLong(abno))
								.nick(nick)
								.parent(parent)
								.type(type)
								.parentArguments(parentArguments)
								.parentReasons(parentReasons)
								.build();
		
		model.addAttribute("dto", dto);
	}



}

