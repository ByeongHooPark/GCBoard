# GCBoard


<br/><br/>
 
# :zap: 프로젝트소개
<br/>

### GCBoard는 Gpt Board + Chess Board 라는 뜻으로 독립적인 두 어플리케이션인 'Gpt 토론사이트'와 '체스게시판'으로 구성된 프로젝트 입니다.
<img src="https://github.com/ByeongHooPark/GCBoard/assets/123047580/5c5c8b5c-474e-44c1-bced-a87bafafda41"  width="300" height="300"/>
<img src="
https://github.com/ByeongHooPark/GCBoard/assets/123047580/d76193c1-d2a1-49e8-bb78-7d91d69a9509"  width="300" height="300"/>


##  1. GPT 토론사이트

#### 정보통신기술의 발달로 정보가 넘쳐나는 현대사회에서는 사람의 힘으로 생산되는 모든 정보에 가치를 매기는 거나 정보의 옳고 그름을 판단하기가 힘듭니다. 즉, 발달된 기술에도 불구하고 사람들은 여전히 양질의 정보에 접근하기가 힘듭니다. 이러한 문제점에 대해 ‘AI가 인터넷 상에서 생산되는 정보를 평가하면 어떨까?’라는 생각이 들었고 이 게시판을 만들게 되었습니다.

#### GPT를 이용해 토론 주제를 생성할 수 있습니다. 정해진 주제에 대해 사용자들은 주장과 논거를 제시할 수 있으며, 이 주장들의 논리적 타당성이 GPT에 의해 평가됩니다.

<br/><br/>

##  2. 체스 게시판

#### 사용자들끼리 자유롭게 체스 게임방을 만들고 채팅 등의 상호작용을 할 수 있는 웹소켓을 이용한 체스 어플리케이션입니다. 

<br/>
<br/>
<br/>
  
# :zap: 주요 기능설명 - GPT 토론사이트
<br/>

:turtle: 체스 게시판의 코드는 각 java, html, css, js 디렉토라 안의 chess 디렉토리 안에 모아두었습니다. 그 외의 코드들은 공통 코드와 GPT토론사이트의 코드들입니다.

<br/><br/>

##  1. GPT 토론사이트
<br/>

### :penguin: GPT를 이용한 입출력 (토론 주제 선정, 토론 주장 평가)
 #### 입출력 방식은 Chat GPT와 같습니다. 그런데 저의 어플리케이션에서는 GPT의 답변을 두 가지로 나눠서 받을 필요가 있었습니다. 그래서 입력 프롬프트에 특정한 name값을 갖는 JSON형식으로 출력해달라고 하고 얻은 value값으로 DB와 뷰에 반영했습니다. 물론 GPT가 유효한 JSON형식으로 답하지 못할때도 많았는데 예외처리로 일정한 형식의 값을 반환하도록 설정했습니다.

<br/>

> #### 관련 코드  
> #### /service/ProjectServiceProcess.java 의 1, 2, 7, 8, 9번 메서드 
> #### /service/ProjectServiceUtil.java 의 3, 3.1번 메서드 

<br/>
<br/>

### :penguin: 점수와 참여 횟수에 따른 권한부여
 #### 유저들은 토론을 하며 GPT에게 점수를 부여받습니다. 평균점수와 토론 참여 횟수를 바탕으로 5가지 등급의 권한이 부여가 되며, 이는 개인정보 페이지에서 확인할 수 있습니다. 낮은 등급의 유저가 등급이 오를때마다 특정한 문구가 하나씩 보이게 됩니다.

<br/>

> #### 관련 코드  
> #### /service/ProjectServiceUtil.java 의 3.2번 메서드
> #### /info/individual-info.html 의 'tier-wrap'클래스를 가진 div태그

<br/>
<br/>



##  2. 체스 게시판

<br/>



<br/><br/>













