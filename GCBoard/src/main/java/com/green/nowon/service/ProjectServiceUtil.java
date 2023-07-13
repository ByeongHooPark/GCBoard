package com.green.nowon.service;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.nowon.domain.ArgueBoardEntity;
import com.green.nowon.domain.ArgueContentEntity;
import com.green.nowon.domain.ArgueContentRepo;
import com.green.nowon.domain.MemberEntity;
import com.green.nowon.domain.MemberRepo;
import com.green.nowon.domain.MemberRole;
import com.green.nowon.domain.NickEntity;
import com.green.nowon.domain.NickRepo;
import com.green.nowon.domain.dto.SetWithGptDTO;
import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Component
public class ProjectServiceUtil {

	private final StringRedisTemplate template;
	private final MemberRepo memberRepo;
	private final ArgueContentRepo argueContentRepo;
	private final NickRepo nickRepo;
	
	@Value("${gpt.key}")
	private String gptKey;
	
	
////1. redis ////
	
	//저장, 수정(같은 키로 재실행시)
	public void setDataExpire(String key, String value,long seconds){
		ValueOperations<String, String> ops = template.opsForValue();
		Duration timeout = Duration.ofSeconds(seconds);
		ops.set(key, value, timeout);
		
		template.opsForValue();
	}
	
	//존재여부 조회
	public boolean existData(String key) {
		return template.hasKey(key);
	}
	
	//value 확인
	public String valueData(String key) {
		return template.opsForValue().get(key);
	}
	
	//삭제
	public void deleteData(String key) {
		template.delete(key);
	}
	
	
////2. 랜덤 닉네임만들기 ////
	
