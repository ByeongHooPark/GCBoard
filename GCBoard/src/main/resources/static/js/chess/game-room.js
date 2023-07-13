/**
 * 
 */

/* 웹소켓 */
let stompClient = null;
//csrf
let token = $("meta[name='_csrf']").attr("content");
let header = $("meta[name='_csrf_header']").attr("content");

/***************  chess관련 전역변수들 ******************/
let crno = 0;
let title = null;

let whiteNick = "미정";
let blackNick = "미정";

let myNomalNick = "아직 닉네임이 없습니다";
let myEncodedNick;

let count = 0;
let state = "대기";//대기, 게임중, 선택중, 무르기. 다 넣으면 좋은데 앞에것 두 개만 넣겠다.

/* 대문자 : 백 / 소문자 : 흑 */
let chessBoard = [
	["r", "n", "b", "q", "k", "b", "n", "r"],
	["p", "p", "p", "p", "p", "p", "p", "p"],
	["", "", "", "", "", "", "", ""],
	["", "", "", "", "", "", "", ""],
	["", "", "", "", "", "", "", ""],
	["", "", "", "", "", "", "", ""],
	["P", "P", "P", "P", "P", "P", "P", "P"],
	["R", "N", "B", "Q", "K", "B", "N", "R"]
];

const defaultBoard = [
	["r", "n", "b", "q", "k", "b", "n", "r"],
	["p", "p", "p", "p", "p", "p", "p", "p"],
	["", "", "", "", "", "", "", ""],
	["", "", "", "", "", "", "", ""],
	["", "", "", "", "", "", "", ""],
	["", "", "", "", "", "", "", ""],
	["P", "P", "P", "P", "P", "P", "P", "P"],
	["R", "N", "B", "Q", "K", "B", "N", "R"]
];

//말선택시 좌표
let selectedRow = -1;
let selectedCol = -1;

/*********유틸 함수******************************************************/

/* 버튼 활성화/비활성화 조절 */
//채팅
function chatActive() { $("#chat-btn").prop("disabled", false); }
function chatDisable() { $("#chat-btn").prop("disabled", true); }
//무르기
function backActive() { $("#back-btn").prop("disabled", false); }
function backDisable() { $("#back-btn").prop("disabled", true); }
//플레이어 선택 버튼
function whiteActive() { $("#select-white").prop("disabled", false); }
function whiteDisable() { $("#select-white").prop("disabled", true); }
function blackActive() { $("#select-black").prop("disabled", false); }
function blackDisable() { $("#select-black").prop("disabled", true); }
//체스판
function chessActive() { $('.chess-square').css('pointer-events', 'auto'); }
function chessDisable() { $('.chess-square').css('pointer-events', 'none'); }

/*흑 체스판 돌리기*/
function rotateBlack() {
	$('.game-board').css('transform', 'rotate(180deg)');
	$(".chess-square").addClass("black-lotate");
}

/*********************************************************************/
$(function() {
	//테이블 세팅
	initSetting();
	//기물 넣기
	locateObject();

	//버튼 비활성화
	backDisable();
	chatDisable();
	//체스판 비활성화
	chessDisable();

	//form태그 submit 비활성화
	$("form").on('submit', function(e) {
		e.preventDefault();
	});

	//민트(플레이어가 말 선택시 나타나는 영역) 핸들러
	onMintHandler();
	onNonMintHandler();

})
/******************웹소켓 아닌 짧은 함수(기물 움직임 함수는 맨아래)*****************************/
/* 게임 시작 */
function startGame() {

	state = "게임중";
	$(".state").text(state);

	$.ajax({
		beforeSend: function(xhr) { xhr.setRequestHeader(header, token); },
		url: '/chess/start',
		type: 'post',
		data: {
			crno: crno,
			mynick: myEncodedNick
		},
		success: function(flag) {
			//플레이어라면 체스판, 무르기버튼 활성화
			if (flag != "observer") {
				backActive();
			}
			if (flag == "white") {
				chessActive();
			}
			console.log("flag : " + flag);

			let massageTag = `<div><span>게임을 시작합니다!</span></div>`
			$(".chat-board").append(massageTag);
		}
	});
}

