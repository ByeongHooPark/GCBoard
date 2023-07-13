/**
 * 
 */
var email = "";

$(function(){
				$("#test").val(777);
        		$(".btn-mailsend").click(mailsendClicked);
        		$(".btn-check").click(authChecked);
//        		var form = document.querySelector('form');
//				var formData = new FormData(form);
//        		formData.append('authFlag', 777);
        	});
        	
        	//비동기-인증번호 메일 보내기
        	function mailsendClicked(){
				
				$(".btn-de").hide();
				$(".btn-re").show();
				
        		//메일 보내기위한 메일주소
        		email = $("#email").val().trim();
        		
        		//csrf
        		var token = $("meta[name='_csrf']").attr("content");
			var header = $("meta[name='_csrf_header']").attr("content");
        		
        		$.ajax({
				//csrf
				beforeSend:function(xhr) {xhr.setRequestHeader(header, token);},
        			url:"/signup/email",
        			type:"post",
        			data:{toEmail:email},
        			success:function(){//0
        				console.log("성공!");
						$("#btn-check-wrap").show();
        			}
        		})
        	};
        	
        	//비동기-인증번호 검증하기
        	function authChecked(){
				
				//입력코드
        		var inCode = $("#in-code").val().trim();
        		
        		//csrf
        		var token = $("meta[name='_csrf']").attr("content");
				var header = $("meta[name='_csrf_header']").attr("content");
        		
        		$.ajax({
					//csrf
					beforeSend:function(xhr) {xhr.setRequestHeader(header, token);},
        			url:"/signup/check",
        			type:"post",
        			data:{email:email,code:inCode},
        			success:function(result){//result : 0(성공) or 1(실패)
						//$(".auth-flag").val() : 인증하지 않았을시 -1, 인증성공시 0
						
						//1)인증 성공시
						if(result==0){
							//성공 표시값 입력

							//인증 블록 제거
							$(".auth-wrap").hide();
							//성공 메세지 블록 생성
							$(".auth-success").show();
						//2)인증 실패시
						} else{
							alert("인증코드가 만료되었거나 일치하지 않습니다.");
						}
        			}
        		})
}












 
 