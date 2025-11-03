/* ========================= NFL TEAMS (FULL DATA) ========================= */
const nflTeams=[
{name:"Cowboys",location:"Dallas",color:"#041E42",division:"NFC East"},
{name:"Giants",location:"New York",color:"#0B2265",division:"NFC East"},
{name:"Eagles",location:"Philadelphia",color:"#004C54",division:"NFC East"},
{name:"Commanders",location:"Washington",color:"#773141",division:"NFC East"},

{name:"Packers",location:"Green Bay",color:"#203731",division:"NFC North"},
{name:"Bears",location:"Chicago",color:"#0B162A",division:"NFC North"},
{name:"Vikings",location:"Minnesota",color:"#4F2683",division:"NFC North"},
{name:"Lions",location:"Detroit",color:"#0076B6",division:"NFC North"},

{name:"Buccaneers",location:"Tampa Bay",color:"#D50A0A",division:"NFC South"},
{name:"Saints",location:"New Orleans",color:"#D3BC8D",division:"NFC South"},
{name:"Falcons",location:"Atlanta",color:"#A71930",division:"NFC South"},
{name:"Panthers",location:"Carolina",color:"#0085CA",division:"NFC South"},

{name:"49ers",location:"San Francisco",color:"#AA0000",division:"NFC West"},
{name:"Rams",location:"Los Angeles",color:"#003594",division:"NFC West"},
{name:"Seahawks",location:"Seattle",color:"#002244",division:"NFC West"},
{name:"Cardinals",location:"Arizona",color:"#97233F",division:"NFC West"},

{name:"Patriots",location:"New England",color:"#002244",division:"AFC East"},
{name:"Bills",location:"Buffalo",color:"#00338D",division:"AFC East"},
{name:"Jets",location:"New York",color:"#125740",division:"AFC East"},
{name:"Dolphins",location:"Miami",color:"#008E97",division:"AFC East"},

{name:"Steelers",location:"Pittsburgh",color:"#FFB612",division:"AFC North"},
{name:"Ravens",location:"Baltimore",color:"#241773",division:"AFC North"},
{name:"Bengals",location:"Cincinnati",color:"#FB4F14",division:"AFC North"},
{name:"Browns",location:"Cleveland",color:"#311D00",division:"AFC North"},

{name:"Colts",location:"Indianapolis",color:"#002C5F",division:"AFC South"},
{name:"Titans",location:"Tennessee",color:"#4B92DB",division:"AFC South"},
{name:"Jaguars",location:"Jacksonville",color:"#006778",division:"AFC South"},
{name:"Texans",location:"Houston",color:"#03202F",division:"AFC South"},

{name:"Chiefs",location:"Kansas City",color:"#E31837",division:"AFC West"},
{name:"Raiders",location:"Las Vegas",color:"#000000",division:"AFC West"},
{name:"Chargers",location:"Los Angeles",color:"#0080C6",division:"AFC West"},
{name:"Broncos",location:"Denver",color:"#FB4F14",division:"AFC West"}
];

// temp for testing
localStorage.clear();

/* ========================= STATE & PERSISTENCE ========================= */
let games=[],currentGame=null,editIndex=null;

function saveGames(){ try{ localStorage.setItem("footballpool_games", JSON.stringify(games)); }catch(e){} }
function loadGames(){
  try{
    const data=JSON.parse(localStorage.getItem("footballpool_games")||"[]");
    if(Array.isArray(data)) games=data;
  }catch(e){ games=[]; }
}

/* ========================= UTIL ========================= */
function getTextColor(bg){
  bg=bg.replace("#","");
  const r=parseInt(bg.substring(0,2),16),
        g=parseInt(bg.substring(2,4),16),
        b=parseInt(bg.substring(4,6),16);
  return ((0.299*r+0.587*g+0.114*b)/255)>0.6?"#000":"#fff";
}
function makePillHTML(team,color){
  const textColor=getTextColor(color);
  return `<span style="background:${color};color:${textColor};border:2px solid #fff;padding:2px 8px;border-radius:6px;font-weight:bold;">${team}</span>`;
}
function shuffle(a){for(let i=a.length-1;i>0;i--){const j=Math.floor(Math.random()*(i+1));[a[i],a[j]]=[a[j],a[i]]}return a;}

/* >>> ADDED: helper to partition 0–9 into 5 disjoint pairs (for 5×5) <<< */
function makeDigitPairs(){
  const digits=[0,1,2,3,4,5,6,7,8,9];
  shuffle(digits);
  return [[digits[0],digits[1]],[digits[2],digits[3]],[digits[4],digits[5]],[digits[6],digits[7]],[digits[8],digits[9]]];
}