/* crno,title 세팅 + 체스판 만들기  */
function initSetting() {

	crno = $("#crno-input").val();
	title = $("#title-span").text();

	//1행,열 정보 삽입
	//2색칠하기
	let chessBoardTag = "";
	let squareColor = "";
	for (let row = 0; row < 8; row++) {
		let chessSquares = "";
		for (let col = 0; col < 8; col++) {
			//2. 색칠하기
			if ((row + col) % 2 == 0) { squareColor = "#F5F5DC"; } else { squareColor = "#8B4513"; }
			//1행,열 정보 삽입
			chessSquares += `<div class="chess-square" data-row=${row} data-col=${col} onclick="selectObject(this)" style="background-color: ${squareColor}"></div>`;
		}
		chessBoardTag = `<div class="row">${chessSquares}</div>`
		$(".game-board").append(chessBoardTag);
	}
}

//게임 종료 팝업 아래의 버튼누르면 호출
//내 화면과 전역변수를 DB와 동기화 해야함(내가 늦게 누르면 다른사람이 미리 진행했을수도 있다)
function gameFin() {
	$.ajax({
		url: '/chess/regame',
		data: { crno: crno },
		success: function(data) {

			$(".state").text(data.state);
			$(".white-nick").text(data.whiteNick);
			$(".black-nick").text(data.blackNick);
			state = data.state;
			whiteNick = data.whiteNick;
			blackNick = data.blackNick;
			chessBoard = JSON.parse(data.currentBackUp).map(innerArray => Array.from(innerArray));

			locateObject();

			//1.2 화면 정리
			$(".welcome-popup").remove();
			$(".board-util").hide();

			if (data.state == "대기") {
				$(".board-util").show();
				$(".select-player").show();
				//대기 상태에서 누가 이미 한 진영을 선택했을 수도 있음
				//백만 선택되었을때
				if (whiteNick != "미정") {
					whiteDisable();
					$("#select-white").text("선택 불가");
					//흑만 선택되었을때	
				} else if (blackNick != "미정") {
					blackDisable();
					$("#select-black").text("선택 불가");
				}
			}

		}
	})

	$(".game-set").hide();
}


