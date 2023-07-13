/**
 * 
 */
 
 function createRoom(){
	 
	//csrf
	var token = $("meta[name='_csrf']").attr("content");
	var header = $("meta[name='_csrf_header']").attr("content");
	
	var title = $("#title").val();
	
	$.ajax({
	  beforeSend:function(xhr) {xhr.setRequestHeader(header, token);},
	  url: '/chess/create',
	  type: 'POST',
	  data: {title:title},
	  success: function(crno) {
	    // 새 창 열기
//	    var newWindow = window.open(`/chess/room/${crno}`);
	      window.open(location.origin+`/chess/room/${crno}`,'게임방${crno}',"width=1200, height=900","menubar=no, toolbar=no");
	  }
	});
 }
 