/* ========================= SETUP DROPDOWNS ========================= */
function populateTeamDropdowns(){
  const divisions=[...new Set(nflTeams.map(t=>t.division))];
  const a=document.getElementById('teamA'), b=document.getElementById('teamB');
  if(a){
    a.innerHTML='<option value="" disabled selected>Select Team</option>';
    b.innerHTML='<option value="" disabled selected>Select Team</option>';
    divisions.forEach(div=>{
      const groupA=document.createElement('optgroup');groupA.label=div;
      const groupB=document.createElement('optgroup');groupB.label=div;
      nflTeams.filter(t=>t.division===div).forEach(t=>{
        const optA=document.createElement('option');optA.value=t.name;optA.textContent=`${t.location} ${t.name}`;groupA.appendChild(optA);
        const optB=document.createElement('option');optB.value=t.name;optB.textContent=`${t.location} ${t.name}`;groupB.appendChild(optB);
      });
      a.appendChild(groupA); b.appendChild(groupB);
    });
  }
  const weekSel=document.getElementById('weekSelect');
  if(weekSel && !weekSel.options.length){
    for(let i=1;i<=18;i++){const o=document.createElement('option');o.value=`Week ${i}`;o.textContent=`Week ${i}`;weekSel.appendChild(o);}
    ["Playoffs","Super Bowl"].forEach(w=>{const o=document.createElement('option');o.value=w;o.textContent=w;weekSel.appendChild(o);});
  }
}

/* ========================= POPUPS ========================= */
function openCreatePopup(){
  document.getElementById('teamA').selectedIndex=0;
  document.getElementById('teamB').selectedIndex=0;
  document.getElementById('weekSelect').selectedIndex=0;
  document.getElementById('createPopup').style.display='flex';
}
function closeCreatePopup(){document.getElementById('createPopup').style.display='none';}

/* ========================= GAME CREATION ========================= */
function createGame(){
  const nameA=document.getElementById('teamA').value;
  const nameB=document.getElementById('teamB').value;
  const week=document.getElementById('weekSelect').value||"Week 1";
  if(!nameA||!nameB)return alert("Please select both teams");

  const teamAObj=nflTeams.find(t=>t.name===nameA);
  const teamBObj=nflTeams.find(t=>t.name===nameB);
  const n=parseInt(gridSize.value,10);
  const squares=Array.from({length:n},()=>Array(n).fill(null));

  const g={
    id:Date.now(),
    week,
    teamA:teamAObj.name, teamB:teamBObj.name,
    colorA:teamAObj.color, colorB:teamBObj.color,
    divisionA:teamAObj.division, divisionB:teamBObj.division,
    gridSize:n, scoreA:0, scoreB:0, quarter:"1",
    squares,
    rowNumbers:[], colNumbers:[],
    xTeam:null, yTeam:null, xScoreRef:null, yScoreRef:null,
    active:false, ended:false
  };
  games.push(g);
  closeCreatePopup();
  updateDropdown();
  renderGames();
}

/* ========================= SELECT / VIEW ========================= */
function updateDropdown(){
  const sel=document.getElementById('gameSelect');
  if(!sel) return;
  sel.innerHTML='';
  games.forEach(g=>{
    const o=document.createElement('option');
    o.value=g.id; o.textContent=`${g.week?g.week+': ':''}${g.teamA} vs ${g.teamB}`;
    sel.appendChild(o);
  });
}
function viewSelectedGame(){
  const id=parseInt(document.getElementById('gameSelect').value,10);
  if(id) openBoard(id);
}