/********* 연결 : 웹소켓 함수 ************************************************/
/* 1. 닉네임 생성. */
/* 2. 구독 */
function connect() {
	$.ajax({
		beforeSend: function(xhr) { xhr.setRequestHeader(header, token); },
		url: '/chess/enter',
		type: 'post',
		data: { crno: crno },
		success: function(data) {
			//1. 정보 뿌리기+화면 정리, 2. 소켓 접속

			//1. 정보 뿌리기+화면 정리
			//1.1 정보 뿌리기 (내꺼만 바꾸기)
			$(".state").text(data.state);
			//$(".count").text(data.count);
			$(".white-nick").text(data.whiteNick);
			$(".black-nick").text(data.blackNick);
			whiteNick = data.whiteNick;
			blackNick = data.blackNick;

			myNomalNick = data.myNomalNick;
			$(".my-nick").text(myNomalNick);
			myEncodedNick = data.myEncodedNick;

			/*일단 되긴 하는데 잘 지켜봐야할듯*/
			chessBoard = JSON.parse(data.currentBackUp).map(innerArray => Array.from(innerArray));
			locateObject();
			//		  console.log(chessBoard);
			//		  console.log("chessBoard[][] : " + chessBoard[0][1]);

			let cnnoList = JSON.parse(data.cnnoList);
			//		  console.log(cnnoList);

			let nickList = JSON.parse(data.nickList);
			let nickarr = nickList.slice(1, -1).split(", ");
			//		  console.log(nickList);
			//		  console.log(nickarr);
			let userList = "";

			//1 : 내 이름 빼고 출력. 내 이름은 소켓 통신으로 다같이 한다
			for (let i = 0; i < cnnoList.length - 1; i++) {
				userList += `<span class="users-nick" data-no="${nickarr[i]}">${nickarr[i]}</span>`
			}
			userList = "<div class='user-list'>" + userList + "</div>"
			$(".user-list-wrap").append(userList);

			//1.2 화면 정리
			$(".welcome-popup").remove();
			$(".board-util").hide();

			if (data.state == "대기") {
				$(".board-util").show();
				$(".select-player").show();

				//대기 상태에서 누가 이미 한 진영을 선택했을 수도 있음
				//백만 선택되었을때
				if (whiteNick != "미정") {
					whiteDisable();
					$("#select-white").text("선택 불가");
					//흑만 선택되었을때	
				} else if (blackNick != "미정") {
					blackDisable();
					$("#select-black").text("선택 불가");
				}
			}

			//2. 소켓접속
			let socket = new SockJS("/my-ws");
			stompClient = Stomp.over(socket);

			//소켓에 넣을 데이터 정리
			let newUser = `<span class="users-nick" data-no="${nickarr[cnnoList.length - 1]}">${nickarr[cnnoList.length - 1]}</span>`
			let countAll = data.count;
			let chatHello = `<div class="chat-info">
								<span>[알림] </span>
								<span>${myNomalNick}</span>
								<span>님이 입장하셨습니다</span>
							</div>`
			//접속
			stompClient.connect({}, function(frame) {

				let helloData = {
					requestType: "hello",
					newUser: newUser,
					userCount: countAll,
					chatHello: chatHello
				};

				//구독하기 위해 메세지를 보내야한다?
				stompClient.send(`/app/hello/${crno}`, {}, JSON.stringify(helloData));
				stompClient.subscribe(`/topic/chess/${crno}`, function(e) {

					let dataBody = JSON.parse(e.body);
					let requestType = dataBody.requestType
					/**********************************************************************************/
					//1. 맨처음 구독시
					if (requestType == "hello") {
						console.log("여기까지!!");
						$(".user-list-wrap").append(dataBody.newUser);
						$(".count").text(dataBody.userCount);
						$(".chat-board").append(dataBody.chatHello);

						//2.채팅치기
					} else if (requestType == "msg") {
						console.log("여기까지!!");
						let massageTag = `<div><span>${dataBody.myNick}</span><span> : </span><span>${dataBody.sendMassage}</span></div>`
						$(".chat-board").append(massageTag);
						$(".chat-board").scrollTop($(".chat-board")[0].scrollHeight);

						//3.1 플레이어 백 선택
					} else if (requestType == "white") {
						console.log("여기까지!!");
						whiteNick = dataBody.myNick;
						$(".white-nick").text(whiteNick);

						let massageTag = `<div><span>${whiteNick}</span><span>님이 백 진영을 선택했습니다</span></div>`
						$(".chat-board").append(massageTag);

						//플레이어가 다 정해졌는지 여부
						//1. 다 정해졌을떄
						if (blackNick != "미정") {
							//화면 정리
							$(".board-util").hide();
							$(".select-player").hide();
							blackActive();
							$("#select-black").text("흑 선택");
							//게임 시작
							startGame();
							//2. 아닐때
						} else {
							console.log("누군가가 백을 선택했어요");
							whiteDisable();
							$("#select-white").text("선택 불가");
						}

						//3.2 플레이어 흑 선택
					} else if (requestType == "black") {
						console.log("여기까지!!");
						blackNick = dataBody.myNick;
						$(".black-nick").text(blackNick);

						//체스판 돌리기(흑만 해당)
						if (myNomalNick == blackNick) {
							rotateBlack();
						}

						let massageTag = `<div><span>${blackNick}</span><span>님이 흑 진영을 선택했습니다</span></div>`
						$(".chat-board").append(massageTag);

						//플레이어가 다 정해졌는지 여부
						//1. 다 정해졌을떄
						if (whiteNick != "미정") {
							//화면 정리
							$(".board-util").hide();
							$(".select-player").hide();
							whiteActive();
							$("#select-white").text("백 선택");
							//게임 시작
							startGame();
							//2. 아닐때
						} else {
							blackDisable();
							$("#select-black").text("선택 불가");
						}
						//4. 기물 놓기(게임판 화면에 뿌리기)
					} else if (requestType == "putObject") {

						chessBoard = JSON.parse(dataBody.currentBackUp).map(innerArray => Array.from(innerArray));
						locateObject();

						if (dataBody.playerNick == myNomalNick) {
							chessActive();
						}
						//5. 게임끝
					} else if (requestType == "gameSet") {
						
						console.log("게임끝!!게임끝!!게임끝!!게임끝!!게임끝!!게임끝!!");
						
						$(".game-set-title").text(dataBody.alertTitle);
						$(".reason-sentence").text(dataBody.alertReason);

						chessDisable();
						backDisable();

						$(".welcome-popup").hide();
						$(".game-set").show();
						$(".board-util").show();

						whiteNick = "미정";
						blackNick = "미정";

						state = "대기";
						chessBoard = defaultBoard;
						
						//6. 강제로 나가기
					} else if (requestType == "byebye"){
						$(`[data-no="${dataBody.targetNick}"]`).remove();
						$(".count").text($(".count").text()-1);
					}
				});
			});
			chatActive();
		}
	});
}


