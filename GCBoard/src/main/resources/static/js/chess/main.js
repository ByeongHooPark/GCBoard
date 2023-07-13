/**
 * 
 */
function joinRoom(element) {
  let crno = $(element).next().val();
  console.log(crno);
  window.open(`http://localhost:8080/chess/room/${crno}`, `게임방${crno}`, "width=1200, height=900", "menubar=no, toolbar=no");
}

 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 