/* ========================= START / END ========================= */
function toggleGame(id){
  const g=games.find(x=>x.id===id);
  if(!g.active && !g.ended){
    // create number arrays
    const numbers=[...Array(10).keys()];

    // 5×5: each axis uses all digits 0–9 once, split into 5 pills of 2 digits
    if(g.gridSize===5){
      g.rowNumbers = makeDigitPairs();   // e.g., [[3,7],[1,9],[0,5],[2,8],[4,6]]
      g.colNumbers = makeDigitPairs();   // independent partition for columns
    }else{
      g.rowNumbers=shuffle([...Array(g.gridSize).keys()]);
      g.colNumbers=shuffle([...Array(g.gridSize).keys()]);
    }

//     if(g.gridSize===5){
//   const rowPair = Array.isArray(g.rowNumbers) ? g.rowNumbers[y] : null;
//   const colPair = Array.isArray(g.colNumbers) ? g.colNumbers[x] : null;
//   if(Array.isArray(rowPair) && Array.isArray(colPair) &&
//      rowPair.includes(lastY) && colPair.includes(lastX)) {
//     s.classList.add('winner');
//   }
// } else if (Array.isArray(g.rowNumbers) && Array.isArray(g.colNumbers)) {
//   if(g.rowNumbers[y] === lastY && g.colNumbers[x] === lastX) {
//     s.classList.add('winner');
//   }
// }

    // assign which team is X/Y (unchanged)
    if(Math.random()<0.5){
      g.xTeam='A'; g.yTeam='B';
      g.xScoreRef='scoreA'; g.yScoreRef='scoreB';
    }else{
      g.xTeam='B'; g.yTeam='A';
      g.xScoreRef='scoreB'; g.yScoreRef='scoreA';
    }

    g.active=true; g.quarter="1";
  }else if(g.active){
    if(g.quarter!=="4" && g.quarter!=="OT"){
      alert("Game can only end in 4th quarter or OT.");
      return;
    }
    g.active=false; g.ended=true;
  }
  renderGames();
}


/* ========================= BOARD POPUP ========================= */
function openBoard(id){
  currentGame=id;
  const g=games.find(x=>x.id===id);
  const n=g.gridSize;

  const modal=document.getElementById('modal');
  const body =document.getElementById('modalBody');
  body.innerHTML='';

  document.getElementById('modalTitle').innerHTML =
    `${makePillHTML(g.teamA,g.colorA)} (${g.scoreA}) - (${g.scoreB}) ${makePillHTML(g.teamB,g.colorB)}`;

  const board=document.createElement('div');
  board.className='board';
  board.style.gridTemplateColumns=`50px repeat(${n},60px)`;
  board.style.gridTemplateRows=`50px repeat(${n},60px)`;

  const corner=document.createElement('div');
  corner.className='corner';
  corner.textContent=(g.active||g.ended) ? (g.quarter==="OT" ? "OT" : `Q${g.quarter}`) : "";
  board.appendChild(corner);

  const cols = (g.active||g.ended) ? g.colNumbers : Array(n).fill('');
  const rows = (g.active||g.ended) ? g.rowNumbers : Array(n).fill('');

  const xColor=(g.xTeam==='A')?g.colorA:(g.xTeam==='B'?g.colorB:g.colorA);
  const yColor=(g.yTeam==='A')?g.colorA:(g.yTeam==='B'?g.colorB:g.colorB);

  const lastX=(g[g.xScoreRef]??0)%10;
  const lastY=(g[g.yScoreRef]??0)%10;

  for(let x=0;x<n;x++){
    const c=document.createElement('div');
    c.className='axis top';

    // 5×5: show two digits "a,b"; 10×10: show single digit
    let label=(g.active||g.ended)?cols[x]:'';
    if(g.active||g.ended){
      if(g.gridSize===5) label = g.colNumbers[x].join(',');
    }

    const textColor=getTextColor(xColor);
    c.innerHTML=`<span class="axis-pill" style="background:${(g.active||g.ended)?xColor:'rgba(255,255,255,0.05)'};color:${textColor};border:2px solid #fff;">${label}</span>`;
    board.appendChild(c);
  }

  for(let y=0;y<n;y++){
    const left=document.createElement('div');
    left.className='axis left';

    // 5×5: show two digits "a,b"; 10×10: show single digit
    let label=(g.active||g.ended)?rows[y]:'';
    if(g.active||g.ended){
      if(g.gridSize===5) label = g.rowNumbers[y].join(',');
    }

    const textColor=getTextColor(yColor);
    left.innerHTML=`<span class="axis-pill" style="background:${(g.active||g.ended)?yColor:'rgba(255,255,255,0.05)'};color:${textColor};border:2px solid #fff;">${label}</span>`;
    board.appendChild(left);

    for(let x=0;x<n;x++){
      const s=document.createElement('div');
      s.className='square';
      if(g.squares[y][x]){ s.classList.add('taken'); s.textContent=g.squares[y][x]; }
      if(g.active && !g.ended) s.onclick=()=>selectSquare(y,x);

      // Winner check:
      // 10×10: exact match of single digits
      // 5×5: either digit in row pair matches lastY AND either digit in col pair matches lastX
      if(g.gridSize===5){
        const rowPair=g.rowNumbers[y];
        const colPair=g.colNumbers[x];

        if (
          Array.isArray(rowPair) &&
          Array.isArray(colPair) &&
          rowPair.includes(lastY) &&
          colPair.includes(lastX)
        ) {
          s.classList.add('winner');
        }

      }else{
        if(g.rowNumbers[y]===lastY && g.colNumbers[x]===lastX) s.classList.add('winner');
      }

      board.appendChild(s);
    }
  }

  body.appendChild(board);
  modal.style.display='flex';
}
function closeModal(){ document.getElementById('modal').style.display='none'; }