/*** 웹소켓함수 : 메세지 보내기 ***/
function sendMassage() {
	let sendMassage = $.trim($("#chat-input").val());
	$("#chat-input").val("")
	if (sendMassage == "") {
		alert("메세지가 입력되자 않았어요"); return;
	}

	let msgData = {
		requestType: "msg",
		myNick: myEncodedNick,
		sendMassage: sendMassage
	}
	//메세지 전달
	stompClient.send(`/app/sendMsg/${crno}`, {}, JSON.stringify(msgData));
}


/*** 웹소켓함수 : 플레이어 선택하기 ***/
function playerSelect(color) {

	//한 쪽 선택했으면 다른쪽 못함
	if (whiteNick == myNomalNick || blackNick == myNomalNick) { return; }

	let playerSelectData = {
		requestType: color,
		myNick: myEncodedNick,
	}

	stompClient.send(`/app/select-player/${crno}`, {}, JSON.stringify(playerSelectData));
}

//웹소켓 - 유저 접속 종료
function byebye() {
	
	//아직 접속 전이면 그냥 종료
	if(myNomalNick == "아직 닉네임이 없습니다"){
		window.close();
		return;
	}
	
	//1.DB > 웹소켓으로 DB에 우선 반영(체스방-count, nick-row 자체)
	//2.DB > count가 0이되면 혹시모르니 관련 nick을 전부 삭제후, 체스방 DB를 삭제한다
	//3.웹소켓 > 정보를 뿌려야한다.참여자 목록에서 지우기, count 1 내리기
	let byebyeData = {
			requestType: "byebye",
			targetNick: myEncodedNick
		}
		
	stompClient.send(`/app/byebye/${crno}`, {}, JSON.stringify(byebyeData));
	
	//플레이어가 나가면? -> 게임종료, 상대방이 승리
	if(myNomalNick == whiteNick || myNomalNick == blackNick){
		let winnerSide = null;
		let nick = "미정";
		
		//흑 승(백이 나감)
		if (myNomalNick == whiteNick) {
			winnerSide = "흑";
			nick = blackNick;
			//백 승(흑이 나감)
		} else if (myNomalNick == blackNick) {
			winnerSide = "백";
			nick = whiteNick; 
		}
	
		let alertTitle = winnerSide + "진영 " + nick + "님의 승리!";
		let alertReason = "상대가 접속을 종료했습니다";
	
		let resetBoard = JSON.stringify(defaultBoard);
	
		let gameSetData = {
			requestType: "gameSet",
			alertTitle: alertTitle,
			alertReason: alertReason,
			resetBoard: resetBoard
		}
		stompClient.send(`/app/gameSet/${crno}`, {}, JSON.stringify(gameSetData));
	}
	window.close();
}

