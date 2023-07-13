/**
 * 
 */

$(function() {
	
	$(".left-square").hover(
		function() {
			$(".big-G").hide();
			$(".G-hover-span").show();
			$(".left-square").css("background-color","#a9d6f5")
		},
		function() {
			$(".big-G").show();
			$(".G-hover-span").hide();
			$(".left-square").css("background-color","#fff")
		});
		
	$(".right-square").hover(
		function() {
			$(".big-C").hide();
			$(".C-hover-span").show();
			$(".right-square").css("background-color","#87d37c")
		},
		function() {
			$(".big-C").show();
			$(".C-hover-span").hide();
			$(".right-square").css("background-color","#fff")
		});
		
})