/* ========================= EDIT POPUP ========================= */
function openEdit(id){
  editIndex=games.findIndex(g=>g.id===id);
  const g=games[editIndex];
  quarterSelect.value=g.quarter;
  scoreA.value=g.scoreA;
  scoreB.value=g.scoreB;
  labelA.textContent=g.teamA;
  labelB.textContent=g.teamB;
  document.getElementById('editPopup').style.display='flex';
}
function closeEdit(){ document.getElementById('editPopup').style.display='none'; }
function saveEdit(){
  const g=games[editIndex];
  g.quarter = quarterSelect.value;
  g.scoreA  = parseInt(scoreA.value||"0",10);
  g.scoreB  = parseInt(scoreB.value||"0",10);
  closeEdit();
  renderGames();
}

/* ========================= SQUARES / JOIN ========================= */
function selectSquare(y,x){
  const g=games.find(x=>x.id===currentGame);
  if(!g.active || g.ended) return;
  if(g.squares[y][x]) return alert("Square taken");
  const name=prompt("Enter player name:");
  if(!name) return;
  g.squares[y][x]=name;
  openBoard(currentGame);
  renderGames();
}
function fillAllSquares(id){
  const g=games.find(x=>x.id===id);
  const names=["Alice","Bob","Charlie","Diana","Eve","Frank","Gina","Henry","Ivan","Julia","Ken","Lara"];
  for(let y=0;y<g.gridSize;y++){
    for(let x=0;x<g.gridSize;x++){
      if(!g.squares[y][x]) g.squares[y][x]=names[Math.floor(Math.random()*names.length)];
    }
  }
  renderGames();
}
function simulateJoin(id){
  const g=games.find(x=>x.id===id);
  const names=["Alice","Bob","Charlie","Diana","Eve","Frank","Gina","Henry","Ivan","Julia","Ken","Lara"];
  const empties=[];
  g.squares.forEach((r,i)=>r.forEach((c,j)=>!c && empties.push([i,j])));
  if(!empties.length) return alert("Board full");
  const [y,x]=empties[Math.floor(Math.random()*empties.length)];
  g.squares[y][x]=names[Math.floor(Math.random()*names.length)];
  renderGames();
}

/* ========================= RENDER ========================= */
function renderGames(){
  const wrap=document.getElementById('games');
  wrap.innerHTML='';
  games.forEach(g=>{
    const taken=g.squares.flat().filter(Boolean).length;
    const total=g.gridSize*g.gridSize;
    const full=taken===total;
    const pct=Math.round(taken/total*100);

    const card=document.createElement('div');
    card.className='card';

    const headerHTML = `${makePillHTML(g.teamA,g.colorA)} (${g.scoreA}) - (${g.scoreB}) ${makePillHTML(g.teamB,g.colorB)}`;

    card.innerHTML = `
      <div class="card-header">
        <span>${headerHTML}</span>
        <span class="${g.ended?'ended-label':g.active?'live-label':''}">
          ${g.week?g.week+' | ':''}${g.ended?'ENDED':g.active?'LIVE':'Ready'}
        </span>
      </div>
      <div class="progress"><div class="progress-bar" style="width:${pct}%;"></div></div>
      <div>${taken}/${total} Squares Filled | Quarter: ${g.quarter}</div>
      <div class="buttons">
        ${!g.ended && full && !g.active?`<button class="start" onclick="toggleGame(${g.id})">Start Game</button>`:''}
        ${g.active && (g.quarter==="4"||g.quarter==="OT")?`<button class="start active" onclick="toggleGame(${g.id})">End Game</button>`:''}
        ${g.active?`<button class="edit" onclick="openEdit(${g.id})">Edit</button>`:''}
        ${!g.active && !g.ended && !full?`<button class="join" onclick="simulateJoin(${g.id})">Sim Join</button><button class="fill" onclick="fillAllSquares(${g.id})">Fill All</button>`:''}
        ${g.ended?`<button class="start" disabled>Game Ended</button>`:''}
        <button class="board" onclick="openBoard(${g.id})">Open Board</button>
      </div>
    `;
    wrap.appendChild(card);
  });
  updateDropdown();
  saveGames();
}

/* ========================= INIT ========================= */
loadGames();
populateTeamDropdowns();
renderGames();