/**************************웹소켓함수 : 민트,논민트 핸들러***********************************************/
function onMintHandler() {
	//잘모름. 이벤트 리스너 : mint-target을 클릭했을 시 //이벤트 위임. 동적으로 생성된 태그에 대해서도 적용
	$(document).on('click.mint', '.mint-target', function(event) {
		console.log("민트 핸들러")

		let mintTarget = event.target;

		//논리 좌표값
		let row = $(mintTarget).data('row');
		let col = $(mintTarget).data('col');

		//클릭된 민트 논리좌표의 값
		let mintLocate = chessBoard[row][col];
		$('.mint-target').removeClass('mint-target');

		//민트 논리좌표에 움직인 말의 값 넣기
		chessBoard[row][col] = chessBoard[selectedRow][selectedCol];
		//말이 있던 좌표의 값을 비우기
		chessBoard[selectedRow][selectedCol] = "";

		let updateBoard = JSON.stringify(chessBoard);

		let putObjectData = {
			requestType: "putObject",
			currentBackUp: updateBoard,//
			playerNick: myNomalNick//돌을 둔사람, 원래 인코드 닉을 넣어야 하지만,
			//넣어봤자 효과가 없다는것을 알고 필요 없을때는 넣지 않는다.
		}

		//체스판 비활성화
		chessDisable();
		//소켓 바뀐 체스 보드판 뿌리기
		stompClient.send(`/app/putObject/${crno}`, {}, JSON.stringify(putObjectData));

		//킹을 잡으면 게임 끝냄
		//게임 끝
		if (mintLocate == 'k' || mintLocate == 'K') {
			let winnerSide = null;
			//백 승
			if (mintLocate == 'k') {
				winnerSide = "백";
				//흑 승
			} else { winnerSide = "흑"; }

			let resetBoard = JSON.stringify(defaultBoard);

			let alertTitle = winnerSide + "진영 " + myNomalNick + "님의 승리!";
			let alertReason = "상대의 킹을 잡았습니다";

			let gameSetData = {
				requestType: "gameSet",
				alertTitle: alertTitle,
				alertReason: alertReason,
				resetBoard: resetBoard
			}

			stompClient.send(`/app/gameSet/${crno}`, {}, JSON.stringify(gameSetData));
		}

	});
};

function onNonMintHandler() {
	//잘모름. 이벤트 리스너 : mint-target 외 다른곳을 클릭했을 시
	$(document).on('click.nonMint', function(event) {
		console.log("논민트 핸들러");
		let targetElement = event.target;
		if (!$(targetElement).is('.mint-target')) {
			$('.mint-target').removeClass('mint-target');
		}
	});
};