	public String randomNickname(){
	    List<String> adjList = Arrays.asList("밝은", "슬픈", "웃긴", "진지한", "똑똑한", "친절한", "용감한", "정직한", "부드러운", "어리버리한",
	            "야생의", "수줍은", "시끄러운", "조용한", "호기심 많은", "차분한", "사나운", "소심한", "지혜로운", "미친",
	            "작은", "거대한", "화려한", "평범한", "달콤한", "시큼한", "뜨거운", "차가운", "부드러운", "거친");
	    List<String> nameList = Arrays.asList("모험가", "마법사", "전사", "도적", "궁수", "성기사", "주술사", "암살자", "흑마법사", "기사",
	            "마법기사", "닌자", "사냥꾼", "요술사", "도둑", "무사", "영웅", "기사단장", "소환사", "수호자",
	            "광전사", "대마법사", "암흑기사", "정령사", "도발자", "정찰병", "보물사냥꾼", "천마", "마족사냥꾼", "대장군");
	    String number = Integer.toString((int)(Math.random() * 9999)+1);
	    Collections.shuffle(adjList);
	    Collections.shuffle(nameList);
	    String adj = adjList.get(0);
	    String name = nameList.get(0);
	    
	    return adj+name+number;
	}

	
////3. gpt 채점, 점수반영 ////
	//해야할일 : gpt로 채점, 점수반영
	//ArgueContentEntity(create) , MemberEntity(update), Model에 넣기
	public String setWithGPT(SetWithGptDTO gptDTO) {
		OpenAiService service = new OpenAiService(gptKey);
		StringBuilder sb = new StringBuilder();
		
		MemberEntity mE = gptDTO.getME();
		ArgueBoardEntity abE = gptDTO.getAbE();
		NickEntity nE = nickRepo.findByMemberAndArgueBoard(mE,abE).get();
		String presentA = gptDTO.getPresentArgument();
		String presentR = gptDTO.getPresentReason();
		
		String nick = nE.getNick();
		
		String promptTemplate = "의 주장과 논거에 대해 논리적인 관점에서 평가하여 100점 만점을 기준으로 점수를 부여한 뒤에 점수와 평가를 각각 json형식으로 출력해줘. 출력할때 점수의 name은 'score', 논리적 오류의 name은 'evaluation'으로 해줘.";
		
	// 1. 질문 만들기
		//1) 다른 사람의 주장에 이어서 말하는 중이라면
		if(gptDTO.getPastArgument()!=null) {
			String pastA = gptDTO.getPastArgument();
			String pastR = gptDTO.getPastReason();
			
			sb.append("다음은 이전 사람의 주장과 논거에 대해 ")
			  .append(nick)
			  .append("가 다시 주장과 논거를 펼치는 토론이야. ")
			  .append(nick)
			  .append(promptTemplate)
			  .append(" 이전 사람의 주장 : ")
			  .append(pastA)
			  .append(" 이전 사람의 논거 : ")
			  .append(pastR)
			  ;
		
		//2) 처음으로 주제를 선정하고 말하는 중이라면
		} else  {
			sb.append("다음은 '")
			  .append(abE.getDetail())
			  .append("'라는 내용의 토론 내용에 대해 ")
			  .append(nick)
			  .append("가 주장과 논거를 펼치는 내용이야.")
			  .append(nick)
			  .append(promptTemplate)
			  ;
		}
			sb.append(nick)
			  .append("의 주장 : ")
			  .append(presentA)
			  .append(nick)
			  .append("의 논거 : ")
			  .append(presentR)
			  .append(". 다시한번 말하지만 다른 말 붙이지말고 json형식으로 추출만해줘!")
			  ;
		
	    long valueScore;
	    String valueEvaluation;
	    String gptPrompt = sb.toString();
		String name1 = "score";
		String name2 = "evaluation";
	    
	 //2. 만든 질문에 대해 gpt로 답 얻어오기
	 // 3. 얻은 답변을 json형식의 문자열에서 score, evaluation 추출
	    Map<String, String> gptMap = gptProgress(gptPrompt,name1,name2);
	    
	    if(gptMap.get(name1).equals("오류")) {
	       	 valueScore = 70;
	         valueEvaluation = "죄송합니다. 오류로 인해 임의의 점수를 부여하겠습니다.";
	    } else {
	    	valueScore = Long.parseLong(gptMap.get(name1));
	    	valueEvaluation = gptMap.get(name2);
	    }
	    
		//ArgueContentEntity(create)
	    ArgueContentEntity acE = ArgueContentEntity.builder()
											.arguments(presentA)
											.reason(presentR)
											.member(mE)
											.argueBoard(abE)
											.nickName(nE)
											.gptScore(valueScore)
											.gptEval(valueEvaluation)
											.type(gptDTO.getType())
											.parent(gptDTO.getPAcE())
											.build();
							   
		argueContentRepo.save(acE);
		
		//MemberEntity(update)
		updateMember(mE,valueScore);
		
		return "redirect:/argue/gpt-scoring/"+acE.getAcno();
	}

////3.1 gpt한테 답변 얻기////
	public Map<String, String> gptProgress(String gptPrompt, String name1, String name2){
		
		OpenAiService service = new OpenAiService(gptKey);
		String value1 = null;
		String value2 = null;
		
		//******
		String jsonString = null;
		
	    //***예외처리 하는 이유***//
	    //a. 답변할 때 gpt가 시간을 너무 많이 써서 오류를 낸다
	    //b. 답변을 해도 json형식으로 답변을 안한다
	    //c. json형식으로 답변을 해도 원하는 name값이 아니다
		try {
			//2. 만든 질문에 대해 gpt로 답 얻어오기
					CompletionRequest completionRequest = CompletionRequest.builder()
					        .prompt(gptPrompt)
					        .model("text-davinci-003")//답변 모델
					        .maxTokens(1000)//답변 길이
					        .build();
					
					List<CompletionChoice> choices = service.createCompletion(completionRequest).getChoices();
					StringBuilder sb = new StringBuilder();
					
					for (CompletionChoice choice : choices) {
						sb.append(choice.getText());
					}
					
					System.out.println("변환전 : "+sb.toString());
					
					jsonString = sb.toString();
			// 3. 얻은 답변을 json형식의 문자열에서 추출
			        ObjectMapper objectMapper = new ObjectMapper();
			        	
		            JsonNode jsonNode = objectMapper.readTree(jsonString);
		            // name에 해당하는 value 값 추출
		            value1 = jsonNode.get(name1).asText();
		            value2 = jsonNode.get(name2).asText();
		           
		       }catch (Exception e) {
		    	   value1 = "오류";
		    	   value2 = "오류";
			}
		
		//******
		System.out.println("답변 : " + jsonString);
		
		
		 Map<String, String> gptMap = new HashMap<>();
		 gptMap.put(name1, value1);
		 gptMap.put(name2, value2);
		
		return gptMap;
	}
	
	
	
////3.2 MemberEntity업데이트 ////
	public void updateMember(MemberEntity mE, long curScore) {
		
		Set<MemberRole> roles = mE.getRole();
		
		
		System.out.println("curScore : " + curScore);
		
		long curCount = mE.getCount();
		long totCount = curCount+1;
		double avgScore = (mE.getScore()*curCount+curScore)*(1.0)/totCount;
		
		//많아야 티어가 1개 움직이므로
		if(totCount>=40 && avgScore>=80) {
			mE.addRole(MemberRole.DIAMOND);
			System.out.println("유지!");
		}else if(totCount>=30 && avgScore>=60) {
			mE.addRole(MemberRole.PLATINUM)
			  .removeRole(MemberRole.DIAMOND);
			System.out.println("강등!");
		}else if(totCount>=20 && avgScore>=40) {
			mE.addRole(MemberRole.GOLD)
			  .removeRole(MemberRole.PLATINUM);
		}else if(totCount>=10 && avgScore>=20) {
			mE.addRole(MemberRole.SILVER)
			  .removeRole(MemberRole.GOLD);
		}else {
			mE.removeRole(MemberRole.SILVER);
		}
		
		System.out.println("도착!!");
		System.out.println("avgScore : " + avgScore);
		System.out.println("totCount : " + totCount);
		
		//소수 둘째자리까지만 나타내기
		avgScore = Math.round(avgScore * 100) / 100.0;
		
		mE.update(avgScore, totCount, roles);
		
		memberRepo.save(mE);
	}
}









