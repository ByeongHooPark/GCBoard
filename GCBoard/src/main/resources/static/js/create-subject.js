/**
 * 
 */

 function createRandom(){
	 
	 $.ajax({
	    url: '/argue/random',
	    success: function(result){//name이 title, detail인 map
			
			console.log("title : "+result.title);
			console.log("detail : "+result.detail);
			
			$("#title").val(result.title);
			$("#detail").val(result.detail);
			
		}
	})
	 
 }