/***************************** 말선택 - 기물 움직임 코드 **********************************/
/* 플레이어 말 선택 */
let validTarget = [];//좌표 배열
function selectObject(e) {

	let row = parseInt($(e).attr('data-row'));
	let col = parseInt($(e).attr('data-col'));

	console.log("row : " + row);
	console.log("col : " + col);

	let objectC = chessBoard[row][col];



	//	console.log("말 정보 : " + objectC);
	//빈곳 찍으면 아무것도 안함
	if (objectC == "") return;

	$.ajax({
		url: '/chess/select-object',
		data: {
			crno: crno,
			mynick: myEncodedNick
		},
		success: function(data) {//black, white

			//좌표 배열
			validTarget = [];

			//피아 구분이 필요하므로 다 따로해야할듯
			if ((data == "black") && ("a" <= objectC) && ("z" >= objectC)) {
				//폰
				if (objectC == 'p') {
					//1칸앞
					if ((row + 1 < 8) && chessBoard[row + 1][col] == "") { validTarget.push([row + 1, col]) };
					//2칸앞
					if ((row + 2 < 8) && row == 1 && chessBoard[row + 2][col] == "" && chessBoard[row + 1][col] == "") { validTarget.push([row + 2, col]) };
					//오른대각선 먹기
					if ((row + 1 < 8) && isWhite(chessBoard[row + 1][col + 1])) { validTarget.push([row + 1, col + 1]) };
					//왼대각선 먹기
					if ((row + 1 < 8) && isWhite(chessBoard[row + 1][col - 1])) { validTarget.push([row + 1, col - 1]) };
					//킹
				} else if (objectC == 'k') {
					kingRoute(objectC, row, col);

					//퀸
				} else if (objectC == 'q') {
					queenRoute(objectC, row, col);

					//비숍
				} else if (objectC == 'b') {
					bishopRoute(objectC, row, col);

					//나이트
				} else if (objectC == 'n') {
					knightRoute(objectC, row, col);

					//룩	
				} else if (objectC == 'r') {
					rookRoute(objectC, row, col);
				}

			} else if ((data == "white") && ("A" <= objectC) && ("Z" >= objectC)) {
				if (objectC == 'P') {
					//1칸앞
					if ((row - 1 >= 0) && chessBoard[row - 1][col] == "") { validTarget.push([row - 1, col]) };
					//2칸앞
					if ((row - 2 >= 0) && row == 6 && chessBoard[row - 2][col] == "" && chessBoard[row - 1][col] == "") { validTarget.push([row - 2, col]) };
					//오른대각선 먹기
					if ((row - 1 >= 0) && isBlack(chessBoard[row - 1][col + 1])) { validTarget.push([row - 1, col + 1]) };
					//왼대각선 먹기
					if ((row - 1 >= 0) && isBlack(chessBoard[row - 1][col - 1])) { validTarget.push([row - 1, col - 1]) };
					//킹
				} else if (objectC == 'K') {
					kingRoute(objectC, row, col);

					//퀸
				} else if (objectC == 'Q') {
					queenRoute(objectC, row, col);

					//비숍
				} else if (objectC == 'B') {
					bishopRoute(objectC, row, col);

					//나이트
				} else if (objectC == 'N') {
					knightRoute(objectC, row, col);

					//룩	
				} else if (objectC == 'R') {
					rookRoute(objectC, row, col);
				} else { return; }
			}

			//논리적 좌표값 전역변수로 킵(리스너로간다)
			selectedRow = row;
			selectedCol = col;

			//할일 선택한 좌표값에 class 부여.ㅇ
			//'말놓기'함수에서 내가 움직일 말과, class가 부여된 태그들을 픽할수 있어야함
			//갈수 있는 곳이 없으면 선택 안되게 해야함
			//하나를 픽한 후에 다른 말을 눌러도 안됨. 색이 변한곳 외에 다른 곳을 누르면 픽이 취소되게 해야함 
			for (let arr of validTarget) {//arr : [row, col]
				$(`[data-row=${arr[0]}][data-col=${arr[1]}]`).addClass('mint-target');
			}

		}
	})
}


/* 말선택 함수의 보조함수 */
//피아구분을 위한 함수
function isBlack(e) { if (e == "" || e === undefined) return false; if ("A" <= e && "Z" >= e) { return false; } else { return true; } };
function isWhite(e) { if (e == "" || e === undefined) return false; if ("A" <= e && "Z" >= e) { return true; } else { return false; } };
function isTeam(a, b) { if ((isBlack(a) && isBlack(b)) || (isWhite(a) && isWhite(b))) { return true; } else { return false; } };
function isEnemy(a, b) { if ((isBlack(a) && isWhite(b)) || (isWhite(a) && isBlack(b))) { return true; } else { return false; } };
//양측 말 경로 공통함수(논리이름값, row, col)
function kingRoute(name, row, col) {
	//위로
	if ((row - 1 >= 0) && (chessBoard[row - 1][col] == "" || isEnemy(name, chessBoard[row - 1][col]))) { validTarget.push([row - 1, col]) };
	//아래로
	if ((row + 1 < 8) && (chessBoard[row + 1][col] == "" || isEnemy(name, chessBoard[row + 1][col]))) { validTarget.push([row + 1, col]) };
	//왼쪽으로
	if ((col - 1 >= 0) && (chessBoard[row][col - 1] == "" || isEnemy(name, chessBoard[row][col - 1]))) { validTarget.push([row, col - 1]) };
	//오른쪽으로
	if ((col + 1 < 8) && (chessBoard[row][col + 1] == "" || isEnemy(name, chessBoard[row][col + 1]))) { validTarget.push([row, col + 1]) };
	//왼쪽 위로
	if ((row - 1 >= 0 && col - 1 >= 0) && (chessBoard[row - 1][col - 1] == "" || isEnemy(name, chessBoard[row - 1][col - 1]))) { validTarget.push([row - 1, col - 1]) };
	//오른쪽 위로
	if ((row - 1 >= 0 && col + 1 < 8) && (chessBoard[row - 1][col + 1] == "" || isEnemy(name, chessBoard[row - 1][col + 1]))) { validTarget.push([row - 1, col + 1]) };
	//왼쪽 아래로
	if ((row + 1 < 8 && col - 1 >= 0) && (chessBoard[row + 1][col - 1] == "" || isEnemy(name, chessBoard[row + 1][col - 1]))) { validTarget.push([row + 1, col - 1]) };
	//오른쪽 아래로
	if ((row + 1 < 8 && col + 1 < 8) && (chessBoard[row + 1][col + 1] == "" || isEnemy(name, chessBoard[row + 1][col + 1]))) { validTarget.push([row + 1, col + 1]) };
};

function queenRoute(name, row, col) {
	bishopRoute(name, row, col);
	rookRoute(name, row, col);
};

function bishopRoute(name, row, col) {
	let i = 0;
	let target;
	//왼쪽 위로
	while (true) {
		i++;
		if (row - i < 0 || col - i < 0) { break; };
		target = chessBoard[row - i][col - i];
		if (target == "") {
			validTarget.push([row - i, col - i])
		} else if (isEnemy(name, target)) {
			validTarget.push([row - i, col - i])
			break;
		} else if (isTeam(name, target))
			break;
	}
	//오른쪽 위로
	i = 0;
	while (true) {
		i++;
		if (row - i < 0 || col + i > 7) { break; };
		target = chessBoard[row - i][col + i];
		if (target == "") {
			validTarget.push([row - i, col + i])
		} else if (isEnemy(name, target)) {
			validTarget.push([row - i, col + i])
			break;
		} else if (isTeam(name, target))
			break;
	}

	//오른쪽 아래로
	i = 0;
	while (true) {
		i++;
		if (row + i > 7 || col > 7) { break; };
		target = chessBoard[row + i][col + i];
		if (target == "") {
			validTarget.push([row + i, col + i])
		} else if (isEnemy(name, target)) {
			validTarget.push([row + i, col + i])
			break;
		} else if (isTeam(name, target))
			break;
	}


	//왼쪽 아래로
	i = 0;
	while (true) {
		i++;
		if (row + i > 7 || col - i < 0) { break; };
		target = chessBoard[row + i][col - i];
		if (target == "") {
			validTarget.push([row + i, col - i])
		} else if (isEnemy(name, target)) {
			validTarget.push([row + i, col - i])
			break;
		} else if (isTeam(name, target))
			break;
	}
};


function rookRoute(name, row, col) {
	let i = 0;
	let target;
	//위로
	while (true) {
		i++;
		console.log("i : " + i);
		console.log("row : " + row);
		console.log("row - i : " + row - i);

		if (row - i < 0) { break; };
		target = chessBoard[row - i][col];
		if (target == "") {
			validTarget.push([row - i, col])
		} else if (isEnemy(name, target)) {
			validTarget.push([row - i, col])
			break;
		} else if (isTeam(name, target))
			break;
	}

	//아래로
	i = 0;
	while (true) {
		i++;
		if (row + i > 7) { break; };
		target = chessBoard[row + i][col];
		if (target == "") {
			validTarget.push([row + i, col])
		} else if (isEnemy(name, target)) {
			validTarget.push([row + i, col])
			break;
		} else if (isTeam(name, target))
			break;
	}

	//왼쪽으로
	i = 0;
	while (true) {
		i++;
		if (col < 0) { break; };
		target = chessBoard[row][col - i];
		if (target == "") {
			validTarget.push([row, col - i])
		} else if (isEnemy(name, target)) {
			validTarget.push([row, col - i])
			break;
		} else if (target === undefined || isTeam(name, target))
			break;
	}


	//오른쪽으로
	i = 0;
	while (true) {
		i++;
		if (col > 7) { break; };
		if (chessBoard[row][col + i] === undefined) { break; };
		target = chessBoard[row][col + i];
		if (target == "") {
			validTarget.push([row, col + i])
		} else if (isEnemy(name, target)) {
			validTarget.push([row, col + i])
			break;
		} else if (target === undefined || isTeam(name, target))
			break;
	}
};
function knightRoute(name, row, col) {
	//왼쪽2칸, 위로1칸
	if ((row - 1 >= 0) && (chessBoard[row - 1][col - 2] == "" || isEnemy(name, chessBoard[row - 1][col - 2]))) { validTarget.push([row - 1, col - 2]) };
	//왼쪽2칸, 아래로1칸
	if ((row + 1 < 8) && (chessBoard[row + 1][col - 2] == "" || isEnemy(name, chessBoard[row + 1][col - 2]))) { validTarget.push([row + 1, col - 2]) };
	//위로2칸, 왼쪽1칸
	if ((row - 2 >= 0) && (chessBoard[row - 2][col - 1] == "" || isEnemy(name, chessBoard[row - 2][col - 1]))) { validTarget.push([row - 2, col - 1]) };
	//위로2칸, 오른쪽1칸
	if ((row - 2 >= 0) && (chessBoard[row - 2][col + 1] == "" || isEnemy(name, chessBoard[row - 2][col + 1]))) { validTarget.push([row - 2, col + 1]) };
	//오른쪽2칸, 위로1칸
	if ((row - 1 >= 0) && (chessBoard[row - 1][col + 2] == "" || isEnemy(name, chessBoard[row - 1][col + 2]))) { validTarget.push([row - 1, col + 2]) };
	//오른쪽2칸, 아래로1칸
	if ((row + 1 < 8) && (chessBoard[row + 1][col + 2] == "" || isEnemy(name, chessBoard[row + 1][col + 2]))) { validTarget.push([row + 1, col + 2]) };
	//아래로2칸, 왼쪽1칸
	if ((row + 2 < 8) && (chessBoard[row + 2][col - 1] == "" || isEnemy(name, chessBoard[row + 2][col - 1]))) { validTarget.push([row + 2, col - 1]) };
	//아래로2칸, 오른쪽1칸
	if ((row + 2 < 8) && (chessBoard[row + 2][col + 1] == "" || isEnemy(name, chessBoard[row + 2][col + 1]))) { validTarget.push([row + 2, col + 1]) };
};

/************************************************************************************/

/*이중배열 논리 위치로 말 옮기기*/
function locateObject() {

	let element;

	//일단 이미지 다 제거
	$(".chess-square").css("background-image", "none");

	//재배치
	for (let i = 0; i < 8; i++) {
		for (let j = 0; j < 8; j++) {
			if (chessBoard[i][j] != "") {

				//태그
				element = $(`[data-row=${i}][data-col=${j}]`);

				//이중배열 상의 논리적 말의 위치
				let object = chessBoard[i][j];


				if (object == "R") {
					element.css('background-image', 'url(/image/chess/white-rook.png)');
				} else if (object == "N") {
					element.css('background-image', 'url(/image/chess/white-knight.png)');
				} else if (object == "B") {
					element.css('background-image', 'url(/image/chess/white-bishop.png)');
				} else if (object == "Q") {
					element.css('background-image', 'url(/image/chess/white-queen.png)');
				} else if (object == "K") {
					element.css('background-image', 'url(/image/chess/white-king.png)');
				} else if (object == "P") {
					element.css('background-image', 'url(/image/chess/white-pawn.png)');
				} else if (object == "r") {
					element.css('background-image', 'url(/image/chess/black-rook.png)');
				} else if (object == "n") {
					element.css('background-image', 'url(/image/chess/black-knight.png)');
				} else if (object == "b") {
					element.css('background-image', 'url(/image/chess/black-bishop.png)');
				} else if (object == "q") {
					element.css('background-image', 'url(/image/chess/black-queen.png)');
				} else if (object == "k") {
					element.css('background-image', 'url(/image/chess/black-king.png)');
				} else if (object == "p") {
					element.css('background-image', 'url(/image/chess/black-pawn.png)');
				}
			}
		}
	}
}


/*******************************************************************************/














